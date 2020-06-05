package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DButtonDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DTextDialog;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.ArtistHosting;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.Inkbunny;

/**
 * GUI for downloading files from Inkbunny.net
 * 
 * @author Drakovek
 */
public class InkbunnyGUI extends ArtistHostingGUI {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -6604662058393667184L;

	/**
	 * Key for Inkbunny directory in preferences
	 */
	private static final String DIRECTORY = "INKB-DIR";
	
	/**
	 * DeviantArt object for downloading from Inkbunny
	 */
	private Inkbunny ink;
	
	//TODO SOME SORT OF API ENABLE MESSAGE
	
	/**
	 * Initializes the InkbunnyGUI object.
	 * 
	 * @param start_gui Parent of InkbunnyGUI
	 */
	public InkbunnyGUI(StartGUI start_gui) {
		super(start_gui, "inkbunny", "artists", false);
		load_directory();
	}
	
	/**
	 * Loads the Inkbunny directory from preferences.
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
		this.create_login_gui(true);
		if(this.ink != null) {
			this.ink.close();
		}
		this.ink = new Inkbunny(this.start_gui, this.dvk_handler);
	}

	@Override
	public boolean login(String username, String password) {
		return this.ink.login(username, password);
	}

	@Override
	public void load_checks() {
		Preferences prefs = Preferences.userNodeForPackage(DeviantArtGUI.class);
		set_main(prefs.getBoolean("INK-MAIN", false));
		set_scraps(prefs.getBoolean("INK-SCRAPS", false));
		set_journals(prefs.getBoolean("INK-JOURNALS", false));
		set_favorites(prefs.getBoolean("INK-FAVORITES", false));
	}

	@Override
	public void save_checks() {
		Preferences prefs = Preferences.userNodeForPackage(DeviantArtGUI.class);
		prefs.putBoolean("INK-MAIN", get_main());
		prefs.putBoolean("INK-SCRAPS", get_scraps());
		prefs.putBoolean("INK-JOURNALS", get_journals());
		prefs.putBoolean("INK-FAVORITES", get_favorites());
	}

	@Override
	public void get_artists() {
		if(!this.ink.is_logged_in()) {
			this.ink.login("guest", "");
		}
		this.dvks = ArtistHosting.get_artists(
				this.dvk_handler, "inkbunny.net");
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
		this.start_gui.append_console("running_inkbunny", true);
	}

	@Override
	public void get_pages(Dvk dvk, boolean check_all) {
		File dir = dvk.get_dvk_file();
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		this.start_gui.append_console(
				this.start_gui.get_base_gui().get_language_string("getting_artist")
				+ " - " + dvk.get_artists()[0], false);
		//GET MAIN GALLERY IDS
		ArrayList<Dvk> g_ids = new ArrayList<>();
		String fav_artist = dvk.get_artists()[0];
		String user_id = this.ink.get_user_id(fav_artist);
		if(get_main()) {
			this.start_gui.append_console("getting_gallery", true);
			g_ids = this.ink.get_pages(user_id, dir, 'm', fav_artist, check_all);
		}
		//GET SCRAP GALLERY IDS
		if(get_scraps()) {
			this.start_gui.append_console("getting_scraps", true);
			g_ids.addAll(this.ink.get_pages(user_id, dir, 's', fav_artist, check_all));
		}
		//GET JOURNAL IDS
		ArrayList<String> j_ids = new ArrayList<>();
		if(get_journals()) {
			this.start_gui.append_console("getting_journals", true);
			j_ids = this.ink.get_journal_pages(fav_artist, dir, check_all, 1);
		}
		//GET FAVORITES
		ArrayList<Dvk> fav_ids = new ArrayList<>();
		File fav_dir = new File(dir, "favorites");
		if(get_favorites()) {
			this.start_gui.append_console("getting_favorites", true);
			if(!fav_dir.isDirectory()) {
				fav_dir.mkdir();
			}
			fav_ids = this.ink.get_pages(user_id, dir, 'f', fav_artist, check_all);
		}
		//DOWNLOAD MAIN GALLERY
		ArrayList<Dvk> ds;
		int g_size = g_ids.size();
		int j_size = j_ids.size();
		int f_size = fav_ids.size();
		int size = g_size + j_size + f_size;
		this.start_gui.append_console("downloading_pages", true);
		for(int i = g_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(false, true, g_size - (i + 1), size);
			ds = this.download_media_page(g_ids.get(i).get_page_url(),
					g_ids.get(i).get_sql_id(), dir, fav_artist, false);
			if(ds != null) {
				fav_artist = ds.get(0).get_artists()[0];
			}
		}
		//DOWNLOAD JOURNALS GALLERY
		Dvk d;
		for(int i = j_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(
							false, true, g_size + (j_size - (i + 1)), size);
			d = this.download_journal_page(j_ids.get(i), dir, false);
			if(d != null) {
				fav_artist = d.get_artists()[0];
			}
		}
		//DOWNLOAD FAVORITES GALLERY
		int off = g_size + j_size;
		for(int i = f_size - 1; !this.start_gui.get_base_gui().is_canceled() && i > -1; i--) {
			this.start_gui.get_main_pbar().set_progress(
					false, true, off + (f_size - (i + 1)), size);
			this.download_media_page(g_ids.get(i).get_page_url(),
					g_ids.get(i).get_sql_id(), dir, fav_artist, true);
		}
		this.start_gui.append_console("", false);
	}
	
	/**
	 * Downloads an Inkbunny journal from a given ID.
	 * 
	 * @param journal_id Inkbunny journal ID
	 * @param directory Directory in which to save journal
	 * @param single Whether this is a single download
	 * @return Downloaded journal in Dvk form
	 */
	public Dvk download_journal_page(String journal_id, File directory, boolean single) {
		if(!this.start_gui.get_base_gui().is_canceled()) {
			Dvk dvk = this.ink.get_journal_dvk(journal_id, directory, single, true);
			//CANCEL IF DOWNLOAD FAILED
			if(dvk == null || dvk.get_title() == null) {
				this.start_gui.get_base_gui().set_canceled(true);
				this.start_gui.append_console("inkbunny_failed", true);
				return null;
			}
			this.start_gui.append_console(dvk.get_artists()[0] + " - " 
					+ dvk.get_title(), false);
			return dvk;
		}
		return null;
	}
	
