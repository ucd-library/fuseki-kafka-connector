package edu.ucdavis.library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.FusekiException;
import org.apache.jena.fuseki.server.eventbus.DatasetChangesEventBus;
import org.apache.jena.fuseki.webapp.FusekiServerListener;
import org.apache.jena.graph.Node;
import org.apache.jena.iri.IRI;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.sparql.core.assembler.AssemblerUtils;
import org.apache.jena.sparql.util.graph.GraphUtils;
import org.slf4j.Logger;

// Look to use: org.apache.jena.sparql.core.DatasetGraphMonitor
// https://stackoverflow.com/questions/51950233/jena-dataset-listener
// https://github.com/rdfhdt/hdt-java/blob/master/hdt-jena/src/main/java/org/rdfhdt/hdtjena/HDTGraphAssembler.java


public class FusekiKafkaConnector {
    private static Logger log = Fuseki.configLog;
	
    public static String NS = "http://library.ucdavis.edu/ns#";
    public static String PREFIX = "PREFIX ucd: <"+NS+"> \n";
    private static Model model = ModelFactory.createDefaultModel();

    public static final Resource KAFKA_CONFIG = resource("Kafka");
    public static final String KAFKA_USERNAME = "KAFKA_USERNAME";
    public static final String KAFKA_PASSWORD = "KAFKA_PASSWORD";
    public static final String KAFKA_HOST = "KAFKA_HOST";
    public static final String KAFKA_PORT = "KAFKA_PORT";
    public static final String KAFKA_TOPIC = "KAFKA_TOPIC";
    
    public static HashMap<String, Resource> KAFKA_CONFIG_PARAMS = new HashMap<String, Resource>();
    static {
    	KAFKA_CONFIG_PARAMS.put(KAFKA_USERNAME, resource("kafkaUsername"));
    	KAFKA_CONFIG_PARAMS.put(KAFKA_PASSWORD, resource("kafkaPassword"));
    	KAFKA_CONFIG_PARAMS.put(KAFKA_HOST, resource("kafkaHost"));
    	KAFKA_CONFIG_PARAMS.put(KAFKA_PORT, resource("kafkaPort"));
    	KAFKA_CONFIG_PARAMS.put(KAFKA_TOPIC, resource("kafkaTopic"));
    }
    
    public static final Resource KAFKA_CONFIG_USERNAME = resource("kafkaUsername");
    public static final Resource KAFKA_CONFIG_PASSWORD = resource("kafkaPassword");
    public static final Resource KAFKA_CONFIG_HOST = resource("kafkaHost");
    public static final Resource KAFKA_CONFIG_PORT = resource("kafkaPort");
    public static final Resource KAFKA_CONFIG_TOPIC = resource("kafkaTopic");
    
    public static HashMap<String, String> KAFKA_PARAMS = new HashMap<String, String>();
    static { 
    	KAFKA_PARAMS.put(KAFKA_USERNAME, "");
    	KAFKA_PARAMS.put(KAFKA_PASSWORD, "");
    	KAFKA_PARAMS.put(KAFKA_HOST, "kafka");
    	KAFKA_PARAMS.put(KAFKA_PORT, "9092");
    	KAFKA_PARAMS.put(KAFKA_TOPIC, "fuseki-rdf-patch");
    };

    private static boolean kafkaEnabled = false;
	private static EventHandler eventHandler = new EventHandler(KAFKA_PARAMS);
	
	
	public static void init() {
		initParams();
		if( !kafkaEnabled ) return;
	
		eventHandler.connect();
		DatasetChangesEventBus.listen(eventHandler);
	}
	
	private static void initParams() {
		String configFile = FusekiServerListener.initialSetup.fusekiServerConfigFile;
		Model model = AssemblerUtils.readAssemblerFile(configFile);
		List<Resource> kafkaConfigs = GraphUtils.listResourcesByType(model, KAFKA_CONFIG);
		Map<String, String> env = System.getenv();
		
		if( kafkaConfigs.size() == 1 ) {
			kafkaEnabled = true;
			Resource config = kafkaConfigs.get(0);
			
			for(Entry<String, String> entry: KAFKA_PARAMS.entrySet() ) {
				if( env.containsKey(entry.getKey()) ) {
					KAFKA_PARAMS.put(entry.getKey(), env.get(entry.getKey()));
					continue;
				}
				
				String configVal = getConfigParam(model, config, KAFKA_CONFIG_PARAMS.get(entry.getKey()));
				if( configVal != null ) {
					KAFKA_PARAMS.put(entry.getKey(), configVal);
				}
			}
	       
			
			DatasetChangesEventBus.listen(eventHandler);
		} else {
			log.warn("No kafka or more than one kafka config found.  Ignoring");
		}
	}
	
	private static String getConfigParam(Model model, Resource config, Resource param) {
		String query = PREFIX+"SELECT * { ?s ?param ?o }";
        QuerySolutionMap qsm = new QuerySolutionMap();
        qsm.add("s", config);
        qsm.add("param", param);

        QueryExecution qExec = QueryExecutionFactory.create(query, model, qsm);
        ResultSet rs = ResultSetFactory.copyResults(qExec.execSelect());
        return getObject(rs);
	}
	
	private static String getObject(ResultSet rs) {
		if( !rs.hasNext() ) return null;
		QuerySolution result = rs.next();
		Node node = result.get("?o").asNode();
		return node.getLiteralValue().toString();
	}
	
    private static Resource resource(String localname) { 
    	return model.createResource(iri(localname)); 
    }
    
    private static String iri(String localname) {
        String uri = NS + localname;
        IRI iri = IRIResolver.parseIRI(uri);
        if ( iri.hasViolation(true) )
            throw new FusekiException("Bad IRI: "+iri);
        if ( ! iri.isAbsolute() )
            throw new FusekiException("Bad IRI: "+iri);

        return uri;
    }

	
}
