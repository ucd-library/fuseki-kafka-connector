package edu.ucdavis.library;

import org.apache.jena.fuseki.server.eventbus.DatasetChangesEvent;
import org.apache.jena.fuseki.server.eventbus.DatasetEventBusListener;
import org.apache.jena.graph.Node;

public class EventHandler implements DatasetEventBusListener {


	@Override
	public void onChange(DatasetChangesEvent e) {
		if( e.getEvent() == "change" ) {
			System.out.println(e.getEvent()+" ("+e.getQaction().label+"): "+getLabel(e.getG())+" "+getLabel(e.getS())+" "+getLabel(e.getP())+" "+getLabel(e.getO()));
		} else {
			System.out.println(e.getEvent());
		}
	}
	
	public String getLabel(Node node) {
		if( node.isLiteral() ) return node.getLiteralValue().toString();
		if( node.isURI() ) return node.getURI();
		return "<>";
	}


}
