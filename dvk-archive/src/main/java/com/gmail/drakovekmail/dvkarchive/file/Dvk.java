package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.HtmlProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;

/**
 * Class for handling a single DVK file.
 * 
 * @author Drakovek
 */
public class Dvk {
	/**
	 * File object for the Dvk
	 */
	private File dvk_file;
	
	/**
	 * ID of the Dvk
	 */
	private String id;
	
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
	 * Initializes a Dvk object with no filled fields.
	 */
	public Dvk() {
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
	 * Sets the Dvk ID.
	 * 
	 * @param id Dvk ID.
	 */
	public void set_id(final String id) {
		if(id == null) {
			this.id = "";
		}
		else {
			this.id = id.toUpperCase();
		}
	}
	
	/**
	 * Returns the Dvk ID.
	 *
	 * @return Dvk ID
	 */
	public String get_id() {
		return this.id;
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
			this.web_tags = ArrayProcessing.clean_array(web_tags);
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
}
