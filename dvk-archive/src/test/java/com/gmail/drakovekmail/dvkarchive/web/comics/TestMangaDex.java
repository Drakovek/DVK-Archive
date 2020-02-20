package com.gmail.drakovekmail.dvkarchive.web.comics;

import static org.junit.Assert.assertEquals;
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
		dvks = MangaDex.get_chapters(this.connect, dvk, "English", 1);
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
		dvks = MangaDex.get_chapters(this.connect, dvk, "Italian", 1);
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
		dvks = MangaDex.get_chapters(this.connect, dvk, "JAKJSKDJK", 1);
		assertEquals(0, dvks.size());
	}

	/**
	 * Tests the get_start_chapter method.
	 */
	public void test_get_start_chapter() {
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler dvk_handler = new DvkHandler();
		dvk_handler.read_dvks(dirs, prefs, null, false, false, false);
		Dvk title = MangaDex.get_title_info(this.connect, "34326");
		ArrayList<Dvk> cps = MangaDex.get_chapters(this.connect, title, "French", 1);
		//WITH NO EXISTING FILES
		int chapter = MangaDex.get_start_chapter(dvk_handler, cps, false);
		assertEquals(74, chapter);
		//CREATE DVK
		Dvk dvk = new Dvk();
		dvk.set_id("MDX688478-5");
		dvk.set_title("Randomphilia | Ch. 70 | Pg. 5");
		dvk.set_page_url("https://mangadex.cc/chapter/688478/1");
		dvk.set_artist("Artist");
		dvk.set_dvk_file(new File(this.test_dir, "dvk.dvk"));
		dvk.set_media_file("media.jpg");
		dvk.write_dvk();
		//CHECK START CHAPTER WITH EXISTING FILES
		dvk_handler.read_dvks(dirs, prefs, null, false, false, false);
		chapter = MangaDex.get_start_chapter(dvk_handler, cps, false);
		assertEquals(5, chapter);
		chapter = MangaDex.get_start_chapter(dvk_handler, cps, true);
		assertEquals(74, chapter);
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
}
