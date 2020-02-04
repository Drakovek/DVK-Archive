package com.gmail.drakovekmail.dvkarchive.gui.language;

import java.util.HashMap;

/**
 * Class for handling key pairs for English language Strings.
 * 
 * @author Drakovek
 */
public class DEnglish extends LanguageMap{
	
	/**
	 * Sets up the English key pairs.
	 */
	public DEnglish() {
		this.lang_keys = new HashMap<>();
		this.lang_keys.put("dvk_archive", "DVK Archive");
		this.lang_keys.put("file", "^File");
	}
}
