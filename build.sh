#!/bin/bash
set -euo pipefail
# Build mock image using podman
# Usage: ./scripts/build-mock-image.sh

image_name=keda-demo

# Check if version is set in environment variable APP_VERSION
if [ -z "${APP_VERSION:-}" ]; then
  echo "APP_VERSION is not set. Using default version 'latest'."
  version=latest
else
  version=${APP_VERSION}
  echo "Building with version ${APP_VERSION}"
fi

./gradlew clean bootJar

podman build --tag=${image_name}:${version} --file=./Containerfile .
#  --build-arg=GIT_COMMIT=$(git rev-parse HEAD) \
#  --build-arg=GIT_BRANCH=$(git rev-parse --abbrev-ref HEAD) \
#  --build-arg=GIT_TAG=$(git describe --tags --abbrev=0) \

registryHost=$(multipass list | grep microk8s | awk '{print $3}')

podman tag ${image_name}:${version} ${registryHost}:32000/${image_name}:${version}
podman push ${registryHost}:32000/${image_name}:${version}
