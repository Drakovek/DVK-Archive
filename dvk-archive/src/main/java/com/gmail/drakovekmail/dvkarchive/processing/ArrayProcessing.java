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
	 * Converts a String array to an ArrayList<String>.
	 * 
	 * @param array Given String array
	 * @return ArrayList<String> representation of array
	 */
	public static ArrayList<String> array_to_list(final String[] array) {
		if(array == null) {
			return new ArrayList<>();
		}
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
	
	/**
	 * Converts an ArrayList<String> into a String array.
	 * 
	 * @param list Given ArrayList<String>
	 * @return String array representation of list
	 */
	public static String[] list_to_array(final ArrayList<String> list) {
		if(list == null) {
			return new String[0];
		}
		String[] array = new String[list.size()];
		for(int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	/**
	 * Removes all duplicate and null entries from a String array.
	 * 
	 * @param array Given String array
	 * @return String array without duplicate or null entries.
	 */
	public static String[] clean_array(final String[] array) {
		ArrayList<String> list = array_to_list(array);
		list = clean_list(list);
		return list_to_array(list);
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
		Arrays.parallelSort(return_array, new StringCompare());
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
		if(array == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < array.length; i++) {
			if(i > 0) {
				builder.append(',');
				for(int k = 0; k < indent; k++) {
					builder.append(' ');
				}
			}
			if(use_html) {
				builder.append(HtmlProcessing.add_escapes(array[i]));
			}
			else {
				builder.append(array[i]);
			}
		}
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
		if(array_str == null) {
			return null;
		}
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
		if(array == null) {
			return false;
		}
		if(case_sensitive) {
			for(int i = 0; i < array.length; i++) {
				if(array[i] != null && array[i].equals(search)) {
					return true;
				}
			}
		}
		else {
			String lower = search.toLowerCase();
			for(int i = 0; i < array.length; i++) {
				if(array[i] != null && array[i].toLowerCase().equals(lower)) {
					return true;
				}
			}
		}
		return false;
	}
}
