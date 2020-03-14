package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JTextField;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionListener;

/**
 * TextField GUI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DTextField extends JTextField {
	
	//TODO ADD INSETS
	
	/**
	 * ID for the text field
	 */
	private String id;
	
	/**
	 * DActionEvent to call when action occurs
	 */
	private DActionEvent event;
	
	/**
	 * ActionListener for getting button requests
	 */
	private DActionListener listener;

	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -8301547490896455231L;

	/**
	 * Initializes the DTextField object
	 * 
	 * @param base_gui BaseGUI for getting UI settings.
	 * @param event DActionEvent to call when action occurs
	 * @param id ID for the text field
	 */
	public DTextField(BaseGUI base_gui, DActionEvent event, String id) {
		this.id = id;
		this.event = event;
		//SET FONT
		this.setFont(base_gui.get_font());
		//SET ACTION
		this.listener = new DActionListener(base_gui, event, id);
		this.addActionListener(this.listener);
	}
	
	/**
	 * Sets the text field to allow actions to be recorded,
	 * even while a process is running.
	 */
	public void always_allow_action() {
		this.removeActionListener(this.listener);
		this.listener = new DActionListener(
				null, 
				this.event,
				this.id);
		this.addActionListener(this.listener);
	}
}
