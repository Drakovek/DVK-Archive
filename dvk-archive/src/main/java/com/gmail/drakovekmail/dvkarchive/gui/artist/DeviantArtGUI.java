package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DButtonDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DTextDialog;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.ArtistHosting;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.DeviantArt;

/**
 * GUI for downloading files from DeviantArt.net
 * 
 * @author Drakovek
 */
public class DeviantArtGUI extends ArtistHostingGUI {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -1766971777431496910L;

	/**
	 * Key for DeviantArt directory in preferences
	 */
	private static final String DIRECTORY = "DEV-DIR";
	
	/**
	 * DeviantArt object for downloading from DeviantArt
	 */
	private DeviantArt dev;
	
	/**
	 * Initializes the DeviantArtGUI object.
	 * 
	 * @param start_gui Parent of DeviantArtGUI
	 */
	public DeviantArtGUI(StartGUI start_gui) {
		super(start_gui, "deviantart", "artists", false);
		load_directory();
	}
	
	/**
	 * Loads the DeviantArt directory from preferences.
	 */
	public void load_directory() {
		Preferences prefs = Preferences.userNodeForPackage(
				DeviantArtGUI.class);
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
				DeviantArtGUI.class);
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
	public void create_initial_gui() {
		create_login_gui(false);
		if(this.dev != null) {
			this.dev.close();
		}
		this.dev = new DeviantArt();
	}

	@Override
	public boolean login(String username, String password) {
		this.dev.login(username, password);
		return this.dev.is_logged_in();
	}
	
	@Override
	public void load_checks() {
		Preferences prefs = Preferences.userNodeForPackage(DeviantArtGUI.class);
		set_main(prefs.getBoolean("DEV-MAIN", false));
		set_scraps(prefs.getBoolean("DEV-SCRAPS", false));
		set_journals(prefs.getBoolean("DEV-JOURNALS", false));
		set_favorites(prefs.getBoolean("DEV-FAVORITES", false));
	}

	@Override
	public void save_checks() {
		Preferences prefs = Preferences.userNodeForPackage(DeviantArtGUI.class);
		prefs.putBoolean("DEV-MAIN", get_main());
		prefs.putBoolean("DEV-SCRAPS", get_scraps());
		prefs.putBoolean("DEV-JOURNALS", get_journals());
		prefs.putBoolean("DEV-FAVORITES", get_favorites());
	}

