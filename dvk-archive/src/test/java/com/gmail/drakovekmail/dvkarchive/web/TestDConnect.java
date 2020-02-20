package com.gmail.drakovekmail.dvkarchive.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.html.DomElement;

/**
 * Unit tests for the DConnect class.
 * 
 * @author Drakovek
 */
public class TestDConnect {

	/**
	 * DConnect object for running web tests.
	 */
	private DConnect connect;

	/**
	 * Directory for holding test files.
	 */
	private File test_dir;

	/**
	 * Sets up objects for testing.
	 */
	@Before
	public void set_up() {
		this.connect = new DConnect(false, false);
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "connect_dir");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
	}
	
	/**
	 * Sets up objects for testing.
	 */
	@After
	public void tear_down() {
		this.connect.close_client();
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the load_from_string method.
	 */
	@Test
	public void test_load_from_string() {
		//TEST INVALID STRINGS
		this.connect.load_from_string(null);
		assertEquals(null, this.connect.get_page());
		this.connect.load_from_string("");
		assertEquals(null, this.connect.get_page());
		//TEST HTML
		DomElement de;
		String html = "<html><h1>Text!</h1></html>";
		this.connect.load_from_string(html);
		assertNotEquals(null, this.connect.get_page());
		de = this.connect.get_page().getFirstByXPath("//h1");
		assertEquals("Text!", de.asText());
	}
	
	/**
	 * Tests the load_page and get_page methods.
	 */
	@Test
	public void test_load_get_page() {
		DomElement de;
		//TEST INVALID URL
		this.connect.load_page(null, null);
		assertEquals(null, this.connect.get_page());
		String url = "lkjslkdjflajs";
		this.connect.load_page(url, null);
		assertEquals(null, this.connect.get_page());
		//TEST VALID URL
		url = "http://pythonscraping.com/exercises/exercise1.html";
		this.connect.load_page(url, null);
		assertNotEquals(null, this.connect.get_page());
		de = this.connect.get_page().getFirstByXPath("//h1");
		assertEquals("An Interesting Title", de.asText());
		de = this.connect.get_page().getFirstByXPath("//title");
		assertEquals("A Useful Page", de.asText());
		//TEST AJAX WAITING
		url = "http://pythonscraping.com/pages/javascript/ajaxDemo.html";
		this.connect.initialize_client(false, true);
		this.connect.load_page(url, "//button[@id='loadedButton']");
		assertNotEquals(null, this.connect.get_page());
		de = this.connect.get_page().getFirstByXPath("//button[@id='loadedButton']");
		assertEquals("A button to click!", de.asText());
	}
	
	/**
	 * Tests the download method.
	 */
	@Test
	public void test_download() {
		File file = new File(this.test_dir, "image.jpg");
		this.connect.download(null, null, false);
		this.connect.download(null, file, false);
		assertFalse(file.exists());
		this.connect.download("kjsdf", file, false);
		assertFalse(file.exists());
		//DOWNLOAD VALID FILE
		String url = "http://www.pythonscraping.com/img/gifts/img5.jpg";
		this.connect.download(url, file, false);
		assertTrue(file.exists());
		assertEquals(33362L, file.length());
	}
	
	/**
	 * Tests the basic_download method.
	 */
	@Test
	public void test_basic_download() {
		File file = new File(this.test_dir, "image.jpg");
		DConnect.basic_download(null, null);
		DConnect.basic_download(null, file);
		assertFalse(file.exists());
		DConnect.basic_download("kjsdf", file);
		assertFalse(file.exists());
		//DOWNLOAD VALID FILE
		String url = "http://www.pythonscraping.com/img/gifts/img6.jpg";
		DConnect.basic_download(url, file);
		assertTrue(file.exists());
		assertEquals(39785L, file.length());
	}
	
	/**
	 * Tests the remove_header_footer method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_remove_header_footer() {
		assertEquals("", DConnect.remove_header_footer(""));
		assertEquals("", DConnect.remove_header_footer("  \n\r  "));
		assertEquals("ble>", DConnect.remove_header_footer("ble>"));
		assertEquals("<ble", DConnect.remove_header_footer("<ble"));
		assertEquals("", DConnect.remove_header_footer("<p>"));
		assertEquals("", DConnect.remove_header_footer("<head><foot>"));
		assertEquals("test", DConnect.remove_header_footer("<p>  test"));
		assertEquals("test", DConnect.remove_header_footer("test </p>"));
		assertEquals("Things", DConnect.remove_header_footer("<head> Things  \n\n </foot>"));
		assertEquals("<p>bleh</p>", DConnect.remove_header_footer("<div><p>bleh</p></div>"));
	}
}
