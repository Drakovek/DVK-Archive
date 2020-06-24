package com.gmail.drakovekmail.dvkarchive.gui.comics;


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
import com.gmail.drakovekmail.dvkarchive.gui.artist.ArtistHostingGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
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
	private static final long serialVersionUID = -4511022942598801099L;

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
	private static final String DIRECTORY = "MDX-DIR";
	
	/**
	 * Initializes the MangaDexGUI object.
	 * 
	 * @param start_gui Parent of MangaDexGUI
	 */
	public MangaDexGUI(StartGUI start_gui) {
		super(start_gui, "mangadex", "titles", true);
		load_directory();
	}
	
	@Override
	public void create_initial_gui() {
		BaseGUI base_gui = get_start_gui().get_base_gui();
		//CREATE TITLE PANEL
		DLabel title_lbl = new DLabel(base_gui, null, "mangadex");
		title_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		title_lbl.set_font_large();
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel title_pnl = base_gui.get_y_stack(title_lbl, sep);
		//CREATE DESCRIPTION PANEL
		DLabel desc_lbl = new DLabel(base_gui, null, "mangadex_desc");
		desc_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		desc_lbl.wrap_text(true);
		JPanel desc_pnl = base_gui.get_y_stack(title_pnl, desc_lbl);
		//CREATE BUTTON PANEL
		DButton run_btn = new DButton(base_gui, this, "skip_login");
		run_btn.set_text_id("run");
		JPanel button_pnl = base_gui.get_y_stack(desc_pnl, run_btn);
		//CREATE SERVICE PANEL
		JPanel service_pnl = base_gui.get_spaced_panel(
				button_pnl, 0, 0, false, false, false, false);
		this.removeAll();
		this.add(service_pnl);
		this.revalidate();
		this.repaint();
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
			get_start_gui().set_directory(directory);
		}
		else {
			prefs.put(DIRECTORY, "");
			get_start_gui().reset_directory();
		}
	}
	
	@Override
	public void directory_opened() {
		save_directory(get_start_gui().get_directory());
		start_process("read_dvks", true);
	}
	
	@Override
	public void get_artists() {
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		get_start_gui().append_console("sorting_dvks", true);
		close_connection();
		try {
			this.sel = new DConnectSelenium(true, get_start_gui());
			this.connect = new DConnect(false, false);
		}
		catch(DvkException e) {}
		set_list_dvks(MangaDex.get_downloaded_titles(get_dvk_handler()));
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < get_list_dvks().size(); i++) {
			list.add(get_list_dvks().get(i).get_title());
		}
		set_list(list);
	}

	@Override
	public void get_pages(Dvk dvk, boolean check_all) {
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		File dir = dvk.get_dvk_file();
		Dvk title = MangaDex.get_title_info(
				this.connect, dvk.get_dvk_id());
		get_start_gui().append_console(
				get_start_gui().get_base_gui()
					.get_language_string("getting_title")
						+ " - " + title.get_title(), false);
		download_title(title, dir, check_all);
		get_start_gui().append_console("", false);
	}

	@Override
	public void download_page(String url) {
		String id = MangaDex.get_title_id(url);
		if(id.length() > 0) {
			get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
			Dvk title = MangaDex.get_title_info(this.connect, id);
			get_start_gui().append_console(
					get_start_gui().get_base_gui().get_language_string("getting_title")
						+ " - " + title.get_title(), false);
			File dir = new File(
					get_start_gui().get_directory(),
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
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		get_start_gui().append_console("getting_chapters", true);
		ArrayList<Dvk> chapters = MangaDex.get_chapters(
				this.connect, title, get_start_gui(), "English", 1);
		get_start_gui().append_console("downloading_pages", true);
		ArrayList<Dvk> downloaded = MangaDex.get_dvks(
				this.sel,
				get_dvk_handler(),
				get_start_gui(),
				directory,
				chapters,
				check_all,
				true);
		for(int i = 0; i < downloaded.size(); i++) {
			get_dvk_handler().add_dvk(downloaded.get(i));
		}
	}

	@Override
	public void close() {
		close_connection();
		this.close_dvk_handler();
	}
	
	/**
	 * Closes all web connection objects.
	 */
	public void close_connection() {
		if(this.connect != null) {
			try {
				this.connect.close();
			}
			catch(DvkException e) {}
		}
		if(this.sel != null) {
			try {
				this.sel.close();
			}
			catch(DvkException e) {}
		}
	}
		

	@Override
	public boolean login(String username, String password) {
		return false;
	}

	@Override
	public void print_start() {
		get_start_gui().append_console("", false);
		get_start_gui().append_console("running_mangadex", true);
	}

	@Override
	public void load_checks() {}

	@Override
	public void save_checks() {}

	@Override
	public void add() {}
}
