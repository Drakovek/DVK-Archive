package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.ServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DCheckBox;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DPasswordField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DTextDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DCheckEvent;

/**
 * GUI for downloading files from artist-hosting websites.
 * 
 * @author Drakovek
 */
public abstract class ArtistHostingGUI extends ServiceGUI implements DActionEvent, DCheckEvent {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -1291420201034906620L;

	/**
	 * Name of the current artist-hosting service.
	 */
	private String name;
	
	/**
	 * Label for the main GUI list of artists/titles/etc.
	 */
	private String list_label;
	
	/**
	 * Whether the main GUI uses simple layout without checkboxes and add button
	 */
	private boolean simple;
	
	/**
	 * Whether login was skipped.
	 */
	private boolean skipped;
	
	/**
	 * Whether to download the main gallery
	 */
	private boolean main;
	
	/**
	 * Whether to download the scraps gallery
	 */
	private boolean scraps;
	
	/**
	 * Whether to download artist's journals
	 */
	private boolean journals;
	
	/**
	 * Whether to download artist's favorites
	 */
	private boolean favorites;
	
	/**
	 * Whether the current downloading process has failed
	 */
	private boolean failed;
	
	/**
	 * DVK handler for loading existing DVK files.
	 */
	private DvkHandler dvk_handler;
	
	/**
	 * Dvks with title/artist and directory info.
	 */
	private ArrayList<Dvk> list_dvks;
	
	/**
	 * List for holding artists/titles.
	 */
	private DList lst;
	
	/**
	 * Button for checking all pages
	 */
	private DButton all_btn;
	
	/**
	 * Button for checking new pages
	 */
	private DButton new_btn;
	
	/**
	 * Button for downloading a single title
	 */
	private DButton single_btn;
	
	/**
	 * Button for adding artists/titles
	 */
	private DButton add_btn;
	
	/**
	 * CheckBox for main gallery
	 */
	private DCheckBox main_chk;
	
	/**
	 * CheckBox for scraps gallery
	 */
	private DCheckBox scraps_chk;
	
	/**
	 * CheckBox for journal gallery
	 */
	private DCheckBox journal_chk;
	
	/**
	 * CheckBox for favorites gallery
	 */
	private DCheckBox favorite_chk;
	
	/**
	 * Button for logging in
	 */
	private DButton login_btn;
	
	/**
	 * Button for skipping login
	 */
	private DButton skip_btn;
	
	/**
	 * Text field for entering username
	 */
	private DTextField u_txt;
	
	/**
	 * Password field for entering password
	 */
	private DPasswordField p_txt;
	
	/**
	 * Initializes the ArtistHostingGUI object.
	 * 
	 * @param start_gui Parent of ArtistHostingGUI
	 * @param name_id Language ID for the name of current service.
	 * @param list_label Label for the main GUI list of artists/titles/etc.
	 * @param simple Whether the main GUI uses simple layout without checkboxes and add button
	 */
	public ArtistHostingGUI(StartGUI start_gui, String name_id, String list_label, boolean simple) {
		super(start_gui);
		this.failed = false;
		FilePrefs prefs = get_start_gui().get_file_prefs();
		try {
			this.dvk_handler = new DvkHandler(prefs, null, get_start_gui());
		}
		catch(DvkException e) {}
		this.list_label = list_label;
		this.simple = simple;
		set_list_dvks(new ArrayList<>());
		this.name = start_gui.get_base_gui().get_language_string(name_id);
		this.setLayout(new GridLayout(1, 1));
		//INITIALIZE COMPONENTS
		load_checks();
		BaseGUI base_gui = start_gui.get_base_gui();
		this.all_btn = new DButton(base_gui, this, "check_all");
		this.new_btn = new DButton(base_gui, this, "check_new");
		this.single_btn = new DButton(base_gui, this, "download_single");
		this.add_btn = new DButton(base_gui, this, "add");
		this.lst = new DList(base_gui, this, "list", true);
		this.login_btn = new DButton(base_gui, this, "login");
		this.skip_btn = new DButton(base_gui, this, "skip_login");
		this.main_chk = new DCheckBox(base_gui, this, "main", get_main());
		this.scraps_chk = new DCheckBox(base_gui, this, "scraps", get_scraps());
		this.journal_chk = new DCheckBox(base_gui, this, "journals", get_journals());
		this.favorite_chk = new DCheckBox(base_gui, this, "favorites", get_favorites());
		//CREATE GUI
		this.skipped = true;
		create_initial_gui();
	}
	
