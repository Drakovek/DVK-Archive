package com.gmail.drakovekmail.dvkarchive.file;

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
	public static void start() {
		//SET ANTIALIASING
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
	}
}
