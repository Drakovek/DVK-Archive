package com.gmail.drakovekmail.dvkarchive.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Font;

import org.junit.Test;

import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;

/**
 * Unit tests for the BaseGUI class.
 * 
 * @author Drakovek
 */
public class TestBaseGUI {
	
	/**
	 * Tests the save_preferences and load_preferences methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_save_load_preferences() {
		//SAVE PREFERENCES
		BaseGUI base = new BaseGUI();
		base.set_font("Dialog", 25, true);
		base.set_theme("Nimbus");
		base.set_use_aa(false);
		base.save_preferences();
		base = null;
		//LOAD PREFERENCES
		base = new BaseGUI();
		base.load_preferences();
		Font font = base.get_font();
		assertEquals("Dialog", font.getFamily());
		assertEquals(25, font.getSize());
		assertTrue(font.isBold());
		assertEquals("Nimbus", base.get_theme());
		assertFalse(base.use_aa());
	}
	
	/**
	 * Tests the get_font and set_font methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_font() {
		BaseGUI base = new BaseGUI();
		base.set_font(null, 10, true);
		Font font = base.get_font();
		assertEquals("Dialog", font.getFamily());
		assertTrue(font.isBold());
		assertEquals(10, font.getSize());
		base.set_font("Dialog", 24, false);
		font = base.get_font();
		assertEquals("Dialog", font.getFamily());
		assertFalse(font.isBold());
		assertEquals(24, font.getSize());
	}
	
	/**
	 * Tests the set_use_aaa and use_aa methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void get_set_anti_aliasing() {
		BaseGUI base = new BaseGUI();
		base.set_use_aa(false);
		assertFalse(base.use_aa());
		base.set_use_aa(true);
		assertTrue(base.use_aa());
	}
	
	/**
	 * Tests the get_theme and set_theme methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void get_set_theme() {
		BaseGUI base = new BaseGUI();
		base.set_theme(null);
		assertEquals("", base.get_theme());
		base.set_theme("Metal");
		assertEquals("Metal", base.get_theme());
		base.set_theme("Nimbus");
		assertEquals("Nimbus", base.get_theme());
	}
	
	/**
	 * Tests the get_language_string method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_language_string() {
		BaseGUI base = new BaseGUI();
		LanguageHandler lang = base.get_language_handler();
		lang.set_language("English");
		assertEquals("^File", base.get_language_string("file"));
		assertEquals("", base.get_language_string("Nope"));
	}
	
	/**
	 * Tests the get_running and is_running methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_running() {
		BaseGUI base = new BaseGUI();
		base.set_running(true);
		assertTrue(base.is_running());
		base.set_running(false);
		assertFalse(base.is_running());
	}
	
	/**
	 * Tests the set_canceled and is_canceled methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_canceled() {
		BaseGUI base = new BaseGUI();
		base.set_canceled(true);
		assertTrue(base.is_canceled());
		base.set_canceled(false);
		assertFalse(base.is_canceled());
	}
}
