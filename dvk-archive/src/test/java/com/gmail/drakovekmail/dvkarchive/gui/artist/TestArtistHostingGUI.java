package com.gmail.drakovekmail.dvkarchive.gui.artist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * Unit tests for the ArtistHostingGUI class.
 * 
 * @author Drakovek
 */
public class TestArtistHostingGUI {
	
	/**
	 * Directory for holding test files
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Main ArtistHostingGUI used for testing
	 * Uses FurAffinityGUI extension class since ArtistHostingGUI is abstract
	 */
	private FurAffinityGUI gui;
	
	/**
	 * StartGUI used to initialize the ArtistHostingGUI
	 */
	private StartGUI start;
	
	/**
	 * Sets up test files and objects.
	 */
	@Before
	public void set_up() {
		BaseGUI base = new BaseGUI();
		this.start = new StartGUI(base, false);
		this.start.get_file_prefs().set_index_dir(this.temp_dir.getRoot());
		this.start.set_directory(this.temp_dir.getRoot());
		this.start.get_base_gui().set_theme("Metal");
		this.gui = new FurAffinityGUI(this.start);
		this.start.set_directory(this.temp_dir.getRoot());
	}
	
	/**
	 * Deletes test files and closes test objects.
	 */
	@After
	public void tear_down() {
		this.gui.close();
		this.gui = null;
		this.start.close();
		this.start = null;
	}
	
	/**
	 * Tests the get_main and set_main methods.
	 */
	@Test
	public void test_get_set_main() {
		this.gui.set_main(false);
		assertFalse(this.gui.get_main());
		this.gui.set_main(true);
		assertTrue(this.gui.get_main());
	}
	
	/**
	 * Tests the get_scraps and set_scraps methods.
	 */
	@Test
	public void test_get_set_scraps() {
		this.gui.set_scraps(false);
		assertFalse(this.gui.get_scraps());
		this.gui.set_scraps(true);
		assertTrue(this.gui.get_scraps());
	}
	
	/**
	 * Tests the get_journals and set_journals methods.
	 */
	@Test
	public void test_get_set_journals() {
		this.gui.set_journals(false);
		assertFalse(this.gui.get_journals());
		this.gui.set_journals(true);
		assertTrue(this.gui.get_journals());
	}
	
	/**
	 * Tests the get_favorites and set_favorites methods.
	 */
	@Test
	public void test_get_set_favorites() {
		this.gui.set_favorites(false);
		assertFalse(this.gui.get_favorites());
		this.gui.set_favorites(true);
		assertTrue(this.gui.get_favorites());
	}
	
	/**
	 * Tests the is_already_downloaded method.
	 */
	@Test
	public void test_is_already_downloaded() {
		//CREATE DVK1
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("ID123-J");
		dvk.set_title("Title 1");
		dvk.set_artist("Artist");
		dvk.set_page_url("/page/");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk1.dvk"));
		dvk.set_media_file("dvk1.png");
		dvk.write_dvk();
		//SET DVK2
		dvk.set_title("Title 2");
		dvk.set_dvk_id("THG123");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk2.dvk"));
		dvk.set_media_file("dvk2.jpg");
		dvk.write_dvk();
		//SET DVK3
		dvk.set_title("Title 3");
		dvk.set_dvk_id("ID345");
		dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "dvk3.dvk"));
		dvk.set_media_file("dvk3.txt");
		dvk.write_dvk();
		//READ DVKS
		this.gui.get_start_gui().get_base_gui().set_canceled(false);
		this.gui.read_dvks();
		assertTrue(dvk.get_dvk_file().exists());
		assertEquals(this.temp_dir.getRoot(), this.gui.get_start_gui().get_directory());
		ArrayList<Dvk> dvks = this.gui.get_dvk_handler().get_dvks(0, -1, 'a', false, false);
		assertEquals(3, dvks.size());
		assertEquals("ID123-J", dvks.get(0).get_dvk_id());
		//CHECK ALREADY DOWNLOADED
		assertFalse(this.gui.is_already_downloaded("THG12", false, true));
		assertTrue(this.gui.is_already_downloaded("THG12%", true, true));
		assertTrue(this.gui.is_already_downloaded("ID123-J", false, false));
		assertFalse(this.gui.is_already_downloaded("ID123-J", false, true));
		assertTrue(this.gui.is_already_downloaded("ID345", false, true));
	}
}
