package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Dimension;
import javax.swing.JFrame;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Frame UI object for DVK Archive.
 * @author drakovek
 *
 */
public class DFrame extends JFrame {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 5034873869653858091L;

	/**
	 * Initializes the DFrame object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param title Frame title
	 */
	public DFrame(BaseGUI base_gui, String title) {
		super(base_gui.get_language_string(title));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(200, 200));
	}
}
