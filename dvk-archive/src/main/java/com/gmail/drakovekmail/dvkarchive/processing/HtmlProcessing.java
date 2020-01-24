package com.gmail.drakovekmail.dvkarchive.processing;

/**
 * Class with methods to deal with HTML data.
 * 
 * @author Drakovek
 */
public class HtmlProcessing {
	
	/**
	 * Returns single character for a given HTML escape character.
	 * Returned in string format. Empty if escape is invalid. 
	 * 
	 * @param escape HTML escape character
	 * @return Unicode character
	 */
	public static String escape_to_char(final String escape) {
		if(escape == null || !escape.startsWith("&") || !escape.endsWith(";")) {
			return new String();
		}
		String mid = escape.substring(1, escape.length() - 1);
		if(mid.startsWith("#")) {
			try {
				char out = (char)Integer.parseInt(mid.substring(1));
				return String.valueOf(out);
			}
			catch(Exception e) {
				return new String();
			}
		}
		switch(mid) {
			case "quot": return "\"";
			case "apos": return "'";
			case "amp": return "&";
			case "lt": return "<";
			case "gt": return ">";
			case "nbsp": return " ";
			default: return new String();
		}
	}
	
	/**
	 * Replaces all HTML escape characters in a string with Unicode characters.
	 * 
	 * @param str Given String
	 * @return String with HTML escape characters replaced
	 */
	public static String replace_escapes(final String str) {
		if(str == null) {
			return new String();
		}
		String out = str;
		int end;
		int start = out.indexOf('&');
		while(start != -1) {
			end = out.indexOf(';', start);
			if(end != -1) {
				end++;
				StringBuilder builder = new StringBuilder();
				builder.append(out.substring(0, start));
				builder.append(escape_to_char(out.substring(start, end)));
				builder.append(out.substring(end));
				out = builder.toString();
				start = out.indexOf('&', start);
			}
			else {
				start = -1;
			}
		}
		return out;
	}
	
	/**
	 * Replaces all uncommon characters in a String with HTML escapes.
	 * 
	 * @param str Given String
	 * @return String with added HTML escape characters
	 */
	public static String add_escapes(final String str) {
		if(str == null) {
			return new String();
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char value = str.charAt(i);
			if((value > 47 && value < 58)
					|| (value > 64 && value < 91)
					|| (value > 96 && value < 124)
					|| value == ' ') {
				builder.append(value);
			}
			else {
				builder.append("&#");
				builder.append(Integer.toString(value));
				builder.append(";");
			}
		}
		return builder.toString();
	}
	
	/**
	 * Replaces all uncommon characters in a String with HTML escapes.
	 * Keeps HTML tags and structures intact.
	 * 
	 * @param str Given HTML String
	 * @return String with added HTML escape characters
	 */
	public static String add_escapes_to_html(final String str) {
		if(str == null) {
			return new String();
		}
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char char_value = str.charAt(i);
			if (char_value == '"' || char_value == '\'') {
				int end = str.indexOf('"', i + 1) + 1;
				if(end == 0) {
					end = str.indexOf('\'', i + 1) + 1;
				}
				if(end == 0) {
					end = str.length();
				}
				output.append(str.substring(i, end));
				i = end - 1;
			}
			else if(char_value > 31 && char_value < 127) {
				output.append(str.charAt(i));
			}
			else {
				output.append("&#");
				output.append(Integer.toString(char_value));
				output.append(";");
			}
		}
		return output.toString();
	}
}
