package com.gmail.drakovekmail.dvkarchive.gui.language;

import javax.swing.KeyStroke;

/**
 * Class for getting text values for the UI.
 * Deals with handling text for various languages.
 * 
 * @author Drakovek
 */
public class LanguageHandler {
	
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
}
