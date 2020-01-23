package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;

/**
 * Class for handling a single DVK file.
 * 
 * @author Drakovek
 */
public class Dvk
{
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
	 * Initializes a Dvk object with no filled fields.
	 */
	public Dvk()
	{
		clear_dvk();
	}
	
	/**
	 * Clears all Dvk fields.
	 */
	private void clear_dvk()
	{
		set_dvk_file(null);
		set_id(null);
	}

	/**
	 * Sets the Dvk file.
	 * 
	 * @param dvk_file Dvk file
	 */
	public void set_dvk_file(final File dvk_file)
	{
		this.dvk_file = dvk_file;
	}
	
	/**
	 * Returns the Dvk file.
	 * 
	 * @return Dvk file
	 */
	public File get_dvk_file()
	{
		return this.dvk_file;
	}
	
	/**
	 * Sets the Dvk ID.
	 * 
	 * @param id Dvk ID.
	 */
	public void set_id(final String id)
	{
		if(id == null)
		{
			this.id = "";
		}
		else
		{
			this.id = id.toUpperCase();
		}
	}
	
	/**
	 * Returns the Dvk ID.
	 *
	 * @return Dvk ID
	 */
	public String get_id()
	{
		return this.id;
	}
	
	/**
	 * Sets the Dvk title.
	 * 
	 * @param title Dvk title
	 */
	public void set_title(final String title)
	{
		this.title = title;
	}
	
	/**
	 * Returns the Dvk title.
	 * 
	 * @return Dvk title
	 */
	public String get_title()
	{
		return this.title;
	}
	
	/**
	 * Sets the Dvk artists variable for a single artist.
	 * 
	 * @param artist Dvk artist
	 */
	public void set_artist(final String artist)
	{
		if(artist == null)
		{
			this.artists = new String[0];
		}
		else
		{
			this.artists = new String[1];
			this.artists[0] = artist;
		}
	}
	
	/**
	 * Sets the Dvk artists.
	 * 
	 * @param artists Dvk artists
	 */
	public void set_artists(final String[] artists)
	{
		if(artists == null)
		{
			this.artists = new String[0];
		}
		else
		{
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
	public String[] get_artists()
	{
		return this.artists;
	}
}
