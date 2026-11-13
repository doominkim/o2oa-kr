#!/usr/bin/env python3
"""Finds Chinese text in o2server Java sources that can reach the UI untranslated.

Usage: python3 scripts/i18n/find-zh-java.py [--max 20] [--dump leftovers.json] [module ...]

Chinese string literals (comments ignored) are grouped by context:
  annotation  @FieldDescribe / @Jaxrs*Describe API docs   -> ignored (developer-facing)
  log         LOGGER.* / logger.* calls                    -> ignored
  exception   exception classes and `new ...Exception(`    -> must have a Korean entry in
              o2server/x_base_core_project/src/main/resources/ko-messages.json
              (translated at runtime by KoreanMessages)
  other       everything else                              -> must be translated in place or
              listed with a reason in scripts/i18n/server-keep-han/*.json
Exit code is 1 when anything is left.
"""
import glob, json, os, re, subprocess, sys

ROOT = subprocess.run(["git", "rev-parse", "--show-toplevel"], capture_output=True, text=True).stdout.strip()
HAN = re.compile(r"[\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff]")
ANNOT = re.compile(r"@(FieldDescribe|JaxrsDescribe|JaxrsMethodDescribe|JaxrsParameterDescribe|JaxrsFieldDescribe|Schema|Operation|Parameter)\s*\([^)]*$")
LOG = re.compile(r"(LOGGER|logger|log|LOG)\s*\.\s*(debug|info|warn|error|trace|print)\s*\([^;]*$")

args = sys.argv[1:]
limit = 20
dump = None
if "--max" in args:
    i = args.index("--max"); limit = int(args[i + 1]); del args[i:i + 2]
if "--dump" in args:
    i = args.index("--dump"); dump = args[i + 1]; del args[i:i + 2]
modules = set(args)


def literals(src):
    i, n, line = 0, len(src), 1
    while i < n:
        c = src[i]
        if c == "\n":
            line += 1; i += 1; continue
        if src.startswith("//", i):
            j = src.find("\n", i); i = n if j < 0 else j; continue
        if src.startswith("/*", i):
            j = src.find("*/", i + 2); end = n if j < 0 else j + 2
            line += src.count("\n", i, end); i = end; continue
        if src.startswith('"""', i):
            j = src.find('"""', i + 3); end = n if j < 0 else j
            yield line, src[i + 3:end], i; line += src.count("\n", i, end); i = end + 3; continue
        if c == '"':
            j = i + 1; buf = []
            while j < n and src[j] != '"' and src[j] != "\n":
                if src[j] == "\\":
                    buf.append(src[j:j + 2]); j += 2; continue
                buf.append(src[j]); j += 1
            yield line, "".join(buf), i; i = j + 1; continue
        if c == "'":
            j = src.find("'", i + (3 if src[i + 1:i + 2] == "\\" else 2)); i = j + 1 if j > 0 else i + 1; continue
        i += 1


def unescape(s):
    try:
        return s.encode("utf-8").decode("unicode_escape").encode("latin-1").decode("utf-8")
    except Exception:
        return s


table = {e["zh"] for e in json.load(open(os.path.join(ROOT, "o2server/x_base_core_project/src/main/resources/ko-messages.json"), encoding="utf-8"))}
keep = {}
for p in glob.glob(os.path.join(ROOT, "scripts/i18n/server-keep-han/*.json")):
    for f, items in json.load(open(p, encoding="utf-8")).items():
        keep.setdefault(f, set()).update(x["text"] for x in items)

files = subprocess.run(["git", "ls-files", "o2server/**/*.java"], cwd=ROOT, capture_output=True, text=True).stdout.split()
left = 0
dumped = {}
for f in files:
    if "/src/test/" in f or (modules and f.split("/")[1] not in modules):
        continue
    src = open(os.path.join(ROOT, f), encoding="utf-8").read()
    if not HAN.search(src):
        continue
    hits = []
    for line, raw, pos in literals(src):
        if not HAN.search(raw):
            continue
        before = src[max(0, pos - 300):pos]
        stmt = before[before.rfind(";") + 1:]
        text = unescape(raw)
        if ANNOT.search(before[-200:]) or LOG.search(stmt):
            continue
        is_exception = re.search(r"Exception\w*\.java$", f) or re.search(r"new\s+\w*Exception\w*\s*\([^;]*$", stmt)
        if is_exception:
            if text not in table:
                hits.append((line, "exception (no ko-messages entry)", text))
        elif text not in keep.get(f, ()) and raw not in keep.get(f, ()):
            hits.append((line, "other", text))
    if hits:
        left += len(hits)
        dumped[f] = [{"line": line, "kind": kind, "text": text} for line, kind, text in hits]
        print(f"\n{f} ({len(hits)})")
        for line, kind, text in hits[:limit]:
            print(f"  L{line} [{kind}] {json.dumps(text, ensure_ascii=False)[:100]}")
        if len(hits) > limit:
            print(f"  ... {len(hits) - limit} more")
if dump:
    json.dump(dumped, open(dump, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
print(f"\n{left} Chinese strings left")
sys.exit(1 if left else 0)
