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
		//CREATE SUB-DIRECTORIES
		File sub1 = new File(this.test_dir, "sub1");
		if(!sub1.isDirectory()) {
			sub1.mkdir();
		}
		File sub2 = new File(this.test_dir, "sub2");
		if(!sub2.isDirectory()) {
			sub2.mkdir();
		}
		//CREATE DVKS - ARTIST 1
		Dvk dvk = new Dvk();
		dvk.set_id("ID123");
		dvk.set_title("dvk1");
		dvk.set_artist("artist1");
		dvk.set_page_url("www.website.com/view/1");
		dvk.set_dvk_file(new File(sub1, "dvk1.dvk"));
		dvk.set_media_file("dvk1.png");
		dvk.write_dvk();
		dvk.set_title("dvk2");
		dvk.set_dvk_file(new File(sub2, "dvk2.dvk"));
		dvk.set_media_file("dvk2.jpg");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 2
		dvk.set_title("dvk3");
		dvk.set_artist("artist2");
		dvk.set_dvk_file(new File(this.test_dir, "dvk3.dvk"));
		dvk.set_media_file("dvk3.png");
		dvk.write_dvk();
		dvk.set_title("dvk4");
		dvk.set_dvk_file(new File(sub1, "dvk4.dvk"));
		dvk.set_media_file("dvk4.txt");
		dvk.write_dvk();
		dvk.set_title("dvk5");
		dvk.set_dvk_file(new File(sub2, "dvk5.dvk"));
		dvk.set_media_file("dvk5.jpg");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 3
		dvk.set_title("dvk6");
		dvk.set_artist("artist3");
		dvk.set_dvk_file(new File(sub2, "dvk6.dvk"));
		dvk.set_media_file("dvk6.txt");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 4
		dvk.set_title("dvk7");
		dvk.set_artist("artist4");
		dvk.set_page_url("diferent/page");
		dvk.set_dvk_file(new File(this.test_dir, "dvk7.dvk"));
		dvk.set_media_file("dvk7.pdf");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 5 - SINGLE
		dvk.set_title("dvk8");
		dvk.set_artist("artist5");
		dvk.set_page_url("www.website.com/view/2");
		String[] tags = {"Tag1", "Thing", "DVK:Single", "Last"};
		dvk.set_web_tags(tags);
		dvk.set_dvk_file(new File(this.test_dir, "dvk8.dvk"));
		dvk.set_media_file("dvk8.txt");
		dvk.write_dvk();
		//CHECK GET ARTISTS
		ArrayList<Dvk> dvks;
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		handler.sort_dvks_title(true, false);
		assertEquals(8, handler.get_size());
		dvks = ArtistHosting.get_artists(handler, "website.com");
		assertEquals(3, dvks.size());
		assertEquals("artist1", dvks.get(0).get_artists()[0]);
		assertEquals(this.test_dir, dvks.get(0).get_dvk_file());
		assertEquals("artist2", dvks.get(1).get_artists()[0]);
		assertEquals(this.test_dir, dvks.get(1).get_dvk_file());
		assertEquals("artist3", dvks.get(2).get_artists()[0]);
		assertEquals(sub2, dvks.get(2).get_dvk_file());
	}
	
	/**
	 * Tests the get_common_directory method.
	 */
	@Test
	public void test_get_common_directory() {
		//CREATE SUB-DIRECTORIES
		File sub1 = new File(this.test_dir, "sub1");
		if(!sub1.isDirectory()) {
			sub1.mkdir();
		}
		File sub2 = new File(this.test_dir, "sub2");
		if(!sub2.isDirectory()) {
			sub2.mkdir();
		}
		File supsub = new File(sub1, "supsub");
		if(!supsub.isDirectory()) {
			supsub.mkdir();
		}
		//CHECK INVALID
		File file = ArtistHosting.get_common_directory(
				sub2, new File("bleh"));
		assertEquals(sub2, file);
		//CHECK COMMON FILES
		file = ArtistHosting.get_common_directory(sub1, sub1);
		assertEquals(sub1, file);
		file = ArtistHosting.get_common_directory(sub2, sub1);
		assertEquals(this.test_dir, file);
		file = ArtistHosting.get_common_directory(sub1, supsub);
		assertEquals(sub1, file);
		file = ArtistHosting.get_common_directory(sub2, supsub);
		assertEquals(this.test_dir, file);
	}
}
