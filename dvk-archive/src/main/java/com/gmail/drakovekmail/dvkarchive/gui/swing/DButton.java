package com.gmail.drakovekmail.dvkarchive.gui.swing;

import javax.swing.JButton;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;

/**
 * Button UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DButton extends JButton {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 732391822296443001L;

	/**
	 * Initializes the DButton object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param label Button Label
	 */
	public DButton(BaseGUI base_gui, String label) {
		super(LanguageHandler.get_text(base_gui.get_language_string(label)));
		this.setFont(base_gui.get_font());
	}
}
