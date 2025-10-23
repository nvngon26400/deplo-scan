#!/usr/bin/env bash
set -euo pipefail

# Move into the Spring Boot app directory
cd demo

# Ensure Gradle wrapper is executable
chmod +x ./gradlew || true

# Build the application (skip tests for faster deploy)
./gradlew clean build -x test

# Find the generated Spring Boot jar
JAR_FILE=$(ls build/libs/*-SNAPSHOT.jar | head -n 1)

# Fallback if pattern did not match
if [ -z "${JAR_FILE:-}" ]; then
  JAR_FILE=$(ls build/libs/*.jar | head -n 1)
fi

# Run the app binding to Railway's PORT if provided
exec java -jar "$JAR_FILE"