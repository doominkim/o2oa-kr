#!/usr/bin/env node
// Verifies translated language packs (lp/<target>.js) against the zh-cn source.
//
// Usage: node scripts/i18n/check-lp.mjs [--lang ko] [--max 40] [--skip-han] [file-or-dir ...]
//
// For every o2web/source/**/lp/zh-cn.js it loads the source and the target pack
// in isolated sandboxes, then reports:
//   missing   target pack file absent
//   keys      keys present in one pack but not the other, or value type changes
//   vars      placeholder / HTML tag mismatch between source and target strings
//   han       Chinese characters left in a target string or function body
//   ref       target pack still loads another pack via "lp.zh-cn"
//   keep      a keepSource key (scripts/i18n/exceptions.json) differs from the source
// Exit code is 1 when any issue is found.

import { execFileSync } from "node:child_process";
import { existsSync, readFileSync } from "node:fs";
import path from "node:path";
import vm from "node:vm";

const root = execFileSync("git", ["rev-parse", "--show-toplevel"], { encoding: "utf8" }).trim();

const args = process.argv.slice(2);
let lang = "ko";
let max = 40;
let skipHan = false;
const filters = [];
for (let i = 0; i < args.length; i++) {
  if (args[i] === "--lang") lang = args[++i];
  else if (args[i] === "--max") max = Number(args[++i]);
  else if (args[i] === "--skip-han") skipHan = true;
  else filters.push(path.relative(root, path.resolve(args[i])).replace(/\/$/, ""));
}

process.chdir(root);

// A filter selects a zh-cn.js file itself or every pack under a directory.
const selected = (f) => filters.some((p) => f === p || f.startsWith(`${p}/`));
const sources = execFileSync("git", ["ls-files", "o2web/source/**/lp/zh-cn.js"], { encoding: "utf8" })
  .split("\n")
  .filter(Boolean)
  .filter((f) => filters.length === 0 || selected(f));

const AUTO = Symbol("autovivified");
const HAN = /[\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff]/;
// scripts/i18n/exceptions.json, all sections keyed by zh-cn.js path then key path:
//   keepSource     values stored or compared by code; the target must keep the source value
//   relabeledKeys  objects whose keys are display labels; compared by value order, keys translated
//   targetOnlyKeys keys that exist only in the target pack (fork-only UI text with no zh-cn source)
const exceptions = JSON.parse(readFileSync(path.join(root, "scripts/i18n/exceptions.json"), "utf8"));
const underAny = (paths, k) => paths.some((p) => k === p || k.startsWith(`${p}[`) || k.startsWith(`${p}.`));
const kept = (src, k) => underAny(Object.keys(exceptions.keepSource[src] || {}), k);
const relabeled = (src) => new Set(Object.keys(exceptions.relabeledKeys[src] || {}));
const targetOnly = (src, k) => underAny(Object.keys((exceptions.targetOnlyKeys || {})[src] || {}), k);

// Stands in for MWF / o2 globals: reading any property creates a nested stub and
// calling a stub (e.g. MWF.xDesktop.requireApp) is a no-op.
function autoviv() {
  const target = function () {};
  target[AUTO] = true;
  return new Proxy(target, {
    get(t, k) {
      if (typeof k === "symbol" || k === "prototype") return t[k];
      if (!Object.prototype.hasOwnProperty.call(t, k)) t[k] = autoviv();
      return t[k];
    },
    apply() {
      return undefined;
    },
  });
}

function load(file) {
  const window = {};
  const sandbox = { MWF: autoviv(), o2: autoviv(), window, console: { log() {}, warn() {}, error() {} } };
  window.window = window;
  sandbox.self = window;
  vm.createContext(sandbox);
  // MooTools Object.merge, used by a few packs to extend shared LP objects.
  vm.runInContext(
    `Object.merge = function merge(target) {
      for (var i = 1; i < arguments.length; i++) {
        var src = arguments[i];
        if (!src || typeof src !== "object") continue;
        for (var k in src) {
          var v = src[k];
          if (v && typeof v === "object" && !Array.isArray(v)) {
            if (!target[k] || typeof target[k] !== "object") target[k] = {};
            merge(target[k], v);
          } else target[k] = v;
        }
      }
      return target;
    };`,
    sandbox,
  );
  vm.runInContext(readFileSync(file, "utf8"), sandbox, { filename: file, timeout: 5000 });
  return { MWF: sandbox.MWF, o2: sandbox.o2, window };
}

