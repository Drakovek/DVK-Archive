package com.gmail.drakovekmail.dvkarchive.gui.error;

import java.io.File;
import com.gmail.drakovekmail.dvkarchive.file.ErrorFinding;
import com.gmail.drakovekmail.dvkarchive.gui.SimpleServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.work.DSwingWorker;

/**
 * GUI for getting unlinked media.
 * 
 * @author Drakovek
 */
public class UnlinkedMediaGUI extends SimpleServiceGUI {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -5777741199565492675L;

	/**
	 * Initializes the UnlinkedMediaGUI object.
	 * 
	 * @param start_gui Parent of the UnlinkedMediaGUI
	 */
	public UnlinkedMediaGUI(StartGUI start_gui) {
		super(start_gui, "unlinked_title", "unlinked_desc");
	}

	/**
	 * Starts SwingWorker for the unlinked media process.
	 */
	private void start_get_unlinked() {
		this.sw = new DSwingWorker(this, "unlinked");
		this.sw.execute();
	}

	/**
	 * Runs process to get unlinked media.
	 * Displays results in the start GUI.
	 */
	public void get_unlinked() {
		this.start_gui.get_progress_bar().set_progress(true, false, 0, 0);
		this.start_gui.append_console("", false);
		this.start_gui.append_console("unlinked_console", true);
		File[] dirs = {this.start_gui.get_directory()};
		ErrorFinding.get_unlinked_media(this.dvk_handler, dirs, this.start_gui);
	}

	@Override
	public void run(String id) {
		switch(id) {
			case "read_dvks":
				read_dvks();
				break;
			case "unlinked":
				get_unlinked();
				break;
		}
	}

	@Override
	public void done(String id) {
		switch(id) {
			case "read_dvks":
				this.start_gui.get_progress_bar()
					.set_progress(true, false, 0, 0);
				start_get_unlinked();
				break;
			case "unlinked":
				this.start_gui.get_progress_bar()
					.set_progress(false, false, 0, 0);
				this.start_gui.append_console("finished", true);
				break;
		}
	}
}
