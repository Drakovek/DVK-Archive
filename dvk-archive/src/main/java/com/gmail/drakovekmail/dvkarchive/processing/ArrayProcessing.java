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
		if(array == null) {
			return new String[0];
		}
		//REMOVE NULL ENTRIES
		ArrayList<String> list = array_to_list(array);
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
		return list_to_array(list);
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
		Arrays.sort(return_array, new StringCompare());
		return return_array;
	}
}
