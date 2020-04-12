package com.gmail.drakovekmail.dvkarchive.gui.artist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * Unit tests for the ArtistHostingGUI class.
 * 
 * @author Drakovek
 */
public class TestArtistHostingGUI {
	
	/**
	 * Tests the get_main and set_main methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_main() {
		BaseGUI base = new BaseGUI();
		StartGUI start = new StartGUI(base, false);
		FurAffinityGUI gui = new FurAffinityGUI(start);
		gui.set_main(false);
		assertFalse(gui.get_main());
		gui.set_main(true);
		assertTrue(gui.get_main());
	}
	
	/**
	 * Tests the get_scraps and set_scraps methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_scraps() {
		BaseGUI base = new BaseGUI();
		StartGUI start = new StartGUI(base, false);
		FurAffinityGUI gui = new FurAffinityGUI(start);
		gui.set_scraps(false);
		assertFalse(gui.get_scraps());
		gui.set_scraps(true);
		assertTrue(gui.get_scraps());
	}
	
	/**
	 * Tests the get_journals and set_journals methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_journals() {
		BaseGUI base = new BaseGUI();
		StartGUI start = new StartGUI(base, false);
		FurAffinityGUI gui = new FurAffinityGUI(start);
		gui.set_journals(false);
		assertFalse(gui.get_journals());
		gui.set_journals(true);
		assertTrue(gui.get_journals());
	}
	
	/**
	 * Tests the get_favorites and set_favorites methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_set_favorites() {
		BaseGUI base = new BaseGUI();
		StartGUI start = new StartGUI(base, false);
		FurAffinityGUI gui = new FurAffinityGUI(start);
		gui.set_favorites(false);
		assertFalse(gui.get_favorites());
		gui.set_favorites(true);
		assertTrue(gui.get_favorites());
	}
}
