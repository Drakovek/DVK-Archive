package com.gmail.drakovekmail.dvkarchive.processing;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

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
}
