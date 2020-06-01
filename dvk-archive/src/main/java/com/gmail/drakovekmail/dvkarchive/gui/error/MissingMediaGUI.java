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
		try(DvkHandler dvk_handler = new DvkHandler(this.start_gui.get_file_prefs())) {
			this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
			File[] dirs = {this.start_gui.get_directory()};
			dvk_handler.read_dvks(dirs, this.start_gui);
			this.start_gui.append_console("", false);
			this.start_gui.append_console("missing_media_console", true);
			this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
			ErrorFinding.get_missing_media_dvks(dvk_handler, this.start_gui);
		}
		catch(DvkException e) {}
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
