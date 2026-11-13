#!/usr/bin/env python3
"""Patches an official release distribution with the o2server Java files this fork changed
since UPSTREAM_BASE (working tree, so uncommitted edits are included when run locally).
Also adds x_base_core_project resources (ko-messages.json, language/*_ko.properties) and
the translated API descriptions shipped in each war's describe/ folder.

Usage: deploy/patch-classes.py --server <o2server dir> --java-home <JDK 21> --base <git rev>
(the release was compiled by JDK 21 with --release 11; the bytecode check needs the same javac)

Every changed file falls in one of four groups (compared with the base revision):
  comment   only comments differ                      -> nothing to compile
  describe  only comments and API description strings  -> nothing to compile; the war's
            (@FieldDescribe, @Jaxrs*Describe, ...)        describe/*.json and sources are updated
  text      only string literal contents differ        -> compiled; each class is patched only when
                                                          its bytecode equals the release class apart
                                                          from string constants (the fork's sources
                                                          are newer than the release in places)
  code      anything else (the fork's real changes)    -> compiled and patched as is
Classes that inline a changed `static final String` (javac copies constant values into the
classes that use them) are recompiled as dependents and must pass the same bytecode check.
JPA entities are OpenJPA-enhanced like the release build before the check; entity code changes
are not supported.

Code files that cannot be compiled against the release libraries must be listed in
deploy/unpatchable.txt; any other failure aborts. Text-group classes that fail the check
are reported and keep the release text.
"""
import argparse, collections, glob, json, os, re, shutil, subprocess, sys, tempfile, zipfile

REPO = subprocess.run(["git", "rev-parse", "--show-toplevel"], capture_output=True, text=True,
                      cwd=os.path.dirname(os.path.abspath(__file__))).stdout.strip()

ap = argparse.ArgumentParser()
ap.add_argument("--server", required=True)
ap.add_argument("--java-home", required=True)
ap.add_argument("--base", required=True)
ap.add_argument("--compile-cp", default="", help="extra jars (':'-separated) used only for compiling")
args = ap.parse_args()
SRV = os.path.abspath(args.server)
JAVAC = os.path.join(args.java_home, "bin", "javac")
JAR = os.path.join(args.java_home, "bin", "jar")

DESCRIBE = re.compile(r"@(FieldDescribe|JaxrsDescribe|JaxrsMethodDescribe|JaxrsParameterDescribe|"
                      r"JaxrsFieldDescribe|FieldTypeDescribe|Describe|Schema|Operation|Parameter)\b")


def run(cmd, cwd=None):
    r = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True)
    if r.returncode:
        sys.exit(f"FAILED: {' '.join(cmd[:3])} ...\n{r.stdout}\n{r.stderr}")
    return r.stdout


def git_show(path):
    r = subprocess.run(["git", "show", f"{args.base}:{path}"], cwd=REPO, capture_output=True)
    return r.stdout.decode("utf-8", "replace") if r.returncode == 0 else None


def tokens(src):
    """Java tokens without comments: (kind, text, in_describe). Strings keep their raw text."""
    out, i, n = [], 0, len(src)
    depth, describe_depth = 0, None  # parenthesis depth; depth at which a describe annotation opened
    pending_describe = False
    while i < n:
        c = src[i]
        if c.isspace():
            i += 1
        elif src.startswith("//", i):
            j = src.find("\n", i); i = n if j < 0 else j
        elif src.startswith("/*", i):
            j = src.find("*/", i + 2); i = n if j < 0 else j + 2
        elif src.startswith('"""', i):
            j = src.find('"""', i + 3)
            while j > 0 and src[j - 1] == "\\":
                j = src.find('"""', j + 1)
            j = n if j < 0 else j + 3
            out.append(("str", src[i:j], describe_depth is not None)); i = j
        elif c in "\"'":
            j = i + 1
            while j < n and src[j] != c:
                j += 2 if src[j] == "\\" else 1
            out.append(("str", src[i:j + 1], describe_depth is not None)); i = j + 1
        elif c == "@" and DESCRIBE.match(src, i):
            m = DESCRIBE.match(src, i)
            out.append(("tok", m.group(0), False)); i = m.end(); pending_describe = True
        elif c.isalnum() or c in "_$":
            j = i
            while j < n and (src[j].isalnum() or src[j] in "_$"):
                j += 1
            out.append(("tok", src[i:j], describe_depth is not None)); i = j
        else:
            if c == "(":
                depth += 1
                if pending_describe and describe_depth is None:
                    describe_depth = depth
            elif c == ")":
                if describe_depth == depth:
                    describe_depth = None
                depth -= 1
            if c not in "(" and not c.isspace():
                pending_describe = pending_describe and c == "("
            if c != "(":
                pending_describe = False
            out.append(("tok", c, describe_depth is not None)); i += 1
    return out