	/**
	 * Returns whether login was skipped.
	 * 
	 * @return Whether login was skipped
	 */
	public boolean get_skipped() {
		return this.skipped;
	}
	
	/**
	 * Returns Dvk objects with title/artist and directory info to show in GUI list.
	 * 
	 * @return Dvks to show in list
	 */
	public ArrayList<Dvk> get_list_dvks() {
		return this.list_dvks;
	}
	
	/**
	 * Sets the Dvk objects to show in the GUI list.
	 * 
	 * @param list_dvks List Dvk objects
	 */
	public void set_list_dvks(ArrayList<Dvk> list_dvks) {
		this.list_dvks = list_dvks;
	}
	
	/**
	 * Returns the loaded DvkHandler object.
	 * 
	 * @return DvkHandler
	 */
	public DvkHandler get_dvk_handler() {
		return this.dvk_handler;
	}
	
	/**
	 * Creates and displays a login GUI.
	 * 
	 * @param allow_skipping Whether to allow skipping login
	 */
	protected void create_login_gui(boolean allow_skipping) {
		BaseGUI base_gui = get_start_gui().get_base_gui();
		this.u_txt = new DTextField(base_gui, this, "nothing");
		this.p_txt = new DPasswordField(base_gui);
		DLabel u_lbl = new DLabel(base_gui, this.u_txt, "username");
		DLabel p_lbl = new DLabel(base_gui, this.p_txt, "password");
		//CREATE INPUT PANEL
		JPanel usr_pnl = base_gui.get_x_stack(u_lbl, 0, this.u_txt, 1);
		JPanel pass_pnl = base_gui.get_x_stack(p_lbl, 0, this.p_txt, 1);
		JPanel in_pnl = base_gui.get_y_stack(usr_pnl, pass_pnl);
		//CREATE BOTTOM PANEL
		JPanel btn_pnl = base_gui.get_y_stack(this.login_btn, this.skip_btn);
		JPanel btm_pnl;
		if(allow_skipping) {
			btm_pnl = base_gui.get_y_stack(in_pnl, btn_pnl);
		}
		else {
			btm_pnl = base_gui.get_y_stack(in_pnl, this.login_btn);
		}
		//CREATE TOP LABEL
		String label = base_gui.get_language_string("login");
		label = LanguageHandler.get_text(label);
		label = this.name + " - " + label;
		DLabel login_lbl = new DLabel(base_gui, null, "login");
		login_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		login_lbl.set_font_large();
		login_lbl.setText(label);
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel top_pnl = base_gui.get_y_stack(login_lbl, sep);
		//CREATE FULL LOGIN PANEL
		JPanel login_pnl;
		login_pnl = base_gui.get_y_stack(top_pnl, btm_pnl);
		//CREATE CENTER PANEL
		JPanel center_pnl = base_gui.get_spaced_panel(login_pnl, 0, 0, true, true, true, true);
		DScrollPane scr = new DScrollPane(center_pnl);
		//UPDATE MAIN PANEL
		this.removeAll();
		this.add(scr);
		this.revalidate();
		this.repaint();
		//LIMIT SIZE
		int height = base_gui.get_font().getSize() * 2;
		height += base_gui.get_space_size();
		int width = (int)this.u_txt.getSize().getWidth();
		this.u_txt.setPreferredSize(new Dimension(width, height));
		width = (int)this.p_txt.getSize().getWidth();
		this.p_txt.setPreferredSize(new Dimension(width, height));
	}
	
	/**
	 * Creates the initial GUI.
	 * Also called to reset when process is canceled.
	 */
	public abstract void create_initial_gui();
	
	/**
	 * Starts the process of logging in to website.
	 */
	private void start_login() {
		get_start_gui().get_main_pbar().set_progress(true, false, 0, 0);
		get_start_gui().append_console("attempt_login", true);
		String user = null;
		String pass = null;
		try {
			user = this.u_txt.getText();
			pass = this.p_txt.get_text();
			
		}
		catch(NullPointerException e) {}
		boolean login = login(user, pass);
		user = null;
		pass = null;
		if(login) {
			try {
				this.p_txt.setText("");
			}
			catch(NullPointerException e) {};
			get_start_gui().append_console("login_success", true);
			this.skipped = false;
		}
		else {
			get_start_gui().append_console("login_failed", true);
		}
	}
	
