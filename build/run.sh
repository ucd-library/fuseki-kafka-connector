#! /bin/bash


VERSION=3.16.0-SNAPSHOT

set -e
ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $ROOT_DIR/..

if [[ ! -f "lib/jena-fuseki-core-$VERSION.jar" ]]; then
  mkdir -p lib
  docker run -v $(pwd)/lib:/jars ucdlib/jena-fuseki-eb:latest bash -c "cp /jena-fuseki/lib/jena-fuseki-core-$VERSION.jar /jars/"
fi

docker run -v $(pwd):/code maven:3-openjdk-8 bash -c "/code/build/build.sh $VERSION /code"