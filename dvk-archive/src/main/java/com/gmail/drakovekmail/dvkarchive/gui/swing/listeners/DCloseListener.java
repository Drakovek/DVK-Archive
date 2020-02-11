package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Deals with a GUI window closing.
 * 
 * @author Drakovek
 */
public class DCloseListener implements WindowListener {

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
	 * Initializes the DCloseListener object
	 * 
	 * @param base_gui BaseGUI for determining if process is running
	 * @param event DActionEvent to call when action occurs
	 * @param id ID of component calling the action
	 */
	public DCloseListener(BaseGUI base_gui, DActionEvent event, String id) {
		this.base_gui = base_gui;
		this.event = event;
		this.id = id;
	}
	
	/**
	 * Calls the event method of DActionEvent when closing attempted.
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(!this.base_gui.is_running()) {
			this.event.event(this.id);
		}
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}
