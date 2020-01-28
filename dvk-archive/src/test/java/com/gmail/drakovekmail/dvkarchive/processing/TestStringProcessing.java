package com.gmail.drakovekmail.dvkarchive.processing;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the StringProcessing class.
 * 
 * @author Drakovek
 */
public class TestStringProcessing {
	
	/**
	 * Tests the extend_int method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_extend_int() {
		assertEquals("00", StringProcessing.extend_int(256, 2));
		assertEquals("", StringProcessing.extend_int(12, 0));
		assertEquals("", StringProcessing.extend_int(12, -1));
		assertEquals("15", StringProcessing.extend_int(15, 2));
		assertEquals("00012", StringProcessing.extend_int(12, 5));
	}
	
	/**
	 * Tests the remove_whitespace method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_remove_whitespace() {
		assertEquals("", StringProcessing.remove_whitespace(null));
		assertEquals("", StringProcessing.remove_whitespace(""));
		assertEquals("", StringProcessing.remove_whitespace(" "));
		assertEquals("", StringProcessing.remove_whitespace("   "));
		assertEquals("blah", StringProcessing.remove_whitespace("  blah"));
		assertEquals("blah", StringProcessing.remove_whitespace("blah   "));
		assertEquals("blah", StringProcessing.remove_whitespace("  blah "));
		assertEquals("blah", StringProcessing.remove_whitespace("blah"));
	}
}
