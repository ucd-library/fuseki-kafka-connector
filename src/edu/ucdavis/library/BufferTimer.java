package edu.ucdavis.library;

import java.util.LinkedList;

public class BufferTimer implements Runnable {

	private LinkedList<String> quads = new LinkedList<String>();
	private EventHandler handler;
	
	BufferTimer(EventHandler handler) {
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
	
	public synchronized void addQuad(String quad) {
		quads.push(quad);
	}
	
	private synchronized void insert() {
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
