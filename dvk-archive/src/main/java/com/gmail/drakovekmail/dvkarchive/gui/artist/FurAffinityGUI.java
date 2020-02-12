package com.gmail.drakovekmail.dvkarchive.gui.artist;

import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * GUI for downloading files from FurAffinity.net
 * 
 * @author Drakovek
 */
public class FurAffinityGUI extends ArtistHostingGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 8096375680530307994L;

	/**
	 * Initializes the FurAffinityGUI object.
	 * 
	 * @param start_gui Parent of FurAffinityGUI
	 */
	public FurAffinityGUI(StartGUI start_gui) {
		super(start_gui, "fur_affinity");
		create_login_gui();
	}
}
