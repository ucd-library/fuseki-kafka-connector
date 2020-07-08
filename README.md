# fuseki-kafka-connector
This is plugin for the [UC Davis Library - Fuseki](https://github.com/ucd-library/jena) build that connects to the new dataset changes event bus to send rdf patch like messages to Kafka.

# Usage

The `jena-kafka-connector` jar uses a custom build of Apache Jena that wraps all datasets with a `DatasetChanges` interface and sends all changes as events to a new `DatasetEventBus`.  This `DatasetEventBus` is static and accessible from a class loaded using the Fuseki config loadClass.  The `FusekiKafkaConnector` listens to dataset change events on the event bus, buffers them for a short amount of time, and creates [rdf-patch](https://afs.github.io/rdf-patch/) like messages that it puts on a Kafka message stream which can be consumed by external services.

## Configure Library

First, copy the jena-kafka-connector-*.jar into the `$FUSEKI_BASE/extra/` as well as the kafka-clients-*.jar jar provided in the root of this repository (or you can get it off maven central).

Next, add the Kafka configuration and ja:loadClass prefix to the Fuseki config.ttl file. The configuration parameters are shown below with defaults provided.  If you are using a default value, you can leave it out of config file.

config.ttl
```ttl
## Fuseki Server configuration file.

@prefix :        <#> .
@prefix fuseki:  <http://jena.apache.org/fuseki#> .
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
@prefix ja:      <http://jena.hpl.hp.com/2005/11/Assembler#> .
@prefix ucd:     <http://library.ucdavis.edu/ns#> .

[] rdf:type fuseki:Server ;

   # Add any custom classes you want to load.
   # Must have a "public static void init()" method.
   ja:loadClass "edu.ucdavis.library.FusekiKafkaConnector" ;   

   # End triples.
   .

# FusekiKafkaConnector config
[] rdf:type ucd:Kafka ;
  ucd:kafkaHost "kafka" ;
  ucd:kafkaPort "9092" ;
  ucd:kafkaTopic "fuseki-rdf-patch" ;
  ucd:kafkaUsername "" ;
  ucd:kafkaPassword "" ;
  .
```


# Build JAR

The build process happen inside the `maven:3-openjdk-8` docker image.  The `./build/run.sh` script does the following:

  - Cleans the `target` and `lib` directories
  - pulls the correct `ucdlib/jena-fuseki-eb` docker image which has the custom `jena-fuseki-core-*.jar` and `jena-fuseki-webapp-*.jar` required for the build.
  - loads the required jars from the docker image into the `./lib` directory
  - mounts this directory into the `maven:3-openjdk-8` container
  - runs the `./build/build.sh` command from within the container which; installs the custom jars from local files, then runs the `mvn package` command to build the jars
  - `mvn package` command populates the `target` directory which should contain `jena-kafka-connector-*.jar` if all was successfull. 

# Develop

  - Load the https://github.com/ucd-library/jena library into eclipse.  You can follow the Jena provided tutorial here: https://jena.apache.org/tutorials/using_jena_with_eclipse.html
  - Add this repository to eclipse, importing as a maven package.  Make sure to run `maven install` from within the project in eclipse.
  - modify the config.ttl file to use this jar.  See Usage section above.  The config file is located in `./jena-fuseki2/jena-fuseki-webapp/run/config.ttl` which is in the `jena-fuseki-webapp` sub project in eclipse.
  - Addthe `Java Appplication` run configuration with main set to `org.apache.jena.fuseki.cmd.FusekiCmd` in project `jena-fuseki-webapp`.  No arguments required.  You many need to click the 'Dependencies' tab and add the `fuseki-kafka-connector` project.