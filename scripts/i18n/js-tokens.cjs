// Reads [[file, oldSource, newSource], ...] on stdin and prints {file: result} where result is
// "same", "unparsable" (acorn could not tokenize one side) or a description of the first
// differing token. Comments are dropped and string/template contents blanked, so edits
// limited to comments and string text compare as "same". Used by verify-text-only.py.
// acorn 8 (the top-level node_modules/acorn is 6.x, which has no "latest" ecmaVersion).
const acorn = require(require("path").join(__dirname, "../../node_modules/terser/node_modules/acorn"));

function tokens(src) {
  const out = [];
  for (const t of acorn.tokenizer(src, { ecmaVersion: "latest", allowHashBang: true, allowReturnOutsideFunction: true })) {
    const type = t.type.label;
    if (type === "string" || type === "template") out.push(type);
    else if (type === "regexp") out.push("regexp:" + t.value.pattern + "/" + t.value.flags);
    else out.push(type + ":" + (t.value === undefined ? "" : String(t.value)));
  }
  return out;
}

let input = "";
process.stdin.on("data", (d) => (input += d));
process.stdin.on("end", () => {
  const result = {};
  for (const [file, oldSrc, newSrc] of JSON.parse(input)) {
    let a, b;
    try {
      a = tokens(oldSrc);
      b = tokens(newSrc);
    } catch (e) {
      result[file] = "unparsable";
      continue;
    }
    const n = Math.max(a.length, b.length);
    let diff = -1;
    for (let i = 0; i < n; i++) if (a[i] !== b[i]) { diff = i; break; }
    result[file] = diff < 0 ? "same" : `code tokens changed at token ${diff}: ${a[diff]} -> ${b[diff]}`;
  }
  process.stdout.write(JSON.stringify(result));
});
