package com.gmail.drakovekmail.dvkarchive.gui.work;

import javax.swing.SwingWorker;

/**
 * DSwingWorker for running Swing thread processes.
 * 
 * @author Drakovek
 */
public class DSwingWorker extends SwingWorker<Void, Void> {

	/**
	 * ID of the process
	 */
	private String id;
	
	/**
	 * DWorker to call for processes
	 */
	private DWorker worker;
	
	/**
	 * Initializes the DSwingWorker object.
	 * 
	 * @param worker DWorker to call for processes
	 * @param id ID of the process
	 */
	public DSwingWorker(DWorker worker, final String id) {
		this.worker = worker;
		this.id = id;
	}
	
	/**
	 * Main process
	 */
	@Override
	protected Void doInBackground() throws Exception {
		this.worker.run(this.id);
		return null;
	}
	
	/**
	 * Runs when process is finished
	 */
	@Override
	protected void done() {
		try {
			this.get();
		}
		catch(Exception e) {
			System.out.println("SwingWorker exception");
			e.printStackTrace();
		}
		this.worker.done(this.id);
	}
}
