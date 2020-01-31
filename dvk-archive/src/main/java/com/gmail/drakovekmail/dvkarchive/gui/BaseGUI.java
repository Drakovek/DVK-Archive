package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.Font;
import java.util.prefs.Preferences;

/**
 * Class containing methods for use by the program's GUI elements.
 * 
 * @author Drakovek
 */
public class BaseGUI {
	
	/**
	 * Key for font size in preferences.
	 */
	private static final String FONT_SIZE = "font_size";
	
	/**
	 * Key for font family in preferences.
	 */
	private static final String FONT_FAMILY = "font_family";
	
	/**
	 * Key for whether the font is bold in preferences.
	 */
	private static final String FONT_BOLD = "font_bold";
	
	/**
	 * Default font for the program.
	 */
	private Font font;
	
	/**
	 * Initializes the BaseGUI by loading preferences.
	 */
	public BaseGUI() {
		load_preferences();
	}
	
	/**
	 * Saves GUI preferences.
	 */
	public void save_preferences() {
		Preferences prefs = Preferences.userNodeForPackage(BaseGUI.class);
		//SET FONT
		prefs.put(FONT_FAMILY, get_font().getFamily());
		prefs.putBoolean(FONT_BOLD, get_font().isBold());
		prefs.putInt(FONT_SIZE, get_font().getSize());
	}
	
	/**
	 * Loads GUI preferences.
	 */
	public void load_preferences() {
		Preferences prefs = Preferences.userNodeForPackage(BaseGUI.class);
		//SET FONT
		String family = prefs.get(FONT_FAMILY, "Dialog").toString();
		boolean bold = prefs.getBoolean(FONT_BOLD, false);
		int size = prefs.getInt(FONT_SIZE, 12);
		set_font(family, size, bold);
	}
	
	/**
	 * Sets the default font for the program.
	 * 
	 * @param family Font family
	 * @param size Font size
	 * @param is_bold Wheter the font is bold
	 */
	public void set_font(String family, int size, boolean is_bold) {
		int font_type = Font.PLAIN;
		if(is_bold) {
			font_type = Font.BOLD;
		}
		this.font = new Font(family, font_type, size);
		save_preferences();
	}
	
	/**
	 * Returns the default font for the program.
	 * 
	 * @return Default font
	 */
	public Font get_font() {
		return this.font;
	}
}
