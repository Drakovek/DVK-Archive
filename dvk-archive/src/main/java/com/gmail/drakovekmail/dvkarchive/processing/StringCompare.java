package com.gmail.drakovekmail.dvkarchive.processing;

import java.util.Comparator;

/**
 * Class with methods for comparing Strings alpha-numerically.
 * 
 * @author Drakovek
 */
public class StringCompare implements Comparator<String>
{
	/**
	 * Returns whether a given char is a digit (0-9).
	 * 
	 * @param chr Given char
	 * @return Whether chr is a digit
	 */
	public static boolean is_digit(final char chr)
	{
		if(chr > 47 && chr < 58)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Returns whether a given string starts with numerical data.
	 * Returns true if first character is a number or decimal mark.
	 * 
	 * @param str Given String
	 * @return Whether str starts with numerical data
	 */
	public static boolean is_number_string(final String str)
	{
		if(str == null || str.length() < 1)
		{
			return false;
		}
		char first = str.charAt(0);
		if(str.length() > 1 && is_digit(str.charAt(1)))
		{
			if(first == '.' || first == ',')
			{
				return true;
			}
		}
		return is_digit(first);
	}
	
	/**
	 * Returns the first section of a given String.
	 * Section contains either text only or number only data.
	 * 
	 * @param str Given String
	 * @return First section of str.
	 */
	public static String get_section(final String str)
	{
		if(str == null || str.length() == 0)
		{
			return new String();
		}
		int end;
		boolean is_num = is_number_string(str);
		//IF TEXT, GET TEXT SECTION
		if(!is_num)
		{
			for(end = 1; end < str.length() && !is_number_string(str.substring(end)); end++);
			return str.substring(0, end);
		}
		//GET FIRST NUMBER SECTION
		end = 0;
		while(end < str.length() && is_digit(str.charAt(end)))
		{
			end++;
		}
		if(!is_number_string(str.substring(end)))
		{
			return str.substring(0, end);
		}
		//GET FULL DECIMAL
		end++;
		while(end < str.length() && is_digit(str.charAt(end)))
		{
			end++;
		}
		return str.substring(0, end);
	}
	
	/**
	 * Compares two String sections alpha-numerically.
	 * Strings must contain only text data or only numerical data.
	 * 
	 * @param str1 String 1
	 * @param str2 String 2
	 * @return < 0 if str1 is first, > 0 if str2 is first, 0 if both are equal
	 */
	public static int compare_sections(final String str1, final String str2)
	{
		if(str1 == null || str2 == null)
		{
			return 0;
		}
		boolean digit1 = is_number_string(str1);
		boolean digit2 = is_number_string(str2);
		if(digit1 && digit2 && str1.length() < 11 && str2.length() < 11)
		{
			try
			{
				float val1 = Float.parseFloat(str1.replaceAll(",", "."));
				float val2 = Float.parseFloat(str2.replaceAll(",", "."));
				if(val1 < val2)
				{
					return -1;
				}
				else if (val1 > val2)
				{
					return 1;
				}
				return 0;
			}
			catch(NumberFormatException e) {}
		}
		return str1.toLowerCase().compareTo(str2.toLowerCase());
	}
	
	/**
	 * Compares two strings alpha-numerically
	 * 
	 * @param str1 String 1
	 * @param str2 String 2
	 * @return < 0 if str1 is first, > 0 if str2 is first, 0 if both are equal
	 */
	public static int compare_alphanum(final String str1, final String str2)
	{
		if(str1 == null || str2 == null || str1.toLowerCase().equals(str2.toLowerCase()))
		{
			return 0;
		}
		int result = 0;
		String end1 = str1;
		String end2 = str2;
		while(result == 0 && (end1.length() > 0 || end2.length() > 0))
		{
			String section1 = get_section(end1);
			String section2 = get_section(end2);
			end1 = end1.substring(section1.length());
			end2 = end2.substring(section2.length());
			result = compare_sections(section1, section2);
		}
		if(result != 0)
		{
			return result;
		}
		return str1.toLowerCase().compareTo(str2.toLowerCase());
	}

	@Override
	public int compare(String str1, String str2)
	{
		return compare_alphanum(str1, str2);
	}
}
