package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the ArtistHosting class.
 * 
 * @author Drakovek
 */
public class TestArtistHosting {
	
	/**
	 * Main directory for storing test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Tests the get_artists method.
	 */
	//TODO REINSTATE
	/*
	@Test
	public void test_get_artists() {
		//CREATE SUB-DIRECTORIES
		File sub1 = new File(this.temp_dir.getRoot(), "sub1");
		if(!sub1.isDirectory()) {
			sub1.mkdir();
		}
		File sub2 = new File(this.temp_dir.getRoot(), "sub2");
		if(!sub2.isDirectory()) {
			sub2.mkdir();
		}
		//CREATE DVKS - ARTIST 1
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("ID123");
		dvk.set_title("dvk1");
		dvk.set_artist("artist1");
		String[] tags = {"Tag1", "Thing", "DVK:Single", "Last"};
		dvk.set_web_tags(tags);
		dvk.set_page_url("www.Website.com/view/1");
		dvk.set_dvk_file(new File(sub1, "dvk1.dvk"));
		dvk.set_media_file("dvk1.png");
		dvk.write_dvk();
		dvk.set_title("dvk2");
		dvk.set_dvk_file(new File(sub2, "dvk2.dvk"));
		dvk.set_web_tags(new String[0]);
		dvk.set_media_file("dvk2.jpg");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 2
		dvk.set_title("dvk3");
		dvk.set_artist("artist2");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk3.dvk"));
		dvk.set_media_file("dvk3.png");
		dvk.write_dvk();
		dvk.set_title("dvk4");
		dvk.set_dvk_file(new File(sub1, "dvk4.dvk"));
		dvk.set_media_file("dvk4.txt");
		dvk.write_dvk();
		dvk.set_title("dvk5");
		dvk.set_dvk_file(new File(sub2, "dvk5.dvk"));
		dvk.set_media_file("dvk5.jpg");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 3
		dvk.set_title("dvk6");
		dvk.set_artist("artist3");
		String[] different_tags = {"fine"};
		dvk.set_web_tags(different_tags);
		dvk.set_dvk_file(new File(sub2, "dvk6.dvk"));
		dvk.set_media_file("dvk6.txt");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 4
		dvk.set_title("dvk7");
		dvk.set_artist("artist4");
		dvk.set_page_url("diferent/page");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk7.dvk"));
		dvk.set_media_file("dvk7.pdf");
		dvk.write_dvk();
		//CREATE DVKS - ARTIST 5 - SINGLE
		dvk.set_title("dvk8");
		dvk.set_artist("artist5");
		dvk.set_page_url("www.website.com/view/2");
		dvk.set_web_tags(tags);
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk8.dvk"));
		dvk.set_media_file("dvk8.txt");
		dvk.write_dvk();
		//CHECK GET ARTISTS
		ArrayList<Dvk> dvks;
		File[] dirs = {this.temp_dir.getRoot()};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler handler = new DvkHandler(prefs, dirs, null)) {
			assertEquals(8, handler.get_size());
			dvks = ArtistHosting.get_artists(handler, "website.com");
			assertEquals(3, dvks.size());
			assertEquals("artist1", dvks.get(0).get_artists()[0]);
			assertEquals(sub2, dvks.get(0).get_dvk_file());
			assertEquals("artist2", dvks.get(1).get_artists()[0]);
			assertEquals(this.temp_dir.getRoot(), dvks.get(1).get_dvk_file());
			assertEquals("artist3", dvks.get(2).get_artists()[0]);
			assertEquals(sub2, dvks.get(2).get_dvk_file());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	*/
	
	/**
	 * Tests the get_common_directory method.
	 */
	//TODO REINSTATE
	/*
	@Test
	public void test_get_common_directory() {
		//CREATE SUB-DIRECTORIES
		File sub1 = new File(this.temp_dir.getRoot(), "sub1");
		if(!sub1.isDirectory()) {
			sub1.mkdir();
		}
		File sub2 = new File(this.temp_dir.getRoot(), "sub2");
		if(!sub2.isDirectory()) {
			sub2.mkdir();
		}
		File supsub = new File(sub1, "supsub");
		if(!supsub.isDirectory()) {
			supsub.mkdir();
		}
		//CHECK INVALID
		File file = ArtistHosting.get_common_directory(
				sub2, new File("bleh"));
		assertEquals(sub2, file);
		//CHECK COMMON FILES
		file = ArtistHosting.get_common_directory(sub1, sub1);
		assertEquals(sub1, file);
		file = ArtistHosting.get_common_directory(sub2, sub1);
		assertEquals(this.temp_dir.getRoot(), file);
		file = ArtistHosting.get_common_directory(sub1, supsub);
		assertEquals(sub1, file);
		file = ArtistHosting.get_common_directory(sub2, supsub);
		assertEquals(this.temp_dir.getRoot(), file);
	}
	*/
	
