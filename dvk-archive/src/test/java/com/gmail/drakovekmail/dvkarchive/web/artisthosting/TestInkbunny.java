package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.file.InOut;

/**
 * Unit tests for the Inkbunny class.
 * 
 * @author Drakovek
 */
public class TestInkbunny {
	
	/*
	 * Just a disclaimer:
	 * Inkbunny.net has a lot of pretty weird and extreme stuff,
	 * and I just want to assure you that all the links used for testing here are tame.
	 * I can't say the same for the rest of the site, so you might
	 * want to use Inkbunny's blacklisting feature if you want to explore further.
	 * Don't say I didn't warn you.
	 */
	
	/**
	 * Inkbunny object for running tests on
	 */
	private Inkbunny ink;
	
	/**
	 * Main directory for holding test files
	 */
	private File test_dir;
	
	/**
	 * Handles test DVK files
	 */
	private DvkHandler dvk_handler;
	
	/**
	 * Creates test files and initializes objects used for testing.
	 */
	@Before
	public void set_up() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "inktest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(this.test_dir);
		try {
			this.dvk_handler = new DvkHandler(prefs);
		}
		catch(DvkException e) {}
		this.ink = new Inkbunny(null, this.dvk_handler);
	}
	
	/**
	 * Removes all test files and closes connections.
	 */
	@After
	public void tear_down() {
		this.ink.close();
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the get_page_id method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_page_id() {
		//MEDIA PAGES
		assertEquals("INK123456", Inkbunny.get_page_id("https://inkbunny.net/s/123456-some-things", true));
		assertEquals("INK5678", Inkbunny.get_page_id("https://inkbunny.net/s/5678", true));
		assertEquals("INK123", Inkbunny.get_page_id("inkbunny.net/s/123/", true));
		assertEquals("123", Inkbunny.get_page_id("https://inkbunny.net/s/123", false));
		//JOURNAL PAGES
		assertEquals("INK123456-J", Inkbunny.get_page_id("https://inkbunny.net/j/123456-some-things/", true));
		assertEquals("INK123-J", Inkbunny.get_page_id("inkbunny.net/j/123/", true));
		assertEquals("INK1234-J", Inkbunny.get_page_id("inkbunny.net/j/1234", true));
		assertEquals("1234", Inkbunny.get_page_id("inkbunny.net/j/1234", false));
		//INVALID URLS
		assertEquals("", Inkbunny.get_page_id("https://inkbunny.net/s/", true));
		assertEquals("", Inkbunny.get_page_id("https://inkbunny.net/s/12'", true));
		assertEquals("", Inkbunny.get_page_id("https://inkbunny.net/s/1NotNum0", false));
		assertEquals("", Inkbunny.get_page_id("https://inkbunny.net/q/123", true));
		assertEquals("", Inkbunny.get_page_id("url.net/j/1234", false));
		assertEquals("", Inkbunny.get_page_id("inkbunny.com/j/1234", true));
		assertEquals("", Inkbunny.get_page_id("something.net/s/1234", false));
	}
	
	/**
	 * Tests the get_user_id method.
	 */
	@Test
	public void test_get_user_id() {
		assertEquals("VOID", this.ink.get_user_id("Tyroo"));
		assertTrue(this.ink.login("guest", ""));
		assertEquals("VOID", this.ink.get_user_id("kkqlk;vj'akjern"));
		assertEquals("123706", this.ink.get_user_id("Tyroo"));
		assertEquals("544679", this.ink.get_user_id("biobasher"));
		assertEquals("159684", this.ink.get_user_id("LittleNapoleon"));
	}
	
	/**
	 * Tests the get_time method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_time() {
		//TEST ALL MONTHS
		assertEquals("2020/01/28|14:44", Inkbunny.get_time("28 Jan 2020 14:44 CEST"));
		assertEquals("2006/02/15|05:23", Inkbunny.get_time("15   feb  2006   5:23   PST"));
		assertEquals("2020/03/05|12:44", Inkbunny.get_time("5  MAR 2020 12:44 gmt"));
		assertEquals("2020/04/28|14:44", Inkbunny.get_time("28 Apr 2020 14:44 CEST"));
		assertEquals("2020/05/30|06:44", Inkbunny.get_time("30 may 2020 6:44"));
		assertEquals("2020/06/10|14:44", Inkbunny.get_time("10 Jun 2020 14:44 "));
		assertEquals("2012/07/18|08:45", Inkbunny.get_time(" 18 Jul 2012  08:45 CEST"));
		assertEquals("2010/08/08|14:42", Inkbunny.get_time("08 aug 2010 14:42 CEST"));
		assertEquals("2020/09/28|23:05", Inkbunny.get_time("28 seP 2020 23:05 EST"));
		assertEquals("2015/10/28|14:44", Inkbunny.get_time("28 Oct 2015 14:44 CEST"));
		assertEquals("1945/11/08|12:45", Inkbunny.get_time("  08 Nov 1945 12:45 CEST"));
		assertEquals("2020/12/28|14:44", Inkbunny.get_time("28 Dec 2020 14:44 CEST"));
		assertEquals(null, Inkbunny.get_time("28 Different 2020 14:44 CEST"));
		//INVALID
		assertEquals(null, Inkbunny.get_time("Mar 2020 14:44 CEST"));
		assertEquals(null, Inkbunny.get_time("28  2020 14:44 CEST"));
		assertEquals(null, Inkbunny.get_time("28 Mar 14:44 CEST"));
		assertEquals(null, Inkbunny.get_time("28 Mar 2020 :44 CEST"));
		assertEquals(null, Inkbunny.get_time("28 Mar 2020 14: CEST"));
		assertEquals(null, Inkbunny.get_time("28 Mar 2020 1444 CEST"));
	}
	
	/**
	 * Tests the get_dvks method.
	 */
	@Test
	public void test_get_dvks() {
		//CREATE DVK 1 TO BE FAVORITED
		Dvk dvk1 = new Dvk();
		dvk1.set_id("INK2110302-1");
		dvk1.set_title("title 1");
		dvk1.set_artist("artist");
		dvk1.set_page_url("/page/");
		String[] tags = {"Tag", "thing", "Favorite:person"};
		dvk1.set_web_tags(tags);
		dvk1.set_dvk_file(new File(this.test_dir, "dvk1.dvk"));
		dvk1.set_media_file("dvk1.png");
		dvk1.write_dvk();
		//CREATE DVK 2 TO BE FAVORITED
		Dvk dvk2 = new Dvk();
		dvk2.set_id("INK2110302-2");
		dvk2.set_title("Title 2");
		dvk2.set_artist("artist");
		dvk2.set_page_url("/page/");
		dvk2.set_dvk_file(new File(this.test_dir, "dvk2.dvk"));
		dvk2.set_media_file("dvk2.txt");
		dvk2.set_secondary_file("dvk2.png");
		dvk2.write_dvk();
		//CREATE DVK 3 WITH FAVORITES, NOT TO BE FAVORITED
		Dvk dvk3 = new Dvk();
		dvk3.set_id("INK1095495-2");
		dvk3.set_title("Title 3");
		dvk3.set_artist("artist");
		dvk3.set_page_url("/page/");
		dvk3.set_web_tags(tags);
		dvk3.set_dvk_file(new File(this.test_dir, "dvk3.dvk"));
		dvk3.set_media_file("dvk3.jpg");
		dvk3.write_dvk();
		//CREATE DVK 4, DECOY JOURNAL PAGE
		Dvk dvk4 = new Dvk();
		dvk4.set_id("INK1095495-J");
		dvk4.set_title("Title 4");
		dvk4.set_artist("artist");
		dvk4.set_page_url("/page/");
		dvk4.set_dvk_file(new File(this.test_dir, "dvk4.dvk"));
		dvk4.set_media_file("dvk4.png");
		dvk4.write_dvk();
		try {
			dvk2.get_media_file().createNewFile();
			dvk2.get_secondary_file().createNewFile();
			dvk3.get_media_file().createNewFile();
		}
		catch(IOException e) {}
		assertTrue(dvk2.get_media_file().exists());
		assertTrue(dvk2.get_secondary_file().exists());
		assertTrue(dvk3.get_media_file().exists());
		assert(dvk1.get_dvk_file().exists());
		assert(dvk2.get_dvk_file().exists());
		assert(dvk3.get_dvk_file().exists());
		assert(dvk4.get_dvk_file().exists());
		//LOAD DVKS
		String url;
		File[] dirs = {this.test_dir};
		assertTrue(this.ink.login("guest", ""));
		//TEST ADDING FAVORITES TAGS - EQUAL PAGES
		this.dvk_handler.read_dvks(dirs, null);
		assertEquals(4, this.dvk_handler.get_size());
		ArrayList<String> favorites = new ArrayList<>();
		favorites.add("Favorite:New Artist");
		ArrayList<Dvk> dvks = this.ink.get_dvks("2110302", this.test_dir, 2, true, favorites, false);
		assertTrue(dvk2.get_media_file().exists());
		assertTrue(dvk2.get_secondary_file().exists());
		assertEquals(2, dvks.size());
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(4, dvks.size());
		assertEquals("title 1", dvks.get(0).get_title());
		assertEquals("Title 2", dvks.get(1).get_title());
		assertEquals(4, dvks.get(0).get_web_tags().length);
		assertEquals("Tag", dvks.get(0).get_web_tags()[0]);
		assertEquals("thing", dvks.get(0).get_web_tags()[1]);
		assertEquals("Favorite:person", dvks.get(0).get_web_tags()[2]);
		assertEquals("Favorite:New Artist", dvks.get(0).get_web_tags()[3]);
		assertEquals(1, dvks.get(1).get_web_tags().length);
		assertEquals("Favorite:New Artist", dvks.get(1).get_web_tags()[0]);
		//TEST ADDING FAVORITES TAGS - FEWER PAGES
		favorites.add("Favorite:Another");
		dvks = this.ink.get_dvks("2110302", this.test_dir, 3, true, favorites, false);
		assertFalse(dvk2.get_media_file().exists());
		assertFalse(dvk2.get_secondary_file().exists());
		assertEquals(1, dvks.size());
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(3, dvks.size());
		Dvk dvk = dvks.get(0);
		assertEquals("INK2110302-1", dvk.get_id());
		assertEquals("Flying magic toaster NOT required.", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("BlueShark", dvk.get_artists()[0]);
		assertEquals("2020/03/17|12:02", dvk.get_time());
		String desc = "Inside joke that will never "
				+ "find its place.<br /><br />Welp im still awake fudge me.<br /><br />"
				+ "Anywho.&nbsp;&nbsp;His ribs somehow froged into his clavicle and made "
				+ "him look like bred.&nbsp;&nbsp;So he is bred.&nbsp;&nbsp;Kerflunk."
				+ "&nbsp;&nbsp;Trippy motion.";
		assertEquals(desc, dvk.get_description());
		assertEquals(12, dvk.get_web_tags().length);
		assertEquals("Gallery:Scraps", dvk.get_web_tags()[0]);
		assertEquals("Rating:General", dvk.get_web_tags()[1]);
		assertEquals("Video - Animation/3D/CGI", dvk.get_web_tags()[2]);
		assertEquals("blender", dvk.get_web_tags()[3]);
		assertEquals("dream", dvk.get_web_tags()[4]);
		assertEquals("dreaming", dvk.get_web_tags()[5]);
		assertEquals("fox", dvk.get_web_tags()[6]);
		assertEquals("male", dvk.get_web_tags()[7]);
		assertEquals("DVK:Single", dvk.get_web_tags()[8]);
		assertEquals("Favorite:New Artist", dvk.get_web_tags()[9]);
		assertEquals("Favorite:Another", dvk.get_web_tags()[10]);
		assertEquals("Favorite:person", dvk.get_web_tags()[11]);
		assertEquals("https://inkbunny.net/s/2110302", dvk.get_page_url());
		url = "https://us.ib.metapix.net/files/full/3054/3054993_BlueShark_bred_2.mp4";
		assertEquals(url, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_file());
		assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
		assertEquals("Flying magic toaster NOT required_INK2110302-1.dvk", dvk.get_dvk_file().getName());
		assertEquals("Flying magic toaster NOT required_INK2110302-1.mp4", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
		//TEST STORY
		dvks = this.ink.get_dvks("2163805", this.test_dir, 3, false, null, true);
		assertEquals(1, dvks.size());
		dvk = dvks.get(0);
		assertEquals(4, this.dvk_handler.get_size());
		assertEquals("INK2163805-1", dvk.get_id());
		assertEquals("Shrek And The Risers: Chapter 1", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Suneverse", dvk.get_artists()[0]);
		assertEquals("2020/05/22|07:17", dvk.get_time());
		desc = "And so, our <span "
				+ "class='strikethrough'>two</span><span class='strikethrough'>"
				+ "</span> three stalwart friends begin another whirlwind adventure"
				+ "...With help from Wolfie&#039;s nephew.";
		assertEquals(desc, dvk.get_description());
		assertEquals(26, dvk.get_web_tags().length);
		assertEquals("Gallery:Main", dvk.get_web_tags()[0]);
		assertEquals("Rating:General", dvk.get_web_tags()[1]);
		assertEquals("Writing - Document", dvk.get_web_tags()[2]);
		assertEquals("big bad wolf", dvk.get_web_tags()[3]);
		assertEquals("cat", dvk.get_web_tags()[4]);
		assertEquals("donkey", dvk.get_web_tags()[5]);
		assertEquals("donkey (shrek)", dvk.get_web_tags()[6]);
		assertEquals("fairytale", dvk.get_web_tags()[7]);
		assertEquals("fan-fic", dvk.get_web_tags()[8]);
		assertEquals("female", dvk.get_web_tags()[9]);
		assertEquals("fiona", dvk.get_web_tags()[10]);
		assertEquals("human", dvk.get_web_tags()[11]);
		assertEquals("jack rider", dvk.get_web_tags()[12]);
		assertEquals("king arthur", dvk.get_web_tags()[13]);
		assertEquals("male", dvk.get_web_tags()[14]);
		assertEquals("movies", dvk.get_web_tags()[15]);
		assertEquals("multiple characters", dvk.get_web_tags()[16]);
		assertEquals("ogre", dvk.get_web_tags()[17]);
		assertEquals("pig", dvk.get_web_tags()[18]);
		assertEquals("pinocchio", dvk.get_web_tags()[19]);
		assertEquals("puppet", dvk.get_web_tags()[20]);
		assertEquals("puss in boots", dvk.get_web_tags()[21]);
		assertEquals("shrek", dvk.get_web_tags()[22]);
		assertEquals("three little pigs", dvk.get_web_tags()[23]);
		assertEquals("wolf", dvk.get_web_tags()[24]);
		assertEquals("Shrek And The Risers", dvk.get_web_tags()[25]);
		assertEquals("https://inkbunny.net/s/2163805", dvk.get_page_url());
		url = "https://us.ib.metapix.net/files/full/3138/3138552_Suneverse_shrek_"
				+ "and_the_risers_title.png";
		assertEquals(null, dvk.get_direct_url());
		assertEquals(url, dvk.get_secondary_url());
		assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
		assertEquals("Shrek And The Risers Chapter 1_INK2163805-1.dvk", dvk.get_dvk_file().getName());
		assertEquals("Shrek And The Risers Chapter 1_INK2163805-1.html", dvk.get_media_file().getName());
		assertEquals("Shrek And The Risers Chapter 1_INK2163805-1.png", dvk.get_secondary_file().getName());
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		assertTrue(dvk.get_secondary_file().exists());
		String read = InOut.read_file(dvk.get_media_file());
		assertTrue(read.startsWith("<!DOCTYPE html><html><span class='font_title'>Chapter 1: The stalwart friends"));
		desc = "&quot;Is everybody alright?&quot;";
		assertTrue(read.contains(desc));
		desc = "Our story continues in Chapter 2: Bard From Showbiz.</html>";
		assertTrue(read.endsWith(desc));
		//TEST MULTIPLE PAGES, THUMBNAIL
		dvks = this.ink.get_dvks("1095495", this.test_dir, 3, false, null, true);
		assertEquals(2, dvks.size());
		assertEquals(5, this.dvk_handler.get_size());
		assertFalse(dvk3.get_media_file().exists());
		dvk = dvks.get(0);
		assertEquals("INK1095495-1", dvk.get_id());
		assertEquals("Double Duty (2013) [1/2]", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("LittleNapoleon", dvk.get_artists()[0]);
		assertEquals("2016/05/21|05:21", dvk.get_time());
		desc = "If you&#039;ve been over on Kraken&#039;s gallery, perhaps you&#039;ve heard of the "
				+ "little-known Caribbean state called the Banana Republic? The elite Presidential "
				+ "Guard unit there is a team of highly trained female combat professionals. They "
				+ "also double as the national beach volleyball team! Better obey El Presidente, or "
				+ "you won&#039;t get to watch Carmen at this year&#039;s Presidential Volleyball "
				+ "Tournament.<br /><br /><br />Carmen &amp; Free Peoples&#039; Worker&#039;s "
				+ "Democratic Sovereign Republic of Banana &copy; <a style='border: none;' "
				+ "title='Kraken on Fur Affinity' rel='nofollow' href='https://furaffinity.net"
				+ "/user/Kraken'><img style='border: none; vertical-align: bottom; width: 14px; "
				+ "height: 14px;' width='14' height='14' src='https://us.ib.metapix.net/images78"
				+ "/contacttypes/internet-furaffinity.png' /></a>&#9;&#9;&#9;&#9;&#9;<a "
				+ "title='Kraken on Fur Affinity' rel='nofollow' href='https://furaffinity.net/"
				+ "user/Kraken'>Kraken</a><br />Art by me!<br />";
		assertEquals(desc, dvk.get_description());
		assertEquals(7, dvk.get_web_tags().length);
		assertEquals("Gallery:Main", dvk.get_web_tags()[0]);
		assertEquals("Rating:General", dvk.get_web_tags()[1]);
		assertEquals("Picture/Pinup", dvk.get_web_tags()[2]);
		assertEquals("female", dvk.get_web_tags()[3]);
		assertEquals("jaguar", dvk.get_web_tags()[4]);
		assertEquals("volleyball", dvk.get_web_tags()[5]);
		assertEquals("Favorite:person", dvk.get_web_tags()[6]);
		assertEquals("https://inkbunny.net/s/1095495", dvk.get_page_url());
		url = "https://us.ib.metapix.net/files/full/1516/1516215_LittleNapoleon"
				+ "_1371773338.littlenapoleon_carmen-guard-small.jpg";
		assertEquals(url, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
		assertEquals("Double Duty 2013 1-2_INK1095495-1.dvk", dvk.get_dvk_file().getName());
		assertEquals("Double Duty 2013 1-2_INK1095495-1.jpg", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
		dvk = dvks.get(1);
		assertEquals("INK1095495-2", dvk.get_id());
		assertEquals("Double Duty (2013) [2/2]", dvk.get_title());
		url = "https://us.ib.metapix.net/files/full/1516/1516216_LittleNapoleon"
				+ "_1371773588.littlenapoleon_carmen-volleyball-small.jpg";
		assertEquals(url, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
		assertEquals("Double Duty 2013 2-2_INK1095495-2.dvk", dvk.get_dvk_file().getName());
		assertEquals("Double Duty 2013 2-2_INK1095495-2.jpg", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
	}
	
	/**
	 * Tests the get_pages method when getting pages.
	 */
	@Test
	public void test_get_pages_favorites() {
		//CREATE DVK 1
		Dvk dvk1 = new Dvk();
		dvk1.set_id("INK406592-1");
		dvk1.set_title("Shark bird");
		dvk1.set_artist("KyteFoxBunny");
		String[] tags1 = {"Other", "Favorite:Tyroo", "Next"};
		dvk1.set_web_tags(tags1);
		dvk1.set_page_url("https://inkbunny.net/s/406592");
		dvk1.set_dvk_file(new File(this.test_dir, "shark.dvk"));
		dvk1.set_media_file("shark.png");
		dvk1.write_dvk();
		//CREATE DVK 2
		Dvk dvk2 = new Dvk();
		dvk2.set_id("INK419741-1");
		dvk2.set_title("Let's Dance");
		dvk2.set_artist("Snofu");
		String[] tags2 = {"Thing", "Favorite:SomeoneElse"};
		dvk2.set_web_tags(tags2);
		dvk2.set_page_url("https://inkbunny.net/s/419741");
		dvk2.set_dvk_file(new File(this.test_dir, "dance.dvk"));
		dvk2.set_media_file("dance.jpg");
		dvk2.write_dvk();
		File sub = new File(this.test_dir, "sub");
		sub.mkdir();
		assertTrue(sub.exists());
		assertTrue(dvk1.get_dvk_file().exists());
		assertTrue(dvk2.get_dvk_file().exists());
		//TEST GETTING EMPTY FAVORITES GALLERY
		File[] dirs = {this.test_dir};
		this.dvk_handler.read_dvks(dirs, null);
		assertTrue(this.ink.login("guest", ""));
		assertEquals(0, this.ink.get_pages("544679", sub, 'f', "biobasher", true).size());
		//TEST GETTING FAVORITES
		ArrayList<Dvk> dvks = this.ink.get_pages("123706", sub, 'f', "Tyroo", false, 1, null, 1);
		assertTrue(dvks.size() > 1);
		int index = dvks.size() - 1;
		assertEquals("419741", dvks.get(index).get_page_url());
		assertEquals("1", dvks.get(index).get_id());
		assertEquals("1265360", dvks.get(index - 1).get_page_url());
		assertEquals("1", dvks.get(index - 1).get_id());
		//TEST DIDN'T MOVE
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(2, dvks.size());
		assertEquals(this.test_dir, dvks.get(0).get_dvk_file().getParentFile());
		assertEquals(this.test_dir, dvks.get(1).get_dvk_file().getParentFile());
		//TEST GET ALL
		dvks = this.ink.get_pages("123706", sub, 'f', "Tyroo", true, 2, null, 1);
		assertTrue(dvks.size() > 3);
		for(int i = 0; i < dvks.size(); i++) {
			assertNotEquals("406592", dvks.get(i).get_page_url());
			assertEquals("1", dvks.get(i).get_id());
		}
		index = dvks.size() - 1;
		assertEquals("343043", dvks.get(index).get_page_url());
	}
	
	/**
	 * Tests the get_pages method.
	 */
	@Test
	public void test_get_pages() {
		//CREATE DVK 1
		Dvk dvk1 = new Dvk();
		dvk1.set_id("INK1095485-1");
		dvk1.set_title("Nightsparrow");
		dvk1.set_artist("LittleNapoleon");
		dvk1.set_page_url("https://inkbunny.net/s/1095485");
		dvk1.set_dvk_file(new File(this.test_dir, "night.dvk"));
		dvk1.set_media_file("night.jpg");
		dvk1.write_dvk();
		//CREATE DVK 2
		Dvk dvk2 = new Dvk();
		dvk2.set_id("INK1095495-1");
		dvk2.set_title("Double Duty");
		dvk2.set_artist("LittleNapoleon");
		dvk2.set_page_url("https://inkbunny.net/s/1095495");
		dvk2.set_dvk_file(new File(this.test_dir, "double.dvk"));
		dvk2.set_media_file("double.jpg");
		dvk2.write_dvk();
		//CREATE DVK 3
		Dvk dvk3 = new Dvk();
		dvk3.set_id("INK1874284-1");
		dvk3.set_title("To the West!");
		dvk3.set_artist("LittleNapoleon");
		String[] tags = {"Test", "Dvk:Single"};
		dvk3.set_web_tags(tags);
		dvk3.set_page_url("https://inkbunny.net/s/1874284");
		dvk3.set_dvk_file(new File(this.test_dir, "west.dvk"));
		dvk3.set_media_file("west.png");
		dvk3.write_dvk();
		assertTrue(dvk1.get_dvk_file().exists());
		assertTrue(dvk2.get_dvk_file().exists());
		//CREATE NEW DIRECTORY
		File sub = new File(this.test_dir, "sub");
		sub.mkdir();
		assertTrue(this.ink.login("guest", ""));
		//TEST EMPTY SCRAPS DIRECTORY
		File[] dirs = {this.test_dir};
		this.dvk_handler.read_dvks(dirs, null);
		ArrayList<Dvk> dvks = this.ink.get_pages("159684", sub, 's', "LittleNapoleon", true);
		assertEquals(0, dvks.size());
		dvks = this.ink.get_pages("159684", sub, 'm', "LittleNapoleon", false, 10, null, 1);
		assertTrue(dvks.size() > 60);
		int index = -1;
		for(int i = 0; i < dvks.size(); i++) {
			assertNotEquals("1095485", dvks.get(i).get_page_url());
			assertNotEquals("1874284", dvks.get(i).get_page_url());
			assertNotEquals("1095394", dvks.get(i).get_page_url());
			if(dvks.get(i).get_page_url().equals("1095490")) {
				assertEquals("1", dvks.get(i).get_id());
				index = i;
			}
		}
		assertNotEquals(-1, index);
		assertEquals("1095495", dvks.get(index - 1).get_page_url());
		assertEquals("2", dvks.get(index - 1).get_id());
		assertEquals("1095532", dvks.get(index - 2).get_page_url());
		assertEquals("1", dvks.get(index - 2).get_id());
		assertEquals("1095540", dvks.get(index - 3).get_page_url());
		assertEquals("1", dvks.get(index - 3).get_id());
		//CHECK FILES MOVED
		dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(3, dvks.size());
		assertEquals("Double Duty", dvks.get(0).get_title());
		assertEquals(this.test_dir, dvks.get(0).get_dvk_file().getParentFile());
		assertTrue(dvks.get(0).get_dvk_file().exists());
		assertEquals("Nightsparrow", dvks.get(1).get_title());
		assertEquals(this.test_dir, dvks.get(1).get_dvk_file().getParentFile());
		assertTrue(dvks.get(1).get_dvk_file().exists());
		assertEquals("To the West!", dvks.get(2).get_title());
		assertEquals(sub, dvks.get(2).get_dvk_file().getParentFile());
		assertTrue(dvks.get(2).get_dvk_file().exists());
		//TEST NO FAVORITES ADDED
		assertTrue(dvks.get(0).get_web_tags() == null);
		assertTrue(dvks.get(1).get_web_tags() == null);
		assertEquals(2, dvks.get(2).get_web_tags().length);
		assertEquals("Test", dvks.get(2).get_web_tags()[0]);
		assertEquals("Dvk:Single", dvks.get(2).get_web_tags()[1]);
		//TEST GETTING ALL PAGES
		dvks = this.ink.get_pages("159684", sub, 'm', "LittleNapoleon", true);
		assertTrue(dvks.size() > 70);
		index = dvks.size() - 1;
		assertEquals("1095394", dvks.get(index).get_page_url());
		assertEquals("1", dvks.get(index).get_id());
		assertEquals("1095397", dvks.get(index - 1).get_page_url());
		assertEquals("1", dvks.get(index - 1).get_id());
		assertEquals("1095404", dvks.get(index - 2).get_page_url());
		assertEquals("1", dvks.get(index - 2).get_id());
		index = -1;
		for(int i = 0; i < dvks.size(); i++) {
			if(dvks.get(i).get_page_url().equals("1095400")) {
				assertEquals("2", dvks.get(i).get_id());
				index = i;
			}
		}
		assertNotEquals(-1, index);
	}
	
	/**
	 * Tests the get_journal_pages method.
	 */
	@Test
	public void test_get_journal_pages() {
		//CREATE DVK1
		Dvk dvk1 = new Dvk();
		dvk1.set_id("INK386752-J");
		dvk1.set_title("Streaming");
		dvk1.set_artist("SonicSpirit");
		String[] tags = {"blah", "Dvk:Single", "other"};
		dvk1.set_web_tags(tags);
		dvk1.set_page_url("https://inkbunny.net/j/386752-SonicSpirit-streaming-whooooooo-");
		dvk1.set_dvk_file(new File(this.test_dir, "streaming.dvk"));
		dvk1.set_media_file("streaming.png");
		dvk1.write_dvk();
		File sub = new File(this.test_dir, "sub");
		sub.mkdir();
		assertTrue(dvk1.get_dvk_file().exists());
		assertTrue(sub.exists());
		//CHECK SKIPS SINGLE
		File[] dirs = {this.test_dir};
		this.dvk_handler.read_dvks(dirs, null);
		ArrayList<String> ids = this.ink.get_journal_pages("SonicSpirit", sub, false, 1);
		assertTrue(ids.size() > 25);
		assertFalse(ids.contains("386752"));
		assertTrue(ids.contains("389024"));
		assertTrue(ids.contains("377566"));
		assertTrue(ids.contains("125294"));
		assertEquals("4165", ids.get(ids.size() - 1));
		//CHECK SINGLE DVK MOVED
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(1, dvks.size());
		assertEquals("Streaming", dvks.get(0).get_title());
		assertEquals(sub, dvks.get(0).get_dvk_file().getParentFile());
		assertTrue(dvks.get(0).get_dvk_file().exists());
		//CREATE DVK2
		Dvk dvk2 = new Dvk();
		dvk2.set_id("INK389024-J");
		dvk2.set_title("Doop");
		dvk2.set_artist("SonicSpirit");
		dvk2.set_page_url("https://inkbunny.net/j/389024-SonicSpirit-doop-doop-it-doesnt-matter-update");
		dvk2.set_dvk_file(new File(this.test_dir, "doop.dvk"));
		dvk2.set_media_file("doop.dvk");
		dvk2.write_dvk();
		assertTrue(dvk2.get_dvk_file().exists());
		//CHECKS STOPS ON JOURNAL GALLERY PAGE
		this.dvk_handler.read_dvks(dirs, null);
		ids = this.ink.get_journal_pages("SonicSpirit", sub, false, 1);
		assertTrue(ids.size() > 1);
		assertFalse(ids.contains("389024"));
		assertFalse(ids.contains("386752"));
		assertFalse(ids.contains("4165"));
		assertTrue(ids.contains("388107"));
	}
	
	/**
	 * Tests the get_journal_dvk method.
	 */
	@Test
	public void test_get_journal_dvk() {
		File[] dirs = {this.test_dir};
		this.dvk_handler.read_dvks(dirs, null);
		//TEST INVALID
		Dvk dvk = this.ink.get_journal_dvk("nwD135ajkds", this.test_dir, true, true);
		assertEquals(null, dvk.get_title());
		//TEST GETTING JOURNAL PAGE
		dvk = this.ink.get_journal_dvk("387688", this.test_dir, true, true);
		assertEquals("INK387688-J", dvk.get_id());
		assertEquals("It's an \"It Doesn't Matter\" Week!", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("SonicSpirit", dvk.get_artists()[0]);
		assertEquals("2020/04/28|14:44", dvk.get_time());
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals("Gallery:Journals", dvk.get_web_tags()[0]);
		assertEquals("DVK:Single", dvk.get_web_tags()[1]);
		String desc = "LET'S DO THIS! For everybody who doesn't know (which seems like most peoples)"
				+ ", I have two main projects right now, the comic The New Normal, and It Doesn't "
				+ "Matter, my big stupid Sonic TF animation...that still hasn't gotten to the "
				+ "TF yet, cuz I'm setting up the story and animating is slow. The way I've "
				+ "been doing it is I alternate weeks where I put out a page of The New Normal and "
				+ "a shot (in animation terms scene, but yanno, confusing) of It Doesn't Matter! "
				+ "So here's this week's update of <a href=\"https://vimeo.com/407854574\" "
				+ "rel=\"nofollow\"> It Doesn't Matter Zone 1, Scene 5 (so far) </a> and the full "
				+ "<a href=\"https://vimeo.com/379934017\" rel=\"nofollow\"> It Doesn't Matter Zone "
				+ "1 (so far </a> ! I hope you like it so far! <br/> <br/> Oh, and I haven't put "
				+ "sound effects in on scene 5 yet, and kinda not dialed in all the sound period "
				+ "just yet. <br/> <br/> <a href=\"https://www.patreon.com/sonic_spirit\" "
				+ "rel=\"nofollow\"> I have a Patreon, now! Join up to see comic pages and animation "
				+ "shots early! </a>";
		assertEquals(desc, dvk.get_description());
		assertEquals("https://inkbunny.net/j/387688", dvk.get_page_url());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
		assertEquals("It-s an It Doesn-t Matter Week_INK387688-J.dvk", dvk.get_dvk_file().getName());
		assertEquals("It-s an It Doesn-t Matter Week_INK387688-J.html", dvk.get_media_file().getName());
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		String journal = InOut.read_file(dvk.get_media_file());
		assertEquals("<!DOCTYPE html><html>" + desc + "</html>", journal);
		//TEST GETTING SECOND JOURNAL PAGE
		dvk = this.ink.get_journal_dvk("382279", this.test_dir, false, true);
		assertEquals("INK382279-J", dvk.get_id());
		assertEquals("“Alteration” is a Pretty Generic Name, Help Me Do Better?", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("SonicSpirit", dvk.get_artists()[0]);
		assertEquals("2020/02/28|19:19", dvk.get_time());
		assertEquals(1, dvk.get_web_tags().length);
		assertEquals("Gallery:Journals", dvk.get_web_tags()[0]);
		desc = "So, I need a better name for a cyberpunk nanobot-driven &#8220;bodymod&#8221; TF "
				+ "setting/story, and thought it&#8217;d be fun to ask for suggestions! <br/> "
				+ "<br/> Long story short, I wrote a short story TF going into FurSquared that "
				+ "I&#8217;ll be tossing up here, (just need to finish an illustration to go "
				+ "with it), and I really enjoyed it. I like the setting and the characters, "
				+ "but their arcs are pretty complete...which makes it perfect fodder for TF "
				+ "sequences. <br/> <br/> So yeah! Thoughts on what I should re-name this "
				+ "cyberpunk nanobot TF setting, site unseen?";
		assertEquals(desc, dvk.get_description());
		assertEquals("https://inkbunny.net/j/382279", dvk.get_page_url());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
		assertEquals("Alteration is a Pretty Generic Name Help Me Do Better_INK382279-J.dvk",
				dvk.get_dvk_file().getName());
		assertEquals("Alteration is a Pretty Generic Name Help Me Do Better_INK382279-J.html",
				dvk.get_media_file().getName());
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		journal = InOut.read_file(dvk.get_media_file());
		assertEquals("<!DOCTYPE html><html>" + desc + "</html>", journal);
		//TEST DVKS ADDED
		ArrayList<Dvk> dvks = this.dvk_handler.get_dvks(0, -1, 'a', false, false);
		assertEquals(2, dvks.size());
		assertEquals("It's an \"It Doesn't Matter\" Week!", dvks.get(0).get_title());
		assertEquals("“Alteration” is a Pretty Generic Name, Help Me Do Better?", dvks.get(1).get_title());
	}
}
