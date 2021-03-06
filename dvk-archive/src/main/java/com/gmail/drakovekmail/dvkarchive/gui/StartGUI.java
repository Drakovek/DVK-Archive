package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.gui.artist.DeviantArtGUI;
import com.gmail.drakovekmail.dvkarchive.gui.artist.FurAffinityGUI;
import com.gmail.drakovekmail.dvkarchive.gui.artist.InkbunnyGUI;
import com.gmail.drakovekmail.dvkarchive.gui.error.MissingMediaGUI;
import com.gmail.drakovekmail.dvkarchive.gui.error.SameIDsGUI;
import com.gmail.drakovekmail.dvkarchive.gui.error.UnlinkedMediaGUI;
import com.gmail.drakovekmail.dvkarchive.gui.reformat.ReformatDvksGUI;
import com.gmail.drakovekmail.dvkarchive.gui.reformat.RenameFilesGUI;
import com.gmail.drakovekmail.dvkarchive.gui.settings.SettingsBarGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DComboBox;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DFrame;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DMenu;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DMenuItem;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DProgressBar;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextArea;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.work.DSwingWorker;
import com.gmail.drakovekmail.dvkarchive.gui.work.DWorker;

/**
 * Class dealing with the main GUI.
 * Starts with the DVK Archive program.
 * 
 * @author Drakovek
 *
 */
public class StartGUI implements DActionEvent, Disabler, DWorker {
	
	/**
	 * Current directory for the StartGUI
	 */
	private File directory;
	
	/**
	 * Settings bar for the StartGUI
	 */
	private SettingsBarGUI settings_bar;
	
	/**
	 * Main frame of the StartGUI
	 */
	private DFrame frame;
	
	/**
	 * ComboBox for selecting service categories
	 */
	private DComboBox cat_box;
	
	/**
	 * List for selecting services.
	 */
	private DList service_list;
	
	/**
	 * File menu
	 */
	private DMenu file_menu;
	
	/**
	 * Button used for canceling processes
	 * Also clears the console log
	 */
	private DButton cancel_btn;
	
	/**
	 * BaseGUI for getting UI settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * FilePrefs for getting file settings
	 */
	private FilePrefs file_prefs;
	
	/**
	 * DTextArea used as the console log
	 */
	private DTextArea console;
	
	/**
	 * ScrollPane that holds the console log
	 */
	DScrollPane console_scr;
	
	/**
	 * Main progress bar for the GUI
	 */
	private DProgressBar main_pbar;
	
	/**
	 * Secondary progress bar for the GUI
	 */
	private DProgressBar sec_pbar;
	
	/**
	 * ID of the currently selected service
	 */
	private String current_service;
	
	/**
	 * Panel for holding service_pnl
	 */
	private JPanel content_pnl;
	
	/**
	 * Panel containing GUI elements for the current service
	 */
	private ServiceGUI service_pnl;
	
