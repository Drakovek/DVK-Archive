package com.gmail.drakovekmail.dvkarchive.gui.settings;

import java.awt.GridLayout;
import javax.swing.JPanel;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * GUI for selecting the displayed language of DVK Archive.
 * 
 * @author Drakovek
 */
public class LanguageGUI extends JPanel implements DActionEvent {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 9017720315662106326L;

	/**
	 * BaseGUI for UI Settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * Saved language
	 */
	private String start_language;
	
	/**
	 * Currently selected language
	 */
	private String current_language;
	
	/**
	 * List of available languages
	 */
	private String[] languages;
	
	/**
	 * List for selecting language
	 */
	private DList language_lst;
	
	/**
	 * Initializes the LanguageGUI
	 * 
	 * @param base_gui BaseGUI for UI settings
	 */
	public LanguageGUI(BaseGUI base_gui) {
		//GET THEMES
		this.start_language = LanguageHandler.get_language();
		this.current_language = this.start_language;
		this.languages = LanguageHandler.get_languages();
		//CREATE GUI
		this.base_gui = base_gui;
		this.language_lst = new DList(base_gui, this, "language", false);
		this.language_lst.always_allow_action();
		this.language_lst.set_list(this.languages, false);
		DScrollPane language_scr = new DScrollPane(this.language_lst);
		DLabel language_lbl = new DLabel(base_gui, this.language_lst, "language");
		language_lbl.set_font_large();
		setLayout(new GridLayout(1, 1));
		add(base_gui.get_y_stack(language_lbl, 0, language_scr, 1));
	}
	
	/**
	 * Sets the current language.
	 * Based on the language selected in the language list.
	 */
	private void selected() {
		int sel = this.language_lst.getSelectedIndex();
		if(sel != -1) {
			this.current_language = this.languages[sel];
		}
	}
	/**
	 * Saves language settings, if changed.
	 * 
	 * @return Whether the language setting changed
	 */
	public boolean save() {
		boolean changed = !this.start_language.equals(this.current_language);
		if(changed) {
			this.base_gui.get_language_handler().set_language(this.current_language);
		}
		return changed;
	}
	
	@Override
	public void event(String id) {
		selected();
	}
}
