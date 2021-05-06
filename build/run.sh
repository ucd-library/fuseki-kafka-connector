#! /bin/bash


VERSION=3.17.0
JENA_DOCKER_VERSION=latest

set -e
ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $ROOT_DIR/..

if [[ -f "target" ]]; then
  rm -rf target
fi

if [[ -f "lib/jena-fuseki-core-$VERSION.jar" ]]; then
  rm lib/jena-fuseki-core-$VERSION.jar
fi

if [[ -f "lib/jena-fuseki-core-$VERSION.jar" ]]; then
  rm lib/jena-fuseki-webapp-$VERSION.jar
fi

mkdir -p lib
docker pull gcr.io/ucdlib-pubreg/jena-fuseki-eb:$JENA_DOCKER_VERSION
docker run -v $(pwd)/lib:/jars gcr.io/ucdlib-pubreg/jena-fuseki-eb:$JENA_DOCKER_VERSION bash -c "cp /jena-fuseki/lib/jena-fuseki-core-$VERSION.jar /jars/"
docker run -v $(pwd)/lib:/jars gcr.io/ucdlib-pubreg/jena-fuseki-eb:$JENA_DOCKER_VERSION bash -c "cp /jena-fuseki/lib/jena-fuseki-webapp-$VERSION.jar /jars/"

docker run -v $(pwd):/code maven:3-openjdk-8 bash -c "/code/build/build.sh $VERSION /code"