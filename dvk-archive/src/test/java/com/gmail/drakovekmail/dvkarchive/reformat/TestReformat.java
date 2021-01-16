package com.gmail.drakovekmail.dvkarchive.reformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gmail.drakovekmail.dvkarchive.file.Config;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.InOut;

/**
 * UnitTests for the Reformat class.
 * 
 * @author Drakovek
 */
public class TestReformat {

	/**
	 * Main directory  for holding test files
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * Tests the reformat_dvks method.
	 */
	@Test
	public void test_reformat_dvks() {
		Config config = new Config();
		config.set_config_directory(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(config)) {
			//WRITE DVK FILE THAT SHOULD NOT BE CHANGED WHEN REFORMATTED
			Dvk no_change_dvk = new Dvk();
			no_change_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "no_change.dvk"));
			no_change_dvk.set_dvk_id("NOC123");
			no_change_dvk.set_title("No change");
			no_change_dvk.set_artist("NoChange");
			no_change_dvk.set_page_url("nochange/url/");
			no_change_dvk.set_media_file("no_change.txt");
			no_change_dvk.set_description("<b>keep description</b>");
			no_change_dvk.write_dvk();
			//WRITE DVK FILE THAT SHOULD HAVE DESCRIPTION AND MEDIA FILE ALTERED
			Dvk altered_dvk = new Dvk();
			altered_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "desc.dvk"));
			altered_dvk.set_dvk_id("DES123");
			altered_dvk.set_title("Alter Description");
			altered_dvk.set_artist("DescArtist");
			altered_dvk.set_page_url("desc/url/");
			altered_dvk.set_media_file("desc.png");
			InOut.write_file(altered_dvk.get_media_file(), "This is a test.");
			altered_dvk.set_description("  <b>   <i> Clean Description </i>   </b> ");
			altered_dvk.write_dvk();
			//CHECK THAT TEST FILES WERE WRITTEN PROPERLY
			assertTrue(no_change_dvk.get_dvk_file().exists());
			assertTrue(altered_dvk.get_dvk_file().exists());
			assertTrue(altered_dvk.get_media_file().exists());
			//REFORMAT DVK FILES
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			Reformat.reformat_dvks(dvk_handler);
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(2, dvks.size());
			//TEST ALTERED_DVK FORMATTED PROPERLY
			assertEquals(altered_dvk.get_dvk_file(), dvks.get(0).get_dvk_file());
			assertEquals("DES123", dvks.get(0).get_dvk_id());
			assertEquals("Alter Description", dvks.get(0).get_title());
			assertEquals(1, dvks.get(0).get_artists().length);
			assertEquals("DescArtist", dvks.get(0).get_artists()[0]);
			assertEquals("desc/url/", dvks.get(0).get_page_url());
			assertEquals(this.temp_dir.getRoot(), dvks.get(0).get_media_file().getParentFile());
			assertEquals("desc.txt", dvks.get(0).get_media_file().getName());
			assertEquals("<b> <i> Clean Description </i> </b>", dvks.get(0).get_description());
			//TEST FILES WERE ACTUALLY WRITTEN TO FILE
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			assertEquals(this.temp_dir.getRoot(), dvks.get(0).get_media_file().getParentFile());
			assertEquals("desc.txt", dvks.get(0).get_media_file().getName());
			assertEquals("<b> <i> Clean Description </i> </b>", dvks.get(0).get_description());
			assertEquals(no_change_dvk.get_dvk_file(), dvks.get(1).get_dvk_file());
			assertEquals("NOC123", dvks.get(1).get_dvk_id());
			assertEquals("No change", dvks.get(1).get_title());
			assertEquals(1, dvks.get(1).get_artists().length);
			assertEquals("NoChange", dvks.get(1).get_artists()[0]);
			assertEquals("nochange/url/", dvks.get(1).get_page_url());
			assertEquals(no_change_dvk.get_media_file(), dvks.get(1).get_media_file());
			assertEquals("<b>keep description</b>", dvks.get(1).get_description());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the remame_files method.
	 */
	@Test
	public void test_rename_files() {
		Config config = new Config();
		config.set_config_directory(this.temp_dir.getRoot());
		try(DvkHandler dvk_handler = new DvkHandler(config)) {
			//WRITE TEST DVK WITH NO LINKED MEDIA FILES
			Dvk no_media_dvk = new Dvk();
			no_media_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "nm.dvk"));
			no_media_dvk.set_dvk_id("NM123");
			no_media_dvk.set_title("No Media");
			no_media_dvk.set_artist("Artist");
			no_media_dvk.set_page_url("/url/");
			no_media_dvk.set_media_file("nm.png");
			no_media_dvk.write_dvk();
			//WRITE TEST DVK WITH LINKED MEDIA FILES
			Dvk linked_dvk = new Dvk();
			linked_dvk.set_dvk_file(new File(this.temp_dir.getRoot(), "ld.dvk"));
			linked_dvk.set_dvk_id("LD246");
			linked_dvk.set_title("Linked DVK");
			linked_dvk.set_artist("Artist");
			linked_dvk.set_page_url("/url/");
			linked_dvk.set_media_file("nm.jpg");
			InOut.write_file(linked_dvk.get_media_file(), "Primary");
			linked_dvk.set_secondary_file("nm.png");
			InOut.write_file(linked_dvk.get_secondary_file(), "Secondary");
			linked_dvk.write_dvk();
			//TEST THAT THE TEST FILES WERE WRITTEN PROPERLY
			assertTrue(no_media_dvk.get_dvk_file().exists());
			assertTrue(linked_dvk.get_dvk_file().exists());
			assertTrue(linked_dvk.get_media_file().exists());
			assertTrue(linked_dvk.get_secondary_file().exists());
			//RENAME DVK FILES AND LINKED MEDIA
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			Reformat.rename_files(dvk_handler);
			ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(2, dvks.size());
			//TEST LINKED_DVK AND MEDIA WERE RENAMED
			assertEquals(this.temp_dir.getRoot(), dvks.get(0).get_dvk_file().getParentFile());
			assertEquals("Linked DVK_LD246.dvk", dvks.get(0).get_dvk_file().getName());
			assertTrue(dvks.get(0).get_dvk_file().exists());
			assertEquals(this.temp_dir.getRoot(), dvks.get(0).get_media_file().getParentFile());
			assertEquals("Linked DVK_LD246.txt", dvks.get(0).get_media_file().getName());
			assertTrue(dvks.get(0).get_media_file().exists());
			assertEquals(this.temp_dir.getRoot(), dvks.get(0).get_secondary_file().getParentFile());
			assertEquals("Linked DVK_LD246_S.txt", dvks.get(0).get_secondary_file().getName());
			assertTrue(dvks.get(0).get_secondary_file().exists());
			//TEST THAT NO_MEDIA_DVK WAS RENAMED
			assertEquals(this.temp_dir.getRoot(), dvks.get(1).get_dvk_file().getParentFile());
			assertEquals("No Media_NM123.dvk", dvks.get(1).get_dvk_file().getName());
			//CHECK DVK FILE INFORMATION IS UNCHANGED
			dvk_handler.read_dvks(this.temp_dir.getRoot());
			dvks = dvk_handler.get_dvks('a', false, false);
			assertEquals(2, dvks.size());
			assertEquals("LD246", dvks.get(0).get_dvk_id());
			assertEquals("Linked DVK", dvks.get(0).get_title());
			assertEquals(1, dvks.get(0).get_artists().length);
			assertEquals("Artist", dvks.get(0).get_artists()[0]);
			assertEquals("/url/", dvks.get(0).get_page_url());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
}