	/**
	 * Attempts login to the artist hosting site.
	 * 
	 * @param username Username
	 * @param password Password
	 * @return Whether login was successful
	 */
	public abstract boolean login(String username, String password);
	
	/**
	 * Creates the main artist hosting GUI.
	 */
	public void create_main_gui() {
		BaseGUI base_gui = get_start_gui().get_base_gui();
		//CREATE BUTTON PANEL
		JPanel btn_pnl = new JPanel();
		btn_pnl.setLayout(new GridLayout(3, 1, 0, base_gui.get_space_size()));
		this.new_btn.setEnabled(false);
		this.all_btn.setEnabled(false);
		this.single_btn.setEnabled(false);
		this.add_btn.setEnabled(false);
		this.main_chk.setEnabled(false);
		this.scraps_chk.setEnabled(false);
		this.favorite_chk.setEnabled(false);
		this.journal_chk.setEnabled(false);
		btn_pnl.add(this.new_btn);
		btn_pnl.add(this.all_btn);
		btn_pnl.add(this.single_btn);
		//CREATE SIDE PANEL
		DScrollPane scr = new DScrollPane(this.lst);
		DLabel list_lbl = new DLabel(base_gui, this.lst, this.list_label);
		list_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel art_pnl = base_gui.get_y_stack(list_lbl, 0, scr, 1);
		JPanel side_pnl = base_gui.get_y_stack(art_pnl, 1, this.add_btn, 0);
		//CREATE CHECK PANEL
		int space = base_gui.get_space_size();
		JPanel check_pnl = new JPanel();
		check_pnl.setLayout(new GridLayout(2, 2, space, space));
		check_pnl.add(this.main_chk);
		check_pnl.add(this.scraps_chk);
		check_pnl.add(this.journal_chk);
		check_pnl.add(this.favorite_chk);
		JPanel action_pnl = base_gui.get_y_stack(btn_pnl, 1, check_pnl, 0);
		//CREATE SPLIT PANEL
		JPanel split_pnl = new JPanel();
		split_pnl.setLayout(new GridLayout(1, 2, base_gui.get_space_size(), 0));
		if(this.simple) {
			split_pnl.add(btn_pnl);
			split_pnl.add(art_pnl);
		}
		else {
			split_pnl.add(action_pnl);
			split_pnl.add(side_pnl);
		}
		//CREATE TOP PANEL
		DLabel lbl = new DLabel(base_gui, null, "artists");
		lbl.setText(this.name);
		lbl.set_font_large();
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		JSeparator sep;
		sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel top_pnl = base_gui.get_y_stack(lbl, sep);
		//UPDATE MAIN PANEL
		this.removeAll();
		this.add(base_gui.get_y_stack(top_pnl, 0, split_pnl, 1));
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Loads preferences for gallery check boxes.
	 */
	public abstract void load_checks();
	
	/**
	 * Saves preferences for gallery check boxes.
	 */
	public abstract void save_checks();
	
	/**
	 * Sets whether to get main gallery pages.
	 * 
	 * @param main Whether to get main gallery
	 */
	public void set_main(boolean main) {
		this.main = main;
	}
	
	/**
	 * Returns whether to get main gallery pages.
	 * 
	 * @return Whether to get main gallery
	 */
	public boolean get_main() {
		return this.main;
	}
	
	/**
	 * Sets whether to get scraps gallery pages.
	 * 
	 * @param scraps Whether to get scraps gallery
	 */
	public void set_scraps(boolean scraps) {
		this.scraps = scraps;
	}
	
	/**
	 * Returns whether to get scraps gallery pages.
	 * 
	 * @return Whether to get scraps gallery
	 */
	public boolean get_scraps() {
		return this.scraps;
	}
	
	/**
	 * Sets whether to get journal pages.
	 * 
	 * @param journals Whether to get journals
	 */
	public void set_journals(boolean journals) {
		this.journals = journals;
	}
	
	/**
	 * Returns whether to get journal pages.
	 * 
	 * @return Whether to get journals
	 */
	public boolean get_journals() {
		return this.journals;
	}
	
	/**
	 * Sets whether to get favorites pages.
	 * 
	 * @param favorites Whether to get favorites
	 */
	public void set_favorites(boolean favorites) {
		this.favorites = favorites;
	}
	
	/**
	 * Returns whether to get favorites pages.
	 * 
	 * @return Whether to get favorites
	 */
	public boolean get_favorites() {
		return this.favorites;
	}
	
	/**
	 * Sets whether the current running process has failed.
	 * 
	 * @param failed Whether the current process has failed.
	 */
	public void set_failed(boolean failed) {
		this.failed = failed;
	}
	
	/**
	 * Sets the main list to show the given ArrayList.
	 * Adds additional item for selecting all items.
	 * 
	 * @param list List to display
	 */
	public void set_list(ArrayList<String> list) {
		String[] array = new String[list.size() + 1];
		array[0] = get_start_gui().get_base_gui().get_language_string("select_all");
		for(int i = 0; i < list.size(); i++) {
			array[i + 1] = list.get(i);
		}
		this.lst.setListData(array);
	}
	
	/**
	 * Gets list of artists and updates the main list accordingly.
	 */
	public abstract void get_artists();
	
	/**
	 * Reads all dvks in base_gui's selected directory.
	 */
	public void read_dvks() {
		close_dvk_handler();
		File[] dirs = {get_start_gui().get_directory()};
		this.dvk_handler.read_dvks(dirs);
		get_artists();
	}
	
	/**
	 * Returns list of indexes for the dvks selected in main list.
	 * 
	 * @return Int array of indexes
	 */
	private int[] get_selected() {
		int[] sel = this.lst.getSelectedIndices();
		//IF ALL SELECTED, RETURN ALL
		if(sel.length > 0 && sel[0] == 0) {
			sel = new int[get_list_dvks().size()];
			for(int i = 0; i < sel.length; i++) {
				sel[i] = i;
			}
		}
		else {
			//SHIFT INDEXES
			for(int i = 0; i < sel.length; i++) {
				sel[i] = sel[i] - 1;
			}
		}
		return sel;
	}
	
	/**
	 * Runs the get_pages method for all selected artists/titles.
	 * 
	 * @param check_all Whether to check all pages
	 */
	private void run_get_pages(boolean check_all) {
		print_start();
		int[] sel = get_selected();
		get_start_gui().get_base_gui().set_canceled(false);
		for(int i = 0; i < sel.length; i++) {
			get_start_gui().get_secondary_pbar().set_progress(false, true, i, sel.length);
			if(!get_start_gui().get_base_gui().is_canceled() && !this.failed) {
				get_pages(get_list_dvks().get(sel[i]), check_all);
			}
		}
	}
	
	/**
	 * Runs the download_page method for a given URL.
	 * 
	 * @param url Page URL
	 */
	private void run_download_single(String url) {
		print_start();
		get_start_gui().get_base_gui().set_canceled(false);
		download_page(url);
	}
	
	/**
	 * Returns whether a DVK with a given ID has already been downloaded.
	 * 
	 * @param id ID to search for
	 * @param like Whether to use SQL "LIKE" to search for IDs. Otherwise, looks for exact ID match.
	 * @param exclude_journals Whether to exclude Journal IDs (IDs that end with "-J")
	 * @return If DVK is already downloaded
	 */
	public boolean is_already_downloaded(
			String id,
			boolean like,
			boolean exclude_journals) {
		ArrayList<String> params = new ArrayList<>();
		params.add(id);
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.DVK_ID);
		if(like) {
			sql.append(" LIKE ?");
		}
		else {
			sql.append(" = ?");
		}
		if(exclude_journals) {
			sql.append(" AND ");
			sql.append(DvkHandler.DVK_ID);
			sql.append(" NOT LIKE ?");
			params.add("%-J");
		}
		try(ResultSet rs = this.dvk_handler.sql_select(sql.toString(), params, true)) {
			ArrayList<Dvk> result_dvks = DvkHandler.get_dvks(rs);
			if(result_dvks.size() > 0) {
				//ALREADY DOWNLOADED
				get_start_gui().append_console("already_downloaded", true);
				for(int i = 0; i < result_dvks.size(); i++) {
					get_start_gui().append_console(
							result_dvks.get(i).get_dvk_file().getAbsolutePath(), false);
				}
				return true;
			}
			return false;
		}
		catch(SQLException e) {}
		return false;
	}
	
