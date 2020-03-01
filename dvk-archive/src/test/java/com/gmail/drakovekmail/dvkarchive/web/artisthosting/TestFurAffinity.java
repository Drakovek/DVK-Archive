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
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "faftest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
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
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
		this.fur.close();
	}
	
	/**
	 * Tests the get_url_artists method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_url_artist() {
		assertEquals("artist", FurAffinity.get_url_artist("ArTiSt"));
		assertEquals("otherartistguy", FurAffinity.get_url_artist("Other_Artist_Guy"));
		assertEquals("thatisit", FurAffinity.get_url_artist("That is It"));
	}
	
	/**
	 * Tests the get_page_id method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_page_id() {
		String url = "https://www.different.net/view/13982138/";
		assertEquals("", FurAffinity.get_page_id(url));
		url = "https://www.furaffinity.net/bleh/13982138/";
		assertEquals("", FurAffinity.get_page_id(url));
		url = "https://www.furaffinity.net/view/13982138";
		assertEquals("FAF13982138", FurAffinity.get_page_id(url));
		url = "furaffinity.net/view/22840286/";
		assertEquals("FAF22840286", FurAffinity.get_page_id(url));
		url = "https://www.furaffinity.net/journal/8610682";
		assertEquals("FAF8610682-J", FurAffinity.get_page_id(url));
		url = "furaffinity.net/journal/8594821/";
		assertEquals("FAF8594821-J", FurAffinity.get_page_id(url));
		url = "https://www.furaffinity.net/view/bleh";
		assertEquals("FAFbleh", FurAffinity.get_page_id(url));
		url = "https://www.furaffinity.net/journal/bleh";
		assertEquals("FAFbleh-J", FurAffinity.get_page_id(url));
		url = "https://www.furaffinity.net/view/";
		assertEquals("FAF", FurAffinity.get_page_id(url));
		url = "https://www.furaffinity.net/journal/";
		assertEquals("FAF-J", FurAffinity.get_page_id(url));
	}
	
	/**
	 * Tests the get_time method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_time() {
		//INVALID
		assertEquals("0000/00/00|00:00", FurAffinity.get_time("1, 2020 10:23 PM"));
		assertEquals("0000/00/00|00:00", FurAffinity.get_time("Jan 2020 10:23 PM"));
		assertEquals("0000/00/00|00:00", FurAffinity.get_time("Jan 1, 10:23 PM"));
		assertEquals("0000/00/00|00:00", FurAffinity.get_time("Jan 1, 2020 23 PM"));
		assertEquals("0000/00/00|00:00", FurAffinity.get_time("Jan 1, 2020 10 PM"));
		assertEquals("0000/00/00|00:00", FurAffinity.get_time("Jan 1, 2020 10:23 "));
		//TEST ALL MONTHS
		assertEquals("2020/01/01|22:23", FurAffinity.get_time(" Jan  1,  2020  10:23 PM  "));
		assertEquals("1997/02/13|09:05", FurAffinity.get_time("FEB  13, 1997  9:05 Am"));
		assertEquals("2005/03/12|00:15", FurAffinity.get_time("mar  012,   2005  12:15 am"));
		assertEquals("2016/04/08|12:05", FurAffinity.get_time("aPr  08, 2016  012:05 pm"));
		assertEquals("2016/05/15|13:12", FurAffinity.get_time("May  15, 2016  1:12 Pm"));
		assertEquals("2016/06/08|01:05", FurAffinity.get_time("jun  08, 2016  1:05  Am"));
		assertEquals("2016/07/08|01:05", FurAffinity.get_time("Jul  08, 2016  1:05  Am"));
		assertEquals("2016/08/08|01:05", FurAffinity.get_time("aug  08, 2016  1:05  Am"));
		assertEquals("2016/09/08|01:05", FurAffinity.get_time("seP  08, 02016  1:005  Am"));
		assertEquals("2016/10/08|01:05", FurAffinity.get_time("Oct  08, 2016  1:05  Am"));
		assertEquals("2016/11/08|01:05", FurAffinity.get_time("nOv 08, 2016  1:05  Am"));
		assertEquals("2016/12/08|01:05", FurAffinity.get_time("dec  08, 2016  1:05  Am"));
		assertEquals("0000/00/00|00:00", FurAffinity.get_time("jug  08, 2016  1:05  Am"));
	}
	
	/**
	 * Tests the get_pages method.
	 */
	@Test
	public void test_get_pages() {
		this.fur.initialize_connect();
		//CREATE DVK
		Dvk dvk = new Dvk();
		dvk.set_dvk_file(new File(this.test_dir, "rabbit.dvk"));
		dvk.set_id("FAF13982138");
		dvk.set_title("Rabbit in the city");
		dvk.set_artist("MrSparta");
		dvk.set_page_url("https://www.furaffinity.net/view/13982138/");
		dvk.set_media_file("rabbit.png");
		dvk.write_dvk();
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		//TEST SMALL SAMPLE
		ArrayList<String> links = this.fur.get_pages("drakovek", false, handler, true, false, 1);
		assertEquals(1, links.size());
		assertEquals("https://www.furaffinity.net/view/32521285/", links.get(0));
		links = this.fur.get_pages("drakovek", true, handler, false, false, 1);
		assertEquals(1, links.size());
		assertEquals("https://www.furaffinity.net/view/31071186/", links.get(0));
		//TEST ALREADY DOWNLOADED
		links = this.fur.get_pages("mrsparta", false, handler, false, false, 1);
		assertTrue(links.size() > 94);
		int index = -1;
		for(int i = 0; i < links.size(); i++) {
			assertNotEquals("https://www.furaffinity.net/view/13982138/", links.get(i));
			if(links.get(i).equals("https://www.furaffinity.net/view/14019897/")) {
				index = i;
			}
		}
		assertNotEquals(-1, index);
		assertEquals("https://www.furaffinity.net/view/14202184/", links.get(index - 1));
		assertEquals("https://www.furaffinity.net/view/14354843/", links.get(index - 2));
		assertEquals("https://www.furaffinity.net/view/14664720/", links.get(index - 3));
		//TEST LOGIN
		links = this.fur.get_pages("mrsparta", false, handler, false, true, 1);
		assertEquals(0, links.size());
	}
	
	/**
	 * Tests the get_dvk method.
	 */
	@Test
	public void test_get_dvk() {
		this.fur.initialize_connect();
		for(int pass = 0; pass < 2; pass++) {
			//FIRST DVK
			String url = "https://www.furaffinity.net/view/32521285/";
			Dvk dvk = this.fur.get_dvk(url, this.test_dir, false);
			assertEquals("FAF32521285", dvk.get_id());
			assertEquals("Robin the Bobcat", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Drakovek", dvk.get_artists()[0]);
			assertEquals("2019/08/03|18:21", dvk.get_time());
			assertEquals("https://www.furaffinity.net/view/32521285/", dvk.get_page_url());
			assertEquals("https://d.facdn.net/art/drakovek/1564867315/1564867315.drakovek_robin.png", dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Character portrait for Robin, a bobcat character from a story I've had in my head for a long time that may or may not ever be written down. We'll see.", dvk.get_description());
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
			assertEquals("Robin the Bobcat_FAF32521285.dvk", dvk.get_dvk_file().getName());
			assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
			assertEquals("Robin the Bobcat_FAF32521285.png", dvk.get_media_file().getName());
			assertEquals(this.test_dir, dvk.get_media_file().getParentFile());
			assertEquals(null, dvk.get_secondary_file());
			//SECOND DVK
			url = "https://www.furaffinity.net/view/15301779/";
			dvk = this.fur.get_dvk(url, this.test_dir, false);
			assertEquals("FAF15301779", dvk.get_id());
			assertEquals("Affinity Ch. 1", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("MrSparta", dvk.get_artists()[0]);
			assertEquals("2014/12/24|03:11", dvk.get_time());
			assertEquals("https://www.furaffinity.net/view/15301779/", dvk.get_page_url());
			assertEquals("https://d.facdn.net/art/mrsparta/stories/1488278723/1419405086.mrsparta_md-1.txt", dvk.get_direct_url());
			assertEquals("https://d.facdn.net/art/mrsparta/stories/1488278723/1419405086.thumbnail.mrsparta_md-1.txt.gif", dvk.get_secondary_url());
			assertEquals("I'm trying something new. It's a strange practice called \"character development\"", dvk.get_description());
			assertEquals(15, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Category:Story", dvk.get_web_tags()[1]);
			assertEquals("Type:All", dvk.get_web_tags()[2]);
			assertEquals("Species:Unspecified / Any", dvk.get_web_tags()[3]);
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
			assertEquals("Affinity Ch 1_FAF15301779.dvk", dvk.get_dvk_file().getName());
			assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
			assertEquals("Affinity Ch 1_FAF15301779.txt", dvk.get_media_file().getName());
			assertEquals(this.test_dir, dvk.get_media_file().getParentFile());
			assertEquals("Affinity Ch 1_FAF15301779.gif", dvk.get_secondary_file().getName());
			assertEquals(this.test_dir, dvk.get_secondary_file().getParentFile());
			//SECOND DVK
			url = "https://www.furaffinity.net/view/29756524/";
			dvk = this.fur.get_dvk(url, this.test_dir, false);
			assertEquals("FAF29756524", dvk.get_id());
			assertEquals("[Changed fanart] Are you going to eat that Peach, human?", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Tavaer", dvk.get_artists()[0]);
			assertEquals("2018/12/16|01:02", dvk.get_time());
			assertEquals("https://www.furaffinity.net/view/29756524/", dvk.get_page_url());
			assertEquals("https://d.facdn.net/art/tavaer/1544936555/1544936555.tavaer_purero.png", dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Puro: \"Are you going to eat the peach, Lin-san?\"  <br/>  <br/>  Lin: \"Go right ahead.\"  <br/>  <br/>  Puro: \"Thank you...\"  <br/>  <br/>  Puro: \"LickLickLickLickLick....\"  <br/>  <br/>  Lin: *blushing* \"Yare Yare Daze...\"", dvk.get_description());
			assertEquals(33, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Category:Artwork (Digital)", dvk.get_web_tags()[1]);
			assertEquals("Type:Fanart", dvk.get_web_tags()[2]);
			assertEquals("Species:Wolf", dvk.get_web_tags()[3]);
			assertEquals("Gender:Male", dvk.get_web_tags()[4]);
			assertEquals("Gallery:Main", dvk.get_web_tags()[5]);
			assertEquals("Personal Works", dvk.get_web_tags()[6]);
			assertEquals("dumb memes i made", dvk.get_web_tags()[7]);
			assertEquals("Changed", dvk.get_web_tags()[8]);
			assertEquals("latex", dvk.get_web_tags()[9]);
			assertEquals("wolf", dvk.get_web_tags()[10]);
			assertEquals("Changed fanart Are you going to eat that Peach human_FAF29756524.dvk", dvk.get_dvk_file().getName());
			assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
			assertEquals("Changed fanart Are you going to eat that Peach human_FAF29756524.png", dvk.get_media_file().getName());
			assertEquals(this.test_dir, dvk.get_media_file().getParentFile());
			assertEquals(null, dvk.get_secondary_file());
			//MATURE DVK
			url = "https://www.furaffinity.net/view/34705077/";
			dvk = this.fur.get_dvk(url, this.test_dir, false);
			if(pass == 0) {
				assertEquals(null, dvk.get_title());
			}
			else {
				assertEquals("FAF34705077", dvk.get_id());
				assertEquals("Here comes Mom!", dvk.get_title());
				assertEquals(1, dvk.get_artists().length);
				assertEquals("MrSparta", dvk.get_artists()[0]);
				assertEquals("2020/01/21|20:39", dvk.get_time());
				assertEquals("https://www.furaffinity.net/view/34705077/", dvk.get_page_url());
				assertEquals("https://d.facdn.net/art/mrsparta/1579653555/1579653555.mrsparta_figuretest.jpg", dvk.get_direct_url());
				assertEquals(null, dvk.get_secondary_url());
				assertEquals("You know things are serious when someone rips a traffic light out of the ground to beat your ass.", dvk.get_description());
				assertEquals(23, dvk.get_web_tags().length);
				assertEquals("Rating:Adult", dvk.get_web_tags()[0]);
				assertEquals("Category:All", dvk.get_web_tags()[1]);
				assertEquals("Type:General Furry Art", dvk.get_web_tags()[2]);
				assertEquals("Species:Bear", dvk.get_web_tags()[3]);
				assertEquals("Gender:Female", dvk.get_web_tags()[4]);
				assertEquals("Gallery:Main", dvk.get_web_tags()[5]);
				assertEquals("grizzly", dvk.get_web_tags()[6]);
				assertEquals("brown", dvk.get_web_tags()[7]);
				assertEquals("bear", dvk.get_web_tags()[8]);
				assertEquals("mom", dvk.get_web_tags()[9]);
				assertEquals("milf", dvk.get_web_tags()[10]);
				assertEquals("boobs", dvk.get_web_tags()[11]);
				assertEquals("breasts", dvk.get_web_tags()[12]);
				assertEquals("tits", dvk.get_web_tags()[13]);
				assertEquals("pussy", dvk.get_web_tags()[14]);
				assertEquals("vagina", dvk.get_web_tags()[15]);
				assertEquals("nude", dvk.get_web_tags()[16]);
				assertEquals("female", dvk.get_web_tags()[17]);
				assertEquals("girl", dvk.get_web_tags()[18]);
				assertEquals("woman", dvk.get_web_tags()[19]);
				assertEquals("big", dvk.get_web_tags()[20]);
				assertEquals("thicc", dvk.get_web_tags()[21]);
				assertEquals("amazon", dvk.get_web_tags()[22]);
				assertEquals("Here comes Mom_FAF34705077.dvk", dvk.get_dvk_file().getName());
				assertEquals(this.test_dir, dvk.get_dvk_file().getParentFile());
				assertEquals("Here comes Mom_FAF34705077.jpg", dvk.get_media_file().getName());
				assertEquals(this.test_dir, dvk.get_media_file().getParentFile());
				assertEquals(null, dvk.get_secondary_file());
			}
			//LOGIN
			if(pass == 0) {
				File cap_file = this.fur.get_captcha();
				String[] info = this.fur.get_user_info(cap_file);
				this.fur.login(info[0], info[1], info[2]);
				info = null;
				assertTrue(this.fur.is_logged_in());
			}	
		}
	}
}
