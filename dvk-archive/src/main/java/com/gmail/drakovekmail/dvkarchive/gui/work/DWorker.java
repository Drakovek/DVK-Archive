package com.gmail.drakovekmail.dvkarchive.gui.work;

/**
 * Interface with methods to be called in threaded processes.
 * 
 * @author Drakovek
 */
public interface DWorker {
	
	/**
	 * Method containing threaded process.
	 * 
	 * @param id ID of the process
	 */
	public void run(String id);
	
	/**
	 * Method called when process is finished.
	 * 
	 * @param id ID of the process
	 */
	public void done(String id);
}
