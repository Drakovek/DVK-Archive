package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;
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
	 * Used to get Dvk information
	 */
	private DvkHandler dvk_handler;

	/**
	 * Initializes the SameIDsGUI object.
	 * 
	 * @param start_gui Parent of the SameIDsGUI
	 */
	public SameIDsGUI(StartGUI start_gui) {
		super(start_gui, "same_ids_title", "same_ids_desc");
		this.dvk_handler = new DvkHandler(start_gui.get_file_prefs());
	}
	
	/**
	 * Runs process to get DVKs with same IDs.
	 * Displays results in the start GUI.
	 */
	@Override
	public void run_process() {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		File[] dirs = {this.start_gui.get_directory()};
		this.dvk_handler.read_dvks(dirs, this.start_gui);
		this.start_gui.append_console("", false);
		this.start_gui.append_console("same_ids_console", true);
		ErrorFinding.get_same_ids(this.dvk_handler, this.start_gui);
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {
		this.dvk_handler.close_connection();
	}

}
