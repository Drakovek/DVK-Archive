package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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
	 * Loads all the DVK files within the given directory.
	 * Includes sub-directories.
	 * 
	 * @param dir Directory in which to search for files
	 */
	public void read_dvks(final File dir) {
		File[] dirs = {dir};
		read_dvks(dirs);
	}
	
	/**
	 * Loads all the DVK files within the given directories.
	 * Includes sub-directories.
	 * 
	 * @param dirs Directories in which to search for files
	 */
	public void read_dvks(final File[] dirs) {
		this.dvks = new ArrayList<>();
		File[] dvk_dirs = get_directories(dirs);
		for(File dir: dvk_dirs) {
			DvkDirectory dvkd = new DvkDirectory();
			dvkd.read_dvks(dir);
			this.dvks.addAll(dvkd.get_dvks());
			dvkd = null;
		}
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
}
