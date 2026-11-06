#!/usr/bin/env bash
# Build MicroEmulator without Maven: javac + jar only.
# Skips the jour instrumentation step.
# Requires: JDK (javac, jar), curl (only if ASM is not already present).
# ASM 3.1 is found in ~/.m2, or downloaded from Maven Central into build-nomaven/deps.
# Override with: ASM_JAR=/path/to/asm-3.1.jar ./build-nomaven.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build-nomaven"
CLASSES="$OUT/classes"
JAR="$OUT/microemulator.jar"

# ASM 3.1 is the version pinned in microemulator/pom.xml (asm:asm:3.1).
ASM_VERSION=3.1
ASM_URL="https://repo1.maven.org/maven2/asm/asm/${ASM_VERSION}/asm-${ASM_VERSION}.jar"
DEPS="$ROOT/build-nomaven-deps"

find_asm() {
  if [ -n "${ASM_JAR:-}" ] && [ -f "$ASM_JAR" ]; then echo "$ASM_JAR"; return; fi
  local m2="$HOME/.m2/repository/asm/asm/${ASM_VERSION}/asm-${ASM_VERSION}.jar"
  if [ -f "$m2" ]; then echo "$m2"; return; fi
  if [ -f "$DEPS/asm-${ASM_VERSION}.jar" ]; then echo "$DEPS/asm-${ASM_VERSION}.jar"; return; fi
  mkdir -p "$DEPS"
  echo "Downloading ASM ${ASM_VERSION} from Maven Central..." >&2
  curl -fsSL -o "$DEPS/asm-${ASM_VERSION}.jar" "$ASM_URL" || {
    echo "Download failed: $ASM_URL" >&2
    echo "Set ASM_JAR=/path/to/asm-${ASM_VERSION}.jar and rerun." >&2
    exit 1
  }
  echo "$DEPS/asm-${ASM_VERSION}.jar"
}

ASM_JAR="$(find_asm)"
# Sanity check: the jar must contain the ASM 3 API that microemu uses.
if ! jar tf "$ASM_JAR" | grep -q 'org/objectweb/asm/ClassAdapter.class'; then
  echo "$ASM_JAR does not look like ASM 3.x (no ClassAdapter)." >&2
  exit 1
fi
echo "Using ASM: $ASM_JAR"

MODULES=(
  microemu-cldc
  microemu-midp
  microemu-javase
  microemu-javase-swing
  microemu-extensions/microemu-jsr-75
  microemu-extensions/microemu-jsr-82
  microemu-extensions/microemu-jsr-120
  microemu-extensions/microemu-jsr-135
  microemu-extensions/microemu-jsr-184
)
RESOURCE_MODULES=(
  microemu-javase
  microemu-extensions/microemu-device-large
  microemu-extensions/microemu-device-minimum
  microemu-extensions/microemu-device-resizable
)

rm -rf "$OUT"
mkdir -p "$CLASSES"

SOURCES="$OUT/sources.txt"
: > "$SOURCES"
for m in "${MODULES[@]}"; do
  find "$ROOT/$m/src/main/java" -name '*.java' >> "$SOURCES"
done

echo "Compiling $(wc -l < "$SOURCES") sources with $(javac -version 2>&1)"
javac -nowarn -encoding ISO-8859-1 -cp "$ASM_JAR" -d "$CLASSES" "@$SOURCES"

for m in "${RESOURCE_MODULES[@]}"; do
  if [ -d "$ROOT/$m/src/main/resources" ]; then
    cp -R "$ROOT/$m/src/main/resources/." "$CLASSES/"
  fi
done

# The emulator needs ASM at runtime (MIDlet class instrumentation), so bundle it.
unzip -qo "$ASM_JAR" -x 'META-INF/*' -d "$CLASSES"

jar cfe "$JAR" org.microemu.app.Main -C "$CLASSES" .
echo "Built $JAR"
