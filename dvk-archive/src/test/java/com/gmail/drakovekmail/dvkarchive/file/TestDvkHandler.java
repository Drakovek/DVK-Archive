package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

/**
 * Unit tests for the DvkHandler class.
 * 
 * @author Drakovek
 */
public class TestDvkHandler {
	
	/**
	 * Main directory for holding test files.
	 */
	private File test_dir;
	
	/**
	 * Sub-directory for holding test files.
	 */
	private File f1;
	
	/**
	 * Sub-directory for holding test files.
	 */
	private File f2;
	
	/**
	 * Sub-directory for holding test files.
	 */
	private File sub;
	
	/**
	 * Creates the test directory for holding test files.
	 */
	@Before
	public void create_test_files() {
		//CREATE DIRECTORIES
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "handlerdir");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		this.f1 = new File(this.test_dir, "f1");
		if(!this.f1.isDirectory()) {
			this.f1.mkdir();
		}
		this.f2 = new File(this.test_dir, "f2");
		if(!this.f2.isDirectory()) {
			this.f2.mkdir();
		}
		File f3 = new File(this.test_dir, "f3");
		if(!f3.isDirectory()) {
			f3.mkdir();
		}
		File f4 = new File(this.f2, "f4");
		if(!f4.isDirectory()) {
			f4.mkdir();
		}
		this.sub = new File(this.f1, "sub");
		if(!this.sub.isDirectory()) {
			this.sub.mkdir();
		}
		//CREATE DVK0
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.f1, "dvk0.dvk"));
		dvk.set_id("DVK0");
		dvk.set_title("Page 1");
		dvk.set_time_int(2020, 1, 29, 10, 39);
		dvk.set_artist("Artist1");
		dvk.set_page_url("/page0");
		dvk.set_media_file("dvk0.png");
		dvk.write_dvk();
		//CREATE DVK1
		dvk.set_dvk_file(new File(this.sub, "dvk1.dvk"));
		dvk.set_id("DVK1");
		dvk.set_title("Page 1.05");
		dvk.set_time_int(2020, 1, 29, 10, 20);
		dvk.set_artist("Artist2");
		dvk.set_page_url("/page1");
		dvk.set_media_file("dvk1.png");
		dvk.write_dvk();
		//CREATE DVK2
		dvk.set_dvk_file(new File(this.f2, "dvk2.dvk"));
		dvk.set_id("DVK2");
		dvk.set_title("Page 1.5");
		dvk.set_time_int(2020, 1, 29, 8, 20);
		dvk.set_artist("Artist1");
		dvk.set_page_url("/page2");
		dvk.set_media_file("dvk2.png");
		dvk.write_dvk();
		//CREATE DVK3
		dvk.set_dvk_file(new File(f3, "dvk3.dvk"));
		dvk.set_id("DVK3");
		dvk.set_title("Page 10");
		dvk.set_time_int(2018, 1, 12, 8, 20);
		String[] artists = {"Artist2", "Artist3"};
		dvk.set_artists(artists);
		dvk.set_page_url("/page3");
		dvk.set_media_file("dvk3.png");
		dvk.write_dvk();
		//CREATE DVK4
		dvk.set_dvk_file(new File(f3, "dvk4.dvk"));
		dvk.set_id("DVK4");
		dvk.set_title("Something");
		dvk.set_time_int(2018, 1, 12, 8, 20);
		dvk.set_artist("Artist1");
		dvk.set_page_url("/page4");
		dvk.set_media_file("dvk4.png");
		dvk.write_dvk();
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
	 * Tests the read_dvks method.
	 */
	@Test
	public void test_read_dvks() {
		//CREATE INDEX DIRECTORY
		File index_dir = new File(this.test_dir, "indexing");
		if(!index_dir.isDirectory()) {
			index_dir.mkdir();
		}
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(index_dir);
		//TEST INVALID DIRECTORIES
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(
				null, prefs, null, false, false, false);
		assertEquals(0, handler.get_size());
		assertEquals(0, handler.get_size());
		//LOAD FROM MAIN TEST DIRECTORY
		File[] dirs = {this.test_dir};
		handler.read_dvks(
				dirs, prefs, null, false, false, false);
		assertEquals(5, handler.get_size());
		assertEquals("Page 1", handler.get_dvk(0).get_title());
		assertEquals("Page 1.05", handler.get_dvk(1).get_title());
		assertEquals("Page 1.5", handler.get_dvk(2).get_title());
		assertEquals("Page 10", handler.get_dvk(3).get_title());
		assertEquals("Something", handler.get_dvk(4).get_title());
		//LOAD FROM MULTIPLE DIRECTORIES
		dirs = new File[2];
		dirs[0] = this.f1;
		dirs[1] = this.f2;
		handler.read_dvks(
				dirs, prefs, null, false, false, true);
		assertEquals(3, handler.get_size());
		//CHECK SAVED INDEX
		File[] files = index_dir.listFiles();
		Arrays.sort(files);
		assertEquals(4, files.length);
		assertEquals("1.ser", files[0].getName());
		assertEquals("2.ser", files[1].getName());
		assertEquals("3.ser", files[2].getName());
		assertEquals("indexes.json", files[3].getName());	
	}
	
	/**
	 * Tests the add_dvk method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_add_dvk() {
		DvkHandler dvk_handler;
		dvk_handler = new DvkHandler();
		assertEquals(0, dvk_handler.get_size());
		Dvk dvk = new Dvk();
		dvk.set_title("Title!");
		dvk_handler.add_dvk(dvk);
		assertEquals(1, dvk_handler.get_size());
		assertEquals("Title!", dvk_handler.get_dvk(0).get_title());
	}
	
	/**
	 * Tests the set_dvk method.
	 */
	@Test
	public void test_set_dvk() {
		FilePrefs prefs = new FilePrefs();
		File[] dirs = {this.test_dir};
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, true, false);
		handler.sort_dvks_title(false, false);
		//TEST SETTING DVKS
		assertEquals(5, handler.get_size());
		assertEquals("Page 1", handler.get_dvk(0).get_title());
		Dvk dvk = new Dvk();
		dvk.set_title("New title");
		dvk.set_id("id-bleh");
		handler.set_dvk(dvk, 0);
		assertEquals("New title", handler.get_dvk(0).get_title());
	}
	
	/**
	 * Tests the get_size method.
	 */
	@Test
	public void test_get_size() {
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		assertEquals(0, handler.get_size());
		File[] dirs = {this.test_dir};
		handler.read_dvks(
				dirs, prefs, null, false, false, false);
		assertEquals(5, handler.get_size());
	}
	
	/**
	 * Tests the get_directories methods.
	 */
	@Test
	public void test_get_directories() {
		//TEST INVALID DIRECTORY
		File dir = null;
		File[] dirs = null;
		dirs = DvkHandler.get_directories(dirs);
		assertEquals(0, dirs.length);
		dirs = DvkHandler.get_directories(dir);
		assertEquals(0, dirs.length);
		//TEST NON-EXISTANT DIRECTORIES
		dirs = new File[2];
		dirs[0] = null;
		dirs[1] = new File("ljalkdmwner");
		dirs = DvkHandler.get_directories(dirs);
		assertEquals(0, dirs.length);
		dir = new File("kljasdsf");
		dirs = DvkHandler.get_directories(dir);
		assertEquals(0, dirs.length);
		//TEST SINGLE DIRECTORY
		dirs = DvkHandler.get_directories(this.test_dir);
		assertEquals(4, dirs.length);
		assertEquals("f1", dirs[0].getName());
		assertEquals("sub", dirs[1].getName());
		assertEquals("f2", dirs[2].getName());
		assertEquals("f3", dirs[3].getName());
		//TEST PARTIALLY VALID DIRECTORIES
		dirs = new File[2];
		dirs[0] = null;
		dirs[1] = this.test_dir;
		dirs = DvkHandler.get_directories(dirs);
		assertEquals(4, dirs.length);
		//TEST MULTIPLE DIRECTORIES
		dirs = new File[2];
		dirs[0] = this.f1;
		dirs[1] = this.f2;
		dirs = DvkHandler.get_directories(dirs);
		assertEquals(3, dirs.length);
		assertEquals("f1", dirs[0].getName());
		assertEquals("sub", dirs[1].getName());
		assertEquals("f2", dirs[2].getName());
		//TEST OVERLAPPING DIRECTORIES
		dirs = new File[2];
		dirs[0] = this.f1;
		dirs[1] = this.sub;
		dirs = DvkHandler.get_directories(dirs);
		assertEquals(2, dirs.length);
		assertEquals("f1", dirs[0].getName());
		assertEquals("sub", dirs[1].getName());
	}
	
	/**
	 * Tests the sort_title method.
	 */
	@Test
	public void test_sort_title() {
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		File[] dirs = {this.test_dir};
		handler.read_dvks(dirs, prefs, null, false, false, false);
		//TEST STANDARD TITLE SORT
		handler.sort_dvks_title(false, false);
		assertEquals(5, handler.get_size());
		assertEquals("Page 1", handler.get_dvk(0).get_title());
		assertEquals("Page 1.05", handler.get_dvk(1).get_title());
		assertEquals("Page 1.5", handler.get_dvk(2).get_title());
		assertEquals("Page 10", handler.get_dvk(3).get_title());
		assertEquals("Something", handler.get_dvk(4).get_title());
		//TEST GROUPED ARTISTS
		handler.sort_dvks_title(true, false);
		assertEquals(5, handler.get_size());
		assertEquals("Page 1", handler.get_dvk(0).get_title());
		assertEquals("Page 1.5", handler.get_dvk(1).get_title());
		assertEquals("Something", handler.get_dvk(2).get_title());
		assertEquals("Page 1.05", handler.get_dvk(3).get_title());
		assertEquals("Page 10", handler.get_dvk(4).get_title());
		//TEST INVERTED
		handler.sort_dvks_title(false, true);
		assertEquals(5, handler.get_size());
		assertEquals("Something", handler.get_dvk(0).get_title());
		assertEquals("Page 10", handler.get_dvk(1).get_title());
		assertEquals("Page 1.5", handler.get_dvk(2).get_title());
		assertEquals("Page 1.05", handler.get_dvk(3).get_title());
		assertEquals("Page 1", handler.get_dvk(4).get_title());
	}
	
	/**
	 * Tests the sort_time method.
	 */
	@Test
	public void test_sort_time() {
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		File[] dirs = {this.test_dir};
		handler.read_dvks(
				dirs, prefs, null, false, false, false);
		//TEST STANDARD TIME SORT
		handler.sort_dvks_time(false, false);
		assertEquals(5, handler.get_size());
		assertEquals("Page 10", handler.get_dvk(0).get_title());
		assertEquals("Something", handler.get_dvk(1).get_title());
		assertEquals("Page 1.5", handler.get_dvk(2).get_title());
		assertEquals("Page 1.05", handler.get_dvk(3).get_title());
		assertEquals("Page 1", handler.get_dvk(4).get_title());
		//TEST GROUPED ARTISTS
		handler.sort_dvks_time(true, false);
		assertEquals(5, handler.get_size());
		assertEquals("Something", handler.get_dvk(0).get_title());
		assertEquals("Page 1.5", handler.get_dvk(1).get_title());
		assertEquals("Page 1", handler.get_dvk(2).get_title());
		assertEquals("Page 1.05", handler.get_dvk(3).get_title());
		assertEquals("Page 10", handler.get_dvk(4).get_title());
		//TEST INVERTED
		handler.sort_dvks_time(false, true);
		assertEquals(5, handler.get_size());
		assertEquals("Page 1", handler.get_dvk(0).get_title());
		assertEquals("Page 1.05", handler.get_dvk(1).get_title());
		assertEquals("Page 1.5", handler.get_dvk(2).get_title());
		assertEquals("Something", handler.get_dvk(3).get_title());
		assertEquals("Page 10", handler.get_dvk(4).get_title());
	}
}
