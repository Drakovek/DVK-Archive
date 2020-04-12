package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JComboBox;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionListener;

/**
 * ComboBox UI object for DVK Archive
 * 
 * @author Drakovek
 */
public class DComboBox extends JComboBox<String> {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 3599138495631460455L;

	/**
	 * Initializes the DComboBox.
	 * 
	 * @param base_gui BaseGUI for getting UI options.
	 * @param event DActionEvent to call when item selected.
	 * @param items Items to list in the ComboBox
	 * @param id ID of DComboBox
	 */
	public DComboBox(
			BaseGUI base_gui,
			DActionEvent event,
			String[] items,
			String id) {
		super(items);
		//SET FONT
		this.setFont(base_gui.get_font());
		this.addActionListener(new DActionListener(base_gui, event, id));
	}	
}
