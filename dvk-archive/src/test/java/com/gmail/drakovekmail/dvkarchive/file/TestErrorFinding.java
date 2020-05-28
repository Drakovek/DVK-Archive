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
			//CREATE DVK3
			Dvk dvk4 = new Dvk();
			dvk4.set_id("ID1");
			dvk4.set_title("Dvk4");
			dvk4.set_artist("Artist");
			dvk4.set_page_url("page");
			dvk4.set_dvk_file(new File(d3, "dvk4.dvk"));
			dvk4.set_media_file("dvk4.jpg");
			dvk4.set_secondary_file("dvk4.txt");
			dvk4.get_media_file().createNewFile();
			dvk4.write_dvk();
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
		assertEquals(2, unlinked.size());
		String[] names = new String[2];
		names[0] = unlinked.get(0).getName();
		names[1] = unlinked.get(1).getName();
		Arrays.sort(names);
		assertEquals("u2.jpg", names[0]);
		assertEquals("u3.jpg", names[1]);
	}
	
	/**
	 * Tests the get_missing_media_dvks method.
	 */
	@Test
	public void test_get_missing_media_dvks() {
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler dvk_handler = new DvkHandler(prefs);
		dvk_handler.read_dvks(dirs, null);
		assertEquals(4, dvk_handler.get_size());
		ArrayList<File> missing;
		missing = ErrorFinding.get_missing_media_dvks(dvk_handler, null);
		assertEquals(2, missing.size());
		String[] names = new String[2];
		names[0] = missing.get(0).getName();
		names[1] = missing.get(1).getName();
		Arrays.sort(names);
		assertEquals("dvk3.dvk", names[0]);
		assertEquals("dvk4.dvk", names[1]);
	}
	
	/**
	 * Tests the get_same_ids method.
	 */
	@Test
	public void test_get_same_ids() {
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler dvk_handler = new DvkHandler(prefs);
		dvk_handler.read_dvks(dirs, null);
		assertEquals(4, dvk_handler.get_size());
		ArrayList<File> same;
		same = ErrorFinding.get_same_ids(dvk_handler, null);
		assertEquals(3, same.size());
		String[] names = new String[3];
		names[0] = same.get(0).getName();
		names[1] = same.get(1).getName();
		names[2] = same.get(2).getName();
		Arrays.sort(names);
		assertEquals("dvk1.dvk", names[0]);
		assertEquals("dvk2.dvk", names[1]);
		assertEquals("dvk4.dvk", names[2]);
	}
}