// Flattens a loaded pack into path -> value. Autovivified empty objects and
// window self references are skipped. Objects in `relabel` flatten by value order.
function flatten(value, prefix, out, seen, relabel) {
  if (value && (typeof value === "object" || value[AUTO])) {
    if (seen.has(value)) return out;
    seen.add(value);
    const keys = Object.keys(value);
    if (value[AUTO] && keys.length === 0) return out;
    if (Array.isArray(value) || relabel.has(prefix)) {
      Object.values(value).forEach((v, i) => flatten(v, `${prefix}[${i}]`, out, seen, relabel));
      if (keys.length === 0) out.set(prefix, []);
      return out;
    }
    for (const k of keys) {
      if (k === "window" || k === "self") continue;
      flatten(value[k], prefix ? `${prefix}.${k}` : k, out, seen, relabel);
    }
    return out;
  }
  out.set(prefix, value);
  return out;
}

// Placeholders are ASCII names ({name}, {0}, {$}, {item.text}); braces around
// words like "{组件名称}" are visible hints and get translated.
function tokens(str) {
  const vars = (str.match(/\{[\w$.-]*\}|%[sd]|\$\{[^}]*\}/g) || []).sort();
  const tags = (str.match(/<\/?[a-zA-Z][\w-]*/g) || []).map((t) => t.toLowerCase()).sort();
  return [...vars, ...tags].join(" ");
}

let issueCount = 0;
const report = [];
for (const src of sources) {
  const target = path.join(path.dirname(src), `${lang}.js`);
  const issues = [];
  if (!existsSync(target)) {
    issues.push(`missing ${target}`);
  } else {
    let a;
    let b;
    try {
      a = flatten(load(src), "", new Map(), new Set(), relabeled(src));
    } catch (e) {
      issues.push(`source load error: ${e.message}`);
    }
    try {
      b = flatten(load(target), "", new Map(), new Set(), relabeled(src));
    } catch (e) {
      issues.push(`target load error: ${e.message}`);
    }
    if (lang !== "zh-cn" && /lp[./]zh-cn\b/.test(readFileSync(target, "utf8")))
      issues.push("ref   target still loads a zh-cn pack (use the target language)");
    if (a && b) {
      for (const [k, v] of a) {
        if (!b.has(k)) {
          issues.push(`keys  missing: ${k}`);
          continue;
        }
        const t = b.get(k);
        if (typeof v !== typeof t) {
          issues.push(`keys  type ${typeof v} -> ${typeof t}: ${k}`);
          continue;
        }
        if (kept(src, k)) {
          if (t !== v) issues.push(`keep  ${k}: must stay ${JSON.stringify(v)} (scripts/i18n/exceptions.json)`);
        } else if (typeof t === "string") {
          if (tokens(v) !== tokens(t)) issues.push(`vars  ${k}: [${tokens(v)}] -> [${tokens(t)}]`);
          if (!skipHan && HAN.test(t)) issues.push(`han   ${k}: ${JSON.stringify(t).slice(0, 120)}`);
        } else if (!skipHan && typeof t === "function" && HAN.test(String(t))) {
          issues.push(`han   ${k}: function body`);
        }
      }
      for (const k of b.keys()) if (!a.has(k) && !targetOnly(src, k)) issues.push(`keys  extra: ${k}`);
    }
  }
  issueCount += issues.length;
  report.push({ src, issues });
}

for (const { src, issues } of report) {
  if (issues.length === 0) continue;
  console.log(`\n${src} (${issues.length})`);
  for (const i of issues.slice(0, max)) console.log(`  ${i}`);
  if (issues.length > max) console.log(`  ... ${issues.length - max} more`);
}
const ok = report.filter((r) => r.issues.length === 0).length;
console.log(`\n${ok}/${report.length} packs clean, ${issueCount} issues`);
process.exit(issueCount ? 1 : 0);
