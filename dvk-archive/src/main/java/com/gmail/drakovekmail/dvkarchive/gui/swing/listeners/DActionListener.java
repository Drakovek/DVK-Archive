package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Deals with GUI components' action events.
 * 
 * @author Drakovek
 */
public class DActionListener implements ActionListener{

	/**
	 * DActionEvent to call when action occurs.
	 */
	private DActionEvent event;
	
	/**
	 * ID of component calling the action.
	 */
	private String id;
	
	/**
	 * Initializes DActionListener.
	 * 
	 * @param event DActionEvent to call when action occurs
	 * @param id ID of component calling the action
	 */
	public DActionListener(DActionEvent event, String id) {
		this.event = event;
		this.id = id;
	}
	
	/**
	 * Calls the event method of DActionEvent when action occurs.
	 */
	@Override
	public void actionPerformed(ActionEvent arg) {
		this.event.event(this.id);
	}
}
