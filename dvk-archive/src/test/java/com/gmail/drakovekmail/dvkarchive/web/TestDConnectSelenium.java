package com.gmail.drakovekmail.dvkarchive.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.DomElement;

/**
 * Unit tests for the DConnectSelenium class.
 * 
 * @author Drakovek
 */
public class TestDConnectSelenium {

	/**
	 * DConnectSelenuim object for running web tests.
	 */
	private DConnectSelenium connect;

	/**
	 * Directory for holding test files.
	 */
	private File test_dir;

	/**
	 * Sets up objects for testing.
	 */
	@Before
	public void set_up() {
		this.connect = new DConnectSelenium(true);
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
		this.connect.close_driver();
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
	}
	
	/**
	 * Tests the load_page and get_page methods.
	 */
	@Test
	public void test_load_get_page() {
		DomElement de;
		//TEST INVALID URL
		this.connect.load_page(null, null, 1, 10);
		assertEquals(null, this.connect.get_page());
		String url = "lkjslkdjflajs";
		this.connect.load_page(url, null, 1, 10);
		assertEquals(null, this.connect.get_page());
		//TEST VALID URL
		url = "http://pythonscraping.com/exercises/exercise1.html";
		this.connect.load_page(url, null, 1, 10);
		assertNotEquals(null, this.connect.get_page());
		de = this.connect.get_page().getFirstByXPath("//h1");
		assertEquals("An Interesting Title", de.asText());
		de = this.connect.get_page().getFirstByXPath("//title");
		assertEquals("A Useful Page", de.asText());
		//TEST AJAX WAITING
		url = "http://pythonscraping.com/pages/javascript/ajaxDemo.html";
		this.connect.load_page(url, "//button[@id='loadedButton']", 2, 10);
		assertNotEquals(null, this.connect.get_page());
		de = this.connect.get_page().getFirstByXPath("//button[@id='loadedButton']");
		assertEquals("A button to click!", de.asText());
		//TEST INVALID ELEMENT
		url = "http://pythonscraping.com/exercises/exercise1.html";
		this.connect.load_page(url, "//a[href='non-existant']", 2, 10);
		assertEquals(null, this.connect.get_page());
	}
}
