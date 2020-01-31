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
	 * Key for font size in preferences
	 */
	private static final String FONT_SIZE = "font_size";
	
	/**
	 * Key for font family in preferences
	 */
	private static final String FONT_FAMILY = "font_family";
	
	/**
	 * Key for whether the font is bold in preferences
	 */
	private static final String FONT_BOLD = "font_bold";
	
	/**
	 * Key for whether fonts should be anti-aliased
	 */
	private static final String AA = "aa";
	
	/**
	 * Key for the look and feel(theme) of the GUI
	 */
	private static final String THEME = "theme";
	
	/**
	 * Default font for the program
	 */
	private Font font;
	
	/**
	 * Whether fonts should be anti-aliased
	 */
	private boolean aa;
	
	/**
	 * Look and feel(theme) for the GUI
	 */
	private String theme;
	
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
		//SET THEME
		prefs.put(THEME, get_theme());
		//SET ANTI-ALIASING
		prefs.putBoolean(AA, use_aa());
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
		//SET THEME
		set_theme(prefs.get(THEME, "Metal"));
		//SET ANTIALIASING
		set_use_aa(prefs.getBoolean(AA, true));
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
	}
	
	/**
	 * Returns the default font for the program.
	 * 
	 * @return Default font
	 */
	public Font get_font() {
		return this.font;
	}
	
	/**
	 * Sets whether fonts should be anti-aliased.
	 * 
	 * @param aa Whether to use anti-aliasing
	 */
	public void set_use_aa(boolean aa) {
		this.aa = aa;
	}
	
	/**
	 * Returns whether fonts should be anti-aliased.
	 * 
	 * @return Whether to use anti-aliasing.
	 */
	public boolean use_aa() {
		return this.aa;
	}
	
	/**
	 * Sets the look and feel(theme) for the GUI.
	 * 
	 * @param theme Theme of the GUI
	 */
	public void set_theme(String theme) {
		if(theme == null) {
			this.theme = new String();
		}
		else {
			this.theme = theme;
		}
	}
	
	/**
	 * Returns the look and feel(theme) for the GUI.
	 * 
	 * @return Theme of the GUI.
	 */
	public String get_theme() {
		return this.theme;
	}
}
