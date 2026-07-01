#!/usr/bin/env sh
set -e
BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MVNW_DIR="$BASE_DIR/.mvn/apache-maven-3.9.9"
MVNW_ZIP="$BASE_DIR/.mvn/apache-maven-3.9.9-bin.zip"
MVNW_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip"
if [ ! -x "$MVNW_DIR/bin/mvn" ]; then
  mkdir -p "$BASE_DIR/.mvn"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$MVNW_URL" -o "$MVNW_ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$MVNW_URL" -O "$MVNW_ZIP"
  else
    echo "curl or wget is required to bootstrap Maven" >&2
    exit 1
  fi
  unzip -q -o "$MVNW_ZIP" -d "$BASE_DIR/.mvn"
fi
exec "$MVNW_DIR/bin/mvn" "$@"
