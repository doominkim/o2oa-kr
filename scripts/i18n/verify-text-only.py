#!/usr/bin/env python3
"""Checks that translation edits changed only comments and string contents.

Usage: python3 scripts/i18n/verify-text-only.py [--base REV] [path ...]

Compares each changed file (working tree against REV, default HEAD) after
dropping comments and blanking string/char/template contents:
  .java        own lexer (comments, "..." '...' and \"\"\" text blocks)
  .js          acorn tokenizer (scripts/i18n/js-tokens.cjs); files acorn
               cannot read fall back to the Java lexer plus `...` templates
  .json        same keys, same non-string values, same array lengths
Other file types (html, css, md, properties) are not checked.

Also fails when a text listed in scripts/i18n/{server,web}-keep-han/*.json
(stored or compared values) occurs a different number of times in that file.

Exit code 1 when any file differs in code.
"""
import glob, json, os, re, subprocess, sys

ROOT = subprocess.run(["git", "rev-parse", "--show-toplevel"], capture_output=True, text=True).stdout.strip()
os.chdir(ROOT)

args = sys.argv[1:]
base = "HEAD"
if "--base" in args:
    i = args.index("--base"); base = args[i + 1]; del args[i:i + 2]

if args:
    files = subprocess.run(["git", "diff", "--name-only", base, "--"] + args, capture_output=True, text=True).stdout.split()
else:
    files = subprocess.run(["git", "diff", "--name-only", base], capture_output=True, text=True).stdout.split()
files = [f for f in files if os.path.exists(f)]


def lex(src, js=False):
    """Tokens without comments; string/char/template bodies replaced by a marker."""
    out, i, n = [], 0, len(src)
    while i < n:
        c = src[i]
        if c in " \t\r\n\f":
            i += 1
        elif src.startswith("//", i):
            j = src.find("\n", i); i = n if j < 0 else j
        elif src.startswith("/*", i):
            j = src.find("*/", i + 2); i = n if j < 0 else j + 2
        elif not js and src.startswith('"""', i):
            j = src.find('"""', i + 3)
            while j > 0 and src[j - 1] == "\\":
                j = src.find('"""', j + 1)
            out.append('"S"'); i = n if j < 0 else j + 3
        elif c in "\"'" or (js and c == "`"):
            j = i + 1
            while j < n and src[j] != c:
                j += 2 if src[j] == "\\" else 1
            out.append(c + "S" + c); i = j + 1
        elif c.isalnum() or c in "_$":
            j = i
            while j < n and (src[j].isalnum() or src[j] in "_$."):
                j += 1
            out.append(src[i:j]); i = j
        else:
            out.append(c); i += 1
    return out


def without_comments(src, css=False):
    """Source text with comments removed; strings kept verbatim."""
    out, i, n = [], 0, len(src)
    while i < n:
        c = src[i]
        if not css and src.startswith("//", i):
            j = src.find("\n", i); i = n if j < 0 else j
        elif src.startswith("/*", i):
            j = src.find("*/", i + 2); i = n if j < 0 else j + 2
        elif c in "\"'`":
            j = i + 1
            while j < n and src[j] != c and src[j] != "\n":
                j += 2 if src[j] == "\\" else 1
            out.append(src[i:j + 1]); i = j + 1
        else:
            out.append(c); i += 1
    return "".join(out)


def js_tokens(paths):
    r = subprocess.run(["node", "scripts/i18n/js-tokens.cjs"], input=json.dumps(paths), capture_output=True, text=True)
    if r.returncode:
        sys.exit(r.stderr)
    return json.loads(r.stdout)


def json_shape(v):
    if isinstance(v, dict):
        return {k: json_shape(x) for k, x in v.items()}
    if isinstance(v, list):
        return [json_shape(x) for x in v]
    return "S" if isinstance(v, str) else v


def keep_texts():
    keep = {}
    for p in glob.glob("scripts/i18n/server-keep-han/*.json") + glob.glob("scripts/i18n/web-keep-han/*.json"):
        for f, items in json.load(open(p, encoding="utf-8")).items():
            keep.setdefault(f, set()).update(it["text"] for it in items)
    return keep


def show(rev, f):
    r = subprocess.run(["git", "show", f"{rev}:{f}"], capture_output=True)
    return r.stdout.decode("utf-8", "replace") if r.returncode == 0 else None


keep = keep_texts()
bad = []
js_pairs = []
for f in files:
    old = show(base, f)
    if old is None:
        continue
    new = open(f, encoding="utf-8", errors="replace").read()
    code_file = f.endswith((".java", ".js", ".json", ".css"))
    old_k = without_comments(old, f.endswith(".css")) if code_file else old
    new_k = without_comments(new, f.endswith(".css")) if code_file else new
    for t in keep.get(f, ()):
        # One-character values are kept only as whole literals ("无"); the same character
        # inside a longer translated string is not a kept value.
        count = (lambda s: s.count(f'"{t}"') + s.count(f"'{t}'")) if len(t) == 1 else (lambda s: s.count(t))
        if count(old_k) != count(new_k):
            bad.append((f, f"keep-listed text {t!r}: {count(old_k)} -> {count(new_k)}"))
    if f.endswith(".java"):
        if lex(old) != lex(new):
            bad.append((f, "code tokens changed"))
    elif f.endswith(".js"):
        js_pairs.append((f, old, new))
    elif f.endswith(".json"):
        try:
            if json_shape(json.loads(old)) != json_shape(json.loads(new)):
                bad.append((f, "JSON keys or non-string values changed"))
        except ValueError:
            if lex(old, js=True) != lex(new, js=True):
                bad.append((f, "code tokens changed (not strict JSON)"))

if js_pairs:
    tok = js_tokens([[f, old, new] for f, old, new in js_pairs])
    for f, old, new in js_pairs:
        res = tok[f]
        if res == "same":
            continue
        if res == "unparsable":
            if lex(old, js=True) != lex(new, js=True):
                bad.append((f, "code tokens changed (fallback lexer)"))
        else:
            bad.append((f, res))

for f, why in bad:
    print(f"{f}: {why}")
print(f"{len(files)} files checked, {len(bad)} problems")
sys.exit(1 if bad else 0)
