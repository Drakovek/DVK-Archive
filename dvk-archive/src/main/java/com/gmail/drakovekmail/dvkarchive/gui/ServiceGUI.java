package com.gmail.drakovekmail.dvkarchive.gui;

import javax.swing.JPanel;

/**
 * Panel for holding UI elements for a DVK Archive service.
 * 
 * @author Drakovek
 */
public abstract class ServiceGUI extends JPanel implements Disabler {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1079185111615979346L;
	
	/**
	 * BaseGUI for getting UI settings
	 */
	protected StartGUI start_gui;
	
	/***
	 * Initializes the ServiceGUI object.
	 * 
	 * @param start_gui Parent of the ServiceGUI
	 */
	public ServiceGUI(StartGUI start_gui) {
		this.start_gui = start_gui;
	}
	
	/**
	 * Returns whether start_gui currently has a directory loaded.
	 * 
	 * @return Whether directory is loaded
	 */
	public boolean directory_loaded() {
		return this.start_gui.get_directory() != null;
	}
}
