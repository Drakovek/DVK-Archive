package com.gmail.drakovekmail.dvkarchive.processing;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the StringProcessing class.
 * 
 * @author Drakovek
 */
public class TestStringProcessing {
	
	/**
	 * Tests the extend_int method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_extend_int() {
		assertEquals("00", StringProcessing.extend_int(256, 2));
		assertEquals("", StringProcessing.extend_int(12, 0));
		assertEquals("", StringProcessing.extend_int(12, -1));
		assertEquals("15", StringProcessing.extend_int(15, 2));
		assertEquals("00012", StringProcessing.extend_int(12, 5));
	}
	
	/**
	 * Tests the extend_num method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_extend_num() {
		assertEquals("00", StringProcessing.extend_num("10F", 2));
		assertEquals("", StringProcessing.extend_num("A3", 0));
		assertEquals("", StringProcessing.extend_num("F3", -1));
		assertEquals("2F", StringProcessing.extend_num("2F", 2));
		assertEquals("0002E", StringProcessing.extend_num("2E", 5));
	}
	
	/**
	 * Tests the remove_whitespace method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_remove_whitespace() {
		assertEquals("", StringProcessing.remove_whitespace(null));
		assertEquals("", StringProcessing.remove_whitespace(""));
		assertEquals("", StringProcessing.remove_whitespace(" "));
		assertEquals("", StringProcessing.remove_whitespace("   "));
		assertEquals("blah", StringProcessing.remove_whitespace("  blah"));
		assertEquals("blah", StringProcessing.remove_whitespace("blah   "));
		assertEquals("blah", StringProcessing.remove_whitespace("  blah "));
		assertEquals("blah", StringProcessing.remove_whitespace("blah"));
	}
	
	/**
	 * Tests the get_filename method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_filename() {
		assertEquals("0", StringProcessing.get_filename(null));
		assertEquals("0", StringProcessing.get_filename(""));
		assertEquals("This - That 2", StringProcessing.get_filename("This & That 2"));
		assertEquals("end filler", StringProcessing.get_filename("! !end filler!??  "));
		assertEquals("0", StringProcessing.get_filename("$"));
		assertEquals("thing-stuff - bleh", StringProcessing.get_filename("thing--stuff  @*-   bleh"));
		assertEquals("a - b - c", StringProcessing.get_filename("a% - !b @  ??c"));
		assertEquals("Test", StringProcessing.get_filename("Test String", 5));
		assertEquals("Test String", StringProcessing.get_filename("Test String", -1));
	}
	
	/**
	 * Tests the truncate_string method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_truncate_string() {
	    assertEquals("", StringProcessing.truncate_string(null, 2));
	    assertEquals("", StringProcessing.truncate_string("blah", 0));
	    assertEquals("", StringProcessing.truncate_string("bleh", -1));
	    assertEquals("bleh", StringProcessing.truncate_string("bleh", 4));
	    assertEquals("wor", StringProcessing.truncate_string("words", 3));
	    assertEquals("word", StringProcessing.truncate_string("word-stuff", 5));
	    assertEquals("stu", StringProcessing.truncate_string("words n stuff", 4));
	    assertEquals("word", StringProcessing.truncate_string("word stuff", 5));
	    assertEquals("stu", StringProcessing.truncate_string("words-n-stuff", 4));
	    String i = "This string is way too long to work as a title p25";
	    String o = "This string is way too long to work p25";
	    assertEquals(o, StringProcessing.truncate_string(i, 40));
	    i = "HereIsA LongThingWithoutManySpacesWhichCanBeShort";
	    o = "HereIsA WithoutManySpacesWhichCanBeShort";
	    assertEquals(o, StringProcessing.truncate_string(i, 40));
	    i = "ThisMessageIsAbsolutelyWayToLongToWorkFor-";
	    i = i + "AnyThingAtAllSoLetsSeeHowThisWillFareISuppose";
	    o = "ThisMessageIsAbsolutelyWayToLongToWorkFo";
	    assertEquals(o, StringProcessing.truncate_string(i, 40));
	    i = "ThisMessageIsAbsolutelyWayToLongToWorkForAnyThing-";
	    i = i + "AtAllSoLetsSeeHowThisWillFareISuppose";
	    o = "Th-AtAllSoLetsSeeHowThisWillFareISuppose";
	    assertEquals(o, StringProcessing.truncate_string(i, 40));
	    i = "ThisLongTitleHasNoSpacesAtAllSoItHasAMiddleBreak";
	    o = "ThisLongTitleHasAtAllSoItHasAMiddleBreak";
	    assertEquals(o, StringProcessing.truncate_string(i, 40));
	}
	
	/**
	 * Tests the get_extension method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_extension() {
		assertEquals("", StringProcessing.get_extension(null));
		assertEquals(".png", StringProcessing.get_extension("test.png"));
		assertEquals(".long", StringProcessing.get_extension(".long"));
		assertEquals(".thing", StringProcessing.get_extension("test.thing"));
		assertEquals("", StringProcessing.get_extension("test.tolong"));
		assertEquals("", StringProcessing.get_extension("test.notextension"));
		assertEquals(".png", StringProcessing.get_extension("blah.test.png"));
		assertEquals("", StringProcessing.get_extension("kskdjfjskjd"));
		assertEquals(".png", StringProcessing.get_extension("test.png?extra.thing"));
		assertEquals(".thing", StringProcessing.get_extension("test.thing?"));
		assertEquals("", StringProcessing.get_extension("test.tolong?extra"));
	}
	
	/**
	 * Tests the remove_section method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_remove_section() {
		assertEquals("test", StringProcessing.remove_section("test", 0, 0));
		assertEquals("test", StringProcessing.remove_section("test", 4, 4));
		assertEquals("test", StringProcessing.remove_section("testThing", 4, 9));
		assertEquals("Test", StringProcessing.remove_section("ThisTest", 0, 4));
		assertEquals("wordsstuff", StringProcessing.remove_section("wordsANDstuff", 5, 8));
	}
}
