package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;

import com.gmail.drakovekmail.dvkarchive.file.DvkException;
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
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		File[] dirs = {get_start_gui().get_directory()};
		get_start_gui().append_console("", false);
		get_start_gui().append_console("unlinked_console", true);
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		try(DvkHandler dvk_handler = new DvkHandler(get_start_gui().get_file_prefs(), dirs, null)) {
			ErrorFinding.get_unlinked_media( dvk_handler, dirs, get_start_gui());
		}
		catch(DvkException e) {}
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
