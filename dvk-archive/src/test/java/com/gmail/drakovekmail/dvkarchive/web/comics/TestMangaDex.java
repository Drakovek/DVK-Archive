package com.gmail.drakovekmail.dvkarchive.web.comics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;
import com.gmail.drakovekmail.dvkarchive.web.DConnectSelenium;

/**
 * Unit tests for the MangaDex class.
 * 
 * @author Drakovek
 */
public class TestMangaDex {
	
	/**
	 * Directory to hold all test files during testing.
	 */
	private File test_dir;
	
	/**
	 * DConnect object for online connection.
	 */
	private DConnect connect;
	
	/**
	 * Creates test files and initializes connect object.
	 */
	@Before
	public void set_up() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "mangadextest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		this.connect = new DConnect(false, false);
	}
	
	/**
	 * Removes test files and closes connect object.
	 */
	@After
	public void tear_down() {
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
		this.connect.close_client();
		this.connect = null;
	}
	
	/**
	 * Tests the get_title_id method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_title_id() {
		String url = "www.differentsite.com/title/12";
		assertEquals("", MangaDex.get_title_id(url));
		url = "www.mangadex.cc/nope/27153/";
		assertEquals("", MangaDex.get_title_id(url));
		url = "www.mangadex.cc/title/invalid/";
		assertEquals("", MangaDex.get_title_id(url));
		url = "mangadex.com/title/27152";
		assertEquals("27152", MangaDex.get_title_id(url));
		url = "www.mangadex.cc/title/27153/jojo-s-bizarre-adventure";
		assertEquals("27153", MangaDex.get_title_id(url));
	}
	
	/**
	 * Tests the get_chapter_id method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_chapter_id() {
		String url = "www.differentsite.com/chapter/23";
		assertEquals("", MangaDex.get_chapter_id(url));
		url = "www.mangadex.cc/nope/27153/";
		assertEquals("", MangaDex.get_chapter_id(url));
		url = "www.mangadex.cc/chapter/invalid/";
		assertEquals("", MangaDex.get_chapter_id(url));
		url = "mangadex.com/chapter/27152";
		assertEquals("27152", MangaDex.get_chapter_id(url));
		url = "www.mangadex.cc/chapter/27153/kjaksd";
		assertEquals("27153", MangaDex.get_chapter_id(url));
	}
	
	/**
	 * Tests the get_title_info method.
	 */
	@Test
	public void test_get_title_info() {
		//TEST INVALID TITLES
		Dvk dvk;
		dvk = MangaDex.get_title_info(this.connect, "blah");
		assertEquals(null, dvk.get_title());
		assertEquals(null, dvk.get_page_url());
		//TITLE 1
		dvk = MangaDex.get_title_info(this.connect, "34326");
		assertEquals("Randomphilia", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Devin Bosco Le", dvk.get_artists()[0]);
		assertEquals(9, dvk.get_web_tags().length);
		assertEquals("MangaDex:34326", dvk.get_web_tags()[0]);
		assertEquals("Shounen", dvk.get_web_tags()[1]);
		assertEquals("4-Koma", dvk.get_web_tags()[2]);
		assertEquals("Full Color", dvk.get_web_tags()[3]);
		assertEquals("Long Strip", dvk.get_web_tags()[4]);
		assertEquals("Official Colored", dvk.get_web_tags()[5]);
		assertEquals("Web Comic", dvk.get_web_tags()[6]);
		assertEquals("Comedy", dvk.get_web_tags()[7]);
		assertEquals("Slice of Life", dvk.get_web_tags()[8]);
		String value = "English :  <br/>  A world where logic does not exist"
				+ " and anything is possible. The only limit is your "
				+ "imagination. Bite-sized comics with dry humor for the "
				+ "broken souls.  <br/>  <br/>  Un monde o&#249; la logique "
				+ "n&#8217;existe pas et o&#249; tout est possible. La seule "
				+ "limite est votre imagination. Des bandes "
				+ "dessin&#233;es mordues.";
		assertEquals(value, dvk.get_description());
		String url = "https://mangadex.org/title/34326/randomphilia";
		assertEquals(url, dvk.get_page_url());
		//TITLE 2
		dvk = MangaDex.get_title_info(this.connect, "27152");
		value = "JoJo's Bizarre Adventure Part 2 - Battle Tendency "
				+ "(Official Colored)";
		assertEquals(value, dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Araki Hirohiko", dvk.get_artists()[0]);
		assertEquals(14, dvk.get_web_tags().length);
		assertEquals("MangaDex:27152", dvk.get_web_tags()[0]);
		assertEquals("Shounen", dvk.get_web_tags()[1]);
		assertEquals("Full Color", dvk.get_web_tags()[2]);
		assertEquals("Official Colored", dvk.get_web_tags()[3]);
		assertEquals("Action", dvk.get_web_tags()[4]);
		assertEquals("Adventure", dvk.get_web_tags()[5]);
		assertEquals("Comedy", dvk.get_web_tags()[6]);
		assertEquals("Drama", dvk.get_web_tags()[7]);
		assertEquals("Historical", dvk.get_web_tags()[8]);
		assertEquals("Horror", dvk.get_web_tags()[9]);
		assertEquals("Mystery", dvk.get_web_tags()[10]);
		assertEquals("Martial Arts", dvk.get_web_tags()[11]);
		assertEquals("Supernatural", dvk.get_web_tags()[12]);
		assertEquals("Vampires", dvk.get_web_tags()[13]);
		value = "Second story arc of JoJo no Kimyou na Bouken "
				+ "series.  <br/>  <br/>  Takes place in the 1930s"
				+ ", and follows the misadventures of Joseph Joestar,"
				+ " the grandson of Jonathan Joestar, as he fights "
				+ "vampires and ancient super beings with some help "
				+ "from a cybernetically-enhanced Nazi and an Italian "
				+ "man he has a lot in common with.";
		assertEquals(value, dvk.get_description());
		value = "https://mangadex.org/title/27152/jojo-s-bizarre"
				+ "-adventure-part-2-battle-tendency-official-colored";
		assertEquals(value, dvk.get_page_url());
	}
	
	/**
	 * Tests the get_chapters method.
	 */
	@Test
	public void test_get_chapters() {
		//TITLE 1
		Dvk dvk = new Dvk();
		dvk.set_title("JoJo's Bizarre Adventure Part 2");
		String value = "https://mangadex.org/title/27152/jojo-s-bizarre"
				+ "-adventure-part-2-battle-tendency-official-colored";
		dvk.set_page_url(value);
		dvk.set_artist("Araki Hirohiko");
		String[] tags = {"tag", "other"};
		dvk.set_web_tags(tags);
		ArrayList<Dvk> dvks;
		dvks = MangaDex.get_chapters(this.connect, dvk, null, "English", 1);
		assertEquals(69, dvks.size());
		//TITLE 1, CHAPTER 1
		assertEquals(2, dvks.get(0).get_web_tags().length);
		value = "JoJo's Bizarre Adventure Part 2 | "
				+ "Vol. 7 Ch. 69 - The Comeback";
		assertEquals(value, dvks.get(0).get_title());
		assertEquals(2, dvks.get(0).get_artists().length);
		assertEquals("Araki Hirohiko", dvks.get(0).get_artists()[0]);
		value = "JoJo's Colored Adventure";
		assertEquals(value, dvks.get(0).get_artists()[1]);
		value = "https://mangadex.org/chapter/2140";
		assertEquals(value, dvks.get(0).get_page_url());
		assertEquals("2018/01/18|19:08", dvks.get(0).get_time());
		assertEquals("2140", dvks.get(0).get_id());
		//TITLE 1, CHAPTER 2
		value = "JoJo's Bizarre Adventure Part 2 | Vol. 4 Ch. 39 - "
				+ "Chasing the Red Stone to Swizerland";
		assertEquals(value, dvks.get(30).get_title());
		value = "https://mangadex.org/chapter/2081";
		assertEquals(value, dvks.get(30).get_page_url());
		assertEquals("2018/01/18|18:44", dvks.get(30).get_time());
		assertEquals("2081", dvks.get(30).get_id());
		//TITLE 1, CHAPTER 3
		value = "JoJo's Bizarre Adventure Part 2 | Vol. 1 Ch. 1 "
				+ "- Joseph Joestar of New York";
		assertEquals(value, dvks.get(68).get_title());
		value = "https://mangadex.org/chapter/1949";
		assertEquals(value, dvks.get(68).get_page_url());
		assertEquals("2018/01/18|16:44", dvks.get(68).get_time());
		assertEquals("1949", dvks.get(68).get_id());
		//TITLE 1, SEPARATE LANGUAGE
		dvks = MangaDex.get_chapters(this.connect, dvk, null, "Italian", 1);
		assertEquals(26, dvks.size());
		value = "JoJo's Bizarre Adventure Part 2 | "
				+ "Vol. 3 Ch. 26 - La maledizione delle fedi";
		assertEquals(value, dvks.get(0).get_title());
		assertEquals(2, dvks.get(0).get_artists().length);
		assertEquals("Araki Hirohiko", dvks.get(0).get_artists()[0]);
		assertEquals("JoJo No Sense", dvks.get(0).get_artists()[1]);
		assertEquals("2019/07/31|16:16", dvks.get(0).get_time());
		value = "https://mangadex.org/chapter/676740";
		assertEquals(value, dvks.get(0).get_page_url());
		assertEquals("676740", dvks.get(0).get_id());
		//TITLE 1, NON-EXISTANT LANGUAGE
		dvks = MangaDex.get_chapters(this.connect, dvk, null, "JAKJSKDJK", 1);
		assertEquals(0, dvks.size());
	}

	/**
	 * Tests the get_start_chapter method.
	 */
	@Test
	public void test_get_start_chapter() {
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler dvk_handler = new DvkHandler();
		dvk_handler.read_dvks(dirs, prefs, null, false, false, false);
		//CREATE TEST CHAPTER DVKS
		ArrayList<Dvk> cps = new ArrayList<>();
		Dvk dvk = new Dvk();
		dvk.set_title("Randomphilia | Ch. 75");
		dvk.set_id("770792");
		cps.add(dvk);
		dvk = new Dvk();
		dvk.set_title("Randomphilia | Ch. 74");
		dvk.set_id("770791");
		cps.add(dvk);
		dvk = new Dvk();
		dvk.set_title("Randomphilia | Ch. 73");
		dvk.set_id("761782");
		cps.add(dvk);
		dvk = new Dvk();
		dvk.set_title("Randomphilia | Ch. 72");
		dvk.set_id("761781");
		cps.add(dvk);
		dvk = new Dvk();
		dvk.set_title("Randomphilia | Ch. 71");
		dvk.set_id("688479");
		cps.add(dvk);
		//WITH NO EXISTING FILES
		int chapter = MangaDex.get_start_chapter(dvk_handler, cps, false);
		assertEquals(4, chapter);
		//CREATE DVK
		dvk = new Dvk();
		dvk.set_id("MDX761782-2");
		dvk.set_title("Randomphilia | Ch. 73 | Pg. 2");
		dvk.set_page_url("https://mangadex.cc/chapter/761782/1");
		dvk.set_artist("Artist");
		dvk.set_dvk_file(new File(this.test_dir, "dvk.dvk"));
		dvk.set_media_file("media.jpg");
		dvk.write_dvk();
		//CHECK START CHAPTER WITH EXISTING FILES
		dvk_handler.read_dvks(dirs, prefs, null, false, false, false);
		chapter = MangaDex.get_start_chapter(dvk_handler, cps, false);
		assertEquals(2, chapter);
		chapter = MangaDex.get_start_chapter(dvk_handler, cps, true);
		assertEquals(4, chapter);
		//CREATE NEW DVK
		dvk.set_id("MDX688478-5");
		dvk.set_title("Randomphilia | Ch. 75 | Pg. 1");
		dvk.set_page_url("https://mangadex.org/chapter/770792");
		dvk.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk.set_media_file("media.jpg");
		dvk.write_dvk();
		//CHECK START CHAPTER WITH LATEST CHAPTER DOWNLOADED
		dvk_handler.read_dvks(dirs, prefs, null, false, false, false);
		chapter = MangaDex.get_start_chapter(dvk_handler, cps, false);
		assertEquals(0, chapter);
	}
	
	/**
	 * Tests the get_dvks method.
	 */
	@Test
	public void test_get_dvks() {
		DConnectSelenium s_connect = null;
		try {
			//CREATE DVK
			Dvk dvk = new Dvk();
			dvk.set_id("MDX770792-3");
			dvk.set_title("Randomphilia | Ch. 75 | Pg. 3");
			dvk.set_artist("Artist");
			dvk.set_page_url("https://mangadex.org/chapter/770792/3");
			dvk.set_dvk_file(new File(this.test_dir, "dvk.dvk"));
			dvk.set_media_file("test.png");
			dvk.write_dvk();
			//CREATE TEST CHAPTER DVKS
			ArrayList<Dvk> cps = new ArrayList<>();
			dvk = new Dvk();
			dvk.set_id("770792");
			dvk.set_title("Randomphilia | Ch. 75");
			dvk.set_artist("Biru no Fukuro");
			String[] tags = {"Tag"};
			dvk.set_web_tags(tags);
			dvk.set_time("2019/12/21|15:03");
			dvk.set_description("Desc");
			dvk.set_page_url("https://mangadex.org/chapter/770792");
			cps.add(dvk);
			dvk = new Dvk();
			dvk.set_id("770791");
			dvk.set_title("Randomphilia | Ch. 74");
			dvk.set_page_url("https://mangadex.org/chapter/770791");
			cps.add(dvk);
			dvk = new Dvk();
			dvk.set_id("761782");
			dvk.set_title("Randomphilia | Ch. 73");
			dvk.set_page_url("https://mangadex.org/chapter/761782");
			cps.add(dvk);
			//GET DVKS
			File[] dirs = {this.test_dir};
			FilePrefs prefs = new FilePrefs();
			DvkHandler handler = new DvkHandler();
			handler.read_dvks(dirs, prefs, null, false, false, false);
			s_connect = new DConnectSelenium(true);
			ArrayList<Dvk> dvks = MangaDex.get_dvks(
					s_connect, handler, null,
					this.test_dir, cps, false, false);
			//CHECK PAGE 1
			String value;
			assertEquals(3, dvks.size());
			assertEquals("MDX770792-4", dvks.get(2).get_id());
			value = "Randomphilia | Ch. 75 | Pg. 4";
			assertEquals(value, dvks.get(2).get_title());
			assertEquals(1, dvks.get(2).get_artists().length);
			assertEquals("Biru no Fukuro", dvks.get(2).get_artists()[0]);
			assertEquals(1, dvks.get(2).get_web_tags().length);
			assertEquals("Tag", dvks.get(2).get_web_tags()[0]);
			assertEquals("2019/12/21|15:03", dvks.get(2).get_time());
			assertEquals("Desc", dvks.get(2).get_description());
			value = "https://mangadex.org/chapter/770792/4";
			assertEquals(value, dvks.get(2).get_page_url());
			value = "https://s2.mangadex.org/data/"
					+ "2d60025d419442a4d56d58a7bbcdc6db/M4.jpg";
			assertEquals(value, dvks.get(2).get_direct_url());
			value = "Randomphilia - Ch 75 - Pg 4_MDX770792-4.dvk";
			File file = new File(this.test_dir, value);
			assertEquals(file, dvks.get(2).get_dvk_file());
			value = "Randomphilia - Ch 75 - Pg 4_MDX770792-4.jpg";
			file = new File(this.test_dir, value);
			assertEquals(file, dvks.get(2).get_media_file());
			//CHECK PAGE 2
			assertEquals("MDX770792-1", dvks.get(0).get_id());
			value = "Randomphilia | Ch. 75 | Pg. 1";
			assertEquals(value, dvks.get(0).get_title());
			value = "https://mangadex.org/chapter/770792/1";
			assertEquals(value, dvks.get(0).get_page_url());
			value = "https://s2.mangadex.org/data/"
					+ "2d60025d419442a4d56d58a7bbcdc6db/M1.jpg";
			assertEquals(value, dvks.get(0).get_direct_url());
			value = "Randomphilia - Ch 75 - Pg 1_MDX770792-1.dvk";
			file = new File(this.test_dir, value);
			assertEquals(file, dvks.get(0).get_dvk_file());
			value = "Randomphilia - Ch 75 - Pg 1_MDX770792-1.jpg";
			file = new File(this.test_dir, value);
			assertEquals(file, dvks.get(0).get_media_file());
			//CHECK INVALID
			dvks = MangaDex.get_dvks(
					s_connect,
					handler,
					null,
					new File("jslkdjf"),
					new ArrayList<>(), false, false);
			assertEquals(0, dvks.size());
			dvks = MangaDex.get_dvks(
					s_connect, handler,
					null, null,
					new ArrayList<>(), false, false);
			assertEquals(0, dvks.size());
		}
		finally {
			if(s_connect != null) {
				s_connect.close_driver();
			}
		}
	}
	
	/**
	 * Tests the get_id_from_tags method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_id_from_tags() {
		String[] tags = new String[3];
		tags[0] = "bleh";
		tags[1] = "eh";
		tags[2] = "no";
		assertEquals("", MangaDex.get_id_from_tags(tags));
		tags[0] = "MangaDex:";
		assertEquals("", MangaDex.get_id_from_tags(tags));
		tags[0] = "MangaDex:137";
		assertEquals("137", MangaDex.get_id_from_tags(tags));
		tags[0] = "blah";
		tags[1] = "Mangadex:2345";
		assertEquals("2345", MangaDex.get_id_from_tags(tags));
		tags[1] = "no";
		tags[2] = "mangadex:bleh";
		assertEquals("bleh", MangaDex.get_id_from_tags(tags));
	}
	
	/**
	 * Tests the get_downloaded_titles method.
	 */
	@Test
	public void test_get_downloaded_titles() {
		//DVK 1 - MANGADEX:123
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.test_dir, "dvk1.dvk"));
		dvk.set_id("id");
		dvk.set_title("Title");
		dvk.set_artist("artist");
		dvk.set_page_url("www.mangadex.org/thing");
		dvk.set_media_file("file");
		String[] tags = new String[2];
		tags[0] = "mangadex:123";
		tags[1] = "blah";
		dvk.set_web_tags(tags);
		dvk.write_dvk();
		//DVK 2 - REPEAT MANGADEX TAG
		dvk.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk.set_media_file("file");
		dvk.write_dvk();
		//DVK 3 - NEW MANGADEX TAG
		dvk.set_title("Other | ch 1 | pg2");
		dvk.set_dvk_file(new File(this.test_dir, "dvk3.dvk"));
		dvk.set_media_file("file");
		tags[0] = "blah";
		tags[1] = "MangaDex:702";
		dvk.set_web_tags(tags);
		dvk.write_dvk();
		//DVK 4 - NEW MANGADEX TAG - INVALID PAGE URL
		dvk.set_dvk_file(new File(this.test_dir, "dvk4.dvk"));
		dvk.set_media_file("file");
		dvk.set_page_url("something.com");
		tags[1] = "MangaDex:137";
		dvk.set_web_tags(tags);
		dvk.write_dvk();
		//DVK SUB - NEW MANGADEX TAG
		File file = new File(this.test_dir, "sub");
		if(!file.isDirectory()) {
			file.mkdir();
		}
		dvk.set_page_url("https://mangadex.cc/other");
		dvk.set_dvk_file(new File(file, "dvk-sub.dvk"));
		dvk.set_media_file("file");
		tags[1] = "Mangadex:29";
		dvk.set_web_tags(tags);
		dvk.write_dvk();
		//CHECK LOADED PROPERLY
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		ArrayList<Dvk> dvks = MangaDex.get_downloaded_titles(handler);
		assertEquals(5, handler.get_size());
		assertEquals(3, dvks.size());
		//CHECK DVK 1
		boolean check = false;
		for(Dvk cur_dvk: dvks) {
			file = cur_dvk.get_dvk_file();
			if(file.getParentFile().equals(this.test_dir) && file.getName().equals("dvk1.dvk")) {
				check = cur_dvk.get_id().equals("123");
				assertEquals("Title", cur_dvk.get_title());
				break;
			}
		}
		assertTrue(check);
		//CHECK DVK3
		check = false;
		for(Dvk cur_dvk: dvks) {
			file = cur_dvk.get_dvk_file();
			if(file.getParentFile().equals(this.test_dir) && file.getName().equals("dvk3.dvk")) {
				check = cur_dvk.get_id().equals("702");
				assertEquals("Other ", cur_dvk.get_title());
				break;
			}
		}
		assertTrue(check);
		//CHECK DVK SUB
		check = false;
		for(Dvk cur_dvk: dvks) {
			file = cur_dvk.get_dvk_file();
			if(!file.getParentFile().equals(this.test_dir)) {
				check = cur_dvk.get_id().equals("29");
				break;
			}
		}
		assertTrue(check);
	}
}
