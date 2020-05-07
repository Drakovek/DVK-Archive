package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;

/**
 * Class containing methods for handling many Dvk objects.
 * 
 * @author Drakovek
 */
public class DvkHandler {
	
	/**
	 * ArrayList of all loaded Dvk objects.
	 */
	private ArrayList<Dvk> dvks;
	
	/**
	 * Initializes DvkHandler with empty Dvk list.
	 */
	public DvkHandler() {
		this.dvks = new ArrayList<>();
	}
	
	/**
	 * Loads all the DVK files within the given directories.
	 * Includes sub-directories.
	 * 
	 * @param dirs Directories in which to search for files
	 * @param prefs FilePrefs for getting index directory
	 * @param start_gui Used for displaying progress.
	 * @param use_index Whether to load from index files
	 * @param check_new Whether to update list when loading from index
	 * @param save_index Whether to save index files
	 */
	public void read_dvks(
			final File[] dirs,
			FilePrefs prefs,
			StartGUI start_gui,
			boolean use_index,
			boolean check_new,
			boolean save_index) {
		if(start_gui != null) {
			start_gui.append_console("", false);
			start_gui.append_console("reading_dvks", true);
			start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		}
		this.dvks = new ArrayList<>();
		DvkIndexing index = new DvkIndexing(prefs.get_index_dir());
		File[] dvk_dirs = get_directories(dirs);
		int max = dvk_dirs.length;
		for(int i = 0; i < max; i++) {
			//SHOW PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(
						false, true, i, max);
				//BREAK IF CANCELLED
				if(start_gui.get_base_gui().is_canceled()) {
					break;
				}
			}
			//ADD DIRECTORY
			DvkDirectory dvkd = index.get_dvk_directory(
					dvk_dirs[i], use_index, check_new, save_index);
			this.dvks.addAll(dvkd.get_dvks());
			dvkd = null;
		}
		index.write_index_list();
	}
	
	/**
	 * Adds a given Dvk to the list of Dvk objects.
	 * 
	 * @param dvk Given Dvk object
	 */
	public void add_dvk(Dvk dvk) {
		this.dvks.add(dvk);
	}
	
	/**
	 * Sets the Dvk object at a given index.
	 * 
	 * @param dvk Given Dvk object
	 * @param index Index of Dvk to replace
	 */
	public void set_dvk(Dvk dvk, int index) {
		this.dvks.set(index, dvk);
	}
	
	/**
	 * Returns the number of Dvk objects loaded.
	 * 
	 * @return Size of dvks list
	 */
	public int get_size() {
		return this.dvks.size();
	}
	
	/**
	 * Returns the Dvk at a given index of the dvks list.
	 * 
	 * @param index Index of Dvk
	 * @return Dvk object
	 */
	public Dvk get_dvk(final int index) {
		return this.dvks.get(index);
	}
	
	/**
	 * Returns an array of sub-directories within a given directory.
	 * Only lists directories that contain DVK files.
	 * 
	 * @param dir Directory to search
	 * @return Sub-directories of dir containing DVK files
	 */
	public static File[] get_directories(final File dir) {
		File[] dirs = {dir};
		return get_directories(dirs);
	}
	
	/**
	 * Returns an array of sub-directories within given directories.
	 * Only lists directories that contain DVK files.
	 * 
	 * @param dirs Directories to search
	 * @return Sub-directories of dirs containing DVK files
	 */
	public static File[] get_directories(final File[] dirs) {
		if(dirs == null) {
			return new File[0];
		}
		File[] files;
		ArrayList<File> all_dirs = new ArrayList<>();
		ArrayList<String> dvk_dirs = new ArrayList<>();
		for(File file: dirs) {
			if(file != null && file.exists()) {
				all_dirs.add(file);
			}
		}
		while(all_dirs.size() > 0) {
			//TODO NULL POINTER?
			//SEARCH FOR DVKS AND NEW DIRECTORIES
			boolean add_dirs = false;
			files = all_dirs.get(0).listFiles();
			for(File file: files) {
				if(file.isDirectory()) {
					all_dirs.add(file);
				}
				else if(file.getName().toLowerCase().endsWith(".dvk")) {
					add_dirs = true;
				}
			}
			//ADD DIRECTORY TO LIST IF CONTAINS DVK
			if(add_dirs) {
				dvk_dirs.add(all_dirs.get(0).getAbsolutePath());
			}
			all_dirs.remove(0);
		}
		//REMOVE DUPLICATE DIRECTORIES AND CONVERT TO FILE
		dvk_dirs = ArrayProcessing.clean_list(dvk_dirs);
		Collections.sort(dvk_dirs);
		File[] end_dirs = new File[dvk_dirs.size()];
		for(int i = 0; i < end_dirs.length; i++) {
			end_dirs[i] = new File(dvk_dirs.get(i));
		}
		return end_dirs;
	}
	
	/**
	 * Sorts all Dvks alpha-numerically by title.
	 * 
	 * @param group_artists Whether to group Dvks of the same artist
	 * @param reverse Whether to reverse the sorting order
	 */
	public void sort_dvks_title(
			final boolean group_artists, 
			final boolean reverse) {
		DvkCompare compare = new DvkCompare();
		compare.set_parameters(DvkCompare.TITLE,
				group_artists,
				reverse);
		Collections.sort(this.dvks, compare);
	}
	
	/**
	 * Sorts all Dvks by time published.
	 * 
	 * @param group_artists Whether to group Dvks of the same artist
	 * @param reverse Whether to reverse the sorting order
	 */
	public void sort_dvks_time(
			final boolean group_artists, 
			final boolean reverse) {
		DvkCompare compare = new DvkCompare();
		compare.set_parameters(DvkCompare.TIME,
				group_artists,
				reverse);
		Collections.sort(this.dvks, compare);
	}
	
	/**
	 * Returns whether any loaded Dvk is linked to given file.
	 * 
	 * @param file Given file
	 * @return Whether any Dvks link to the given file
	 */
	public boolean contains_file(File file) {
		int size = get_size();
		for(int i = 0; i < size; i++) {
			File media = get_dvk(i).get_media_file();
			if(file.equals(media)) {
				return true;
			}
			File second = get_dvk(i).get_secondary_file();
			if(second != null && file.equals(second)) {
				return true;
			}
		}
		return false;
	}
}
