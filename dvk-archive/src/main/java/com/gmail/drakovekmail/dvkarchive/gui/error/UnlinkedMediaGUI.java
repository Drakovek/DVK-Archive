package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;
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
	private static final long serialVersionUID = 7675526073648299168L;

	/**
	 * Initializes the UnlinkedMediaGUI object.
	 * 
	 * @param start_gui Parent of the UnlinkedMediaGUI
	 */
	public UnlinkedMediaGUI(StartGUI start_gui) {
		super(start_gui, "unlinked_title", "unlinked_desc");
	}

	/**
	 * Runs process to get unlinked media.
	 * Displays results in the start GUI.
	 */
	@Override
	public void run_process() {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		this.start_gui.append_console("", false);
		this.start_gui.append_console("unlinked_console", true);
		File[] dirs = {this.start_gui.get_directory()};
		ErrorFinding.get_unlinked_media(this.start_gui.get_file_prefs(), dirs, this.start_gui);
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
