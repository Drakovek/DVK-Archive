package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;

import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.ErrorFinding;
import com.gmail.drakovekmail.dvkarchive.gui.SimpleServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * GUI for finding DVKs with missing associated media.
 * 
 * @author Drakovek
 */
public class MissingMediaGUI extends SimpleServiceGUI {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -4722287619279847888L;

	/**
	 * Initializes the MissingMediaGUI object.
	 * 
	 * @param start_gui Parent of the MissingMediaGUI
	 */
	public MissingMediaGUI(StartGUI start_gui) {
		super(start_gui, "missing_media_title", "missing_media_desc");
	}
	
	/**
	 * Runs process to get missing media DVKs.
	 * Displays results in the start GUI.
	 */
	@Override
	public void run_process() {
		File[] dirs = {get_start_gui().get_directory()};
		try(DvkHandler dvk_handler = new DvkHandler(get_start_gui().get_file_prefs(), dirs, get_start_gui())) {
			get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
			get_start_gui().append_console("", false);
			get_start_gui().append_console("missing_media_console", true);
			get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
			ErrorFinding.get_missing_media_dvks(dvk_handler, get_start_gui());
		}
		catch(DvkException e) {}
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
