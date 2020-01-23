package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;

import junit.framework.TestCase;

/**
 * Unit tests for the Dvk class.
 * 
 * @author Drakovek
 */
public class TestDvk extends TestCase {
	/**
	 * Tests the get_dvk_file and set_dvk_file methods.
	 */
	public static void test_get_set_dvk_file() {
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(null);
		assertEquals(null, dvk.get_dvk_file());
		dvk.set_dvk_file(new File("blah.dvk"));
		assertEquals("blah.dvk", dvk.get_dvk_file().getName());
	}
	
	/**
	 * Tests the get_id and set_id methods.
	 */
	public static void test_get_set_id() {
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
	public static void test_get_set_title() {
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
	public static void test_get_set_artists() {
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
		assertEquals("", dvk.get_artists()[0]);
		assertEquals("artist1", dvk.get_artists()[1]);
		assertEquals("artist10", dvk.get_artists()[2]);
		assertEquals("test1.0.20-stuff", dvk.get_artists()[3]);
		assertEquals("test10.0.0-stuff", dvk.get_artists()[4]);
	}
	
	/**
	 * Tests the set_time_int method.
	 */
	public static void test_set_time_int() {
		Dvk dvk = new Dvk();
		dvk.set_time_int(0, 0, 0, 0, 0);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST INVALID YEAR
		dvk.set_time_int(0, 10, 10, 7, 15);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST INVALID MONTH
		dvk.set_time_int(2017, 0, 10, 7, 15);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time_int(2017, 13, 10, 7, 15);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST INVALID DAY
		dvk.set_time_int(2017, 10, 0, 7, 15);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time_int(2017, 10, 32, 7, 15);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST INVALID HOUR
		dvk.set_time_int(2017, 10, 10, -1, 15);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time_int(2017, 10, 10, 24, 15);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST INVALID MINUTE
		dvk.set_time_int(2017, 10, 10, 7, -1);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time_int(2017, 10, 10, 7, 60);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST VALID TIME
		dvk.set_time_int(2017, 10, 6, 19, 15);
		assertEquals("2017/10/06|19:15", dvk.get_time());
	}
	
	/**
	 * Tests the get_time() and set_time() methods.
	 */
	public static void test_get_set_time() {
		Dvk dvk = new Dvk();
		dvk.set_time(null);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time("2017/10/06");
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time("yyyy/mm/dd/hh/tt");
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time("2017!10!06!05!00");
		assertEquals("2017/10/06|05:00", dvk.get_time());
	}
	
	/**
	 * Tests the get_web_tags and set_web_tags methods.
	 */
	public static void test_get_set_web_tags() {
		Dvk dvk = new Dvk();
		dvk.set_web_tags(null);
		assertEquals(null, dvk.get_web_tags());
		dvk.set_web_tags(new String[0]);
		assertEquals(null, dvk.get_web_tags());
		String[] input = {"tag1", "Tag2", "tag1", null};
		dvk.set_web_tags(input);
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals("tag1", dvk.get_web_tags()[0]);
		assertEquals("Tag2", dvk.get_web_tags()[1]);
	}
}
