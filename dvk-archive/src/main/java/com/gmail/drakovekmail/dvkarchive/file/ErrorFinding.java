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
	 * @param handler DvkHandler with Dvk to check links
	 * @param directories Directories in which to search for files
	 * @param start_gui Used for displaying progress and results
	 * @return List of unlinked files
	 */
	public static ArrayList<File> get_unlinked_media(
			DvkHandler handler,
			File[] directories,
			StartGUI start_gui) {
		//FIND ALL MEDIA FILES
		File[] dirs = DvkHandler.get_directories(directories);
		ArrayList<File> missing = new ArrayList<>();
		for(File dir: dirs) {
			File[] files = dir.listFiles();
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
