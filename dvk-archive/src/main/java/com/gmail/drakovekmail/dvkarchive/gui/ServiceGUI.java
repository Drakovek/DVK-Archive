package com.gmail.drakovekmail.dvkarchive.gui;

import javax.swing.JPanel;

/**
 * Panel for holding UI elements for a DVK Archive service.
 * 
 * @author Drakovek
 */
public class ServiceGUI extends JPanel {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 6231675453440569722L;
	
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
}
