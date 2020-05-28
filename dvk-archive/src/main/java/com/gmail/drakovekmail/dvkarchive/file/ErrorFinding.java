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
		DvkHandler handler = new DvkHandler(prefs);
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
				
				start_gui.get_main_pbar().set_progress(false, true, i, dirs.length);
			}
			//LOAD DIRECTORY
			File[] files = {dirs[i]};
			handler.read_dvks(files, null);
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
		handler.close_connection();
		return missing;
	}
	
	/**
	 * Returns list of Dvks missing their associated media file(s).
	 * 
	 * @param dvk_handler Contains Dvk objects to check
	 * @param start_gui Used to show progress if not null
	 * @return Dvks with missing primary or secondary media
	 */
	public static ArrayList<File> get_missing_media_dvks(
			DvkHandler dvk_handler,
			StartGUI start_gui) {
		int size = dvk_handler.get_size();
		ArrayList<File> files = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			//UPDATE PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, i, size);
				//BREAK IF CANCELED
				if(start_gui.get_base_gui().is_canceled()) {
					break;
				}
			}
			Dvk dvk = dvk_handler.get_dvk(i);
			if(!dvk.get_media_file().exists()
					|| (dvk.get_secondary_file() != null 
					&& !dvk.get_secondary_file().exists())) {
				files.add(dvk.get_dvk_file());
				//PRINT
				if(start_gui != null) {
					start_gui.append_console(dvk.get_dvk_file().getAbsolutePath(), false);
				}
			}
		}
		return files;
	}
	
	/**
	 * Returns a list of Dvks that share the same IDs.
	 * 
	 * @param dvk_handler Contains Dvk objects to check
	 * @param start_gui Used to show progress if not null
	 * @return List of Dvks that have identical IDs
	 */
	public static ArrayList<File> get_same_ids(
			DvkHandler dvk_handler,
			StartGUI start_gui) {
		int size = dvk_handler.get_size();
		ArrayList<File> files = new ArrayList<>();
		ArrayList<Integer> indexes = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			//UPDATE PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, i, size);
				//ESCAPE IF CANCELLED
				if(start_gui.get_base_gui().is_canceled()) {
					break;
				}
			}
			String id1 = dvk_handler.get_dvk(i).get_id();
			for(int k = i + 1; k < size; k++) {
				//CHECK IF ID SAME AS FIRST ID
				Dvk dvk = dvk_handler.get_dvk(k);
				if(id1.equals(dvk.get_id()) 
						&& !indexes.contains(Integer.valueOf(k))) {
					//CHECK IF DVK ALREADY IN LIST
					if(!indexes.contains(Integer.valueOf(i))) {
						//ADD FIRST DVK
						files.add(dvk_handler.get_dvk(i).get_dvk_file());
						indexes.add(Integer.valueOf(i));
						//PRINT
						if(start_gui != null) {
							start_gui.append_console(
									dvk_handler
										.get_dvk(i)
										.get_dvk_file()
										.getAbsolutePath(), false);
						}
					}
					//ADD SECOND DVK
					files.add(dvk.get_dvk_file());
					indexes.add(Integer.valueOf(k));
					//PRINT
					if(start_gui != null) {
						start_gui.append_console(
								"    "
								+ dvk
									.get_dvk_file()
									.getAbsolutePath(), false);
					}
				}
			}
		}
		return files;
	}
}
