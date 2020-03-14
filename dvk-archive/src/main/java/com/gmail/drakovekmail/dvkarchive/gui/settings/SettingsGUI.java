package com.gmail.drakovekmail.dvkarchive.gui.settings;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.gmail.drakovekmail.dvkarchive.file.Start;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * GUI for entering DVK Archive settings.
 * 
 * @author Drakovek
 */
public class SettingsGUI implements DActionEvent {
	
	//TODO SHORTEN THIS CRAP
	
	/**
	 * The currently selected settings category
	 */
	private String category;
	
	/**
	 * Main dialog for the settings GUI
	 */
	private DDialog dialog;
	
	/**
	 * Main panel for housing the settings GUI
	 */
	private JPanel main_pnl;
	
	/**
	 * Panel for holding settings services
	 */
	private JPanel service_pnl;
	
	/**
	 * Parent of the Settings GUI
	 */
	private StartGUI start_gui;
	
	/**
	 * List for showing settings categories
	 */
	private DList cat_list;
	
	/**
	 * GUI for selecting DVK Archive's main GUI theme
	 */
	private ThemeGUI theme_gui;
	
	/**
	 * GUI for selecting the font for DVK Archive
	 */
	private FontGUI font_gui;
	
	/**
	 * Initializes the SettingsGUI.
	 * 
	 * @param start_gui Parent of the SettingsGUI
	 */
	public SettingsGUI(StartGUI start_gui) {
		this.category = new String();
		this.start_gui = start_gui;
		BaseGUI base_gui;
		base_gui = this.start_gui.get_base_gui();
		//CREATE INTERNAL GUIS
		this.theme_gui = new ThemeGUI(base_gui);
		this.font_gui = new FontGUI(base_gui);
		//CREATE SIDE PANEL
		this.cat_list = new DList(base_gui, this, "category", false);
		this.cat_list.set_list(get_categories(), true);
		this.cat_list.always_allow_action();
		DScrollPane cat_scr = new DScrollPane(this.cat_list);
		DLabel cat_lbl = new DLabel(base_gui, this.cat_list, "category");
		JPanel side_pnl = base_gui.get_y_stack(cat_lbl, 0, cat_scr, 1);
		//CREATE BOTTOM PANEL
		JPanel btn_pnl = new JPanel();
		btn_pnl.setLayout(new GridLayout(1, 2,
				base_gui.get_space_size(), 1));
		DButton cancel_btn;
		cancel_btn = new DButton(base_gui, this, "cancel");
		cancel_btn.always_allow_action();
		btn_pnl.add(cancel_btn);
		DButton save_btn;
		save_btn = new DButton(base_gui, this, "save");
		save_btn.always_allow_action();
		btn_pnl.add(save_btn);
		JPanel shift_pnl = base_gui.get_x_stack(
				base_gui.get_x_space(), 1, btn_pnl, 0);
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel btm_pnl = base_gui.get_y_stack(sep, shift_pnl);
		//CREATE SERVICE PANEL
		this.service_pnl = new JPanel();
		this.service_pnl.setLayout(new GridLayout(1, 1));
		//CREATE FULL PANEL
		JPanel full_pnl = new JPanel();
		full_pnl.setLayout(new BorderLayout());
		full_pnl.add(base_gui.get_spaced_panel(btm_pnl, 1, 0, true, false, false, false), BorderLayout.SOUTH);
		full_pnl.add(base_gui.get_spaced_panel(side_pnl, 0, 1, false, false, false, true), BorderLayout.WEST);
		full_pnl.add(this.service_pnl, BorderLayout.CENTER);
		this.main_pnl = base_gui.get_spaced_panel(full_pnl);
	}

	/**
	 * Returns list of settings categories.
	 * 
	 * @return List of settings categories
	 */
	public static String[] get_categories() {
		String[] cats = {"language", "theme", "font"};
		return cats;
	}

	/**
	 * Opens the settings GUI.
	 */
	public void open() {
		this.start_gui.get_base_gui().set_running(true);
		String title = this.start_gui.get_base_gui().get_language_string("settings");
		title = LanguageHandler.get_text(title);
		this.dialog = new DDialog(this.start_gui.get_frame(), this.main_pnl, title);
		this.dialog.setVisible(true);
		this.dialog = null;
		this.start_gui.get_base_gui().set_running(false);
	}
	
	/**
	 * Changes the category of the settings to edit.
	 */
	public void change_category() {
		int sel = this.cat_list.getSelectedIndex();
		if(sel != -1) {
			//GET CATEGORY
			String cat = get_categories()[sel];
			if(!cat.equals(this.category)) {
				this.category = cat;
				this.service_pnl.removeAll();
				switch(cat) {
					case "font":
						this.service_pnl.add(this.font_gui);
						break;
					case "theme":
						this.service_pnl.add(this.theme_gui);
						break;
				}
				this.service_pnl.revalidate();
				this.service_pnl.repaint();
			}
		}
	}
	
	/**
	 * Saves all the edited settings.
	 * Restarts program if necessary.
	 */
	private void save() {
		boolean tchange = this.theme_gui.save();
		boolean fchange = this.font_gui.save();
		this.dialog.dispose();
		if(tchange || fchange) {
			//RESTART
			this.start_gui.get_frame().dispose();
			Start.start();
		}
	}

	@Override
	public void event(String id) {
		switch(id) {
			case "category":
				change_category();
				break;
			case "cancel":
				this.dialog.dispose();
				break;
			case "save":
				save();
				break;
		}
	}
}
