package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.HtmlProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;
import com.google.common.io.Files;

/**
 * Class for handling a single DVK file.
 * 
 * @author Drakovek
 */
public class Dvk {
	
	//TODO ADD PARAMETERS FOR LOCAL IMAGES TO USE IN HTML DESCRIPTIONS AND MEDIA.
	//TODO PREVENT OVERWRITING DVK FILES
	
	/**
	 * Array of Tika data types and associated extensions. 
	 */
	private static final String[][] FILE_TYPES = {
			{"image/jpeg", ".jpg"},
			{"image/png", ".png"},
			{"image/gif", ".gif"},
			{"image/vnd.adobe.photoshop", ".psd"},
			{"image/bmp", ".bmp"},
			{"text/html", ".html"},
			{"text/plain", ".txt"},
			{"application/pdf", ".pdf"},
			{"application/rtf", ".rtf"},
			{"application/msword", ".doc"},
			{"application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"},
			{"application/x-shockwave-flash", ".swf"},{"video/quicktime", ".mov"},
			{"video/x-ms-wmv", ".wmv"},
			{"video/x-msvideo", ".avi"},
			{"video/mp4", ".mp4"},
			{"video/webm", ".webm"},
			{"audio/mpeg", ".mp3"}};

	/**
	 * File object for the Dvk
	 */
	private File dvk_file;
	
	/**
	 * ID for the Dvk object in the Dvk info SQLite database
	 */
	private int sql_id;
	
	/**
	 * ID of the Dvk
	 */
	private String dvk_id;
	
	/**
	 * Title of the Dvk
	 */
	private String title;
	
	/**
	 * Artist(s) of the Dvk
	 */
	private String[] artists;
	
	/**
	 * Time published for the Dvk.
	 * Formatted YYYY/MM/DD|hh:mm
	 */
	private String time;
	
	/**
	 * Web tags for the Dvk.
	 */
	private String[] web_tags;
	
	/**
	 * Description for the Dvk.
	 * In HTML format.
	 */
	private String description;
	
	/**
	 * Page URL of the Dvk's origin.
	 */
	private String page_url;
	
	/**
	 * Direct URL for the Dvk's referenced media.
	 */
	private String direct_url;
	
	/**
	 * Direct URL for the Dvk's referenced secondary media.
	 */
	private String secondary_url;
	
	/**
	 * Associated media file for the Dvk object.
	 */
	private String media_file;
	
	/**
	 * Associated secondary media file for the Dvk object.
	 */
	private String secondary_file;
	
	/**
	 * Initializes the Dvk object with no filled fields.
	 */
	public Dvk() {
		clear_dvk();
	}
	
	/**
	 * Initializes the Dvk object by loading from a given DVK file.
	 * 
	 * @param dvk_file Given DVK file
	 */
	public Dvk(final File dvk_file) {
		set_dvk_file(dvk_file);
		read_dvk();
	}
	
	/**
	 * Clears all Dvk fields to their default values.
	 */
	public void clear_dvk() {
		set_sql_id(0);
		set_dvk_id(null);
		set_title(null);
		set_artists(null);
		set_time(null);
		set_web_tags(null);
		set_description(null);
		set_page_url(null);
		set_direct_url(null);
		set_secondary_url(null);
		set_media_file("");
		set_secondary_file("");
	}
	
