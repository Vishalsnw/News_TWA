#!/bin/bash

GRADLE_VERSION=8.4

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
CLASSPATH="gradle/wrapper/gradle-wrapper.jar"

JAVA_HOME="${JAVA_HOME:-}"

if [ -z "$JAVA_HOME" ]; then
    JAVACMD="java"
else
    JAVACMD="$JAVA_HOME/bin/java"
fi

if ! command -v "$JAVACMD" &> /dev/null; then
    echo "ERROR: JAVA_HOME is not set and no 'java' command could be found."
    exit 1
fi

exec "$JAVACMD" \
    -Xmx64m \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