def classify(old, new):
    a, b = tokens(old), tokens(new)
    if a == b:
        return "comment"
    if len(a) != len(b) or any(x[0] != y[0] or (x[0] == "tok" and x[1] != y[1]) for x, y in zip(a, b)):
        return "code"
    if all(x[1] == y[1] or x[2] for x, y in zip(a, b)):
        return "describe"
    return "text"


def describe_pairs(old, new):
    """(old, new) unescaped describe strings that changed, by position."""
    pairs = []
    for x, y in zip(tokens(old), tokens(new)):
        if x[0] == "str" and x[2] and x[1] != y[1] and x[1].startswith('"') and y[1].startswith('"'):
            pairs.append((json.loads(x[1]) if is_json_str(x[1]) else x[1][1:-1],
                          json.loads(y[1]) if is_json_str(y[1]) else y[1][1:-1]))
    return pairs


def is_json_str(lit):
    try:
        json.loads(lit)
        return True
    except ValueError:
        return False


CONST = re.compile(r"static\s+final\s+String\s+(\w+)\s*=\s*(\"(?:\\.|[^\"\\])*\")\s*;")


def changed_constants(old, new):
    """Old values of `static final String` constants whose value changed (non-ASCII only)."""
    before = {m.group(1): m.group(2) for m in CONST.finditer(old)}
    after = {m.group(1): m.group(2) for m in CONST.finditer(new)}
    vals = set()
    for k, v in before.items():
        if k in after and after[k] != v and is_json_str(v):
            s = json.loads(v)
            if any(ord(ch) > 127 for ch in s):
                vals.add(s)
    return vals


# Persistent superclasses shipped in x_base_core_project; the release enhances them with OpenJPA
# (EnhanceBaseBuilder) and plain javac output breaks entity metadata ("No registered metadata").
BASE_MAPPED = ["com.x.base.core.entity.JpaObject", "com.x.base.core.entity.SliceJpaObject",
               "com.x.base.core.entity.StorageObject"]


def is_entity(path):
    src = open(path, encoding="utf-8").read()
    return "\n@Entity" in src or "\n@MappedSuperclass" in src


def class_of(module, path):
    return os.path.relpath(path, os.path.join(REPO, "o2server", module, "src/main/java"))[:-5].replace("/", ".")


