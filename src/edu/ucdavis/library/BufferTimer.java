package edu.ucdavis.library;

import java.util.LinkedList;
import org.apache.kafka.clients.producer.Producer;

public class BufferTimer implements Runnable {

	private LinkedList<String> quads = null;
	private EventHandler handler;
	
	BufferTimer(LinkedList<String> quads, EventHandler handler) {
		this.quads = quads;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		insert();
	}
	
	private void insert() {
		if( quads.size() == 0 ) return;
		
		// TODO: add transaction boundaries
		String tx = "";
		for( String quad: quads) {
			tx += quad+"\n";
		}

		this.handler.sendMessage(tx);
		this.handler.clearBuffer();
	}

}
