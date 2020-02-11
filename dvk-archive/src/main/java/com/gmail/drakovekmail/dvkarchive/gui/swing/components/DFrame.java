package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Dimension;
import javax.swing.JFrame;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DCloseListener;

/**
 * Frame UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DFrame extends JFrame implements DActionEvent {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -7361405164773931119L;

	/**
	 * Initializes the DFrame object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param title Frame title
	 */
	public DFrame(BaseGUI base_gui, String title) {
		super(base_gui.get_language_string(title));
		this.setMinimumSize(new Dimension(200, 200));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new DCloseListener(base_gui, this, "close"));
	}

	@Override
	public void event(String id) {
		this.dispose();
	}
}