def enhance(module, files, out, cp):
    """OpenJPA-enhances the compiled entities like the release build (EnhanceBuilder): the patched
    base superclasses, and entities whose change is text only (code changes to entities are not
    supported). Text entities then go through the same bytecode check as other text classes."""
    targets = [c for c in BASE_MAPPED if os.path.exists(os.path.join(out, c.replace(".", "/") + ".class"))]
    for f in files:
        if not is_entity(f):
            continue
        cls = class_of(module, f)
        if cls in BASE_MAPPED:
            continue
        if group[os.path.relpath(f, REPO)] == "code":
            sys.exit(f"FAILED: {os.path.relpath(f, REPO)} is a JPA entity; patching entity code is not supported")
        targets.append(cls)
    if not targets:
        return
    mapped = list(BASE_MAPPED)
    for p in glob.glob(os.path.join(REPO, "o2server", module, "src/main/java/**/*.java"), recursive=True):
        if "\n@MappedSuperclass" in open(p, encoding="utf-8").read():
            mapped.append(class_of(module, p))
    xml = os.path.join(out, "..", "enhance-persistence.xml")
    with open(xml, "w", encoding="utf-8") as w:
        w.write('<persistence xmlns="http://java.sun.com/xml/ns/persistence" version="2.0">'
                '<persistence-unit name="enhance">'
                + "".join(f"<class>{c}</class>" for c in sorted(set(mapped + targets)))
                + "</persistence-unit></persistence>")
    paths = [os.path.join(out, c.replace(".", "/") + ".class") for c in targets]
    run([os.path.join(args.java_home, "bin", "java"), "-cp", ":".join([out] + cp),
         "org.apache.openjpa.enhance.PCEnhancer", "-p", xml] + paths)
    print(f"enhanced {module}: {len(targets)} classes")


def artifact(m):
    candidates = ((f"{SRV}/store/jars/{m}.jar", "jar"), (f"{SRV}/store/{m}.war", "war"), (f"{SRV}/{m}.war", "war"))
    if m == "x_console":
        candidates = ((f"{SRV}/console.jar", "jar"),)
    for art, kind in candidates:
        if os.path.exists(art):
            return art, kind
    return None, None


SHAPE_TOOL = {}


def shape_tool():
    """Compiles deploy/ClassShape.java once; returns the java classpath that runs it."""
    if not SHAPE_TOOL:
        asm = glob.glob(f"{SRV}/commons/ext_java11/asm-[0-9]*.jar")
        if not asm:
            sys.exit("FAILED: no asm jar in the release commons")
        d = tempfile.mkdtemp(prefix="o2shape.")
        run([JAVAC, "-nowarn", "--release", "11", "-cp", asm[0], "-d", d, os.path.join(REPO, "deploy", "ClassShape.java")])
        SHAPE_TOOL["cp"] = f"{asm[0]}:{d}"
    return SHAPE_TOOL["cp"]


def shapes(classes_dir, names):
    """{class name: shape text (None when missing)} for classes in classes_dir."""
    out = {}
    names = sorted(names)
    for k in range(0, len(names), 500):
        text = run([os.path.join(args.java_home, "bin", "java"), "-cp", shape_tool(), "ClassShape", classes_dir]
                   + names[k:k + 500])
        for part in text.split("=== ")[1:]:
            head, _, body = part.partition("\n")
            if head.endswith(" MISSING"):
                out[head[:-8]] = None
            else:
                out[head] = body
    return out


def class_names(out_dir):
    names = []
    for root, _, fs in os.walk(out_dir):
        for f in fs:
            if f.endswith(".class"):
                names.append(os.path.relpath(os.path.join(root, f), out_dir)[:-6].replace("/", "."))
    return names


def source_of(classname):
    """Repository source path of a (possibly nested) class, or None."""
    top = classname.split("$")[0].replace(".", "/") + ".java"
    hits = glob.glob(os.path.join(REPO, "o2server", "*", "src/main/java", top))
    return os.path.relpath(hits[0], REPO) if hits else None


# ---------------------------------------------------------------------------------------------
tracked = run(["git", "diff", "--name-only", args.base, "--", "o2server/*.java"], cwd=REPO).split()
untracked = run(["git", "ls-files", "--others", "--exclude-standard", "--", "o2server/*.java"], cwd=REPO).split()
changed = sorted(f for f in set(tracked + untracked) if os.path.exists(os.path.join(REPO, f)))
allowed = set()
listing = os.path.join(REPO, "deploy", "unpatchable.txt")
if os.path.exists(listing):
    allowed = {l.strip() for l in open(listing) if l.strip() and not l.startswith("#")}

