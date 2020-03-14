package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JButton;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionListener;

/**
 * Button UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DButton extends JButton {
	
	//TODO ADD INSETS
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 6748579195004292152L;

	/**
	 * ID for the button.
	 */
	private String id;
	
	/**
	 * DActionEvent to call when action occurs
	 */
	private DActionEvent event;
	
	/**
	 * BaseGUI for getting UI settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * ActionListener for getting button requests
	 */
	private DActionListener listener;

	/**
	 * Initializes the DButton object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param event DActionEvent to call when action occurs
	 * @param id ID for the button
	 */
	public DButton(BaseGUI base_gui, DActionEvent event, String id) {
		this.base_gui = base_gui;
		this.event = event;
		this.id = id;
		//SET LABEL AND MNEMONICS
		set_text_id(id);
		//SET FONT
		this.setFont(base_gui.get_font());
		//SET ACTION
		this.listener = new DActionListener(base_gui, event, id);
		this.addActionListener(this.listener);
	}
	
	/**
	 * Sets the text for the button using a language ID.
	 * Also sets button mnemonic.
	 * 
	 * @param id LanguageID
	 */
	public void set_text_id(String id) {
		String ls = this.base_gui.get_language_string(id);
		this.setText(LanguageHandler.get_text(ls));
		String label = this.base_gui.get_language_string(id);
		int index = LanguageHandler.get_mnemonic_index(label);
		this.setDisplayedMnemonicIndex(index);
		label = LanguageHandler.get_text(label);
		this.setMnemonic(label.charAt(index));
	}
	
	/**
	 * Sets the button to allow actions to be recorded,
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
