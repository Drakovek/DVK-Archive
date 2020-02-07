package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JMenu;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;

/**
 * Menu UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DMenu extends JMenu {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -5976338253587090427L;

	/**
	 * Initializes the DMenu object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param id Label text ID
	 */
	public DMenu(BaseGUI base_gui, String id) {
		//SET LABEL AND MNEMONICS
		super(LanguageHandler.get_text(base_gui.get_language_string(id)));
		String label = base_gui.get_language_string(id);
		int index = LanguageHandler.get_mnemonic_index(label);
		this.setDisplayedMnemonicIndex(index);
		label = LanguageHandler.get_text(label);
		this.setMnemonic(label.charAt(index));
		//SET FONT
		this.setFont(base_gui.get_font());
	}
}
