package edu.ucdavis.library;

import java.util.Date;
import java.util.LinkedList;

public class BufferTimer implements Runnable {

	private LinkedList<String> quads;
	private EventHandler handler;
	public boolean running = false;
	
	BufferTimer(LinkedList<String> quads, EventHandler handler) {
		this.quads = quads;
		this.handler = handler;
	}

	@Override
	public void run() {
		while( true ) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			insert();
		}
	}
	
	private void insert() {
		String tx;
		
		synchronized(quads) { 
			if( quads.size() == 0 ) return;
			
			// TODO: add transaction boundaries
			tx = "";
			for( String quad: quads) {
				tx += quad+"\n";
			}
	
			quads.clear();
		}
		
		handler.sendMessage(tx);
	}
	
}
