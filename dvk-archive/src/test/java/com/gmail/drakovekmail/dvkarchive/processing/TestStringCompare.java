package com.gmail.drakovekmail.dvkarchive.processing;

import junit.framework.TestCase;

/**
 * Unit tests for the StringCompare class.
 * 
 * @author Drakovek
 */
public class TestStringCompare extends TestCase {
	/**
	 * Tests the is_digit method.
	 */
	public static void test_is_digit() {
		assertFalse(StringCompare.is_digit(' '));
		assertFalse(StringCompare.is_digit('A'));
		assertFalse(StringCompare.is_digit(':'));
		assertFalse(StringCompare.is_digit('/'));
		assertTrue(StringCompare.is_digit('0'));
		assertTrue(StringCompare.is_digit('5'));
		assertTrue(StringCompare.is_digit('9'));
	}
	
	/**
	 * Tests the is_number_string method.
	 */
	public static void test_is_number_string() {
		assertFalse(StringCompare.is_number_string(""));
		assertFalse(StringCompare.is_number_string(null));
		assertFalse(StringCompare.is_number_string("string02"));
		assertTrue(StringCompare.is_number_string("25 Thing"));
		assertTrue(StringCompare.is_number_string(".34 Thing"));
		assertTrue(StringCompare.is_number_string(",53.4"));
		assertFalse(StringCompare.is_number_string(".not number"));
		assertFalse(StringCompare.is_number_string(", nope"));
	}
	
	/**
	 * Tests the get_section method.
	 */
	public static void test_get_section() {
		assertEquals("", StringCompare.get_section(""));
		assertEquals("", StringCompare.get_section(null));
		assertEquals("Some Text", StringCompare.get_section("Some Text"));
		assertEquals("Test#", StringCompare.get_section("Test#1 - Other"));
		assertEquals("256", StringCompare.get_section("256"));
		assertEquals("15", StringCompare.get_section("15 Thing"));
		assertEquals("10.5", StringCompare.get_section("10.5"));
		assertEquals("12,5", StringCompare.get_section("12,5"));
		assertEquals("10.5", StringCompare.get_section("10.5,10"));
		assertEquals("12,5", StringCompare.get_section("12,5.12"));
		assertEquals(".25", StringCompare.get_section(".25 Text"));
		assertEquals(",50", StringCompare.get_section(",50.2 Thing"));
		assertEquals("T, s.!", StringCompare.get_section("T, s.!"));
		assertEquals("Num: ", StringCompare.get_section("Num: .02!"));
		assertEquals("# ", StringCompare.get_section("# ,40"));
	}
	
	/**
	 * Tests the compare_sections method.
	 */
	public static void test_compare_sections() {
		assertEquals(0, StringCompare.compare_sections(null, null));
		assertEquals(0, StringCompare.compare_sections("A", null));
		assertEquals(0, StringCompare.compare_sections(null, "A"));
		assertTrue(StringCompare.compare_sections("", "a") < 0);
		assertTrue(StringCompare.compare_sections("word", "") > 0);
		assertTrue(StringCompare.compare_sections("B", "a") > 0);
		assertTrue(StringCompare.compare_sections("a", "B") < 0);
		assertTrue(StringCompare.compare_sections("text", "58") > 0);
		assertTrue(StringCompare.compare_sections("24", "string") < 0);
		assertEquals(0, StringCompare.compare_sections("0.5", ".5"));
		assertEquals(0, StringCompare.compare_sections(",5", ".5"));
		assertTrue(StringCompare.compare_sections("010.5", "1.5") > 0);
		assertTrue(StringCompare.compare_sections("0.05", ".5") < 0);
		assertTrue(StringCompare.compare_sections(".2", ".02") > 0);
		assertTrue(StringCompare.compare_sections("00000001", "0") > 0);
		String str1 = "123450000000000000000000000000000000000000000";
		String str2 = "123450000000000000000000000000000000000000001";
		assertTrue(StringCompare.compare_sections(str1, str2) < 0);
		str1 = ".123450000000000000000000000000000000000000001";
		str2 = ".123450000000000000000000000000000000000000000";
		assertTrue(StringCompare.compare_sections(str1, str2) > 0);
	}
	
	/**
	 * Tests the compare_alphanum method.
	 */
	public static void test_compare_alphanum() {
		assertEquals(0, StringCompare.compare_alphanum(null, null));
		assertEquals(0, StringCompare.compare_alphanum(null, "a"));
		assertEquals(0, StringCompare.compare_alphanum("b", null));
		assertTrue(StringCompare.compare_alphanum("B", "a") > 0);
		assertTrue(StringCompare.compare_alphanum("a", "B") < 0);
		assertTrue(StringCompare.compare_alphanum("T1", "T2") < 0);
		assertTrue(StringCompare.compare_alphanum("St 0100", "st 2") > 0);
		assertEquals(0, StringCompare.compare_alphanum("Same25", "same25"));
		assertTrue(StringCompare.compare_alphanum("T 0.5", "T 0,05") > 0);
		assertTrue(StringCompare.compare_alphanum("v1.2.10", "v1.2.02") > 0);
		assertTrue(StringCompare.compare_alphanum("T 5 Ext", "T 20") < 0);
		assertTrue(StringCompare.compare_alphanum("T .6 0.5", "T 0.6 .25") > 0);
		assertTrue(StringCompare.compare_alphanum(".1", "0.75") < 0);
		assertTrue(StringCompare.compare_alphanum("T 0.5", "T 0,5") > 0);
	}
}
