package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JTextField;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * TextField GUI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DTextField extends JTextField {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -8301547490896455231L;

	/**
	 * Initializes the DTextField object
	 * 
	 * @param base_gui BaseGUI for getting UI settings.
	 */
	public DTextField(BaseGUI base_gui) {
		this.setFont(base_gui.get_font());
	}
}
