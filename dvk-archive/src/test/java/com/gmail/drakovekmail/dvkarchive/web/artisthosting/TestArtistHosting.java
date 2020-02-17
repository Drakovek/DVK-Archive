package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;

/**
 * Unit tests for the ArtistHosting class.
 * 
 * @author Drakovek
 */
public class TestArtistHosting {
	
	/**
	 * Main directory for storing test files.
	 */
	private File test_dir;
	
	/**
	 * Creates the test directory for holding test files.
	 */
	@Before
	public void create_test_directory() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "arthosttest");
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
	 * Tests the get_artists method.
	 */
	@Test
	public void test_get_artists() {
		//FIRST ARTIST
		Dvk dvk = new Dvk();
		dvk.set_id("ID123");
		dvk.set_title("Valid");
		dvk.set_artist("Artist");
		dvk.set_page_url("www.url.com/whatever");
		dvk.set_dvk_file(new File(this.test_dir, "dvk1.dvk"));
		dvk.set_media_file("blah.png");
		dvk.write_dvk();
		//SAME ARTIST
		dvk.set_title("Duplicate");
		dvk.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk.write_dvk();
		//DIFFERENT ARTIST
		dvk.set_title("Next");
		dvk.set_artist("Other Artist");
		dvk.set_dvk_file(new File(this.test_dir, "dvk3.dvk"));
		dvk.write_dvk();
		//DIFFERENT URL
		dvk.set_title("Other");
		dvk.set_artist("Thing");
		dvk.set_page_url("www.otherwebsite.com/view");
		dvk.set_dvk_file(new File(this.test_dir, "dvk4.dvk"));
		dvk.write_dvk();
		//TEST GETTING ARTISTS
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		File[] dirs = {this.test_dir};
		handler.read_dvks(dirs, prefs, null, false, false, false);
		ArrayList<Dvk> dvks;
		dvks = ArtistHosting.get_artists(handler, "url.com");
		assertEquals(2, dvks.size());
		assertEquals("Artist", dvks.get(0).get_artists()[0]);
		assertEquals("Other Artist", dvks.get(1).get_artists()[0]);
		dvks = ArtistHosting.get_artists(handler, "otherwebsite.com");
		assertEquals(1, dvks.size());
		assertEquals("Thing", dvks.get(0).get_artists()[0]);
		assertEquals(0, ArtistHosting.get_artists(handler, "blah").size());
		assertEquals(0, ArtistHosting.get_artists(null, "blah").size());
	}
}
