package edu.ucdavis.library;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.server.eventbus.DatasetChangesEvent;
import org.apache.jena.fuseki.server.eventbus.DatasetEventBusListener;
import org.apache.jena.graph.Node;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;

public class EventHandler implements DatasetEventBusListener {

	private static Logger log = Fuseki.serverLog;
	
	private BufferTimer buffer = null;
	private LinkedList<String> quads = null; 
	private HashMap<String, String> kafkaParams;
	private Producer<String, String> producer;
	
	EventHandler(HashMap<String, String> kafkaParams) {
		super();

		
		
//		try {
//			URL url = new URL("http://localhost:8080");
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			con.setRequestMethod("GET");
//			BufferedReader in = new BufferedReader(
//					  new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			StringBuffer content = new StringBuffer();
//			while ((inputLine = in.readLine()) != null) {
//			    content.append(inputLine);
//			}
//			in.close();
//			System.out.println("TESTING: "+ content.toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		this.kafkaParams = kafkaParams;
	}
	
	public void connect() {
		logConnection();
		
		Properties props = new Properties();
		
		// Good list of available properties: 
		// https://jaceklaskowski.gitbooks.io/apache-kafka/kafka-properties.html
		props.put("bootstrap.servers", kafkaParams.get(FusekiKafkaConnector.KAFKA_HOST)+":"+kafkaParams.get(FusekiKafkaConnector.KAFKA_PORT));
		props.put("acks", "all");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");	         
	    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	    props.put("linger.ms", 1);
	    props.put("retries", 0);
	    
		producer = new KafkaProducer<String, String>(props);
	}
	
	private void logConnection() {
		log.info("Attempting kafka connection. Username: " + 
			(kafkaParams.get(FusekiKafkaConnector.KAFKA_USERNAME) == "" ? "[empty]" : kafkaParams.get(FusekiKafkaConnector.KAFKA_USERNAME))+ " " +
			(kafkaParams.get(FusekiKafkaConnector.KAFKA_PASSWORD) == "" ? "without password " : "with password ") +
			"@ http://"+kafkaParams.get(FusekiKafkaConnector.KAFKA_HOST)+":"+kafkaParams.get(FusekiKafkaConnector.KAFKA_PORT)+" "+
			", topic: "+kafkaParams.get(FusekiKafkaConnector.KAFKA_TOPIC)
		);
	}

	@Override
	public void onChange(DatasetChangesEvent e) {
		if( e.getEvent() == "change" ) {
			if( this.quads == null ) this.quads = new LinkedList<String>();
			this.quads.add(this.getActionQuad(e));
			
			if( this.buffer == null ) {
				this.buffer = new BufferTimer(this.quads, this);
				Thread thread = new Thread(this.buffer);
				thread.start();
			}
		}
	}
	
	private String getActionQuad(DatasetChangesEvent e) {
		return e.getQaction().label+": "+
			getLabel(e.getG())+" "+
			getLabel(e.getS())+" "+
			getLabel(e.getP())+" "+
			getLabel(e.getO())+" .";
	}
	
	private String getLabel(Node node) {
		if( node.isBlank() ) return node.getBlankNodeId().getLabelString();
		if( node.isLiteral() ) return node.toString();
		if( node.isURI() ) return "<"+node.toString()+">";
		return "<>";
	}
	
	public void clearBuffer() {
		this.buffer = null;
		this.quads = null;
	}

	public void sendMessage(String msg) {
		System.out.println(kafkaParams.get(FusekiKafkaConnector.KAFKA_TOPIC));
		System.out.println(msg);
		
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(
			kafkaParams.get(FusekiKafkaConnector.KAFKA_TOPIC), 
	        msg
	    );
		try {
			RecordMetadata meta =  producer.send(record).get();
			int t = 1;
			int y = t;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
	}
}