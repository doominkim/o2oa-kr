#!/usr/bin/env node
// Finds Chinese UI text left in o2web/source outside language packs (lp/*.js).
//
// Usage: node scripts/i18n/find-zh.mjs [--max 30] [file-or-dir ...]
//
// Scans string literals, template strings and regexes in .js, text and
// attributes in .html, values and keys in .json, and template/script parts of
// .vue files. Comments are ignored. Vendored locale data and third-party
// bundles listed in VENDOR are skipped. Intentional leftovers (values compared
// or stored by code, Chinese-only data) are listed with a reason in
// scripts/i18n/web-keep-han/*.json:
//   { "<repo-relative file>": [ { "text": "<string as reported>", "reason": "..." } ] }
// Exit code is 1 when anything is left.

import { execFileSync } from "node:child_process";
import { existsSync, readdirSync, readFileSync } from "node:fs";
import path from "node:path";
import * as acornLoose from "acorn-loose";
import * as walk from "acorn-walk";
import { Parser as HtmlParser } from "htmlparser2";
import { parse as parseSfc } from "@vue/compiler-sfc";

const HAN = /[\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff]/;
// Locale tables and third-party code that never render Chinese for a Korean UI.
const VENDOR = [
  /\/lp\/[^/]+\.js$/,
  /\.min\.js$/,
  /\/(node_modules|dist|test|tests)\//,
  /x_component_Template\/framework\/xlsx/,
  /x_component_Attendance\/\$Common\/fullcalendar\/lang/,
  /\/echarts/,
  /o2_lib\/htmleditor\/.*\/(lang|translations)\//,
  /o2_lib\/(mootools|eruda|dingding|ofdjs|echarts5?|codemirror\/mode)\//,
];

const root = execFileSync("git", ["rev-parse", "--show-toplevel"], { encoding: "utf8" }).trim();
const args = process.argv.slice(2);
let max = 30;
const filters = [];
for (let i = 0; i < args.length; i++) {
  if (args[i] === "--max") max = Number(args[++i]);
  else filters.push(path.relative(root, path.resolve(args[i])).replace(/\/$/, ""));
}

function jsStrings(code, base = 0) {
  const out = [];
  let ast;
  try {
    ast = acornLoose.parse(code, { ecmaVersion: "latest", sourceType: "module", locations: true });
  } catch {
    return [{ line: 0, text: "<parse error>" }];
  }
  walk.full(ast, (node) => {
    if (node.type === "Literal" && typeof node.value === "string" && HAN.test(node.value)) {
      out.push({ line: node.loc.start.line + base, text: node.value });
    } else if (node.type === "TemplateElement" && HAN.test(node.value.cooked ?? node.value.raw)) {
      out.push({ line: node.loc.start.line + base, text: node.value.cooked ?? node.value.raw });
    } else if (node.type === "Literal" && node.regex && HAN.test(node.regex.pattern)) {
      out.push({ line: node.loc.start.line + base, text: `/${node.regex.pattern}/` });
    }
  });
  return out;
}

function htmlStrings(code) {
  const out = [];
  let inScript = false;
  let scriptStart = 0;
  let scriptBuf = "";
  const lineAt = (idx) => code.slice(0, idx).split("\n").length;
  const parser = new HtmlParser(
    {
      onopentag(name, attrs) {
        for (const [k, v] of Object.entries(attrs)) if (HAN.test(v)) out.push({ line: lineAt(parser.startIndex), text: `${k}="${v}"` });
        if (name === "script") {
          inScript = true;
          scriptBuf = "";
          scriptStart = lineAt(parser.endIndex) - 1;
        }
      },
      ontext(t) {
        if (inScript) scriptBuf += t;
        else if (HAN.test(t)) out.push({ line: lineAt(parser.startIndex), text: t.trim() });
      },
      onclosetag(name) {
        if (name === "script") {
          inScript = false;
          out.push(...jsStrings(scriptBuf, scriptStart));
        }
      },
    },
    { decodeEntities: false },
  );
  parser.write(code);
  parser.end();
  return out;
}

function jsonStrings(code) {
  let data;
  try {
    data = JSON.parse(code);
  } catch {
    // Loose JSON (comments, expressions): fall back to non-comment lines.
    return code.split("\n").flatMap((l, i) => (HAN.test(l) && !/^\s*\/\//.test(l) ? [{ line: i + 1, text: l.trim() }] : []));
  }
  const out = [];
  const visit = (v, p) => {
    if (typeof v === "string") {
      if (HAN.test(v)) out.push({ line: 0, text: v, path: p });
    } else if (v && typeof v === "object") {
      for (const [k, x] of Object.entries(v)) {
        if (HAN.test(k)) out.push({ line: 0, text: k, path: `${p}.<key>` });
        visit(x, `${p}.${k}`);
      }
    }
  };
  visit(data, "$");
  return out;
}

function vueStrings(code) {
  const { descriptor } = parseSfc(code);
  const out = [];
  if (descriptor.template) {
    const base = descriptor.template.loc.start.line - 1;
    out.push(...htmlStrings(descriptor.template.content).map((x) => ({ ...x, line: x.line + base })));
  }
  for (const s of [descriptor.script, descriptor.scriptSetup].filter(Boolean)) out.push(...jsStrings(s.content, s.loc.start.line - 1));
  return out;
}

function scan(file, code) {
  if (!HAN.test(code)) return [];
  if (file.endsWith(".js")) return jsStrings(code);
  if (file.endsWith(".html")) return htmlStrings(code);
  if (file.endsWith(".json")) return jsonStrings(code);
  if (file.endsWith(".vue")) return vueStrings(code);
  return [];
}

const allowDir = path.join(root, "scripts/i18n/web-keep-han");
const allow = {};
if (existsSync(allowDir)) {
  for (const n of readdirSync(allowDir).filter((x) => x.endsWith(".json"))) {
    for (const [f, list] of Object.entries(JSON.parse(readFileSync(path.join(allowDir, n), "utf8")))) {
      allow[f] = allow[f] || new Set();
      for (const x of list) allow[f].add(x.text);
    }
  }
}

const selected = (f) => filters.length === 0 || filters.some((p) => f === p || f.startsWith(`${p}/`));
const files = execFileSync(
  "git",
  ["ls-files", "--cached", "--others", "--exclude-standard", "o2web/source/**/*.js", "o2web/source/**/*.html", "o2web/source/**/*.json", "o2web/source/**/*.vue"],
  { cwd: root, encoding: "utf8", maxBuffer: 1 << 28 },
)
  .split("\n")
  .filter(Boolean)
  .filter((f) => !VENDOR.some((re) => re.test(f)))
  .filter(selected);

let left = 0;
let fileCount = 0;
for (const f of files) {
  const keep = allow[f] || new Set();
  const hits = scan(f, readFileSync(path.join(root, f), "utf8")).filter((h) => !keep.has(h.text));
  if (!hits.length) continue;
  left += hits.length;
  fileCount++;
  console.log(`\n${f} (${hits.length})`);
  for (const h of hits.slice(0, max)) console.log(`  ${h.line ? `L${h.line}` : h.path || ""}: ${JSON.stringify(h.text).slice(0, 100)}`);
  if (hits.length > max) console.log(`  ... ${hits.length - max} more`);
}
console.log(`\n${files.length} files scanned, ${left} Chinese strings left in ${fileCount} files`);
process.exit(left ? 1 : 0);
