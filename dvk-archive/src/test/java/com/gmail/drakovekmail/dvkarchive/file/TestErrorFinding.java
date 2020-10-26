package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the ErrorFinding class.
 * 
 * @author Drakovek
 */
public class TestErrorFinding {
	
	/**
	 * Main directory for storing test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Creates files for testing.
	 */
	@Before
	public void create_test_files() {
		try {
			//CREATE SUBDIRECTORIES
			File d1 = this.temp_dir.newFolder("d1");
			File d2 = this.temp_dir.newFolder("d2");
			File d3 = this.temp_dir.newFolder("d3");
			//CREATE DVK1
			Dvk dvk1 = new Dvk();
			dvk1.set_dvk_id("ID1");
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
			dvk2.set_dvk_id("ID1");
			dvk2.set_title("Dvk2");
			dvk2.set_artist("Artist");
			dvk2.set_page_url("page");
			dvk2.set_dvk_file(new File(d2, "dvk2.dvk"));
			dvk2.set_media_file("dvk2.png");
			dvk2.write_dvk();
			dvk2.get_media_file().createNewFile();
			//CREATE DVK3
			Dvk dvk3 = new Dvk();
			dvk3.set_dvk_id("ID3");
			dvk3.set_title("Dvk3");
			dvk3.set_artist("Artist");
			dvk3.set_page_url("page");
			dvk3.set_dvk_file(new File(d3, "dvk3.dvk"));
			dvk3.set_media_file("dvk3.jpg");
			dvk3.write_dvk();
			//CREATE DVK3
			Dvk dvk4 = new Dvk();
			dvk4.set_dvk_id("ID1");
			dvk4.set_title("Dvk4");
			dvk4.set_artist("Artist");
			dvk4.set_page_url("page");
			dvk4.set_dvk_file(new File(d3, "dvk4.dvk"));
			dvk4.set_media_file("dvk4.jpg");
			dvk4.set_secondary_file("dvk4.txt");
			dvk4.get_media_file().createNewFile();
			dvk4.write_dvk();
			//CREATE UNLINKED FILES
			File u1 = new File(this.temp_dir.getRoot(), "u1.png");
			u1.createNewFile();
			File u2 = new File(d3, "u2.jpg");
			u2.createNewFile();
			File u3 = new File(d2, "u3.jpg");
			u3.createNewFile();
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the get_unlinked_media method.
	 */
	@Test
	public void test_get_unlinked_media() {
		File[] dirs = {this.temp_dir.getRoot()};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
			ArrayList<File> unlinked;
			unlinked = ErrorFinding.get_unlinked_media(dvk_handler, dirs, null);
			assertEquals(2, unlinked.size());
			String[] names = new String[2];
			names[0] = unlinked.get(0).getName();
			names[1] = unlinked.get(1).getName();
			Arrays.sort(names);
			assertEquals("u2.jpg", names[0]);
			assertEquals("u3.jpg", names[1]);
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_missing_media_dvks method.
	 */
	@Test
	public void test_get_missing_media_dvks() {
		File[] dirs = {this.temp_dir.getRoot()};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
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
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_same_ids method.
	 */
	@Test
	public void test_get_same_ids() {
		File[] dirs = {this.temp_dir.getRoot()};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
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
		catch(DvkException e) {
			assertTrue(false);
		}
	}
}