	/**
	 * Downloads media for a given Inkbunny submission.
	 * 
	 * @param sub_id Inkbunny submission ID
	 * @param page_count Number of pages in the submission
	 * @param directory Directory in which to save media
	 * @param fav_artist Artist to add as favorite tag, if applicable
	 * @param single Whether this is a single download
	 * @return List of Dvks for the Inkbunny submission
	 */
	public ArrayList<Dvk> download_media_page(
			String sub_id,
			int page_count,
			File directory,
			String fav_artist,
			boolean single) {
		if(!this.start_gui.get_base_gui().is_canceled()) {
			ArrayList<Dvk> ds = null;
			ArrayList<String> favorites = null;
			if(fav_artist != null) {
				favorites = new ArrayList<>();
				favorites.add(fav_artist);
			}
			ds = this.ink.get_dvks(sub_id, directory, page_count, single, favorites, true);
			//CANCEL IF DOWNLOAD FAILED
			if(ds == null || ds.size() == 0) {
				this.start_gui.get_base_gui().set_canceled(true);
				this.start_gui.append_console("inkbunny_failed", true);
				return null;
			}
			this.start_gui.append_console(ds.get(0).get_artists()[0] + " - " 
					+ ds.get(ds.size() - 1).get_title(), false);
			return ds;
		}
		return null;
	}

	@Override
	public void download_page(String url) {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		String id = Inkbunny.get_page_id(url, true);
		if(id.length() > 0) {
			if(!id.endsWith("-J")) {
				id = id + "-";
			}
			StringBuilder sql = new StringBuilder("SELECT * ");
			sql.append(" FROM ");
			sql.append(DvkHandler.DVKS);
			sql.append(" WHERE ");
			sql.append(DvkHandler.DVK_ID);
			sql.append(" LIKE ?;");
			String[] params = {id + "%"};
			try(ResultSet rs = this.dvk_handler.get_sql_set(sql.toString(), params)) {
				ArrayList<Dvk> results = DvkHandler.get_dvks(rs);
				if(results.size() == 0) {
					//DOWNLOAD MEDIA
					if(id.endsWith("-J")) {
						download_journal_page(Inkbunny.get_page_id(url, false),
								this.start_gui.get_directory(), true);
					}
					else {
						download_media_page(Inkbunny.get_page_id(url, false), 2,
								this.start_gui.get_directory(), null, true);
					}
				}
				else {
					//ALREADY DOWNLOADED
					this.start_gui.append_console("already_downloaded", true);
					for(int i = 0; i < results.size(); i++) {
						this.start_gui.append_console(
								results.get(i).get_dvk_file().getAbsolutePath(), false);
					}
				}
			}
			catch(SQLException e) {}
		}
		else {
			//DISPLAY INVALID URL MESSAGE
			String[] buttons = {"ok"};
			String[] labels = {"invalid_inkbunny"};
			String title = this.start_gui.get_base_gui().get_language_string("invalid_url");
			DButtonDialog dialog = new DButtonDialog();
			this.start_gui.get_base_gui().set_running(true);
			dialog.open(this.start_gui.get_base_gui(), this.start_gui.get_frame(), title, labels, buttons);
			this.start_gui.get_base_gui().set_running(false);
		}
	}
	
	@Override
	public void add() {
		String[] messages = {"enter_inkbunny"};
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
		this.ink.close();
		close_dvk_handler();
	}

}
