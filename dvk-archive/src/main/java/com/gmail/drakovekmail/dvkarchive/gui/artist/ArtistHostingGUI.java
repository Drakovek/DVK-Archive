package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
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
	 * DVK handler for loading existing DVK files.
	 */
	protected DvkHandler dvk_handler;
	
	/**
	 * Dvks with title/artist and directory info.
	 */
	protected ArrayList<Dvk> dvks;
	
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
	 * Refreshes the artist/title list
	 */
	private DButton refresh_btn;
	
	/**
	 * Label for showing CAPTCHA images.
	 */
	private JLabel image_lbl;
	
	/**
	 * Button for reloading CAPTCHA
	 */
	private DButton cap_btn;
	
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
	 * Text field for entering captcha
	 */
	private DTextField c_txt;
	
	/**
	 * Initializes the ArtistHostingGUI object.
	 * 
	 * @param start_gui Parent of ArtistHostingGUI
	 * @param name_id Language ID for the name of current service.
	 */
	public ArtistHostingGUI(StartGUI start_gui, String name_id) {
		super(start_gui);
		this.dvks = new ArrayList<>();
		this.name = start_gui.get_base_gui()
				.get_language_string(name_id);
		this.setLayout(new GridLayout(1, 1));
		this.skipped = true;
		//INITIALIZE COMPONENTS
		BaseGUI base_gui = start_gui.get_base_gui();
		this.all_btn = new DButton(base_gui, this, "check_all");
		this.new_btn = new DButton(base_gui, this, "check_new");
		this.single_btn = new DButton(base_gui, this, "download_single");
		this.refresh_btn = new DButton(base_gui, this, "refresh");
		this.lst = new DList(base_gui, this, "list", true);
		this.cap_btn = new DButton(base_gui, this, "refresh_captcha");
		this.login_btn = new DButton(base_gui, this, "login");
		this.skip_btn = new DButton(base_gui, this, "skip_login");
		//CREATE GUI
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
	 * Creates and displays a login GUI.
	 * 
	 * @param use_captcha Whether to include CAPTCHA dialog in the GUI
	 */
	protected void create_login_gui(boolean use_captcha) {
		load_checks();
		this.start_gui.get_scroll_panel().set_fit(true, false);
		BaseGUI base_gui = this.start_gui.get_base_gui();
		this.u_txt = new DTextField(base_gui, this, "nothing");
		this.p_txt = new DPasswordField(base_gui);
		DLabel u_lbl = new DLabel(base_gui, this.u_txt, "username");
		DLabel p_lbl = new DLabel(base_gui, this.p_txt, "password");
		//CREATE INPUT PANEL
		JPanel usr_pnl = base_gui.get_x_stack(u_lbl, 0, this.u_txt, 1);
		JPanel pass_pnl = base_gui.get_x_stack(p_lbl, 0, this.p_txt, 1);
		JPanel in_pnl = base_gui.get_y_stack(usr_pnl, pass_pnl);
		//CREATE BOTTOM PANEL
		JPanel btn_pnl = base_gui.get_y_stack(
				this.login_btn, this.skip_btn);
		JPanel btm_pnl = base_gui.get_y_stack(in_pnl, btn_pnl);
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
		//CREATE CAPTCHA PANEL
		this.c_txt = new DTextField(base_gui, this, "nothing");
		DLabel c_lbl = new DLabel(base_gui, this.c_txt, "captcha");
		this.image_lbl = new JLabel();
		this.image_lbl.setHorizontalAlignment(
				SwingConstants.CENTER);
		this.image_lbl.setVerticalAlignment(
				SwingConstants.CENTER);
		JPanel c_txt_pnl = base_gui.get_y_stack(
				this.image_lbl,
				base_gui.get_x_stack(c_lbl, 0, this.c_txt, 1));
		JPanel cap_pnl = base_gui.get_y_stack(c_txt_pnl, this.cap_btn);
		//CREATE FULL LOGIN PANEL
		JPanel login_pnl;
		if(use_captcha) {
			login_pnl = base_gui.get_y_stack(
					top_pnl,
					base_gui.get_y_stack(cap_pnl, btm_pnl));
		}
		else {
			login_pnl = base_gui.get_y_stack(top_pnl, btm_pnl);
		}
		//CREATE CENTER PANEL
		JPanel center_pnl = base_gui.get_spaced_panel(login_pnl, 0, 0, false, false, false, false);
		//UPDATE MAIN PANEL
		this.removeAll();
		this.add(center_pnl);
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Creates the initial GUI.
	 * Also called to reset when process is canceled.
	 */
	public abstract void create_initial_gui();
	
	/**
	 * Refreshes the Displayed CAPTCHA image.
	 */
	private void refresh_captcha() {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		this.start_gui.append_console("", false);
		this.start_gui.append_console("loading_captcha", true);
		File captcha = get_captcha();
		if(captcha.exists()) {
			ImageIcon icon = new ImageIcon(captcha.getAbsolutePath());
			this.image_lbl.setIcon(icon);
		}
		else {
			this.start_gui.append_console("captcha_failed", true);
			this.image_lbl.setIcon(null);
		}
	}
	
	/**
	 * Starts the process of logging in to website.
	 */
	private void start_login() {
		this.start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		this.start_gui.append_console("attempt_login", true);
		boolean login = login(
				this.u_txt.getText(),
				this.p_txt.get_text(),
				this.c_txt.getText());
		if(login) {
			this.p_txt.setText("");
			this.start_gui.append_console("login_success", true);
			this.skipped = false;
		}
		else {
			this.start_gui.append_console("login_failed", true);
		}
	}
	
	/**
	 * Attempts login to the artist hosting site.
	 * 
	 * @param username Username
	 * @param password Password
	 * @param captcha Captcha
	 * @return Whether login was successful
	 */
	public abstract boolean login(String username, String password, String captcha);
	
	/**
	 * Returns the file of a downloaded CAPTCHA image.
	 * 
	 * @return File for downloaded CAPTCHA
	 */
	public abstract File get_captcha();
	
	/**
	 * Creates the main artist hosting GUI.
	 */
	public void create_main_gui() {
		this.start_gui.get_scroll_panel().set_fit(true, true);
		BaseGUI base_gui = this.start_gui.get_base_gui();
		//CREATE BUTTON PANEL
		JPanel btn_pnl = new JPanel();
		btn_pnl.setLayout(new GridLayout(
				3, 1, 0, base_gui.get_space_size()));
		this.new_btn.setEnabled(false);
		this.all_btn.setEnabled(false);
		this.single_btn.setEnabled(false);
		btn_pnl.add(this.new_btn);
		btn_pnl.add(this.all_btn);
		btn_pnl.add(this.single_btn);
		//CREATE SIDE PANEL
		DScrollPane scr = new DScrollPane(this.lst);
		DLabel art_lbl = new DLabel(base_gui, this.lst, "artists");
		art_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel art_pnl = base_gui.get_y_stack(art_lbl, 0, scr, 1);
		JPanel side_pnl = base_gui.get_y_stack(
				art_pnl, 1, this.refresh_btn, 0);
		//CREATE CHECK PANEL
		int space = base_gui.get_space_size();
		JPanel check_pnl = new JPanel();
		check_pnl.setLayout(new GridLayout(2, 2, space, space));
		DCheckBox main_chk = new DCheckBox(
				base_gui, this, "main", get_main());
		DCheckBox scraps_chk = new DCheckBox(
				base_gui, this, "scraps", get_scraps());
		DCheckBox journal_chk = new DCheckBox(
				base_gui, this, "journals", get_journals());
		DCheckBox favorite_chk = new DCheckBox(
				base_gui, this, "favorites", get_favorites());
		check_pnl.add(main_chk);
		check_pnl.add(scraps_chk);
		check_pnl.add(journal_chk);
		check_pnl.add(favorite_chk);
		JPanel action_pnl = base_gui.get_y_stack(
				btn_pnl, 1, check_pnl, 0);
		//CREATE SPLIT PANEL
		JPanel split_pnl = new JPanel();
		split_pnl.setLayout(
				new GridLayout(1, 2, base_gui.get_space_size(), 0));
		split_pnl.add(action_pnl);
		split_pnl.add(side_pnl);
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
		this.add(base_gui.get_y_stack(
				top_pnl, 0, split_pnl, 1));
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
	 * Sets the main list to show the given ArrayList.
	 * Adds additional item for selecting all items.
	 * 
	 * @param list List to display
	 */
	public void set_list(ArrayList<String> list) {
		String[] array = new String[list.size() + 1];
		array[0] = this.start_gui.get_base_gui(
				).get_language_string("select_all");
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
	protected void read_dvks() {
		this.dvk_handler = new DvkHandler();
		File[] dirs = {this.start_gui.get_directory()};
		FilePrefs prefs = this.start_gui.get_file_prefs();
		boolean index = prefs.use_index();
		this.dvk_handler.read_dvks(
				dirs, prefs, this.start_gui, index, true, index);
		//SORT DVKS
		if(!this.start_gui.get_base_gui().is_canceled()) {
			this.start_gui.append_console("sorting_dvks", true);
			this.start_gui.get_main_pbar()
				.set_progress(true, false, 0, 0);
			sort_dvks();
		}
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
			sel = new int[this.dvks.size()];
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
		for(int i = 0; i < sel.length; i++) {
			this.start_gui.get_secondary_pbar().set_progress(
					false, true, i, sel.length);
			if(!this.start_gui.get_base_gui().is_canceled()) {
				get_pages(this.dvks.get(sel[i]), check_all);
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
		download_page(url);
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
	 * Sorts the DVKs in the DvkHandler.
	 */
	public abstract void sort_dvks();
	
	@Override
	public void enable_all() {
		if(! this.start_gui.get_base_gui()
				.is_canceled()) {
			this.new_btn.setEnabled(true);
			this.all_btn.setEnabled(true);
			this.single_btn.setEnabled(true);
		}
		this.refresh_btn.setEnabled(true);
		this.lst.setEnabled(true);
		this.cap_btn.setEnabled(true);
		this.login_btn.setEnabled(true);
		this.skip_btn.setEnabled(true);
	}

	@Override
	public void disable_all() {
		this.new_btn.setEnabled(false);
		this.all_btn.setEnabled(false);
		this.single_btn.setEnabled(false);
		this.refresh_btn.setEnabled(false);
		this.lst.setEnabled(false);
		this.cap_btn.setEnabled(false);
		this.login_btn.setEnabled(false);
		this.skip_btn.setEnabled(false);
	}
	
	@Override
	public void event(String id) {
		switch(id) {
			case "refresh_captcha":
				start_process("refresh_captcha", true);
				break;
			case "login":
				start_process("login", true);
				break;
			case "skip_login":
				create_main_gui();
				start_process("read_dvks", true);
				break;
			case "refresh":
				start_process("read_dvks", true);
				break;
			case "check_new":
				start_process("check_new", false);
				break;
			case "check_all":
				start_process("check_all", false);
				break;
			case "download_single":
				if(this.directory_loaded()) {
					String[] messages = {"enter_page_url"};
					DTextDialog dialog = new DTextDialog();
					String url = dialog.open(
							this.start_gui.get_base_gui(),
							this.start_gui.get_frame(),
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
			case "refresh_captcha":
				refresh_captcha();
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
		this.start_gui.get_main_pbar().set_progress(false, false, 0, 0);
		this.start_gui.get_secondary_pbar().set_progress(false, false, 0, 0);
		if(this.start_gui.get_base_gui().is_canceled()) {
			this.start_gui.append_console("canceled", true);
			create_initial_gui();
		}
		else {
			this.start_gui.append_console("finished", true);
		}
		this.start_gui.get_base_gui().set_running(false);
		this.start_gui.enable_all();
		if(id.equals("login")) {
			if(get_skipped()) {
				start_process("refresh_captcha", true);
			}
			else {
				create_main_gui();
				start_process("read_dvks", true);
			}
		}
		enable_all();
	}
}
