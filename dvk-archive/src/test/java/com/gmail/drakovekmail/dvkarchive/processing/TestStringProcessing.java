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
		//TEST PADDING OUT INTEGER STRINGS WITH ZEROS
		assertEquals("15", StringProcessing.pad_int(15, 2));
		assertEquals("00012", StringProcessing.pad_int(12, 5));
		//TEST USING INVALID VALUES
		assertEquals("00", StringProcessing.pad_int(256, 2));
		assertEquals("", StringProcessing.pad_int(12, 0));
		assertEquals("", StringProcessing.pad_int(12, -1));
	}
	
	/**
	 * Tests the extend_num method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_extend_num() {
		//TEST PADDING OUT NUMBER STRINGS WITH ZEROS
		assertEquals("2F", StringProcessing.pad_num("2F", 2));
		assertEquals("0002E", StringProcessing.pad_num("2E", 5));
		//TEST USING INVALID VALUES
		assertEquals("00", StringProcessing.pad_num("10F", 2));
		assertEquals("", StringProcessing.pad_num("A3", 0));
		assertEquals("", StringProcessing.pad_num("F3", -1));
		assertEquals("", StringProcessing.pad_num(null, 2));
	}
	
	/**
	 * Tests the remove_whitespace method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_remove_whitespace() {
		//TEST REMOVING WHITESPACE FROM THE BEGINNING AND END OF STRINGS
		assertEquals("", StringProcessing.remove_whitespace(""));
		assertEquals("", StringProcessing.remove_whitespace(" "));
		assertEquals("", StringProcessing.remove_whitespace(" \t  "));
		assertEquals("blah", StringProcessing.remove_whitespace("  blah"));
		assertEquals("blah", StringProcessing.remove_whitespace("blah   "));
		assertEquals("blah", StringProcessing.remove_whitespace(" \t blah  \t"));
		assertEquals("blah", StringProcessing.remove_whitespace("blah"));
		//TEST USING INVALID STRING
		assertEquals("", StringProcessing.remove_whitespace(null));
	}
	
	/**
	 * Tests the get_filename method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_filename() {
		//TEST GETTING FILE FRIENDLY NAMES
		assertEquals("This - That 2", StringProcessing.get_filename("This & That 2"));
		assertEquals("end filler", StringProcessing.get_filename("! !end filler!??  "));
		assertEquals("thing-stuff - bleh", StringProcessing.get_filename("thing--stuff  @*-   bleh"));
		assertEquals("a - b - c", StringProcessing.get_filename("a% - !b @  ??c"));
		assertEquals("Test", StringProcessing.get_filename("Test String", 5));
		assertEquals("Test String", StringProcessing.get_filename("Test String", -1));
		//TEST GETTING FILENAMES WITH NO LENGTH
		assertEquals("0", StringProcessing.get_filename(""));
		assertEquals("0", StringProcessing.get_filename("$"));
		//TEST GETTING FILENAME WHEN GIVEN STRING IS INVALID
		assertEquals("0", StringProcessing.get_filename(null));
	}
	
	/**
	 * Tests the truncate_string method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_truncate_string() {
	    //TEST TRUNCATING STRINGS
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
	    //TEST WHEN GIVEN STRING IS INVALID
	    assertEquals("", StringProcessing.truncate_string(null, 2));
	}
	
	/**
	 * Tests the get_extension method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_extension() {
		//TEST GETTING EXTENSIONS FROM FILENAMES
		assertEquals(".png", StringProcessing.get_extension("test.png"));
		assertEquals(".long", StringProcessing.get_extension(".long"));
		assertEquals(".thing", StringProcessing.get_extension("test.thing"));
		assertEquals(".png", StringProcessing.get_extension("blah.test.png"));
		//TEST GETTING EXTENTIONS FROM URLS WITH TOKENS
		assertEquals(".png", StringProcessing.get_extension("test.png?extra.thing"));
		assertEquals(".thing", StringProcessing.get_extension("thing.test.thing?"));
		//TEST GETTING INVALID EXTENSIONS
		assertEquals("", StringProcessing.get_extension("test.tolong"));
		assertEquals("", StringProcessing.get_extension("test.notextension"));
		assertEquals("", StringProcessing.get_extension("kskdjfjskjd"));
		assertEquals("", StringProcessing.get_extension("test.tolong?extra"));
		//TEST GETTING EXTENSION IF GIVEN STRING IS NULL
		assertEquals("", StringProcessing.get_extension(null));
	}
}
