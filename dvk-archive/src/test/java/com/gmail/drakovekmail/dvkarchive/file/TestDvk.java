package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;
import com.google.common.io.Files;

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
	 * Main directory for holding test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Tests the test_can_write method.
	 */
	@Test
	public void test_can_write() {
		//TEST VALID DVK THAT CAN BE WRITTEN
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "write.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		assertTrue(dvk.can_write());
		//TEST WITH NO DVK FILE
		dvk.set_dvk_file(null);
		assertFalse(dvk.can_write());
		//TEST DVK FILE WITHOUT EXISTING PARENT DIRECTORY
		dvk.set_dvk_file(new File("/non-existant/file.dvk"));
		assertFalse(dvk.can_write());
		//TEST WITH NO DVK ID
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "write.dvk"));
		dvk.set_dvk_id(null);
		assertFalse(dvk.can_write());
		//TEST WITH NO TITLE
		dvk.set_dvk_id("id123");
		dvk.set_title(null);
		assertFalse(dvk.can_write());
		//TEST WITH NO ARTIST
		dvk.set_title("Title");
		dvk.set_artist(null);
		assertFalse(dvk.can_write());
		//TEST WITH NO PAGE URL
		dvk.set_artist("artist");
		dvk.set_page_url(null);
		assertFalse(dvk.can_write());
		//TEST WITH NO MEDIA FILE
		dvk.set_page_url("/url/");
		dvk.set_media_file("");
		assertFalse(dvk.can_write());
		//TEST VALID WRITABLE DVK
		dvk.set_media_file("file.txt");
		assertTrue(dvk.can_write());
	}
	
	/**
	 * Tests the read_dvk and write_dvk methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_read_write_dvk() {
		//TODO CHECK THAT NON-DVK JSON DATA IS STILL RETAINED
		assertTrue(true);
	}
	
	/**
	 * Tests the write_media method.
	 */
	@Test
	public void test_write_media() {
		try (DConnect connect = new DConnect(false, false)) {
			//CREATE DVK THAT CANNOT BE WRITTEN
			Dvk media_dvk = new Dvk();
			media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "inv.dvk"));
			media_dvk.set_dvk_id("id123");
			media_dvk.set_artist("artist");
			media_dvk.set_page_url("/url/");
			media_dvk.set_media_file("media.png");
			media_dvk.set_direct_url("http://www.pythonscraping.com/img/gifts/img6.jpg");
			//TEST WRITE MEDIA WHEN URL IS VALID, BUT DVK CANNOT BE WRITTEN
			media_dvk.write_media(connect);
			assertFalse(media_dvk.get_dvk_file().exists());
			assertFalse(media_dvk.get_media_file().exists());
			//TEST WRITE MEDIA WHEN DVK CAN BE WRITTEN, BUT URL IS INVALID
			media_dvk.set_title("Title");
			media_dvk.set_direct_url("!@#$%^");
			media_dvk.write_media(connect);
			assertFalse(media_dvk.get_dvk_file().exists());
			assertFalse(media_dvk.get_media_file().exists());
			//TEST VALID DIRECT MEDIA URL
			media_dvk.set_direct_url("http://www.pythonscraping.com/img/gifts/img6.jpg");
			media_dvk.write_media(connect);
			assertTrue(media_dvk.get_dvk_file().exists());
			assertTrue(media_dvk.get_media_file().exists());
			assertEquals("media.jpg", media_dvk.get_media_file().getName());
			assertEquals(39785L, media_dvk.get_media_file().length());
			//CREATE DVK WITH INVALID SECONDARY URL
			Dvk secondary_dvk = new Dvk();
			secondary_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "inv2.dvk"));
			secondary_dvk.set_dvk_id("id123");
			secondary_dvk.set_title("Title");
			secondary_dvk.set_artist("artist");
			secondary_dvk.set_page_url("/url/");
			secondary_dvk.set_media_file("primary.jpg");
			secondary_dvk.set_direct_url("http://www.pythonscraping.com/img/gifts/img6.jpg");
			secondary_dvk.set_secondary_file("secondary.txt");
			secondary_dvk.set_secondary_url("!@#$%^");
			//TEST WRITING MEDIA WITH VALID PRIMARY, BUT INVALID SECONDARY MEDIA URLS
			media_dvk.write_media(connect);
			assertFalse(secondary_dvk.get_dvk_file().exists());
			assertFalse(secondary_dvk.get_media_file().exists());
			assertFalse(secondary_dvk.get_secondary_file().exists());
			//TEST WRITING MEDIA WITH VALID PRIMARY AND SECONDARY MEDIA URLS
			secondary_dvk.set_secondary_url("http://www.pythonscraping.com/img/gifts/img4.jpg");
			secondary_dvk.write_media(connect);
			assertTrue(secondary_dvk.get_dvk_file().exists());
			assertTrue(secondary_dvk.get_media_file().exists());
			assertTrue(secondary_dvk.get_secondary_file().exists());
			assertEquals(39785L, secondary_dvk.get_media_file().length());
			assertEquals(85007L, secondary_dvk.get_secondary_file().length());
			assertEquals("secondary.jpg", secondary_dvk.get_secondary_file().getName());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_dvk_file and set_dvk_file methods.
	 */
	@Test
	public void test_get_set_dvk_file() {
		//TEST EMPTY DVK FILE WITH CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(null, dvk.get_dvk_file());
		//TEST GETTING AND SETTING DVK FILE
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "blah.dvk"));
		assertEquals("blah.dvk", dvk.get_dvk_file().getName());
		assertEquals(this.temp_dir.getRoot(), dvk.get_dvk_file().getParentFile());
		//TEST SETTING DVK FILE TO NULL
		dvk.set_dvk_file(null);
		assertEquals(null, dvk.get_dvk_file());
		
		//WRITE DVK FILE
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "new.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url");
		dvk.set_media_file("file.txt");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("new.dvk", read_dvk.get_dvk_file().getName());
		assertEquals(this.temp_dir.getRoot(), read_dvk.get_dvk_file().getParentFile());
	}
	
	/**
	 * Tests the get_dvk_id and set_dvk_id methods.
	 */
	@Test
	public void test_get_set_dvk_id() {
		//TEST EMPTY ID WITH CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(null, dvk.get_dvk_file());
		//TEST GETTING AND SETTING DVK ID
		dvk.set_dvk_id("id1234");
		assertEquals("ID1234", dvk.get_dvk_id());
		dvk.set_dvk_id("TST128-J");
		assertEquals("TST128-J", dvk.get_dvk_id());
		//TEST INVALID IDS
		dvk.set_dvk_id(null);
		assertEquals(null, dvk.get_dvk_id());
		dvk.set_dvk_id("");
		assertEquals(null, dvk.get_dvk_id());

		//WRITE DVK FILE
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "id_dvk.dvk"));
		dvk.set_dvk_id("WRITE456");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING DVK ID
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("WRITE456", read_dvk.get_dvk_id());
	}
	
	/**
	 * Tests the get_sql_id and set_sql_id methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_sql_id() {
		//TEST DEFAULT SQL ID FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(0, dvk.get_sql_id());
		//TEST GETTING AND SETTING SQL ID
		dvk.set_sql_id(15);
		assertEquals(15, dvk.get_sql_id());
		dvk.set_sql_id(25689746);
		assertEquals(25689746, dvk.get_sql_id());
	}
	
	/**
	 * Tests the get_title and set_title methods.
	 */
	@Test
	public void test_get_set_title() {
		//TEST EMPTY TITLE FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(null, dvk.get_title());
		//TEST GETTING AND SETTING TITLE
		dvk.set_title("Test Title");
		assertEquals("Test Title", dvk.get_title());
		dvk.set_title(null);
		assertEquals(null, dvk.get_title());
		dvk.set_title("");
		assertEquals("", dvk.get_title());
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "ttl_dvk.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title #2");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING TITLE FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("Title #2", read_dvk.get_title());
	}
	
	/**
	 * Tests the get_artists, set_artist and set_artists methods.
	 */
	@Test
	public void test_get_set_artists() {
		//TEST EMPTY ARTISTS FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(0, dvk.get_artists().length);
		//TEST GETTING AND SETTING SINGLE ARTIST
		dvk.set_artist("Artist Person");
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Artist Person", dvk.get_artists()[0]);
		dvk.set_artist("");
		assertEquals(1, dvk.get_artists().length);
		assertEquals("", dvk.get_artists()[0]);
		//TEST GETTING AND SETTING MULTIPLE ARTISTS
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
		assertEquals("", dvk.get_artists()[0]);
		assertEquals("artist1", dvk.get_artists()[1]);
		assertEquals("artist10", dvk.get_artists()[2]);
		assertEquals("test1.0.20-stuff", dvk.get_artists()[3]);
		assertEquals("test10.0.0-stuff", dvk.get_artists()[4]);
		//TEST SETTING INVALID ARTIST VALUES
		dvk.set_artist(null);
		assertEquals(0, dvk.get_artists().length);
		dvk.set_artists(null);
		assertEquals(0, dvk.get_artists().length);
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "artist.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		artists = new String[2];
		artists[0] = "Person";
		artists[1] = "artist";
		dvk.set_artists(artists);
		dvk.set_page_url("/url");
		dvk.set_media_file("file.txt");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING ARTISTS FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals(2, read_dvk.get_artists().length);
		assertEquals("artist", read_dvk.get_artists()[0]);
		assertEquals("Person", read_dvk.get_artists()[1]);
	}
	
	/**
	 * Tests the set_time_int method.
	 */
	@Test
	public void test_set_time_int() {
		//TEST EMPTY TIME FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST VALID TIME
		dvk.set_time_int(2017, 10, 6, 19, 15);
		assertEquals("2017/10/06|19:15", dvk.get_time());
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
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "time_dvk.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		dvk.set_time_int(2020, 10, 26, 21, 15);
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING TIME FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("2020/10/26|21:15", read_dvk.get_time());
	}
	
	/**
	 * Tests the get_time() and set_time() methods.
	 */
	@Test
	public void test_get_set_time() {
		//TEST DEFAULT TIME FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals("0000/00/00|00:00", dvk.get_time());
		//TEST GETTING AND SETTING TIME
		dvk.set_time("2017!10!06!05!00");
		assertEquals("2017/10/06|05:00", dvk.get_time());
		//TEST SETTING INVALID TIMES
		dvk.set_time(null);
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time("2017/10/06");
		assertEquals("0000/00/00|00:00", dvk.get_time());
		dvk.set_time("yyyy/mm/dd/hh/tt");
		assertEquals("0000/00/00|00:00", dvk.get_time());
		
		//WRITE DVK FILE
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "time.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		dvk.set_time("2020/10/27|17:55");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING TIME FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("2020/10/27|17:55", read_dvk.get_time());
	}
	
	/**
	 * Tests the get_web_tags and set_web_tags methods.
	 */
	@Test
	public void test_get_set_web_tags() {
		//TEST EMPTY WEB TAGS FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertTrue(dvk.get_web_tags() == null);
		//TEST GETTING AND SETTING WEB TAGS
		String[] tags = {"tag2", "Tag1", "tag2", null};
		dvk.set_web_tags(tags);
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals("tag2", dvk.get_web_tags()[0]);
		assertEquals("Tag1", dvk.get_web_tags()[1]);
		//TEST INVALID TAGS
		dvk.set_web_tags(null);
		assertTrue(dvk.get_web_tags() == null);
		dvk.set_web_tags(new String[0]);
		assertTrue(dvk.get_web_tags() == null);
		tags = new String[1];
		tags[0] = null;
		dvk.set_web_tags(tags);
		assertTrue(dvk.get_web_tags() == null);
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "tags.txt"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		tags = new String[2];
		tags[0] = "String";
		tags[1] = "tags";
		dvk.set_web_tags(tags);
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING WEB TAGS FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals(2, read_dvk.get_web_tags().length);
		assertEquals("String", read_dvk.get_web_tags()[0]);
		assertEquals("tags", read_dvk.get_web_tags()[1]);
	}
	
	/**
	 * Tests the get_description and set_descritpion methods.
	 */
	@Test
	public void test_get_set_description() {
		//TEST DEFAULT DESCRIPTION FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(null, dvk.get_description());
		//TEST GETTING AND SETTING DESCRIPTION
		dvk.set_description("   <i>Baño</i>  ");
		assertEquals("<i>Ba&#241;o</i>", dvk.get_description());
		dvk.set_description("<i>Ba&#241;o</i>");
		assertEquals("<i>Ba&#241;o</i>", dvk.get_description());
		//TEST SETTING EMPTY DESCRIPTION
		dvk.set_description(null);
		assertEquals(null, dvk.get_description());
		dvk.set_description("");
		assertEquals(null, dvk.get_description());
		dvk.set_description("   ");
		assertEquals(null, dvk.get_description());
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "desc.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		dvk.set_description("Other Description");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING DESCRIPTION FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("Other Description", read_dvk.get_description());
	}
	
	/**
	 * Tests the get_page_url and set_page_url methods.
	 */
	@Test
	public void test_get_set_page_url() {
		//TEST EMPTY PAGE URL FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(null, dvk.get_page_url());
		//TEST GETTING AND SETTING PAGE URL
		dvk.set_page_url("/Page/URL");
		assertEquals("/Page/URL", dvk.get_page_url());
		//TEST SETTING INVALID URL
		dvk.set_page_url(null);
		assertEquals(null, dvk.get_page_url());
		dvk.set_page_url("");
		assertEquals(null, dvk.get_page_url());
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "page.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("test.net/url/Page");
		dvk.set_media_file("file.txt");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING PAGE URL FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("test.net/url/Page", read_dvk.get_page_url());
	}
	
	/**
	 * Tests the get_direct_url and set_direct_url methods.
	 */
	@Test
	public void test_get_set_direct_url() {
		//TEST EMPTY DIRECT URL FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(null, dvk.get_direct_url());
		//TEST GETTING AND SETTING DIRECT URL
		dvk.set_direct_url("/direct/URL");
		assertEquals("/direct/URL", dvk.get_direct_url());
		//TEST SETTING INVALID DIRECT URL
		dvk.set_direct_url(null);
		assertEquals(null, dvk.get_direct_url());
		dvk.set_direct_url("");
		assertEquals(null, dvk.get_direct_url());
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "direct.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		dvk.set_direct_url("test.net/url/Direct");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING DIRECT URL
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("test.net/url/Direct", read_dvk.get_direct_url());
	}
	
	/**
	 * Tests the get_secondary_url and set_secondary_url methods.
	 */
	@Test
	public void test_get_set_secondary_url() {
		//TEST EMPTY SECONDARY URL FROM CONSTRUCTOR
		Dvk dvk = new Dvk();
		assertEquals(dvk.get_secondary_url(), null);
		//TEST GETTING AND SETTING SECONDARY URL
		dvk.set_secondary_url("/Page/URL");
		assertEquals("/Page/URL", dvk.get_secondary_url());
		//TEST SETTING INVALID SECONDARY URL
		dvk.set_secondary_url(null);
		assertEquals(null, dvk.get_secondary_url());
		dvk.set_secondary_url("");
		assertEquals(null, dvk.get_secondary_url());
		
		//WRITE DVK
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "sec.dvk"));
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/url/");
		dvk.set_media_file("file.txt");
		dvk.set_secondary_url("test.net/url/second");
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		//TEST READING SECONDARY URL FROM DVK FILE
		Dvk read_dvk = new Dvk(dvk.get_dvk_file());
		dvk = null;
		assertEquals("test.net/url/second", read_dvk.get_secondary_url());
	}
	
	/**
	 * Tests the get_media_file and set_media_file methods.
	 */
	@Test
	public void test_get_set_media_file() {
		try {
			//TEST EMPTY MEDIA FILE FROM CONSTRUCTOR
			Dvk dvk = new Dvk();
			assertEquals(null, dvk.get_media_file());
			//TEST SETTING MEDIA FILE WITH NO DVK FILE SET
			dvk.set_media_file("file.txt");
			assertEquals(null, dvk.get_media_file());
			dvk.set_media_file(new File(this.temp_dir.getRoot(), "file.txt"));
			assertEquals(null, dvk.get_media_file());
			//TEST SETTING MEDIA FILE FROM DVK FILE WITH INVALID PARENT
			dvk.set_dvk_file(new File("/non-existant/file.dvk"));
			dvk.set_media_file("file.txt");
			assertEquals(null, dvk.get_media_file());
			dvk.set_media_file(new File("/non-existant/file.dvk"));
			assertEquals(null, dvk.get_media_file());
			//TEST SETTING MEDIA WITH VALID DVK FILE
			dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "media.dvk"));
			dvk.set_media_file("file.txt");
			assertEquals(this.temp_dir.getRoot(), dvk.get_media_file().getParentFile());
			assertEquals("file.txt", dvk.get_media_file().getName());
			dvk.set_media_file(new File(this.temp_dir.getRoot(), "file.jpg"));
			assertEquals(this.temp_dir.getRoot(), dvk.get_media_file().getParentFile());
			assertEquals("file.jpg", dvk.get_media_file().getName());
			//TEST SETTING INVALID MEDIA FILE WITH INVALID DVK FILE
			File file = null;
			String str = null;
			dvk.set_media_file(file);
			assertEquals(null, dvk.get_media_file());
			dvk.set_media_file(str);
			assertEquals(null, dvk.get_media_file());
			dvk.set_media_file("");
			assertEquals(null, dvk.get_media_file());
			//TEST SETTING MEDIA FILE IN DIFFERENT PARENT DIRECTORY TO DVK FILE
			File sub = this.temp_dir.newFolder("sub");
			dvk.set_media_file(new File(sub, "file.txt"));
			assertEquals(null, dvk.get_media_file());
			
			//WRITE DVK
			dvk.set_dvk_file(new File(sub, "media.dvk"));
			dvk.set_dvk_id("id123");
			dvk.set_title("Title");
			dvk.set_artist("artist");
			dvk.set_page_url("/url/");
			dvk.set_media_file(new File(sub, "media.png"));
			dvk.write_dvk();
			assertTrue(dvk.get_dvk_file().exists());
			//TEST READING MEDIA FILE VALUE FROM DVK FILE
			Dvk read_dvk = new Dvk(dvk.get_dvk_file());
			dvk = null;
			assertEquals(sub, read_dvk.get_media_file().getParentFile());
			assertEquals("media.png", read_dvk.get_media_file().getName());
		}
		catch (IOException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_secondary_file and set_secondar_file methods.
	 */
	@Test
	public void test_get_set_secondary_file() {
		try {
			//TEST EMPTY SECONDARY MEDIA FILE FROM CONSTRUCTOR
			Dvk dvk = new Dvk();
			assertEquals(null, dvk.get_secondary_file());
			//TEST SETTING SECONDARY MEDIA FILE WITH NO DVK FILE SET
			dvk.set_secondary_file("file.txt");
			assertEquals(null, dvk.get_secondary_file());
			dvk.set_secondary_file(new File(this.temp_dir.getRoot(), "file.txt"));
			assertEquals(null, dvk.get_secondary_file());
			//TEST SETTING SECONDARY MEDIA FILE FROM DVK FILE WITH INVALID PARENT
			dvk.set_dvk_file(new File("/non-existant/file.dvk"));
			dvk.set_secondary_file("file.txt");
			assertEquals(null, dvk.get_secondary_file());
			dvk.set_secondary_file(new File("/non-existant/file.dvk"));
			assertEquals(null, dvk.get_secondary_file());
			//TEST SETTING SECONDARY MEDIA WITH VALID DVK FILE
			dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "media.dvk"));
			dvk.set_secondary_file("file.txt");
			assertEquals(this.temp_dir.getRoot(), dvk.get_secondary_file().getParentFile());
			assertEquals("file.txt", dvk.get_secondary_file().getName());
			dvk.set_secondary_file(new File(this.temp_dir.getRoot(), "file.jpg"));
			assertEquals(this.temp_dir.getRoot(), dvk.get_secondary_file().getParentFile());
			assertEquals("file.jpg", dvk.get_secondary_file().getName());
			//TEST SETTING INVALID SECONDARY MEDIA FILE WITH INVALID DVK FILE
			File file = null;
			String str = null;
			dvk.set_secondary_file(file);
			assertEquals(null, dvk.get_secondary_file());
			dvk.set_secondary_file(str);
			assertEquals(null, dvk.get_secondary_file());
			dvk.set_secondary_file("");
			assertEquals(null, dvk.get_secondary_file());
			//TEST SETTING SECONDARY MEDIA FILE IN DIFFERENT PARENT DIRECTORY TO DVK FILE
			File sub = this.temp_dir.newFolder("sub");
			dvk.set_secondary_file(new File(sub, "file.txt"));
			assertEquals(null, dvk.get_secondary_file());
			
			//WRITE DVK
			dvk.set_dvk_file(new File(sub, "media.dvk"));
			dvk.set_dvk_id("id123");
			dvk.set_title("Title");
			dvk.set_artist("artist");
			dvk.set_page_url("/url/");
			dvk.set_media_file("file.txt");
			dvk.set_secondary_file(new File(sub, "media.png"));
			dvk.write_dvk();
			assertTrue(dvk.get_dvk_file().exists());
			//TEST READING SECONDARY MEDIA FILE VALUE FROM DVK FILE
			Dvk read_dvk = new Dvk(dvk.get_dvk_file());
			dvk = null;
			assertEquals(sub, read_dvk.get_secondary_file().getParentFile());
			assertEquals("media.png", read_dvk.get_secondary_file().getName());
		}
		catch (IOException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_filename method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_filename() {
		//TEST WITH NO TITLE OR ID
		Dvk dvk = new Dvk();
		assertEquals("", dvk.get_filename(false));
		//TEST WITH ONLY TITLE
		dvk.set_title("Title");
		assertEquals("", dvk.get_filename(false));
		//TEST WITH ONLY ID
		dvk.set_dvk_id("ID123");
		dvk.set_title(null);
		assertEquals("", dvk.get_filename(false));
		//TEST WITH VALID TITLE AND ID
		dvk.set_title("a   B-cd!");
		assertEquals("a B-cd_ID123", dvk.get_filename(false));
		assertEquals("a B-cd_ID123_S", dvk.get_filename(true));
		//TEST WITH EMPTY TITLE
		dvk.set_title("");
		assertEquals("0_ID123", dvk.get_filename(false));
		assertEquals("0_ID123_S", dvk.get_filename(true));
	}
	
	/**
	 * Tests the rename_files method.
	 */
	@Test
	public void test_rename_files() {
		try {
			//CREATE TEST DVK WITHOUT EXISTING MEDIA FILES
			Dvk no_media_dvk = new Dvk();
			no_media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "rnm.dvk"));
			no_media_dvk.set_dvk_id("id123");
			no_media_dvk.set_title("Title");
			no_media_dvk.set_artist("Artist");
			no_media_dvk.set_page_url("/url/");
			no_media_dvk.set_media_file(new File(this.temp_dir.getRoot(), "file.txt"));
			no_media_dvk.set_secondary_file(new File(this.temp_dir.getRoot(), "sec.png"));
			no_media_dvk.write_dvk();
			assertTrue(no_media_dvk.get_dvk_file().exists());
			assertFalse(no_media_dvk.get_media_file().exists());
			assertFalse(no_media_dvk.get_secondary_file().exists());
			//TEST RENAMING DVK FILE
			no_media_dvk.rename_files("New", "New2");
			Dvk read_dvk = new Dvk(no_media_dvk.get_dvk_file());
			no_media_dvk = null;
			assertEquals("New.dvk", read_dvk.get_dvk_file().getName());
			assertEquals("New.txt", read_dvk.get_media_file().getName());
			assertEquals("New2.png", read_dvk.get_secondary_file().getName());
			assertTrue(read_dvk.get_dvk_file().exists());
			assertFalse(read_dvk.get_media_file().exists());
			assertFalse(read_dvk.get_secondary_file().exists());
			//CREATE TEST DVK WITH EXISTING MEDIA FILE
			Dvk media_dvk = new Dvk();
			media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "rnm.dvk"));
			media_dvk.set_dvk_id("id123");
			media_dvk.set_title("Title");
			media_dvk.set_artist("Artist");
			media_dvk.set_page_url("/url/");
			File media_file = this.temp_dir.newFile("media.png");
			media_dvk.set_media_file(media_file);
			media_dvk.write_dvk();
			assertTrue(media_dvk.get_dvk_file().exists());
			assertTrue(media_dvk.get_media_file().exists());
			assertEquals(null, media_dvk.get_secondary_file());
			//TEST RENAMING DVK FILE WITH MEDIA FILE
			media_dvk.rename_files("other", "other");
			read_dvk = new Dvk(media_dvk.get_dvk_file());
			media_dvk = null;
			assertEquals("other.dvk", read_dvk.get_dvk_file().getName());
			assertEquals("other.png", read_dvk.get_media_file().getName());
			assertEquals(null, read_dvk.get_secondary_file());
			assertTrue(read_dvk.get_dvk_file().exists());
			assertTrue(read_dvk.get_media_file().exists());
			//CREATE TEST DVK WITH EXISTING MEDIA AND SECONDARY FILES
			Dvk secondary_dvk = new Dvk();
			secondary_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "rnm.dvk"));
			secondary_dvk.set_dvk_id("id123");
			secondary_dvk.set_title("Title");
			secondary_dvk.set_artist("Artist");
			secondary_dvk.set_page_url("/url/");
			media_file = this.temp_dir.newFile("media.pdf");
			secondary_dvk.set_media_file(media_file);
			File sec_file = this.temp_dir.newFile("secondary.jpg");
			secondary_dvk.set_secondary_file(sec_file);
			secondary_dvk.write_dvk();
			assertTrue(secondary_dvk.get_dvk_file().exists());
			assertTrue(secondary_dvk.get_media_file().exists());
			assertTrue(secondary_dvk.get_secondary_file().exists());
			//TEST RENAMING DVK FILE WITH MEDIA AND SECONDARY FILES
			secondary_dvk.rename_files("First", "Second");
			read_dvk = new Dvk(secondary_dvk.get_dvk_file());
			secondary_dvk = null;
			assertEquals("First.dvk", read_dvk.get_dvk_file().getName());
			assertEquals("First.pdf", read_dvk.get_media_file().getName());
			assertEquals("Second.jpg", read_dvk.get_secondary_file().getName());
			assertTrue(read_dvk.get_dvk_file().exists());
			assertTrue(read_dvk.get_media_file().exists());
			assertTrue(read_dvk.get_secondary_file().exists());
		}
		catch(IOException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the update_extensions method.
	 */
	@Test
	public void test_update_extensions() {
		try {
			//COPY RESOURCE FILES TO TEMP DIRECTORY FOR TESTING
			File image_file = new File(this.temp_dir.getRoot(), "image.pdf");
			File text_file = new File(this.temp_dir.getRoot(), "text.jpg");
			File resources = new File("src/test/resources");
			File resource_file = new File(resources, "logo.png");
			Files.copy(resource_file, image_file);
			resource_file = new File(resources, "text.txt");
			Files.copy(resource_file, text_file);
			assertTrue(image_file.exists());
			assertTrue(text_file.exists());
			//CREATE DVK WITH IMPROPER EXTENSIONS FOR LINKED MEDIA FILES
			Dvk dvk = new Dvk();
			dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "ext.dvk"));
			dvk.set_dvk_id("id123");
			dvk.set_title("Title");
			dvk.set_artist("artist");
			dvk.set_page_url("/url/");
			dvk.set_media_file(image_file);
			dvk.set_secondary_file(text_file);
			dvk.write_dvk();
			//TEST THAT FILES EXIST IN ORIGINAL FORMAT
			assertTrue(dvk.get_dvk_file().exists());
			assertTrue(dvk.get_media_file().exists());
			assertTrue(dvk.get_secondary_file().exists());
			assertEquals("image.pdf", dvk.get_media_file().getName());
			assertEquals("text.jpg", dvk.get_secondary_file().getName());
			//TEST UPDATING EXTENSIONS OF LINKED MEDIA
			dvk.update_extensions();
			assertTrue(dvk.get_dvk_file().exists());
			assertTrue(dvk.get_media_file().exists());
			assertTrue(dvk.get_secondary_file().exists());
			assertEquals("image.png", dvk.get_media_file().getName());
			assertEquals("text.txt", dvk.get_secondary_file().getName());
		}
		catch(IOException e) {
			assertTrue(false);
		}
	}
}
