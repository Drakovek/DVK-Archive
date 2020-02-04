package com.gmail.drakovekmail.dvkarchive.gui.language;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the LanguageHandler class.
 * 
 * @author Drakovek
 */
public class TestLanguageHandler {

	/**
	 * Tests the get_text method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_text() {
		String out = LanguageHandler.get_text("test");
		assertEquals("test", out);
		out = LanguageHandler.get_text("^Word");
		assertEquals("Word", out);
		out = LanguageHandler.get_text("T^hing");
		assertEquals("Thing", out);
		out = LanguageHandler.get_text("^Fi^nal^");
		assertEquals("Fi^nal^", out);
	}
	
	/**
	 * Tests the get_mnemonic_index method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_mnemonic_index() {
		int out;
		out = LanguageHandler.get_mnemonic_index("^Word");
		assertEquals(0, out);
		out = LanguageHandler.get_mnemonic_index("T^hing");
		assertEquals(1, out);
		out = LanguageHandler.get_mnemonic_index("Word^");
		assertEquals(4, out);
		out = LanguageHandler.get_mnemonic_index("N^e^xt");
		assertEquals(1, out);
		out = LanguageHandler.get_mnemonic_index("Word");
		assertEquals(0, out);
	}
	
	/**
	 * Tests the get_key_code method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_key_code() {
		int out;
		out = LanguageHandler.get_key_code('A');
		assertEquals(65, out);
		out = LanguageHandler.get_key_code('a');
		assertEquals(97, out);
		out = LanguageHandler.get_key_code('q');
		assertEquals(113, out);
		out = LanguageHandler.get_key_code('m');
		assertEquals(109, out);
	}
}