	/**
	 * Returns whether the Dvk object can be written.
	 * Returns false if Dvk doesn't contain necessary info.
	 * 
	 * @return Whether Dvk can be written.
	 */
	public boolean can_write() {
		if(get_dvk_file() == null
				|| get_dvk_id() == null
				|| get_title() == null
				|| get_artists().length == 0
				|| get_page_url() == null
				|| get_media_file() == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Writes the Dvk object parameters to dvk_file.
	 */
	public void write_dvk() {
		if(can_write()) {
			JSONObject json = new JSONObject();
			json.put("file_type", "dvk");
			json.put("id", get_dvk_id());
			//INFO
			JSONObject info = new JSONObject();
			info.put("title", get_title());
			JSONArray array = new JSONArray(get_artists());
			info.put("artists", array);
			if(!get_time().equals("0000/00/00|00:00")) {
				info.put("time", get_time());
			}
			if(get_web_tags() != null) {
				array = new JSONArray(get_web_tags());
				info.put("web_tags", array);
			}
			if(get_description() != null) {
				info.put("description", get_description());
			}
			json.put("info", info);
			//WEB
			JSONObject web = new JSONObject();
			web.put("page_url", get_page_url());
			if(get_page_url() != null) {
				web.put("direct_url", get_direct_url());
			}
			if(get_secondary_url() != null) {
				web.put("secondary_url", get_secondary_url());
			}
			json.put("web", web);
			//FILE
			JSONObject file = new JSONObject();
			file.put("media_file", get_media_file().getName());
			if(get_secondary_file() != null) {
				file.put("secondary_file", get_secondary_file().getName());
			}
			json.put("file", file);
			InOut.write_file(get_dvk_file(), json.toString(4));
		}
	}
	
	/**
	 * Writes the Dvk object, as well as downloading associated media.
	 * Downloads from direct_url and secondary_url.
	 * Writes to media_file and secondary_file.
	 * 
	 * @param connect DConnect object for downloading media
	 */
	public void write_media(DConnect connect) {
		write_dvk();
		if(get_dvk_file().exists()) {
			connect.download(get_direct_url(), get_media_file());
			//CHECK IF MEDIA DOWNLOADED
			if(get_media_file().exists()) {
				//DOWNLOAD SECONDARY FILE, IF AVAILABLE
				if(get_secondary_url() != null) {
					connect.download(get_secondary_url(), get_secondary_file());
					//DELETE FILES IF DOWNLOAD FAILED
					if(!get_secondary_file().exists()) {
						get_dvk_file().delete();
						get_media_file().delete();
					}
				}
			}
			else {
				//IF DOWNLOAD FAILED, DELETE DVK
				get_dvk_file().delete();
			}
		}
		//UPDATE EXTENSIONS
		update_extensions();
	}
	
	/**
	 * Reads DVK info from the file referenced in dvk_file.
	 */
	public void read_dvk() {
		clear_dvk();
		String source = InOut.read_file(get_dvk_file());
		try {
			JSONObject json = new JSONObject(source);
			if(json.getString("file_type").equals("dvk")) {
				set_dvk_id(get_json_string(json, "id"));
				//INFO
				JSONObject info = json.getJSONObject("info");
				set_title(get_json_string(info, "title"));
				set_artists(get_json_array(info, "artists"));
				set_time(get_json_string(info, "time"));
				set_web_tags(get_json_array(info, "web_tags"));
				set_description(get_json_string(info, "description"));
				//WEB
				JSONObject web = json.getJSONObject("web");
				set_page_url(get_json_string(web, "page_url"));
				set_direct_url(get_json_string(web, "direct_url"));
				set_secondary_url(get_json_string(web, "secondary_url"));
				//FILE
				JSONObject file = json.getJSONObject("file");
				set_media_file(get_json_string(file, "media_file"));
				set_secondary_file(get_json_string(file, "secondary_file"));
			}
		}
		catch(JSONException e) {
			clear_dvk();
		}
	}
	
	/**
	 * Returns the String for a key in a given JSONObject.
	 * 
	 * @param json JSONObject to parse
	 * @param key JSON key
	 * @return String for JSON key
	 */
	private static String get_json_string(final JSONObject json, final String key) {
		try {
			String str = json.getString(key);
			return str;
		}
		catch(JSONException e) {
			return null;
		}
	}
	
	/**
	 * Returns the String array for a key in a given JSONObject.
	 * 
	 * @param json JSONObject to parse
	 * @param key JSON key
	 * @return String array for JSON key
	 */
	private static String[] get_json_array(final JSONObject json, final String key) {
		try {
			JSONArray array = json.getJSONArray(key);
			String[] str_array = new String[array.length()];
			for(int i = 0; i < str_array.length; i++) {
				str_array[i] = array.getString(i);
			}
			return str_array;
		}
		catch(JSONException e) {
			return null;
		}
	}

	/**
	 * Sets the Dvk file.
	 * 
	 * @param dvk_file Dvk file
	 */
	public void set_dvk_file(final File dvk_file) {
		this.dvk_file = dvk_file;
	}
	
	/**
	 * Returns the Dvk file.
	 * 
	 * @return Dvk file
	 */
	public File get_dvk_file() {
		return this.dvk_file;
	}
	
	/**
	 * Sets the SQL ID.
	 * 
	 * @param sql_id SQL ID
	 */
	public void set_sql_id(int sql_id) {
		this.sql_id = sql_id;
	}
	
	/**
	 * Returns the SQL ID.
	 * 
	 * @return SQL ID
	 */
	public int get_sql_id() {
		return this.sql_id;
	}
	
	/**
	 * Sets the Dvk ID.
	 * 
	 * @param id Dvk ID.
	 */
	public void set_dvk_id(final String id) {
		if(id == null || id.length() == 0) {
			this.dvk_id = null;
		}
		else {
			this.dvk_id = id.toUpperCase();
		}
	}
	
	/**
	 * Returns the Dvk ID.
	 *
	 * @return Dvk ID
	 */
	public String get_dvk_id() {
		return this.dvk_id;
	}
	
	/**
	 * Sets the Dvk title.
	 * 
	 * @param title Dvk title
	 */
	public void set_title(final String title) {
		this.title = title;
	}
	
	/**
	 * Returns the Dvk title.
	 * 
	 * @return Dvk title
	 */
	public String get_title() {
		return this.title;
	}
	
	/**
	 * Sets the Dvk artists variable for a single artist.
	 * 
	 * @param artist Dvk artist
	 */
	public void set_artist(final String artist) {
		if(artist == null) {
			this.artists = new String[0];
		}
		else {
			this.artists = new String[1];
			this.artists[0] = artist;
		}
	}
	
	/**
	 * Sets the Dvk artists.
	 * 
	 * @param artists Dvk artists
	 */
	public void set_artists(final String[] artists) {
		if(artists == null) {
			this.artists = new String[0];
		}
		else {
			String[] array = ArrayProcessing.clean_array(artists);
			array = ArrayProcessing.sort_alphanum(array);
			this.artists = array;
		}
	}
	
	/**
	 * Returns the Dvk artists.
	 * 
	 * @return Dvk artists.
	 */
	public String[] get_artists() {
		return this.artists;
	}
	
	/**
	 * Sets the time published for the Dvk.
	 * 
	 * @param year Year (1-9999)
	 * @param month Month (1-12)
	 * @param day Day (1-31)
	 * @param hour Hour (0-23)
	 * @param minute Minute (0-59)
	 */
	public void set_time_int(
			final int year,
			final int month,
			final int day,
			final int hour,
			final int minute) {
		if(year < 1 || year > 9999 
				|| month < 1 || month > 12
				|| day < 1 || day > 31
				|| hour < 0 || hour > 23
				|| minute < 0 || minute > 59) {
			this.time = "0000/00/00|00:00";
		}
		else {
			String year_str = StringProcessing.extend_int(year, 4);
			String month_str = StringProcessing.extend_int(month, 2);
			String day_str = StringProcessing.extend_int(day, 2);
			String hour_str = StringProcessing.extend_int(hour, 2);
			String minute_str = StringProcessing.extend_int(minute, 2);
			String time_str = year_str + "/" + month_str + "/" + day_str;
			this.time = time_str  + "|" + hour_str + ":" + minute_str;
		}
	}
	
	/**
	 * Sets the time published for the Dvk.
	 * Defaults to value 0000/00/00|00:00 if invalid.
	 * 
	 * @param time Time String, formatted YYYY/MM/DD|hh:mm
	 */
	public void set_time(final String time) {
		if(time == null || time.length() != 16) {
			this.time = "0000/00/00|00:00";
		}
		else {
			try {
				int year = Integer.parseInt(time.substring(0,4));
				int month = Integer.parseInt(time.substring(5,7));
				int day = Integer.parseInt(time.substring(8,10));
				int hour = Integer.parseInt(time.substring(11,13));
				int minute = Integer.parseInt(time.substring(14,16));
				set_time_int(year, month, day, hour, minute);
			}
			catch(NumberFormatException e) {
				this.time = "0000/00/00|00:00";
			}
		}
	}
	
	/**
	 * Returns the time published for the Dvk.
	 * Formatted YYYY/MM/DD|hh:mm
	 * 
	 * @return Dvk time published
	 */
	public String get_time() {
		return this.time;
	}
	
	/**
	 * Sets Dvk web tags.
	 * 
	 * @param web_tags Dvk web tags.
	 */
	public void set_web_tags(final String[] web_tags) {
		if(web_tags == null || web_tags.length == 0) {
			this.web_tags = null;
		}
		else {
			String[] tags = ArrayProcessing.clean_array(web_tags);
			if(tags.length == 0) {
				this.web_tags = null;
			}
			else {
				this.web_tags = tags;
			}
		}
	}
	
	/**
	 * Returns Dvk web tags.
	 * 
	 * @return Dvk web tags.
	 */
	public String[] get_web_tags() {
		return this.web_tags;
	}
	
	/**
	 * Sets the Dvk description.
	 * 
	 * @param description Dvk description
	 */
	public void set_description(final String description) {
		if(description == null) {
			this.description = null;
		}
		else {
			//REMOVE WHITESPACE
			String desc = StringProcessing.remove_whitespace(description);
			if(desc.length() == 0){
				this.description = null;
			}
			else {
				this.description = HtmlProcessing.add_escapes_to_html(desc);
			}
		}
		
	}
	
	/**
	 * Returns the Dvk description.
	 * 
	 * @return Dvk description
	 */
	public String get_description() {
		return this.description;
	}
	
	/**
	 * Sets the Dvk page URL.
	 * 
	 * @param page_url Page URL
	 */
	public void set_page_url(final String page_url) {
		if(page_url == null || page_url.length() == 0) {
			this.page_url = null;
		}
		else {
			this.page_url = page_url;
		}
	}
	
	/**
	 * Returns the Dvk page URL.
	 * 
	 * @return Page URL
	 */
	public String get_page_url() {
		return this.page_url;
	}
	
	/**
	 * Sets the direct media URL.
	 * 
	 * @param direct_url Direct media URL
	 */
	public void set_direct_url(final String direct_url) {
		if(direct_url == null || direct_url.length() == 0) {
			this.direct_url = null;
		}
		else {
			this.direct_url = direct_url;
		}
	}
	
	/**
	 * Returns the direct media URL.
	 * 
	 * @return Direct media URL
	 */
	public String get_direct_url() {
		return this.direct_url;
	}
	
	/**
	 * Sets the direct secondary media URL.
	 * 
	 * @param secondary_url Direct secondary media URL
	 */
	public void set_secondary_url(final String secondary_url) {
		if(secondary_url == null || secondary_url.length() == 0) {
			this.secondary_url = null;
		}
		else {
			this.secondary_url = secondary_url;
		}
	}
	
	/**
	 * Returns the direct secondary media URL.
	 * 
	 * @return Direct secondary media URL
	 */
	public String get_secondary_url() {
		return this.secondary_url;
	}
	
	/**
	 * Sets the associated media file for the Dvk.
	 * Assumes media is in the same directory as dvk_file.
	 * 
	 * @param filename Filename for the associated media.
	 */
	public void set_media_file(final String filename) {
		this.media_file = filename;
		if(filename == null || filename.length() == 0) {
			this.media_file = null;
		}
	}
	
	/**
	 * Sets the associated media file for the Dvk.
	 * Assumes media is in the same directory as dvk_file.
	 * 
	 * @param file File for the associated media.
	 */
	public void set_media_file(File file) {
		this.media_file = null;
		if(file != null
				&& get_dvk_file() != null
				&& file.getParentFile().equals(get_dvk_file().getParentFile())) {
			this.media_file = file.getName();
		}
	}
	
	/**
	 * Returns the Dvk's associated media file.
	 * 
	 * @return Associated media file.
	 */
	public File get_media_file() {
		try {
			File parent = this.dvk_file.getParentFile();
			if(!parent.exists()) {
				return null;
			}
			return new File(parent, this.media_file);
		}
		catch(Exception e) {}
		return null;
	}
	
	/**
	 * Sets the associated secondary media file for the Dvk.
	 * Assumes media is in the same directory as dvk_file.
	 * 
	 * @param filename Filename for the secondary associated media.
	 */
	public void set_secondary_file(final String filename) {
		this.secondary_file = filename;
		if(filename == null || filename.length() == 0) {
			this.secondary_file = null;
		}
	}
	
	/**
	 * Sets the associated secondary media file for the Dvk.
	 * Assumes media is in the same directory as dvk_file.
	 * 
	 * @param file File for the secondary associated media.
	 */
	public void set_secondary_file(File file) {
		this.secondary_file = null;
		if(file != null
				&& get_dvk_file() != null
				&& file.getParentFile().equals(get_dvk_file().getParentFile())) {
			this.secondary_file = file.getName();
		}
	}
	
	/**
	 * Returns the Dvk's secondary associated media file.
	 * 
	 * @return Secondary associated media file.
	 */
	public File get_secondary_file() {
		try {
			File parent = this.dvk_file.getParentFile();
			if(!parent.exists()) {
				return null;
			}
			return new File(parent, this.secondary_file);
		}
		catch(Exception e) {}
		return null;
	}
	
	/**
	 * Returns a filename for the Dvk based on title and id.
	 * Doesn't include extension.
	 * 
	 * @param secondary Whether this is for a secondary file
	 * @return Dvk filename
	 */
	public String get_filename(boolean secondary) {
		if(get_dvk_id() == null || get_title() == null) {
			return new String();
		}
		StringBuilder filename = new StringBuilder();
		filename.append(StringProcessing.get_filename(get_title()));
		filename.append("_");
		filename.append(get_dvk_id());
		if(secondary) {
			filename.append("_S");
		}
		return filename.toString();
	}
	
	/**
	 * Renames the DVK file and its associated media files.
	 * Retains all media file extensions.
	 * 
	 * @param filename Main filename to use when renaming
	 * @param secondary Filename to use for secondary media
	 */
	public void rename_files(String filename, String secondary) {
		//RENAME DVK FILE
		get_dvk_file().delete();
		set_dvk_file(new File(get_dvk_file().getParentFile(), filename + ".dvk"));
		//RENAME MEDIA FILE
		if(get_media_file() != null) {
			File from = get_media_file();
			String rename = filename + StringProcessing.get_extension(from.getName());
			//CHECK IF RENAME IS NEEDED
			if(!from.getName().equals(rename)) {
				try {
					//RENAME TO TEMPORARY FILE
					set_media_file("xXTeMpPrImXx.tmp");
					Files.move(from, get_media_file());
					//RENAME TO FINAL FILENAME
					from = get_media_file();
					set_media_file(rename);
					Files.move(from, get_media_file());
				}
				catch(IOException e) {
					set_media_file(rename);
				}
			}
		}
		//RENAME SECONDARY MEDIA FILE
		if(get_secondary_file() != null) {
			File from = get_secondary_file();
			String rename = secondary + StringProcessing.get_extension(from.getName());
			//CHECK IF RENAME IS NEEDED
			if(!from.getName().equals(rename)) {
				try {
					//RENAME TO TEMPORARY FILE
					set_secondary_file("xXTeMpSeCoNdXx.tmp");
					Files.move(from, get_secondary_file());
					//RENAME TO FINAL FILENAME
					from = get_secondary_file();
					set_secondary_file(rename);
					Files.move(from, get_secondary_file());
				}
				catch(IOException e) {
					set_secondary_file(rename);
				}
			}
		}
		//REWRITE DVK FILE
		write_dvk();
	}

	/**
	 * Updates the Dvks associated media extensions
	 * to fit with their true data type.
	 */
	public void update_extensions() {
		Tika tika = new Tika();
		//MAIN MEDIA FILE
		if(get_media_file() != null
				&& get_media_file().exists()) {
			//GET MEDIA EXTENSION
			String filename = get_media_file().getName();
			String ext = StringProcessing.get_extension(filename);
			filename = filename.substring(0,
					filename.length() - ext.length());
			try {
				//DETERMINE ACTUAL FILE TYPE
				String type = tika.detect(get_media_file());
				type = type.toLowerCase();
				for(int i = 0; i < FILE_TYPES.length; i++) {
					if(FILE_TYPES[i][0].equals(type)) {
						ext = FILE_TYPES[i][1];
						break;
					}
				}
			} catch (IOException e) {}
			//RENAME FILE
			if(!get_media_file().getName().endsWith(ext)) {
				File file = get_media_file();
				try {
					set_media_file(filename + ext);
					Files.move(file, get_media_file());
				} catch (IOException e) {}
			}
		}
		//SECONDARY MEDIA FILE
		if(get_secondary_file() != null
				&& get_secondary_file().exists()) {
			//GET MEDIA EXTENSION
			String filename = get_secondary_file().getName();
			String ext = StringProcessing.get_extension(filename);
			filename = filename.substring(0,
					filename.length() - ext.length());
			try {
				//DETERMINE ACTUAL FILE TYPE
				String type = tika.detect(get_secondary_file());
				type = type.toLowerCase();
				for(int i = 0; i < FILE_TYPES.length; i++) {
					if(FILE_TYPES[i][0].equals(type)) {
						ext = FILE_TYPES[i][1];
						break;
					}
				}
			} catch (IOException e) {}
			//RENAME FILE
			if(!get_secondary_file().getName().endsWith(ext)) {
				File file = get_secondary_file();
				try {
					set_secondary_file(filename + ext);
					Files.move(file, get_secondary_file());
				} catch (IOException e) {}
			}
		}
		//WRITE DVK
		if(get_dvk_file() != null && get_dvk_file().exists()) {
			write_dvk();
		}
	}
}
