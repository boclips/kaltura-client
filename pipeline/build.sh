#!/usr/bin/env bash

set -eu

cwd="$(cd "$(dirname $0)" && pwd)"
export GRADLE_USER_HOME="${cwd}/.gradle"

(
cd source
./gradlew test dependencyCheckAnalyze
)
