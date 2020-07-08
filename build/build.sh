#! /bin/bash


VERSION=$1
ROOT_DIR=$2
set -e

mvn install:install-file \
   -Dfile=$ROOT_DIR/lib/jena-fuseki-core-$VERSION.jar \
   -DgroupId=org.apache.jena \
   -DartifactId=jena-fuseki-core \
   -Dversion=$VERSION \
   -Dpackaging=jar \
   -DgeneratePom=true

mvn install:install-file \
   -Dfile=$ROOT_DIR/lib/jena-fuseki-webapp-$VERSION.jar \
   -DgroupId=org.apache.jena \
   -DartifactId=jena-fuseki-webapp \
   -Dversion=$VERSION \
   -Dpackaging=jar \
   -DgeneratePom=true

cd $ROOT_DIR
mvn package -Dmaven.test.skip=true -Drat.skip=true