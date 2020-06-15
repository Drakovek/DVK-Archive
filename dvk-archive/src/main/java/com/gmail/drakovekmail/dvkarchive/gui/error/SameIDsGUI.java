package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;

import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.ErrorFinding;
import com.gmail.drakovekmail.dvkarchive.gui.SimpleServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * GUI for finding DVK files with the same ID.
 * 
 * @author Drakovek
 */
public class SameIDsGUI extends SimpleServiceGUI {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 4030206997720178512L;

	/**
	 * Initializes the SameIDsGUI object.
	 * 
	 * @param start_gui Parent of the SameIDsGUI
	 */
	public SameIDsGUI(StartGUI start_gui) {
		super(start_gui, "same_ids_title", "same_ids_desc");
	}
	
	/**
	 * Runs process to get DVKs with same IDs.
	 * Displays results in the start GUI.
	 */
	@Override
	public void run_process() {
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		File[] dirs = {get_start_gui().get_directory()};
		try(DvkHandler dvk_handler = new DvkHandler(get_start_gui().get_file_prefs(), dirs, get_start_gui())) {
			get_start_gui().append_console("", false);
			get_start_gui().append_console("same_ids_console", true);
			get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
			ErrorFinding.get_same_ids(dvk_handler, get_start_gui());
		}
		catch(DvkException e) {}
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
