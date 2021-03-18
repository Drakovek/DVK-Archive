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
	 * Tests the can_write method.
	 */
	@Test
	public void test_can_write() {
		// TEST VALID DVK THAT CAN BE WRITTEN
	    Dvk dvk = new Dvk();
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "write.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("write.txt");
	    assertTrue(dvk.can_write());
	    // TEST WITH NO DVK FILE
	    dvk.set_dvk_file(null);
	    assertFalse(dvk.can_write());
	    // TEST DVK FILE WITHOUT EXISTING PARENT DIRECTORY
	    dvk.set_dvk_file(new File("/non-existant/file.dvk"));
	    assertFalse(dvk.can_write());
	    // TEST WITH NO DVK ID
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "write.dvk"));
	    dvk.set_dvk_id(null);
	    assertFalse(dvk.can_write());
	    // TEST WITH NO TITLE
	    dvk.set_dvk_id("id123");
	    dvk.set_title(null);
	    assertFalse(dvk.can_write());
	    // TEST WITH NO ARTIST
	    dvk.set_title("Title");
	    dvk.set_artist(null);
	    assertFalse(dvk.can_write());
	    // TEST WITH NO PAGE URL
	    dvk.set_artist("artist");
	    dvk.set_page_url(null);
	    assertFalse(dvk.can_write());
	    // TEST WITH NO MEDIA FILE
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("");
	    assertFalse(dvk.can_write());
	    // TEST VALID WRITABLE DVK
	    dvk.set_media_file("write.txt");
	    assertTrue(dvk.can_write());
	}
	
	/**
	 * Tests the get_dvk_file and set_dvk_file methods.
	 */
	@Test
	public void test_get_set_dvk_file() {
		// TEST EMPTY DVK FILE WITH CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_dvk_file());
	    // TEST GETTING AND SETTING DVK FILE
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "file.dvk"));
	    assertEquals("file.dvk", dvk.get_dvk_file().getName());
	    assertEquals(this.temp_dir.getRoot(), dvk.get_dvk_file().getParentFile());
	    // TEST SETTING DVK FILE TO NULL
	    dvk.set_dvk_file(null);
	    assertEquals(null, dvk.get_dvk_file());
	    // WRITE DVK FILE
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dfile.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url");
	    dvk.set_media_file("dfile.txt");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("dfile.dvk", read_dvk.get_dvk_file().getName());
	    assertEquals(this.temp_dir.getRoot(), read_dvk.get_dvk_file().getParentFile());
	}
	
	/**
	 * Tests the get_dvk_id and set_dvk_id methods.
	 */
	@Test
	public void test_get_set_dvk_id() {
		// TEST EMPTY ID WITH CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_dvk_file());
	    // TEST GETTING AND SETTING DVK ID
	    dvk.set_dvk_id("id1234");
	    assertEquals("ID1234", dvk.get_dvk_id());
	    dvk.set_dvk_id("TST128-J");
	    assertEquals("TST128-J", dvk.get_dvk_id());
	    // TEST INVALID IDS
	    dvk.set_dvk_id(null);
	    assertEquals(null, dvk.get_dvk_id());
	    dvk.set_dvk_id("");
	    assertEquals(null, dvk.get_dvk_id());
	    // WRITE DVK FILE
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "id_dvk.dvk"));
	    dvk.set_dvk_id("WRITE456");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("id_dvk.txt");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING DVK ID
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
		// TEST EMPTY TITLE FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_title());
	    // TEST GETTING AND SETTING TITLE
	    dvk.set_title("Test Title");
	    assertEquals("Test Title", dvk.get_title());
	    dvk.set_title(null);
	    assertEquals(null, dvk.get_title());
	    dvk.set_title("");
	    assertEquals("", dvk.get_title());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "ttl_dvk.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title #2");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("ttl_dvk.dvk.txt");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING TITLE FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("Title #2", read_dvk.get_title());
	}
	
	/**
	 * Tests the get_artists, set_artist and set_artists methods.
	 */
	@Test
	public void test_get_set_artists() {
		// TEST EMPTY ARTISTS FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(0, dvk.get_artists().length);
	    // TEST GETTING AND SETTING SINGLE ARTIST
	    dvk.set_artist("Artist Person");
	    assertEquals(1, dvk.get_artists().length);
	    assertEquals("Artist Person", dvk.get_artists()[0]);
	    dvk.set_artist("");
	    assertEquals(1, dvk.get_artists().length);
	    assertEquals("", dvk.get_artists()[0]);
	    // TEST GETTING AND SETTING MULTIPLE ARTISTS
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
	    // TEST SETTING INVALID ARTIST VALUES
	    dvk.set_artist(null);
	    assertEquals(0, dvk.get_artists().length);
	    dvk.set_artists(null);
	    assertEquals(0, dvk.get_artists().length);
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "artist.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    artists = new String[2];
	    artists[0] = "Person";
	    artists[1] = "artist";
	    dvk.set_artists(artists);
	    dvk.set_page_url("/url");
	    dvk.set_media_file("artist.txt");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING ARTISTS FROM DVK FILE
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
	    // TEST EMPTY TIME FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // TEST VALID TIME
	    dvk.set_time_int(2017, 10, 6, 19, 15);
	    assertEquals("2017/10/06|19:15", dvk.get_time());
	    // TEST INVALID YEAR
	    dvk.set_time_int(0, 10, 10, 7, 15);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // TEST INVALID MONTH
	    dvk.set_time_int(2017, 0, 10, 7, 15);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    dvk.set_time_int(2017, 13, 10, 7, 15);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // TEST INVALID DAY
	    dvk.set_time_int(2017, 10, 0, 7, 15);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    dvk.set_time_int(2017, 10, 32, 7, 15);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // TEST INVALID HOUR
	    dvk.set_time_int(2017, 10, 10, -1, 15);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    dvk.set_time_int(2017, 10, 10, 24, 15);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // TEST INVALID MINUTE
	    dvk.set_time_int(2017, 10, 10, 7, -1);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    dvk.set_time_int(2017, 10, 10, 7, 60);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "int_time.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("int_time.txt");
	    dvk.set_time_int(2020, 10, 26, 21, 15);
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING TIME FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("2020/10/26|21:15", read_dvk.get_time());
	}
	
	/**
	 * Tests the get_time and set_time methods.
	 */
	@Test
	public void test_get_set_time() {
		// TEST DEFAULT TIME FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // TEST GETTING AND SETTING TIME
	    dvk.set_time("2017!10!06!05!00");
	    assertEquals("2017/10/06|05:00", dvk.get_time());
	    // TEST SETTING INVALID TIMES
	    dvk.set_time(null);
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    dvk.set_time("2017/10/06");
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    dvk.set_time("yyyy/mm/dd/hh/tt");
	    assertEquals("0000/00/00|00:00", dvk.get_time());
	    // WRITE DVK FILE
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "time.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("time.txt");
	    dvk.set_time("2020/10/27|17:55");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING TIME FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("2020/10/27|17:55", read_dvk.get_time());
	}
	
	/**
	 * Tests the get_web_tags and set_web_tags methods.
	 */
	@Test
	public void test_get_set_web_tags() {
	    // TEST EMPTY WEB TAGS FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(0, dvk.get_web_tags().length);
	    // TEST GETTING AND SETTING WEB TAGS
	    String[] tags = {"tag2", "Tag1", "tag2", null};
	    dvk.set_web_tags(tags);
	    assertEquals(2, dvk.get_web_tags().length);
	    assertEquals("tag2", dvk.get_web_tags()[0]);
	    assertEquals("Tag1", dvk.get_web_tags()[1]);
	    // TEST INVALID TAGS
	    dvk.set_web_tags(null);
	    assertEquals(0, dvk.get_web_tags().length);
	    dvk.set_web_tags(new String[0]);
	    assertEquals(0, dvk.get_web_tags().length);
	    tags = new String[1];
	    tags[0] = null;
	    dvk.set_web_tags(tags);
	    assertEquals(0, dvk.get_web_tags().length);
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "tags.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("tags.txt");
	    tags = new String[2];
	    tags[0] = "String";
	    tags[1] = "tags";
	    dvk.set_web_tags(tags);
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING WEB TAGS FROM DVK FILE
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
		// TEST DEFAULT DESCRIPTION FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_description());
	    // TEST GETTING AND SETTING DESCRIPTION
	    dvk.set_description("   <i>Baño</i>  ");
	    assertEquals("<i>Ba&#241;o</i>", dvk.get_description());
	    dvk.set_description("<i>Ba&#241;o</i>");
	    assertEquals("<i>Ba&#241;o</i>", dvk.get_description()); 
	    // TEST SETTING EMPTY DESCRIPTION
	    dvk.set_description(null);
	    assertEquals(null, dvk.get_description());
	    dvk.set_description("");
	    assertEquals(null, dvk.get_description());
	    dvk.set_description("   ");
	    assertEquals(null, dvk.get_description());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "desc.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("desc.txt");
	    dvk.set_description("Other Description");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING DESCRIPTION FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("Other Description", read_dvk.get_description());
	}
	
	/**
	 * Tests the get_page_url and set_page_url methods.
	 */
	@Test
	public void test_get_set_page_url() {
		// TEST EMPTY PAGE URL FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_page_url());
	    // TEST GETTING AND SETTING PAGE URL
	    dvk.set_page_url("/Page/URL");
	    assertEquals("/Page/URL", dvk.get_page_url());
	    // TEST SETTING INVALID URL
	    dvk.set_page_url(null);
	    assertEquals(null, dvk.get_page_url());
	    dvk.set_page_url("");
	    assertEquals(null, dvk.get_page_url());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "page.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("test.net/url/Page");
	    dvk.set_media_file("page.txt");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING PAGE URL FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("test.net/url/Page", read_dvk.get_page_url());
	}
	
	/**
	 * Tests the get_direct_url and set_direct_url methods.
	 */
	@Test
	public void test_get_set_direct_url() {
		// TEST EMPTY DIRECT URL FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_direct_url());
	    // TEST GETTING AND SETTING DIRECT URL
	    dvk.set_direct_url("/direct/URL");
	    assertEquals("/direct/URL", dvk.get_direct_url());
	    // TEST SETTING INVALID DIRECT URL
	    dvk.set_direct_url(null);
	    assertEquals(null, dvk.get_direct_url());
	    dvk.set_direct_url("");
	    assertEquals(null, dvk.get_direct_url());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "direct.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("direct.txt");
	    dvk.set_direct_url("test.net/url/Direct");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING DIRECT URL
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("test.net/url/Direct", read_dvk.get_direct_url());
	}
	
	/**
	 * Tests the get_secondary_url and set_secondary_url methods.
	 */
	@Test
	public void test_get_set_secondary_url() {
		// TEST EMPTY SECONDARY URL FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_secondary_url());
	    // TEST GETTING AND SETTING SECONDARY URL
	    dvk.set_secondary_url("/Page/URL");
	    assertEquals("/Page/URL", dvk.get_secondary_url());
	    // TEST SETTING INVALID SECONDARY URL
	    dvk.set_secondary_url(null);
	    assertEquals(null, dvk.get_secondary_url());
	    dvk.set_secondary_url("");
	    assertEquals(null, dvk.get_secondary_url());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "sec.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("sec.txt");
	    dvk.set_secondary_url("test.net/url/second");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING SECONDARY URL FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals("test.net/url/second", read_dvk.get_secondary_url());
	}
	
	/**
	 * Tests the get_media_file and set_media_file methods.
	 */
	@Test
	public void test_get_set_media_file() {
		// TEST EMPTY MEDIA FILE FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_media_file());
	    // TEST SETTING MEDIA FILE WITH NO DVK FILE SET
	    dvk.set_media_file("media.txt");
	    assertEquals(null, dvk.get_media_file());
	    // TEST SETTING MEDIA FILE FROM DVK FILE WITH INVALID PARENT
	    dvk.set_dvk_file(new File("/non-existant/file.dvk"));
	    dvk.set_media_file("media.txt");
	    assertEquals(null, dvk.get_media_file());
	    // TEST SETTING MEDIA WITH VALID DVK FILE
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "media.dvk"));
	    dvk.set_media_file("media.txt");
	    assertEquals(this.temp_dir.getRoot(), dvk.get_media_file().getParentFile());
	    assertEquals("media.txt", dvk.get_media_file().getName());
	    // TEST SETTING INVALID MEDIA FILE WITH INVALID DVK FILE
	    dvk.set_media_file(null);
	    assertEquals(null, dvk.get_media_file());
	    dvk.set_media_file("");
	    assertEquals(null, dvk.get_media_file());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "new_media.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("new_media.png");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING MEDIA FILE VALUE FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals(this.temp_dir.getRoot(), read_dvk.get_media_file().getParentFile());
	    assertEquals("new_media.png", read_dvk.get_media_file().getName());
	}
	
	/**
	 * Tests the get_secondary_file and set_secondar_file methods.
	 */
	@Test
	public void test_get_set_secondary_file() {
		// TEST EMPTY SECONDARY MEDIA FILE FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(null, dvk.get_secondary_file());
	    // TEST SETTING SECONDARY MEDIA FILE WITH NO DVK FILE SET
	    dvk.set_secondary_file("secFile.txt");
	    assertEquals(null, dvk.get_secondary_file());
	    // TEST SETTING SECONDARY MEDIA FILE FROM DVK FILE WITH INVALID PARENT
	    dvk.set_dvk_file(new File("/non-existant/file.dvk"));
	    dvk.set_secondary_file("secFile.txt");
	    assertEquals(null, dvk.get_secondary_file());
	    // TEST SETTING SECONDARY MEDIA WITH VALID DVK FILE
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "secFile.dvk"));
	    dvk.set_secondary_file("secFile.txt");
	    assertEquals(this.temp_dir.getRoot(), dvk.get_secondary_file().getParentFile());
	    assertEquals("secFile.txt", dvk.get_secondary_file().getName());
	    // TEST SETTING INVALID SECONDARY MEDIA FILE WITH INVALID DVK FILE
	    dvk.set_secondary_file(null);
	    assertEquals(null, dvk.get_secondary_file());
	    dvk.set_secondary_file("");
	    assertEquals(null, dvk.get_secondary_file());
	    // WRITE DVK
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "newSecFile.dvk"));;
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("newSecFile.txt");
	    dvk.set_secondary_file("newSecFile.png");
	    dvk.write_dvk();
	    assertTrue(dvk.get_dvk_file().exists());
	    // TEST READING SECONDARY MEDIA FILE VALUE FROM DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals(this.temp_dir.getRoot(), read_dvk.get_secondary_file().getParentFile());
	    assertEquals("newSecFile.png", read_dvk.get_secondary_file().getName());
	}
	
	/**
	 * Tests the get_favorites and set_favorites methods.
	 */
	@Test
	public void test_get_set_favorites() {
		// TEST GETTING EMPTY FAVORITES LIST FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertEquals(0, dvk.get_favorites().length);
	    // TEST GETTING AND SETTING FAVORITES
	    String[] favorites = {"Test", "artist", null, "artist"};
	    dvk.set_favorites(favorites);
	    assertEquals(2, dvk.get_favorites().length);
	    assertEquals("artist", dvk.get_favorites()[0]);
	    assertEquals("Test", dvk.get_favorites()[1]);
	    // TEST GETTING FAVORITES FROM WEB TAGS IN OLD FORMAT
	    String[] tags = new String[5];
	    tags[0] = "favorite:Other";
	    tags[1] = "tag1";
	    tags[2] = "tag2";
	    tags[3] = "Favorite:thing";
	    tags[4] = "Favorite:Test";
	    dvk.set_web_tags(tags);
	    dvk.set_favorites(favorites);
	    assertEquals(2, dvk.get_web_tags().length);
	    assertEquals("tag1", dvk.get_web_tags()[0]);
	    assertEquals("tag2", dvk.get_web_tags()[1]);
	    assertEquals(4, dvk.get_favorites().length);
	    assertEquals("artist", dvk.get_favorites()[0]);
	    assertEquals("Other", dvk.get_favorites()[1]);
	    assertEquals("Test", dvk.get_favorites()[2]);
	    assertEquals("thing", dvk.get_favorites()[3]);
	    tags = new String[5];
	    tags[0] = "favorite:Other";
	    tags[1] = "tag1";
	    tags[2] = "tag2";
	    tags[3] = "Favorite:thing";
	    tags[4] = "Favorite:Test";
	    dvk.set_web_tags(tags);
	    dvk.set_favorites(null);
	    assertEquals(2, dvk.get_web_tags().length);
	    assertEquals("tag1", dvk.get_web_tags()[0]);
	    assertEquals("tag2", dvk.get_web_tags()[1]);
	    assertEquals(3, dvk.get_favorites().length);
	    assertEquals("Other", dvk.get_favorites()[0]);
	    assertEquals("Test", dvk.get_favorites()[1]);
	    assertEquals("thing", dvk.get_favorites()[2]);
	    // TEST SETTING INVALID FAVORITES
	    dvk = new Dvk();
	    dvk.set_favorites(null);
	    assertEquals(0, dvk.get_favorites().length);
	    // WRITE DVK
	    dvk = new Dvk();
	    dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "fav.dvk"));
	    dvk.set_dvk_id("id123");
	    dvk.set_title("Title");
	    dvk.set_artist("Artist");
	    dvk.set_page_url("/url/");
	    dvk.set_media_file("media.png");
	    tags = new String[2];
	    tags[0] = "Thing";
	    tags[1] ="Favorite:Person";
	    dvk.set_web_tags(tags);
	    favorites = new String[2];
	    favorites[0] = "person2";
	    favorites[1] = "Other Artist";
	    dvk.set_favorites(favorites);
	    dvk.write_dvk();
	    // TEST READING FAVORITES FROM A DVK FILE
	    Dvk read_dvk = new Dvk(dvk.get_dvk_file());
	    dvk = null;
	    assertEquals(1, read_dvk.get_web_tags().length);
	    assertEquals("Thing", read_dvk.get_web_tags()[0]);
	    assertEquals(3, read_dvk.get_favorites().length);
	    assertEquals("Other Artist", read_dvk.get_favorites()[0]);
	    assertEquals("Person", read_dvk.get_favorites()[1]);
	    assertEquals("person2", read_dvk.get_favorites()[2]);
	}
	
	/**
	 * Tests the set_single and is_single methods.
	 */
	@Test
	public void test_get_set_single() {
		// TEST GETTING DEFAULT SINGLE VALUE FROM CONSTRUCTOR
	    Dvk dvk = new Dvk();
	    assertFalse(dvk.is_single());
	    // TEST GETTING AND SETTING IS SINGLE
	    dvk.set_single(true);
	    assertTrue(dvk.is_single());
	    dvk.set_single(false);
	    assertFalse(dvk.is_single());
	    // TEST GETTING SINGLE FROM LEGACY
	    String[] tags = {"DVK:Single", "tag", "tag2", "dvk:single"};
	    dvk.set_web_tags(tags);
	    dvk.set_single(false);
	    assertTrue(dvk.is_single());
	    assertEquals(2, dvk.get_web_tags().length);
	    assertEquals("tag", dvk.get_web_tags()[0]);
	    assertEquals("tag2", dvk.get_web_tags()[1]);
	    // WRITE DVKS
	    Dvk dvk_single = new Dvk();
	    dvk_single.set_dvk_file(new File(this.temp_dir.getRoot(), "single.dvk"));
	    dvk_single.set_dvk_id("id123");
	    dvk_single.set_title("Title");
	    dvk_single.set_artist("Artist");
	    dvk_single.set_page_url("/url/");
	    dvk_single.set_media_file("single.png");
	    dvk_single.set_single(true);
	    dvk_single.write_dvk();
	    Dvk dvk_tag = new Dvk();
	    dvk_tag.set_dvk_file(new File(this.temp_dir.getRoot(), "stag.dvk"));
	    dvk_tag.set_dvk_id("id123");
	    dvk_tag.set_title("Title");
	    dvk_tag.set_artist("Artist");
	    dvk_tag.set_page_url("/url/");
	    dvk_tag.set_media_file("stag.png");
	    tags = new String[2];
	    tags[0] = "tag1";
	    tags[1] = "Dvk:Single";
	    dvk_tag.set_web_tags(tags);
	    dvk_tag.write_dvk();
	    Dvk dvk_none = new Dvk();
	    dvk_none.set_dvk_file(new File(this.temp_dir.getRoot(), "none.dvk"));
	    dvk_none.set_dvk_id("id123");
	    dvk_none.set_title("Title");
	    dvk_none.set_artist("Artist");
	    dvk_none.set_page_url("/url/");
	    dvk_none.set_media_file("none.png");
	    tags = new String[1];
	    tags[0] = "none";
	    dvk_none.set_web_tags(tags);
	    dvk_none.write_dvk();
	    // TEST READING SINGLE VALUE FROM DVK FILES
	    Dvk read_dvk = new Dvk(dvk_single.get_dvk_file());
	    dvk_single = null;
	    assertTrue(read_dvk.is_single());
	    read_dvk = new Dvk(dvk_tag.get_dvk_file());
	    dvk_tag = null;
	    assertEquals(1, read_dvk.get_web_tags().length);
	    assertEquals("tag1", read_dvk.get_web_tags()[0]);
	    assertTrue(read_dvk.is_single());
	    read_dvk = new Dvk(dvk_none.get_dvk_file());
	    dvk_none = null;
	    assertEquals(1, read_dvk.get_web_tags().length);
	    assertEquals("none", read_dvk.get_web_tags()[0]);
	    assertFalse(read_dvk.is_single());
	}
	
	/**
	 * Tests the get_filename method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_filename() {
		// TEST WITH NO TITLE OR ID
	    Dvk dvk = new Dvk();
	    assertEquals("", dvk.get_filename(false, null));
	    // TEST WITH ONLY TITLE
	    dvk.set_title("Title");
	    assertEquals("", dvk.get_filename(false, null));
	    // TEST WITH ONLY ID
	    dvk.set_dvk_id("ID123");
	    dvk.set_title(null);
	    assertEquals("", dvk.get_filename(false, null));
	    // TEST WITH VALID TITLE AND ID
	    dvk.set_title("a   B-cd!");
	    assertEquals("a B-cd_ID123", dvk.get_filename(false, null));
	    assertEquals("a B-cd_ID123_S", dvk.get_filename(true, null));
	    // TEST WITH ID THAT IS TOO LONG
	    dvk.set_title("Title");
	    dvk.set_dvk_id("VERYLONG1234567890987654321");
	    assertEquals("Title_DVK165356273", dvk.get_filename(false, null));
	    assertEquals("Title_DVK165356273_S", dvk.get_filename(true, null));
	    assertEquals("Title_AAA165356273", dvk.get_filename(false, "AAA"));
	    assertEquals("Title_ABC165356273_S", dvk.get_filename(true, "abc"));
	    assertEquals("VERYLONG1234567890987654321", dvk.get_dvk_id());
	    // TEST WITH EMPTY TITLE
	    dvk.set_title("");
	    dvk.set_dvk_id("id123");
	    assertEquals("0_ID123", dvk.get_filename(false, null));
	    assertEquals("0_ID123_S", dvk.get_filename(true, null));
	}
	
	/**
	 * Tests the rename_files method.
	 */
	@Test
	public void test_rename_files() {
		// CREATE TEST DVK WITHOUT EXISTING MEDIA FILES
	    Dvk no_media_dvk = new Dvk();
	    no_media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "rnm.dvk"));
	    no_media_dvk.set_dvk_id("id123");
	    no_media_dvk.set_title("Title");
	    no_media_dvk.set_artist("Artist");
	    no_media_dvk.set_page_url("/url/");
	    no_media_dvk.set_media_file("rnm.txt");
	    no_media_dvk.set_secondary_file("rnm_sec.png");
	    no_media_dvk.write_dvk();
	    assertTrue(no_media_dvk.get_dvk_file().exists());
	    assertFalse(no_media_dvk.get_media_file().exists());
	    assertFalse(no_media_dvk.get_secondary_file().exists());
	    // TEST RENAMING DVK FILE
	    no_media_dvk.rename_files("New", "New2");
	    Dvk read_dvk = new Dvk(no_media_dvk.get_dvk_file());
	    no_media_dvk = null;
	    assertEquals("New.dvk", read_dvk.get_dvk_file().getName());
	    assertEquals("New.txt", read_dvk.get_media_file().getName());
	    assertEquals("New2.png", read_dvk.get_secondary_file().getName());
	    assertTrue(read_dvk.get_dvk_file().exists());
	    assertFalse(read_dvk.get_media_file().exists());
	    assertFalse(read_dvk.get_secondary_file().exists());
	    // CREATE TEST DVK WITH EXISTING MEDIA FILE
	    Dvk media_dvk = new Dvk();
	    media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "rnm.dvk"));
	    media_dvk.set_dvk_id("id123");
	    media_dvk.set_title("Title");
	    media_dvk.set_artist("Artist");
	    media_dvk.set_page_url("/url/");
	    media_dvk.set_media_file("enm.png");
	    InOut.write_file(media_dvk.get_media_file(), "TEST Media");
	    media_dvk.write_dvk();
	    assertTrue(media_dvk.get_dvk_file().exists());
	    assertTrue(media_dvk.get_media_file().exists());
	    assertEquals(null, media_dvk.get_secondary_file());
	    // TEST RENAMING DVK FILE WITH MEDIA FILE
	    media_dvk.rename_files("other", "other");
	    read_dvk = new Dvk(media_dvk.get_dvk_file());
	    media_dvk = null;
	    assertEquals("other.dvk", read_dvk.get_dvk_file().getName());
	    assertEquals("other.png", read_dvk.get_media_file().getName());
	    assertEquals(null, read_dvk.get_secondary_file());
	    assertTrue(read_dvk.get_dvk_file().exists());
	    assertTrue(read_dvk.get_media_file().exists());
	    assertEquals("TEST Media", InOut.read_file(read_dvk.get_media_file()));
	    // CREATE TEST DVK WITH EXISTING MEDIA AND SECONDARY FILES
	    Dvk secondary_dvk = new Dvk();
	    secondary_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "rnm.dvk"));
	    secondary_dvk.set_dvk_id("id123");
	    secondary_dvk.set_title("Title");
	    secondary_dvk.set_artist("Artist");
	    secondary_dvk.set_page_url("/url/");
	    secondary_dvk.set_media_file("rnm.txt");
	    InOut.write_file(secondary_dvk.get_media_file(), "Primary");
	    secondary_dvk.set_secondary_file("rnms.txt");
	    InOut.write_file(secondary_dvk.get_secondary_file(), "Secondary");
	    secondary_dvk.write_dvk();
	    assertTrue(secondary_dvk.get_dvk_file().exists());
	    assertTrue(secondary_dvk.get_media_file().exists());
	    assertTrue(secondary_dvk.get_secondary_file().exists());
	    // TEST RENAMING DVK FILE WITH MEDIA AND SECONDARY FILES
	    secondary_dvk.rename_files("Prime", "Second");
	    read_dvk = new Dvk(secondary_dvk.get_dvk_file());
	    secondary_dvk = null;
	    assertEquals("Prime.dvk", read_dvk.get_dvk_file().getName());
	    assertEquals("Prime.txt", read_dvk.get_media_file().getName());
	    assertEquals("Second.txt", read_dvk.get_secondary_file().getName());
	    assertEquals("Primary", InOut.read_file(read_dvk.get_media_file()));
	    assertEquals("Secondary", InOut.read_file(read_dvk.get_secondary_file()));
	    assertTrue(read_dvk.get_dvk_file().exists());
	    assertTrue(read_dvk.get_media_file().exists());
	    assertTrue(read_dvk.get_secondary_file().exists());
	}
	
	/**
	 * Tests the write_media method.
	 */
	@Test
	public void test_write_media() {
		try(DConnect connect = new DConnect(false, false)) {
			// CREATE DVK THAT CANNOT BE WRITTEN
		    Dvk media_dvk = new Dvk();
		    media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "inv.dvk"));
		    media_dvk.set_dvk_id("id123");
		    media_dvk.set_artist("artist");
		    media_dvk.set_page_url("/url/");
		    media_dvk.set_media_file("inv.txt");
		    media_dvk.set_direct_url("http://www.pythonscraping.com/img/gifts/img6.jpg");
		    // TEST WRITE MEDIA WHEN URL IS VALID, BUT DVK CANNOT BE WRITTEN
		    media_dvk.write_media(connect, false);
		    assertFalse(media_dvk.get_dvk_file().exists());
		    assertFalse(media_dvk.get_media_file().exists());
		    // TEST WRITE MEDIA WHEN DVK CAN BE WRITTEN, BUT URL IS INVALID
		    media_dvk.set_title("Title");
		    media_dvk.set_direct_url("!@#$%^");
		    media_dvk.write_media(connect, true);
		    assertFalse(media_dvk.get_dvk_file().exists());
		    assertFalse(media_dvk.get_media_file().exists());
		    assertEquals("0000/00/00|00:00", media_dvk.get_time());
		    // TEST VALID DIRECT MEDIA URL
		    media_dvk.set_direct_url("http://www.pythonscraping.com/img/gifts/img6.jpg");
		    media_dvk.write_media(connect, false);
		    assertTrue(media_dvk.get_dvk_file().exists());
		    assertTrue(media_dvk.get_media_file().exists());
		    assertEquals("inv.jpg", media_dvk.get_media_file().getName());
		    assertEquals(39785L, media_dvk.get_media_file().length());
		    assertEquals("0000/00/00|00:00", media_dvk.get_time());
		    // CREATE DVK WITH INVALID SECONDARY URL
		    Dvk secondary_dvk = new Dvk();
		    secondary_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "inv2.dvk"));
		    secondary_dvk.set_dvk_id("id123");
		    secondary_dvk.set_title("Title");
		    secondary_dvk.set_artist("artist");
		    secondary_dvk.set_page_url("/url/");
		    secondary_dvk.set_media_file("primary.jpg");
		    secondary_dvk.set_direct_url("http://www.pythonscraping.com/img/gifts/img6.jpg");
		    secondary_dvk.set_secondary_file("secondary.png");
		    secondary_dvk.set_secondary_url("!@#$%^");
		    // TEST WRITING MEDIA WITH VALID PRIMARY, BUT INVALID SECONDARY MEDIA URLS
		    media_dvk.write_media(connect, false);
		    assertFalse(secondary_dvk.get_dvk_file().exists());
		    assertFalse(secondary_dvk.get_media_file().exists());
		    assertFalse(secondary_dvk.get_secondary_file().exists());
		    // TEST WRITING MEDIA WITH VALID PRIMARY AND SECONDARY MEDIA URLS
		    secondary_dvk.set_secondary_url("http://www.pythonscraping.com/img/gifts/img4.jpg");
		    secondary_dvk.write_media(connect, true);
		    assertTrue(secondary_dvk.get_dvk_file().exists());
		    assertTrue(secondary_dvk.get_media_file().exists());
		    assertTrue(secondary_dvk.get_secondary_file().exists());
		    assertEquals(39785L, secondary_dvk.get_media_file().length());
		    assertEquals(85007L, secondary_dvk.get_secondary_file().length());
		    assertEquals("secondary.jpg", secondary_dvk.get_secondary_file().getName());
		    assertEquals("2014/08/04|00:49", secondary_dvk.get_time());
		}
		catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the move_dvk method.
	 */
	@Test
	public void test_move_dvk() {
		try {
			// CREATE TEST FILES
			File sub_dir = this.temp_dir.newFolder("sub");
		    Dvk no_media = new Dvk();
		    no_media.set_dvk_file(new File(this.temp_dir.getRoot(), "nm_move.dvk"));
		    no_media.set_dvk_id("NM123");
		    no_media.set_title("No Media");
		    no_media.set_artist("artist");
		    no_media.set_page_url("/url/");
		    no_media.set_media_file("nm_move.png");
		    no_media.write_dvk();
		    Dvk main_dvk = new Dvk();
		    main_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "move_main.dvk"));
		    main_dvk.set_dvk_id("ID234");
		    main_dvk.set_title("Main");
		    main_dvk.set_artist("artist");
		    main_dvk.set_page_url("/url/");
		    main_dvk.set_media_file("move_main.txt");
		    InOut.write_file(main_dvk.get_media_file(), "Main");
		    main_dvk.write_dvk();
		    Dvk second = new Dvk();
		    second.set_dvk_file(new File(this.temp_dir.getRoot(), "s_move.dvk"));
		    second.set_dvk_id("SEC246");
		    second.set_title("Second");
		    second.set_artist("artist");
		    second.set_page_url("/url/");
		    second.set_media_file("s_move.txt");
		    second.set_secondary_file("s_move2.txt");
		    InOut.write_file(second.get_media_file(), "Primary");
		    InOut.write_file(second.get_secondary_file(), "Secondary");
		    second.write_dvk();
		    // TEST MOVING DVK FILE WITH NO MEDIA
		    File file = no_media.get_dvk_file();
		    assertTrue(file.exists());
		    no_media.move_dvk(sub_dir);
		    assertFalse(file.exists());
		    assertEquals(sub_dir, no_media.get_dvk_file().getParentFile());
		    assertEquals("nm_move.dvk", no_media.get_dvk_file().getName());
		    assertTrue(no_media.get_dvk_file().exists());
		    assertEquals("No Media", no_media.get_title());
		    // TEST MOVING DVK WITH MEDIA
		    assertTrue(main_dvk.get_dvk_file().exists());
		    file = main_dvk.get_media_file();
		    assertTrue(file.exists());
		    main_dvk.move_dvk(sub_dir);
		    assertFalse(file.exists());
		    assertEquals(sub_dir, main_dvk.get_dvk_file().getParentFile());
		    assertEquals("move_main.dvk", main_dvk.get_dvk_file().getName());
		    assertTrue(main_dvk.get_dvk_file().exists());
		    assertEquals(sub_dir, main_dvk.get_media_file().getParentFile());
		    assertEquals("move_main.txt", main_dvk.get_media_file().getName());
		    assertTrue(main_dvk.get_media_file().exists());
		    assertEquals("Main", InOut.read_file(main_dvk.get_media_file()));
		    assertEquals("Main", main_dvk.get_title());
		    // TEST MOVING DVK WITH SECONDARY MEDIA
		    assertTrue(second.get_dvk_file().exists());
		    assertTrue(second.get_media_file().exists());
		    file = second.get_secondary_file();
		    assertTrue(file.exists());
		    second.move_dvk(sub_dir);
		    assertFalse(file.exists());
		    assertEquals(sub_dir, second.get_dvk_file().getParentFile());
		    assertEquals("s_move.dvk", second.get_dvk_file().getName());
		    assertTrue(second.get_dvk_file().exists());
		    assertEquals(sub_dir, second.get_media_file().getParentFile());
		    assertEquals("s_move.txt", second.get_media_file().getName());
		    assertTrue(second.get_media_file().exists());
		    assertEquals(sub_dir, second.get_secondary_file().getParentFile());
		    assertEquals("s_move2.txt", second.get_secondary_file().getName());
		    assertTrue(second.get_secondary_file().exists());
		    assertEquals("Primary", InOut.read_file(second.get_media_file()));
		    assertEquals("Secondary", InOut.read_file(second.get_secondary_file()));
		    assertEquals("Second", second.get_title());
		    // TEST MOVING TO INVALID DIRECTORIES
		    main_dvk.move_dvk(null);
		    assertEquals(sub_dir, main_dvk.get_dvk_file().getParentFile());
		    assertEquals("move_main.dvk", main_dvk.get_dvk_file().getName());
		    main_dvk.move_dvk(new File("/non-existant/dir/"));
		    assertEquals(sub_dir, main_dvk.get_dvk_file().getParentFile());
		    assertEquals("move_main.dvk", main_dvk.get_dvk_file().getName());
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
			dvk.set_media_file("image.pdf");
			dvk.set_secondary_file("text.jpg");
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
