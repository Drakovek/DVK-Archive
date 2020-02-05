package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * ScrollPane UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DScrollPane extends JScrollPane{
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -5417711048416825287L;

	/**
	 * Initializes the DScrollPane object.
	 * 
	 * @param view Component to use in the scroll pane
	 */
	public DScrollPane(JComponent view) {
		super(view);
	}
	
	/**
	 * Initializes the DScrollPane object.
	 * 
	 * @param view Component to use in the scroll pane
	 * @param hsbp Horizontal ScrollBar Policy
	 * @param vsbp Vertical ScrollBar Policy
	 */
	public DScrollPane(JComponent view, int hsbp, int vsbp) {
		super(view, vsbp, hsbp);
	}
}
