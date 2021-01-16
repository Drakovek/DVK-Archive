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
		//RETURNS EMPTY STRING IF GIVEN STRING IS NOT A VALID HTML ESCAPE CHARACTER
		if(escape == null || !escape.startsWith("&") || !escape.endsWith(";")) {
			return new String();
		}
		//REMOVE STARTING $ AND ENDING ; FROM THE ESCAPE CHARACTER
		String mid = escape.substring(1, escape.length() - 1);
		//IF HTML ESCAPE CHARACTER REFERS TO A UNICODE INDEX
		if(mid.startsWith("#")) {
			try {
				//RETURN UNICODE CHARACTER
				char out = (char)Integer.parseInt(mid.substring(1));
				return String.valueOf(out);
			}
			catch(Exception e) {
				//IF UNICODE INDEX ISN'T VALID, RETURN EMPTY STRING
				return new String();
			}
		}
		//RETURN CHARACTER FOR HTML SPECIFIC ESCAPE CHARACTERS
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
		//RETURNS EMPTY STRING IF GIVEN STRING IS NULL
		if(str == null) {
			return new String();
		}
		//RUN WHILE STRING CONTAINS HTML ESCAPE CHARACTERS
		int end;
		String out = str;
		int start = out.indexOf('&');
		while(start != -1) {
			//GET AND CONVERT HTML ESCAPE CHARACTER
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
		//RETURNS AN EMPTY STRING IF THE GIVEN STRING IS NULL
		if(str == null) {
			return new String();
		}
		//RUN THROUGH EACH CHARACTER IN THE GIVEN STRING
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char value = str.charAt(i);
			if((value > 47 && value < 58)
					|| (value > 64 && value < 91)
					|| (value > 96 && value < 124)
					|| value == ' ') {
				//IF CURRENT CHARACTER IS a-z, A-Z, 0-9, or a space, use the original character
				builder.append(value);
			}
			else {
				//IF CURRENT CHARACTER IS NOT ALPHA-NUMERIC, USE THE HTML ESCAPE CHARACTER
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
		//RETURNS EMPTY STRING IF THE GIVEN STRING IS NULL
		if(str == null) {
			return new String();
		}
		//RUN THROUGH EACH CHARACTER OF THE GIVEN STRING
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char char_value = str.charAt(i);
			if (char_value == '"' || char_value == '\'') {
				//LEAVE TEXT IN QUOTES ALONE
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
				//LEAVE ALL LATIN CHARACTERS AND HTML CHARACTERS ALONE
				output.append(str.charAt(i));
			}
			else {
				//REPLACE NON-STANDARD CHARACTERS
				output.append("&#");
				output.append(Integer.toString(char_value));
				output.append(";");
			}
		}
		//RETURN MODIFIED STRING
		return output.toString();
	}
	
	/**
	 * Cleans up HTML element.
	 * Removes whitespace and removes header and footer tags.
	 * 
	 * @param html HTML element
	 * @param remove_ends Whether to remove header and footer tags
	 * @return Cleaned HTML element
	 */
	public static String clean_element(String html, boolean remove_ends) {
		//RETURNS EMPTY STRING IF GIVEN ELEMENT IS NULL
		if(html == null) {
			return new String();
		}
		//REMOVE NEW LINE AND CARRIAGE RETURN CHARACTERS
		String str = html.replace("\n", "");
		str = str.replace("\r", "");
		//REMOVE WHITESPACE BETWEEN TAGS
		while(str.contains("  <")) {
			str = str.replace("  <", " <");
		}
		while(str.contains(">  ")) {
			str = str.replace(">  ", "> ");
		}
		//REMOVE HEADER AND FOOTER, IF SPECIFIED
		if(remove_ends) {
			str = StringProcessing.remove_whitespace(str);
			//REMOVE HEADER
			if(str.startsWith("<")) {
				int start = str.indexOf('>');
				if(start != -1) {
					str = str.substring(start + 1);
				}
			}
			//REMOVE FOOTER
			if(str.endsWith(">")) {
				int end = str.lastIndexOf('<');
				if(end != -1) {
					str = str.substring(0, end);
				}
			}
		}
		
		//REMOVE WHITESPACE FROM THE START AND END OF STRING
		str = StringProcessing.remove_whitespace(str);
		return str;
	}
}
