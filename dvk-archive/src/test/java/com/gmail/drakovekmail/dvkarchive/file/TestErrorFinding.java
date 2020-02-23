package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ErrorFinding class.
 * 
 * @author Drakovek
 */
public class TestErrorFinding {
	
	/**
	 * Main directory for storing test files.
	 */
	private File test_dir;
	
	/**
	 * Creates files for testing.
	 */
	@Before
	public void create_test_files() {
		//CREATE MAIN DIRECTORY
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "errortest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		//CREATE SUBDIRECTORIES
		File d1 = new File(this.test_dir, "d1");
		if(!d1.isDirectory()) {
			d1.mkdir();
		}
		File d2 = new File(this.test_dir, "d2");
		if(!d2.isDirectory()) {
			d2.mkdir();
		}
		File d3 = new File(this.test_dir, "d3");
		if(!d3.isDirectory()) {
			d3.mkdir();
		}
		try {
			//CREATE DVK1
			Dvk dvk1 = new Dvk();
			dvk1.set_id("ID1");
			dvk1.set_title("Dvk1");
			dvk1.set_artist("Artist");
			dvk1.set_page_url("/page/");
			dvk1.set_dvk_file(new File(d1, "dvk1.dvk"));
			dvk1.set_media_file("dvk1.txt");
			dvk1.set_secondary_file("dvk1.png");
			dvk1.write_dvk();
			dvk1.get_media_file().createNewFile();
			dvk1.get_secondary_file().createNewFile();
			//CREATE DVK2
			Dvk dvk2 = new Dvk();
			dvk2.set_id("ID1");
			dvk2.set_title("Dvk2");
			dvk2.set_artist("Artist");
			dvk2.set_page_url("page");
			dvk2.set_dvk_file(new File(d2, "dvk2.dvk"));
			dvk2.set_media_file("dvk2.png");
			dvk2.write_dvk();
			dvk2.get_media_file().createNewFile();
			//CREATE DVK3
			Dvk dvk3 = new Dvk();
			dvk3.set_id("ID3");
			dvk3.set_title("Dvk3");
			dvk3.set_artist("Artist");
			dvk3.set_page_url("page");
			dvk3.set_dvk_file(new File(d3, "dvk3.dvk"));
			dvk3.set_media_file("dvk3.jpg");
			dvk3.write_dvk();
			//CREATE UNLINKED FILES
			File u1 = new File(this.test_dir, "u1.png");
			u1.createNewFile();
			File u2 = new File(d3, "u2.jpg");
			u2.createNewFile();
			File u3 = new File(d2, "u3.jpg");
			u3.createNewFile();
		}
		catch(IOException e) {}
	}
	
	/**
	 * Deletes test files.
	 */
	@After
	public void remove_test_files() {
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the get_unlinked_media method.
	 */
	@Test
	public void test_get_unlinked_media() {
		FilePrefs prefs = new FilePrefs();
		prefs.set_use_index(false);
		File[] dirs = {this.test_dir};
		ArrayList<File> unlinked;
		unlinked = ErrorFinding.get_unlinked_media(prefs, dirs, null);
		Arrays.sort(dirs);
		assertEquals(2, unlinked.size());
		assertEquals("u3.jpg", unlinked.get(0).getName());
		assertEquals("u2.jpg", unlinked.get(1).getName());
	}
}
