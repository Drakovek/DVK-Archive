package com.gmail.drakovekmail.dvkarchive.gui.swing;

import javax.swing.JLabel;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;

/**
 * Label UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DLabel extends JLabel {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -3466955131579718614L;

	/**
	 * Initializes the DLabel object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param label Label text
	 */
	public DLabel(BaseGUI base_gui, String label) {
		super(LanguageHandler.get_text(base_gui.get_language_string(label)));
		this.setFont(base_gui.get_font());
	}
}
