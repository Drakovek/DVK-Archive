package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JMenuItem;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionListener;

/**
 * MenuItem UI object for DVKArchive.
 * 
 * @author Drakovek
 */
public class DMenuItem extends JMenuItem {
	
	/**
	 * SerialVerionUID
	 */
	private static final long serialVersionUID = 2820703107830083389L;

	/**
	 * Initializes the DMenuItem object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param event DActionEvent to call when action occurs
	 * @param id ID for the Button Label
	 */
	public DMenuItem(BaseGUI base_gui, DActionEvent event, String id) {
		//SET LABEL AND MNEMONICS
		super(LanguageHandler.get_text(base_gui.get_language_string(id)));
		String label = base_gui.get_language_string(id);
		int index = LanguageHandler.get_mnemonic_index(label);
		this.setDisplayedMnemonicIndex(index);
		label = LanguageHandler.get_text(label);
		this.setMnemonic(label.charAt(index));
		//SET FONT
		this.setFont(base_gui.get_font());
		//SET ACTION
		this.addActionListener(new DActionListener(base_gui, event, id));
	}
}
