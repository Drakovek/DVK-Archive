package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Deals with CheckBoxes being checked or unchecked.
 * 
 * @author Drakovek
 */
public class DCheckListener implements ItemListener {
	
	/**
	 * ID of component calling the action
	 */
	private String id;
	
	/**
	 * DCheckEvent to call when action occurs
	 */
	private DCheckEvent event;
	
	/**
	 * Initializes DCheckListener.
	 * 
	 * @param base_gui BaseGUI for determining if process is running
	 * @param event DCheckEvent to call when action occurs
	 * @param id ID of component calling the action
	 */
	public DCheckListener(
			BaseGUI base_gui,
			DCheckEvent event,
			String id) {
		this.event = event;
		this.id = id;
	}
	
	@Override
	public void itemStateChanged(ItemEvent i_event) {
		boolean selected = i_event.getStateChange() == ItemEvent.SELECTED;
		this.event.check_event(this.id, selected);
	}
}
