package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Insets;

import javax.swing.JPasswordField;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Password field UI object for DVK Archive
 * 
 * @author Drakovek
 */
public class DPasswordField extends JPasswordField{

	//TODO ADD ACTION

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -3491868892300994899L;

	/**
	 * Initializes the DPasswordField object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 */
	public DPasswordField(BaseGUI base_gui) {
		//SET FONT
		this.setFont(base_gui.get_font());
		//SET MARGINS
		int w = base_gui.get_space_size();
		int h = (w / 2);
		Insets ins = new Insets(h, w, h, w);
		setMargin(ins);
	}
	
	/**
	 * Gets the text from password field.
	 * WARNING: Dispose of password data as soon as possible.
	 * 
	 * @return Text entered into password field
	 */
	public String get_text() {
		StringBuilder builder = new StringBuilder();
		char[] pass = this.getPassword();
		for(int i = 0; i < pass.length; i++) {
			builder.append(pass[i]);
		}
		pass = null;
		return builder.toString();
	}
}
