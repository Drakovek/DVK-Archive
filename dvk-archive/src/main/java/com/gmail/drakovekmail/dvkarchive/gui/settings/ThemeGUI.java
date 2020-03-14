package com.gmail.drakovekmail.dvkarchive.gui.settings;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * GUI for selecting the main GUI theme for DVK Archive.
 * 
 * @author Drakovek
 */
public class ThemeGUI extends JPanel implements DActionEvent {
	
	//TODO SELECT CURRENT THEME AT STARTUP
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 5993120599127241821L;

	/**
	 * Saved theme
	 */
	private String start_theme;
	
	/**
	 * Currently selected theme
	 */
	private String current_theme;
	
	/**
	 * List of themes
	 */
	private LookAndFeelInfo[] themes;
	
	/**
	 * BaseGUI for UI Settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * List for selecting themes
	 */
	private DList theme_lst;

	/**
	 * Initializes the ThemeGUI
	 * 
	 * @param base_gui BaseGUI for UI settings
	 */
	public ThemeGUI(BaseGUI base_gui) {
		//GET THEMES
		this.start_theme = base_gui.get_theme();
		this.current_theme = this.start_theme;
		this.themes = UIManager.getInstalledLookAndFeels();
		//CREATE GUI
		this.base_gui = base_gui;
		this.theme_lst = new DList(base_gui, this, "theme", false);
		this.theme_lst.always_allow_action();
		this.theme_lst.set_list(get_theme_names(), false);
		DScrollPane theme_scr = new DScrollPane(this.theme_lst);
		DLabel theme_lbl = new DLabel(base_gui, this.theme_lst, "theme");
		theme_lbl.set_font_large();
		setLayout(new GridLayout(1, 1));
		add(base_gui.get_y_stack(theme_lbl, 0, theme_scr, 1));
	}

	/**
	 * Returns an array of names for the available themes.
	 * 
	 * @return names of the available themes.
	 */
	private String[] get_theme_names() {
		String[] strings = new String[this.themes.length];
		for(int i = 0; i < strings.length; i++) {
			strings[i] = this.themes[i].getName();
		}
		return strings;
	}
	
	/**
	 * Sets the current theme.
	 * Based on the theme selected in the theme list.
	 */
	private void selected() {
		int sel = this.theme_lst.getSelectedIndex();
		if(sel != -1) {
			this.current_theme = this.themes[sel].getClassName();
		}
	}
	
	/**
	 * Saves theme settings, if changed.
	 * 
	 * @return Whether the theme setting changed
	 */
	public boolean save() {
		boolean changed = !this.start_theme.equals(this.current_theme);
		if(changed) {
			this.base_gui.set_theme(this.current_theme);
			this.base_gui.save_preferences();
		}
		return changed;
	}
	
	@Override
	public void event(String id) {
		selected();
	}
}
