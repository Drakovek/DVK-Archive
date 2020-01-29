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
	 * Returns the number of Dvk objects loaded.
	 * 
	 * @return Size of dvks list.
	 */
	public int get_size() {
		return this.dvks.size();
	}
	
	/**
	 * Returns the Dvk located at a given index in the dvks list.
	 * 
	 * @param index Dvk index
	 * @return Dvk at index
	 */
	public Dvk get_dvk(final int index) {
		return this.dvks.get(index);
	}
}
