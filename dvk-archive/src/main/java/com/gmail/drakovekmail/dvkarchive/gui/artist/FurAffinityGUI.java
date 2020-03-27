package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DButtonDialog;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.ArtistHosting;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.FurAffinity;

/**
 * GUI for downloading files from FurAffinity.net
 * 
 * @author Drakovek
 */
public class FurAffinityGUI extends ArtistHostingGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -5888878483328311951L;

	/**
	 * Key for Fur Affinity directory in preferences
	 */
	private static final String DIRECTORY = "directory";
	
	/**
	 * FurAffinity object for downloading from FurAffinity
	 */
	private FurAffinity fur;
	
	/**
	 * Initializes the FurAffinityGUI object.
	 * 
	 * @param start_gui Parent of FurAffinityGUI
	 */
	public FurAffinityGUI(StartGUI start_gui) {
		super(start_gui, "fur_affinity");
		load_directory();
	}
	
	@Override
	public void create_initial_gui() {
		create_login_gui(true);
		if(this.fur != null) {
			this.fur.close();
		}
		this.fur = new FurAffinity(this.start_gui.get_file_prefs());
	}
	
	/**
	 * Loads the Fur Affinity directory from preferences.
	 */
	public void load_directory() {
		Preferences prefs = Preferences.userNodeForPackage(
				FurAffinityGUI.class);
		File file = new File(prefs.get(DIRECTORY, ""));
		save_directory(file);
	}

	/**
	 * Saves a given directory to preferences.
	 * 
	 * @param directory Given directory
	 */
	public void save_directory(File directory) {
		Preferences prefs = Preferences.userNodeForPackage(
				FurAffinityGUI.class);
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
		if(!this.start_gui.get_base_gui().is_canceled()) {
			start_process("read_dvks", true);
		}
	}
	
	@Override
	public void get_artists() {
		this.dvks = ArtistHosting.get_artists(
				this.dvk_handler, "furaffinity.net");
		set_artists();
	}
	
	/**
	 * Sets the artist list based on loaded artist Dvks.
	 */
	public void set_artists() {
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
		this.start_gui.get_main_pbar().set_progress(
				true, false, 0, 0);
		File dir = dvk.get_dvk_file();
		this.start_gui.append_console(
				this.start_gui.get_base_gui()
					.get_language_string("getting_artist")
				+ " - " + dvk.get_artists()[0], false);
		//GET GALLERY PAGES
		this.start_gui.append_console("getting_gallery", true);
		ArrayList<String> pages;
		String artist = FurAffinity.get_url_artist(
				dvk.get_artists()[0]);
		pages = this.fur.get_pages(
				this.start_gui, artist, false,
				this.dvk_handler, check_all, !this.skipped, 1);
		//GET SCRAP PAGES
		this.start_gui.append_console("getting_scraps", true);
		pages.addAll(this.fur.get_pages(
				this.start_gui, artist, true, 
				this.dvk_handler, check_all, !this.skipped, 1));
		//GET JOURNAL PAGES
		this.start_gui.append_console("getting_journals", true);
		pages.addAll(this.fur.get_journal_pages(
				this.start_gui, artist, this.dvk_handler,
				check_all, !this.skipped, 1));
		//DOWNLOAD PAGES
		this.start_gui.append_console("downloading_pages", true);
		for(int i = pages.size() - 1;
				!this.start_gui.get_base_gui().is_canceled() && i > -1;
				i--) {
			this.start_gui.get_main_pbar().set_progress(
					false, true, pages.size() - (i + 1), pages.size());
			download_page(pages.get(i), dir, false);
		}
		this.start_gui.append_console("", false);
	}

	@Override
	public void download_page(String url) {
		this.start_gui.get_main_pbar().set_progress(
				true, false, 0, 0);
		//CHECK URL IS VALID
		if(FurAffinity.get_page_id(url, false).length() > 0) {
			//DOWNLOAD PAGE
			download_page(
					url, this.start_gui.get_directory(), true);
		}
		else {
			//ASK TO ADD AS ARTIST
			String[] labels = {"invalid_fur_url", "use_fur_artist"};
			String[] buttons = {"yes", "no"};
			DButtonDialog dbd = new DButtonDialog();
			String result = dbd.open(this.start_gui.get_base_gui(), this.start_gui.get_frame(), "invalid_url", labels, buttons);
			if(result.equals("yes")) {
				//CREATE ARTIST FOLDER
				File dir = new File(this.start_gui.get_directory(), url);
				if(!dir.exists()) {
					dir.mkdir();
				}
				//ADD TO ARTIST LIST
				Dvk art_dvk = new Dvk();
				art_dvk.set_artist(url);
				art_dvk.set_dvk_file(new File(dir, "dvk.dvk").getParentFile());
				this.dvks.add(0, art_dvk);
				set_artists();
			}
		}
	}
	
	/**
	 * Downloads a given Fur Affinity page.
	 * 
	 * @param url URL of Fur Affinity page
	 * @param directory Directory in which to save media
	 * @param single Whether this is a single download
	 */
	public void download_page(String url, File directory, boolean single) {
		if(!this.start_gui.get_base_gui().is_canceled()
				&& url != null
				&& FurAffinity.get_page_id(url, false).length() > 0) {
			//CHECK WHETHER URL IS JOURNAL OR GALLERY PAGE
			Dvk dvk = null;
			if(url.contains("/journal/")) {
				//DOWNLOAD JOURNAL PAGE
				dvk = this.fur.get_journal_dvk(
						url, directory, single, true);
				this.dvk_handler.add_dvk(dvk);
			}
			else {
				//DOWNLOAD GALLERY PAGE
				dvk = this.fur.get_dvk(
						url, directory, single, true);
			}
			//CANCEL IF DOWNLOAD FAILED
			if(dvk == null || dvk.get_title() == null) {
				this.start_gui.get_base_gui().set_canceled(true);
				this.start_gui.append_console(
						"fur_affinity_failed", true);
			}
			else {
				this.start_gui.append_console(dvk.get_title(), false);
				this.dvk_handler.add_dvk(dvk);
			}
		}
	}

	@Override
	public void close() {
		this.fur.close();
	}

	@Override
	public File get_captcha() {
		return this.fur.get_captcha();
	}

	@Override
	public boolean login(String username, String password, String captcha) {
		this.fur.login(username, password, captcha);
		return this.fur.is_logged_in();
	}

	@Override
	public void print_start() {
		this.start_gui.append_console("", false);
		this.start_gui.append_console(
				"running_furaffinity", true);
	}
}
