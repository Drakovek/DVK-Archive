package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for handling all DVK files in a given directory.
 * 
 * @author Drakovek
 */
public class DvkDirectory {
	
	/**
	 * ArrayList of loaded Dvk objects.
	 */
	ArrayList<Dvk> dvks;
	
	/**
	 * Initializes DvkDirectory to have an empty Dvk list.
	 */
	public DvkDirectory() {
		this.dvks = new ArrayList<>();
	}
	
	/**
	 * Loads all the DVK files in a given directory to the dvks list.
	 * 
	 * @param dir Directory from which to load DVK files
	 */
	public void read_dvks(final File dir) {
		this.dvks = new ArrayList<>();
		if(dir != null && dir.isDirectory()) {
			File[] files = dir.listFiles();
			Arrays.parallelSort(files);
			for(File file: files) {
				if(file.getName().endsWith(".dvk")) {
					Dvk dvk = new Dvk(file);
					if(dvk.get_title() != null) {
						this.dvks.add(dvk);
					}
				}
			}
		}
	}
	
	/**
	 * Returns ArrayList of loaded Dvk objects.
	 * 
	 * @return ArrayList of Dvk objects
	 */
	public ArrayList<Dvk> get_dvks() {
		return this.dvks;
	}
}
