package com.gmail.drakovekmail.dvkarchive.gui;

import javax.swing.JPanel;
import com.gmail.drakovekmail.dvkarchive.gui.work.DSwingWorker;
import com.gmail.drakovekmail.dvkarchive.gui.work.DWorker;

/**
 * Panel for holding UI elements for a DVK Archive service.
 * 
 * @author Drakovek
 */
public abstract class ServiceGUI extends JPanel implements Disabler, DWorker {
	
	/**
	 * SerialBersionUID
	 */
	private static final long serialVersionUID = -1253621005054405872L;

	/**
	 * BaseGUI for getting UI settings
	 */
	protected StartGUI start_gui;
	
	/**
	 * SwingWorker for running threads
	 */
	protected DSwingWorker sw;
	
	/***
	 * Initializes the ServiceGUI object.
	 * 
	 * @param start_gui Parent of the ServiceGUI
	 */
	public ServiceGUI(StartGUI start_gui) {
		this.start_gui = start_gui;
		this.start_gui.get_base_gui().set_canceled(true);
	}
	
	/**
	 * Returns whether start_gui currently has a directory loaded.
	 * 
	 * @return Whether directory is loaded
	 */
	public boolean directory_loaded() {
		return this.start_gui.get_directory() != null;
	}
	
	/**
	 * Called on when directory opened in the Start GUI.
	 */
	public abstract void directory_opened();
	
	/**
	 * Starts a process thread while disabling UI.
	 * 
	 * @param id ID of the process
	 * @param reset_canceled Whether to reset the canceled variable
	 */
	public void start_process(String id, boolean reset_canceled) {
		//DISABLE ITEMS
		this.start_gui.get_base_gui().set_running(true);
		if(reset_canceled) {
			this.start_gui.get_base_gui().set_canceled(false);
		}
		this.start_gui.disable_all();
		disable_all();
		//START PROCESS
		this.sw = new DSwingWorker(this, id);
		this.sw.execute();
	}
}
