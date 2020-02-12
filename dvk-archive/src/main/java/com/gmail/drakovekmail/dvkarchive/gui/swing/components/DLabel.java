package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Component;
import java.awt.Font;

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
	private static final long serialVersionUID = -5073421630112310014L;
	
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
		setText(LanguageHandler.get_text(
				this.base_gui.get_language_string(id)));
	}
	
	/**
	 * Sets the font to be larger than default.
	 */
	public void set_font_large() {
		Font font = this.base_gui.get_font();
		int size = font.getSize();
		size = (int)(size * 1.5);
		Font new_font = new Font(font.getFamily(),
				font.getStyle(), size);
		this.setFont(new_font);
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
