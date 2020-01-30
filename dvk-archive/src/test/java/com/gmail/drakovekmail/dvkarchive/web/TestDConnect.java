package com.gmail.drakovekmail.dvkarchive.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	 * Sets up objects for testing.
	 */
	@Before
	public void set_up() {
		this.connect = new DConnect();
	}
	
	/**
	 * Sets up objects for testing.
	 */
	@After
	public void tear_down() {
		this.connect.close_client();
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
		if(this.connect.get_page() != null) {
			de = this.connect.get_page().getFirstByXPath("//h1");
			assertEquals("An Interesting Title", de.asText());
			de = this.connect.get_page().getFirstByXPath("//title");
			assertEquals("A Useful Page", de.asText());
		}
		else {
			assertTrue(false);
		}
		//TEST AJAX WAITING
		url = "http://pythonscraping.com/pages/javascript/ajaxDemo.html";
		this.connect.load_page(url, "//button[@id='loadedButton']");
		if(this.connect.get_page() != null) {
			de = this.connect.get_page().getFirstByXPath("//button[@id='loadedButton']");
			assertEquals("A button to click!", de.asText());
		}
		else {
			assertTrue(false);
		}
	}
}
