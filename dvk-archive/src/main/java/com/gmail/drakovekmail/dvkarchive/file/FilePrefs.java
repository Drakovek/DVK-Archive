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
	 * Key for the "use index" setting
	 */
	private static final String USE_INDEX = "use_index";
	
	/**
	 * Directory in which to store DvkDirectory index files.
	 */
	private File index_dir;
	
	/**
	 * Whether to load DvkDirectories from index files
	 */
	private boolean use_index;
	
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
		set_index_dir(new File(path));
		//GET USE INDEXVALUE
		set_use_index(prefs.getBoolean(USE_INDEX, true));
	}
	
	/**
	 * Saves preferences.
	 */
	public void save_preferences() {
		Preferences prefs = Preferences.userNodeForPackage(FilePrefs.class);
		//GET INDEX DIR
		prefs.put(INDEX_DIR, get_index_dir().getAbsolutePath());
		//GET USE INDEXVALUE
		prefs.putBoolean(USE_INDEX, use_index());
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
	
	/**
	 * Sets whether to use index files.
	 * 
	 * @param use_index Whether to use index files
	 */
	public void set_use_index(boolean use_index) {
		this.use_index = use_index;
	}
	
	/**
	 * Returns whether to use index files.
	 * 
	 * @return Whether to use index files
	 */
	public boolean use_index() {
		return this.use_index;
	}
}
