package com.gmail.drakovekmail.dvkarchive.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
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
		StartGUI gui = new StartGUI(base_gui, false);
		String[] categories = gui.get_categories(false);
		assertEquals(4, categories.length);
		assertEquals("artist_hosting", categories[0]);
		assertEquals("comics", categories[1]);
		assertEquals("error_finding", categories[2]);
		assertEquals("reformatting", categories[3]);
		//TEST CATEGORY LANGUAGE STRINGS
		categories = gui.get_categories(true);
		assertEquals(4, categories.length);
		assertEquals("Artist Hosting", categories[0]);
		assertEquals("Comics", categories[1]);
		assertEquals("Error Finding", categories[2]);
		assertEquals("Reformatting", categories[3]);
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
		String[] services;
		services = StartGUI.get_services("artist_hosting");
		assertEquals(3, services.length);
		assertEquals("deviantart", services[0]);
		assertEquals("fur_affinity", services[1]);
		assertEquals("inkbunny", services[2]);
		//TEST ERROR FINDING
		services = StartGUI.get_services("error_finding");
		assertEquals(3, services.length);
		assertEquals("same_ids", services[0]);
		assertEquals("missing_media", services[1]);
		assertEquals("unlinked_media", services[2]);
	}
	
	/**
	 * Tests the reset_directory method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_reset_directory() {
		//RESET DIRECTORY
		BaseGUI base_gui = new BaseGUI();
		StartGUI gui = new StartGUI(base_gui, false);
		gui.reset_directory();
		assertEquals(null, gui.get_directory());
	}
	
	/**
	 * Tests the get_directory and set_directory methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_directory() {
		//RESET DIRECTORY
		BaseGUI base_gui = new BaseGUI();
		StartGUI gui = new StartGUI(base_gui, false);
		gui.reset_directory();
		//TEST INVALID FILE WHILE NULL
		gui.set_directory(new File("ksdlkfjw"));
		assertEquals(null, gui.get_directory());
		//TEST VALID FILE
		String user_dir = System.getProperty("user.dir");
		File dir = new File(user_dir);
		assertTrue(dir.isDirectory());
		gui.set_directory(dir);
		assertEquals(dir.getAbsolutePath(),
				gui.get_directory().getAbsolutePath());
		//TEST INVALLID FILES
		gui.set_directory(null);
		assertEquals(dir.getAbsolutePath(),
				gui.get_directory().getAbsolutePath());
		gui.set_directory(new File("kjskdjfklsjw"));
		assertEquals(dir.getAbsolutePath(),
				gui.get_directory().getAbsolutePath());
	}
}
