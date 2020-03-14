package com.gmail.drakovekmail.dvkarchive.file;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * Class for handling the start of the DVK Archive program.
 * 
 * @author Drakovek
 */
public class Start {
	
	/**
	 * Starts the DVK Archive
	 * 
	 * @param args Not used
	 */
	public static void main(String[] args) {
		start();
	}
	
	/**
	 * Starts the DVK Archive program by initializing the GUI.
	 */
	@SuppressWarnings("unused")
	public static void start() {
		BaseGUI base_gui = new BaseGUI();
		//SET ANTIALIASING
		if(base_gui.use_aa()) {
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
		}
		else {
			System.setProperty("awt.useSystemAAFontSettings", "off");
			System.setProperty("swing.aatext", "false");
		}
		new StartGUI(base_gui, true);
	}
}