	/**
	 * Creates the Start GUI.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param show_gui Whether or not to display the GUI
	 */
	public StartGUI(BaseGUI base_gui, boolean show_gui) {
		//INITIALIZE INSTANCE VARIABLES
		this.base_gui = base_gui;
		this.file_prefs = new FilePrefs();
		this.current_service = new String();
		this.frame = new DFrame(this.base_gui, this, "dvk_archive");
		//CREATE SETTINGS BAR
		this.settings_bar = new SettingsBarGUI(this);
		this.frame.getContentPane().add(this.settings_bar,
				BorderLayout.SOUTH);
		//CREATE CATEGORY PANEL
		this.cat_box = new DComboBox(
				this.base_gui, this,
				get_categories(true),
				"category");
		DLabel cat_lbl = new DLabel(this.base_gui, this.cat_box, "category");
		JPanel cat_pnl = this.base_gui.get_y_stack(cat_lbl, this.cat_box);
		//CREATE SERVICE PANEL
		this.service_list = new DList(this.base_gui, this, "service", false);
		update_services();
		DScrollPane serve_scr;
		serve_scr = new DScrollPane(this.service_list);
		DLabel serve_lbl = new DLabel(this.base_gui, this.service_list, "service");
		JPanel serve_pnl = this.base_gui.get_y_stack(serve_lbl, 0, serve_scr, 1);
		//CREATE SIDE PANEL
		JPanel prog_pnl = this.base_gui.get_y_stack(cat_pnl, 0, serve_pnl, 1);
		JPanel side_pnl = this.base_gui.get_spaced_panel(
				prog_pnl, 0, 1, true, true, true, false);
		this.frame.getContentPane().add(side_pnl, BorderLayout.WEST);
		//CREATE PROGRESS BAR
		this.cancel_btn = new DButton(this.base_gui, this, "clear");
		this.cancel_btn.always_allow_action();
		this.main_pbar = new DProgressBar(this.base_gui);
		this.sec_pbar = new DProgressBar(this.base_gui);
		JPanel bar_pnl = new JPanel();
		bar_pnl.setLayout(new GridLayout(1, 2, this.base_gui.get_space_size(), 0));
		bar_pnl.add(this.sec_pbar);
		bar_pnl.add(this.main_pbar);
		JPanel cancel_pnl = this.base_gui.get_x_stack(
				bar_pnl, 1, this.cancel_btn, 0);
		//CREATE CONSOLE LOG
		DLabel console_lbl = new DLabel(this.base_gui, null, "console_log");
		console_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel console_pnl = new JPanel();
		this.console = new DTextArea(this.base_gui);
		this.console.setLineWrap(false);
		this.console_scr = new DScrollPane(this.console);
		this.console.setText("");
		console_pnl = this.base_gui.get_y_stack(
				console_lbl, 0, this.console_scr, 1);
		JPanel log_pnl = this.base_gui.get_y_stack(
				console_pnl, 1, cancel_pnl, 0);
		//CREATE CENTER PANEL
		this.content_pnl = new JPanel();
		this.content_pnl.setLayout(new GridLayout(1, 1));
		Dimension space = new Dimension(1, base_gui.get_space_size() * 30);
		this.content_pnl.add(Box.createRigidArea(space));
		JSplitPane spl = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				this.content_pnl, log_pnl);
		this.frame.getContentPane().add(
				this.base_gui.get_spaced_panel(spl),
				BorderLayout.CENTER);
		//CREATE MENU BAR
		JMenuBar menu_bar = new JMenuBar();
		this.file_menu = new DMenu(this.base_gui, "file");
		DMenuItem open_mit = new DMenuItem(this.base_gui, this, "open");
		DMenuItem exit_mit = new DMenuItem(this.base_gui, this, "exit");
		this.file_menu.add(open_mit);
		this.file_menu.addSeparator();
		this.file_menu.add(exit_mit);
		menu_bar.add(this.file_menu);
		this.frame.setJMenuBar(menu_bar);
		//PACK AND CREATE FRAME
		reset_directory();
		if(show_gui) {
			this.service_list.setSelectedIndex(0);
			this.frame.pack_restricted();
			this.frame.setLocationRelativeTo(null);
			this.frame.setVisible(true);
		}
	}
	
	/**
	 * Returns a String array of available service categories.
	 * 
	 * @param use_language Whether to use language strings instead of IDs.
	 * @return String array of service categories
	 */
	public String[] get_categories(boolean use_language) {
		String[] categories = new String[4];
		categories[0] = "artist_hosting";
		categories[1] = "comics";
		categories[2] = "error_finding";
		categories[3] = "reformatting";
		//CHANGE TO LANGUAGE VALUES
		if(use_language) {
			for(int i = 0; i < categories.length; i++) {
				categories[i] = this.base_gui.get_language_string(categories[i]);
			}
		}
		return categories;
	}
	
	/**
	 * Returns a String array of available services for a given category.
	 * 
	 * @param category Given category ID
	 * @return String array of available services
	 */
	public static String[] get_services(String category) {
		String[] services = new String[0];
		switch(category) {
			case "artist_hosting":
				services = new String[3];
				services[0] = "deviantart";
				services[1] = "fur_affinity";
				services[2] = "inkbunny";
				break;
			case "comics":
				services = new String[1];
				services[0] = "mangadex";
				break;
			case "error_finding":
				services = new String[3];
				services[0] = "same_ids";
				services[1] = "missing_media";
				services[2] = "unlinked_media";
				break;
			case "reformatting":
				services = new String[2];
				services[0] = "reformat_dvks";
				services[1] = "rename_files";
				break;
		}
		//CHANGE TO LANGUAGE VALUES
		return services;
	}
	
	/**
	 * Updates the service list to reflect the current category.
	 */
	public void update_services() {
		String cat;
		int index = this.cat_box.getSelectedIndex();
		cat = get_categories(false)[index];
		String[] services = get_services(cat);
		this.service_list.set_list(services, true);
	}
	
	/**
	 * Changes the current service.
	 */
	public void change_service() {
		int selected = this.service_list.getSelectedIndex();
		if(selected != -1) {
			//DETERMINE SERVICE SELECTED
			String cat;
			int index = this.cat_box.getSelectedIndex();
			cat = get_categories(false)[index];
			String service = get_services(cat)[selected];
			if(!this.current_service.equals(service)) {
				this.base_gui.set_running(true);
				disable_all();
				if(this.service_pnl != null) {
					this.service_pnl.disable_all();
				}
				this.main_pbar.set_progress(true, false, 0, 0);
				DSwingWorker worker = new DSwingWorker(this, service);
				worker.execute();
			}
		}
	}
	
	/**
	 * Appends text to the console log.
	 * 
	 * @param text Text to append.
	 * @param is_id Whether text is a language ID
	 */
	public void append_console(String text, boolean is_id) {
		this.console.append_text(text, is_id);
		this.console_scr.bottom_left();
	}
	
	/**
	 * Returns the GUI's main progress bar.
	 * 
	 * @return Main Progress bar
	 */
	public DProgressBar get_main_pbar() {
		return this.main_pbar;
	}
	
	/**
	 * Returns the GUI's secondary progress bar.
	 * 
	 * @return Secondary Progress bar
	 */
	public DProgressBar get_secondary_pbar() {
		return this.sec_pbar;
	}

	/**
	 * Exits the program, disposing the main frame.
	 */
	public void exit() {
		this.frame.dispose();
		this.frame = null;
	}
	
	/**
	 * Resets the current directory to null.
	 */
	public void reset_directory() {
		this.directory = null;
		this.settings_bar.set_directory(get_directory());
	}
	
	/**
	 * Sets the current directory.
	 * 
	 * @param dir Given directory
	 */
	public void set_directory(File dir) {
		if(dir != null && dir.isDirectory()) {
			this.directory = dir;
			this.settings_bar.set_directory(get_directory());
		}
	}

	/**
	 * Returns the current directory.
	 * 
	 * @return Current directory
	 */
	public File get_directory() {
		return this.directory;
	}
	
	/**
	 * Returns the BaseGUI object for this object.
	 * 
	 * @return BaseGUI
	 */
	public BaseGUI get_base_gui() {
		return this.base_gui;
	}
	
	/**
	 * Returns the main frame of StartGUI object.
	 * 
	 * @return DFrame
	 */
	public DFrame get_frame() {
		return this.frame;
	}
	
	/**
	 * Returns the FilePrefs object for this object.
	 * 
	 * @return FilePrefs
	 */
	public FilePrefs get_file_prefs() {
		return this.file_prefs;
	}
	
	/**
	 * Opens a file dialog to select the current directory.
	 */
	public void open() {
		JFileChooser fc = new JFileChooser();
		if(get_directory() != null) {
			fc.setCurrentDirectory(get_directory());
		}
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int value = fc.showOpenDialog(this.frame);
		if(value == JFileChooser.APPROVE_OPTION) {
			set_directory(fc.getSelectedFile());
			this.service_pnl.directory_opened();
		}
	}
	
	/**
	 * Runs when the cancel/clear button is pressed.
	 * When process is running, cancels the process.
	 * Otherwise, clears the console log.
	 */
	public void cancel_clear() {
		if(this.base_gui.is_running()) {
			this.main_pbar.set_progress(true, false, 0, 0);
			this.base_gui.set_canceled(true);
			append_console("canceling", true);
		}
		else {
			this.console.setText("");
		}
	}

	@Override
	public void event(String id) {
		switch(id) {
			case "open":
				open();
				break;
			case "exit":
				exit();
				break;
			case "category":
				update_services();
				break;
			case "service":
				change_service();
				break;
			case "clear":
				cancel_clear();
				break;
			case "close_frame":
				close();
				break;
		}
	}
	
	/**
	 * Closes the StartGUI.
	 */
	public void close() {
		if(this.service_pnl != null) {
			this.service_pnl.close();
		}
		this.frame.close();
	}

	@Override
	public void enable_all() {
		this.settings_bar.enable_all();
		this.cat_box.setEnabled(true);
		this.service_list.setEnabled(true);
		this.file_menu.setEnabled(true);
		this.cancel_btn.set_text_id("clear");
	}

	@Override
	public void disable_all() {
		this.settings_bar.disable_all();
		this.cat_box.setEnabled(false);
		this.service_list.setEnabled(false);
		this.file_menu.setEnabled(false);
		this.cancel_btn.set_text_id("cancel");
	}

	@Override
	public void run(String id) {
		//CHANGE SERVICE GUI
		if(this.service_pnl != null) {
			this.service_pnl.close();
		}
		this.service_pnl = null;
		this.base_gui.set_canceled(true);
		switch(id) {
			case "fur_affinity":
				this.service_pnl = new FurAffinityGUI(this);
				break;
			case "deviantart":
				this.service_pnl = new DeviantArtGUI(this);
				break;
			case "inkbunny":
				this.service_pnl = new InkbunnyGUI(this);
				break;
			case "unlinked_media":
				this.service_pnl = new UnlinkedMediaGUI(this);
				break;
			case "same_ids":
				this.service_pnl = new SameIDsGUI(this);
				break;
			case "missing_media":
				this.service_pnl = new MissingMediaGUI(this);
				break;
			case "reformat_dvks":
				this.service_pnl = new ReformatDvksGUI(this);
				break;
			case "rename_files":
				this.service_pnl = new RenameFilesGUI(this);
				break;
		}
		JPanel spaced = this.base_gui.get_spaced_panel(this.service_pnl);
		this.content_pnl.removeAll();
		this.content_pnl.add(spaced);
		this.content_pnl.revalidate();
		this.content_pnl.repaint();
		this.current_service = id;
	}

	@Override
	public void done(String id) {
		enable_all();
		this.service_pnl.enable_all();
		this.main_pbar.set_progress(false, false, 0, 0);
		this.base_gui.set_running(false);
	}
}
