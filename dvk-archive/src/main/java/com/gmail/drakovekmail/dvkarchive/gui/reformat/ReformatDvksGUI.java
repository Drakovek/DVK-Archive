package com.gmail.drakovekmail.dvkarchive.gui.reformat;

import java.io.File;

import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.gui.SimpleServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.reformat.Reformat;

/**
 * GUI for reformatting DVK files.
 * 
 * @author Drakovek
 */
public class ReformatDvksGUI extends SimpleServiceGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -2453881274943469647L;

	/**
	 * Initializes the ReformatDvksGUI object.
	 * 
	 * @param start_gui Parent of the ReformatDvksGUI
	 */
	public ReformatDvksGUI(StartGUI start_gui) {
		super(start_gui, "reformat_title", "reformat_desc");
	}
	
	/**
	 * Runs process to get reformat DVKs.
	 * Displays results in the start GUI.
	 */
	@Override
	public void run_process() {
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		File[] dirs = {get_start_gui().get_directory()};
		FilePrefs prefs = get_start_gui().get_file_prefs();
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, get_start_gui())) {
			get_start_gui().append_console("", false);
			get_start_gui().append_console("reformat_console", true);
			Reformat.reformat_dvks(dvk_handler, get_start_gui());
		}
		catch(DvkException e) {}
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
