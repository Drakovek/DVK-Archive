package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.BorderLayout;
import com.gmail.drakovekmail.dvkarchive.gui.settings.SettingsBarGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DFrame;

/**
 * Class dealing with the main GUI.
 * Starts with the DVK Archive program.
 * 
 * @author Drakovek
 *
 */
public class StartGUI {
	
	/**
	 * Creates the Start GUI.
	 */
	public StartGUI() {
		BaseGUI base_gui = new BaseGUI();
		DFrame frame = new DFrame(base_gui, "dvk_archive");
		frame.getContentPane().add(new SettingsBarGUI(base_gui), BorderLayout.SOUTH);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
