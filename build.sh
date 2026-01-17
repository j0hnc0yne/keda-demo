#!/bin/bash
set -euo pipefail
# Build mock image using podman
# Usage: ./scripts/build-mock-image.sh

image_name=keda-demo

# Check if version is set in environment variable APP_VERSION
if [ -z "${APP_VERSION:-}" ]; then
  echo "APP_VERSION is not set. Running gradle to get version."
  version=`./gradlew -q properties --no-daemon | grep '^version:' | sed -E 's/^version:[[:space:]]*//' || true`
else
  version=${APP_VERSION}
fi

echo "Building with version [${version}]"

./gradlew clean bootJar

echo "Building image ${image_name}:${version} using podman"
podman build --tag=${image_name}:${version} --file=./Containerfile .

registryHost=$(microk8s kubectl get node microk8s-vm -o jsonpath='{.status.addresses[0].address}')

echo "Tagging image for registry at ${registryHost}:32000"
podman tag ${image_name}:${version} ${registryHost}:32000/${image_name}:${version}
echo "Pushing image to registry at ${registryHost}:32000"
podman push ${registryHost}:32000/${image_name}:${version}
