package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.util.ArrayList;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;

/**
 * Contains methods for getting info from artist hosting websites.
 * 
 * @author Drakovek
 */
public class ArtistHosting {
	
	/**
	 * Returns a list of Dvks of different artist's work from a given domain.
	 * Used for determining which artists to download from and to which directories.
	 * 
	 * @param handler DvkHandler containing loaded Dvks
	 * @param domain Domain in which to check for artists
	 * @return List of Dvks from different artists of a domain
	 */
	public static ArrayList<Dvk> get_artists(DvkHandler handler, String domain) {
		if(handler == null) {
			return new ArrayList<>();
		}
		handler.sort_dvks_title(true, false);
		ArrayList<Dvk> dvks = new ArrayList<>();
		ArrayList<String> artists = new ArrayList<>();
		int size = handler.get_size();
		for(int i = 0; i < size; i++) {
			Dvk dvk = handler.get_dvk(i);
			String artist = dvk.get_artists()[0];
			String url = dvk.get_page_url();
			if(url.contains(domain) && !artists.contains(artist)) {
				artists.add(artist);
				dvks.add(dvk);
			}
		}
		return dvks;
	}
}
