package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.ArtistHosting;

/**
 * GUI for downloading files from FurAffinity.net
 * 
 * @author Drakovek
 */
public class FurAffinityGUI extends ArtistHostingGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -6101647561781599187L;

	/**
	 * Key for Fur Affinity directory in preferences
	 */
	private static final String DIRECTORY = "directory";
	
	/**
	 * Initializes the FurAffinityGUI object.
	 * 
	 * @param start_gui Parent of FurAffinityGUI
	 */
	public FurAffinityGUI(StartGUI start_gui) {
		super(start_gui, "fur_affinity");
		create_login_gui();
		load_directory();
	}
	
	/**
	 * Loads the Fur Affinity directory from preferences.
	 */
	public void load_directory() {
		Preferences prefs = Preferences.userNodeForPackage(FurAffinityGUI.class);
		File file = new File(prefs.get(DIRECTORY, ""));
		save_directory(file);
	}

	/**
	 * Saves a given directory to preferences.
	 * 
	 * @param directory Given directory
	 */
	public void save_directory(File directory) {
		Preferences prefs = Preferences.userNodeForPackage(FurAffinityGUI.class);
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
		this.dvks = ArtistHosting.get_artists(
				this.dvk_handler, "furaffinity.net");
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < this.dvks.size(); i++) {
			list.add(this.dvks.get(i).get_artists()[0]);
		}
		this.set_list(list);
	}

	@Override
	public void sort_dvks() {
		this.dvk_handler.sort_dvks_title(true, false);
	}

	@Override
	public void get_pages(Dvk dvk, boolean check_all) {
		this.start_gui.append_console(dvk.get_artists()[0], false);
	}

	@Override
	public void download_page(String url) {
		if(url != null) {
			System.out.println(url);
		}
	}

	@Override
	public void set_up() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tear_down() {
		// TODO Auto-generated method stub
		
	}
}
