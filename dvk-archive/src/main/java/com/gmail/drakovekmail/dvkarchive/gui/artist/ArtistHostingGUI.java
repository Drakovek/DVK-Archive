package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
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
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * GUI for downloading files from artist-hosting websites.
 * 
 * @author Drakovek
 */
public abstract class ArtistHostingGUI extends ServiceGUI implements DActionEvent {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 7605219333024907810L;

	/**
	 * Name of the current artist-hosting service.
	 */
	private String name;
	
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
		//INITIALIZE COMPONENTS
		BaseGUI base_gui = start_gui.get_base_gui();
		this.all_btn = new DButton(base_gui, this, "check_all");
		this.new_btn = new DButton(base_gui, this, "check_new");
		this.single_btn = new DButton(base_gui, this, "download_single");
		this.refresh_btn = new DButton(base_gui, this, "refresh");
	}
	
	/**
	 * Creates and displays a login GUI.
	 */
	protected void create_login_gui() {
		BaseGUI base_gui = this.start_gui.get_base_gui();
		DTextField u_txt = new DTextField(base_gui);
		DTextField p_txt = new DTextField(base_gui);
		DLabel u_lbl = new DLabel(base_gui, u_txt, "username");
		DLabel p_lbl = new DLabel(base_gui, p_txt, "password");
		//CREATE INPUT PANEL
		JPanel usr_pnl = base_gui.get_x_stack(u_lbl, 0, u_txt, 1);
		JPanel pass_pnl = base_gui.get_x_stack(p_lbl, 0, p_txt, 1);
		JPanel in_pnl = base_gui.get_y_stack(usr_pnl, pass_pnl);
		//CREATE BOTTOM PANEL
		DButton login_btn = new DButton(base_gui, this, "login");
		DButton skip_btn = new DButton(base_gui, this, "skip_login");
		JPanel btn_pnl = base_gui.get_y_stack(login_btn, skip_btn);
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
		//CREATE FULL PANEL
		JPanel full_pnl = base_gui.get_y_stack(top_pnl, btm_pnl);
		//CREATE CENTER PANEL
		JPanel center_pnl = base_gui.get_spaced_panel(full_pnl, 0, 0, false, false, false, false);
		//UPDATE MAIN PANEL
		this.removeAll();
		this.add(center_pnl);
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Creates the main artist hosting GUI.
	 */
	public void create_main_gui() {
		BaseGUI base_gui = this.start_gui.get_base_gui();
		//CREATE BUTTON PANEL
		JPanel btn_pnl = new JPanel();
		btn_pnl.setLayout(new GridLayout(
				3, 1, 0, base_gui.get_space_size()));
		btn_pnl.add(this.all_btn);
		btn_pnl.add(this.new_btn);
		btn_pnl.add(this.single_btn);
		//CREATE SIDE PANEL
		this.lst = new DList(base_gui, this, "list", true);
		DScrollPane scr = new DScrollPane(this.lst);
		DLabel art_lbl = new DLabel(base_gui, this.lst, "artists");
		art_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel art_pnl = base_gui.get_y_stack(art_lbl, 0, scr, 1);
		JPanel side_panel = base_gui.get_y_stack(
				art_pnl, 1, this.refresh_btn, 0);
		//CREATE SPLIT PANEL
		JPanel split_pnl = new JPanel();
		split_pnl.setLayout(
				new GridLayout(1, 2, base_gui.get_space_size(), 0));
		split_pnl.add(btn_pnl);
		split_pnl.add(side_panel);
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
			this.start_gui.get_progress_bar()
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
	private void page_runner(boolean check_all) {
		int[] sel = get_selected();
		for(int i = 0; i < sel.length; i++) {
			if(!this.start_gui.get_base_gui().is_canceled()) {
				get_pages(this.dvks.get(sel[i]), check_all);
			}
		}
	}

	/**
	 * Gets pages for a given artist/title.
	 * 
	 * @param dvk Dvk with title/artist and directory info.
	 * @param check_all Whether to check all pages
	 */
	public abstract void get_pages(Dvk dvk, boolean check_all);
	
	/**
	 * Sorts the DVKs in the DvkHandler.
	 */
	public abstract void sort_dvks();
	
	@Override
	public void enable_all() {
		this.new_btn.setEnabled(true);
		this.all_btn.setEnabled(true);
		this.single_btn.setEnabled(true);
		this.refresh_btn.setEnabled(true);
		this.lst.setEnabled(true);
	}

	@Override
	public void disable_all() {
		this.new_btn.setEnabled(false);
		this.all_btn.setEnabled(false);
		this.single_btn.setEnabled(false);
		this.refresh_btn.setEnabled(false);
		this.lst.setEnabled(false);
	}
	
	@Override
	public void event(String id) {
		switch(id) {
			case "login":
				System.out.println("login");
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
		}
	}
	
	@Override
	public void run(String id) {
		switch(id) {
			case "check_new":
				page_runner(false);
				break;
			case "check_all":
				page_runner(true);
				break;
			case "read_dvks":
				this.start_gui.get_progress_bar()
					.set_progress(true, false, 0, 0);
				read_dvks();
				break;
		}
	}

	@Override
	public void done(String id) {
		switch(id) {
			case "check_new":
			case "check_all":
			case "read_dvks":
				this.start_gui.get_progress_bar()
					.set_progress(false, false, 0, 0);
				if(this.start_gui.get_base_gui().is_canceled()) {
					this.start_gui.append_console("canceled", true);
				}
				else {
					this.start_gui.append_console("finished", true);
				}
				this.start_gui.get_base_gui().set_running(false);
				this.start_gui.enable_all();
				enable_all();
				break;
		}
	}
}
