#!/usr/bin/env python3
"""Compiles the o2server Java files this fork changed since UPSTREAM_BASE (working tree, so
uncommitted edits are included when run locally) against an official release distribution
and writes the classes into that distribution's artifacts (store/jars/<module>.jar,
store/<module>.war or <module>.war).
Also adds x_base_core_project resources (ko-messages.json, language/*_ko.properties).

Usage: deploy/patch-classes.py --server <o2server dir> --java-home <JDK 11> --base <git rev>

Files that cannot be compiled against the release libraries (the fork is ahead of the
release in places) must be listed in deploy/unpatchable.txt; any other failure aborts.
"""
import argparse, collections, glob, os, shutil, subprocess, sys, tempfile

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


def run(cmd, cwd=None):
    r = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True)
    if r.returncode:
        sys.exit(f"FAILED: {' '.join(cmd[:3])} ...\n{r.stdout}\n{r.stderr}")
    return r.stdout


# Persistent superclasses shipped in x_base_core_project; the release enhances them with OpenJPA
# (EnhanceBaseBuilder) and plain javac output breaks entity metadata ("No registered metadata").
BASE_MAPPED = ["com.x.base.core.entity.JpaObject", "com.x.base.core.entity.SliceJpaObject",
               "com.x.base.core.entity.StorageObject"]


def enhance_mapped_superclasses(module, files, out, cp):
    for f in files:
        src = open(f, encoding="utf-8").read()
        if "\n@Entity" in src or "\n@MappedSuperclass" in src:
            cls = os.path.relpath(f, os.path.join(REPO, "o2server", module, "src/main/java"))[:-5].replace("/", ".")
            if module != "x_base_core_project" or cls not in BASE_MAPPED:
                sys.exit(f"FAILED: {os.path.relpath(f, REPO)} is a JPA entity; patching entities is not supported")
    targets = [c for c in BASE_MAPPED if os.path.exists(os.path.join(out, c.replace(".", "/") + ".class"))]
    if not targets:
        return
    xml = os.path.join(out, "..", "enhance-persistence.xml")
    with open(xml, "w", encoding="utf-8") as w:
        w.write('<persistence xmlns="http://java.sun.com/xml/ns/persistence" version="2.0">'
                '<persistence-unit name="enhance">'
                + "".join(f"<class>{c}</class>" for c in BASE_MAPPED) + "</persistence-unit></persistence>")
    paths = [os.path.join(out, c.replace(".", "/") + ".class") for c in targets]
    run([os.path.join(args.java_home, "bin", "java"), "-cp", ":".join([out] + cp),
         "org.apache.openjpa.enhance.PCEnhancer", "-p", xml] + paths)
    print(f"enhanced {', '.join(t.rsplit('.', 1)[1] for t in targets)}")


tracked = run(["git", "diff", "--name-only", args.base, "--", "o2server/*.java"], cwd=REPO).split()
untracked = run(["git", "ls-files", "--others", "--exclude-standard", "--", "o2server/*.java"], cwd=REPO).split()
changed = sorted(f for f in set(tracked + untracked) if os.path.exists(os.path.join(REPO, f)))
allowed = set()
listing = os.path.join(REPO, "deploy", "unpatchable.txt")
if os.path.exists(listing):
    allowed = {l.strip() for l in open(listing) if l.strip() and not l.startswith("#")}

by_module = collections.defaultdict(list)
for f in changed:
    by_module[f.split("/")[1]].append(os.path.join(REPO, f))

base_cp = (glob.glob(f"{SRV}/store/jars/*.jar") + glob.glob(f"{SRV}/commons/ext_java11/*.jar")
           + [p for p in args.compile_cp.split(":") if p])
work = tempfile.mkdtemp(prefix="o2patch.")
skipped = []
try:
    # Base jar first: other modules compile against the patched base classes.
    for m in sorted(by_module, key=lambda x: x != "x_base_core_project"):
        files = by_module[m]
        candidates = ((f"{SRV}/store/jars/{m}.jar", "jar"), (f"{SRV}/store/{m}.war", "war"), (f"{SRV}/{m}.war", "war"))
        if m == "x_console":
            candidates = ((f"{SRV}/console.jar", "jar"),)
        for art, kind in candidates:
            if os.path.exists(art):
                break
        else:
            sys.exit(f"FAILED: no release artifact for module {m} ({len(files)} changed files)")
        out = os.path.join(work, m, "out"); os.makedirs(out)
        cp = list(base_cp) + ([f"{SRV}/console.jar"] if m == "x_console" else [])
        if kind == "war":
            ex = os.path.join(work, m, "war"); os.makedirs(ex)
            subprocess.run([JAR, "xf", art, "WEB-INF/classes", "WEB-INF/lib"], cwd=ex, capture_output=True)
            cp = [f"{ex}/WEB-INF/classes"] + glob.glob(f"{ex}/WEB-INF/lib/*.jar") + cp
        empty = os.path.join(work, "empty"); os.makedirs(empty, exist_ok=True)
        javac = [JAVAC, "-nowarn", "-encoding", "UTF-8", "--release", "11", "-proc:none", "-implicit:none",
                 "-sourcepath", empty, "-cp", ":".join(cp), "-d", out]
        r = subprocess.run(javac + files, capture_output=True, text=True)
        if r.returncode:
            bad = sorted({f for f in files if f + ":" in r.stderr})
            unexpected = [f for f in bad if os.path.relpath(f, REPO) not in allowed]
            if not bad or unexpected:
                sys.exit(f"FAILED {m}:\n{r.stderr}")
            skipped += [os.path.relpath(f, REPO) for f in bad]
            files = [f for f in files if f not in bad]
            if not files:
                continue
            run(javac + files)
        enhance_mapped_superclasses(m, files, out, cp)
        if kind == "jar":
            run([JAR, "uf", art, "."], cwd=out)
        else:
            stage = os.path.join(work, m, "stage")
            shutil.copytree(out, os.path.join(stage, "WEB-INF", "classes"))
            run([JAR, "uf", art, "WEB-INF"], cwd=stage)
        print(f"patched {m} ({kind}): {len(files)} files")
    res = f"{REPO}/o2server/x_base_core_project/src/main/resources"
    run([JAR, "uf", f"{SRV}/store/jars/x_base_core_project.jar", "ko-messages.json", "language"], cwd=res)
    print("patched x_base_core_project resources")
    for f in skipped:
        print(f"not patched (listed in deploy/unpatchable.txt): {f}")
finally:
    shutil.rmtree(work, ignore_errors=True)
