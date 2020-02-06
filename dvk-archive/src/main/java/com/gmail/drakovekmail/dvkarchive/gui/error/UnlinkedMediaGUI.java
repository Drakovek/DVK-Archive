package com.gmail.drakovekmail.dvkarchive.gui.error;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.SimpleServiceGUI;

/**
 * GUI for getting unlinked media.
 * 
 * @author Drakovek
 */
public class UnlinkedMediaGUI extends SimpleServiceGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 7109589706058802015L;

	/**
	 * Initializes the UnlinkedMediaGUI object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 */
	public UnlinkedMediaGUI(BaseGUI base_gui) {
		super(base_gui, "unlinked_title", "unlinked_desc");
	}
}
