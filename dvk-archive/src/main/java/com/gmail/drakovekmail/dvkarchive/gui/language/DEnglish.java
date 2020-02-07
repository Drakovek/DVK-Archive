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
		//MAIN GUI
		this.lang_keys.put("dvk_archive", "DVK Archive");
		this.lang_keys.put("category", "Ca^tegory");
		this.lang_keys.put("service", "S^ervice");
		this.lang_keys.put("cancel", "^Cancel");
		this.lang_keys.put("console_log", "Console Log");
		this.lang_keys.put("run", "^Run");
		this.lang_keys.put("file", "^File");
		this.lang_keys.put("open", "^Open");
		this.lang_keys.put("exit", "^Exit");
		//SETTINGS
		this.lang_keys.put("no_dir_select", "(No Directories Selected)");
		this.lang_keys.put("settings", "^Settings");
		//PROGRAM CATEGORIES
		this.lang_keys.put("artist_hosting", "Artist Hosting");
		this.lang_keys.put("error_finding", "Error Finding");
		//ARTIST HOSTING
		this.lang_keys.put("deviantart", "DeviantArt");
		this.lang_keys.put("fur_affinity", "Fur Affinity");
		this.lang_keys.put("inkbunny", "Inkbunny");
		this.lang_keys.put("transfur", "Transfur");
		//ERROR FINDING
		this.lang_keys.put("same_ids", "Same IDs");
		this.lang_keys.put("missing_media", "Missing Media");
		this.lang_keys.put("unlinked_media", "Unlinked Media");
		//UNLINKED MEDIA
		this.lang_keys.put("unlinked_title", "     Find Unlinked Media     ");
		String desc = "Finds media files in DVK directories that ";
		desc = desc + "do not have a corresponding DVK file.";
		this.lang_keys.put("unlinked_desc", desc);
	}
}
