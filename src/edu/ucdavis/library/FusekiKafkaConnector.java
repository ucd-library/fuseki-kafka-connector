package edu.ucdavis.library;

import org.apache.jena.fuseki.server.eventbus.DatasetChangesEventBus;

// Look to use: org.apache.jena.sparql.core.DatasetGraphMonitor
// https://stackoverflow.com/questions/51950233/jena-dataset-listener
// https://github.com/rdfhdt/hdt-java/blob/master/hdt-jena/src/main/java/org/rdfhdt/hdtjena/HDTGraphAssembler.java


public class FusekiKafkaConnector {
	
	private static EventHandler eventHandler = new EventHandler();
	
	public static void init() {
		DatasetChangesEventBus.listen(eventHandler);
	}
	
}
