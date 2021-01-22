#!/bin/sh

cd "$(dirname "$0")" || exit 131

docker-compose down

mkdir -p volumes

docker-compose up -d