	@Override
	public void get_artists() {
		this.dvks = ArtistHosting.get_artists(
				this.dvk_handler, "deviantart.com");
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
	public void print_start() {
		this.start_gui.append_console("", false);
		this.start_gui.append_console(
				"running_deviantart", true);
	}

	@Override
	public void get_pages(Dvk dvk, boolean check_all) {
		File dir = dvk.get_dvk_file();
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		this.start_gui.append_console(
				this.start_gui.get_base_gui().get_language_string("getting_artist")
				+ " - " + dvk.get_artists()[0], false);
		//GET MAIN GALLERY PAGES
		ArrayList<String> main_pages = new ArrayList<>();
		String artist = dvk.get_artists()[0];
		if(get_main()) {
			this.start_gui.append_console("getting_gallery", true);
			main_pages = this.dev.get_pages(
					this.start_gui, artist, dir, 'm', this.dvk_handler, check_all, 0);
		}
		//GET SCRAP PAGES
		ArrayList<String> scrap_pages = new ArrayList<>();
		if(get_scraps()) {
			this.start_gui.append_console("getting_scraps", true);
			scrap_pages = this.dev.get_pages(
					this.start_gui, artist, dir, 's', this.dvk_handler, check_all, 0);
		}
		//GET MODULE PAGES
		ArrayList<String> journal_pages = new ArrayList<>();
		ArrayList<Dvk> mod_pages = new ArrayList<>();
		if(get_journals()) {
			//GET JOURNAL PAGES
			ArrayList<Dvk> jp = new ArrayList<>();
			this.start_gui.append_console("getting_journals", true);
			jp = this.dev.get_module_pages(
					this.start_gui, artist, null, dir, 'j', this.dvk_handler, check_all, 0);
			for(int i = 0; i < jp.size(); i++) {
				journal_pages.add(jp.get(i).get_page_url());
			}
			//GET STATUS UPDATES
			mod_pages = this.dev.get_module_pages(
					this.start_gui, artist, null, dir, 's', this.dvk_handler, check_all, 0);
			//GET POLLS
			mod_pages.addAll(this.dev.get_module_pages(
					this.start_gui, artist, null, dir, 'p', this.dvk_handler, check_all, 0));
		}
		//GET FAVORITES
		ArrayList<String> favs = new ArrayList<>();
		File fav_dir = new File(dir, "favorites");
		if(get_favorites()) {
			this.start_gui.append_console("getting_favorites", true);
			if(!fav_dir.isDirectory()) {
				fav_dir.mkdir();
			}
			favs = this.dev.get_pages(
					this.start_gui, artist, fav_dir, 'f', this.dvk_handler, check_all, 0);
		}
		//DOWNLOAD MAIN GALLERY
		Dvk d = null;
		String fav_artist = artist;
		int g_size = main_pages.size();
		int s_size = scrap_pages.size();
		int j_size = journal_pages.size();
		int m_size = mod_pages.size();
		int f_size = favs.size();
		int size = g_size + s_size + j_size + m_size + f_size;
		this.start_gui.append_console("downloading_pages", true);
		for(int i = g_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(false, true, g_size - (i + 1), size);
			d = this.download_media_page(main_pages.get(i), dir, null, "Gallery:Main", false);
		}
		//DOWNLOAD SCRAPS GALLERY
		for(int i = s_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(
					false, true, g_size + (s_size - (i + 1)), size);
			d = this.download_media_page(scrap_pages.get(i), dir, null, "Gallery:Main", false);
		}
		//DOWNLOAD JOURNAL GALLERY
		int off = g_size + s_size;
		for(int i = j_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(
					false, true, off + (j_size - (i + 1)), size);
			d = this.download_media_page(journal_pages.get(i), dir, null, null, false);
		}
		//DOWNLOAD MODULE PAGES
		off += j_size;
		for(int i = m_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(
					false, true, off + (m_size - (i + 1)), size);
			d = this.download_module_page(mod_pages.get(i), dir);
		}
		if(d != null) {
			fav_artist = d.get_artists()[0];
		}
		//DOWNLOAD FAVORITES PAGES
		off += m_size;
		for(int i = f_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(
					false, true, off + (f_size - (i + 1)), size);
			download_media_page(favs.get(i), fav_dir, fav_artist, null, true);
		}
		this.start_gui.append_console("", false);
	}

	@Override
	public void download_page(String url) {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		//CHECK URL IS VALID
		String id = DeviantArt.get_page_id(url, true);
		if(id.length() > 0 && !id.endsWith("-P") && !id.endsWith("-S")) {
			//CHECK DVK IS NOT ALREADY DOWNLOADED
			boolean download = true;
			int size = this.dvk_handler.get_size();
			for(int i = 0; i < size; i++) {
				if(this.dvk_handler.get_dvk(i).get_id().equals(id)) {
					download = false;
					break;
				}
			}
			//DOWNLOAD PAGE
			if(download) {
				download_media_page(url, this.start_gui.get_directory(), null, null, true);
			}
			else {
				this.start_gui.append_console("already_downloaded", true);
			}
		}
		else {
			//DISPLAY INVALID URL MESSAGE
			String[] buttons = {"ok"};
			String[] labels = {"invalid_deviantart"};
			String title = this.start_gui.get_base_gui().get_language_string("invalid_url");
			DButtonDialog dialog = new DButtonDialog();
			this.start_gui.get_base_gui().set_running(true);
			dialog.open(this.start_gui.get_base_gui(), this.start_gui.get_frame(), title, labels, buttons);
			this.start_gui.get_base_gui().set_running(false);
		}
	}
	
	/**
	 * Downloads a given DeviantArt page.
	 * 
	 * @param url URL of DeviantArt page
	 * @param directory Directory in which to save media
	 * @param artist Artist to use when adding favorite tag.
	 * Doesn't create favorite tag if null.
	 * @param gallery Gallery tag to add to Dvk
	 * @param single Whether this is a single download
	 * @return Downloaded Dvk object
	 */
	public Dvk download_media_page(
			String url,
			File directory,
			String artist,
			String gallery,
			boolean single) {
		String id = DeviantArt.get_page_id(url, true);
		if(!this.start_gui.get_base_gui().is_canceled() && id.length() > 0) {
			//CHECK WHETHER URL IS JOURNAL OR GALLERY PAGE
			Dvk dvk = null;
			if(id.endsWith("-J")) {
				//DOWNLOAD JOURNAL PAGE
				dvk = this.dev.get_journal_dvk(url, this.dvk_handler, directory, single);
			}
			else {
				//DOWNLOAD GALLERY PAGE
				dvk = this.dev.get_dvk(url, this.dvk_handler, gallery, directory, artist, single);
			}
			//CANCEL IF DOWNLOAD FAILED
			if(dvk == null || dvk.get_title() == null) {
				this.start_gui.get_base_gui().set_canceled(true);
				this.start_gui.append_console("deviantart_failed", true);
				return null;
			}
			this.start_gui.append_console(dvk.get_artists()[0] + " - " + dvk.get_title(), false);
			return dvk;
		}
		return null;
	}
	
	/**
	 * Downloads a given DeviantArt module page.
	 * 
	 * @param info Dvk with module page info
	 * @param directory Directory in which to save media
	 * @param gallery Gallery tag to add to Dvk
	 * @return Downloaded Dvk object
	 */
	public Dvk download_module_page(Dvk info, File directory) {
		String id = DeviantArt.get_page_id(info.get_page_url(), true);
		if(!this.start_gui.get_base_gui().is_canceled() && id.length() > 0) {
			//CHECK WHETHER URL IS POLL OR STATUS UPDATE PAGE
			Dvk dvk = null;
			if(id.endsWith("-S")) {
				//DOWNLOAD JOURNAL PAGE
				dvk = DeviantArt.get_status_dvk(info, this.dvk_handler, directory, true);
			}
			else {
				//DOWNLOAD GALLERY PAGE
				dvk = DeviantArt.get_poll_dvk(info, this.dvk_handler, directory, true);
			}
			//CANCEL IF DOWNLOAD FAILED
			if(dvk == null || dvk.get_title() == null) {
				this.start_gui.get_base_gui().set_canceled(true);
				this.start_gui.append_console("deviantart_failed", true);
				return null;
			}
			this.start_gui.append_console(dvk.get_artists()[0] + " - " + dvk.get_title(), false);
			return dvk;
		}
		return null;
	}
	@Override
	public void add() {
		String[] messages = {"enter_deviantart"};
		DTextDialog dialog = new DTextDialog();
		String artist = dialog.open(
				this.start_gui.get_base_gui(),
				this.start_gui.get_frame(),
				"add_artist",
				messages);
		if(artist != null) {
			//CREATE ARTIST FOLDER
			File dir = new File(
					this.start_gui.get_directory(),
					StringProcessing.get_filename(artist));
			if(!dir.exists()) {
				dir.mkdir();
			}
			//ADD TO ARTIST LIST
			Dvk art_dvk = new Dvk();
			art_dvk.set_artist(artist);
			art_dvk.set_dvk_file(new File(dir, "dvk.dvk").getParentFile());
			this.dvks.add(0, art_dvk);
			set_artists();
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
	public void close() {
		this.dev.close();
		close_dvk_handler();
	}
}
