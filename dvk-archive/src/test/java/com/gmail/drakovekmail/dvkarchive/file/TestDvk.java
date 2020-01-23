package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;

import junit.framework.TestCase;

/**
 * Unit tests for the Dvk class.
 * 
 * @author Drakovek
 */
public class TestDvk extends TestCase
{
	/**
	 * Tests the get_dvk_file() and set_dvk_file() methods.
	 */
	public static void test_get_set_dvk_file()
	{
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(null);
		assertEquals(null, dvk.get_dvk_file());
		dvk.set_dvk_file(new File("blah.dvk"));
		assertEquals("blah.dvk", dvk.get_dvk_file().getName());
	}
	
	/**
	 * Tests the get_id() and set_id() methods.
	 */
	public static void test_get_set_id()
	{
		Dvk dvk = new Dvk();
		dvk.set_id(null);
		assertEquals("", dvk.get_id());
		dvk.set_id("");
		assertEquals("", dvk.get_id());
		dvk.set_id("id1234");
		assertEquals("ID1234", dvk.get_id());	
	}
	
	/**
	 * Tests the get_title() and set_title methods.
	 */
	public static void test_get_set_title()
	{
		Dvk dvk = new Dvk();
		dvk.set_title(null);
		assertEquals(null, dvk.get_title());
		dvk.set_title("");
		assertEquals("", dvk.get_title());
		dvk.set_title("Test Title");
		assertEquals("Test Title", dvk.get_title());
	}
}
