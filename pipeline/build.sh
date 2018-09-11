#!/usr/bin/env bash

set -eu

cwd="$(cd "$(dirname $0)" && pwd)"
export GRADLE_USER_HOME="${cwd}/.gradle"

version=$(cat version/version)

(
cd source
./gradlew -Pversion=${version} clean build --rerun-tasks
)

cp -a source/* dist/
