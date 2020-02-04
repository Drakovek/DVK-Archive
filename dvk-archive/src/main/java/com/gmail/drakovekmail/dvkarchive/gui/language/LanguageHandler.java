package com.gmail.drakovekmail.dvkarchive.gui.language;

import java.util.prefs.Preferences;

import javax.swing.KeyStroke;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Class for getting text values for the UI.
 * Deals with handling text for various languages.
 * 
 * @author Drakovek
 */
public class LanguageHandler {
	
	/**
	 * Key for the selected language in preferences.
	 */
	private static final String LANGUAGE = "language";
	
	/**
	 * LanguageMap object for the selected language.
	 */
	private LanguageMap language_map;
	
	/**
	 * Initializes LanguageHandler by loading language preferences.
	 */
	public LanguageHandler() {
		String lang = get_language();
		set_language(lang);
	}
	
	/**
	 * Returns the pure text value of a language String.
	 * Language string has a carrot(^) before the mnemonic.
	 * Removes the mnemonic carrot.
	 * 
	 * @param lang_str Language String
	 * @return Pure text from language String
	 */
	public static String get_text(String lang_str) {
		return lang_str.replaceFirst("\\^", "");
	}
	
	/**
	 * Returns the index of the mnemonic character in a language String.
	 * Language string has a carrot(^) before the mnemonic.
	 * Index is relative to pure text given by get_text method.
	 * 
	 * @param lang_str Language String
	 * @return Index of mnemonic character
	 */
	public static int get_mnemonic_index(String lang_str) {
		int index = lang_str.indexOf('^');
		if(index < 0) {
			return 0;
		}
		return index;
	}
	
	/**
	 * Returns the key code for a given character.
	 * 
	 * @param character Given character
	 * @return Key code int
	 */
	public static int get_key_code(char character) {
		return KeyStroke.getKeyStroke(character, 0).getKeyCode();
	}
	
	/**
	 * Sets the program's UI language.
	 * 
	 * @param language UI language
	 */
	public void set_language(String language) {
		String lang;
		//ADD MORE LANGUAGES, IF AVAILABLE
		switch(language) {
			default:
				lang = "English";
				this.language_map = new DEnglish();
				break;
		}
		
		Preferences prefs = Preferences.userNodeForPackage(LanguageHandler.class);
		prefs.put(LANGUAGE, lang);
	}
	
	/**
	 * Returns the name of the UI's language.
	 * 
	 * @return UI Language
	 */
	public static String get_language() {
		Preferences prefs = Preferences.userNodeForPackage(BaseGUI.class);
		String lang = prefs.get(LANGUAGE, "English").toString();
		return lang;
	}
	
	/**
	 * Returns the language String for a given key in the UI's language.
	 * 
	 * @param key Given key
	 * @return Language String
	 */
	public String get_language_string(String key) {
		return this.language_map.get_language_string(key);
	}
}
