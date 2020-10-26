package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the InOut class.
 * 
 * @author Drakovek
 */
public class TestInOut{
	
	/**
	 * Directory to hold all test files during testing.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Tests the write_file methods.
	 */
	@Test
	public void test_write_file() {
		//TEST INVALID CONTENTS
		File file = new File(this.temp_dir.getRoot(), "file.txt");
		String str = null;
		ArrayList<String> list = null;
		InOut.write_file(null, str);
		InOut.write_file(null, list);
		InOut.write_file(file, str);
		assertFalse(file.exists());
		InOut.write_file(file, list);
		assertFalse(file.exists());
		//TEST INVALID FILE
		str = "test string";
		list = new ArrayList<>();
		list.add("item");
		list.add("next");
		InOut.write_file(null, str);
		InOut.write_file(null, list);
		assertFalse(file.exists());
		//TEST VALID FILES
		InOut.write_file(file, str);
		assertTrue(file.exists());
		String out = InOut.read_file(file);
		assertEquals(str, out);
		InOut.write_file(file, list);
		out = InOut.read_file(file);
		assertEquals("item\r\nnext", out);
		InOut.write_file(file, "");
		out = InOut.read_file(file);
		assertEquals("", out);
	}
	
	/**
	 * Tests the read_file and read_file_array methods.
	 */
	@Test
	public void test_read_file() {
		//TEST INVALID FILES
		String str = InOut.read_file(null);
		assertEquals(0, str.length());
		ArrayList<String> list = InOut.read_file_list(null);
		assertEquals(0, list.size());
		//TEST VALID FILES
		str = "line1\r\nline2";
		File file = new File(this.temp_dir.getRoot(), "file.txt");
		InOut.write_file(file, str);
		list = InOut.read_file_list(file);
		assertEquals(2, list.size());
		assertEquals("line1", list.get(0));
		assertEquals("line2", list.get(1));
		str = new String();
		str = InOut.read_file(file);
		assertEquals("line1\r\nline2", str);
	}
}
