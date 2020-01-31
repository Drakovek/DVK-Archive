package com.gmail.drakovekmail.dvkarchive.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Font;

import org.junit.Test;

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
		BaseGUI base = new BaseGUI();
		base.set_font("Dialog", 25, true);
		base.save_preferences();
		base = null;
		base = new BaseGUI();
		base.load_preferences();
		Font font = base.get_font();
		assertEquals("Dialog", font.getFamily());
		assertEquals(25, font.getSize());
		assertTrue(font.isBold());
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
}
