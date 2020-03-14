package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JCheckBox;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DCheckEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DCheckListener;

/**
 * CheckBox UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DCheckBox extends JCheckBox {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -7442427064479562628L;

	/**
	 * ID of the CheckBox
	 */
	private String id;
	
	/**
	 * DCheckEvent to call when checked/unchecked
	 */
	private DCheckEvent event;
	
	/**
	 * ActionListener for getting button requests
	 */
	private DCheckListener listener;
	
	/**
	 * Initializes DCheckBox object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param event DCheckEvent to call when checked/unchecked
	 * @param id ID of the CheckBox
	 * @param selected Whether the CheckBox starts as selected
	 */
	public DCheckBox(
			BaseGUI base_gui,
			DCheckEvent event,
			String id,
			boolean selected) {
		this.id = id;
		this.event = event;
		//SET TEXT
		String ls = base_gui.get_language_string(id);
		setText(LanguageHandler.get_text(ls));
		String label = base_gui.get_language_string(id);
		int index = LanguageHandler.get_mnemonic_index(label);
		setDisplayedMnemonicIndex(index);
		label = LanguageHandler.get_text(label);
		setMnemonic(label.charAt(index));
		//SET FONT
		setFont(base_gui.get_font());
		//SET SELECTED
		setSelected(selected);
		//SET ACTION
		this.listener = new DCheckListener(base_gui, event, id);
		this.addItemListener(this.listener);
	}
	
	/**
	 * Sets the CheckBox to allow actions to be recorded,
	 * even while a process is running.
	 */
	public void always_allow_action() {
		this.removeItemListener(this.listener);
		this.listener = new DCheckListener(
				null, 
				this.event,
				this.id);
		this.addItemListener(this.listener);
	}
}
