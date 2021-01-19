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
			this.connect = new DConnectSelenium(true);
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
		url = "http://pythonscraping.com/pages/javascript/ajaxDemo.html";
		this.connect.load_page(url, "//button[@id='loadedButton']", 10);
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
}
