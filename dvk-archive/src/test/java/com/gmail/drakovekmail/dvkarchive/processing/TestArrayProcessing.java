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
	 * Tests the array_to_list method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_array_to_list() {
		ArrayList<String> list;
		list = ArrayProcessing.array_to_list(null);
		assertEquals(0, list.size());
		String[] array = new String[2];
		array[0] = "Thing1";
		array[1] = "Thing2";
		ArrayList<String> compare = new ArrayList<>();
		compare.add("Thing1");
		compare.add("Thing2");
		list = ArrayProcessing.array_to_list(array);
		assertEquals(2, list.size());
		assertEquals(compare.get(0), list.get(0));
		assertEquals(compare.get(1), list.get(1));
	}
	
	/**
	 * Tests the string_to_array method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_string_to_array() {
		String[] array = ArrayProcessing.string_to_array("Test");
		assertEquals(1, array.length);
		assertEquals("Test", array[0]);
		array = ArrayProcessing.string_to_array("Some,things");
		assertEquals(2, array.length);
		assertEquals("Some", array[0]);
		assertEquals("things", array[1]);
		array = ArrayProcessing.string_to_array("I&#45;1,I&#45;2,Other");
		assertEquals(3, array.length);
		assertEquals("I-1", array[0]);
		assertEquals("I-2", array[1]);
		assertEquals("Other", array[2]);
	}
	
	/**
	 * Tests the list_to_array method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_list_to_array() {
		String[] array;
		array = ArrayProcessing.list_to_array(null);
		assertEquals(0, array.length);
		ArrayList<String> list = new ArrayList<>();
		list.add("Thing1");
		list.add("Thing2");
		String[] compare = new String[2];
		compare[0] = "Thing1";
		compare[1] = "Thing2";
		array = ArrayProcessing.list_to_array(list);
		assertEquals(2, array.length);
		assertEquals(compare[0], array[0]);
		assertEquals(compare[1], array[1]);
	}
	
	/**
	 * Tests the clean_array() method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_clean_array() {
		String[] array;
		array = ArrayProcessing.clean_array(null);
		assertEquals(0, array.length);
		array = new String[6];
		array[0] = "these";
		array[1] = "are";
		array[2] = "things";
		array[3] = "";
		array[4] = null;
		array[5] = "are";
		array = ArrayProcessing.clean_array(array);
		assertEquals(4, array.length);
		assertEquals("these", array[0]);
		assertEquals("are", array[1]);
		assertEquals("things", array[2]);
		assertEquals("", array[3]);
	}
	
	/**
	 * Tests the clean_list method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_clean_list() {
		ArrayList<String> list = new ArrayList<>();
		list = ArrayProcessing.clean_list(null);
		assertEquals(0, list.size());
		list.clear();
		list.add("these");
		list.add("are");
		list.add("things");
		list.add("");
		list.add(null);
		list.add("are");
		list = ArrayProcessing.clean_list(list);
		assertEquals(4, list.size());
		assertEquals("these", list.get(0));
		assertEquals("are", list.get(1));
		assertEquals("things", list.get(2));
		assertEquals("", list.get(3));
	}
	
	/**
	 * Tests the sort_alphanum method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_sort_alphanum() {
		String[] array = ArrayProcessing.sort_alphanum(null);
		assertEquals(0, array.length);
		String[] input = {"10.05", "a", "5", "010,5"};
		array = ArrayProcessing.sort_alphanum(input);
		assertEquals(4, array.length);
		assertEquals("5", array[0]);
		assertEquals("10.05", array[1]);
		assertEquals("010,5", array[2]);
		assertEquals("a", array[3]);
	}
	
	/**
	 * Tests the array_to_string method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_array_to_string() {
		String out;
		//NO HTML
		out = ArrayProcessing.array_to_string(null, 0, false);
		assertEquals(0, out.length());
		String[] in = {"I-1", "I-2", "Other"};
		out = ArrayProcessing.array_to_string(in, 0, false);
		assertEquals("I-1,I-2,Other", out);
		out = ArrayProcessing.array_to_string(in, -1, false);
		assertEquals("I-1,I-2,Other", out);
		out = ArrayProcessing.array_to_string(in, 2, false);
		assertEquals("I-1,  I-2,  Other", out);
		//WITH HTML
		out = ArrayProcessing.array_to_string(in, 0, true);
		assertEquals("I&#45;1,I&#45;2,Other", out);
		out = ArrayProcessing.array_to_string(in, 2, true);
		assertEquals("I&#45;1,  I&#45;2,  Other", out);
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_contains() {
		//INVALID
		assertFalse(ArrayProcessing.contains(null, "thing", true));
		assertFalse(ArrayProcessing.contains(null, "thing", false));
		//CASE SENSITIVE
		String[] array = {"these", "are", null, "words"};
		assertFalse(ArrayProcessing.contains(array, "thing", true));
		assertTrue(ArrayProcessing.contains(array, "these", true));
		assertTrue(ArrayProcessing.contains(array, "are", true));
		assertTrue(ArrayProcessing.contains(array, "words", true));
		assertFalse(ArrayProcessing.contains(array, "WoRdS", true));
		assertFalse(ArrayProcessing.contains(array, "These", true));
		//CASE INSENSITIVE
		assertFalse(ArrayProcessing.contains(array, "thing", false));
		assertTrue(ArrayProcessing.contains(array, "these", false));
		assertTrue(ArrayProcessing.contains(array, "are", false));
		assertTrue(ArrayProcessing.contains(array, "words", false));
		assertTrue(ArrayProcessing.contains(array, "WoRdS", false));
		assertTrue(ArrayProcessing.contains(array, "These", false));
	}
}