	/**
	 * Tests the move_dvk method.
	 */
	//TODO REINSTATE
	/*
	@Test
	public void test_move_dvk() {
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("id123");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("/page/");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk.dvk"));
		dvk.set_media_file("dvk.txt");
		dvk.set_secondary_file("dvk.png");
		String[] tags = {"bleh", "DVK:Single"};
		dvk.set_web_tags(tags);
		try {
			dvk.get_media_file().createNewFile();
			dvk.get_secondary_file().createNewFile();
		} catch (IOException e) {}
		dvk.write_dvk();
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		assertTrue(dvk.get_secondary_file().exists());
		//CHANGE NOTHING
		dvk = ArtistHosting.move_dvk(dvk, null);
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		assertTrue(dvk.get_secondary_file().exists());
		assertEquals(this.temp_dir.getRoot(), dvk.get_dvk_file().getParentFile());
		//CHANGE DIRECTORY
		File sub = new File(this.temp_dir.getRoot(), "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		assertTrue(dvk.get_media_file().exists());
		dvk = ArtistHosting.move_dvk(dvk, sub);
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		assertTrue(dvk.get_secondary_file().exists());
		assertEquals(sub, dvk.get_dvk_file().getParentFile());
		assertEquals(sub, dvk.get_media_file().getParentFile());
		assertEquals(sub, dvk.get_secondary_file().getParentFile());
		assertEquals("dvk.dvk", dvk.get_dvk_file().getName());
		assertEquals("dvk.txt", dvk.get_media_file().getName());
		assertEquals("dvk.png", dvk.get_secondary_file().getName());
		//DON'T MOVE, DVK NOT SINGLE
		dvk.set_secondary_file("");
		dvk.set_web_tags(null);
		dvk = ArtistHosting.move_dvk(dvk, this.temp_dir.getRoot());
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		assertEquals(null, dvk.get_secondary_file());
		assertEquals(sub, dvk.get_dvk_file().getParentFile());
		assertEquals(sub, dvk.get_media_file().getParentFile());
		assertEquals("dvk.dvk", dvk.get_dvk_file().getName());
		assertEquals("dvk.txt", dvk.get_media_file().getName());
	}
	*/
	
	/**
	 * Tests the update favorite method.
	 */
	//TODO REINSTATE
	/*
	@Test
	public void test_update_favorite() {
		//CREATE DVK
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("ID123");
		dvk.set_title("Title!");
		dvk.set_artist("ArtGuy");
		dvk.set_page_url("/page/");
		String[] tags = {"Something", "blah", "favorite:person"};
		dvk.set_web_tags(tags);
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk.dvk"));
		dvk.set_media_file("dvk.jpg");
		dvk.write_dvk();
		//READ DVKS
		File[] dirs = {this.temp_dir.getRoot()};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		try(DvkHandler handler = new DvkHandler(prefs, dirs, null)) {
			//TEST UPDATITNG FAVORITES
			assertEquals(1, handler.get_size());
			assertEquals(null, ArtistHosting.update_favorite(handler, "blah", "ID256"));
			Dvk result = ArtistHosting.update_favorite(handler, "Person", "ID123");
			assertEquals("Title!", result.get_title());
			assertEquals(3, result.get_web_tags().length);
			//TEST WRITEN TO DISK AND SET TO DVK_HANDLER
			assertTrue(result.get_dvk_file().exists());
			result = handler.get_dvk(result.get_sql_id());
			assertEquals("Title!", result.get_title());
			assertEquals(3, result.get_web_tags().length);
			//TEST ADDING ANOTHER FAVORITE ARTIST
			result = ArtistHosting.update_favorite(handler, "Other", "ID123");
			assertEquals("Title!", result.get_title());
			assertEquals(4, result.get_web_tags().length);
			assertEquals("Something", result.get_web_tags()[0]);
			assertEquals("blah", result.get_web_tags()[1]);
			assertEquals("favorite:person", result.get_web_tags()[2]);
			assertEquals("Favorite:Other", result.get_web_tags()[3]);
			//TEST WRITEN TO DISK AND SET TO DVK_HANDLER
			assertTrue(result.get_dvk_file().exists());
			result = handler.get_dvk(result.get_sql_id());
			assertEquals("Title!", result.get_title());
			assertEquals(4, result.get_web_tags().length);
			assertEquals("Something", result.get_web_tags()[0]);
			assertEquals("blah", result.get_web_tags()[1]);
			assertEquals("favorite:person", result.get_web_tags()[2]);
			assertEquals("Favorite:Other", result.get_web_tags()[3]);
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	*/
	
	/**
	 * Tests the get_dvks_from_ids method.
	 */
	//TODO REINSTATE
	/*
	@Test
	public void test_get_dvks_from_ids() {
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("ID1");
		dvk.set_title("Unimportant");
		dvk.set_artist("Artist");
		dvk.set_page_url("/page/");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk1.dvk"));
		dvk.set_media_file("dvk1.png");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk.set_dvk_id("ID123");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk2.dvk"));
		dvk.write_dvk();
		//CREATE DVK3
		dvk.set_dvk_id("NEW35");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk3.dvk"));
		dvk.write_dvk();
		//READ DVKS FROM FILE
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.temp_dir.getRoot());
		File[] dirs = {this.temp_dir.getRoot()};
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
			assertEquals(3, dvk_handler.get_size());
			//TEST NO VALID IDS
			ArrayList<String> ids = new ArrayList<>();
			ids.add("nope");
			ArrayList<Dvk> dvks = ArtistHosting.get_dvks_from_ids(dvk_handler, ids);
			assertEquals(0, dvks.size());
			//TEST GETTING IDS
			ids.add("ID123");
			ids.add("NEW35");
			dvks = ArtistHosting.get_dvks_from_ids(dvk_handler, ids);
			assertEquals(2, dvks.size());
			String id = dvks.get(0).get_dvk_id();
			assertNotEquals("ID1", id);
			assertTrue(id.equals("ID123") || id.equals("NEW35"));
			assertNotEquals("ID1", dvks.get(1).get_dvk_id());
			assertTrue(dvks.get(1).get_dvk_id().equals("ID123") || dvks.get(1).get_dvk_id().equals("NEW35"));
			assertNotEquals(id, dvks.get(1).get_dvk_id());
		
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	*/
}
