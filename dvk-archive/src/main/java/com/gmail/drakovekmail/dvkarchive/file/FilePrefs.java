package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Class for getting settings related to file operations.
 * 
 * @author Drakovek
 */
public class FilePrefs {
	
	/**
	 * Key for the "index dir" setting
	 */
	private static final String INDEX_DIR = "index_dir";
	
	/**
	 * Directory in which to store DvkDirectory index files
	 */
	private File index_dir;
	
	/**
	 * Initializes FilePrefs by loading preferences.
	 */
	public FilePrefs() {
		load_preferences();
	}
	
	/**
	 * Loads preferences.
	 */
	public void load_preferences() {
		Preferences prefs = Preferences.userNodeForPackage(FilePrefs.class);
		//GET INDEX DIR
		String path = prefs.get(INDEX_DIR, "");
		File file = new File(path);
		if(!file.exists()) {
			file = get_default_directory();
		}
		set_index_dir(file);
	}
	
	/**
	 * Returns a default directory for storing data.
	 * Should be in the same folder as FilePrefs class or .jar package.
	 * 
	 * @return Default directory
	 */
	public static File get_default_directory() {
		try {
			File file = new File(
					FilePrefs.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			while(file != null && file.exists() && !file.isDirectory()) {
				file = file.getParentFile();
			}
			file = new File(file, "dvk-data");
			if(!file.isDirectory()) {
				file.mkdir();
			}
			return file;
		}
		catch (Exception e) {
			return null;
        }
	}
	
	/**
	 * Saves preferences.
	 */
	public void save_preferences() {
		Preferences prefs = Preferences.userNodeForPackage(FilePrefs.class);
		//GET INDEX DIR
		if(get_index_dir().exists() && !get_index_dir().getName().equals("")) {
			prefs.put(INDEX_DIR, get_index_dir().getAbsolutePath());
		}
		else {
			prefs.put(INDEX_DIR, "");
		}
	}
	
	/**
	 * Sets the index directory.
	 * 
	 * @param dir Index directory
	 */
	public void set_index_dir(File dir) {
		if(dir != null) {
			this.index_dir = dir;
		}
		else {
			this.index_dir = new File("");
		}
	}
	
	/**
	 * Returns the index directory.
	 * 
	 * @return Returns the index directory
	 */
	public File get_index_dir() {
		return this.index_dir;
	}
}
