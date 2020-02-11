package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Deals with GUI components' action events.
 * 
 * @author Drakovek
 */
public class DActionListener implements ActionListener{

	/**
	 * BaseGUI for determining if process is running
	 */
	private BaseGUI base_gui;
	
	/**
	 * DActionEvent to call when action occurs
	 */
	private DActionEvent event;
	
	/**
	 * ID of component calling the action
	 */
	private String id;
	
	/**
	 * Initializes DActionListener.
	 * 
	 * @param base_gui BaseGUI for determining if process is running
	 * @param event DActionEvent to call when action occurs
	 * @param id ID of component calling the action
	 */
	public DActionListener(BaseGUI base_gui, DActionEvent event, String id) {
		this.base_gui = base_gui;
		this.event = event;
		this.id = id;
	}
	
	/**
	 * Calls the event method of DActionEvent when action occurs.
	 */
	@Override
	public void actionPerformed(ActionEvent arg) {
		if(!this.base_gui.is_running()) {
			this.event.event(this.id);
		}
	}
}
