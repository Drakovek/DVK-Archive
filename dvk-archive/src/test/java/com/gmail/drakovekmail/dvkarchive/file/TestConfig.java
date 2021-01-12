package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the Config class.
 * 
 * @author Drakovek
 */
public class TestConfig {
	
	/**
	 * Main directory for holding test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();

	/**
	 * Tests the get_config_directory and set_config_directory methods.
	 */
	@Test
	public void test_get_set_config_directory() {
		try {
			//SET UP TESTING VARIABLES
			Config config = new Config();
			File new_dir = this.temp_dir.newFolder("config");
			File invalid_dir = new File(this.temp_dir.getRoot(), "non-existant");
			assertTrue(new_dir.exists());
			assertFalse(invalid_dir.exists());
			//TEST GETTING DEFAULT CONFIG DIRECTORY WHEN DIRECTORY HASN'T BEEN SET
			config.set_config_directory(null);
			File config_dir = config.get_config_directory();
			assertEquals(Config.get_source_directory(), config_dir);
			//TEST SETTING CONFIG DIRECTORY
			config.set_config_directory(new_dir);
			config_dir = config.get_config_directory();
			assertEquals(new_dir, config_dir);
			assertTrue(config_dir.isDirectory());
			//TEST GETTING CONFIG DIRECTORY FROM PREFERENCE FILE
			config = null;
			config_dir = null;
			config = new Config();
			config_dir = config.get_config_directory();
			assertEquals(new_dir, config_dir);
			//TEST SETTING INVALID CONFIG DIRECTORY
			config.set_config_directory(invalid_dir);
			config_dir = config.get_config_directory();
			assertNotEquals(invalid_dir, config_dir);
			assertEquals(Config.get_source_directory(), config_dir);
			//TEST LOADING DEFAULT DIRECTORY
			config.set_config_directory(null);
			config = null;
			config_dir = null;
			config = new Config();
			config_dir = config.get_config_directory();
			assertEquals(Config.get_source_directory(), config_dir);
		} catch (IOException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_source_directory method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_source_directory() {
		File source = Config.get_source_directory();
		assertNotEquals(null, source);
		assertEquals("dvk-data", source.getName());
		assertTrue(source.isDirectory());
	}
}
