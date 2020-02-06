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
	protected BaseGUI base_gui;
	
	/***
	 * Initializes the ServiceGUI object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings.
	 */
	public ServiceGUI(BaseGUI base_gui) {
		this.base_gui = base_gui;
	}
}
