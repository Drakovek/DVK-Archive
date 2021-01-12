package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	 * Sub-directory of temp_dir that contains no DVK files
	 */
	private File no_dvks;
	
	/**
	 * Sub-directory of temp_dir for storing additional test files
	 */
	private File main_sub;
	
	/**
	 * Create test files for running error finding tests.
	 */
	@Before
	public void create_test_files() {
		try {
			//CREATE TEST DIRECTORIES
			this.no_dvks = this.temp_dir.newFolder("no_dvks");
			this.main_sub = this.temp_dir.newFolder("main_sub");
			//CREATE UNLINKED FILES
			this.temp_dir.newFile("unlinked_main.txt");
			this.temp_dir.newFile("main_sub/unlinked_sub.png");
			this.temp_dir.newFile("no_dvks/unlinked_no_dvk.jpg");
			//CREATE MEDIA FILES TO BE LINKED BY DVK FILES
			this.temp_dir.newFile("valid_media.png");
			this.temp_dir.newFile("mm_second.txt");
			this.temp_dir.newFile("main_sub/valid_second.png");
			this.temp_dir.newFile("main_sub/linked.txt");
			//CREATE DVKS IN THE MAIN DIRECTORY
			Dvk missing_media_dvk = new Dvk();
			missing_media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "mm.dvk"));
			missing_media_dvk.set_dvk_id("MM123");
			missing_media_dvk.set_title("Missing Media");
			missing_media_dvk.set_artist("artist");
			missing_media_dvk.set_page_url("/url/");
			missing_media_dvk.set_media_file("unlinked_sub.png");
			missing_media_dvk.write_dvk();
			Dvk valid_media_dvk = new Dvk();
			valid_media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "valid_media.dvk"));
			valid_media_dvk.set_dvk_id("VAL123");
			valid_media_dvk.set_title("Valid");
			valid_media_dvk.set_artist("artist");
			valid_media_dvk.set_page_url("/url/");
			valid_media_dvk.set_media_file("valid_media.png");
			valid_media_dvk.write_dvk();
			Dvk multi_media_dvk = new Dvk();
			multi_media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "multi_media.dvk"));
			multi_media_dvk.set_dvk_id("VAL123");
			multi_media_dvk.set_title("All Media Valid");
			multi_media_dvk.set_artist("artist");
			multi_media_dvk.set_page_url("/url/");
			multi_media_dvk.set_media_file("valid_media.png");
			multi_media_dvk.set_secondary_file("mm_second.txt");
			multi_media_dvk.write_dvk();
			//CREATE DVKS IN THE SUB DIRECTORY
			Dvk missing_second_dvk = new Dvk();
			missing_second_dvk.set_dvk_file(new File(this.main_sub, "ms.dvk"));
			missing_second_dvk.set_dvk_id("MM123");
			missing_second_dvk.set_title("Missing Secondary");
			missing_second_dvk.set_artist("artist");
			missing_second_dvk.set_page_url("/url/");
			missing_second_dvk.set_media_file("linked.txt");
			missing_second_dvk.set_secondary_file("non-existant.png");
			missing_second_dvk.write_dvk();
			Dvk valid_second_dvk = new Dvk();
			valid_second_dvk.set_dvk_file(new File(this.main_sub, "valid_second.dvk"));
			valid_second_dvk.set_dvk_id("UNI246");
			valid_second_dvk.set_title("Valid Secondary");
			valid_second_dvk.set_artist("artist");
			valid_second_dvk.set_page_url("/url/");
			valid_second_dvk.set_media_file("non-existant.txt");
			valid_second_dvk.set_secondary_file("valid_second.png");
			valid_second_dvk.write_dvk();
		}
		catch(IOException e) {}
	}

	/**
	 * Tests the get_unlinked_media function.
	 */
	@Test
	public void test_get_unlinked_media() {
		//SET UP A DVK HANDLER OBJECT AND READ ALL THE TEST DVK FILES
		Config config = new Config();
		config.set_config_directory(this.no_dvks);
		try(DvkHandler dvk_handler = new DvkHandler(config)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			//TEST GETTING UNLINKED FILES
			ArrayList<File> unlinked = ErrorFinding.get_unlinked_media(dvk_handler);
			assertEquals(2, unlinked.size());
			assertEquals("unlinked_sub.png", unlinked.get(0).getName());
			assertEquals(this.main_sub, unlinked.get(0).getParentFile());
			assertEquals("unlinked_main.txt", unlinked.get(1).getName());
			assertEquals(this.temp_dir.getRoot(), unlinked.get(1).getParentFile());
			//TEST THAT THERE ARE NO UNLINKED FILES IN THE NO_DVK TEST FOLDER
			dvk_handler.read_dvks(this.no_dvks);
			unlinked = ErrorFinding.get_unlinked_media(dvk_handler);
			assertEquals(0, unlinked.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_missing_media_dvks function.
	 */
	@Test
	public void test_get_missing_media_dvks() {
		//SET UP A DVK HANDLER OBJECT AND READ ALL THE TEST DVK FILES
		Config config = new Config();
		config.set_config_directory(this.no_dvks);
		try(DvkHandler dvk_handler = new DvkHandler(config)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			//ADD DVK WITH LINKED MEDIA SET TO NULL, 
			Dvk null_dvk = new Dvk();
			null_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "null.dvk"));
			null_dvk.set_dvk_id("NLL123");
			null_dvk.set_title("Null Media");
			null_dvk.set_artist("artist");
			null_dvk.set_page_url("/url/");
			null_dvk.set_media_file("");
			dvk_handler.add_dvk(null_dvk);
			//TEST GETTING DVK FILES WITH INVALID OR MISSING LINKED MEDIA FILES
			ArrayList<File> missing = ErrorFinding.get_missing_media_dvks(dvk_handler);
			assertEquals(4, missing.size());
			assertEquals("ms.dvk", missing.get(0).getName());
			assertEquals(this.main_sub, missing.get(0).getParentFile());
			assertEquals("valid_second.dvk", missing.get(1).getName());
			assertEquals(this.main_sub, missing.get(1).getParentFile());
			assertEquals("mm.dvk", missing.get(2).getName());
			assertEquals(this.temp_dir.getRoot(), missing.get(2).getParentFile());
			assertEquals("null.dvk", missing.get(3).getName());
			assertEquals(this.temp_dir.getRoot(), missing.get(3).getParentFile());
			//TEST GETTING MISSING MEDIA DVKS IN DIRECTORY WITH NO DVK FILES
			dvk_handler.read_dvks(this.no_dvks);
			missing = ErrorFinding.get_missing_media_dvks(dvk_handler);
			assertEquals(0, missing.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_same_ids function.
	 */
	@Test
	public void test_get_same_ids() {
		//SET UP A DVK HANDLER OBJECT AND READ ALL THE TEST DVK FILES
		Config config = new Config();
		config.set_config_directory(this.no_dvks);
		try(DvkHandler dvk_handler = new DvkHandler(config)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			//TEST GETTING DVKS WITH THE SAME ID
			ArrayList<ArrayList<File>> same = ErrorFinding.get_same_ids(dvk_handler);
			assertEquals(2, same.size());
			assertEquals(2, same.get(0).size());
			assertEquals("multi_media.dvk", same.get(0).get(0).getName());
			assertEquals("valid_media.dvk", same.get(0).get(1).getName());
			assertEquals(2, same.get(1).size());
			assertEquals("mm.dvk", same.get(1).get(0).getName());
			assertEquals("ms.dvk", same.get(1).get(1).getName());
			//TEST GETTING DVKS WITH THE SAME ID IN DIRECTORY WITHOUT DVK FILES
			dvk_handler.read_dvks(this.no_dvks);
			same = ErrorFinding.get_same_ids(dvk_handler);
			assertEquals(0, same.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
}
