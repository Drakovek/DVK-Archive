package com.gmail.drakovekmail.dvkarchive.gui.reformat;

import java.io.File;
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
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		File[] dirs = {this.start_gui.get_directory()};
		FilePrefs prefs = this.start_gui.get_file_prefs();
		DvkHandler dvk_handler = new DvkHandler();
		dvk_handler.read_dvks(
				dirs,
				prefs,
				this.start_gui,
				prefs.use_index(),
				true,
				prefs.use_index());
		this.start_gui.append_console("", false);
		this.start_gui.append_console("reformat_console", true);
		Reformat.reformat_dvks(dvk_handler, this.start_gui);
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
