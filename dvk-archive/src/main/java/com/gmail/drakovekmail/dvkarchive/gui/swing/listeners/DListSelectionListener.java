package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Deals with item in a list being selected.
 * 
 * @author Drakovek
 */
public class DListSelectionListener implements ListSelectionListener{

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
	 * @param event DActionEvent to call when item is selected
	 * @param id ID of component calling the action
	 */
	public DListSelectionListener(DActionEvent event, String id) {
		this.event = event;
		this.id = id;
	}
	
	/**
	 * Calls the event method of DActionEvent when item is selected.
	 */
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		this.event.event(this.id);
	}

}
