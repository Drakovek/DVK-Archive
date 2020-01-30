package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DvkDirectory class.
 * 
 * @author Drakovek
 */
public class TestDvkDirectory {
	
	/**
	 * Directory in which to hold test files.
	 */
	private File test_dir;
	
	/**
	 * Creates the test directory for holding test files.
	 */
	@Before
	public void create_test_files() {
		//CREATE DIRECTORIES
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "dvkdir");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		File sub = new File(this.test_dir, "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		//CREATE DVK1
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.test_dir, "dvk1.dvk"));
		dvk.set_id("id123");
		dvk.set_title("Title 1");
		dvk.set_artist("Artist");
		dvk.set_page_url("/page/url");
		dvk.set_media_file("dvk1.png");
		dvk.write_dvk();
		//CREATE DVK2
		dvk.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk.set_id("id234");
		dvk.set_title("Title 2");
		dvk.write_dvk();
		//CREATE SUB DVK
		dvk.set_dvk_file(new File(sub, "sub.dvk"));
		dvk.set_id("id345");
		dvk.set_title("Sub");
		dvk.write_dvk();
		//CREATE NON-DVKS
		dvk.set_dvk_file(new File(this.test_dir, "dvk3.dmf"));
		dvk.set_id("id189");
		dvk.set_title("Not DVK");
		dvk.write_dvk();
		JSONObject json = new JSONObject();
		json.put("thing", "nope");
		json.put("not", "dvk");
		json.put("file_type", "not_dvk");
		InOut.write_file(new File(this.test_dir, "dvk4.dvk"), json.toString());
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
		//LOAD INVALID DIRECTORIES
		DvkDirectory dir = new DvkDirectory();
		assertEquals(0, dir.get_dvks().size());
		dir.read_dvks(null);
		assertEquals(0, dir.get_dvks().size());
		dir.read_dvks(new File("lsjdfwneajiqemrq"));
		assertEquals(0, dir.get_dvks().size());
		//LOAD TEST DIRECTORY
		dir.read_dvks(this.test_dir);
		assertEquals(2, dir.get_dvks().size());
		assertEquals("Title 1", dir.get_dvks().get(0).get_title());
		assertEquals("Title 2", dir.get_dvks().get(1).get_title());
	}
}
