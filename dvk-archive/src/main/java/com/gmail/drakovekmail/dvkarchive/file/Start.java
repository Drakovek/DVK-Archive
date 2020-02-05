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
	 * Starts the DVK Archive program by initializing the GUI.
	 * 
	 * @param args Not Used
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		BaseGUI base_gui = new BaseGUI();
		new StartGUI(base_gui);
	}
}
