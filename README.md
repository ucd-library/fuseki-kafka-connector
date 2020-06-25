# fuseki-kafka-connector
This is plugin for the ucd fuseki build that connects to the graph event bus to send messages to kafka

# Build JAR

First download or build the new jena-fuseki-core jar. If building from ucd src (https://github.com/ucd-library/jena) run:

```
cd jena-fuseki2/jena-fuseki-core
mvn package -Drat.skip=true 
```

Now install the jar into your local mvn repo.  If the jar was installed using ucd src, the command would look something like this.  If downloaded, point `-Dfile` at the jar.

```
mvn install:install-file \
   -Dfile=$(pwd)/../jena/jena-fuseki2/jena-fuseki-core/target/jena-fuseki-core-3.16.0-SNAPSHOT.jar \
   -DgroupId=org.apache.jena \
   -DartifactId=jena-fuseki-core \
   -Dversion=3.16.0-SNAPSHOT \
   -Dpackaging=jar \
   -DgeneratePom=true
```

finally, to build the project:

```
mvn package -Drat.skip=true 
```

This should create the following jar `target/jena-kafka-connector-0.0.1-SNAPSHOT.jar`