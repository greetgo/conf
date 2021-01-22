#!/bin/sh

cd "$(dirname "$0")" || exit 131

docker-compose down
