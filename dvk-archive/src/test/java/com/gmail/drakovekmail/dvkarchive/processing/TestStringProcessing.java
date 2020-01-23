package com.gmail.drakovekmail.dvkarchive.processing;

import junit.framework.TestCase;

/**
 * Unit tests for the StringProcessing class.
 * 
 * @author Drakovek
 */
public class TestStringProcessing extends TestCase
{
	/**
	 * Tests the extend_int method.
	 */
	public static void test_extend_int()
	{
		assertEquals("00", StringProcessing.extend_int(256, 2));
		assertEquals("", StringProcessing.extend_int(12, 0));
		assertEquals("", StringProcessing.extend_int(12, -1));
		assertEquals("15", StringProcessing.extend_int(15, 2));
		assertEquals("00012", StringProcessing.extend_int(12, 5));
	}
}