group = {}
describe_map = {}
old_constants = set()
for f in changed:
    if "/src/main/java/" not in f:
        continue
    new = open(os.path.join(REPO, f), encoding="utf-8").read()
    old = git_show(f)
    if old is None:
        group[f] = "code"
        continue
    g = classify(old, new)
    group[f] = g
    if g in ("describe", "text", "code"):
        for o, n in describe_pairs(old, new):
            describe_map.setdefault(o, n)
    if g in ("text", "code"):
        old_constants |= changed_constants(old, new)

counts = collections.Counter(group.values())
print("changed files: " + ", ".join(f"{k} {v}" for k, v in sorted(counts.items())))

# Release classes that inline an old constant value must be rebuilt too.
dependents = set()
if old_constants:
    needles = [v.encode("utf-8") for v in old_constants]
    arts = glob.glob(f"{SRV}/store/jars/*.jar") + glob.glob(f"{SRV}/store/*.war") + glob.glob(f"{SRV}/*.war")
    for art in arts:
        with zipfile.ZipFile(art) as z:
            for name in z.namelist():
                if not name.endswith(".class"):
                    continue
                data = z.read(name)
                if any(nd in data for nd in needles):
                    cls = name.removeprefix("WEB-INF/classes/")[:-6].replace("/", ".")
                    src = source_of(cls)
                    if src and group.get(src) is None:
                        dependents.add(src)
    for d in sorted(dependents):
        group[d] = "dependent"
    print(f"constant dependents: {len(dependents)}")

by_module = collections.defaultdict(list)
for f, g in group.items():
    if g in ("text", "code", "dependent"):
        by_module[f.split("/")[1]].append(f)

base_cp = (glob.glob(f"{SRV}/store/jars/*.jar") + glob.glob(f"{SRV}/commons/ext_java11/*.jar")
           + [p for p in args.compile_cp.split(":") if p])
