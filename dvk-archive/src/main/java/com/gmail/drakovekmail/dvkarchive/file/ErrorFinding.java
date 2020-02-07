package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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
	 * @param directories Directories in which to search for files.
	 * @return List of unlinked files
	 */
	public static ArrayList<File> get_unlinked_media(
			DvkHandler handler, File[] directories) {
		//FIND ALL MEDIA FILES
		File[] dirs = DvkHandler.get_directories(directories);
		ArrayList<File> missing = new ArrayList<>();
		for(File dir: dirs) {
			File[] files = dir.listFiles();
			for(File file: files) {
				if(!file.isDirectory()
						&& !file.getName().endsWith(".dvk")
						&& !missing.contains(file)) {
					missing.add(file);
				}
			}
		}
		//REMOVE LINKED FILES
		int size = handler.get_size();
		for(int i = 0; i < size; i++) {
			//CHECK FOR MEDIA FILE
			Dvk dvk = handler.get_dvk(i);
			File media = dvk.get_media_file();
			int index = missing.indexOf(media);
			if(index != -1) {
				missing.remove(index);
			}
			//CHECK FOR SECONDARY MEDIA FILE
			media = dvk.get_secondary_file();
			if(media != null) {
				index = missing.indexOf(media);
				if(index != -1) {
					missing.remove(index);
				}
			}
		}
		Collections.sort(missing);
		return missing;
	}
}
