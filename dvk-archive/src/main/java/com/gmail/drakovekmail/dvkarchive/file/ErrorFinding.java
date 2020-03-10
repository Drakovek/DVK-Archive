package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * Class containing methods for finding errors in DVK files.
 * 
 * @author Drakovek
 */
public class ErrorFinding {
	
	/**
	 * Returns list of files not linked to a DVK file.
	 * Only returns files in the same directories as DVK files.
	 * 
	 * @param prefs FilePrefs for getting index settings
	 * @param directories Directories in which to search for files
	 * @param start_gui Used for displaying progress and results
	 * @return List of unlinked files
	 */
	public static ArrayList<File> get_unlinked_media(
			FilePrefs prefs,
			File[] directories,
			StartGUI start_gui) {
		DvkHandler handler = new DvkHandler();
		File[] dirs = new File[0];
		if(start_gui == null || !start_gui.get_base_gui().is_canceled()) {
			dirs = DvkHandler.get_directories(directories);
		}
		ArrayList<File> missing = new ArrayList<>();
		for(int i = 0; i < dirs.length; i++) {
			//BREAK IF CANCELLED
			if(start_gui != null 
					&& start_gui.get_base_gui()
						.is_canceled()) {
				break;
			}
			//UPDATE PROGRESS
			if(start_gui != null) {
				
				start_gui.get_main_pbar()
					.set_progress(false, true, i, dirs.length);
			}
			//LOAD DIRECTORY
			File[] files = {dirs[i]};
			handler.read_dvks(files, prefs, null, prefs.use_index(), true, prefs.use_index());
			files = dirs[i].listFiles();
			Arrays.sort(files);
			for(File file: files) {
				//CHECK IF FILE IS MISSING
				if(!file.isDirectory()
						&& !file.getName().endsWith(".dvk")
						&& !missing.contains(file)
						&& !handler.contains_file(file)) {
					missing.add(file);
					//PRINT PATH
					if(start_gui != null) {
						start_gui.append_console(
								file.getAbsolutePath(),
								false);
					}
				}
			}
		}
		return missing;
	}
}
