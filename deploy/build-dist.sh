#!/bin/bash
# Assembles the runnable o2server distribution for the container image:
#   official release (deploy/release.env) + this fork's web build + patched server classes.
#
# Usage: deploy/build-dist.sh <release zip> <JDK 21 home> <output dir>  (the release was built with JDK 21)
# Prerequisite: `npx gulp build_web` has produced target/o2server/servers/webServer.
# Result: <output dir>/o2server, ready for deploy/Dockerfile.
set -euo pipefail
repo="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=release.env
source "$repo/deploy/release.env"
zip="$1"; java_home="$2"; out="$3"
web="$repo/target/o2server/servers/webServer"
[ -d "$web/x_desktop" ] || { echo "missing web build: run npx gulp build_web first" >&2; exit 1; }

actual="$(shasum -a 256 "$zip" | cut -d' ' -f1)"
if [ "$actual" != "$RELEASE_SHA256" ]; then
  echo "release checksum mismatch: expected $RELEASE_SHA256, got $actual" >&2; exit 1
fi

rm -rf "$out"; mkdir -p "$out"
unzip -q "$zip" -d "$out"
dist="$out/o2server"
[ -f "$dist/console.jar" ] || { echo "unexpected release layout in $zip" >&2; exit 1; }

# The image runs on the base image's JDK; drop bundled JVMs and other-OS launchers.
rm -rf "$dist/jvm" "$dist"/*.bat "$dist"/*_{arm,mips,raspi,sw,macosx64,macosarm,windows}*.sh 2>/dev/null || true

rsync -a "$web/" "$dist/servers/webServer/"
# Compile-only dependency (never copied into the distribution).
dep="$out/compile-deps/$(basename "$COMPILE_DEP_URL")"
mkdir -p "$out/compile-deps"
curl -fsSL --retry 3 -o "$dep" "$COMPILE_DEP_URL"
[ "$(shasum -a 256 "$dep" | cut -d' ' -f1)" = "$COMPILE_DEP_SHA256" ] || { echo "compile dependency checksum mismatch" >&2; exit 1; }
python3 "$repo/deploy/patch-classes.py" --server "$dist" --java-home "$java_home" --base "$UPSTREAM_BASE" --compile-cp "$dep"
install -m 0755 "$repo/deploy/entrypoint.sh" "$dist/entrypoint.sh"
echo "distribution ready: $dist"
