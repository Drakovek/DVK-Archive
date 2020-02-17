package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the DvkIndexing class.
 * 
 * @author Drakovek
 */
public class TestDvkIndexing {
	
	/**
	 * Main directory for holding test files.
	 */
	private File test_dir;
	
	/**
	 * Directory to use as index file directory when testing.
	 */
	private File index_dir;
	
	/**
	 * Creates files for testing.
	 */
	@Before
	public void create_test_files() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "dvkindexdir");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		this.index_dir = new File(this.test_dir, "index");
		if(!this.index_dir.isDirectory()) {
			this.index_dir.mkdir();
		}
	}

	/**
	 * Deletes all test files.
	 */
	@After
	public void delete_test_files() {
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the get_dvk_directory method.
	 */
	@Test
	public void test_get_dvk_directory() {
		//WRITE DVK1
		Dvk dvk = new Dvk();
		dvk.set_id("ID1");
		dvk.set_title("Title1");
		dvk.set_artist("Artist");
		dvk.set_page_url("/page");
		dvk.set_dvk_file(new File(this.test_dir, "dvk1.dvk"));
		dvk.set_media_file("dvk.png");
		dvk.write_dvk();
		//WRITE DVK2
		dvk.set_id("ID2");
		dvk.set_title("Title2");
		dvk.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk.write_dvk();
		//READ DIRECTORY WHILE SAVING INDEX
		DvkIndexing index = new DvkIndexing(this.index_dir);
		DvkDirectory ddir;
		ddir = index.get_dvk_directory(
				this.test_dir, false, false, true);
		assertEquals(2, ddir.get_dvks().size());
		//UPDATE DVK2
		dvk.set_title("New Title");
		dvk.write_dvk();
		//CREATE DVK3
		dvk.set_id("ID3");
		dvk.set_title("Title3");
		dvk.set_dvk_file(new File(this.test_dir, "dvk3.dvk"));
		dvk.write_dvk();
		//DELETE DVK1
		File file = new File(this.test_dir, "dvk1.dvk");
		assertTrue(file.delete());
		//TEST UPDATED INDEX
		ddir = index.get_dvk_directory(this.test_dir, true, true, false);
		assertEquals(2, ddir.get_dvks().size());
		boolean match = false;
		for(int i = 0; i < 2; i++) {
			dvk = ddir.get_dvks().get(i);
			if(dvk.get_id().equals("ID2")
					&& dvk.get_title().equals("New Title")) {
				match = true;
				break;
			}
		}
		match = false;
		for(int i = 0; i < 2; i++) {
			dvk = ddir.get_dvks().get(i);
			if(dvk.get_id().equals("ID3")) {
				match = true;
				break;
			}
		}
		assertTrue(match);
		//DELETE ALL DVKS
		file = new File(this.test_dir, "dvk2.dvk");
		file.delete();
		file = new File(this.test_dir, "dvk3.dvk");
		file.delete();
		//CHECK INDEX STILL THE SAME
		ddir = index.get_dvk_directory(this.test_dir, true, false, false);
		assertEquals(2, ddir.get_dvks().size());
		match = false;
		for(int i = 0; i < 2; i++) {
			dvk = ddir.get_dvks().get(i);
			if(dvk.get_id().equals("ID2")
					&& dvk.get_title().equals("Title2")) {
				match = true;
				break;
			}
		}
		assertTrue(match);
	}

	/**
	 * Tests the get_index_directory and set_index_directory methods.
	 */
	@Test
	public void test_get_set_directory() {
		DvkIndexing index = new DvkIndexing(this.index_dir);
		index.set_index_directory(this.test_dir);
		assertEquals(this.test_dir, index.get_index_directory());
		index.set_index_directory(null);
		assertEquals(this.test_dir, index.get_index_directory());
		index.set_index_directory(new File("ahhhh"));
		assertEquals("ahhhh", index.get_index_directory().getName());
	}
	
	/**
	 * Tests the read_index_list and write_index_list methods.
	 */
	@Test
	public void test_read_write_index_list() {
		//CREATE TEST FILES
		File file1 = new File(this.test_dir, "file1.ser");
		File file2 = new File(this.test_dir, "file2.ser");
		File file3 = new File(this.test_dir, "file3.ser");
		try {
			file1.createNewFile();
			file2.createNewFile();
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//CREATE AND SAVE INDEX LIST
		DvkIndexing index = new DvkIndexing(this.index_dir);
		index.add_to_index_list(file1, this.test_dir);
		index.add_to_index_list(file3, this.index_dir);
		index.write_index_list();
		//READ INDEX LIST
		index = new DvkIndexing(this.index_dir);
		index.read_index_list();
		assertEquals(1, index.get_index_list().size());
		assertEquals(file1, index.get_index_list().get(0)[0]);
		assertEquals(this.test_dir, index.get_index_list().get(0)[1]);
		//READ INVALID INDEX LIST
		file1 = new File(this.index_dir, DvkIndexing.INDEX_LIST);
		file1.delete();
		JSONObject json = new JSONObject();
		json.put("file_type", "kjsdk");
		InOut.write_file(file1, json.toString(4));
		index = new DvkIndexing(this.index_dir);
		index.read_index_list();
		assertEquals(0, index.get_index_list().size());
		//READ FROM NON-EXISTANT DIRECTORIES
		index = new DvkIndexing(null);
		index.read_index_list();
		assertEquals(0, index.get_index_list().size());
		index = new DvkIndexing(new File(this.test_dir, "kljsd"));
		index.read_index_list();
		assertEquals(0, index.get_index_list().size());
	}
	
	/**
	 * Tests the add_to_index_list method.
	 */
	@Test
	public void test_add_to_index_list() {
		DvkIndexing index = new DvkIndexing(this.index_dir);
		assertEquals(0, index.get_index_list().size());
		//TEST ADDING INVALID FILES
		File file = new File(this.index_dir, "1.ser");
		index.add_to_index_list(null, null);
		assertEquals(0, index.get_index_list().size());
		index.add_to_index_list(this.test_dir, null);
		assertEquals(0, index.get_index_list().size());
		index.add_to_index_list(null, file);
		assertEquals(0, index.get_index_list().size());
		//TEST ADDING VALID FILE
		index.add_to_index_list(this.test_dir, file);
		assertEquals(1, index.get_index_list().size());
		assertEquals(this.test_dir,
				index.get_index_list().get(0)[0]);
		assertEquals(file, index.get_index_list().get(0)[1]);
	}
	
	/**
	 * Tests the clean_index_list directory.
	 */
	@Test
	public void test_clean_index_list() {
		DvkIndexing index = new DvkIndexing(this.index_dir);
		File dir = new File(this.index_dir, "dir");
		File file1 = new File(this.index_dir, "file1.ser");
		File file2 = new File(this.index_dir, "file2.ser");
		try {
			file2.createNewFile();
		}
		catch(IOException e) {
			assertTrue(false);
		}
		assertEquals(0, index.get_index_list().size());
		//ADD ITEMS TO DIR LIST
		index.add_to_index_list(this.index_dir, this.index_dir);
		index.add_to_index_list(file1, this.index_dir);
		index.add_to_index_list(file2, dir);
		index.add_to_index_list(file2, this.index_dir);
		assertEquals(4, index.get_index_list().size());
		//CLEAN LIST
		index.clean_index_list();
		assertEquals(1, index.get_index_list().size());
		assertEquals(file2, index.get_index_list().get(0)[0]);
		assertEquals(this.index_dir, index.get_index_list().get(0)[1]);
	}
	
	/**
	 * Tests the clean_index_directory method.
	 */
	@Test
	public void test_clean_index_directory() {
		//CREATE FILES
		DvkIndexing index = new DvkIndexing(this.index_dir);
		assertEquals(0, index.get_index_list().size());
		File file1 = new File(this.index_dir, "file1.ser");
		File file2 = new File(this.index_dir, "file2.ser");
		File file3 = new File(this.index_dir, "file3.ser");
		File file4 = new File(this.index_dir, "file4.bleh");
		File json = new File(this.index_dir, DvkIndexing.INDEX_LIST);
		try {
			file1.createNewFile();
			file2.createNewFile();
			file3.createNewFile();
			file4.createNewFile();
			json.createNewFile();
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//BEFORE DELETING
		File[] files = this.index_dir.listFiles();
		assertEquals(5, files.length);
		//ADD FILES THEN CLEAN DIRECTORY
		index.add_to_index_list(file1, this.index_dir);
		index.clean_index_directory();
		files = this.index_dir.listFiles();
		//CHECK REMAINING FILES
		assertEquals(3, files.length);
		boolean match = false;
		for(int i = 0; i < 3; i++) {
			if(files[i].getName().equals("file1.ser")) {
				match = true;
				break;
			}
		}
		assertTrue(match);
		match = false;
		for(int i = 0; i < 3; i++) {
			if(files[i].getName().equals(DvkIndexing.INDEX_LIST)) {
				match = true;
				break;
			}
		}
		assertTrue(match);
	}
	
	/**
	 * Tests the index_of_index method.
	 */
	@Test
	public void test_index_of_index() {
		DvkIndexing index = new DvkIndexing(this.index_dir);
		assertEquals(0, index.get_index_list().size());
		File file1 = new File(this.index_dir, "file1.ser");
		File file2 = new File(this.index_dir, "file2.ser");
		index.add_to_index_list(file1, this.index_dir);
		assertEquals(0, index.index_of_index(file1));
		assertEquals(-1, index.index_of_index(file2));
	}
	
	/**
	 * Tests the index_of_directory method.
	 */
	@Test
	public void test_index_of_directory() {
		DvkIndexing index = new DvkIndexing(this.index_dir);
		assertEquals(0, index.get_index_list().size());
		File file = new File(this.index_dir, "file.ser");
		File dir = new File(this.index_dir, "dir");
		index.add_to_index_list(file, dir);
		assertEquals(0, index.index_of_directory(dir));
		assertEquals(1, index.index_of_directory(this.test_dir));
		assertEquals("1.ser",
				index.get_index_list().get(1)[0].getName());
		assertEquals(this.test_dir,
				index.get_index_list().get(1)[1]);
		assertEquals(2, index.index_of_directory(this.index_dir));
		assertEquals("2.ser",
				index.get_index_list().get(2)[0].getName());
		assertEquals(this.index_dir,
				index.get_index_list().get(2)[1]);
	}
}
