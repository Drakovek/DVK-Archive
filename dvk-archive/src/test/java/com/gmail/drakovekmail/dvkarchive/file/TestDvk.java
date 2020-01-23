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
	 * Tests the get_dvk_file and set_dvk_file methods.
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
	 * Tests the get_id and set_id methods.
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
	 * Tests the get_title and set_title methods.
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
	
	/**
	 * Tests the get_artists, set_artist and set_artists methods.
	 */
	public static void test_get_set_artists()
	{
		Dvk dvk = new Dvk();
		//TEST SET_ARTIST
		dvk.set_artist(null);
		assertEquals(0, dvk.get_artists().length);
		dvk.set_artist("");
		assertEquals(1, dvk.get_artists().length);
		assertEquals("", dvk.get_artists()[0]);
		dvk.set_artist("Artist");
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Artist", dvk.get_artists()[0]);
		//TEST SET_ARTISTS
		dvk.set_artists(null);
		assertEquals(0, dvk.get_artists().length);
		String[] artists = new String[7];
		artists[0] = "artist10";
		artists[1] = "artist10";
		artists[2] = "";
		artists[3] = null;
		artists[4] = "artist1";
		artists[5] = "test1.0.20-stuff";
		artists[6] = "test10.0.0-stuff";
		dvk.set_artists(artists);
		assertEquals(5, dvk.get_artists().length);
		assertEquals("artist10", dvk.get_artists()[0]);
		assertEquals("", dvk.get_artists()[1]);
		assertEquals("artist1", dvk.get_artists()[2]);
		assertEquals("test1.0.20-stuff", dvk.get_artists()[3]);
		assertEquals("test10.0.0-stuff", dvk.get_artists()[4]);
	}
}