	/**
	 * Prints message that indicates process has started.
	 */
	public abstract void print_start();

	/**
	 * Gets pages for a given artist/title.
	 * 
	 * @param dvk Dvk with title/artist and directory info.
	 * @param check_all Whether to check all pages
	 */
	public abstract void get_pages(Dvk dvk, boolean check_all);
	
	/**
	 * Downloads from a single page URL.
	 * 
	 * @param url Page URL
	 */
	public abstract void download_page(String url);
	
	/**
	 * Called to add artist/title.
	 */
	public abstract void add();
	
	@Override
	public void enable_all() {
		if(!this.failed) {
			this.new_btn.setEnabled(true);
			this.all_btn.setEnabled(true);
			this.single_btn.setEnabled(true);
		}
		this.add_btn.setEnabled(true);
		this.lst.setEnabled(true);
		this.login_btn.setEnabled(true);
		this.skip_btn.setEnabled(true);
		this.main_chk.setEnabled(true);
		this.scraps_chk.setEnabled(true);
		this.journal_chk.setEnabled(true);
		this.favorite_chk.setEnabled(true);
	}

	@Override
	public void disable_all() {
		this.new_btn.setEnabled(false);
		this.all_btn.setEnabled(false);
		this.single_btn.setEnabled(false);
		this.add_btn.setEnabled(false);
		this.lst.setEnabled(false);
		this.login_btn.setEnabled(false);
		this.skip_btn.setEnabled(false);
		this.main_chk.setEnabled(false);
		this.scraps_chk.setEnabled(false);
		this.journal_chk.setEnabled(false);
		this.favorite_chk.setEnabled(false);
	}
	
