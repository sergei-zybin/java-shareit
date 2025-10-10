#!/bin/bash
set -e

cp -rf ./tests/checkstyle.xml ./checkstyle.xml
cp -rf ./tests/suppressions.xml ./suppressions.xml
cp -rf ./tests/gateway/Dockerfile ./gateway/Dockerfile
cp -rf ./tests/server/Dockerfile ./server/Dockerfile
cp -rf ./tests/docker-compose.yml ./docker-compose.yml

mvn enforcer:enforce -Denforcer.rules=requireProfileIdsExist -P check --no-transfer-progress &&
mvn verify -P check,coverage --no-transfer-progress &&

docker compose -f docker-compose.yml build
docker compose -f docker-compose.yml up --detach

echo "Docker is up"
echo "Waiting for services to start..."

sleep 30

chmod a+x ./tests/.github/workflows/wait-for-it.sh
./tests/.github/workflows/wait-for-it.sh -t 120 localhost:9090 &&
echo "Server is up" &&
./tests/.github/workflows/wait-for-it.sh -t 120 localhost:8080 &&
echo "Gateway is up"

result=$?
echo "Docker containers status:"
docker compose -f docker-compose.yml ps

echo "Recent logs:"
docker compose -f docker-compose.yml logs --tail=50

exit $result