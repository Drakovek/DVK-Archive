package com.gmail.drakovekmail.dvkarchive.gui.artist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * Unit tests for the FurAffinityGUI class.
 * 
 * @author Drakovek
 */
public class TestFurAffinityGUI {
	
	/**
	 * Tests the save_checks and load_checks methods.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_save_load_checks() {
		BaseGUI base = new BaseGUI();
		StartGUI start = new StartGUI(base, false);
		FurAffinityGUI gui = new FurAffinityGUI(start);
		//SAVE CHECKS 1
		gui.set_main(false);
		gui.set_scraps(true);
		gui.set_journals(false);
		gui.set_favorites(true);
		gui.save_checks();
		//LOAD CHECKS 1
		gui.set_main(true);
		gui.set_scraps(false);
		gui.set_journals(true);
		gui.set_favorites(false);
		gui.load_checks();
		assertFalse(gui.get_main());
		assertTrue(gui.get_scraps());
		assertFalse(gui.get_journals());
		assertTrue(gui.get_favorites());
		//SAVE CHECKS 2
		gui.set_main(true);
		gui.set_scraps(false);
		gui.set_journals(true);
		gui.set_favorites(false);
		gui.save_checks();
		//LOAD CHECKS 2
		gui.set_main(false);
		gui.set_scraps(true);
		gui.set_journals(false);
		gui.set_favorites(true);
		gui.load_checks();
		assertTrue(gui.get_main());
		assertFalse(gui.get_scraps());
		assertTrue(gui.get_journals());
		assertFalse(gui.get_favorites());
	}
}
