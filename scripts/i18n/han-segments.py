#!/usr/bin/env python3
"""Extracts Chinese text segments from source files and writes translations back.

  han-segments.py extract OUT.json [path ...]   collect segments (default: whole repo)
  han-segments.py apply MAP.json [path ...]     replace segments using {"chinese": "korean"}
  han-segments.py check [path ...]              list remaining segments; exit 1 if any

A segment is the Chinese-bearing part of
  - one comment line, comment markers and surrounding blanks excluded
    (.java .js .vue .ts .json .css and <script>/<style> blocks in markup),
  - one string literal's contents, quotes excluded (JSON object keys are never segments),
  - one text node or attribute value in markup (.html .htm .xml .vm .jsp .svg .ftl .tpl),
  - the value of a .properties line,
  - one line of any other text file (.md .txt .yml .sql .sh .py ...), blanks trimmed.
Only those spans are rewritten, so code and markup outside them never change
(check with scripts/i18n/verify-text-only.py).

Not touched:
  - Chinese language packs and locale data (zh-cn / zh_CN / zh-CHS files, lp/zh-cn.js),
  - third-party code (EXCLUDE_DIRS),
  - string and markup segments containing a text listed in
    scripts/i18n/{server,web}-keep-han/*.json for that file (stored or compared values;
    translating them needs code and data migration); comments are always segments,
  - the i18n tables themselves (ko-messages.json, keep lists).

extract writes {"segments": {"<chinese>": ["path:line", ...]}} ordered by frequency.
"""
import glob, json, os, re, subprocess, sys

ROOT = subprocess.run(["git", "rev-parse", "--show-toplevel"], capture_output=True, text=True).stdout.strip()
os.chdir(ROOT)
HAN = re.compile(r"[\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff]")
JS = (".js", ".cjs", ".mjs", ".ts", ".jsx")
MARKUP = (".html", ".htm", ".vue", ".xml", ".vm", ".jsp", ".svg", ".xsd", ".ftl", ".tpl")
LINES = (".gitignore", ".md", ".txt", ".yml", ".yaml", ".sql", ".sh", ".py", ".cfg", ".conf")
HANDLED = (".java", ".json", ".css", ".wcss", ".less", ".properties", ".gitignore") + JS + MARKUP + LINES
EXCLUDE_DIRS = (
    "o2web/source/o2_lib/",          # third-party libraries (echarts, pdf.js, ckeditor, ...)
    "o2web/source/o2_core/o2/lp/",   # language packs per locale
    "/fullcalendar/",                # bundled third-party calendar with its locale data
    "x_component_Template/framework/",  # bundled SheetJS builds (codepage tables, font names)
    "/lib/editormd/",                # bundled editor.md with CodeMirror (third-party)
    "node_modules/",
)
EXCLUDE_FILES = re.compile(r"(zh[-_](cn|CN|CHS|CHT|tw|TW|hk|HK)|/zh/|_zh\.|\.zh\.|\.min\.(js|css)$)")
EXCLUDE_EXACT = {
    "o2server/x_base_core_project/src/main/resources/ko-messages.json",
}
EXCLUDE_PREFIX = ("scripts/i18n/",)


def candidates(paths):
    files = subprocess.run(["git", "-c", "core.quotepath=false", "ls-files", "-z", "--"] + (paths or ["."]),
                           capture_output=True, text=True).stdout.split("\0")
    out = []
    for f in files:
        if f in EXCLUDE_EXACT or f.startswith(EXCLUDE_PREFIX) or any(d in f for d in EXCLUDE_DIRS):
            continue
        if EXCLUDE_FILES.search(f) or not f.endswith(HANDLED) or not os.path.isfile(f):
            continue
        out.append(f)
    return out


def keep_texts():
    keep = {}
    for p in glob.glob("scripts/i18n/server-keep-han/*.json") + glob.glob("scripts/i18n/web-keep-han/*.json"):
        for f, items in json.load(open(p, encoding="utf-8")).items():
            keep.setdefault(f, set()).update(it["text"] for it in items)
    return keep


def trim(start, end, src):
    while start < end and src[start] in " \t\r\n*/#!-<>=":
        start += 1
    while end > start and src[end - 1] in " \t\r\n*/-<>=":
        end -= 1
    return start, end


def code_spans(src, lo, hi, lang):
    """(start, end, kind) for comment lines and string contents in src[lo:hi].
    lang: "java", "js", "json" or "css"."""
    spans, i = [], lo
    js = lang in ("js", "json")
    prev = ""  # last significant char, to tell JS regex literals from division

    def lines(a, b, kind):
        k = a
        while k < b:
            e = src.find("\n", k, b); e = b if e < 0 else e
            s2, e2 = trim(k, e, src)
            spans.append((s2, e2, kind)); k = e + 1

    while i < hi:
        c = src[i]
        if lang != "css" and src.startswith("//", i):
            j = src.find("\n", i, hi); j = hi if j < 0 else j
            lines(i + 2, j, "comment"); i = j
        elif src.startswith("/*", i):
            j = src.find("*/", i + 2, hi); j = hi if j < 0 else j
            lines(i + 2, j, "comment"); i = j + 2
        elif lang == "java" and src.startswith('"""', i):
            j = src.find('"""', i + 3, hi); j = hi if j < 0 else j
            lines(i + 3, j, "string"); i = j + 3; prev = '"'
        elif c in "\"'" or (js and c == "`"):
            j = i + 1
            while j < hi and src[j] != c and not (c != "`" and src[j] == "\n"):
                j += 2 if src[j] == "\\" else 1
            kind = "string"
            if lang == "json" and re.match(r"\s*:", src[j + 1:j + 40]):
                kind = "key"
            spans.append((i + 1, j, kind)); i = j + 1; prev = c
        elif lang == "js" and c == "/" and prev in "(,=:[!&|?{};+-*%<>~^\n":
            j, klass = i + 1, False
            while j < hi and src[j] != "\n" and (src[j] != "/" or klass):
                if src[j] == "\\":
                    j += 1
                elif src[j] == "[":
                    klass = True
                elif src[j] == "]":
                    klass = False
                j += 1
            i = j + 1; prev = "/"
        else:
            if c == "\n":
                prev = "\n"
            elif not c.isspace():
                prev = c
            i += 1
    return spans


