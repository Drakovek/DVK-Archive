package com.gmail.drakovekmail.dvkarchive.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for the DvkCompare class.
 * 
 * @author Drakovek
 */
public class TestDvkCompare {
	
	/**
	 * Tests the compare method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_compare() {
		//SET UP DVKS
		Dvk dvk1 = new Dvk();
		dvk1.set_title("Title1");
		dvk1.set_artist("Artist2");
		dvk1.set_time_int(2020, 1, 30, 11, 30);
		Dvk dvk2 = new Dvk();
		dvk2.set_title("Title2");
		dvk2.set_artist("Artist1");
		dvk2.set_time_int(2020, 1, 30, 11, 0);
		//TEST ALPHA
		int out;
		DvkCompare compare = new DvkCompare();
		compare.set_parameters(DvkCompare.TITLE, false, false);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out < 0);
		compare.set_parameters(DvkCompare.TITLE, false, true);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out > 0);
		compare.set_parameters(DvkCompare.TITLE, true, false);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out > 0);
		compare.set_parameters(DvkCompare.TITLE, true, true);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out < 0);
		//TEST TIME
		compare.set_parameters(DvkCompare.TIME, false, false);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out > 0);
		compare.set_parameters(DvkCompare.TIME, false, true);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out < 0);
		compare.set_parameters(DvkCompare.TIME, true, false);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out > 0);
		compare.set_parameters(DvkCompare.TIME, true, true);
		out = compare.compare(dvk1, dvk2);
		assertTrue(out < 0);
	}
	
	/**
	 * Tests the compare_artists method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_compare_artists() {
		Dvk dvk1 = new Dvk();
		dvk1.set_title("Artist1");
		Dvk dvk2 = new Dvk();
		String[] artists = {"Artist1", "Next"};
		dvk2.set_artists(artists);
		int out;
		out = DvkCompare.compare_artists(dvk1, dvk2);
		assertTrue(out < 0);
		out = DvkCompare.compare_artists(dvk2, dvk1);
		assertTrue(out > 0);
		out = DvkCompare.compare_artists(dvk1, dvk1);
		assertEquals(0, out);
	}
	
	/**
	 * Tests the compare_title_base method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_compare_title_base() {
		Dvk dvk1 = new Dvk();
		dvk1.set_title("Title 1");
		Dvk dvk2 = new Dvk();
		dvk2.set_title("Title 2");
		int out;
		out = DvkCompare.compare_title_base(dvk1, dvk2);
		assertTrue(out < 0);
		out = DvkCompare.compare_title_base(dvk2, dvk1);
		assertTrue(out > 0);
		out = DvkCompare.compare_title_base(dvk1, dvk1);
		assertEquals(0, out);
	}
	
	/**
	 * Tests the compare_titles method
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_compare_titles() {
		//SET UP DVKS
		Dvk dvk1 = new Dvk();
		dvk1.set_title("Title 1");
		dvk1.set_time_int(2020, 1, 30, 10, 30);
		Dvk dvk2 = new Dvk();
		dvk2.set_title("Title 2");
		dvk2.set_time_int(2020, 1, 30, 10, 30);
		Dvk dvk3 = new Dvk();
		dvk3.set_title("Title 2");
		dvk3.set_time_int(2020, 1, 30, 5, 30);
		//DIFFERENT TITLES
		int out;
		out = DvkCompare.compare_titles(dvk1, dvk2);
		assertTrue(out < 0);
		out = DvkCompare.compare_titles(dvk2, dvk1);
		assertTrue(out > 0);
		//SAME TITLES, DIFFERENT TIMES
		out = DvkCompare.compare_titles(dvk2, dvk3);
		assertTrue(out > 0);
		out = DvkCompare.compare_titles(dvk3, dvk2);
		assertTrue(out < 0);
		//IDENTICAL
		out = DvkCompare.compare_titles(dvk3, dvk3);
		assertEquals(0, out);
	}
	
	/**
	 * Tests the compare_time_base method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_compare_time_base() {
		Dvk dvk1 = new Dvk();
		dvk1.set_time_int(2022, 1, 29, 21, 00);
		Dvk dvk2 = new Dvk();
		dvk2.set_time_int(2010,5,10,15,00);
		int out;
		out = DvkCompare.compare_time_base(dvk1, dvk2);
		assertTrue(out > 0);
		out = DvkCompare.compare_time_base(dvk2, dvk1);
		assertTrue(out < 0);
		out = DvkCompare.compare_time_base(dvk1, dvk1);
		assertEquals(0, out);
	}
	
	/**
	 * Tests the compare_time method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_compare_time() {
		//SET UP DVKS
		Dvk dvk1 = new Dvk();
		dvk1.set_title("Title 1");
		dvk1.set_time_int(2020, 1, 30, 10, 30);
		Dvk dvk2 = new Dvk();
		dvk2.set_title("Title 1");
		dvk2.set_time_int(2020, 1, 30, 5, 30);
		Dvk dvk3 = new Dvk();
		dvk3.set_title("Title 2");
		dvk3.set_time_int(2020, 1, 30, 5, 30);
		//DIFFERENT TIMES
		int out;
		out = DvkCompare.compare_time(dvk1, dvk2);
		assertTrue(out > 0);
		out = DvkCompare.compare_time(dvk2, dvk1);
		assertTrue(out < 0);
		//SAME TIMES, DIFFERENT TITLES
		out = DvkCompare.compare_time(dvk2, dvk3);
		assertTrue(out < 0);
		out = DvkCompare.compare_time(dvk3, dvk2);
		assertTrue(out > 0);
		//IDENTICAL
		out = DvkCompare.compare_time(dvk3, dvk3);
		assertEquals(0, out);
	}
}
