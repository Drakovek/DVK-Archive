package com.gmail.drakovekmail.dvkarchive.reformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;

/**
 * UnitTests for the Reformat class.
 * 
 * @author Drakovek
 */
public class TestReformat {

	/**
	 * Main directory  for holding test files
	 */
	private File test_dir;
	
	/**
	 * Creates the test directory for holding test files.
	 */
	@Before
	public void create_test_directory() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "reftest");
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
	 * Tests the reformat_dvks method.
	 */
	@Test
	public void test_reformat_dvks() {
		//CREATE DVKS FOR REFORMATTING
		Dvk dvk1 = new Dvk();
		dvk1.set_id("ID1");
		dvk1.set_title("Title 1");
		dvk1.set_artist("Artist");
		dvk1.set_page_url("/page/");
		dvk1.set_dvk_file(new File(this.test_dir, "dvk1.dvk"));
		dvk1.set_media_file("dvk1.png");
		Dvk dvk2 = new Dvk();
		dvk2.set_id("ID2");
		dvk2.set_title("Title 2");
		dvk2.set_artist("Artist");
		dvk2.set_page_url("/page/");
		dvk2.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk2.set_media_file("dvk2.jpg");
		dvk2.set_secondary_file("dvk2.txt");
		dvk2.set_description("This is fine already.");
		Dvk dvk3 = new Dvk();
		dvk3.set_id("ID3");
		dvk3.set_title("Title 3");
		dvk3.set_artist("Artist");
		dvk3.set_page_url("/page/");
		dvk3.set_dvk_file(new File(this.test_dir, "dvk3.dvk"));
		dvk3.set_media_file("dvk3.txt");
		dvk3.set_description(" <a> <b>words 'n stuff  </b>   </a> ");
		dvk1.write_dvk();
		dvk2.write_dvk();
		dvk3.write_dvk();
		assertTrue(dvk1.get_dvk_file().exists());
		assertTrue(dvk2.get_dvk_file().exists());
		assertTrue(dvk3.get_dvk_file().exists());
		//REFORMAT DVKS
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.test_dir);
		try (DvkHandler handler = new DvkHandler(prefs)) {
			handler.read_dvks(dirs,  null);
			Reformat.reformat_dvks(handler, null);
			File db = new File(this.test_dir, "dvk_archive.db");
			assertTrue(db.exists());
			handler.delete_database();
			assertFalse(db.exists());
			handler.initialize_connection();
			assertTrue(db.exists());
			handler.read_dvks(dirs, null);
			//CHECK DVKS STILL VALID
			ArrayList<Dvk> dvks = handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(3, dvks.size());
			assertTrue(dvks.get(0).get_dvk_file().exists());
			assertTrue(dvks.get(1).get_dvk_file().exists());
			assertTrue(dvks.get(2).get_dvk_file().exists());
			assertEquals("Title 1", dvks.get(0).get_title());
			assertEquals("Title 2", dvks.get(1).get_title());
			assertEquals("Title 3", dvks.get(2).get_title());
			assertEquals("dvk1.dvk", dvks.get(0).get_dvk_file().getName());
			assertEquals("dvk2.dvk", dvks.get(1).get_dvk_file().getName());
			assertEquals("dvk3.dvk", dvks.get(2).get_dvk_file().getName());
			assertEquals(null, dvks.get(0).get_description());
			assertEquals("This is fine already.", dvks.get(1).get_description());
			assertEquals("<a><b>words 'n stuff</b></a>", dvks.get(2).get_description());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the remame_files method.
	 */
	@Test
	public void test_rename_files() {
		//CREATE DVKS FOR RENAMING
		Dvk dvk1 = new Dvk();
		dvk1.set_id("ID1");
		dvk1.set_title("Title 1");
		dvk1.set_artist("Artist");
		dvk1.set_page_url("/page/");
		dvk1.set_dvk_file(new File(this.test_dir, "dvk1.dvk"));
		dvk1.set_media_file("dvk1.png");
		Dvk dvk2 = new Dvk();
		dvk2.set_id("ID2");
		dvk2.set_title("Title 2");
		dvk2.set_artist("Artist");
		dvk2.set_page_url("/page/");
		dvk2.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk2.set_media_file("dvk2.jpg");
		dvk2.set_secondary_file("dvk2.txt");
		dvk1.write_dvk();
		dvk2.write_dvk();
		try {
			dvk1.get_media_file().createNewFile();
			dvk2.get_media_file().createNewFile();
			dvk2.get_secondary_file().createNewFile();
		} catch (IOException e) {}
		assertTrue(dvk1.get_dvk_file().exists());
		assertTrue(dvk1.get_media_file().exists());
		assertTrue(dvk2.get_dvk_file().exists());
		assertTrue(dvk2.get_media_file().exists());
		assertTrue(dvk2.get_secondary_file().exists());
		//RENAME FILES
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.test_dir);
		try(DvkHandler handler = new DvkHandler(prefs)) {
			handler.read_dvks(dirs, null);
			Reformat.rename_files(handler, null);
			//CHECK FILES READ
			ArrayList<Dvk> dvks = handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(2, dvks.size());
			assertTrue(dvks.get(0).get_dvk_file().exists());
			assertTrue(dvks.get(0).get_media_file().exists());
			assertTrue(dvks.get(1).get_dvk_file().exists());
			assertTrue(dvks.get(1).get_media_file().exists());
			assertTrue(dvks.get(1).get_secondary_file().exists());
			assertEquals("Title 1_ID1.dvk", dvks.get(0).get_dvk_file().getName());
			assertEquals("Title 1_ID1.png", dvks.get(0).get_media_file().getName());
			assertEquals("Title 2_ID2.dvk", dvks.get(1).get_dvk_file().getName());
			assertEquals("Title 2_ID2.jpg", dvks.get(1).get_media_file().getName());
			assertEquals("Title 2_ID2.txt", dvks.get(1).get_secondary_file().getName());
			File db = new File(this.test_dir, "dvk_archive.db");
			assertTrue(db.exists());
			handler.delete_database();
			assertFalse(db.exists());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
		//CHECK FILES ACTUALLY WRITTEN
		try(DvkHandler handler = new DvkHandler(prefs)) {
			handler.read_dvks(dirs, null);
			ArrayList<Dvk> dvks = handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(2, dvks.size());
			assertTrue(dvks.get(0).get_dvk_file().exists());
			assertTrue(dvks.get(0).get_media_file().exists());
			assertTrue(dvks.get(1).get_dvk_file().exists());
			assertTrue(dvks.get(1).get_media_file().exists());
			assertTrue(dvks.get(1).get_secondary_file().exists());
			assertEquals("Title 1_ID1.dvk", dvks.get(0).get_dvk_file().getName());
			assertEquals("Title 1_ID1.png", dvks.get(0).get_media_file().getName());
			assertEquals("Title 2_ID2.dvk", dvks.get(1).get_dvk_file().getName());
			assertEquals("Title 2_ID2.jpg", dvks.get(1).get_media_file().getName());
			assertEquals("Title 2_ID2.txt", dvks.get(1).get_secondary_file().getName());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
}
