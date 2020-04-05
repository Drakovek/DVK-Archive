package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import static org.junit.Assert.assertEquals;
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
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;

/**
 * Unit tests for the FurAffinity object.
 * 
 * @author Drakovek
 */
public class TestFurAffinity {
	
	/**
	 * Directory for holding test files.
	 */
	private File test_dir;
	
	/**
	 * FurAffinity object for testing
	 */
	private FurAffinity fur;
	
	/**
	 * Sets up object and files for testing.
	 */
	@Before
	public void set_up() {
		//CREATE TEST FILES
		create_directory();
		//SET UP FURAFFINITY OBJECT
		FilePrefs prefs = new FilePrefs();
		prefs.set_captcha_dir(this.test_dir);
		this.fur = new FurAffinity(prefs);
	}
	
	/**
	 * Removes test objects and files.
	 */
	@After
	public void tear_down() {
		delete_directory();
		this.fur.close();
	}
	
	/**
	 * Creates the directory for holding test files.
	 */
	private void create_directory() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "faftest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
	}
	
	/**
	 * Deletes the directory holding test files.
	 */
	private void delete_directory() {
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the get_url_artists method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_url_artist() {
		assertEquals("artist",
				FurAffinity.get_url_artist("ArTiSt"));
		assertEquals("otherartistguy",
				FurAffinity.get_url_artist("Other_Artist_Guy"));
		assertEquals("thatisit",
				FurAffinity.get_url_artist("That is It"));
	}
	
	/**
	 * Tests the get_page_id method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_page_id() {
		String url = "https://www.different.net/view/13982138/";
		assertEquals("", FurAffinity.get_page_id(url, true));
		assertEquals("", FurAffinity.get_page_id(url, false));
		url = "https://www.furaffinity.net/bleh/13982138/";
		assertEquals("", FurAffinity.get_page_id(url, true));
		assertEquals("", FurAffinity.get_page_id(url, false));
		url = "https://www.furaffinity.net/view/13982138";
		assertEquals("FAF13982138", FurAffinity.get_page_id(url, true));
		assertEquals("13982138", FurAffinity.get_page_id(url, false));
		url = "furaffinity.net/view/22840286/";
		assertEquals("FAF22840286", FurAffinity.get_page_id(url, true));
		assertEquals("22840286", FurAffinity.get_page_id(url, false));
		url = "https://www.furaffinity.net/journal/8610682";
		assertEquals("FAF8610682-J", FurAffinity.get_page_id(url, true));
		assertEquals("8610682", FurAffinity.get_page_id(url, false));
		url = "furaffinity.net/journal/8594821/";
		assertEquals("FAF8594821-J", FurAffinity.get_page_id(url, true));
		assertEquals("8594821", FurAffinity.get_page_id(url, false));
		url = "https://www.furaffinity.net/view/bleh";
		assertEquals("FAFbleh", FurAffinity.get_page_id(url, true));
		assertEquals("bleh", FurAffinity.get_page_id(url, false));
		url = "https://www.furaffinity.net/journal/bleh";
		assertEquals("FAFbleh-J", FurAffinity.get_page_id(url, true));
		assertEquals("bleh", FurAffinity.get_page_id(url, false));
		url = "https://www.furaffinity.net/view/";
		assertEquals("FAF", FurAffinity.get_page_id(url, true));
		assertEquals("", FurAffinity.get_page_id(url, false));
		url = "https://www.furaffinity.net/journal/";
		assertEquals("FAF-J", FurAffinity.get_page_id(url, true));
		assertEquals("", FurAffinity.get_page_id(url, false));
	}
	
	/**
	 * Tests the get_time method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_time() {
		//INVALID
		assertEquals("0000/00/00|00:00",
				FurAffinity.get_time("1, 2020 10:23 PM"));
		assertEquals("0000/00/00|00:00",
				FurAffinity.get_time("Jan 2020 10:23 PM"));
		assertEquals("0000/00/00|00:00",
				FurAffinity.get_time("Jan 1, 10:23 PM"));
		assertEquals("0000/00/00|00:00",
				FurAffinity.get_time("Jan 1, 2020 23 PM"));
		assertEquals("0000/00/00|00:00",
				FurAffinity.get_time("Jan 1, 2020 10 PM"));
		assertEquals("0000/00/00|00:00",
				FurAffinity.get_time("Jan 1, 2020 10:23 "));
		//TEST ALL MONTHS
		assertEquals("2020/01/01|22:23",
				FurAffinity.get_time(" Jan  1,  2020  10:23 PM  "));
		assertEquals("1997/02/13|09:05",
				FurAffinity.get_time("FEB  13, 1997  9:05 Am"));
		assertEquals("2005/03/12|00:15",
				FurAffinity.get_time("mar  012,   2005  12:15 am"));
		assertEquals("2016/04/08|12:05",
				FurAffinity.get_time("aPr  08, 2016  012:05 pm"));
		assertEquals("2016/05/15|13:12",
				FurAffinity.get_time("May  15, 2016  1:12 Pm"));
		assertEquals("2016/06/08|01:05",
				FurAffinity.get_time("jun  08, 2016  1:05  Am"));
		assertEquals("2016/07/08|01:05",
				FurAffinity.get_time("Jul  08, 2016  1:05  Am"));
		assertEquals("2016/08/08|01:05",
				FurAffinity.get_time("aug  08, 2016  1:05  Am"));
		assertEquals("2016/09/08|01:05",
				FurAffinity.get_time("seP  08, 02016  1:005  Am"));
		assertEquals("2016/10/08|01:05",
				FurAffinity.get_time("Oct  08, 2016  1:05  Am"));
		assertEquals("2016/11/08|01:05",
				FurAffinity.get_time("nOv 08, 2016  1:05  Am"));
		assertEquals("2016/12/08|01:05",
				FurAffinity.get_time("dec  08, 2016  1:05  Am"));
		assertEquals("0000/00/00|00:00",
				FurAffinity.get_time("jug  08, 2016  1:05  Am"));
	}
	
	/**
	 * Tests the get_pages method.
	 */
	@Test
	public void test_get_pages() {
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.test_dir, "shortcut.dvk"));
		dvk.set_id("FAF35034678");
		dvk.set_title("Down the Shortcut");
		dvk.set_artist("Mr_Sparta");
		String[] tags = {"DVK:Single", "Favorite:Whoever"};
		dvk.set_web_tags(tags);
		String url = "https://www.furaffinity.net/view/35034678/";
		dvk.set_page_url(url);
		dvk.set_media_file("shortcut.png");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk.set_dvk_file(new File(this.test_dir, "rabbit.dvk"));
		dvk.set_id("FAF13982138");
		dvk.set_title("Rabbit in the city");
		tags[0] = null;
		dvk.set_web_tags(tags);
		url = "https://www.furaffinity.net/view/13982138/";
		dvk.set_page_url(url);
		dvk.set_media_file("rabbit.png");
		dvk.write_dvk();
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		File sub = new File(this.test_dir, "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		//TEST SMALL SAMPLE
		ArrayList<String> links = this.fur.get_pages(
				null, "drakovek", sub, 'm', handler, true, false, 1);
		assertEquals(2, links.size());
		url = "https://www.furaffinity.net/view/32521285/";
		assertEquals(url, links.get(1));
		links = this.fur.get_pages(
				null, "drakovek", sub, 's', handler, false, false, 1);
		assertEquals(1, links.size());
		url = "https://www.furaffinity.net/view/31071186/";
		assertEquals(url, links.get(0));
		//TEST ALREADY DOWNLOADED
		links = this.fur.get_pages(
				null, "mrsparta", sub, 'm', handler, false, false, 1);
		assertTrue(links.size() > 93);
		int index = -1;
		url = "https://www.furaffinity.net/view/13982138/";
		for(int i = 0; i < links.size(); i++) {
			assertNotEquals(url, links.get(i));
			if(links.get(i).equals(
					"https://www.furaffinity.net/view/14019897/")) {
				index = i;
			}
		}
		assertNotEquals(-1, index);
		url = "https://www.furaffinity.net/view/14202184/";
		assertEquals(url, links.get(index - 1));
		url = "https://www.furaffinity.net/view/14354843/";
		assertEquals(url, links.get(index - 2));
		url = "https://www.furaffinity.net/view/14664720/";
		assertEquals(url, links.get(index - 3));
		//TEST IF DOWNLOADED DVK MOVED
		handler.read_dvks(dirs, prefs, null, false, false, false);
		handler.sort_dvks_title(false, false);
		assertEquals(2, handler.get_size());
		dvk = handler.get_dvk(0);
		assertEquals("shortcut.dvk", dvk.get_dvk_file().getName());
		assertEquals(sub, dvk.get_dvk_file().getParentFile());
		assertEquals("shortcut.png", dvk.get_media_file().getName());
		assertEquals(sub, dvk.get_media_file().getParentFile());
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals("DVK:Single", dvk.get_web_tags()[0]);
		assertEquals("Favorite:Whoever", dvk.get_web_tags()[1]);
		dvk = handler.get_dvk(1);
		assertEquals("rabbit.dvk", dvk.get_dvk_file().getName());
		assertEquals(sub, dvk.get_dvk_file().getParentFile());
		assertEquals("rabbit.png", dvk.get_media_file().getName());
		assertEquals(sub, dvk.get_media_file().getParentFile());
		assertEquals(1, dvk.get_web_tags().length);
		assertEquals("Favorite:Whoever", dvk.get_web_tags()[0]);
		//TEST LOGIN
		if(!this.fur.is_logged_in()) { 
			links = this.fur.get_pages(
					null, "mrsparta", sub, 'm',
					handler, false, true, 1);
			assertEquals(0, links.size());
		}
	}
	
	/**
	 * Tests the get_pages method for getting favorites pages.
	 */
	@Test
	public void test_get_pages_favorites() {
		//CREATE DVK
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.test_dir, "hatchling.dvk"));
		dvk.set_id("FAF27695082");
		dvk.set_title("Hatchlings");
		dvk.set_artist("hawtie10");
		String[] tags = {"DVK:Single"};
		dvk.set_web_tags(tags);
		String url = "https://www.furaffinity.net/view/27695082/";
		dvk.set_page_url(url);
		dvk.set_media_file("hatchling.png");
		dvk.write_dvk();
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		File sub = new File(this.test_dir, "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		//TEST ALREADY DOWNLOADED
		ArrayList<String> links;
		links = this.fur.get_pages(
				null, "Thundergonian", sub, 'f',
				handler, false, false, 1);
		assertTrue(links.size() > 46);
		int index = -1;
		url = "https://www.furaffinity.net/view/27695082/";
		for(int i = 0; i < links.size(); i++) {
			assertNotEquals(url, links.get(i));
			if(links.get(i).equals(
					"https://www.furaffinity.net/view/35442825/")) {
				index = i;
			}
		}
		assertNotEquals(-1, index);
		url = "https://www.furaffinity.net/view/35470135/";
		assertEquals(url, links.get(index - 1));
		url = "https://www.furaffinity.net/view/35496095/";
		assertEquals(url, links.get(index - 2));
		//TEST DOWNLOADED MOVED
		handler.read_dvks(dirs, prefs, null, false, false, false);
		assertEquals(1, handler.get_size());
		dvk = handler.get_dvk(0);
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals("DVK:Single", dvk.get_web_tags()[0]);
		assertEquals("Favorite:Thundergonian",
				dvk.get_web_tags()[1]);
		assertEquals(this.test_dir,
				dvk.get_dvk_file().getParentFile());
		assertEquals(this.test_dir,
				dvk.get_media_file().getParentFile());
		assertTrue(dvk.get_dvk_file().exists());
	}
	
	/**
	 * Tests the get_journal_pages method.
	 */
	@Test
	public void test_get_journal_pages() {
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.test_dir, "might.dvk"));
		dvk.set_id("FAF8104946-J");
		dvk.set_title("might as well");
		dvk.set_artist("angrboda");
		String[] tags = {"bleh", "DVK:Single"};
		dvk.set_web_tags(tags);
		String url = "https://www.furaffinity.net/journal/8104946/";
		dvk.set_page_url(url);
		dvk.set_media_file("might.png");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk.set_dvk_file(new File(this.test_dir, "mff.dvk"));
		dvk.set_id("FAF4030490-J");
		dvk.set_title("finding me at MFF");
		tags[1] = null;
		dvk.set_web_tags(tags);
		url = "https://www.furaffinity.net/journal/4030490/";
		dvk.set_page_url(url);
		dvk.set_media_file("mff.png");
		dvk.write_dvk();
		File sub = new File(this.test_dir, "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		//TEST SMALL SAMPLE
		ArrayList<String> links = this.fur.get_journal_pages(
				null, "mr_sparta", sub, handler, true, false, 1);
		assertTrue(links.size() > 3);
		url = "https://www.furaffinity.net/journal/8594821/";
		assertEquals(url, links.get(links.size() - 1));
		//TEST ALREADY DOWNLOADED
		links = this.fur.get_journal_pages(
				null, "angrboda", sub, handler, false, false, 1);
		assertTrue(links.size() > 47);
		int index = -1;
		url = "https://www.furaffinity.net/journal/4030490/";
		for(int i = 0; i < links.size(); i++) {
			assertNotEquals(url, links.get(i));
			if(links.get(i).equals(
					"https://www.furaffinity.net/journal/4193757/")) {
				index = i;
			}
		}
		assertNotEquals(-1, index);
		url = "https://www.furaffinity.net/journal/4247934/";
		assertEquals(url, links.get(index - 1));
		url = "https://www.furaffinity.net/journal/4355518/";
		assertEquals(url, links.get(index - 2));
		url = "https://www.furaffinity.net/journal/4405644/";
		assertEquals(url, links.get(index - 3));
		//CHECK FILES MOVED
		handler.read_dvks(dirs, prefs, null, false, false, false);
		handler.sort_dvks_title(false, false);
		assertEquals(2, handler.get_size());
		dvk = handler.get_dvk(0);
		assertEquals(1, dvk.get_web_tags().length);
		assertEquals(sub, dvk.get_dvk_file().getParentFile());
		dvk = handler.get_dvk(1);
		assertEquals(2, dvk.get_web_tags().length);
		assertEquals(sub, dvk.get_dvk_file().getParentFile());
		//TEST LOGIN
		if(!this.fur.is_logged_in()) {
			links = this.fur.get_journal_pages(
					null, "mrsparta", sub, handler, false, true, 1);
			assertEquals(0, links.size());
		}
	}
	
	/**
	 * Runs tests after being logged in to Fur Affinity
	 */
	@Test
	public void test_after_login() {
		File cap_file = this.fur.get_captcha();
		String[] info = this.fur.get_user_info(
				"Fur Affinity", cap_file);
		this.fur.login(info[0], info[1], info[2]);
		info = null;
		assertTrue(this.fur.is_logged_in());
		//GET PAGES
		test_get_pages();
		//GET FAVORITES PAGES
		delete_directory();
		create_directory();
		test_get_pages_favorites();
		//GET JOURNAL PAGES
		delete_directory();
		create_directory();
		test_get_journal_pages();
		//GET DVK
		delete_directory();
		create_directory();
		test_get_dvk();
		//GET JOURNAL DVK
		delete_directory();
		create_directory();
		test_get_journal_dvk();
	}
	
	/**
	 * Tests the get_dvk method.
	 */
	@Test
	public void test_get_dvk() {
		//FIRST DVK
		String url = "http://www.furaffinity.net/view/32521285/";
		Dvk dvk = this.fur.get_dvk(url, this.test_dir, null, false, false);
		assertEquals("FAF32521285", dvk.get_id());
		assertEquals("Robin the Bobcat", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Drakovek", dvk.get_artists()[0]);
		assertEquals("2019/08/03|17:21", dvk.get_time());
		String value = "https://www.furaffinity.net/view/32521285/";
		assertEquals(value, dvk.get_page_url());
		value = "https://d.facdn.net/art/drakovek/1564867315/"
				+ "1564867315.drakovek_robin.png";
		assertEquals(value, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		value = "Character portrait for Robin, a bobcat character "
				+ "from a story I've had in my head for a long time "
				+ "that may or may not ever be written down. "
				+ "We'll see.";
		assertEquals(value, dvk.get_description());
		assertEquals(10, dvk.get_web_tags().length);
		assertEquals("Rating:General", dvk.get_web_tags()[0]);
		assertEquals("Category:Artwork (Digital)", dvk.get_web_tags()[1]);
		assertEquals("Type:Portraits", dvk.get_web_tags()[2]);
		assertEquals("Species:Feline (Other)", dvk.get_web_tags()[3]);
		assertEquals("Gender:Female", dvk.get_web_tags()[4]);
		assertEquals("Gallery:Main", dvk.get_web_tags()[5]);
		assertEquals("bobcat", dvk.get_web_tags()[6]);
		assertEquals("portrait", dvk.get_web_tags()[7]);
		assertEquals("vector", dvk.get_web_tags()[8]);
		assertEquals("inkscape", dvk.get_web_tags()[9]);
		assertEquals("Robin the Bobcat_FAF32521285.dvk",
				dvk.get_dvk_file().getName());
		assertEquals(this.test_dir,
				dvk.get_dvk_file().getParentFile());
		assertEquals("Robin the Bobcat_FAF32521285.png",
				dvk.get_media_file().getName());
		assertEquals(this.test_dir,
				dvk.get_media_file().getParentFile());
		assertEquals(null, dvk.get_secondary_file());
		//SECOND DVK
		url = "furaffinity.net/view/15301779";
		dvk = this.fur.get_dvk(url, this.test_dir, "ArtDude", true, false);
		assertEquals("FAF15301779", dvk.get_id());
		assertEquals("Affinity Ch. 1", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("MrSparta", dvk.get_artists()[0]);
		assertEquals("2014/12/24|02:11", dvk.get_time());
		value = "https://www.furaffinity.net/view/15301779/";
		assertEquals(value, dvk.get_page_url());
		value = "https://d.facdn.net/art/mrsparta/stories/"
				+ "1488278723/1419405086.mrsparta_md-1.txt";
		assertEquals(value, dvk.get_direct_url());
		value = "https://d.facdn.net/art/mrsparta/stories/"
				+ "1488278723/1419405086.thumbnail"
				+ ".mrsparta_md-1.txt.gif";
		assertEquals(value, dvk.get_secondary_url());
		value = "I'm trying something new. It's a strange "
				+ "practice called \"character development\"";
		assertEquals(value, dvk.get_description());
		assertEquals(17, dvk.get_web_tags().length);
		assertEquals("Rating:General", dvk.get_web_tags()[0]);
		assertEquals("Category:Story", dvk.get_web_tags()[1]);
		assertEquals("Type:All", dvk.get_web_tags()[2]);
		assertEquals("Species:Unspecified / Any",
				dvk.get_web_tags()[3]);
		assertEquals("Gender:Any", dvk.get_web_tags()[4]);
		assertEquals("Gallery:Scraps", dvk.get_web_tags()[5]);
		assertEquals("Buizel", dvk.get_web_tags()[6]);
		assertEquals("totodile", dvk.get_web_tags()[7]);
		assertEquals("litleo", dvk.get_web_tags()[8]);
		assertEquals("taillow", dvk.get_web_tags()[9]);
		assertEquals("sandslash", dvk.get_web_tags()[10]);
		assertEquals("mass", dvk.get_web_tags()[11]);
		assertEquals("genocide", dvk.get_web_tags()[12]);
		assertEquals("of", dvk.get_web_tags()[13]);
		assertEquals("digimon", dvk.get_web_tags()[14]);
		assertEquals("DVK:Single", dvk.get_web_tags()[15]);
		assertEquals("Favorite:ArtDude", dvk.get_web_tags()[16]);
		assertEquals("Affinity Ch 1_FAF15301779.dvk",
				dvk.get_dvk_file().getName());
		assertEquals(this.test_dir,
				dvk.get_dvk_file().getParentFile());
		assertEquals("Affinity Ch 1_FAF15301779.txt",
				dvk.get_media_file().getName());
		assertEquals(this.test_dir,
				dvk.get_media_file().getParentFile());
		assertEquals("Affinity Ch 1_FAF15301779.gif",
				dvk.get_secondary_file().getName());
		assertEquals(this.test_dir,
				dvk.get_secondary_file().getParentFile());
		//SECOND DVK
		url = "www.furaffinity.net/view/29756524/";
		dvk = this.fur.get_dvk(url, this.test_dir, null, false, false);
		assertEquals("FAF29756524", dvk.get_id());
		value = "[Changed fanart] Are you going to "
				+ "eat that Peach, human?";
		assertEquals(value, dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Tavaer", dvk.get_artists()[0]);
		assertEquals("2018/12/16|00:02", dvk.get_time());
		value = "https://www.furaffinity.net/view/29756524/";
		assertEquals(value, dvk.get_page_url());
		value = "https://d.facdn.net/art/tavaer/"
				+ "1544936555/1544936555.tavaer_purero.png";
		assertEquals(value, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		value = "Puro: \"Are you going to eat the peach, "
				+ "Lin-san?\"  <br/>  <br/>  Lin: \"Go right "
				+ "ahead.\"  <br/>  <br/>  Puro: \"Thank "
				+ "you...\"  <br/>  <br/>  Puro: \"LickLickLick"
				+ "LickLick....\"  <br/>  <br/>  Lin: *blushing*"
				+ " \"Yare Yare Daze...\"";
		assertEquals(value, dvk.get_description());
		assertEquals(33, dvk.get_web_tags().length);
		assertEquals("Rating:General", dvk.get_web_tags()[0]);
		assertEquals("Category:Artwork (Digital)",
				dvk.get_web_tags()[1]);
		assertEquals("Type:Fanart", dvk.get_web_tags()[2]);
		assertEquals("Species:Wolf", dvk.get_web_tags()[3]);
		assertEquals("Gender:Male", dvk.get_web_tags()[4]);
		assertEquals("Gallery:Main", dvk.get_web_tags()[5]);
		assertEquals("Personal Works", dvk.get_web_tags()[6]);
		assertEquals("dumb memes i made", dvk.get_web_tags()[7]);
		assertEquals("Changed", dvk.get_web_tags()[8]);
		assertEquals("latex", dvk.get_web_tags()[9]);
		assertEquals("wolf", dvk.get_web_tags()[10]);
		value = "Changed fanart Are you going to eat "
				+ "that Peach human_FAF29756524.dvk";
		assertEquals(value, dvk.get_dvk_file().getName());
		assertEquals(this.test_dir,
				dvk.get_dvk_file().getParentFile());
		value = "Changed fanart Are you going to eat "
				+ "that Peach human_FAF29756524.png";
		assertEquals(value, dvk.get_media_file().getName());
		assertEquals(this.test_dir,
				dvk.get_media_file().getParentFile());
		assertEquals(null, dvk.get_secondary_file());
		if(this.fur.is_logged_in()) {
			//MATURE DVK
			url = "www.furaffinity.net/view/13634433/";
			dvk = this.fur.get_dvk(url, this.test_dir, "Person", false, false);
			assertEquals("FAF13634433", dvk.get_id());
			assertEquals("spiritual feedback - 3",
					dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("angrboda", dvk.get_artists()[0]);
			assertEquals("2014/06/03|20:51", dvk.get_time());
			value = "https://www.furaffinity.net/view/13634433/";
			assertEquals(value, dvk.get_page_url());
			value = "https://d.facdn.net/art/angrboda/1401843083"
					+ "/1401843083.angrboda_cougars_small_3.jpg";
			assertEquals(value, dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			value = "comm 3/3 from last year for   <a href="
					+ "\"/user/talent\" class=\"linkusername"
					+ "\">    talent  </a>  , who wrote the "
					+ "text.";
			assertEquals(value, dvk.get_description());
			assertEquals(16, dvk.get_web_tags().length);
			assertEquals("Rating:Adult", dvk.get_web_tags()[0]);
			assertEquals("Category:All", dvk.get_web_tags()[1]);
			assertEquals("Type:Transformation",
					dvk.get_web_tags()[2]);
			assertEquals("Species:Cougar", dvk.get_web_tags()[3]);
			assertEquals("Gender:Multiple characters", dvk.get_web_tags()[4]);
			assertEquals("Gallery:Main", dvk.get_web_tags()[5]);
			assertEquals("tf", dvk.get_web_tags()[6]);
			assertEquals("transformation", dvk.get_web_tags()[7]);
			assertEquals("tg", dvk.get_web_tags()[8]);
			assertEquals("transgender", dvk.get_web_tags()[9]);
			assertEquals("cougar", dvk.get_web_tags()[10]);
			assertEquals("mountain", dvk.get_web_tags()[11]);
			assertEquals("lion", dvk.get_web_tags()[12]);
			assertEquals("sequence", dvk.get_web_tags()[13]);
			assertEquals("magic", dvk.get_web_tags()[14]);
			assertEquals("Favorite:Person", dvk.get_web_tags()[15]);
			value = "spiritual feedback - 3_FAF13634433.dvk";
			assertEquals(value,
					dvk.get_dvk_file().getName());
			assertEquals(this.test_dir,
					dvk.get_dvk_file().getParentFile());
			value = "spiritual feedback - 3_FAF13634433.jpg";
			assertEquals(value,
					dvk.get_media_file().getName());
			assertEquals(this.test_dir,
					dvk.get_media_file().getParentFile());
			assertEquals(null, dvk.get_secondary_file());
		}
	}
	
	/**
	 * Tests the get_journal_dvk method.
	 */
	@Test
	public void test_get_journal_dvk() {
		//FIRST DVK
		String url = "www.furaffinity.net/journal/8594821";
		Dvk dvk = this.fur.get_journal_dvk(url, this.test_dir, false, false);
		assertEquals("FAF8594821-J", dvk.get_id());
		assertEquals("100+ watcher special", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("MrSparta", dvk.get_artists()[0]);
		assertEquals("2018/01/24|15:50", dvk.get_time());
		String value = "https://www.furaffinity.net/journal/8594821/";
		assertEquals(value, dvk.get_page_url());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		value = "This actually isn't that special, but thanks. Also, "
				+ "one of my stories broke 1000 views earlier this week. "
				+ "Good times.  <br/>  <br/>  So what do you guys actually "
				+ "want to see from me? I'm mostly just trying fetishes "
				+ "to see which get a lot of views, and working from there."
				+ "   <br/>  <br/>  Also I have a patreon that I assure you "
				+ "has the highest quality.";
		assertEquals(value, dvk.get_description());
		assertTrue(dvk.get_web_tags() == null);
		assertEquals("100 watcher special_FAF8594821-J.dvk",
				dvk.get_dvk_file().getName());
		assertEquals(this.test_dir,
				dvk.get_dvk_file().getParentFile());
		assertEquals("100 watcher special_FAF8594821-J.html",
				dvk.get_media_file().getName());
		assertEquals(this.test_dir,
				dvk.get_media_file().getParentFile());
		assertEquals(null, dvk.get_secondary_file());
		//SECOND DVK
		url = "https://www.furaffinity.net/journal/4743500/";
		dvk = this.fur.get_journal_dvk(url, this.test_dir, true, false);
		assertEquals("FAF4743500-J", dvk.get_id());
		assertEquals("CLOSED $35 quick color pinups - 4 spots",
				dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("angrboda", dvk.get_artists()[0]);
		assertEquals("2013/06/15|19:59", dvk.get_time());
		value = "https://www.furaffinity.net/journal/4743500/";
		assertEquals(value, dvk.get_page_url());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		value = "Edit - closed, thanks everyone. Sorry the spots "
				+ "went so fast!  <br/>  <br/>  <hr class=\""
				+ "bbcode bbcode_hr\"/>  <br/>  <br/>  Just "
				+ "finished up a bunch of big stuff, taking 4 "
				+ "little ones to unwind. Same rules as always:  "
				+ "<br/>  <br/>  Examples here:  <br/>  <a href=\""
				+ "http://www.furaffinity.net/view/10852541/\" "
				+ "title=\"http://www.furaffinity.net/view/10852541"
				+ "/\" class=\"auto_link\">    http://www.furaffinity"
				+ ".net/view/10852541/  </a>  <br/>  <a href=\""
				+ "http://www.furaffinity.net/view/10546296/\" "
				+ "title=\"http://www.furaffinity.net/view/10546296/"
				+ "\" class=\"auto_link\">    http://www.furaffinity"
				+ ".net/view/10546296/  </a>  <br/>  <br/>  1 character,"
				+ " no background, simple colors. No proofs given, "
				+ "final files delivered via note. As always, regular "
				+ "TOS applies. Minor changes can be made if necessary."
				+ "  <br/>  <br/>  Can be general to adult (please "
				+ "specify if your request seems ambiguous)  <br/>  "
				+ "<br/>  Please give a link to a visual ref or a good "
				+ "written descrip, as well as the type of pose you're "
				+ "looking for.  <br/>  <br/>  These will be pay on "
				+ "delivery; I will note you when your pic is complete "
				+ "and give you my payment info, and you will receive "
				+ "the file after payment clears.  <br/>  <br/>  1)   "
				+ "<a href=\"/user/rusvul\" class=\"linkusername\">    "
				+ "rusvul  </a>  <br/>  2)   <a href=\"/user/stormkern\" "
				+ "class=\"linkusername\">    stormkern  </a>  <br/>  3)"
				+ "   <a href=\"/user/aryte\" class=\"linkusername\"> "
				+ "   aryte  </a>  <br/>  4)   <a href=\"/user/avios\" "
				+ "class=\"linkusername\">    avios  </a>";
		assertEquals(value, dvk.get_description());
		assertEquals(1, dvk.get_web_tags().length);
		assertEquals("DVK:Single", dvk.get_web_tags()[0]);
		assertEquals("CLOSED 35 quick color pinups - 4 spots_FAF4743500-J.dvk",
				dvk.get_dvk_file().getName());
		assertEquals(this.test_dir,
				dvk.get_dvk_file().getParentFile());
		assertEquals("CLOSED 35 quick color pinups - 4 spots_FAF4743500-J.html",
				dvk.get_media_file().getName());
		assertEquals(this.test_dir,
				dvk.get_media_file().getParentFile());
		assertEquals(null, dvk.get_secondary_file());
	}
}
