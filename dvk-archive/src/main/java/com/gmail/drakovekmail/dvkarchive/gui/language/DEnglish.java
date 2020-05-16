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
		this.lang_keys.put("ok", "^OK");
		this.lang_keys.put("canceling", "Cancelling...");
		this.lang_keys.put("clear", "^Clear");
		this.lang_keys.put("console_log", "Console Log");
		this.lang_keys.put("run", "^Run");
		this.lang_keys.put("file", "^File");
		this.lang_keys.put("open", "^Open");
		this.lang_keys.put("exit", "^Exit");
		this.lang_keys.put("yes", "^Yes");
		this.lang_keys.put("no", "^No");
		this.lang_keys.put("finished", "[FINISHED]");
		this.lang_keys.put("canceled", "[CANCELED]");
		this.lang_keys.put("reading_dvks", "Reading DVK Files...");
		this.lang_keys.put("sorting_dvks", "Sorting DVK Objects...");
		this.lang_keys.put("select_all", "[SELECT_ALL]");
		//SETTINGS
		this.lang_keys.put("no_dir_select", "(No Directories Selected)");
		this.lang_keys.put("settings", "^Settings");
		this.lang_keys.put("save", "^Save");
		this.lang_keys.put("font", "^Font");
		this.lang_keys.put("size", "Si^ze");
		this.lang_keys.put("bold", "^Bold");
		this.lang_keys.put("aa", "^Anti-Aliasing");
		String text = "0123456789?!.\n\n"
				+ "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG.\n\n"
				+ "the quick brown fox jumps over the lazy dog";
		this.lang_keys.put("preview_text", text);
		this.lang_keys.put("language", "^Language");
		this.lang_keys.put("theme", "^Theme");
		//PROGRAM CATEGORIES
		this.lang_keys.put("artist_hosting", "Artist Hosting");
		this.lang_keys.put("comics", "Comics");
		this.lang_keys.put("error_finding", "Error Finding");
		this.lang_keys.put("reformatting", "Reformatting");
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
		this.lang_keys.put("add_artist", "Add Artist");
		this.lang_keys.put("artists", "^Artists");
		this.lang_keys.put("titles", "^Titles");
		this.lang_keys.put("add", "A^dd");
		this.lang_keys.put("enter_page_url", "Enter Page URL:");
		this.lang_keys.put("loading_captcha", "Loading Captcha...");
		this.lang_keys.put("captcha_failed", "Failed Loading Captcha");
		this.lang_keys.put("attempt_login", "Attempting Login...");
		this.lang_keys.put("login_failed", "Login Failed");
		this.lang_keys.put("login_success", "Login Successful!");
		this.lang_keys.put("getting_gallery", "Getting Gallery Pages...");
		this.lang_keys.put("getting_scraps", "Getting Scrap Pages...");
		this.lang_keys.put("getting_journals", "Getting Journal Pages...");
		this.lang_keys.put("getting_favorites", "Getting Favorites Pages...");
		this.lang_keys.put("downloading_pages", "Downloading Pages...");
		this.lang_keys.put("getting_artist", "Getting Artist");
		this.lang_keys.put("invalid_url", "Invalid URL");
		this.lang_keys.put("main", "^Main");
		this.lang_keys.put("scraps", "Sc^raps");
		this.lang_keys.put("journals", "^Journals");
		this.lang_keys.put("favorites", "^Favorites");
		//COMICS
		this.lang_keys.put("mangadex", "MangaDex");
		//ERROR FINDING
		this.lang_keys.put("same_ids", "Same IDs");
		this.lang_keys.put("missing_media", "Missing Media");
		this.lang_keys.put("unlinked_media", "Unlinked Media");
		//REFORMATTING
		this.lang_keys.put("reformat_dvks", "Reformat DVKs");
		this.lang_keys.put("rename_files", "Rename Files");
		//MANGADEX
		String desc = "Downloads comics from MangaDex.org.";
		this.lang_keys.put("mangadex_desc", desc);
		this.lang_keys.put("running_mangadex", "[RUNNING MANGADEX]");
		this.lang_keys.put("getting_title", "Getting Title");
		this.lang_keys.put("getting_chapters", "Getting Chapters...");
		this.lang_keys.put("mangadex_failed", "MangaDex Failed");
		//FURAFFINITY
		this.lang_keys.put("running_furaffinity", "[RUNNING FUR AFFINITY]");
		this.lang_keys.put("fur_affinity_failed", "Fur Affinity Failed");
		this.lang_keys.put("enter_furaffinity", "Enter Fur Affinity Artist:");
		this.lang_keys.put("offset", "Offset");
		//DEVIANTART
		this.lang_keys.put("running_deviantart", "[RUNNING DEVIANTART]");
		this.lang_keys.put("deviantart_failed", "DeviantArt Failed");
		this.lang_keys.put("enter_deviantart", "Enter DeviantArt Artist:");
		//UNLINKED MEDIA
		this.lang_keys.put("unlinked_title", "Find Unlinked Media");
		this.lang_keys.put("unlinked_console", "[FINDING UNLINKED MEDIA]");
		desc = "Finds media files in DVK directories that ";
		desc = desc + "do not have a corresponding DVK file.";
		this.lang_keys.put("unlinked_desc", desc);
		//SAME IDS
		this.lang_keys.put("same_ids_title", "Find Same IDs");
		this.lang_keys.put("same_ids_console", "[FINDING SAME IDS]");
		desc = "Finds DVK files that share identical DVK IDs.";
		this.lang_keys.put("same_ids_desc", desc);
		//MISSING MEDIA
		this.lang_keys.put("missing_media_title", "Find Missing Media DVKs");
		this.lang_keys.put("missing_media_console","[FINDING MISSING MEDIA DVKS]");
		desc = "Finds DVK files that are missing their associated media "
				+ "files, whether primary or secondary.";
		this.lang_keys.put("missing_media_desc", desc);
		//RENAME FILES
		this.lang_keys.put("rename_title", "Rename Files");
		this.lang_keys.put("rename_console", "[RENAMING FILES]");
		desc = "Renames DVK files and their associated media.";
		this.lang_keys.put("rename_desc", desc);
		//RENAME DVKS
		this.lang_keys.put("reformat_title", "Reformat DVKs");
		this.lang_keys.put("reformat_console", "[REFORMATTING DVKS]");
		desc = "Reformats all DVK files to follow the current formatting standard.";
		this.lang_keys.put("reformat_desc", desc);
	}
}
