package com.gmail.drakovekmail.dvkarchive.processing;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the ArrayProcessing class.
 * 
 * @author Drakovek
 */
public class TestArrayProcessing {
	
	/**
	 * Tests the clean_array() method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_clean_array() {
		//SET UP ARRAY
		String[] array = new String[6];
		array[0] = "these";
		array[1] = "are";
		array[2] = "things";
		array[3] = "";
		array[4] = null;
		array[5] = "are";
		//TEST CLEANING ARRAY
		array = ArrayProcessing.clean_array(array);
		assertEquals(4, array.length);
		assertEquals("these", array[0]);
		assertEquals("are", array[1]);
		assertEquals("things", array[2]);
		assertEquals("", array[3]);
		//TEST CLEANING INVALID ARRAY
		array = ArrayProcessing.clean_array(null);
		assertEquals(0, array.length);
	}
	
	/**
	 * Tests the clean_list method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_clean_list() {
		//SET UP LIST
		ArrayList<String> list = new ArrayList<>();
		list.add("these");
		list.add("are");
		list.add("things");
		list.add("");
		list.add(null);
		list.add("are");
		//TEST CLEANING LIST
		list = ArrayProcessing.clean_list(list);
		assertEquals(4, list.size());
		assertEquals("these", list.get(0));
		assertEquals("are", list.get(1));
		assertEquals("things", list.get(2));
		assertEquals("", list.get(3));
		//TEST CLEANING INVALID LIST
		list = ArrayProcessing.clean_list(null);
		assertEquals(0, list.size());
	}
	
	/**
	 * Tests the sort_alphanum method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_sort_alphanum() {
		//SET UP ARRAY
		String[] input = {"10.05", "a", "5", "010,5"};
		//TEST SORTING ARRAY
		String[] output = ArrayProcessing.sort_alphanum(input);
		assertEquals(4, output.length);
		assertEquals("5", output[0]);
		assertEquals("10.05", output[1]);
		assertEquals("010,5", output[2]);
		assertEquals("a", output[3]);
		//TEST SORTING INVALID ARRAY
		String[] array = ArrayProcessing.sort_alphanum(null);
		assertEquals(0, array.length);
	}

	/**
	 * Tests the array_to_string method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_array_to_string() {
		//SET UP ARRAY
		String output;
		String[] in = {"I-1", "I-2", "Other"};
		//TEST CONVERTING TO STRING WITH VARIOUS INDENT SIZES
		output = ArrayProcessing.array_to_string(in, 0, false);
		assertEquals("I-1,I-2,Other", output);
		output = ArrayProcessing.array_to_string(in, -1, false);
		assertEquals("I-1,I-2,Other", output);
		output = ArrayProcessing.array_to_string(in, 2, false);
		assertEquals("I-1,  I-2,  Other", output);
		//TEST CONVERTING WHILE ADDING HTML ESCAPE CHARACTERS
		output = ArrayProcessing.array_to_string(in, 0, true);
		assertEquals("I&#45;1,I&#45;2,Other", output);
		output = ArrayProcessing.array_to_string(in, 2, true);
		assertEquals("I&#45;1,  I&#45;2,  Other", output);
		//TEST CONVERTING INVALLID ARRAY
		output = ArrayProcessing.array_to_string(null, 0, false);
		assertEquals(null, output);
	}
	
	/**
	 * Tests the string_to_array method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_string_to_array() {
		//TEST CONVERTING STRING TO ARRAY
		String[] array;
		array = ArrayProcessing.string_to_array("Test");
		assertEquals(1, array.length);
		assertEquals("Test", array[0]);
		array = ArrayProcessing.string_to_array("Some,things");
		assertEquals(2, array.length);
		assertEquals("Some", array[0]);
		assertEquals("things", array[1]);
		//TEST CONVERTING HTML ESCAPES
		array = ArrayProcessing.string_to_array("I&#45;1,I&#45;2,Other");
		assertEquals(3, array.length);
		assertEquals("I-1", array[0]);
		assertEquals("I-2", array[1]);
		assertEquals("Other", array[2]);
		//TEST CONVERTING INVALID STRING
		array = ArrayProcessing.string_to_array(null);
		assertTrue(array == null);
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_contains() {
		//SET UP ARRAY
		String[] array = {"these", "are", null, "words"};
		//TEST CASE SENSITIVE SEARCH
		assertFalse(ArrayProcessing.contains(array, "thing", true));
		assertTrue(ArrayProcessing.contains(array, "these", true));
		assertTrue(ArrayProcessing.contains(array, "are", true));
		assertTrue(ArrayProcessing.contains(array, "words", true));
		assertFalse(ArrayProcessing.contains(array, "WoRdS", true));
		assertFalse(ArrayProcessing.contains(array, "These", true));
		//TEST CASE INSENSITIVE SEARCH
		assertFalse(ArrayProcessing.contains(array, "thing", false));
		assertTrue(ArrayProcessing.contains(array, "these", false));
		assertTrue(ArrayProcessing.contains(array, "are", false));
		assertTrue(ArrayProcessing.contains(array, "words", false));
		assertTrue(ArrayProcessing.contains(array, "WoRdS", false));
		assertTrue(ArrayProcessing.contains(array, "These", false));
		//TEST SEARCHING INVALID STRING
		assertFalse(ArrayProcessing.contains(null, "thing", true));
		assertFalse(ArrayProcessing.contains(null, "thing", false));
	}
}
