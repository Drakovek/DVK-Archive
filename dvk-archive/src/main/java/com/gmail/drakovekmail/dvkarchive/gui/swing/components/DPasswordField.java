package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JPasswordField;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Password field UI object for DVK Archive
 * 
 * @author Drakovek
 */
public class DPasswordField extends JPasswordField{
	
	//TODO ADD INSETS
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
		this.setFont(base_gui.get_font());
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
