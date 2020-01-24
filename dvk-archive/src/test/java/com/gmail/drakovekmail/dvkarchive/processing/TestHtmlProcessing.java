package com.gmail.drakovekmail.dvkarchive.processing;

import junit.framework.TestCase;

/**
 * Unit tests for the HtmlProcessing class.
 * 
 * @author Drakovek
 */
public class TestHtmlProcessing extends TestCase{
	
	/**
	 * Tests the escape_to_char method.
	 */
	public static void test_escape_to_char() {
		assertEquals("", HtmlProcessing.escape_to_char(null));
		assertEquals("", HtmlProcessing.escape_to_char(" "));
		assertEquals("", HtmlProcessing.escape_to_char("&;"));
		assertEquals("", HtmlProcessing.escape_to_char("&nope;"));
		assertEquals("\"", HtmlProcessing.escape_to_char("&quot;"));
		assertEquals("'", HtmlProcessing.escape_to_char("&apos;"));
		assertEquals("&", HtmlProcessing.escape_to_char("&amp;"));
		assertEquals("<", HtmlProcessing.escape_to_char("&lt;"));
		assertEquals(">", HtmlProcessing.escape_to_char("&gt;"));
		assertEquals(" ", HtmlProcessing.escape_to_char("&nbsp;"));
		assertEquals("<", HtmlProcessing.escape_to_char("&#60;"));
		assertEquals("", HtmlProcessing.escape_to_char("&#nope;"));
		assertEquals("", HtmlProcessing.escape_to_char("&#;"));
	}
	
	/**
	 * Tests the replace_escapes method.
	 */
	public static void test_replace_escapes() {
		assertEquals("", HtmlProcessing.replace_escapes(null));
		String in = "&lt;i&gt;Test HTML&#60;&#47;i&#62;";
		String out = "<i>Test HTML</i>";
		assertEquals(out, HtmlProcessing.replace_escapes(in));
		in = "this&that";
		assertEquals(in, HtmlProcessing.replace_escapes(in));
		in = "remove&this;";
		out = "remove";
		assertEquals(out, HtmlProcessing.replace_escapes(in));
	}
	
	/**
	 * Tests the add_escapes method.
	 */
	public static void test_add_escapes() {
		assertEquals("", HtmlProcessing.add_escapes(null));
		String in = "<b>Fake tags.</b>";
		String out = "&#60;b&#62;Fake tags&#46;&#60;&#47;b&#62;";
		assertEquals(out, HtmlProcessing.add_escapes(in));
		in = "normal";
		assertEquals(in, HtmlProcessing.add_escapes(in));
	}
	
	/**
	 * Tests the add_escapes_to_html method.
	 */
	public static void test_add_escapes_to_html() {
		assertEquals("", HtmlProcessing.add_escapes_to_html(null));
		String in = "<a href=\"Sommarfågel\">Sommarfågel</a>";
		String out = "<a href=\"Sommarfågel\">Sommarf&#229;gel</a>";
		assertEquals(out, HtmlProcessing.add_escapes_to_html(in));
		in = "<a href='Sommarfågel'>Sommarfågel</a>";
		out = "<a href='Sommarfågel'>Sommarf&#229;gel</a>";
		assertEquals(out, HtmlProcessing.add_escapes_to_html(in));
		in = "<a href=\"Sommarfågel";
		out = "<a href=\"Sommarfågel";
		assertEquals(out, HtmlProcessing.add_escapes_to_html(in));
	}
}
