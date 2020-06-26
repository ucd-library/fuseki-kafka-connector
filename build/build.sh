#! /bin/bash


VERSION=$1
ROOT_DIR=$2
set -e


# LIBS=("core" "arq", "fuseki-core")

# for lib in "${DEPENDS[@]}"; do
#   mvn install:install-file \
#     -Dfile=$(pwd)/lib/jena-$lib-$VERSION.jar \
#     -DgroupId=org.apache.jena \
#     -DartifactId=jena-$lib \
#     -Dversion=$VERSION \
#     -Dpackaging=jar \
#     -DgeneratePom=true
# done

mvn install:install-file \
   -Dfile=$ROOT_DIR/lib/jena-fuseki-core-$VERSION.jar \
   -DgroupId=org.apache.jena \
   -DartifactId=jena-fuseki-core \
   -Dversion=$VERSION \
   -Dpackaging=jar \
   -DgeneratePom=true

cd $ROOT_DIR
mvn package -Dmaven.test.skip=true -Drat.skip=true