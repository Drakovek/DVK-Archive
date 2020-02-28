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
	 * Tests the get_captcha method.
	 */
	@Test
	public void test_get_captcha() {
		File captcha = this.fur.get_captcha();
		assertTrue(captcha.exists());
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
}
