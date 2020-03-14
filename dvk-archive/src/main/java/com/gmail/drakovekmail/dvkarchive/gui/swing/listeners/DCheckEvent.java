package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

/**
 * Contains methods to run when a checkbox event occurs.
 * 
 * @author Drakovek
 */
public interface DCheckEvent {
	
	/**
	 * Runs when checkbox is checked or unchecked.
	 * 
	 * @param id ID of the checkbox checked/unchecked
	 * @param checked Whether the checkbox is checked
	 */
	public void check_event(String id, boolean checked);
}
