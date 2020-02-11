package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JTextArea;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * TextArea UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DTextArea extends JTextArea {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 7497768689408525699L;
	
	/**
	 * BaseGUI for getting UI settings
	 */
	BaseGUI base_gui;
	
	/**
	 * Initializes the DTextArea class.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 */
	public DTextArea(BaseGUI base_gui) {
		this.base_gui = base_gui;
		this.setFont(base_gui.get_font());
		this.setEditable(false);
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
	}
	
	/**
	 * Appends text to the DTextArea.
	 * 
	 * @param text Text to append.
	 * @param is_id Whether text is a language ID
	 */
	public void append_text(String text, boolean is_id) {
		String cur_text = this.getText();
		if(text.length() > 0
				|| (cur_text.length() > 0
						&& !cur_text.endsWith("\n\n"))) {
			String append = text;
			if(is_id) {
				append = this.base_gui.get_language_string(text);
			}
			this.append(append + "\n");
		}
	}
}
