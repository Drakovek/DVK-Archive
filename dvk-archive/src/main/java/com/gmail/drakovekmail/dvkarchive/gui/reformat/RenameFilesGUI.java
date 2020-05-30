package com.gmail.drakovekmail.dvkarchive.gui.reformat;

import java.io.File;

import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.gui.SimpleServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.reformat.Reformat;

/**
 * GUI for renaming DVKs and their associated media.
 * 
 * @author Drakovek
 */
public class RenameFilesGUI extends SimpleServiceGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -5031152738081385825L;

	/**
	 * Initializes the RenameFilesGUI object.
	 * 
	 * @param start_gui Parent of the RenameFilesGUI
	 */
	public RenameFilesGUI(StartGUI start_gui) {
		super(start_gui, "rename_title", "rename_desc");
	}
	
	/**
	 * Runs process to rename DVKs and associated media.
	 * Displays results in the start GUI.
	 */
	@Override
	public void run_process() {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		File[] dirs = {this.start_gui.get_directory()};
		FilePrefs prefs = this.start_gui.get_file_prefs();
		try(DvkHandler dvk_handler = new DvkHandler(prefs)) {
			dvk_handler.read_dvks(dirs, this.start_gui);
			this.start_gui.append_console("", false);
			this.start_gui.append_console("rename_console", true);
			Reformat.rename_files(dvk_handler, this.start_gui);
		}
		catch(DvkException e) {}
	}

	@Override
	public void directory_opened() {}

	@Override
	public void close() {}
}
