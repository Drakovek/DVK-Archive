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
		String int_string = Integer.toString(input_int);
		return extend_num(int_string, length);
	}
	
	/**
	 * Returns a String for a given String of a given length.
	 * If too small, pads out String with zeros.
	 * 
	 * @param input String to extend
	 * @param length Length of returned String
	 * @return Extended string
	 */
	public static String extend_num(String input, int length) {
		if(length < 1) {
			return new String();
		}
		if(length < input.length()) {
			return extend_int(0, length);
		}
		StringBuilder builder = new StringBuilder();
		builder.append(input);
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
	
	/**
	 * Returns a version of a given String that is safe for use as a filename.
	 * Defaults to maximum size of 90 characters.
	 * 
	 * @param str Given String
	 * @return Filename
	 */
	public static String get_filename(final String str) {
		return get_filename(str, 90);
	}
	
	/**
	 * Returns a version of a given String that is safe for use as a filename.
	 * 
	 * @param str Given String
	 * @param length Maximum length of the returned filename
	 * @return Filename
	 */
	public static String get_filename(final String str, final int length) {
		if(str == null) {
			return "0";
		}
		//REMOVE ALL NON-LETTER, NON-NUMERIC CHARACTERS
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char val = str.charAt(i);
			if((val > 47 && val < 58)
					|| (val > 64 && val < 91)
					|| (val > 96 && val < 123)
					|| val == ' ') {
				builder.append(val);
			}
			else {
				builder.append('-');
			}
		}
		//REMOVE START AND END SPACERS
		while(builder.length() > 0 
				&& (builder.charAt(0) == ' ' 
				|| builder.charAt(0) == '-')) {
			builder.deleteCharAt(0);
		}
		while(builder.length() > 0 
				&& (builder.charAt(builder.length() - 1) == ' ' 
				|| builder.charAt(builder.length() - 1) == '-')) {
			builder.deleteCharAt(builder.length() - 1);
		}
		//REMOVE DUPLICATE SPACERS
		for(int i = 1; i < builder.length(); i++) {
			if((builder.charAt(i) == ' '
					|| builder.charAt(i) == '-')
					&& builder.charAt(i) == builder.charAt(i - 1)) {
				builder.deleteCharAt(i);
				i--;
			}
		}
		//REMOVE HANGING HYPHENS
		for(int i = 1; i < builder.length() - 1; i++) {
			if(builder.charAt(i) == '-') {
				if((builder.charAt(i - 1) == ' '
						&& builder.charAt(i + 1) != ' ')
						|| (builder.charAt(i - 1) != ' '
						&& builder.charAt(i + 1) == ' ')) {
					builder.deleteCharAt(i);
					i--;
				}
			}
		}
		//TRUNCATE STRING
		//TODO CREATE TEST FOR CHECKING NOT TRUNCATING THE STRING
		String cleaned = builder.toString();
		if(length != -1) {
			cleaned = truncate_string(cleaned, length);
		}
		//RETURN CLEANED STRING
		if(cleaned.length() == 0) {
			return "0";
		}
		return cleaned;
	}
	
	/**
	 * Shortens a given string to be at or below a given length.
	 * Attempts to keep readable by removing characters at break-points.
	 * 
	 * @param str Given String
	 * @param length Maximum length of the returned String
	 * @return Shortened String
	 */
	public static String truncate_string(final String str, final int length) {
		if(str == null || length < 1) {
			return new String();
		}
		if(str.length() <= length) {
			return str;
		}
		// GET INDEX TO REMOVE FROM
		int index;
		if(str.contains(" ")) {
			index = str.lastIndexOf(' ');
		}
		else if(str.contains("-")) {
			index = str.lastIndexOf('-');
		}
		else {
			index = (int)Math.floor(str.length() / 2);
		}
		// DELETE CHARACTERS
		StringBuilder tr = new StringBuilder(str);
		if(index < tr.length() - index) {
			index++;
			while(index < tr.length() && length < tr.length()) {
				tr.deleteCharAt(index);
			}
		}
		else {
			index--;
			while(index > -1 && tr.length() > length) {
			    tr.deleteCharAt(index);
			    index--;
			}
			if (index > -1
					&& index < tr.length() - 2
					&& tr.charAt(index) == tr.charAt(index + 1)
					&& (tr.charAt(index) == ' '
					|| tr.charAt(index) == '-')) {
				tr.deleteCharAt(index);
			}
		}
		//IF STILL TOO LONG
		if(tr.length() > length) {
			tr = new StringBuilder(tr.substring(0, length));
		}
		//REMOVE START AND END SPACERS
		while(tr.length() > 0 
				&& (tr.charAt(0) == ' ' 
				|| tr.charAt(0) == '-')) {
			tr.deleteCharAt(0);
		}
		while(tr.length() > 0 
				&& (tr.charAt(tr.length() - 1) == ' ' 
				|| tr.charAt(tr.length() - 1) == '-')) {
			tr.deleteCharAt(tr.length() - 1);
		}
		return tr.toString();
	}
	
	/**
	 * Returns the extension for a given filename.
	 * If extension does not exist, returns empty.
	 * 
	 * @param filename Given filename.
	 * @return Extension for filename
	 */
	public static String get_extension(String filename) {
		if(filename == null) {
			return new String();
		}
		int end = filename.lastIndexOf('?');
		if(end == -1) {
			end = filename.length();
		}
		int start = filename.lastIndexOf('.', end);
		if(start == -1 || end - start > 6) {
			return new String();
		}
		return filename.substring(start, end);
	}
	
	/**
	 * Removes a section from given text.
	 * 
	 * @param text Text to remove from
	 * @param start Start index of section to remove (inclusive)
	 * @param end End index of section to remove (exclusive)
	 * @return Text with section removed
	 */
	public static String remove_section(String text, int start, int end)
	{
		StringBuilder builder = new StringBuilder();
		if(start <= text.length()) {
			builder.append(text.substring(0, start));
		}
		if(end < text.length()) {
			builder.append(text.substring(end));
		}
		return builder.toString();
	}
}
