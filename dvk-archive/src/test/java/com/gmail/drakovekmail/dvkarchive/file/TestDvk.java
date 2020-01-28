package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the Dvk class.
 * 
 * @author Drakovek
 */
public class TestDvk {
	
	/**
	 * Directory to hold all test files during testing.
	 */
	private File test_dir;
	
	/**
	 * Creates the test directory for holding test files.
	 */
	@Before
	public void create_test_directory() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "dvkobject");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
	}
	
	/**
	 * Deletes the test directory after testing.
	 */
	@After
	public void delete_test_directory() {
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the test_can_write method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_can_write() {
		Dvk dvk = new Dvk();
		File user_dir = new File(System.getProperty("user.dir"));
		dvk.set_dvk_file(new File(user_dir, "not_real.dvk"));
		dvk.set_id("id");
		dvk.set_title("title");
		dvk.set_artist("artist");
		dvk.set_page_url("page_url");
		dvk.set_media_file("media.png");
		assertTrue(dvk.can_write());
		dvk.set_dvk_file(null);
		assertFalse(dvk.can_write());
		dvk.set_dvk_file(new File("file.dvk"));
		dvk.set_id(null);
		assertFalse(dvk.can_write());
		dvk.set_id("id123");
		dvk.set_title(null);
		assertFalse(dvk.can_write());
		dvk.set_title("title");
		dvk.set_artist(null);
		assertFalse(dvk.can_write());
		dvk.set_artist("artist");
		dvk.set_page_url(null);
		assertFalse(dvk.can_write());
		dvk.set_page_url("page_url");
		dvk.set_media_file(null);
		assertFalse(dvk.can_write());
	}
	
	/**
	 * Tests the read_dvk and write_dvk methods.
	 */
	@Test
	public void test_read_write_dvk() {
		//SET DVK DATA
		Dvk dvk = new Dvk();
		File dvk_file = new File(this.test_dir, "dvk1.dvk");
		dvk.set_dvk_file(dvk_file);
		dvk.set_id("id1234");
		dvk.set_title("WriteTestTitle");
		String[] artists = {"other", "Artist"};
		dvk.set_artists(artists);
		dvk.set_time_int(1864, 10, 31, 7, 2);
		String[] web_tags = {"test", "Tags"};
		dvk.set_web_tags(web_tags);
		dvk.set_description("<b>desc</b>");
		dvk.set_page_url("http://somepage.com");
		dvk.set_direct_url("http://image.png");
		dvk.set_secondary_url("https://other.png");
		dvk.set_media_file("media.png");
		dvk.set_secondary_file("2nd.jpg");
		//WRITE THEN READ
		dvk.write_dvk();
		dvk.clear_dvk();
		dvk.set_dvk_file(dvk_file);
		dvk.read_dvk();
		//CHECK VALUES
		String in = dvk.get_dvk_file().getAbsolutePath();
		assertEquals(dvk_file.getAbsolutePath(), in);
		assertEquals("ID1234", dvk.get_id());
		assertEquals("WriteTestTitle", dvk.get_title());
		assertEquals(2, dvk.get_artists().length);
		assertEquals("Artist", dvk.get_artists()[0]);
		assertEquals("other", dvk.get_artists()[1]);
		assertEquals("1864/10/31|07:02", dvk.get_time());
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals("test", dvk.get_web_tags()[0]);
		assertEquals("Tags", dvk.get_web_tags()[1]);
		assertEquals("<b>desc</b>", dvk.get_description());
		assertEquals("http://somepage.com", dvk.get_page_url());
		assertEquals("http://image.png", dvk.get_direct_url());
		assertEquals("https://other.png", dvk.get_secondary_url());
		assertEquals("media.png", dvk.get_media_file().getName());
		assertEquals("2nd.jpg", dvk.get_secondary_file().getName());
		//TEST READING N0N-EXISTANT FILE
		dvk.set_dvk_file(null);
		dvk.read_dvk();
		assertEquals(null, dvk.get_title());
		dvk.set_dvk_file(new File(this.test_dir, "kjsdf.txt"));
		dvk.read_dvk();
		assertEquals(null, dvk.get_title());
		//TEST READING NON-DVK FILES
		dvk_file = new File(this.test_dir, "other.dvk");
		InOut.write_file(dvk_file, "Not a dvk");
		dvk.set_dvk_file(dvk_file);
		dvk.read_dvk();
		assertEquals(null, dvk.get_title());
		JSONObject json = new JSONObject();
		json.put("thing", "nope");
		json.put("not", "dvk");
		InOut.write_file(dvk_file, json.toString());
		dvk.set_dvk_file(dvk_file);
		dvk.read_dvk();
		assertEquals(null, dvk.get_title());
		json.put("file_type", "not_dvk");
		InOut.write_file(dvk_file, json.toString());
		dvk.set_dvk_file(dvk_file);
		dvk.read_dvk();
		assertEquals(null, dvk.get_title());
		// TEST WRITING INVALID FILE
		dvk_file = new File(this.test_dir, "dvk2.dvk");
		Dvk invalid = new Dvk();
		invalid.set_dvk_file(dvk_file);
		invalid.write_dvk();
		assertFalse(dvk_file.exists());
	}
	
	/**
	 * Tests the get_dvk_file and set_dvk_file methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_dvk_file() {
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(null);
		assertEquals(null, dvk.get_dvk_file());
		dvk.set_dvk_file(new File("blah.dvk"));
		assertEquals("blah.dvk", dvk.get_dvk_file().getName());
	}
	
	/**
	 * Tests the get_id and set_id methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_id() {
		Dvk dvk = new Dvk();
		dvk.set_id(null);
		assertEquals(null, dvk.get_id());
		dvk.set_id("");
		assertEquals(null, dvk.get_id());
		dvk.set_id("id1234");
		assertEquals("ID1234", dvk.get_id());	
	}
	
	/**
	 * Tests the get_title and set_title methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_title() {
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
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_artists() {
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
	@Test
	@SuppressWarnings("static-method")
	public void test_set_time_int() {
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
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_time() {
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
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_web_tags() {
		Dvk dvk = new Dvk();
		dvk.set_web_tags(null);
		assertTrue(dvk.get_web_tags() == null);
		dvk.set_web_tags(new String[0]);
		assertTrue(dvk.get_web_tags() == null);
		String[] input = {"tag1", "Tag2", "tag1", null};
		dvk.set_web_tags(input);
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals("tag1", dvk.get_web_tags()[0]);
		assertEquals("Tag2", dvk.get_web_tags()[1]);
	}
	
	/**
	 * Tests the get_description and set_descritpion methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_description() {
		Dvk dvk = new Dvk();
		dvk.set_description(null);
		assertEquals(null, dvk.get_description());
		dvk.set_description("");
		assertEquals(null, dvk.get_description());
		dvk.set_description("   ");
		assertEquals(null, dvk.get_description());
		dvk.set_description("   <i>Baño</i>  ");
		assertEquals("<i>Ba&#241;o</i>", dvk.get_description());
		dvk.set_description("<i>Ba&#241;o</i>");
		assertEquals("<i>Ba&#241;o</i>", dvk.get_description());
	}
	
	/**
	 * Tests the get_page_url and set_page_url methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_page_url() {
		Dvk dvk = new Dvk();
		dvk.set_page_url(null);
		assertEquals(null, dvk.get_page_url());
		dvk.set_page_url("");
		assertEquals(null, dvk.get_page_url());
		dvk.set_page_url("/Page/url");
		assertEquals("/Page/url", dvk.get_page_url());
	}
	
	/**
	 * Tests the get_direct_url and set_direct_url methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_direct_url() {
		Dvk dvk = new Dvk();
		dvk.set_direct_url(null);
		assertEquals(null, dvk.get_direct_url());
		dvk.set_direct_url("");
		assertEquals(null, dvk.get_direct_url());
		dvk.set_direct_url("/direct/URL");
		assertEquals("/direct/URL", dvk.get_direct_url());
	}
	
	/**
	 * Tests the get_secondary_url and set_secondary_url methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_secondary_url() {
		Dvk dvk = new Dvk();
		dvk.set_secondary_url(null);
		assertEquals(null, dvk.get_secondary_url());
		dvk.set_secondary_url("");
		assertEquals(null, dvk.get_secondary_url());
		dvk.set_secondary_url("/Page/url");
		assertEquals("/Page/url", dvk.get_secondary_url());
	}
	
	/**
	 * Tests the get_media_file and set_media_file methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_media_file() {
		Dvk dvk = new Dvk();
		dvk.set_media_file("bleh");
		assertEquals(null, dvk.get_media_file());
		dvk.set_dvk_file(new File("blahFolder"));
		dvk.set_media_file("media");
		assertEquals(null, dvk.get_media_file());
		String user_dir = System.getProperty("user.dir");
		File parent = new File(user_dir);
		dvk.set_dvk_file(new File(parent, "thing.dvk"));
		dvk.set_media_file(null);
		assertEquals(null, dvk.get_media_file());
		dvk.set_media_file("media.png");
		String out = parent.getAbsolutePath();
		File media = dvk.get_media_file().getParentFile();
		assertEquals(out, media.getAbsolutePath());
		assertEquals("media.png", dvk.get_media_file().getName());
		dvk.set_media_file("");
		assertEquals(null, dvk.get_media_file());
	}
	
	/**
	 * Tests the get_secondary_file and set_secondar_file methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_secondary_file() {
		Dvk dvk = new Dvk();
		dvk.set_secondary_file("bleh");
		assertEquals(null, dvk.get_secondary_file());
		dvk.set_dvk_file(new File("blahFolder"));
		dvk.set_secondary_file("media");
		assertEquals(null, dvk.get_secondary_file());
		String user_dir = System.getProperty("user.dir");
		File parent = new File(user_dir);
		dvk.set_dvk_file(new File(parent, "thing.dvk"));
		dvk.set_secondary_file(null);
		assertEquals(null, dvk.get_secondary_file());
		dvk.set_secondary_file("media.png");
		String out = parent.getAbsolutePath();
		File media = dvk.get_secondary_file().getParentFile();
		assertEquals(out, media.getAbsolutePath());
		assertEquals("media.png", dvk.get_secondary_file().getName());
		dvk.set_secondary_file("");
		assertEquals(null, dvk.get_secondary_file());
	}
}
