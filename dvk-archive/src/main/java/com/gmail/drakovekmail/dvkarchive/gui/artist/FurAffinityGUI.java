package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DButtonDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DTextDialog;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
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
	private static final long serialVersionUID = 731435074222401851L;

	/**
	 * Key for Fur Affinity directory in preferences
	 */
	private static final String DIRECTORY = "FUR-DIR";
	
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
		super(start_gui, "fur_affinity", "artists", false);
		load_directory();
	}
	
	@Override
	public void create_initial_gui() {
		BaseGUI base_gui = get_start_gui().get_base_gui();
		//CREATE TITLE PANEL
		DLabel title_lbl = new DLabel(base_gui, null, "fur_affinity");
		title_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		title_lbl.set_font_large();
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel title_pnl = base_gui.get_y_stack(title_lbl, sep);
		//CREATE BUTTON PANEL
		DButton login_btn = new DButton(base_gui, this, "login");
		DButton skip_btn = new DButton(base_gui, this, "skip_login");
		JPanel button_pnl = base_gui.get_y_stack(login_btn, skip_btn);
		//CREATE FULL PANEL
		JPanel full_pnl = base_gui.get_y_stack(title_pnl, button_pnl);
		//CREATE SERVICE PANEL
		JPanel service_pnl = base_gui.get_spaced_panel(
				full_pnl, 0, 0, false, false, false, false);
		this.removeAll();
		this.add(service_pnl);
		this.revalidate();
		this.repaint();
		if(this.fur != null) {
			this.fur.close();
		}
		this.fur = new FurAffinity(get_start_gui().get_file_prefs(), get_start_gui());
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
			get_start_gui().set_directory(directory);
		}
		else {
			prefs.put(DIRECTORY, "");
			get_start_gui().reset_directory();
		}
	}

	@Override
	public void directory_opened() {
		get_start_gui().get_base_gui().set_canceled(false);
		save_directory(get_start_gui().get_directory());
		start_process("read_dvks", true);
	}
	
	@Override
	public void get_artists() {
		set_list_dvks(ArtistHosting.get_artists(get_dvk_handler(), "furaffinity.net"));
		set_artists();
	}
	
	/**
	 * Sets the artist list based on loaded artist Dvks.
	 */
	public void set_artists() {
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < get_list_dvks().size(); i++) {
			list.add(get_list_dvks().get(i).get_artists()[0]);
		}
		this.set_list(list);
	}
	
	@Override
	public void get_pages(Dvk dvk, boolean check_all) {
		File dir = dvk.get_dvk_file();
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		get_start_gui().append_console(
				get_start_gui().get_base_gui().get_language_string("getting_artist")
				+ " - " + dvk.get_artists()[0], false);
		try {
			//GET JOURNAL PAGES
			ArrayList<String> g_pages = new ArrayList<>();
			String artist = dvk.get_artists()[0];
			if(get_journals()) {
				//GET JOURNAL PAGES
				get_start_gui().append_console("getting_journals", true);
				g_pages.addAll(this.fur.get_journal_ids(
						artist, dir, get_dvk_handler(), check_all, !get_skipped()));
			}
			//GET MAIN GALLERY PAGES
			if(get_main()) {
				get_start_gui().append_console("getting_gallery", true);
				g_pages = this.fur.get_gallery_ids(
						artist, dir, 'm', get_dvk_handler(),
						check_all, !get_skipped());
			}
			//GET SCRAP PAGES
			if(get_scraps()) {
				get_start_gui().append_console("getting_scraps", true);
				g_pages.addAll(this.fur.get_gallery_ids(
						artist, dir, 's', get_dvk_handler(),
						check_all, !get_skipped()));
			}
			//GET FAVORITES
			ArrayList<String> favs = new ArrayList<>();
			File fav_dir = new File(dir, "favorites");
			if(get_favorites()) {
				get_start_gui().append_console("getting_favorites", true);
				if(!fav_dir.isDirectory()) {
					fav_dir.mkdir();
				}
				favs = this.fur.get_gallery_ids(
						artist, fav_dir, 'f', get_dvk_handler(), 
						check_all, !get_skipped());
			}
			//DOWNLOAD MAIN GALLERY
			Dvk d = null;
			String fav_artist = artist;
			int g_size = g_pages.size();
			int f_size = favs.size();
			int size = g_size + f_size;
			get_start_gui().append_console("downloading_pages", true);
			for(int i = g_size - 1; !get_start_gui().get_base_gui().is_canceled() && i > -1; i--) {
				get_start_gui().get_main_pbar().set_progress(false, true, g_size - (i + 1), size);
				d = this.download_page(g_pages.get(i), dir, null, false);
			}
			if(d != null) {
				fav_artist = d.get_artists()[0];
			}
			//DOWNLOAD FAVORITES PAGES
			for(int i = f_size - 1; !get_start_gui().get_base_gui().is_canceled() && i > -1; i--) {
				get_start_gui().get_main_pbar().set_progress(
						false, true, g_size + (f_size - (i + 1)), size);
				this.download_page(favs.get(i), fav_dir, fav_artist, true);
			}
		}
		catch(DvkException e) {
			set_failed(true);
			get_start_gui().append_console("fur_affinity_failed", true);
		}
		get_start_gui().append_console("", false);
	}

	@Override
	public void download_page(String url) {
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		//CHECK URL IS VALID FUR AFFINITY URL
		String id = FurAffinity.get_page_id(url, true);
		if(id.length() > 0) {
			//CHECK DVK IS NOT ALREADY DOWNLOADED
			boolean download = !is_already_downloaded(id, false, false);
			//DOWNLOAD PAGE
			if(download) {
				try {
					download_page(id, get_start_gui().get_directory(), null, true);
				}
				catch(DvkException e) {}
			}
		}
		else {
			//DISPLAY INVALID URL MESSAGE
			String[] buttons = {"ok"};
			String[] labels = {"invalid_fur_affinity"};
			String title = get_start_gui().get_base_gui().get_language_string("invalid_url");
			DButtonDialog dialog = new DButtonDialog();
			get_start_gui().get_base_gui().set_running(true);
			dialog.open(get_start_gui().get_base_gui(), get_start_gui().get_frame(), title, labels, buttons);
			get_start_gui().get_base_gui().set_running(false);
		}
	}
	
	@Override
	public void add() {
		String[] messages = {"enter_furaffinity"};
		DTextDialog dialog = new DTextDialog();
		String artist = dialog.open(
				get_start_gui().get_base_gui(), get_start_gui().get_frame(),
				"add_artist", messages);
		if(artist != null) {
			//CREATE ARTIST FOLDER
			File dir = new File(get_start_gui().get_directory(), StringProcessing.get_filename(artist));
			if(!dir.exists()) {
				dir.mkdir();
			}
			//ADD TO ARTIST LIST
			Dvk art_dvk = new Dvk();
			art_dvk.set_artist(artist);
			art_dvk.set_dvk_file(new File(dir, "dvk.dvk").getParentFile());
			get_list_dvks().add(0, art_dvk);
			set_artists();
		}
	}
	
	/**
	 * Downloads a given Fur Affinity page.
	 * 
	 * @param id ID of Fur Affinity page
	 * @param directory Directory in which to save media
	 * @param artist Artist to use when adding favorite tag.
	 * Doesn't create favorite tag if null.
	 * @param single Whether this is a single download
	 * @return Downloaded Dvk object
	 * @throws DvkException Throws DvkException if downloading Dvk fails
	 */
	public Dvk download_page(
			String id,
			File directory,
			String artist,
			boolean single) throws DvkException {
		if(!get_start_gui().get_base_gui().is_canceled() && id.length() > 0) {
			//CHECK WHETHER URL IS JOURNAL OR GALLERY PAGE
			Dvk dvk = null;
			if(id.endsWith("-J")) {
				//DOWNLOAD JOURNAL PAGE
				dvk = this.fur.get_journal_dvk(id, get_dvk_handler(), directory, single, true);
			}
			else {
				//DOWNLOAD GALLERY PAGE
				dvk = this.fur.get_dvk(id, get_dvk_handler(), directory, artist, single, true);
			}
			get_start_gui().append_console(dvk.get_artists()[0] + " - " + dvk.get_title(), false);
			return dvk;
		}
		throw new DvkException();
	}

	@Override
	public void close() {
		this.fur.close();
		close_dvk_handler();
	}

	@Override
	public boolean login(String username, String password) {
		this.fur.login();
		return this.fur.is_logged_in();
	}

	@Override
	public void print_start() {
		get_start_gui().append_console("", false);
		get_start_gui().append_console("running_furaffinity", true);
	}

	@Override
	public void load_checks() {
		Preferences prefs = Preferences.userNodeForPackage(FurAffinityGUI.class);
		set_main(prefs.getBoolean("FUR-MAIN", false));
		set_scraps(prefs.getBoolean("FUR-SCRAPS", false));
		set_journals(prefs.getBoolean("FUR-JOURNALS", false));
		set_favorites(prefs.getBoolean("FUR-FAVORITES", false));
	}

	@Override
	public void save_checks() {
		Preferences prefs = Preferences.userNodeForPackage(FurAffinityGUI.class);
		prefs.putBoolean("FUR-MAIN", get_main());
		prefs.putBoolean("FUR-SCRAPS", get_scraps());
		prefs.putBoolean("FUR-JOURNALS", get_journals());
		prefs.putBoolean("FUR-FAVORITES", get_favorites());
	}
}
