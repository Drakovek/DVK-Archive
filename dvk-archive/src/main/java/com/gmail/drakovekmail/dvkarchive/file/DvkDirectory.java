package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for handling all DVK files in a given directory.
 * 
 * @author Drakovek
 */
public class DvkDirectory implements Serializable {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -1105974706051666475L;

	/**
	 * ArrayList of loaded Dvk objects.
	 */
	ArrayList<Dvk> dvks;
	
	/**
	 * The currently loaded directory.
	 */
	private File dir;
	
	/**
	 * Initializes DvkDirectory to have an empty Dvk list.
	 */
	public DvkDirectory() {
		this.dvks = new ArrayList<>();
	}
	
	/**
	 * Loads all the DVK files in a given directory to the dvks list.
	 * 
	 * @param directory Directory from which to load DVK files
	 */
	public void read_dvks(final File directory) {
		this.dir = directory;
		this.dvks = new ArrayList<>();
		if(this.dir != null && this.dir.isDirectory()) {
			File[] files = this.dir.listFiles();
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
	
	/**
	 * Returns whether the DvkDirectory contains a given DVK file.
	 * 
	 * @param file DVK file to search for
	 * @return Whether DvkDirectory contains file
	 */
	public boolean contains_dvk_file(File file) {
		for(Dvk dvk: this.dvks) {
			if(dvk.get_dvk_file().equals(file)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Updates the DvkDirectory to reflect the current state of the directory.
	 * Mainly used to update objects when loaded from an index file.
	 * 
	 * @param modified Last modified date to compare files against
	 */
	public void update_directory(long modified) {
		for(int i = 0; i < this.dvks.size(); i++) {
			Dvk dvk = this.dvks.get(i);
			//REMOVE DVK IF NON-EXISTANT
			if(dvk == null || !dvk.get_dvk_file().exists()) {
				this.dvks.remove(i);
				i--;
			}
			else if(modified < dvk.get_dvk_file().lastModified()) {
				//UPDATES DVK THAT HAVE BEEN EDITED
				Dvk update = new Dvk(dvk.get_dvk_file());
				this.dvks.set(i, update);
			}
		}
		//ADD NEW DVKS
		if(this.dir != null && this.dir.isDirectory()) {
			File[] files = this.dir.listFiles();
			for(File file: files) {
				if(file.getName().endsWith(".dvk")) {
					if(!contains_dvk_file(file)) {
						Dvk dvk = new Dvk(file);
						if(dvk.get_title() != null) {
							this.dvks.add(dvk);
						}
					}
				}
			}
		}
	}
}
