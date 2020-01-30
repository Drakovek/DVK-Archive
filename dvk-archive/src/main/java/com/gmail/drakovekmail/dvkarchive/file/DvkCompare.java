package com.gmail.drakovekmail.dvkarchive.file;

import java.util.Comparator;

import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.StringCompare;

/**
 * Class containing methods for comparing Dvk objects.
 * 
 * @author Drakovek
 */
public class DvkCompare implements Comparator<Dvk>{
	
	/**
	 * Static variable to indicate comparing by title alphabetically.
	 */
	public static final int TITLE = 0;
	
	/**
	 * Static variable to indicate comparing by time published.
	 */
	public static final int TIME = 1;
	
	/**
	 * Type of comparison to run.
	 */
	private int comp_type;
	
	/**
	 * Whether to group artists together when comparing.
	 */
	private boolean group_artists;
	
	/**
	 * Whether to reverse the comparison.
	 */
	private boolean reverse;
	
	/**
	 * Initializes the DvkCompare class with default parameters.
	 */
	public DvkCompare() {
		set_parameters(TITLE, false, false);
	}
	
	/**
	 * Initializes the DvkCompare class with given parameters.
	 * 
	 * @param comp_type Type of comparison to run
	 * @param group_artists Whether to group artists when comparing
	 * @param reverse Whether to reverse all comparisons
	 */
	public DvkCompare(
			final int comp_type,
			final boolean group_artists,
			final boolean reverse) {
		set_parameters(comp_type, group_artists, reverse);
	}
	
	/**
	 * Sets DvkCompare parameters to given parameters.
	 * 
	 * @param comp_type Type of comparison to run
	 * @param group_artists Whether to group artists when comparing
	 * @param reverse Whether to reverse all comparisons
	 */
	public void set_parameters(
			final int comp_type,
			final boolean group_artists,
			final boolean reverse) {
		this.comp_type = comp_type;
		this.group_artists = group_artists;
		this.reverse = reverse;
	}
	
	/**
	 * Compares two dvk objects based on DvkCompare parameters.
	 */
	@Override
	public int compare(Dvk dvk1, Dvk dvk2) {
		int result = 0;
		//GROUP ARTISTS
		if(this.group_artists) {
			result = compare_artists(dvk1, dvk2);
		}
		//COMPARE
		if(result == 0) {
			switch(this.comp_type) {
				case TIME:
					result = compare_time(dvk1, dvk2);
					break;
				default:
					result = compare_titles(dvk1, dvk2);
					break;
			}
		}
		//REVERSE
		if(this.reverse) {
			result = result * -1;
		}
		return result;
	}
	
	/**
	 * Compares two Dvk objects alpha-numerically by artist(s).
	 * If equal, returns zero without further comparisons.
	 * 
	 * @param dvk1 First Dvk object
	 * @param dvk2 Second Dvk object
	 * @return < 0 - dvk1 is first, > 0 - dvk2 is first, 0 - equal
	 */
	public static int compare_artists(Dvk dvk1, Dvk dvk2) {
		String str1 = ArrayProcessing.array_to_string(
				dvk1.get_artists(), 0);
		String str2 = ArrayProcessing.array_to_string(
				dvk2.get_artists(), 0);
		return StringCompare.compare_alphanum(str1, str2);
	}
	
	/**
	 * Compares two Dvk objects alpha-numerically by title.
	 * If equal, returns zero without further comparisons.
	 * 
	 * @param dvk1 First Dvk object
	 * @param dvk2 Second Dvk object
	 * @return < 0 - dvk1 is first, > 0 - dvk2 is first, 0 - equal
	 */
	public static int compare_title_base(Dvk dvk1, Dvk dvk2) {
		String str1 = dvk1.get_title();
		String str2 = dvk2.get_title();
		return StringCompare.compare_alphanum(str1, str2);
	}
	
	/**
	 * Compares two Dvk objects alpha-numerically by title.
	 * If equal, falls back on other comparison methods.
	 * 
	 * @param dvk1 First Dvk object
	 * @param dvk2 Second Dvk object
	 * @return < 0 - dvk1 is first, > 0 - dvk2 is first, 0 - equal
	 */
	public static int compare_titles(Dvk dvk1, Dvk dvk2) {
		int result = compare_title_base(dvk1, dvk2);
		if(result == 0) {
			result = compare_time_base(dvk1, dvk2);
		}
		return result;
	}
	
	/**
	 * Compares two Dvk objects by time published.
	 * If equal, returns zero without further comparisons.
	 * 
	 * @param dvk1 First Dvk object
	 * @param dvk2 Second Dvk object
	 * @return < 0 - dvk1 is first, > 0 - dvk2 is first, 0 - equal
	 */
	public static int compare_time_base(Dvk dvk1, Dvk dvk2) {
		String str1 = dvk1.get_time();
		String str2 = dvk2.get_time();
		return str1.compareTo(str2);
	}
	
	/**
	 * Compares two Dvk objects by time published.
	 * If equal, falls back on other comparison methods.
	 * 
	 * @param dvk1 First Dvk object
	 * @param dvk2 Second Dvk object
	 * @return < 0 - dvk1 is first, > 0 - dvk2 is first, 0 - equal
	 */
	public static int compare_time(Dvk dvk1, Dvk dvk2) {
		int result = compare_time_base(dvk1, dvk2);
		if(result == 0) {
			result = compare_title_base(dvk1, dvk2);
		}
		return result;
	}
}