	@Override
	public void event(String id) {
		switch(id) {
			case "refresh_captcha":
				start_process("refresh_captcha", true);
				break;
			case "login":
				this.failed = false;
				start_process("login", true);
				break;
			case "skip_login":
				this.failed = false;
				create_main_gui();
				start_process("read_dvks", true);
				break;
			case "check_new":
				start_process("check_new", false);
				break;
			case "check_all":
				start_process("check_all", false);
				break;
			case "add":
				add();
				break;
			case "download_single":
				if(this.directory_loaded()) {
					String[] messages = {"enter_page_url"};
					DTextDialog dialog = new DTextDialog();
					String url = dialog.open(
							get_start_gui().get_base_gui(),
							get_start_gui().get_frame(),
							"download_single",
							messages);
					if(url != null) {
						start_process(url, false);
					}
				}
				break;
		}
	}
	
	@Override
	public void check_event(String id, boolean checked) {
		switch(id) {
			case "main":
				this.main = checked;
				break;
			case "scraps":
				this.scraps = checked;
				break;
			case "journals":
				this.journals = checked;
				break;
			case "favorites":
				this.favorites = checked;
				break;
		}
		save_checks();
	}
	
	@Override
	public void run(String id) {
		switch(id) {
			case "login":
				start_login();
				break;
			case "check_new":
				run_get_pages(false);
				break;
			case "check_all":
				run_get_pages(true);
				break;
			case "read_dvks":
				read_dvks();
				break;
			default:
				run_download_single(id);
				break;
		}
	}

	@Override
	public void done(String id) {
		get_start_gui().get_main_pbar().set_progress(false, false, 0, 0);
		get_start_gui().get_secondary_pbar().set_progress(false, false, 0, 0);
		if(get_start_gui().get_base_gui().is_canceled()) {
			get_start_gui().append_console("canceled", true);
			if(id.equals("read_dvks")) {
				this.skipped = true;
				create_initial_gui();
			}
		}
		else if(this.failed) {
			get_start_gui().append_console("failed", true);
			this.skipped = true;
			create_initial_gui();
		}
		else {
			get_start_gui().append_console("finished", true);
		}
		if(id.equals("login")) {
			if(!get_skipped()) {
				create_main_gui();
				start_process("read_dvks", true);
			}
			else {
				enable_all();
				get_start_gui().get_base_gui().set_running(false);
				get_start_gui().enable_all();
			}
		}
		else {
			enable_all();
			get_start_gui().get_base_gui().set_running(false);
			get_start_gui().enable_all();
		}
	}
	
	/**
	 * Closes the main dvk_handler.
	 */
	public void close_dvk_handler() {
		if(this.dvk_handler != null) {
			try {
				this.dvk_handler.close();
			}
			catch(DvkException e) {}
		}
	}
}
