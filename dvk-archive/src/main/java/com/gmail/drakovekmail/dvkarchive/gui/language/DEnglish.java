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
		this.lang_keys.put("canceling", "Cancelling...");
		this.lang_keys.put("clear", "^Clear");
		this.lang_keys.put("console_log", "Console Log");
		this.lang_keys.put("run", "^Run");
		this.lang_keys.put("file", "^File");
		this.lang_keys.put("open", "^Open");
		this.lang_keys.put("exit", "^Exit");
		this.lang_keys.put("finished", "[FINISHED]");
		this.lang_keys.put("canceled", "[CANCELED]");
		this.lang_keys.put("reading_dvks", "Reading DVK Files...");
		this.lang_keys.put("sorting_dvks", "Sorting DVK Objects...");
		this.lang_keys.put("select_all", "[SELECT_ALL]");
		//SETTINGS
		this.lang_keys.put("no_dir_select", "(No Directories Selected)");
		this.lang_keys.put("settings", "^Settings");
		//PROGRAM CATEGORIES
		this.lang_keys.put("artist_hosting", "Artist Hosting");
		this.lang_keys.put("error_finding", "Error Finding");
		this.lang_keys.put("comics", "Comics");
		//ARTIST HOSTING
		this.lang_keys.put("deviantart", "DeviantArt");
		this.lang_keys.put("fur_affinity", "Fur Affinity");
		this.lang_keys.put("inkbunny", "Inkbunny");
		this.lang_keys.put("transfur", "Transfur");
		this.lang_keys.put("username", "^Username:");
		this.lang_keys.put("password", "^Password:");
		this.lang_keys.put("captcha", "^Captcha:");
		this.lang_keys.put("login", "^Login");
		this.lang_keys.put("skip_login", "S^kip Login");
		this.lang_keys.put("refresh_captcha", "^Refresh Captcha");
		this.lang_keys.put("check_all", "Check A^ll");
		this.lang_keys.put("check_new", "Check ^New");
		this.lang_keys.put("download_single", "^Download Single");
		this.lang_keys.put("refresh", "^Refresh");
		this.lang_keys.put("artists", "^Artists");
		//ERROR FINDING
		this.lang_keys.put("same_ids", "Same IDs");
		this.lang_keys.put("missing_media", "Missing Media");
		this.lang_keys.put("unlinked_media", "Unlinked Media");
		//COMICS
		this.lang_keys.put("mangadex", "MangaDex");
		//UNLINKED MEDIA
		this.lang_keys.put("unlinked_title", "Find Unlinked Media");
		this.lang_keys.put("unlinked_console", "[FINDING UNLINKED MEDIA]");
		String desc = "Finds media files in DVK directories that ";
		desc = desc + "do not have a corresponding DVK file.";
		this.lang_keys.put("unlinked_desc", desc);
	}
}
