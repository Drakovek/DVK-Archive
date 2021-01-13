package com.gmail.drakovekmail.dvkarchive.processing;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the HtmlProcessing class.
 * 
 * @author Drakovek
 */
public class TestHtmlProcessing {
	
	/**
	 * Tests the escape_to_char method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_escape_to_char() {
		//TEST REPLACING HTML ESCAPE CHARACTERS
		assertEquals("\"", HtmlProcessing.escape_to_char("&quot;"));
		assertEquals("'", HtmlProcessing.escape_to_char("&apos;"));
		assertEquals("&", HtmlProcessing.escape_to_char("&amp;"));
		assertEquals("<", HtmlProcessing.escape_to_char("&lt;"));
		assertEquals(">", HtmlProcessing.escape_to_char("&gt;"));
		assertEquals(" ", HtmlProcessing.escape_to_char("&nbsp;"));
		assertEquals("<", HtmlProcessing.escape_to_char("&#60;"));
		assertEquals("&", HtmlProcessing.escape_to_char("&#38;"));
		//TEST REPLACING INVALID ESCAPE CHARACTERS
		assertEquals("", HtmlProcessing.escape_to_char(null));
		assertEquals("", HtmlProcessing.escape_to_char(" "));
		assertEquals("", HtmlProcessing.escape_to_char("&;"));
		assertEquals("", HtmlProcessing.escape_to_char("&nope;"));
		assertEquals("", HtmlProcessing.escape_to_char("&#nope;"));
		assertEquals("", HtmlProcessing.escape_to_char("&#;"));
	}
	
	/**
	 * Tests the replace_escapes method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_replace_escapes() {
		//TEST REPLACING HTML ESCAPE CHARACTERS IN STRING
		String input = "&lt;i&gt;Test HTML&#60;&#47;i&#62;";
		assertEquals("<i>Test HTML</i>", HtmlProcessing.replace_escapes(input));
		input = "this&that";
		assertEquals("this&that", HtmlProcessing.replace_escapes(input));
		input = "remove&this;";
		assertEquals("remove", HtmlProcessing.replace_escapes(input));
		//TEST REPLACING ESCAPE CHARACTERS IN INVALID TEST
		assertEquals("", HtmlProcessing.replace_escapes(null));
	}
	
	/**
	 * Tests the add_escapes method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_add_escapes() {
		//TEST REPLACING NON-STANDARD CHARACTERS WITH HTML ESCAPE CHARACTERS
		String input = "<b>Fake tags.</b>";
		String output = "&#60;b&#62;Fake tags&#46;&#60;&#47;b&#62;";
		assertEquals(output, HtmlProcessing.add_escapes(input));
		input = "normal";
		assertEquals("normal", HtmlProcessing.add_escapes(input));
		//TEST ADDING ESCAPE CHARACTERS TO INVALID STRING
		assertEquals("", HtmlProcessing.add_escapes(null));
	}
	
	/**
	 * Tests the add_escapes_to_html method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_add_escapes_to_html() {
		//TEST REPLACING CHARACTERS WITH HTML ESCAPE CHARACTERS IN HTML TEXT
		String input = "<a href=\"Sommarfågel\">Sommarfågel</a>";
		String output = "<a href=\"Sommarfågel\">Sommarf&#229;gel</a>";
		assertEquals(output, HtmlProcessing.add_escapes_to_html(input));
		input = "<a href='Sommarfågel'>Sommarfågel</a>";
		output = "<a href='Sommarfågel'>Sommarf&#229;gel</a>";
		assertEquals(output, HtmlProcessing.add_escapes_to_html(input));
		input = "<a href=\"Sommarfågel";
		output = "<a href=\"Sommarfågel";
		assertEquals(output, HtmlProcessing.add_escapes_to_html(input));
		//TEST ADDING ESCAPE CHARACTERS TO INVALID STRING
		assertEquals("", HtmlProcessing.add_escapes_to_html(null));
	}
}