work = tempfile.mkdtemp(prefix="o2patch.")
skipped, kept_release = [], []
try:
    # Base jar first: other modules compile against the patched base classes.
    for m in sorted(by_module, key=lambda x: x != "x_base_core_project"):
        rel_files = sorted(by_module[m])
        art, kind = artifact(m)
        if not art:
            sys.exit(f"FAILED: no release artifact for module {m} ({len(rel_files)} changed files)")
        out = os.path.join(work, m, "out"); os.makedirs(out)
        release = os.path.join(work, m, "release"); os.makedirs(release)
        cp = list(base_cp) + ([f"{SRV}/console.jar"] if m == "x_console" else [])
        if kind == "war":
            subprocess.run([JAR, "xf", art, "WEB-INF/classes", "WEB-INF/lib"], cwd=release, capture_output=True)
            cp = [f"{release}/WEB-INF/classes"] + glob.glob(f"{release}/WEB-INF/lib/*.jar") + cp
            release_classes = f"{release}/WEB-INF/classes"
        else:
            subprocess.run([JAR, "xf", art], cwd=release, capture_output=True)
            release_classes = release
        files = [os.path.join(REPO, f) for f in rel_files]
        empty = os.path.join(work, "empty"); os.makedirs(empty, exist_ok=True)
        javac = [JAVAC, "-nowarn", "-encoding", "UTF-8", "--release", "11", "-proc:none", "-implicit:none",
                 "-sourcepath", empty, "-cp", ":".join(cp), "-d", out]
        # Classes compiled together see each other's new constants, so whenever a text class falls
        # back to the release the rest is compiled again without it (against the release class).
        while files:
            shutil.rmtree(out); os.makedirs(out)
            r = subprocess.run(javac + files, capture_output=True, text=True)
            if r.returncode:
                bad = sorted({f for f in files if f + ":" in r.stderr})
                if not bad:
                    sys.exit(f"FAILED {m}:\n{r.stderr}")
                for f in bad:
                    rel = os.path.relpath(f, REPO)
                    if group[rel] == "code" and rel not in allowed:
                        sys.exit(f"FAILED {m}: {rel}\n{r.stderr}")
                    if group[rel] == "code":
                        skipped.append(rel)
                    else:
                        kept_release.append((rel, ("constant dependent" if group[rel] == "dependent" else "text")
                                             + " does not compile against the release"))
                files = [f for f in files if f not in bad]
                continue
            enhance(m, files, out, cp)
            # Text and dependent classes: keep only those identical to the release apart from strings.
            checked = collections.defaultdict(list)
            for name in class_names(out):
                src = source_of(name)
                if src and group.get(src) in ("text", "dependent"):
                    checked[src].append(name)
            names = [n for ns in checked.values() for n in ns]
            mine, theirs = shapes(out, names), shapes(release_classes, names)
            differing = [src for src, ns in checked.items()
                         if not all(mine.get(n) is not None and mine.get(n) == theirs.get(n) for n in ns)]
            if not differing:
                break
            for src in differing:
                kept_release.append((src, "constant dependent differs from the release" if group[src] == "dependent"
                                     else "bytecode differs from the release"))
            files = [f for f in files if os.path.relpath(f, REPO) not in differing]
        if not files:
            continue
        if not class_names(out):
            continue
        if kind == "jar":
            run([JAR, "uf", art, "."], cwd=out)
        else:
            stage = os.path.join(work, m, "stage")
            shutil.copytree(out, os.path.join(stage, "WEB-INF", "classes"))
            run([JAR, "uf", art, "WEB-INF"], cwd=stage)
        print(f"patched {m} ({kind}): {len(class_names(out))} classes")

    # API descriptions: translated describe JSON values and sources in every war.
    sources_by_module = collections.defaultdict(list)
    for f, g in group.items():
        if g in ("comment", "describe", "text", "code"):
            sources_by_module[f.split("/")[1]].append(f)
    for m, rel_files in sorted(sources_by_module.items()):
        art, kind = artifact(m)
        if kind != "war":
            continue
        with zipfile.ZipFile(art) as z:
            names = set(z.namelist())
            stage = os.path.join(work, m, "describe-stage")
            touched = 0
            for name in names:
                if name.startswith("describe/") and name.endswith(".json") and describe_map:
                    data = json.loads(z.read(name).decode("utf-8"))
                    new_data = json.loads(json.dumps(data), object_hook=None)

                    def tr(v):
                        if isinstance(v, str):
                            return describe_map.get(v, v)
                        if isinstance(v, list):
                            return [tr(x) for x in v]
                        if isinstance(v, dict):
                            return {k: tr(x) for k, x in v.items()}
                        return v
                    new_data = tr(data)
                    if new_data != data:
                        p = os.path.join(stage, name); os.makedirs(os.path.dirname(p), exist_ok=True)
                        with open(p, "w", encoding="utf-8") as w:
                            json.dump(new_data, w, ensure_ascii=False, indent=2)
                        touched += 1
            for f in rel_files:
                inner = "describe/sources/" + f.split("/src/main/java/", 1)[1]
                if inner in names:
                    p = os.path.join(stage, inner); os.makedirs(os.path.dirname(p), exist_ok=True)
                    shutil.copyfile(os.path.join(REPO, f), p)
                    touched += 1
        if touched:
            run([JAR, "uf", art, "describe"], cwd=stage)
            print(f"patched {m} describe: {touched} files")

    res = f"{REPO}/o2server/x_base_core_project/src/main/resources"
    run([JAR, "uf", f"{SRV}/store/jars/x_base_core_project.jar", "ko-messages.json", "language"], cwd=res)
    print("patched x_base_core_project resources")
    for f in skipped:
        print(f"not patched (listed in deploy/unpatchable.txt): {f}")
    reasons = collections.Counter(r for _, r in kept_release)
    for r, c in sorted(reasons.items()):
        print(f"release text kept ({r}): {c} files")
    if os.environ.get("PATCH_REPORT"):
        with open(os.environ["PATCH_REPORT"], "w", encoding="utf-8") as w:
            json.dump({"groups": group, "kept_release": kept_release, "skipped": skipped}, w, ensure_ascii=False, indent=1)
finally:
    shutil.rmtree(work, ignore_errors=True)
