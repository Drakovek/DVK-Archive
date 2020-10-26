package com.gmail.drakovekmail.dvkarchive.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;

/**
 * Unit tests for the DConnectSelenium class.
 * 
 * @author Drakovek
 */
public class TestDConnectSelenium {

	/**
	 * Directory for holding test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * DConnectSelenuim object for running web tests.
	 */
	private DConnectSelenium connect;

	/**
	 * Sets up objects for testing.
	 */
	@Before
	public void set_up() {
		try {
			this.connect = new DConnectSelenium(true, null);
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
