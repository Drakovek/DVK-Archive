package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Class for handling the directory where config files are stored.
 * 
 * @author Drakovek
 */
public class Config {
	
	/**
	 * Key for the config directory to use for the preference file
	 */
	private static final String CONFIG_DIR = "dvk_config_dir";
	
	/**
	 * Directory where config files are stored
	 */
	File config_dir;
	
	/**
	 * Initializes the Config class and loads the config directory from a preference file.
	 */
	public Config() {
		this.config_dir = null;
		//LOAD CONFIG DIRECTORY FROM THE PREFERENCE FILE
		Preferences prefs = Preferences.userNodeForPackage(Config.class);
		String path = prefs.get(CONFIG_DIR, "aAaAnon-existantAaAa");
		set_config_directory(new File(path));
	}
	
	/**
	 * Sets the directory to store config files, and saves to a preference file.
	 * 
	 * @param config_dir Directory for storing config files.
	 */
	public void set_config_directory(File config_dir) {
		//SET CONFIG DIRECTORY
		if(config_dir == null || !config_dir.isDirectory()) {
			//IF GIVEN FILE IS INVALID, DEFAULT TO THE CODE SOURCE DIRECTORY
			this.config_dir = get_source_directory();
		}
		else {			
			this.config_dir = config_dir;
		}
		//SAVE CONFIG DIRECTORY TO A PREFERENCE FILE
		Preferences prefs = Preferences.userNodeForPackage(Config.class);
		if(this.config_dir != null) {
			prefs.put(CONFIG_DIR, this.config_dir.getAbsolutePath());
		}
		else {
			//IF CONFIG DIRECTORY IS INVALID, SAVE PREFERENCE AS EMPTY
			prefs.put(CONFIG_DIR, "");
		}
	}

	/**
	 * Returns the directory for storing config files.
	 * 
	 * @return Directory of stored config files
	 */
	public File get_config_directory() {
		//IF CONFIG DIRECTORY IS NULL, GET THE SOURCE DIRECTORY FOR THE CONFIG CLASS
		if(this.config_dir == null || !this.config_dir.isDirectory()) {
			set_config_directory(get_source_directory());
		}
		//RETURN CONFIG DIRECTORY
		return this.config_dir;
	}
	
	/**
	 * Returns the directory holding the Config class source code or the packaged DVK-Archive jar file.
	 * 
	 * @return Source code directory
	 */
	public static File get_source_directory() {
		try {
			//GET THE SOURCE FILE FOR THE CONFIG CLASS/DVK-ARCHIVE .JAR
			File source = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			//GET THE PARENT DIRECTORY OF THE SOURCE FILE
			while(source != null && source.exists() && !source.isDirectory()) {
				source = source.getParentFile();
			}
			//CREATE DATA DIRECTORY
			source = new File(source, "dvk-data");
			if(!source.isDirectory()) {
				source.mkdir();
			}
			//RETURN DATA DIRECTORY
			return source;
		} catch (Exception e) {}
		//RETURN NULL IF FAILED GETTING SOURCE FILE
		return null;
	}
}
