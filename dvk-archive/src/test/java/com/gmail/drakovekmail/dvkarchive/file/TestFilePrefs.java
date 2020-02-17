package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the FilePrefs class.
 * 
 * @author Drakovek
 */
public class TestFilePrefs {
	
	/**
	 * Main directory for holding test files.
	 */
	private File test_dir;
	
	/**
	 * Creates directory for holding test files.
	 */
	@Before
	public void create_test_directory() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "fileprefstest");
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
	 * Tests the save_preferences and load_preferences methods.
	 */
	@Test
	public void test_save_load_preferences() {
		//SET PREFERENCES
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.test_dir);
		prefs.set_use_index(false);
		//SAVE PREFERENCES
		prefs.save_preferences();
		//CHECK LOADED PREFERENCES
		prefs = new FilePrefs();
		prefs.load_preferences();
		assertEquals(this.test_dir, prefs.get_index_dir());
		assertFalse(prefs.use_index());
	}
	
	/**
	 * Tests the get_index_dir and set_index_dir methods.
	 */
	@Test
	public void test_get_set_index_dir() {
		//TEST INVALID FILES
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(null);
		assertEquals("", prefs.get_index_dir().getName());
		//TEST VALID FILE
		File dir = new File(this.test_dir, "bleh");
		prefs.set_index_dir(dir);
		assertEquals(dir, prefs.get_index_dir());
	}
	
	/**
	 * Tests the use_indexes and set_use_indexes methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void get_set_use_indexes() {
		FilePrefs prefs = new FilePrefs();
		prefs.set_use_index(true);
		assertTrue(prefs.use_index());
		prefs.set_use_index(false);
		assertFalse(prefs.use_index());
	}
}
