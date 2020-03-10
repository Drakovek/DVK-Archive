package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;

import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.ErrorFinding;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
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
	private void get_same_ids() {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		File[] dirs = {this.start_gui.get_directory()};
		FilePrefs prefs = this.start_gui.get_file_prefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, this.start_gui, prefs.use_index(), true, prefs.use_index());
		this.start_gui.append_console("", false);
		this.start_gui.append_console("same_ids_console", true);
		ErrorFinding.get_same_ids(handler, this.start_gui);
	}
	
	@Override
	public void run(String id) {
		switch(id) {
			case "run":
				get_same_ids();
				break;
		}
	}

	@Override
	public void done(String id) {
		this.start_gui.get_main_pbar()
			.set_progress(false, false, 0, 0);
		if(this.start_gui.get_base_gui().is_canceled()) {
			this.start_gui.append_console("canceled", true);
		}
		else {
			this.start_gui.append_console("finished", true);
		}
		this.start_gui.get_base_gui().set_running(false);
		this.start_gui.enable_all();
		enable_all();
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}

}
