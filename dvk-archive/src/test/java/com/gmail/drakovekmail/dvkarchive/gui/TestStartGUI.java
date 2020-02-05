package com.gmail.drakovekmail.dvkarchive.gui;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;

/**
 * Unit tests for the StartGUI class.
 * 
 * @author Drakovek
 */
public class TestStartGUI {
	
	/**
	 * Tests the get_categories method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_categories() {
		//SET LANGUAGE TO ENGLISH
		LanguageHandler language;
		BaseGUI base_gui = new BaseGUI();
		language = base_gui.get_language_handler();
		language.set_language("English");
		//TEST CATEGORY IDS
		StartGUI gui = new StartGUI(base_gui);
		String[] categories = gui.get_categories(false);
		assertEquals(2, categories.length);
		assertEquals("artist_hosting", categories[0]);
		assertEquals("error_finding", categories[1]);
		//TEST CATEGORY LANGUAGE STRINGS
		categories = gui.get_categories(true);
		assertEquals(2, categories.length);
		assertEquals("Artist Hosting", categories[0]);
		assertEquals("Error Finding", categories[1]);
		base_gui = null;
	}
	
	/**
	 * Tests the get_services method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_services() {
		//SET LANGUAGE TO ENGLISH
		LanguageHandler language;
		BaseGUI base_gui = new BaseGUI();
		language = base_gui.get_language_handler();
		language.set_language("English");
		//TEST ARTIST HOSTING
		StartGUI gui = new StartGUI(base_gui);
		String[] services;
		services = gui.get_services("artist_hosting", false);
		assertEquals(4, services.length);
		assertEquals("deviantart", services[0]);
		assertEquals("fur_affinity", services[1]);
		assertEquals("inkbunny", services[2]);
		assertEquals("transfur", services[3]);
		//TEST ERROR FINDING
		services = gui.get_services("error_finding", true);
		assertEquals(3, services.length);
		assertEquals("Same IDs", services[0]);
		assertEquals("Missing Media", services[1]);
		assertEquals("Unlinked Media", services[2]);
		gui = null;
	}
}
