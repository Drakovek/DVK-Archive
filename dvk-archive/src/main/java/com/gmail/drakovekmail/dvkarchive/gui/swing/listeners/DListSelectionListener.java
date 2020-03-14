package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Deals with item in a list being selected.
 * 
 * @author Drakovek
 */
public class DListSelectionListener implements ListSelectionListener{

	/**
	 * BaseGUI for determining if process is running
	 */
	private BaseGUI base_gui;
	
	/**
	 * DActionEvent to call when item is selected
	 */
	private DActionEvent event;
	
	/**
	 * ID of component calling the action
	 */
	private String id;
	
	/**
	 * Initializes DListSelectionListener.
	 * 
	 * @param base_gui BaseGUI for determining if process is running
	 * @param event DActionEvent to call when item is selected
	 * @param id ID of component calling the action
	 */
	public DListSelectionListener(BaseGUI base_gui, DActionEvent event, String id) {
		this.base_gui = base_gui;
		this.event = event;
		this.id = id;
	}
	
	/**
	 * Calls the event method of DActionEvent when item is selected.
	 */
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if(this.base_gui == null || !this.base_gui.is_running()) {
			this.event.event(this.id);
		}
	}

}
