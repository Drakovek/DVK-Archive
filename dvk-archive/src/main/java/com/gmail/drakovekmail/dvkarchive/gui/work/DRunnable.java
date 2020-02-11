package com.gmail.drakovekmail.dvkarchive.gui.work;

/**
 * Default Runnable object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DRunnable implements Runnable {

	/**
	 * ID of the process
	 */
	private String id;
	
	/**
	 * DWorker to call on to start process
	 */
	private DWorker worker;
	
	/**
	 * Initializes the DRunnable object.
	 * 
	 * @param worker DWorker to call on to start process
	 * @param id ID of the process
	 */
	public DRunnable(DWorker worker, String id) {
		this.id = id;
		this.worker = worker;
	}
	
	/**
	 * Runs the main process.
	 */
	@Override
	public void run() {
		this.worker.run(this.id);
	}
}
