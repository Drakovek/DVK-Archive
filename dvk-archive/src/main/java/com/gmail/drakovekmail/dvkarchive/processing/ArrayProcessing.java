package com.gmail.drakovekmail.dvkarchive.processing;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class containing methods to process String arrays.
 * 
 * @author Drakovek
 */
public class ArrayProcessing {
	
	/**
	 * Removes all duplicate and null entries from a String array.
	 * 
	 * @param array Given String array
	 * @return String array without duplicate or null entries.
	 */
	public static String[] clean_array(final String[] array) {
		//RETURN AN EMPTY STRING IF GIVEN ARRAY IS NULL
		if(array == null) {
			return new String[0];
		}
		//CLEAN ARRAY
		ArrayList<String> list = new ArrayList<>(Arrays.asList(array));
		list = clean_list(list);
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * Removes all duplicate and null entries from an ArrayList<String>.
	 * 
	 * @param list Given ArrayList<String>
	 * @return ArrayList<String> without duplicate or null entries.
	 */
	public static ArrayList<String> clean_list(final ArrayList<String> list) {
		if(list == null) {
			return new ArrayList<>();
		}
		//REMOVE NULL ENTRIES
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i) == null) {
				list.remove(i);
				i--;
			}
		}
		//REMOVE DUPLICATE ENTRIES
		for(int i = 0; i < list.size(); i++) {
			for(int k = i + 1; k < list.size(); k++) {
				if(list.get(i).equals(list.get(k))) {
					list.remove(k);
					k--;
				}
			}
		}
		return list;
	}
	
	/**
	 * Sorts a given String array alpha-numerically.
	 * 
	 * @param array Given String array
	 * @return Sorted String array
	 */
	public static String[] sort_alphanum(final String[] array) {
		if(array == null) {
			return new String[0];
		}
		String[] return_array = array;
		Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
		return return_array;
	}
	
	/**
	 * Converts a given String array to a String.
	 * Items separated by commas and indent.
	 * 
	 * @param array Given String array
	 * @param indent Number of spaces to use as indent between items
	 * @param use_html Whether to replace non-ASCII characters with HTML escapes
	 * @return String of given array
	 */
	public static String array_to_string(String[] array, int indent, boolean use_html) {
		//IF ARRAY IS NULL, RETURN NULL
		if(array == null) {
			return null;
		}
		//CREATE STRING
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < array.length; i++) {
			//ADD COMMAS BETWEEN ENTRIES
			if(i > 0) {
				builder.append(',');
				//ADD SPACES FOR INDENT
				for(int k = 0; k < indent; k++) {
					builder.append(' ');
				}
			}
			//IF SPECIFIED, CONVERT ENTRIES TO HTML WITH ESCAPE CHARACTERS
			if(use_html) {
				builder.append(HtmlProcessing.add_escapes(array[i]));
			}
			else {
				builder.append(array[i]);
			}
		}
		//RETURN FORMATTED STRING
		return builder.toString();
	}
	
	/**
	 * Returns a String array from a given string with entries separated by commas.
	 * Removes HTML escape characters from entries.
	 * 
	 * @param array_str String with entries separated by commas
	 * @return String array from array_str
	 */
	public static String[] string_to_array(String array_str) {
		//IF ARRAY STRING IS INVALID, RETURN NULL
		if(array_str == null) {
			return null;
		}
		//SPLIT STRING INTO ENTRIES
		String[] array = array_str.split(",");
		for(int i = 0; i < array.length; i++) {
			array[i] = HtmlProcessing.replace_escapes(array[i]);
		}
		return array;
	}
	
	/**
	 * Returns whether given String array contains a given string.
	 * 
	 * @param array Array in which to search for string
	 * @param search String to search for
	 * @param case_sensitive Whether comparison should be case sensitive
	 * @return Whether array contains search
	 */
	public static boolean contains(String[] array, String search, boolean case_sensitive) {
		//IF ARRAY IS NULL, RETURN FALSE
		if(array == null) {
			return false;
		}
		//RETURN IF ARRAY CONTAINS A GIVEN STRING		
		if(case_sensitive) {
			//CASE SENSITIVE
			for(int i = 0; i < array.length; i++) {
				if(array[i] != null && array[i].equals(search)) {
					return true;
				}
			}
		}
		else {
			//NOT CASE SENSITIVE
			String lower = search.toLowerCase();
			for(int i = 0; i < array.length; i++) {
				if(array[i] != null && array[i].toLowerCase().equals(lower)) {
					return true;
				}
			}
		}
		//RETURNS FALSE IF ARRAY DOESN'T CONTAIN GIVEN STRING
		return false;
	}
}
