package com.gmail.drakovekmail.dvkarchive.reformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gmail.drakovekmail.dvkarchive.file.Dvk;
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
		dvk1.write_dvk();
		dvk2.write_dvk();
		assertTrue(dvk1.get_dvk_file().exists());
		assertTrue(dvk2.get_dvk_file().exists());
		//REFORMAT DVKS
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		Reformat.reformat_dvks(handler, null);
		//CHECK DVKS STILL VALID
		handler.read_dvks(dirs, prefs, null, false, false, false);
		handler.sort_dvks_title(false, false);
		assertEquals(2, handler.get_size());
		assertTrue(handler.get_dvk(0).get_dvk_file().exists());
		assertTrue(handler.get_dvk(1).get_dvk_file().exists());
		assertEquals("Title 1", handler.get_dvk(0).get_title());
		assertEquals("Title 2", handler.get_dvk(1).get_title());
		assertEquals("dvk1.dvk",
				handler.get_dvk(0).get_dvk_file().getName());
		assertEquals("dvk2.dvk",
				handler.get_dvk(1).get_dvk_file().getName());
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
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		Reformat.rename_files(handler, null);
		//CHECK FILES READ
		handler.read_dvks(dirs, prefs, null, false, false, false);
		handler.sort_dvks_title(false, false);
		assertEquals(2, handler.get_size());
		assertTrue(handler.get_dvk(0).get_dvk_file().exists());
		assertTrue(handler.get_dvk(0).get_media_file().exists());
		assertTrue(handler.get_dvk(1).get_dvk_file().exists());
		assertTrue(handler.get_dvk(1).get_media_file().exists());
		assertTrue(handler.get_dvk(1).get_secondary_file().exists());
		assertEquals("Title 1_ID1.dvk",
				handler.get_dvk(0).get_dvk_file().getName());
		assertEquals("Title 1_ID1.png",
				handler.get_dvk(0).get_media_file().getName());
		assertEquals("Title 2_ID2.dvk",
				handler.get_dvk(1).get_dvk_file().getName());
		assertEquals("Title 2_ID2.jpg",
				handler.get_dvk(1).get_media_file().getName());
		assertEquals("Title 2_ID2.txt",
				handler.get_dvk(1).get_secondary_file().getName());
	}
}
