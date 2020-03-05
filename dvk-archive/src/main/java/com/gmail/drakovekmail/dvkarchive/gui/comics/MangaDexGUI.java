package com.gmail.drakovekmail.dvkarchive.gui.comics;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.artist.ArtistHostingGUI;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;
import com.gmail.drakovekmail.dvkarchive.web.DConnectSelenium;
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
	private static final long serialVersionUID = -4042530806079268106L;

	/**
	 * DConnect object for connecting to MangaDex chapters
	 */
	private DConnect connect;
	
	/**
	 * DConnectSelenium object for connecting to MangaDex pages
	 */
	private DConnectSelenium sel;

	/**
	 * Key for MangaDex directory in preferences
	 */
	private static final String DIRECTORY = "directory";
	
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
		close();
		this.connect = new DConnect(false, false);
		this.sel = new DConnectSelenium(true);
		this.dvks = MangaDex.get_downloaded_titles(this.dvk_handler);
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < this.dvks.size(); i++) {
			list.add(this.dvks.get(i).get_title());
		}
		set_list(list);
	}

	@Override
	public void sort_dvks() {
		this.dvk_handler.sort_dvks_title(false, false);
	}

	@Override
	public void get_pages(Dvk dvk, boolean check_all) {
		this.start_gui.get_progress_bar().set_progress(
				true, false, 0, 0);
		File dir = dvk.get_dvk_file().getParentFile();
		Dvk title = MangaDex.get_title_info(
				this.connect, dvk.get_id());
		this.start_gui.append_console(
				this.start_gui.get_base_gui()
					.get_language_string("getting_title")
						+ " - " + title.get_title(), false);
		download_title(title, dir, check_all);
		this.start_gui.append_console("", false);
	}

	@Override
	public void download_page(String url) {
		String id = MangaDex.get_title_id(url);
		if(id.length() > 0) {
			this.start_gui.get_progress_bar().set_progress(true, false, 0, 0);
			this.start_gui.append_console("", false);
			this.start_gui.append_console("running_mangadex", true);
			Dvk title = MangaDex.get_title_info(this.connect, id);
			this.start_gui.append_console(
					this.start_gui.get_base_gui().get_language_string("getting_title")
						+ " - " + title.get_title(), false);
			File dir = new File(
					this.start_gui.get_directory(),
					StringProcessing.get_filename(title.get_title()));
			if(!dir.exists()) {
				dir.mkdir();
			}
			download_title(title, dir, true);
		}
	}
	
	/**
	 * Downloads a given MangaDex title.
	 * 
	 * @param title Dvk object with MangaDex title info
	 * @param directory Directory in which to save Dvk files
	 * @param check_all Whether to check all pages
	 */
	private void download_title(Dvk title, File directory, boolean check_all) {
		//TODO CHANGE ENGLISH DEFAULT
		this.start_gui.get_progress_bar().set_progress(true, false, 0, 0);
		this.start_gui.append_console("getting_chapters", true);
		ArrayList<Dvk> chapters = MangaDex.get_chapters(
				this.connect, title, this.start_gui, "English", 1);
		this.start_gui.append_console("downloading_pages", true);
		MangaDex.get_dvks(
				this.sel,
				this.dvk_handler,
				this.start_gui,
				directory,
				chapters,
				check_all,
				true);
	}

	@Override
	public void close() {
		if(this.connect != null) {
			this.connect.close_client();
		}
		if(this.sel != null) {
			this.sel.close_driver();
		}
	}

	@Override
	public File get_captcha() {
		return null;
	}

	@Override
	public boolean login(String username, String password, String captcha) {
		return false;
	}
}
