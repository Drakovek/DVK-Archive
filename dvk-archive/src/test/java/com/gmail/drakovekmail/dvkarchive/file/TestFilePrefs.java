package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the FilePrefs class.
 * 
 * @author Drakovek
 */
public class TestFilePrefs {
	
	/**
	 * Main directory for holding test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Tests the save_preferences and load_preferences methods.
	 */
	@Test
	public void test_save_load_preferences() {
		//SET PREFERENCES
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		//SAVE PREFERENCES
		prefs.save_preferences();
		//CHECK LOADED PREFERENCES
		prefs = new FilePrefs();
		prefs.load_preferences();
		assertEquals(this.temp_dir.getRoot(), prefs.get_index_dir());
		//CHECK DEFAULT INDEX FOLDER
		prefs.set_index_dir(null);
		prefs.save_preferences();
		prefs.load_preferences();
		assertEquals("dvk-data", prefs.get_index_dir().getName());
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
		File dir = new File(this.temp_dir.getRoot(), "bleh");
		prefs.set_index_dir(dir);
		assertEquals(dir, prefs.get_index_dir());
	}
	
	/**
	 * Tests the get_default_directory method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_default_directory() {
		assertEquals("dvk-data", FilePrefs.get_default_directory().getName());
	}
}
