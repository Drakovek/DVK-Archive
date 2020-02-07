package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Component;

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
	private static final long serialVersionUID = -6373707390656666673L;
	
	/**
	 * BaseGUI for getting UI settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * Initializes the DLabel object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param comp Component linked to the DLabel
	 * @param id Label text ID
	 */
	public DLabel(BaseGUI base_gui, Component comp, String id) {
		this.base_gui = base_gui;
		set_text_id(id);
		this.setFont(base_gui.get_font());
		if(comp != null) {
			String label = base_gui.get_language_string(id);
			int index = LanguageHandler.get_mnemonic_index(label);
			this.setDisplayedMnemonicIndex(index);
			label = LanguageHandler.get_text(label);
			this.setDisplayedMnemonic(label.charAt(index));
			this.setLabelFor(comp);
		}
	}
	
	/**
	 * Sets the text of the label based on a Language ID.
	 * 
	 * @param id Language ID
	 */
	public void set_text_id(String id) {
		setText(LanguageHandler.get_text(this.base_gui.get_language_string(id)));
	}
	
	/**
	 * Wraps the text of the DLabel.
	 * 
	 * @param center Whether to center the text.
	 */
	public void wrap_text(boolean center) {
		StringBuilder builder = new StringBuilder();
		builder.append("<html>");
		if(center) {
			builder.append("<center>");
		}
		builder.append(this.getText());
		if(center) {
			builder.append("</center>");
		}
		builder.append("</html>");
		this.setText(builder.toString());
	}
}
