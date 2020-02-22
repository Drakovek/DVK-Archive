package com.gmail.drakovekmail.dvkarchive.gui.comics;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.artist.ArtistHostingGUI;
import com.gmail.drakovekmail.dvkarchive.web.comics.MangaDex;

/**
 * GUI for downloading media from MangaDex.
 * 
 * @author Drakovek
 */
public class MangaDexGUI extends ArtistHostingGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -3462597218365046390L;

	/**
	 * Key for MangaDex directory in preferences
	 */
	private static final String DIRECTORY = "directory";
	
	/**
	 * List of Dvks with MangaDex title and directory info
	 */
	private ArrayList<Dvk> titles;
	
	/**
	 * Initializes the MangaDexGUI object.
	 * 
	 * @param start_gui Parent of MangaDexGUI
	 */
	public MangaDexGUI(StartGUI start_gui) {
		super(start_gui, "mangadex");
		create_main_gui();
		load_directory();
	}
	
	/**
	 * Loads the MangaDex directory from preferences.
	 */
	public void load_directory() {
		Preferences prefs = Preferences.userNodeForPackage(MangaDexGUI.class);
		File file = new File(prefs.get(DIRECTORY, ""));
		save_directory(file);
	}

	/**
	 * Saves a given directory to preferences.
	 * 
	 * @param directory Given directory
	 */
	public void save_directory(File directory) {
		Preferences prefs = Preferences.userNodeForPackage(MangaDexGUI.class);
		if(directory != null && directory.isDirectory()) {
			prefs.put(DIRECTORY, directory.getAbsolutePath());
			this.start_gui.set_directory(directory);
		}
		else {
			prefs.put(DIRECTORY, "");
			this.start_gui.reset_directory();
		}
	}
	
	@Override
	public void directory_opened() {
		save_directory(this.start_gui.get_directory());
	}
	
	@Override
	public void get_artists() {
		this.titles = MangaDex.get_downloaded_titles(this.dvk_handler);
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < this.titles.size(); i++) {
			list.add(this.titles.get(i).get_title());
		}
		set_list(list);
	}

	@Override
	public void sort_dvks() {
		this.dvk_handler.sort_dvks_title(false, false);
	}
}