def markup_spans(src):
    spans = []
    i, n = 0, len(src)
    tag = re.compile(r"(?is)<!--(.*?)-->|<(script|style)\b[^>]*>(.*?)</\2\s*>|<[^>]*>")
    for m in tag.finditer(src):
        text_lines(src, i, m.start(), spans, "text")
        if m.group(1) is not None:
            text_lines(src, m.start(1), m.end(1), spans, "comment")
        elif m.group(2):
            lang = "js" if m.group(2).lower() == "script" else "css"
            spans += code_spans(src, m.start(3), m.end(3), lang)
            attr_spans(src, m.start(), src.find(">", m.start()) + 1, spans)
        else:
            attr_spans(src, m.start(), m.end(), spans)
        i = m.end()
    text_lines(src, i, n, spans, "text")
    return spans


def attr_spans(src, a, b, spans):
    for m in re.finditer(r"=\s*(\"([^\"]*)\"|'([^']*)')", src[a:b]):
        g = 2 if m.group(2) is not None else 3
        spans.append((a + m.start(g), a + m.end(g), "string"))


def text_lines(src, a, b, spans, kind):
    k = a
    while k < b:
        e = src.find("\n", k, b); e = b if e < 0 else e
        s2, e2 = k, e
        while s2 < e2 and src[s2] in " \t\r":
            s2 += 1
        while e2 > s2 and src[e2 - 1] in " \t\r":
            e2 -= 1
        spans.append((s2, e2, kind)); k = e + 1


def properties_spans(src):
    spans, pos = [], 0
    for line in src.split("\n"):
        s = line.lstrip()
        if s.startswith(("#", "!")):
            text_lines(src, pos + len(line) - len(s) + 1, pos + len(line), spans, "comment")
        else:
            m = re.match(r"\s*(?:\\.|[^=:\s\\])+\s*[=:\s]\s*", line)
            if m:
                text_lines(src, pos + m.end(), pos + len(line), spans, "string")
        pos += len(line) + 1
    return spans


def spans_of(f, src):
    n = len(src)
    if f.endswith(".java"):
        out = code_spans(src, 0, n, "java")
    elif f.endswith(JS):
        out = code_spans(src, 0, n, "js")
    elif f.endswith(".json"):
        out = code_spans(src, 0, n, "json")
    elif f.endswith((".css", ".wcss", ".less")):
        out = code_spans(src, 0, n, "css")
    elif f.endswith(MARKUP):
        out = markup_spans(src)
    elif f.endswith(".properties"):
        out = properties_spans(src)
    else:
        out = []
        text_lines(src, 0, n, out, "text")
    return sorted((s, e, k) for s, e, k in out if e > s and k != "key" and HAN.search(src, s, e))


def main():
    if len(sys.argv) < 2 or sys.argv[1] not in ("extract", "apply", "check") or (sys.argv[1] != "check" and len(sys.argv) < 3):
        sys.exit(__doc__)
    mode = sys.argv[1]
    table, paths = (None, sys.argv[2:]) if mode == "check" else (sys.argv[2], sys.argv[3:])
    keep = keep_texts()
    mapping = json.load(open(table, encoding="utf-8")) if mode == "apply" else None
    found, changed, missing = {}, 0, set()
    for f in candidates(paths):
        src = open(f, encoding="utf-8", errors="surrogateescape").read()
        if not HAN.search(src):
            continue
        kept = [k for k in keep.get(f, ()) if HAN.search(k)]
        out, last, dirty = [], 0, False
        for s, e, kind in spans_of(f, src):
            if s < last:
                continue
            seg = src[s:e]
            if kind != "comment" and any(seg in k or (len(k) > 1 and k in seg) for k in kept):
                continue
            if mode in ("extract", "check"):
                line = src.count("\n", 0, s) + 1
                found.setdefault(seg, []).append(f"{f}:{line}")
                continue
            ko = mapping.get(seg)
            if ko is None:
                missing.add(seg)
                continue
            if ko != seg:
                out.append(src[last:s]); out.append(ko); last = e; dirty = True
        if dirty:
            out.append(src[last:])
            open(f, "w", encoding="utf-8", errors="surrogateescape").write("".join(out))
            changed += 1
    if mode == "check":
        for seg, where in found.items():
            print(f"{where[0]}: {seg[:100]}")
        print(f"{len(found)} Chinese segments left")
        sys.exit(1 if found else 0)
    if mode == "extract":
        ordered = dict(sorted(found.items(), key=lambda kv: -len(kv[1])))
        json.dump({"segments": ordered}, open(table, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
        print(f"{len(ordered)} unique segments, {sum(len(v) for v in ordered.values())} occurrences")
    else:
        print(f"{changed} files changed, {len(missing)} segments without translation")
        if missing:
            json.dump(sorted(missing), open(table + ".missing.json", "w", encoding="utf-8"), ensure_ascii=False, indent=1)


main()
