#!/bin/bash
set -euo pipefail

image_name=keda-demo
registryHost=localhost:5001

# Check if version is set in environment variable APP_VERSION
if [ -z "${APP_VERSION:-}" ]; then
  echo "APP_VERSION is not set. Running gradle to get version."
  version=`./gradlew -q properties --no-daemon | grep '^version:' | sed -E 's/^version:[[:space:]]*//' || true`
else
  version=${APP_VERSION}
fi

echo "Building with version [${version}]"

./gradlew clean bootJar

echo "Building image ${image_name}:${version} using docker"
docker build --tag=${image_name}:${version} --file=./Containerfile .

echo "Tagging image for registry at ${registryHost}"
docker tag ${image_name}:${version} ${registryHost}/${image_name}:${version}
echo "Pushing image to registry at ${registryHost}"
docker push ${registryHost}/${image_name}:${version}