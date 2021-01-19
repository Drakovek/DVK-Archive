package com.gmail.drakovekmail.dvkarchive.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;

/**
 * Unit tests for the DConnect class.
 * 
 * @author Drakovek
 */
public class TestDConnect {

	/**
	 * Directory for holding test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * DConnect object for running web tests.
	 */
	private DConnect connect;

	/**
	 * Sets up objects for testing.
	 */
	@Before
	public void set_up() {
		try {
			this.connect = new DConnect(false, false);
		}
		catch(DvkException e) {}
	}
	
	/**
	 * Sets up objects for testing.
	 */
	@After
	public void tear_down() {
		try {
			this.connect.close();
		}
		catch(DvkException e) {}
	}
	
	/**
	 * Tests the load_page and get_page methods.
	 */
	@Test
	public void test_load_get_page() {
		//TEST LOADING ELEMENTS FROM A WEB PAGE
		String url = "http://pythonscraping.com/exercises/exercise1.html";
		this.connect.load_page(url, null, 2);
		assertNotEquals(null, this.connect.get_page());
		DomElement de = this.connect.get_page().getFirstByXPath("//h1");
		assertEquals("An Interesting Title", de.asText());
		de = this.connect.get_page().getFirstByXPath("//title");
		assertEquals("A Useful Page", de.asText());
		//TEST WAITING FOR ELEMENT
		this.connect.set_timeout(5);
		url = "http://pythonscraping.com/pages/javascript/ajaxDemo.html";
		this.connect.initialize_client(false, true);
		this.connect.load_page(url, "//button[@id='loadedButton']", 2);
		assertNotEquals(null, this.connect.get_page());
		de = this.connect.get_page().getFirstByXPath("//button[@id='loadedButton']");
		assertEquals("A button to click!", de.asText());
		//TEST WAITING FOR NON-EXISTANT ELEMENT
		url = "http://pythonscraping.com/exercises/exercise1.html";
		this.connect.load_page(url, "//a[href='non-existant']", 2);
		assertEquals(null, this.connect.get_page());
		//TEST LOADING WITH INVALID URL
		this.connect.load_page(null, null, 0);
		assertEquals(null, this.connect.get_page());
		url = "qwertyuiopasdfghjkl";
		this.connect.load_page(url, null, 1);
		assertEquals(null, this.connect.get_page());
	}
	
	/**
	 * Tests the load_json method.
	 */
	@Test
	public void test_load_json() {
		try {
			//TEST LOADING PAGE AS A JSON OBJECT
			JSONObject json;
			json = this.connect.load_json("http://echo.jsontest.com/key/value/json/test", 2);
			assertEquals("test", json.getString("json"));
			assertEquals("value", json.getString("key"));
			//TEST LOADING AN INVALID PAGE
			json = this.connect.load_json("asdsfghjkl", 1);
			assertEquals(null, json);
			json = this.connect.load_json(null, 1);
			assertEquals(null, json);
		}
		catch(JSONException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the download method.
	 */
	@Test
	public void test_download() {
		//TEST DOWNLOADING A GIVEN FILE
		File file = new File(this.temp_dir.getRoot(), "image.jpg");
		String url = "http://www.pythonscraping.com/img/gifts/img6.jpg";
		this.connect.download(url, file);
		assertTrue(file.exists());
		assertEquals(39785L, file.length());
		//TEST DOWNLOADING WITH INVALID PARAMETERS
		file = new File(this.temp_dir.getRoot(), "invalid.jpg");
		this.connect.download(null, null);
		assertFalse(file.exists());
		this.connect.download(null, file);
		assertFalse(file.exists());
		this.connect.download("asdfasdf", file);
		assertFalse(file.exists());
		this.connect.download(url, null);
		assertFalse(file.exists());
	}
	
	/**
	 * Tests the basic_download method.
	 */
	@Test
	public void test_basic_download() {
		//TEST DOWNLOADING A GIVEN FILE
		File file = new File(this.temp_dir.getRoot(), "image.jpg");
		String url = "http://www.pythonscraping.com/img/gifts/img6.jpg";
		DConnect.basic_download(url, file);
		assertTrue(file.exists());
		assertEquals(39785L, file.length());
		//TEST DOWNLOADING WITH INVALID PARAMETERS
		file = new File(this.temp_dir.getRoot(), "invalid.jpg");
		DConnect.basic_download(null, null);
		assertFalse(file.exists());
		DConnect.basic_download(null, file);
		assertFalse(file.exists());
		DConnect.basic_download("kjsdf", file);
		assertFalse(file.exists());
		DConnect.basic_download(url, null);
		assertFalse(file.exists());
	}
	
	/**
	 * Tests the load_from_string method.
	 */
	@Test
	public void test_load_from_string() {
		//TEST GETTING ELEMENTS FROM PAGE LOADED FROM HTML STRING
		String html = "<html><h1>Test!</h1><p>other</p></html>";
		this.connect.load_from_string(html);
		assertNotEquals(null, this.connect.get_page());
		DomElement de = this.connect.get_page().getFirstByXPath("//h1");
		assertEquals("Test!", de.asText());
		de = this.connect.get_page().getFirstByXPath("//p");
		assertEquals("other", de.asText());
		//TEST LOADING INVALID HTML STRINGS
		this.connect.load_from_string(null);
		assertEquals(null, this.connect.get_page());
		this.connect.load_from_string("");
		assertEquals(null, this.connect.get_page());
	}
}
