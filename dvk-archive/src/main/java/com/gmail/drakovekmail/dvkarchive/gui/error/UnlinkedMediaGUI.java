package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;
import java.util.ArrayList;

import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.ErrorFinding;
import com.gmail.drakovekmail.dvkarchive.gui.SimpleServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

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
	 * @param start_gui Parent of the UnlinkedMediaGUI
	 */
	public UnlinkedMediaGUI(StartGUI start_gui) {
		super(start_gui, "unlinked_title", "unlinked_desc");
	}

	/**
	 * Runs the unlinked media finding service.
	 */
	@Override
	public void run() {
		this.start_gui.append_console("", false);
		this.start_gui.append_console("unlinked_console", true);
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(this.start_gui.get_directory());
		File[] dirs = {this.start_gui.get_directory()};
		ArrayList<File> unlinked;
		unlinked = ErrorFinding.get_unlinked_media(handler, dirs);
		for(int i = 0; i < unlinked.size(); i++) {
			this.start_gui.append_console(unlinked.get(i).getAbsolutePath(), false);
		}
		this.start_gui.append_console("finished", true);
	}
}
