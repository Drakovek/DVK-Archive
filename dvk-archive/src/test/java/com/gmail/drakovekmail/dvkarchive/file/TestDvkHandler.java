package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
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
	 * Main DvkHander object for testing purposes.
	 */
	private DvkHandler dvk_handler;
	
	/**
	 * Sub-directory for holding test files.
	 */
	private File f1;
	
	/**
	 * Sub-directory for holding test files.
	 */
	private File f2;
	
	/**
	 * Sub-directory for holding test files.
	 */
	private File sub;
	
	/**
	 * Creates the test directory for holding test files.
	 */
	@Before
	public void create_test_files() {
		//CREATE DIRECTORIES
		File index_dir = null;
		File f3 = null;
		File f4 = null;
		try {
			index_dir = this.temp_dir.newFolder("indexing");
			this.f1 = this.temp_dir.newFolder("f1");
			this.f2 = this.temp_dir.newFolder("f2");
			f3 = this.temp_dir.newFolder("f3");
			f4 = new File(this.f2, "f4");
		}
		catch(IOException e) {
			f4 = new File("");
		}
		if(!f4.isDirectory()) {
			f4.mkdir();
		}
		this.sub = new File(this.f1, "sub");
		if(!this.sub.isDirectory()) {
			this.sub.mkdir();
		}
		//CREATE INDEX DIRECTORY
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(index_dir);
		try {
			this.dvk_handler = new DvkHandler(prefs, null, null);
		}
		catch(DvkException e) {}
		//CREATE DVK0
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.f1, "dvk0.dvk"));
		dvk.set_dvk_id("DVK0");
		dvk.set_title("Page 1");
		dvk.set_time_int(2020, 1, 29, 10, 39);
		dvk.set_artist("Artist1");
		dvk.set_page_url("/page0");
		dvk.set_media_file("dvk0.png");
		dvk.write_dvk();
		//CREATE DVK1
		dvk.set_dvk_file(new File(this.sub, "dvk1.dvk"));
		dvk.set_dvk_id("DVK1");
		dvk.set_title("page 1.05");
		dvk.set_time_int(2020, 1, 29, 10, 20);
		dvk.set_artist("Artist2");
		dvk.set_page_url("/page1");
		dvk.set_media_file("dvk1.png");
		dvk.write_dvk();
		//CREATE DVK2
		dvk.set_dvk_file(new File(this.f2, "dvk2.dvk"));
		dvk.set_dvk_id("DVK2");
		dvk.set_title("Page 1.5");
		dvk.set_time_int(2020, 1, 29, 8, 20);
		dvk.set_artist("Artist1");
		dvk.set_page_url("/page2");
		dvk.set_media_file("dvk2.png");
		dvk.write_dvk();
		//CREATE DVK3
		dvk.set_dvk_file(new File(f3, "dvk3.dvk"));
		dvk.set_dvk_id("DVK3");
		dvk.set_title("Page 10");
		dvk.set_time_int(2018, 1, 12, 8, 20);
		String[] artists = {"Artist2", "Artist3"};
		dvk.set_artists(artists);
		String[] tags = {"Tag1", "tag2!", "other,tag"};
		dvk.set_web_tags(tags);
		dvk.set_description("<p>Description thing!");
		dvk.set_page_url("site/page3");
		dvk.set_direct_url("direct/page.txt");
		dvk.set_secondary_url("second/page.png");
		dvk.set_media_file("dvk3.txt");
		dvk.set_secondary_file("dvk3.png");
		dvk.write_dvk();
		//CREATE DVK4
		dvk.set_dvk_file(new File(f3, "dvk4.dvk"));
		dvk.set_dvk_id("DVK4");
		dvk.set_title("Something");
		dvk.set_time_int(2018, 1, 12, 8, 20);
		dvk.set_artist("Artist1");
		dvk.set_page_url("site/page4");
		dvk.set_media_file("dvk4.txt");
		dvk.write_dvk();
	}
	
	/**
	 * Tests the initialize_connect method.
	 */
	@Test
	public void test_initialize_connect() {
		//CREATE INDEX DIRECTORY
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		File db = new File(this.temp_dir.getRoot(), "dvk_archive.db");
		assertFalse(db.exists());
		try (DvkHandler handler = new DvkHandler(prefs, null, null)) {
			handler.initialize_connection();
			assertTrue(db.exists());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the read_dvks method.
	 */
	@Test
	public void test_read_dvks() {
		//TEST INVALID DIRECTORIES
		this.dvk_handler.read_dvks(null);
		assertEquals(0, this.dvk_handler.get_size());
		assertEquals(0, this.dvk_handler.get_size());
		//LOAD FROM MAIN TEST DIRECTORY
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		assertEquals(5, this.dvk_handler.get_size());
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(5, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		assertTrue(dvks.get(0).get_web_tags() == null);
		assertEquals(null, dvks.get(0).get_secondary_file());
		assertEquals("page 1.05", dvks.get(1).get_title());
		assertEquals("Page 1.5", dvks.get(2).get_title());
		assertEquals("Something", dvks.get(4).get_title());
		//TEST ALL DVK PARAMETERS
		Dvk dvk = dvks.get(3);
		File f3 = new File(this.temp_dir.getRoot(), "f3");
		assertTrue(dvk.get_sql_id() > 0);
		assertEquals(f3, dvk.get_dvk_file().getParentFile());
		assertEquals("dvk3.dvk", dvk.get_dvk_file().getName());
		assertEquals("DVK3", dvk.get_dvk_id());
		assertEquals("Page 10", dvk.get_title());
		assertEquals(2, dvk.get_artists().length);
		assertEquals("Artist2", dvk.get_artists()[0]);
		assertEquals("Artist3", dvk.get_artists()[1]);
		assertEquals("2018/01/12|08:20", dvk.get_time());
		assertEquals(3, dvk.get_web_tags().length);
		assertEquals("Tag1", dvk.get_web_tags()[0]);
		assertEquals("tag2!", dvk.get_web_tags()[1]);
		assertEquals("other,tag", dvk.get_web_tags()[2]);
		assertEquals("<p>Description thing!", dvk.get_description());
		assertEquals("site/page3", dvk.get_page_url());
		assertEquals("direct/page.txt", dvk.get_direct_url());
		assertEquals("second/page.png", dvk.get_secondary_url());
		assertEquals(f3, dvk.get_media_file().getParentFile());
		assertEquals("dvk3.txt", dvk.get_media_file().getName());
		assertEquals(f3, dvk.get_secondary_file().getParentFile());
		assertEquals("dvk3.png", dvk.get_secondary_file().getName());
		try {
			TimeUnit.MILLISECONDS.sleep(1500);
		} catch (InterruptedException e) {}
		//CREATE NEW DVK
		dvk = new Dvk();
		dvk.set_dvk_id("NEW123");
		dvk.set_title("New Dvk");
		dvk.set_artist("NewArtist");
		dvk.set_page_url("/new_page/");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "new_dvk.dvk"));
		dvk.set_media_file("new_dvk.png");
		dvk.write_dvk();
		//MODIFY DVK
		dvk.set_dvk_id("MOD123");
		dvk.set_title("Mod DVK");
		dvk.set_dvk_file(new File(f3, "dvk3.dvk"));
		dvk.set_media_file("mod.png");
		dvk.write_dvk();
		//TEST UPDATING DVK INFO
		this.dvk_handler.read_dvks(dirs);
		assertEquals(6, this.dvk_handler.get_size());
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(6, dvks.size());
		assertEquals("Mod DVK", dvks.get(0).get_title());
		assertEquals("New Dvk", dvks.get(1).get_title());
		assertEquals("Page 1", dvks.get(2).get_title());
		assertEquals("page 1.05", dvks.get(3).get_title());
		assertEquals("Page 1.5", dvks.get(4).get_title());
		assertEquals("Something", dvks.get(5).get_title());
		//LOAD FROM MULTIPLE DIRECTORIES
		dirs = new File[2];
		dirs[0] = this.f1;
		dirs[1] = this.f2;
		this.dvk_handler.read_dvks(dirs);
		assertEquals(3, this.dvk_handler.get_size());
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(3, dvks.size());
		//TEST DELETING DVKS
		dirs = new File[1];
		dirs[0] = this.temp_dir.getRoot();
		this.dvk_handler.read_dvks(dirs);
		assertEquals(6, this.dvk_handler.get_size());
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		dvks.get(0).get_dvk_file().delete();
		assertFalse(dvks.get(0).get_dvk_file().exists());
		this.dvk_handler.read_dvks(dirs);
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(5, dvks.size());
		assertEquals("New Dvk", dvks.get(0).get_title());
		assertEquals("Page 1", dvks.get(1).get_title());
		assertEquals("page 1.05", dvks.get(2).get_title());
		assertEquals("Page 1.5", dvks.get(3).get_title());
		assertEquals("Something", dvks.get(4).get_title());
		//TEST DELETING FOLDER
		try {
			FileUtils.deleteDirectory(f3);
		} catch (IOException e) {
			assertTrue(false);
		}
		assertFalse(f3.exists());
		this.dvk_handler.read_dvks(dirs);
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(4, dvks.size());
		assertEquals("New Dvk", dvks.get(0).get_title());
		assertEquals("Page 1", dvks.get(1).get_title());
		assertEquals("page 1.05", dvks.get(2).get_title());
		assertEquals("Page 1.5", dvks.get(3).get_title());
	}
	
	/**
	 * Tests the remove_duplicates method.
	 */
	@Test
	public void test_remove_duplicates() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		assertEquals(5, this.dvk_handler.get_size());
		//ADD DVKS
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(DvkHandler.DVKS);
		sql.append(" (");
		sql.append(DvkHandler.DVK_ID);
		sql.append(',');
		sql.append(DvkHandler.TITLE);
		sql.append(',');
		sql.append(DvkHandler.ARTISTS);
		sql.append(',');
		sql.append(DvkHandler.PAGE_URL);
		sql.append(',');
		sql.append(DvkHandler.DIRECTORY);
		sql.append(',');
		sql.append(DvkHandler.DVK_FILE);
		sql.append(") VALUES (?,?,?,?,?,?);");
		try(PreparedStatement psmt = this.dvk_handler.get_sql_connection().prepareStatement(sql.toString())) {
			psmt.setString(1, "ID256");
			psmt.setString(2, "DifferentDir");
			psmt.setString(3, "ARTIST");
			psmt.setString(4, "/page");
			psmt.setString(5, this.temp_dir.getRoot().getAbsolutePath());
			psmt.setString(6, "dvk0.dvk");
			psmt.executeUpdate();
			psmt.executeUpdate();
			psmt.setString(5, this.sub.getAbsolutePath());
			psmt.setString(6, "dvk1.dvk");
			psmt.executeUpdate();
		}
		catch(SQLException e) {
			assertTrue(false);
		}
		assertEquals(8, this.dvk_handler.get_size());
		//TEST REMOVING DUPLICATES
		this.dvk_handler.remove_duplicates();
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(4, this.dvk_handler.get_size());
		assertEquals(4, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		assertEquals("Page 1.5", dvks.get(1).get_title());
		assertEquals("Page 10", dvks.get(2).get_title());
		assertEquals("Something", dvks.get(3).get_title());
	}
	
	/**
	 * Tests the get_dvks method while limiting results.
	 */
	@Test
	public void test_get_dvks_limit() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		//TEST NO LIMIT
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(5, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		assertEquals("page 1.05", dvks.get(1).get_title());
		assertEquals("Page 1.5", dvks.get(2).get_title());
		assertEquals("Page 10", dvks.get(3).get_title());
		assertEquals("Something", dvks.get(4).get_title());
		//LIMIT TO 3
		dvks = this.dvk_handler.get_dvks(0, 3, 'a', false, false);
		assertEquals(3, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		assertEquals("page 1.05", dvks.get(1).get_title());
		assertEquals("Page 1.5", dvks.get(2).get_title());
		//OFFSET
		dvks = this.dvk_handler.get_dvks(3, 10, 'a', false, false);
		assertEquals(2, dvks.size());
		assertEquals("Page 10", dvks.get(0).get_title());
		assertEquals("Something", dvks.get(1).get_title());
	}

	/**
	 * Tests the add_dvk method.
	 */
	@Test
	public void test_add_dvk() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		assertEquals(5, this.dvk_handler.get_size());
		//ADD DVK
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("ADD1234");
		dvk.set_title("New Title");
		String[] artists = {"ArtGuy", "Other"};
		dvk.set_artists(artists);
		dvk.set_time("2020/05/08|14:05");
		String[] tags = {"Tag", "Other", "thing"};
		dvk.set_web_tags(tags);
		dvk.set_description("This is text.");
		dvk.set_page_url("/page/");
		dvk.set_direct_url("/page/text.txt");
		dvk.set_secondary_url("/page/text.jpg");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "new.dvk"));
		dvk.set_media_file("new.txt");
		dvk.set_secondary_file("new.jpg");
		this.dvk_handler.add_dvk(dvk);
		//TEST ADDED CORRECTLY
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.TITLE);
		sql.append(" = ?");
		String[] params = {"New Title"};
		try(ResultSet rs = this.dvk_handler.sql_select(sql.toString(), params, true)) {
			rs.next();
			assertEquals("ADD1234", rs.getString(DvkHandler.DVK_ID));
			assertEquals("New Title", rs.getString(DvkHandler.TITLE));
			assertEquals("ArtGuy,Other", rs.getString(DvkHandler.ARTISTS));
			assertEquals("2020/05/08|14:05", rs.getString(DvkHandler.TIME));
			assertEquals("Tag,Other,thing", rs.getString(DvkHandler.WEB_TAGS));
			assertEquals("This is text.", rs.getString(DvkHandler.DESCRIPTION));
			assertEquals("/page/", rs.getString(DvkHandler.PAGE_URL));
			assertEquals("/page/text.txt", rs.getString(DvkHandler.DIRECT_URL));
			assertEquals("/page/text.jpg", rs.getString(DvkHandler.SECONDARY_URL));
			assertEquals(this.temp_dir.getRoot().getAbsolutePath(), rs.getString(DvkHandler.DIRECTORY));
			assertEquals("new.dvk", rs.getString(DvkHandler.DVK_FILE));
			assertEquals("new.txt", rs.getString(DvkHandler.MEDIA_FILE));
			assertEquals("new.jpg", rs.getString(DvkHandler.SECONDARY_FILE));
		}
		catch(SQLException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the delete_dvk method.
	 */
	@Test
	public void test_delete_dvk() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		assertEquals(5, this.dvk_handler.get_size());
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(5, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		this.dvk_handler.delete_dvk(dvks.get(0).get_sql_id());
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(4, dvks.size());
		assertEquals("page 1.05", dvks.get(0).get_title());
	}
	
	/**
	 * Tests the sql_select method.
	 */
	@Test
	public void test_sql_select() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(DvkHandler.TITLE);
		sql.append(" FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.TITLE);
		sql.append(" = ?");
		String[] params = {"Page 1"};
		try (ResultSet rs = this.dvk_handler.sql_select(sql.toString(), params, true)) {
			while(rs.next()) {
				assertEquals("Page 1", rs.getString(DvkHandler.TITLE));
			}
		}
		catch(SQLException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the set_dvk method.
	 */
	@Test
	public void test_set_dvk() {
		//INITIALIZE NEW DVK
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("ADD1234");
		dvk.set_title("New Title");
		String[] artists = {"ArtGuy", "Other"};
		dvk.set_artists(artists);
		dvk.set_time("2020/05/08|14:05");
		String[] tags = {"Tag", "Other", "thing"};
		dvk.set_web_tags(tags);
		dvk.set_description("This is text.");
		dvk.set_page_url("/page/");
		dvk.set_direct_url("/page/text.txt");
		dvk.set_secondary_url("/page/text.jpg");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "new.dvk"));
		dvk.set_media_file("new.txt");
		dvk.set_secondary_file("new.jpg");
		//SET DVK
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(DvkHandler.SQL_ID);
		sql.append(" FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.TITLE);
		sql.append(" = ?");
		String[] params = {"Page 1"};
		int id = -1;
		try(ResultSet rs = this.dvk_handler.sql_select(sql.toString(), params, true)){
			rs.next();
			id = rs.getInt(DvkHandler.SQL_ID);
			this.dvk_handler.set_dvk(dvk, id);
		}
		catch(SQLException e) {
			assertTrue(false);
		}
		//CHECK OLD DVK NO LONGER EXISTS
		try(ResultSet rs = this.dvk_handler.sql_select(sql.toString(), params, false)){
			assertFalse(rs.next());
		}
		catch(SQLException e) {
			assertTrue(false);
		}
		//TEST SET CORRECTLY
		sql = new StringBuilder("SELECT * FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.TITLE);
		sql.append(" = ?");
		params[0] = "New Title";
		try(ResultSet rs = this.dvk_handler.sql_select(sql.toString(), params, true)) {
			rs.next();
			assertEquals(id, rs.getInt(DvkHandler.SQL_ID));
			assertEquals("ADD1234", rs.getString(DvkHandler.DVK_ID));
			assertEquals("New Title", rs.getString(DvkHandler.TITLE));
			assertEquals("ArtGuy,Other", rs.getString(DvkHandler.ARTISTS));
			assertEquals("2020/05/08|14:05", rs.getString(DvkHandler.TIME));
			assertEquals("Tag,Other,thing", rs.getString(DvkHandler.WEB_TAGS));
			assertEquals("This is text.", rs.getString(DvkHandler.DESCRIPTION));
			assertEquals("/page/", rs.getString(DvkHandler.PAGE_URL));
			assertEquals("/page/text.txt", rs.getString(DvkHandler.DIRECT_URL));
			assertEquals("/page/text.jpg", rs.getString(DvkHandler.SECONDARY_URL));
			assertEquals(this.temp_dir.getRoot().getAbsolutePath(), rs.getString(DvkHandler.DIRECTORY));
			assertEquals("new.dvk", rs.getString(DvkHandler.DVK_FILE));
			assertEquals("new.txt", rs.getString(DvkHandler.MEDIA_FILE));
			assertEquals("new.jpg", rs.getString(DvkHandler.SECONDARY_FILE));
		}
		catch(SQLException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_size method.
	 */
	@Test
	public void test_get_size() {
		assertEquals(0, this.dvk_handler.get_size());
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		assertEquals(5, this.dvk_handler.get_size());
		//LOAD FROM MULTIPLE DIRECTORIES
		dirs = new File[2];
		dirs[0] = this.f1;
		dirs[1] = this.f2;
		this.dvk_handler.read_dvks(dirs);
		assertEquals(3, this.dvk_handler.get_size());
	}
	
	/**
	 * Tests the get_directories methods.
	 */
	@Test
	public void test_get_directories() {
		//TEST INVALID DIRECTORY
		File dir = null;
		File[] dirs = null;
		dirs = DvkHandler.get_directories(dirs, true);
		assertEquals(0, dirs.length);
		dirs = DvkHandler.get_directories(dir, true);
		assertEquals(0, dirs.length);
		//TEST NON-EXISTANT DIRECTORIES
		dirs = new File[2];
		dirs[0] = null;
		dirs[1] = new File("ljalkdmwner");
		dirs = DvkHandler.get_directories(dirs, true);
		assertEquals(0, dirs.length);
		dir = new File("kljasdsf");
		dirs = DvkHandler.get_directories(dir, true);
		assertEquals(0, dirs.length);
		//TEST SINGLE DIRECTORY
		dirs = DvkHandler.get_directories(this.temp_dir.getRoot(), true);
		assertEquals(4, dirs.length);
		assertEquals("f1", dirs[0].getName());
		assertEquals("sub", dirs[1].getName());
		assertEquals("f2", dirs[2].getName());
		assertEquals("f3", dirs[3].getName());
		//TEST GET SINGLE DIRECTORY, INCLUDING DIRECTORIES WITHOUT DVK FILES
		File new_dir = new File(this.temp_dir.getRoot(), "new");
		new_dir.mkdir();
		assertTrue(new_dir.exists());
		dirs = DvkHandler.get_directories(this.temp_dir.getRoot(), false);
		assertEquals(8, dirs.length);
		assertEquals(this.temp_dir.getRoot().getName(), dirs[0].getName());
		assertEquals("f1", dirs[1].getName());
		assertEquals("sub", dirs[2].getName());
		assertEquals("f2", dirs[3].getName());
		assertEquals("f4", dirs[4].getName());
		assertEquals("f3", dirs[5].getName());
		assertEquals("indexing", dirs[6].getName());
		assertEquals("new", dirs[7].getName());
		//TEST PARTIALLY VALID DIRECTORIES
		dirs = new File[2];
		dirs[0] = null;
		dirs[1] = this.temp_dir.getRoot();
		dirs = DvkHandler.get_directories(dirs, true);
		assertEquals(4, dirs.length);
		//TEST MULTIPLE DIRECTORIES
		dirs = new File[2];
		dirs[0] = this.f1;
		dirs[1] = this.f2;
		dirs = DvkHandler.get_directories(dirs, true);
		assertEquals(3, dirs.length);
		assertEquals("f1", dirs[0].getName());
		assertEquals("sub", dirs[1].getName());
		assertEquals("f2", dirs[2].getName());
		//TEST OVERLAPPING DIRECTORIES
		dirs = new File[2];
		dirs[0] = this.f1;
		dirs[1] = this.sub;
		dirs = DvkHandler.get_directories(dirs, true);
		assertEquals(2, dirs.length);
		assertEquals("f1", dirs[0].getName());
		assertEquals("sub", dirs[1].getName());
	}
	
	/**
	 * Tests get_dvks method while sorting by title.
	 */
	@Test
	public void test_sort_title() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		//TEST STANDARD TITLE SORT
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(5, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		assertEquals("page 1.05", dvks.get(1).get_title());
		assertEquals("Page 1.5", dvks.get(2).get_title());
		assertEquals("Page 10", dvks.get(3).get_title());
		assertEquals("Something", dvks.get(4).get_title());
		//TEST GROUPED ARTISTS
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', true, false);
		assertEquals(5, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		assertEquals("Page 1.5", dvks.get(1).get_title());
		assertEquals("Something", dvks.get(2).get_title());
		assertEquals("page 1.05", dvks.get(3).get_title());
		assertEquals("Page 10", dvks.get(4).get_title());
		//TEST INVERTED
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, true);
		assertEquals(5, dvks.size());
		assertEquals("Something", dvks.get(0).get_title());
		assertEquals("Page 10", dvks.get(1).get_title());
		assertEquals("Page 1.5", dvks.get(2).get_title());
		assertEquals("page 1.05", dvks.get(3).get_title());
		assertEquals("Page 1", dvks.get(4).get_title());
	}
	
	/**
	 * Tests the get_dvk method.
	 */
	@Test
	public void test_get_dvk() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(DvkHandler.SQL_ID);
		sql.append(" FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" ORDER BY ");
		sql.append(DvkHandler.TITLE);
		sql.append(" COLLATE NOCASE ASC;");
		try(ResultSet rs = this.dvk_handler.sql_select(sql.toString(), new String[0], true)) {
			rs.next();
			Dvk dvk = this.dvk_handler.get_dvk(rs.getInt(DvkHandler.SQL_ID));
			assertEquals("Page 1", dvk.get_title());
			rs.next();
			dvk = this.dvk_handler.get_dvk(rs.getInt(DvkHandler.SQL_ID));
			assertEquals("page 1.05", dvk.get_title());
			rs.next();
			dvk = this.dvk_handler.get_dvk(rs.getInt(DvkHandler.SQL_ID));
			assertEquals("Page 1.5", dvk.get_title());
			rs.next();
			dvk = this.dvk_handler.get_dvk(rs.getInt(DvkHandler.SQL_ID));
			assertEquals("Page 10", dvk.get_title());
			rs.next();
			dvk = this.dvk_handler.get_dvk(rs.getInt(DvkHandler.SQL_ID));
			assertEquals("Something", dvk.get_title());
		}
		catch(SQLException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Tests get_dvks method while sorting by time published.
	 */
	@Test
	public void test_sort_time() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		//TEST STANDARD TIME SORT
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 't', false, false);
		assertEquals(5, dvks.size());
		assertEquals("Page 10", dvks.get(0).get_title());
		assertEquals("Something", dvks.get(1).get_title());
		assertEquals("Page 1.5", dvks.get(2).get_title());
		assertEquals("page 1.05", dvks.get(3).get_title());
		assertEquals("Page 1", dvks.get(4).get_title());
		//TEST GROUPED ARTISTS
		dvks = this.dvk_handler.get_dvks(0, -1, 't', true, false);
		assertEquals(5, dvks.size());
		assertEquals("Something", dvks.get(0).get_title());
		assertEquals("Page 1.5", dvks.get(1).get_title());
		assertEquals("Page 1", dvks.get(2).get_title());
		assertEquals("page 1.05", dvks.get(3).get_title());
		assertEquals("Page 10", dvks.get(4).get_title());
		//TEST INVERTED
		dvks = this.dvk_handler.get_dvks(0, -1, 't', false, true);
		assertEquals(5, dvks.size());
		assertEquals("Page 1", dvks.get(0).get_title());
		assertEquals("page 1.05", dvks.get(1).get_title());
		assertEquals("Page 1.5", dvks.get(2).get_title());
		assertEquals("Something", dvks.get(3).get_title());
		assertEquals("Page 10", dvks.get(4).get_title());
	}
	
	/**
	 * Tests the contains_file method.
	 */
	@Test
	public void test_contains_file() {
		File[] dirs = {this.temp_dir.getRoot()};
		this.dvk_handler.read_dvks(dirs);
		assertEquals(5, this.dvk_handler.get_size());
		File file = new File(this.temp_dir.getRoot(), "noFile.txt");
		assertFalse(this.dvk_handler.contains_file(file));
		file = new File(this.temp_dir.getRoot(), "dvk2.png");
		assertFalse(this.dvk_handler.contains_file(file));
		file = new File(this.f2, "dvk2.png");
		assertTrue(this.dvk_handler.contains_file(file));
		File f3 = new File(this.temp_dir.getRoot(), "f3");
		file = new File(f3, "dvk3.txt");
		assertTrue(this.dvk_handler.contains_file(file));
		file = new File(f3, "dvk3.png");
		assertTrue(this.dvk_handler.contains_file(file));
	}
	
	/**
	 * Tests the delete_database method.
	 */
	@Test
	public void test_delete_database() {
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler handler = new DvkHandler(prefs, null, null)) {
			File file = new File(this.temp_dir.getRoot(), "dvk_archive.db");
			assertTrue(file.exists());
			handler.delete_database();
			assertFalse(file.exists());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
}
