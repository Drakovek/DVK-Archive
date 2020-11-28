package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.Before;
import org.junit.Rule;

/**
 * Unit tests for the DvkHandler class.
 * 
 * @author Drakovek
 */
public class TestDvkHandler {
	
	/**
	 * Main directory for holding test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Sub-directory that of temp_dir that contains test files
	 */
	private File main_sub;
	
	/**
	 * Sub-directory of temp_dir that by default, contains no DVK files
	 */
	private File empty;
	
	/**
	 * Sub-directory of empty directory
	 */
	private File sub_empty;
	
	/**
	 * Sub-directory of temp_dir that by default, contains no DVK files
	 */
	private File empty_2;
	
	/**
	 * Creates test DVK files to use in unit tests.
	 */
	@Before
	public void create_test_files() {
		try {
			//CREATE TEST DIRECTORIES
			this.main_sub = this.temp_dir.newFolder("sub");
			this.empty = this.temp_dir.newFolder("empty");
			this.sub_empty = this.temp_dir.newFolder("empty/sub_empty");
			this.empty_2 = this.temp_dir.newFolder("empty_2");
			//CREATE DVK FILES IN THE MAIN TEMPORARY DIRECTORY
			Dvk main_dvk_1 = new Dvk();
			main_dvk_1.set_dvk_file(new File(this.temp_dir.getRoot(), "main1.dvk"));
			main_dvk_1.set_dvk_id("MAN123");
			main_dvk_1.set_title("Title 10");
			main_dvk_1.set_artist("Artist 1");
			main_dvk_1.set_time_int(2020, 9, 4, 17, 13);
			String[] tags = {"tag1", "other tag", "Tag 3"};
			main_dvk_1.set_web_tags(tags);
			main_dvk_1.set_description("<p>Test &amp; such.</p>");
			main_dvk_1.set_page_url("page/url/");
			main_dvk_1.set_direct_url("/direct/URL/");
			main_dvk_1.set_secondary_url("sec/file/Url");
			main_dvk_1.set_media_file("main.txt");
			main_dvk_1.set_secondary_file("main.jpeg");
			main_dvk_1.write_dvk();
			Dvk main_dvk_2 = new Dvk();
			main_dvk_2.set_dvk_file(new File(this.temp_dir.getRoot(), "main2.dvk"));
			main_dvk_2.set_dvk_id("MAN123");
			main_dvk_2.set_title("TITLE 0.55");
			main_dvk_2.set_artist("Artist 2");
			main_dvk_2.set_page_url("/url/");
			main_dvk_2.set_media_file("main.txt");
			main_dvk_2.set_time_int(2018, 5, 20, 14, 15);
			main_dvk_2.write_dvk();
			//CREATE DVK FILE IN SUB DIRECTORY
			Dvk sub_dvk = new Dvk();
			sub_dvk.set_dvk_file(new File(this.main_sub, "sub.dvk"));
			sub_dvk.set_dvk_id("SUB1");
			sub_dvk.set_title("title 0.55");
			sub_dvk.set_artist("Artist 1");
			sub_dvk.set_page_url("/url/");
			sub_dvk.set_media_file("sub.txt");
			sub_dvk.set_time_int(2017, 10, 6, 12, 0);
			sub_dvk.write_dvk();
			//CREATE DVK FILE IN SUB_EMPTY DIRECTORY
			Dvk sub_empty_dvk = new Dvk();
			sub_empty_dvk.set_dvk_file(new File(this.sub_empty, "sub_empty.dvk"));
			sub_empty_dvk.set_dvk_id("SBE1");
			sub_empty_dvk.set_title("Title 2");
			sub_empty_dvk.set_artist("Test");
			sub_empty_dvk.set_page_url("/url/");
			sub_empty_dvk.set_media_file("sub.txt");
			sub_empty_dvk.set_time_int(2017, 10, 6, 12, 0);
			sub_empty_dvk.write_dvk();
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the inititalize_connection function.
	 */
	@Test
	public void test_initialize_connection() {
		//TEST DATABASE FILE DOESN'T CURRENTLY EXIST
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		File db = new File(this.temp_dir.getRoot(), "dvk_archive.db");
		assertFalse(db.exists());
		//INITIALIZE THE CONNECTION
		try (DvkHandler handler = new DvkHandler(prefs)) {
			handler.initialize_connection();
			//TEST DATABASE FILE NOW EXISTS
			assertTrue(db.exists());
			//CREATE DATABASE FILE THAT IS NOT PROPER SQLITE FILE
			handler.delete_database();
			assertFalse(db.exists());
			InOut.write_file(db, "text");
			assertTrue(db.exists());
			//TRY READING INVALID DATABASE FILE
			handler.initialize_connection();
			assertFalse(db.exists());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the read_dvks function.
	 */
	@Test
	public void test_read_dvks() {
		//INITIALIZE DVK_HANDLER
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs))
		{
			//TEST LOADING AN INVALID DIRECTORY
			dvk_handler.read_dvks(null);
			assertEquals(0, dvk_handler.get_dvks('a', false, false).size());
			dvk_handler.read_dvks(new File(this.temp_dir.getRoot(), "notreal"));
			assertEquals(0, dvk_handler.get_dvks('a', false, false).size());
			//LOAD DVKS FROM THE MAIN TEST DIRECTORY
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("TITLE 0.55", dvks.get(1).get_title());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			//TEST GETTING ALL INFORMATION FROM DVK
			assertEquals(this.temp_dir.getRoot(), dvks.get(3).get_dvk_file().getParentFile());
			assertEquals("main1.dvk", dvks.get(3).get_dvk_file().getName());
			assertEquals("MAN123", dvks.get(3).get_dvk_id());
			assertEquals("Title 10", dvks.get(3).get_title());
			assertEquals(1, dvks.get(3).get_artists().length);
			assertEquals("Artist 1", dvks.get(3).get_artists()[0]);
			assertEquals(3, dvks.get(3).get_web_tags().length);
			assertEquals("2020/09/04|17:13", dvks.get(3).get_time());
			assertEquals("tag1", dvks.get(3).get_web_tags()[0]);
			assertEquals("other tag", dvks.get(3).get_web_tags()[1]);
			assertEquals("Tag 3", dvks.get(3).get_web_tags()[2]);
			assertEquals("<p>Test &amp; such.</p>", dvks.get(3).get_description());
			assertEquals("page/url/", dvks.get(3).get_page_url());
			assertEquals("/direct/URL/", dvks.get(3).get_direct_url());
			assertEquals("sec/file/Url", dvks.get(3).get_secondary_url());
			assertEquals(this.temp_dir.getRoot(), dvks.get(3).get_media_file().getParentFile());
			assertEquals("main.txt", dvks.get(3).get_media_file().getName());
			assertEquals(this.temp_dir.getRoot(), dvks.get(3).get_secondary_file().getParentFile());
			assertEquals("main.jpeg", dvks.get(3).get_secondary_file().getName());
			//TRY READING AN EMPTY DIRECTORY
			dvk_handler.read_dvks(this.empty_2);
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(0, dvks.size());
			//TEST READING AFTER NEW DVK HAS BEEN WRITTEN
			Dvk new_dvk = new Dvk();
			new_dvk.set_dvk_file(new File(this.empty_2, "new_dvk.dvk"));
			new_dvk.set_dvk_id("NEW123");
			new_dvk.set_title("New Dvk");
			new_dvk.set_artist("artist");
			new_dvk.set_page_url("/url/");
			new_dvk.set_media_file("file.png");
			new_dvk.write_dvk();
			assertTrue(new_dvk.get_dvk_file().exists());
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(5, dvks.size());
			assertEquals("New Dvk", dvks.get(0).get_title());
			assertEquals("title 0.55", dvks.get(1).get_title());
			assertEquals("TITLE 0.55", dvks.get(2).get_title());
			assertEquals("Title 2", dvks.get(3).get_title());
			assertEquals("Title 10", dvks.get(4).get_title());
			//TEST READING AFTER DVK HAS BEEN MODIFIED
			Dvk mod_dvk = dvks.get(4);
			mod_dvk.set_title("Modified");
			TimeUnit.SECONDS.sleep(1);
			mod_dvk.write_dvk();
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(5, dvks.size());
			assertEquals("Modified", dvks.get(0).get_title());
			assertEquals("New Dvk", dvks.get(1).get_title());
			assertEquals("title 0.55", dvks.get(2).get_title());
			assertEquals("TITLE 0.55", dvks.get(3).get_title());
			assertEquals("Title 2", dvks.get(4).get_title());
			//TEST READING AFTER DVK HAS BEEN DELETED
			dvks.get(1).get_dvk_file().delete();
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			assertEquals("Modified", dvks.get(0).get_title());
			assertEquals("title 0.55", dvks.get(1).get_title());
			assertEquals("TITLE 0.55", dvks.get(2).get_title());
			assertEquals("Title 2", dvks.get(3).get_title());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
		catch(InterruptedException f) {
			assertTrue(false);
		}
	}
	
	/**
	 * Test the remove_duplicates function.
	 */
	@Test
	public void test_remove_duplicates() {
		//INITIALIZE THE DVK HANDLER
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			//READ DVKS FROM AN EMPTY DIRECTORY
			dvk_handler.read_dvks(this.empty_2);
			assertEquals(0, dvk_handler.get_dvks('a', false, false).size());
			//ADD DVKS TO THE DVK HANDLER
			Dvk dvk = new Dvk();
			dvk.set_dvk_file(new File(this.empty_2, "dup.dvk"));
			dvk.set_dvk_id("ID123");
			dvk.set_title("Duplicated");
			dvk.set_artist("artist");
			dvk.set_page_url("/url/");
			dvk.set_media_file("duplicate");
			dvk_handler.add_dvk(dvk);
			dvk.set_dvk_id("ID456");
			dvk.set_title("dup");
			dvk_handler.add_dvk(dvk);
			dvk.set_dvk_file(new File(this.main_sub, "dup.dvk"));
			dvk.set_title("Not the same");
			dvk_handler.add_dvk(dvk);
			dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "other.dvk"));
			dvk.set_title("Dvk");
			dvk_handler.add_dvk(dvk);
			//CHECK ENTRIES ADDED TO THE DVK HANDLER
			assertEquals(4, dvk_handler.get_dvks('a', false, false).size());
			//TEST REMOVING THE DUPLICATE ENTRIES
			dvk_handler.remove_duplicates();
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(2, dvks.size());
			assertEquals("Dvk", dvks.get(0).get_title());
			assertEquals("Not the same", dvks.get(1).get_title());
			//MAKE SURE FUNCTION DOESN'T BREAK ON EXCEPTION
			dvk_handler.delete_database();
			dvk_handler.remove_duplicates();
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(0, dvks.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}

	/**
	 * Tests the delete_dvk function.
	 */
	@Test
	public void test_delete_dvk() {
		//READ DEFAULT TEST DVK FILES
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("TITLE 0.55", dvks.get(1).get_title());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			//TEST DELETING ONE OF THE DVK ENTRIES
			dvk_handler.delete_dvk(dvks.get(2).get_sql_id());
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(3, dvks.size());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("TITLE 0.55", dvks.get(1).get_title());
			assertEquals("Title 10", dvks.get(2).get_title());
			//TEST DELETING ANOTHER DVK ENTRY
			dvk_handler.delete_dvk(dvks.get(1).get_sql_id());
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(2, dvks.size());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("Title 10", dvks.get(1).get_title());
			//TEST DELETING A DVK ENTRY WITH A NON-EXISTANT SQL ID
			dvk_handler.delete_dvk(45);
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(2, dvks.size());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("Title 10", dvks.get(1).get_title());
			//MAKE SURE FUNCTION DOESN'T BREAK ON EXCEPTION
			dvk_handler.delete_database();
			dvk_handler.delete_dvk(1);
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(0, dvks.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the add_dvk function.
	 */
	@Test
	public void test_add_dvk() {
		//LOAD THE DEFAULT SET OF TEST DVK FILES
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			//ADD DVK TO THE DVK HANDLER
			Dvk dvk = new Dvk();
			dvk.set_dvk_file(new File(this.empty_2, "new.dvk"));
			dvk.set_dvk_id("NEW456");
			dvk.set_title("New DVK");
			String[] artists = {"Artist, Person", "Person 2"};
			dvk.set_artists(artists);
			dvk.set_time_int(1864, 10, 31, 7, 2);
			String[] web_tags = {"Tag, 1", "Tags! & such"};
			dvk.set_web_tags(web_tags);
			dvk.set_description("<p>Description &amp; such</p>");
			dvk.set_page_url("/page/URL");
			dvk.set_direct_url("/direct/media/Url");
			dvk.set_secondary_url("sec/media/URL");
			dvk.set_media_file("new.txt");
			dvk.set_secondary_file("new_s.png");
			dvk_handler.add_dvk(dvk);
			//TEST DVK WAS ADDED CORRECTLY
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(5, dvks.size());
			Dvk returned = dvks.get(0);
			assertEquals(5, returned.get_sql_id());
			assertEquals("NEW456", returned.get_dvk_id());
			assertEquals("New DVK", returned.get_title());
			assertEquals(2, returned.get_artists().length);
			assertEquals("Artist, Person", returned.get_artists()[0]);
			assertEquals("Person 2", returned.get_artists()[1]);
			assertEquals("1864/10/31|07:02", returned.get_time());
			assertEquals(2, returned.get_web_tags().length);
			assertEquals("Tag, 1", returned.get_web_tags()[0]);
			assertEquals("Tags! & such", returned.get_web_tags()[1]);
			assertEquals("<p>Description &amp; such</p>", returned.get_description());
			assertEquals("/page/URL", returned.get_page_url());
			assertEquals("/direct/media/Url", returned.get_direct_url());
			assertEquals("sec/media/URL", returned.get_secondary_url());
			assertEquals(this.empty_2, returned.get_dvk_file().getParentFile());
			assertEquals("new.dvk", returned.get_dvk_file().getName());
			assertEquals("new.txt", returned.get_media_file().getName());
			assertEquals("new_s.png", returned.get_secondary_file().getName());
			//ADD DVK WITH MOST FIELDS ABSENT
			dvk = new Dvk();
			dvk.set_dvk_file(new File(this.empty_2, "min.dvk"));
			dvk.set_dvk_id("MIN246");
			dvk.set_title("minimal");
			dvk.set_artist("Name");
			dvk.set_page_url("/url/");
			dvk_handler.add_dvk(dvk);
			//CHECK MINIMAL DVK WAS ADDED CORRECTLY
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(6, dvks.size());
			returned = dvks.get(0);
			assertEquals(6, returned.get_sql_id());
			assertEquals("MIN246", returned.get_dvk_id());
			assertEquals("minimal", returned.get_title());
			assertEquals(1, returned.get_artists().length);
			assertEquals("Name", returned.get_artists()[0]);
			assertEquals("0000/00/00|00:00", returned.get_time());
			assertTrue(returned.get_web_tags() == null);
			assertEquals(null, returned.get_description());
			assertEquals("/url/", returned.get_page_url());
			assertEquals(null, returned.get_direct_url());
			assertEquals(null, returned.get_secondary_url());
			assertEquals(this.empty_2, returned.get_dvk_file().getParentFile());
			assertEquals("min.dvk", returned.get_dvk_file().getName());
			assertEquals(null, returned.get_media_file());
			assertEquals(null, returned.get_secondary_file());
			//MAKE SURE FUNCTION DOESN'T BREAK ON EXCEPTION
			dvk_handler.delete_database();
			dvk_handler.add_dvk(dvk);
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(0, dvks.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the set_dvk function.
	 */
	@Test
	public void test_set_dvk() {
		//TEST READING THE DEFAULT TEST DVK FILES
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("TITLE 0.55", dvks.get(1).get_title());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			//CHANGE A DVK FILE USING THE SET_DVK FUNCTION
			Dvk dvk = dvks.get(1);
			dvk.set_dvk_id("DIF1234");
			dvk.set_title("Modified");
			String[] artists = {"person 1", "Artist&2"};
			dvk.set_artists(artists);
			dvk.set_time_int(1864, 10, 31, 7, 2);
			String[] tags = {"tag1", "Other, Tag's"};
			dvk.set_web_tags(tags);
			dvk.set_description("<p>Desciption &amp; stuff.</p>");
			dvk.set_page_url("/URL/page/");
			dvk.set_direct_url("Direct/Url/thing");
			dvk.set_secondary_url("sec/URL/page");
			dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "modified.dvk"));
			dvk.set_media_file("mod.txt");
			dvk.set_secondary_file("mod.jpg");
			dvk_handler.set_dvk(dvk, dvks.get(1).get_sql_id());
			//TEST DVK WAS SET CORRECTLY
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			assertEquals("Modified", dvks.get(0).get_title());
			assertEquals("title 0.55", dvks.get(1).get_title());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			Dvk returned = dvks.get(0);
			assertEquals(dvk.get_sql_id(), returned.get_sql_id());
			assertEquals("DIF1234", returned.get_dvk_id());
			assertEquals("Modified", returned.get_title());
			assertEquals(2, returned.get_artists().length);
			assertEquals("Artist&2", returned.get_artists()[0]);
			assertEquals("person 1", returned.get_artists()[1]);
			assertEquals("1864/10/31|07:02", returned.get_time());
			assertEquals(2, returned.get_web_tags().length);
			assertEquals("tag1", returned.get_web_tags()[0]);
			assertEquals("Other, Tag's", returned.get_web_tags()[1]);
			assertEquals("<p>Desciption &amp; stuff.</p>", returned.get_description());
			assertEquals("/URL/page/", returned.get_page_url());
			assertEquals("Direct/Url/thing", returned.get_direct_url());
			assertEquals("sec/URL/page", returned.get_secondary_url());
			assertEquals(this.temp_dir.getRoot(), returned.get_dvk_file().getParentFile());
			assertEquals("modified.dvk", returned.get_dvk_file().getName());
			assertEquals("mod.txt", returned.get_media_file().getName());
			assertEquals("mod.jpg", returned.get_secondary_file().getName());
			//SET DVK WITH MOST FIELDS ABSENT
			dvk = new Dvk();
			dvk.set_dvk_file(new File(this.empty_2, "min.dvk"));
			dvk.set_dvk_id("MIN246");
			dvk.set_title("minimal");
			dvk.set_artist("Name");
			dvk.set_page_url("/url/");
			dvk_handler.set_dvk(dvk, returned.get_sql_id());
			//TEST DVK WAS SET WITH DEFAULT VALUES
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			assertEquals("minimal", dvks.get(0).get_title());
			assertEquals("title 0.55", dvks.get(1).get_title());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			returned = dvks.get(0);
			assertEquals(dvks.get(0).get_sql_id(), returned.get_sql_id());
			assertEquals("MIN246", returned.get_dvk_id());
			assertEquals("minimal", returned.get_title());
			assertEquals(1, returned.get_artists().length);
			assertEquals("Name", returned.get_artists()[0]);
			assertEquals("0000/00/00|00:00", returned.get_time());
			assertTrue(returned.get_web_tags() == null);
			assertEquals(null, returned.get_description());
			assertEquals("/url/", returned.get_page_url());
			assertEquals(null, returned.get_direct_url());
			assertEquals(null, returned.get_secondary_url());
			assertEquals(this.empty_2, returned.get_dvk_file().getParentFile());
			assertEquals("min.dvk", returned.get_dvk_file().getName());
			assertEquals(null, returned.get_media_file());
			assertEquals(null, returned.get_secondary_file());
			//TEST SETTING DVK ENTRY WITH SQL ID THAT DOESN'T EXIST
			dvk_handler.set_dvk(dvk, 45);
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			//MAKE SURE FUNCTION DOESN'T BREAK ON EXCEPTION
			dvk_handler.delete_database();
			dvk_handler.set_dvk(dvk, 2);
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(0, dvks.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_dvks function.
	 */
	@Test
	public void test_get_dvks() {
		//SET UP DVK HANDLER
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			//TEST THAT FUNCTION DOESN'T BREAK WHEN NO DIRECTORIES HAVE BEEN LOADED
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(0, dvks.size());
			//READ THE DEFAULT TEST DVK FILES
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(4, dvks.size());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("TITLE 0.55", dvks.get(1).get_title());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			//TEST GETTING DVK FILES WITH A SPECIFIC SQL WHERE STATEMENT
			StringBuilder statement = new StringBuilder();
			statement.append(DvkHandler.TITLE);
			statement.append("=? OR ");
			statement.append(DvkHandler.TITLE);
			statement.append("=?");
			ArrayList<String> params = new ArrayList<>();
			params.add("Title 2");
			params.add("Title 10");
			dvks = dvk_handler.get_dvks('a', false, false, statement.toString(), params);
			assertEquals(2, dvks.size());
			assertEquals("Title 2", dvks.get(0).get_title());
			assertEquals("Title 10", dvks.get(1).get_title());
			//TEST REVERSING DVK ENTRIES
			dvks = dvk_handler.get_dvks('a', false, true, statement.toString(), params);
			assertEquals(2, dvks.size());
			assertEquals("Title 10", dvks.get(0).get_title());
			assertEquals("Title 2", dvks.get(1).get_title());
			//TEST GETTING DVK FILES WITH EXTRA PARAMETER
			StringBuilder extra = new StringBuilder();
			extra.append("GROUP BY ");
			extra.append(DvkHandler.DVK_ID);
			extra.append(" HAVING COUNT(");
			extra.append(DvkHandler.DVK_ID);
			extra.append(") > 1");
			dvks = dvk_handler.get_dvks('a', false, false, null, null, extra.toString());
			assertEquals(1, dvks.size());
			assertEquals("MAN123", dvks.get(0).get_dvk_id());
			assertEquals("Title 10", dvks.get(0).get_title());
			//MAKE SURE FUNCTION DOESN'T BREAK ON EXCEPTION
			dvk_handler.delete_database();
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(0, dvks.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_loaded_directories method.
	 */
	@Test
	public void test_get_loaded_directories() {
		//INITIALIZE DVK HANDLER
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			//TEST GETTING LOADED DIRECTORIES BEFORE ANYTHING IS LOADED
			ArrayList<File> dirs = dvk_handler.get_loaded_directories();
			assertEquals(0, dirs.size());
			//TEST GETTING LOADED DIRECTORIES
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			dirs = dvk_handler.get_loaded_directories();
			Collections.sort(dirs);
			assertEquals(3, dirs.size());
			assertEquals(this.temp_dir.getRoot(), dirs.get(0));
			assertEquals(this.sub_empty, dirs.get(1));
			assertEquals(this.main_sub, dirs.get(2));
			//MAKE SURE FUNCTION DOESN'T FAIL ON EXCPETION
			dvk_handler.delete_database();
			dirs = dvk_handler.get_loaded_directories();
			assertEquals(0, dirs.size());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_directories function.
	 */
	@Test
	public void test_get_directories() {
		//TEST GETTING ALL DIRECTORIES AND SUBDIRECTORIES
		ArrayList<File> dirs = DvkHandler.get_directories(this.temp_dir.getRoot(), false);
		Collections.sort(dirs);
		assertEquals(5, dirs.size());
		assertEquals(this.temp_dir.getRoot(), dirs.get(0));
		assertEquals(this.empty, dirs.get(1));
		assertEquals(this.sub_empty, dirs.get(2));
		assertEquals(this.empty_2, dirs.get(3));
		assertEquals(this.main_sub, dirs.get(4));
		//TEST ONLY GETTING DIRECTORIES WITH DVK FILES
		dirs = DvkHandler.get_directories(this.temp_dir.getRoot(), true);
		assertEquals(3, dirs.size());
		assertEquals(this.temp_dir.getRoot(), dirs.get(0));
		assertEquals(this.main_sub, dirs.get(1));
		assertEquals(this.sub_empty, dirs.get(2));		
		//TEST GETTING DIRECTORIES FROM DIRECTORY WITH NO DVK FILES
		dirs = DvkHandler.get_directories(this.empty_2, true);
		assertEquals(0, dirs.size());
		//TEST GETTING INVALID DIRECTORY
		dirs = DvkHandler.get_directories(null, false);
		assertEquals(0, dirs.size());
		dirs = DvkHandler.get_directories(new File(this.temp_dir.getRoot(), "notreal"), true);
		assertEquals(0, dirs.size());
	}
	
	/**
	 * Tests the get_sortable_string function.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_sortable_string() {
		//TEST CONVERTING STRINGS SO THAT THEY ARE SORTABLE BY NUMERIC VALUE WHEN SORTED ALPHABETICALLY
		assertEquals("title 00001", DvkHandler.get_sortable_string("Title 1"));
		assertEquals("title 00002", DvkHandler.get_sortable_string("TiTlE 002"));
		assertEquals("test 00052-45", DvkHandler.get_sortable_string("  TeSt!  052.45  "));
		assertEquals("00000-1-25 00002-3", DvkHandler.get_sortable_string("0.1.25  2.3"));
		assertEquals("00002-5 - 00003-44 words 00030", DvkHandler.get_sortable_string("2.5 & 3.44 wordS 30"));
	}

	/**
	 * Tests the get_dvk function.
	 */
	@Test
	public void test_get_dvk() {
		//READ DEFAULT TEST DVKS
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("TITLE 0.55", dvks.get(1).get_title());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			//TEST GETTING DVK BASED ON SQL ID
			Dvk returned = dvk_handler.get_dvk(dvks.get(3).get_sql_id());
			assertEquals(this.temp_dir.getRoot(), returned.get_dvk_file().getParentFile());
			assertEquals("main1.dvk", returned.get_dvk_file().getName());
			assertEquals("MAN123", returned.get_dvk_id());
			assertEquals("Title 10", returned.get_title());
			assertEquals(1, returned.get_artists().length);
			assertEquals("Artist 1", returned.get_artists()[0]);
			assertEquals(3, returned.get_web_tags().length);
			assertEquals("2020/09/04|17:13", returned.get_time());
			assertEquals("tag1", returned.get_web_tags()[0]);
			assertEquals("other tag", returned.get_web_tags()[1]);
			assertEquals("Tag 3", returned.get_web_tags()[2]);
			assertEquals("<p>Test &amp; such.</p>", returned.get_description());
			assertEquals("page/url/", returned.get_page_url());
			assertEquals("/direct/URL/", returned.get_direct_url());
			assertEquals("sec/file/Url", returned.get_secondary_url());
			assertEquals(this.temp_dir.getRoot(), returned.get_media_file().getParentFile());
			assertEquals("main.txt", returned.get_media_file().getName());
			assertEquals(this.temp_dir.getRoot(), returned.get_secondary_file().getParentFile());
			assertEquals("main.jpeg", returned.get_secondary_file().getName());
			//TEST GETTING DVK IF THE SQL ID IS INVALID
			returned = dvk_handler.get_dvk(15);
			assertEquals(null, returned.get_title());
			//MAKE SURE FUNCTION DOESN'T BREAK ON EXCEPTION
			dvk_handler.delete_database();
			returned = dvk_handler.get_dvk(1);
			assertEquals(null, returned.get_title());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_dvks function when sorting by title.
	 */
	@Test
	public void test_sort_title() {
		//READ ALL THE DEFAULT TEST DVK FILES
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			//TEST DVKS ARE SORTED BY TITLE
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("2017/10/06|12:00", dvks.get(0).get_time());
			assertEquals("TITLE 0.55", dvks.get(1).get_title());
			assertEquals("2018/05/20|14:15", dvks.get(1).get_time());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("Title 10", dvks.get(3).get_title());
			//TEST DVKS SORTED BY TITLE IN REVERSE
			dvks = dvk_handler.get_dvks('a', false, true);
			assertEquals("Title 10", dvks.get(0).get_title());
			assertEquals("Title 2", dvks.get(1).get_title());
			assertEquals("TITLE 0.55", dvks.get(2).get_title());
			assertEquals("2018/05/20|14:15", dvks.get(2).get_time());
			assertEquals("title 0.55", dvks.get(3).get_title());
			assertEquals("2017/10/06|12:00", dvks.get(3).get_time());
			//TEST SORTING BY TITLE WHEN ARTISTS ARE GROUPED
			dvks = dvk_handler.get_dvks('a', true, false);
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("Artist 1", dvks.get(0).get_artists()[0]);
			assertEquals("Title 10", dvks.get(1).get_title());
			assertEquals("Artist 1", dvks.get(1).get_artists()[0]);
			assertEquals("TITLE 0.55", dvks.get(2).get_title());
			assertEquals("Artist 2", dvks.get(2).get_artists()[0]);
			assertEquals("Title 2", dvks.get(3).get_title());
			assertEquals("Test", dvks.get(3).get_artists()[0]);
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_dvks function when sorting by time published.
	 */
	@Test
	public void test_sort_time() {
		//READ ALL THE DEFAULT TEST DVK FILES
		FilePrefs file_prefs = new FilePrefs();
		file_prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(file_prefs)) {
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			//TEST DVKS ARE SORTED BY TIME
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('t', false, false);
			assertEquals(4, dvks.size());
			assertEquals("2017/10/06|12:00", dvks.get(0).get_time());
			assertEquals("title 0.55", dvks.get(0).get_title());
			assertEquals("2017/10/06|12:00", dvks.get(1).get_time());
			assertEquals("Title 2", dvks.get(1).get_title());
			assertEquals("2018/05/20|14:15", dvks.get(2).get_time());
			assertEquals("2020/09/04|17:13", dvks.get(3).get_time());
			//TEST DVKS SORTED BY TIME IN REVERSE
			dvks = dvk_handler.get_dvks('t', false, true);
			assertEquals(4, dvks.size());
			assertEquals("2020/09/04|17:13", dvks.get(0).get_time());
			assertEquals("2018/05/20|14:15", dvks.get(1).get_time());
			assertEquals("2017/10/06|12:00", dvks.get(2).get_time());
			assertEquals("Title 2", dvks.get(2).get_title());
			assertEquals("2017/10/06|12:00", dvks.get(3).get_time());
			assertEquals("title 0.55", dvks.get(3).get_title());
			//TEST SORTING BY TIME WHEN ARTISTS ARE GROUPED
			dvks = dvk_handler.get_dvks('t', true, false);
			assertEquals(4, dvks.size());
			assertEquals("2017/10/06|12:00", dvks.get(0).get_time());
			assertEquals("Artist 1", dvks.get(0).get_artists()[0]);
			assertEquals("2020/09/04|17:13", dvks.get(1).get_time());
			assertEquals("Artist 1", dvks.get(1).get_artists()[0]);
			assertEquals("2018/05/20|14:15", dvks.get(2).get_time());
			assertEquals("Artist 2", dvks.get(2).get_artists()[0]);
			assertEquals("2017/10/06|12:00", dvks.get(3).get_time());
			assertEquals("Test", dvks.get(3).get_artists()[0]);
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the delete_database function.
	 */
	@Test
	public void test_delete_database() {
		//SET UP DVK HANDLER
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler handler = new DvkHandler(prefs)) {
			//CHECK DVK ARCHIVE DATABASE FILE EXISTS
			File file = new File(this.temp_dir.getRoot(), "dvk_archive.db");
			assertTrue(file.exists());
			//TEST DELETING THE DATABASE FILE
			assertTrue(handler.delete_database());
			assertFalse(file.exists());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
}
