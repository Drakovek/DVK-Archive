package com.gmail.drakovekmail.dvkarchive.processing;

/**
 * Class containing methods for processing Strings.
 * 
 * @author Drakovek
 */
public class StringProcessing {
	/**
	 * Returns a String for a given int of a given length.
	 * If too small, pads out String with zeros.
	 * 
	 * @param input_int int to convert to String
	 * @param length Length of returned String
	 * @return String for input_int
	 */
	public static String extend_int(final int input_int, final int length) {
		if(length < 1) {
			return new String();
		}
		String int_string = Integer.toString(input_int);
		if(length < int_string.length()) {
			return extend_int(0, length);
		}
		StringBuilder builder = new StringBuilder();
		builder.append(int_string);
		while(builder.length() < length) {
			builder.insert(0, "0");
		}
		return builder.toString();
	}
	
	/**
	 * Removes the whitespace at the beginning and end of a given String.
	 * 
	 * @param str Given String
	 * @return String without whitespace
	 */
	public static String remove_whitespace(final String str) {
		if(str == null) {
			return new String();
		}
		int start = 0;
		int end = 0;
		for(start = 0; start < str.length() && str.charAt(start) == ' '; start++);
		for(end = str.length() - 1; end > -1 && str.charAt(end) == ' '; end--);
		end++;
		if(end < start) {
			return str.substring(start);
		}
		return str.substring(start, end);
	}
}
