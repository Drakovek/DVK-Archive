package com.gmail.drakovekmail.dvkarchive.gui.language;

import java.util.HashMap;

/**
 * Class for handling key pairs for language Strings.
 * 
 * @author Drakovek
 */
public abstract class LanguageMap {
	
	/**
	 * HashMap of keys and their corresponding language Strings.
	 */
	protected HashMap<String, String> lang_keys;
	
	/**
	 * Returns the language String for a given key.
	 * 
	 * @param key Given key
	 * @return Language String
	 */
	public String get_language_string(String key) {
		String lang = this.lang_keys.get(key);
		if(lang == null) {
			return new String();
		}
		return lang;
	}
}
