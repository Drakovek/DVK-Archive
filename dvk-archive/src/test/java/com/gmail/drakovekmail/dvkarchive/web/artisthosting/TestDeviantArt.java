package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.file.InOut;

/**
 * Unit tests for the DeviantArt class.
 * 
 * @author Drakovek
 */
public class TestDeviantArt {
	
	/**
	 * Directory for holding test files.
	 */
	@Rule
	public TemporaryFolder temp_dir = new TemporaryFolder();
	
	/**
	 * DeviantArt class for testing.
	 */
	private DeviantArt dev;
	
	/**
	 * Sets up DeviantArt class and test files.
	 */
	@Before
	public void set_up() {
		this.dev = new DeviantArt();

	}
	
	/**
	 * Closes DeviantArt class and removes test files.
	 */
	@After
	public void tear_down() {
		this.dev.close();
	}
	
	/**
	 * Tests the get_page_id method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_page_id() {
		String id;
		String url;
		//GALLERY URL
		url = "www.deviantart.com/pokefan-tf/art/Anthro-Incineroar-TF-TG-831012876/";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA831012876", id);
		url = "www.deviantart.com/artist/art/thing-1579354";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA1579354", id);
		url = "deviantart.com/thing/art/1579354";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA1579354", id);
		//JOURNAL URLS
		url = "https://www.deviantart.com/artist/journal/title-12357";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA12357-J", id);
		url = "deviantart.com/artist/journal/title-54321";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA54321-J", id);
		url = "deviantart.com/bleh/journal/2561024";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA2561024-J", id);
		//STATUS URLS
		url = "https://www.deviantart.com/artist/status-update/12345";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA12345-S", id);
		url = "www.deviantart.com/artist/status-update/5876";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA5876-S", id);
		url = "deviantart.com/artist/status/1800215";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA1800215-S", id);
		//POLL URLS
		url = "https://www.deviantart.com/akuoreo/poll/title-thing-87659";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA87659-P", id);
		url = "www.deviantart.com/akuoreo/poll/thing-2500";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA2500-P", id);
		url = "deviantart.com/akuoreo/poll/86256";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("DVA86256-P", id);
		//INVALID URLS
		url = "www.nope.com/bleh/art/thing-1579354";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("", id);
		url = "https://www.deviantart.com/thing/bleh/thing-1579354";
		id = DeviantArt.get_page_id(url, false);
		assertEquals("", id);
		url = "https://www.deviantart.com/thing/art/";
		id = DeviantArt.get_page_id(url, true);
		assertEquals("", id);
		id = DeviantArt.get_page_id(null, true);
		assertEquals("", id);
		//TEST NO ENDS
		url = "www.deviantart.com/artist/art/thing-1579354";
		id = DeviantArt.get_page_id(url, false);
		assertEquals("1579354", id);
		url = "www.deviantart.com/akuoreo/poll/thing-2500";
		id = DeviantArt.get_page_id(url, false);
		assertEquals("2500", id);
		url = "deviantart.com/artist/journal/title-54321";
		id = DeviantArt.get_page_id(url, false);
		assertEquals("54321", id);
	}
	
	/**
	 * Tests all DeviantArt methods that require login.
	 */
	@Test
	public void test_login_methods() {
		//LOGIN
		this.dev.initialize_connect();
		String[] info = this.dev.get_user_info("DeviantArt", null);
		this.dev.login(info[0], info[1]);
		info = null;
		assertTrue(this.dev.is_logged_in());
		//TEST GETTING JOURNAL DVK
		test_get_journal_dvk();
		//TEST GETTING MODULE PAGES
		test_get_module_pages();
		//TEST GETTING FAVORITES
		test_get_favorites_pages();
		//TEST GETTING GALLERY PAGES
		test_get_pages();
		//TEST GETTING MEDIA DVK
		test_get_dvk();
	}
	
	/**
	 * Tests the get_dvk method.
	 */
	public void test_get_dvk() {
		//CREATE TEST DIR
		File dvk_dir = null;
		try {
			dvk_dir = this.temp_dir.newFolder("get_dvk");
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//CREATE TEST DVK
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("DVA1234567");
		dvk.set_title("Test");
		dvk.set_artist("person");
		dvk.set_page_url("/page/");
		dvk.set_dvk_file(new File(dvk_dir, "test.dvk"));
		dvk.set_media_file("test.png");
		dvk.write_dvk();
		File[] dirs = {dvk_dir};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(dvk_dir);
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
			//TEST FAVORITING ALREADY DOWNLOADED DVK
			String url = "deviantart.com/artist/art/Thing-1234567";
			dvk = this.dev.get_dvk(url, dvk_handler, null, dvk_dir, "Somebody", true);
			assertEquals("Test", dvk.get_title());
			assertEquals(1, dvk.get_web_tags().length);
			assertEquals("Favorite:Somebody", dvk.get_web_tags()[0]);
			assertTrue(dvk.get_dvk_file().exists());
			//FIRST DVK - IMAGE
			url = "deviantart.com/pokefan-tf/art/Anthro-Incineroar-TF-TG-831012876";
			dvk = this.dev.get_dvk(
					url, dvk_handler, "Gallery:Main", dvk_dir, "Somebody", 0, true, false);
			assertEquals("DVA831012876", dvk.get_dvk_id());
			url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Incineroar-TF-TG-831012876";
			assertEquals(url, dvk.get_page_url());
			assertEquals("Anthro Incineroar TF/TG", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Pokefan-Tf", dvk.get_artists()[0]);
			assertEquals("2020/02/19|09:17", dvk.get_time());
			assertEquals(19, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Main", dvk.get_web_tags()[1]);
			assertEquals("Fan Art", dvk.get_web_tags()[2]);
			assertEquals("Cartoons & Comics", dvk.get_web_tags()[3]);
			assertEquals("Digital", dvk.get_web_tags()[4]);
			assertEquals("Games", dvk.get_web_tags()[5]);
			assertEquals("anthro", dvk.get_web_tags()[6]);
			assertEquals("f2m", dvk.get_web_tags()[7]);
			assertEquals("ftm", dvk.get_web_tags()[8]);
			assertEquals("furry", dvk.get_web_tags()[9]);
			assertEquals("pokemon", dvk.get_web_tags()[10]);
			assertEquals("tf", dvk.get_web_tags()[11]);
			assertEquals("tg", dvk.get_web_tags()[12]);
			assertEquals("transformation", dvk.get_web_tags()[13]);
			assertEquals("transgender", dvk.get_web_tags()[14]);
			assertEquals("tftg", dvk.get_web_tags()[15]);
			assertEquals("tgtf", dvk.get_web_tags()[16]);
			assertEquals("DVK:Single", dvk.get_web_tags()[17]);
			assertEquals("Favorite:Somebody", dvk.get_web_tags()[18]);
			String desc = "<div> Magical belt + girl = buff fire cat. </div> <div> <br/> </div> "
					+ "<div> I know Incineroar's kind of anthro already, but shh <br/> </div>";
			assertEquals(desc, dvk.get_description());
			url = "https://www.deviantart.com/download/831012876/ddqrhn0-6ba2165d-854d-42a0-b1bd-f1fa0b57ce01.png?";
			assertTrue(dvk.get_direct_url().startsWith(url));
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Anthro Incineroar TF-TG_DVA831012876.dvk", dvk.get_dvk_file().getName());
			assertEquals("Anthro Incineroar TF-TG_DVA831012876.png", dvk.get_media_file().getName());
			assertEquals(null, dvk.get_secondary_file());
			//SECOND DVK - LITERATURE
			url = "https://www.deviantart.com/legomax98/art/Finding-One-s-True-Self-770569130";
			dvk = this.dev.get_dvk(url, dvk_handler, null, dvk_dir, null, false);
			assertEquals("DVA770569130", dvk.get_dvk_id());
			url = "https://www.deviantart.com/legomax98/art/Finding-One-s-True-Self-770569130";
			assertEquals(url, dvk.get_page_url());
			assertEquals("Finding One's True Self", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Legomax98", dvk.get_artists()[0]);
			assertEquals("2018/10/30|20:33", dvk.get_time());
			assertEquals(12, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Literature", dvk.get_web_tags()[1]);
			assertEquals("Prose", dvk.get_web_tags()[2]);
			assertEquals("Fiction", dvk.get_web_tags()[3]);
			assertEquals("Fantasy", dvk.get_web_tags()[4]);
			assertEquals("Short Stories", dvk.get_web_tags()[5]);
			assertEquals("female", dvk.get_web_tags()[6]);
			assertEquals("transformation", dvk.get_web_tags()[7]);
			assertEquals("animaltransformation", dvk.get_web_tags()[8]);
			assertEquals("wolftransformation", dvk.get_web_tags()[9]);
			assertEquals("animaltfstory", dvk.get_web_tags()[10]);
			assertEquals("feraltransformation", dvk.get_web_tags()[11]);
			desc = "My first story of hopefully many to come. A young woman travels to a place she always "
					+ "wanted to visit and ends up staying a bit longer than expected";
			assertEquals(desc, dvk.get_description());
			assertEquals(null, dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Finding One-s True Self_DVA770569130.dvk", dvk.get_dvk_file().getName());
			assertEquals("Finding One-s True Self_DVA770569130.html", dvk.get_media_file().getName());
			assertEquals(null, dvk.get_secondary_file());
			assertTrue(dvk.get_media_file().exists());
			String file = InOut.read_file(dvk.get_media_file());
			assertTrue(file.startsWith("<!DOCTYPE html><html><span> <p> <span> "
					+ "To say that Lisa enjoyed the outdoors"));
			assertTrue(file.endsWith("bounded off into the forest. </span> </p> <div> <span> "
					+ "<br/> </span> </div> </span> </html>"));
			assertTrue(file.contains("A voice in the back of her mind told her this wasn’t normal wolf behavior"));
			//THIRD DVK - LITERATURE
			url = "http://www.deviantart.com/pokefan-tf/art/Pokeclipse-TF-RP-700042244";
			dvk = this.dev.get_dvk(url, dvk_handler, "Gallery:Scraps", dvk_dir, null, 0, true, true);
			assertEquals("DVA700042244", dvk.get_dvk_id());
			url = "https://www.deviantart.com/pokefan-tf/art/Pokeclipse-TF-RP-700042244";
			assertEquals(url, dvk.get_page_url());
			assertEquals("Pokeclipse! (TF RP)", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Pokefan-Tf", dvk.get_artists()[0]);
			assertEquals("2017/08/20|21:16", dvk.get_time());
			assertEquals(13, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Scraps", dvk.get_web_tags()[1]);
			assertEquals("DeviantArt Related", dvk.get_web_tags()[2]);
			assertEquals("Devious Fun", dvk.get_web_tags()[3]);
			assertEquals("Other", dvk.get_web_tags()[4]);
			assertEquals("rp", dvk.get_web_tags()[5]);
			assertEquals("tf", dvk.get_web_tags()[6]);
			assertEquals("eclipse", dvk.get_web_tags()[7]);
			assertEquals("pokemon", dvk.get_web_tags()[8]);
			assertEquals("roleplay", dvk.get_web_tags()[9]);
			assertEquals("transformation", dvk.get_web_tags()[10]);
			assertEquals("tfrp", dvk.get_web_tags()[11]);
			assertEquals("DVK:Single", dvk.get_web_tags()[12]);
			assertEquals(null, dvk.get_description());
			assertEquals(null, dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Pokeclipse TF RP_DVA700042244.dvk", dvk.get_dvk_file().getName());
			assertEquals("Pokeclipse TF RP_DVA700042244.html", dvk.get_media_file().getName());
			assertTrue(dvk.get_media_file().exists());
			file = InOut.read_file(dvk.get_media_file());
			desc = "<!DOCTYPE html><html>A solar eclipse is something that is amazing to experience... "
					+ "And we happen to be able to see the entire thing! <br/> <br/> ...however, as you "
					+ "(and / or me) soon find out, this eclipse is different than normal ones. "
					+ "<br/> <br/> <b> Rules </b> <br/> <br/> 1. This is a Pokemon TF rp... meaning "
					+ "only pokemon are allowed. <br/> 2. Allowed side effects are TG, AP, AR, MC, "
					+ "and anthro. Anything else, please ask. <br/> 3. Tell me the answer to 5 "
					+ "times 3 in your first comment. <br/> 4. Put effort into your replies. No \"...\" "
					+ "or anything like that. <br/> 5. Have fun! </html>";
			assertEquals(desc, file);
			//FOURTH DVK - VIDEO
			url = "http://www.deviantart.com/fujoshiineko/art/Calem-s-Noivern-TF-786108284";
			dvk = this.dev.get_dvk(
					url, dvk_handler, "Gallery:Scraps", dvk_dir, "Person", 0, false, false);
			assertEquals("DVA786108284", dvk.get_dvk_id());
			url = "https://www.deviantart.com/fujoshiineko/art/Calem-s-Noivern-TF-786108284";
			assertEquals(url, dvk.get_page_url());
			assertEquals("Calem's Noivern TF", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("FujoshiiNeko", dvk.get_artists()[0]);
			assertEquals("2019/02/17|17:19", dvk.get_time());
			assertEquals(11, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Scraps", dvk.get_web_tags()[1]);
			assertEquals("Film & Animation", dvk.get_web_tags()[2]);
			assertEquals("Other", dvk.get_web_tags()[3]);
			assertEquals("calem", dvk.get_web_tags()[4]);
			assertEquals("pokémon", dvk.get_web_tags()[5]);
			assertEquals("tf", dvk.get_web_tags()[6]);
			assertEquals("transfur", dvk.get_web_tags()[7]);
			assertEquals("xandy", dvk.get_web_tags()[8]);
			assertEquals("noivern", dvk.get_web_tags()[9]);
			assertEquals("Favorite:Person", dvk.get_web_tags()[10]);
			desc = "[Insert Story Here] <br/> <br/> Did a animatic. Doing the whole thing would be too "
					+ "time consuming so did just the tail instead <br/> <br/> For some reason, I can't "
					+ "upload a gif on DA so looping would be impossible. Unless some kind soul could "
					+ "tell me how to";
			assertEquals(desc, dvk.get_description());
			url = "https://wixmp-ed30a86b8c4ca887773594c2.wixmp.com/v/mp4/1d735619-8d85-4a96-a2ee-82852d4551f0/"
					+ "dd01118-e720f3db-1c53-4545-9e41-889295ea4c6e.720p.50714fd3435c49dba3d67d067bcb076c.mp4";
			assertEquals(url, dvk.get_direct_url());
			url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/1d735619-8d85-4a96-a2ee-"
					+ "82852d4551f0/dd01118-49161b67-ecdc-4406-bb89-62212b2d6cda.jpg/v1/fit/w_300,h_900,q_70,"
					+ "strp/calem_s_noivern_tf_by_fujoshiineko_dd01118-300w.jpg";
			assertEquals(url, dvk.get_secondary_url());
			assertEquals("Calem-s Noivern TF_DVA786108284.dvk", dvk.get_dvk_file().getName());
			assertEquals("Calem-s Noivern TF_DVA786108284.mp4", dvk.get_media_file().getName());
			assertEquals("Calem-s Noivern TF_DVA786108284_S.jpg", dvk.get_secondary_file().getName());
			//FIFTH DVK - SWF
			url = "deviantart.com/doom-the-wolf/art/Interactive-dragoness-transformation-298825987";
			dvk = this.dev.get_dvk(
					url, dvk_handler, "Gallery:Scraps", dvk_dir, "Somebody", 0, false, false);
			assertEquals("DVA298825987", dvk.get_dvk_id());
			url = "https://www.deviantart.com/doom-the-wolf/art/Interactive-dragoness-transformation-298825987";
			assertEquals(url, dvk.get_page_url());
			assertEquals("Interactive dragoness transformation", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Doom-the-wolf", dvk.get_artists()[0]);
			assertEquals("2012/04/27|23:56", dvk.get_time());
			assertEquals(5, dvk.get_web_tags().length);
			assertEquals("Rating:Mature", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Scraps", dvk.get_web_tags()[1]);
			assertEquals("Flash", dvk.get_web_tags()[2]);
			assertEquals("Interactive", dvk.get_web_tags()[3]);
			assertEquals("Favorite:Somebody", dvk.get_web_tags()[4]);
			desc = "<b> Update: </b> <i> I improved the background. In the previous version is was just a "
					+ "scribble. </i> <br/> <br/> <br/> Here's an animation of a kind I haven't really made "
					+ "before. I think I can confidently state that this is my best transformation "
					+ "animation. I've made transformation animations before, but this one is different. "
					+ "And it's not just because she isn't a wolf. <br/> <br/> In this animation, the girl "
					+ "doesn't just go through a single long change. Instead, the changes go happening "
					+ "part by part. You can zoom in and out of the animation, or move the camera around. "
					+ "I gave the world a bit of depth, too. <br/> <br/> And finally, notice the buttons "
					+ "don't disappear when the transformation happens (they just become transparent). You "
					+ "can pause the transformation at any time by pressing the button again. <br/> <br/> "
					+ "I worked a long time on this animation. I hope you enjoy it.";
			assertEquals(desc, dvk.get_description());
			url = "https://www.deviantart.com/download/298825987/d4xwvlv-f53cf86b-7dfb-4b77-a639-45087a54d165.swf?";
			assertTrue(dvk.get_direct_url().startsWith(url));
			url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/d4558240-52d8-48b1-9de7-26fcb6f24db3/"
					+ "d4xwvlv-dcce8bba-00a8-414f-9bd9-3e8be2326916.jpg";
			assertEquals(url, dvk.get_secondary_url());
			assertEquals("Interactive dragoness transformation_DVA298825987.dvk", dvk.get_dvk_file().getName());
			assertEquals("Interactive dragoness transformation_DVA298825987.swf", dvk.get_media_file().getName());
			assertEquals("Interactive dragoness transformation_DVA298825987_S.jpg", dvk.get_secondary_file().getName());
			//SIXTH DVK - UNDOWNLOADABLE
			url = "https://www.deviantart.com/drakovek/art/Drakovek-839354922";
			dvk = this.dev.get_dvk(url, dvk_handler, "Gallery:Main", dvk_dir, null, 0, false, false);
			assertEquals("DVA839354922", dvk.get_dvk_id());
			url = "https://www.deviantart.com/drakovek/art/Drakovek-839354922";
			assertEquals(url, dvk.get_page_url());
			assertEquals("Drakovek", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("drakovek", dvk.get_artists()[0]);
			assertEquals("2020/04/25|10:39", dvk.get_time());
			assertEquals(13, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Main", dvk.get_web_tags()[1]);
			assertEquals("Visual Art", dvk.get_web_tags()[2]);
			assertEquals("male", dvk.get_web_tags()[3]);
			assertEquals("vector", dvk.get_web_tags()[4]);
			assertEquals("anthropomorphic", dvk.get_web_tags()[5]);
			assertEquals("cheetah", dvk.get_web_tags()[6]);
			assertEquals("digital", dvk.get_web_tags()[7]);
			assertEquals("digitalillustration", dvk.get_web_tags()[8]);
			assertEquals("inkscape", dvk.get_web_tags()[9]);
			assertEquals("portrait", dvk.get_web_tags()[10]);
			assertEquals("profile", dvk.get_web_tags()[11]);
			assertEquals("vectorillustration", dvk.get_web_tags()[12]);
			desc = "<div> Hey, I actually posted something! How about that? </div> <div> <br/> </div> "
					+ "<div> Well, I'm pretty stubborn, and I held out on making a fursona for years "
					+ "for various reasons, but I finally caved. Well, sort of at least. I'm not really "
					+ "dedicated to having this be a recurring character or anything. I just felt like "
					+ "drawing myself as a cat, I suppose. <br/> </div>";
			assertEquals(desc, dvk.get_description());
			url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/a5ad25e4-56ef-4e95-88b4-"
					+ "4c59f7bca389/ddvqaei-cd05fa1d-51f8-4cf1-99b8-27b29ca3e59f.png?";
			assertTrue(dvk.get_direct_url().startsWith(url));
			assertEquals("Drakovek_DVA839354922.dvk", dvk.get_dvk_file().getName());
			assertEquals("Drakovek_DVA839354922.png", dvk.get_media_file().getName());
			assertEquals(null, dvk.get_secondary_file());
			//SEVENTH DVK - UNDOWNLOADABLE SWF
			url = "www.deviantart.com/horsuhanon/art/TF-Mask-777228001";
			dvk = this.dev.get_dvk(url, dvk_handler, "Gallery:Main", dvk_dir, null, 0, true, false);
			assertEquals("DVA777228001", dvk.get_dvk_id());
			url = "https://www.deviantart.com/horsuhanon/art/TF-Mask-777228001";
			assertEquals(url, dvk.get_page_url());
			assertEquals("TF: Mask", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("HorsuhAnon", dvk.get_artists()[0]);
			assertEquals("2018/12/18|10:02", dvk.get_time());
			assertEquals(8, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Main", dvk.get_web_tags()[1]);
			assertEquals("Flash", dvk.get_web_tags()[2]);
			assertEquals("Animations", dvk.get_web_tags()[3]);
			assertEquals("tf", dvk.get_web_tags()[4]);
			assertEquals("animation", dvk.get_web_tags()[5]);
			assertEquals("transformation", dvk.get_web_tags()[6]);
			assertEquals("DVK:Single", dvk.get_web_tags()[7]);
			desc = "Commissioned by <span> <span class=\"username-with-symbol u\"> <a class=\"u regular "
					+ "username\" href=\"https://www.deviantart.com/onyxsteelgray1213\"> "
					+ "OnyxSteelGray1213 </a> <span class=\"user-symbol regular\" data-quicktip-"
					+ "text=\"\" data-show-tooltip=\"\" data-gruser-type=\"regular\"> </span> </span> </span>";
			assertEquals(desc, dvk.get_description());
			assertEquals(null, dvk.get_direct_url());
			url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/976aef10-4f8c-47ce-"
					+ "8b7c-073393f2b5cc/dcuqoyp-25c8e83b-3eb8-4834-ad2c-a17b5bff76c0.jpg";
			assertEquals(url, dvk.get_secondary_url());
			assertEquals("TF Mask_DVA777228001.dvk", dvk.get_dvk_file().getName());
			assertEquals("TF Mask_DVA777228001", dvk.get_media_file().getName());
			assertEquals("TF Mask_DVA777228001_S.jpg", dvk.get_secondary_file().getName());
			//TEST PDF
			url = "ww.deviantart.com/inkblot123/art/Mind-Your-Manors-Story-742045791";
			dvk = this.dev.get_dvk(url, dvk_handler, "Gallery:Main", dvk_dir, null, 0, false, true);
			url = "https://www.deviantart.com/inkblot123/art/Mind-Your-Manors-Story-742045791";
			assertEquals(url, dvk.get_page_url());
			assertEquals("DVA742045791", dvk.get_dvk_id());
			assertEquals("Mind Your Manors (Story)", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Inkblot123", dvk.get_artists()[0]);
			assertEquals("2018/04/25|06:13", dvk.get_time());
			assertEquals(20, dvk.get_web_tags().length);
			assertEquals("Rating:Mature", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Main", dvk.get_web_tags()[1]);
			assertEquals("Literature", dvk.get_web_tags()[2]);
			assertEquals("Prose", dvk.get_web_tags()[3]);
			assertEquals("Fiction", dvk.get_web_tags()[4]);
			assertEquals("Fantasy", dvk.get_web_tags()[5]);
			assertEquals("Introductions & Chapters", dvk.get_web_tags()[6]);
			assertEquals("dragon", dvk.get_web_tags()[7]);
			assertEquals("female", dvk.get_web_tags()[8]);
			assertEquals("feral", dvk.get_web_tags()[9]);
			assertEquals("girl", dvk.get_web_tags()[10]);
			assertEquals("magic", dvk.get_web_tags()[11]);
			assertEquals("maid", dvk.get_web_tags()[12]);
			assertEquals("serpent", dvk.get_web_tags()[13]);
			assertEquals("storytelling", dvk.get_web_tags()[14]);
			assertEquals("tf", dvk.get_web_tags()[15]);
			assertEquals("transformation", dvk.get_web_tags()[16]);
			assertEquals("woman", dvk.get_web_tags()[17]);
			assertEquals("sea_serpent", dvk.get_web_tags()[18]);
			assertEquals("aquatic_dragon", dvk.get_web_tags()[19]);
			desc = "Ok, so this&#160;tf&#160;is the culmination of a lot of stuff,"
					+ " so bear with me while I list em' all.";
			assertTrue(dvk.get_description().startsWith(desc));
			desc = "Buntlys weren’t one of the best </q> </span> </a> </span> "
					+ "<!-- ^TTT --> <!-- TTT$ --> </span>";
			assertTrue(dvk.get_description().endsWith(desc));
			url = "https://api-da.wixmp.com/_api/download/file?downloadToken=";
			assertTrue(dvk.get_direct_url().startsWith(url));
			url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/1ed28470-c30d-4506-"
					+ "9cd2-b8c48b29190e/dc9sm73-4abb5d54-a584-4dc5-a714-3f86db88cd81.png/v1/fill"
					+ "/w_1326,h_603,q_70,strp/mind_your_manors__story__by_inkblot123_dc9sm73-pre.jpg";
			assertEquals(url, dvk.get_secondary_url());
			assertEquals("Mind Your Manors Story_DVA742045791.dvk", dvk.get_dvk_file().getName());
			assertEquals("Mind Your Manors Story_DVA742045791.pdf", dvk.get_media_file().getName());
			assertEquals("Mind Your Manors Story_DVA742045791_S.jpg", dvk.get_secondary_file().getName());
			assertTrue(dvk.get_dvk_file().exists());
			assertTrue(dvk.get_media_file().exists());
			assertTrue(dvk.get_secondary_file().exists());
			//TEST PREMIUM CONTENT
			url = "https://www.deviantart.com/fluffytg/art/Then-Be-A-Girl-30-872563927";
			dvk = this.dev.get_dvk(url, dvk_handler, "Gallery:Main", dvk_dir, null, 0, false, false);
			assertEquals(url, dvk.get_page_url());
			assertEquals("DVA872563927", dvk.get_dvk_id());
			assertEquals("Then Be A Girl~! #30", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("FluffyTG", dvk.get_artists()[0]);
			assertEquals("2021/03/07|21:17", dvk.get_time());
			assertEquals(21, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Main", dvk.get_web_tags()[1]);
			assertEquals("Visual Art", dvk.get_web_tags()[2]);
			assertEquals("crossdressing", dvk.get_web_tags()[3]);
			assertEquals("cuteadorable", dvk.get_web_tags()[4]);
			assertEquals("femboy", dvk.get_web_tags()[5]);
			assertEquals("genderbender", dvk.get_web_tags()[6]);
			assertEquals("genderidentity", dvk.get_web_tags()[7]);
			assertEquals("kids", dvk.get_web_tags()[8]);
			assertEquals("manga", dvk.get_web_tags()[9]);
			assertEquals("tg", dvk.get_web_tags()[10]);
			assertEquals("trans", dvk.get_web_tags()[11]);
			assertEquals("transgender", dvk.get_web_tags()[12]);
			assertEquals("maletofemale", dvk.get_web_tags()[13]);
			assertEquals("tgtf", dvk.get_web_tags()[14]);
			assertEquals("slice_of_life", dvk.get_web_tags()[15]);
			assertEquals("maletofemaletg", dvk.get_web_tags()[16]);
			assertEquals("toonfawn", dvk.get_web_tags()[17]);
			assertEquals("fluffytg", dvk.get_web_tags()[18]);
			assertEquals("fluffy_tg", dvk.get_web_tags()[19]);
			assertEquals("Premium Content", dvk.get_web_tags()[20]);
			desc = "<span> Then Be A Girl~! - #30 <br/> <br/> Early Access to this series is only $3 a month "
					+ "on patreon! <br/> <br/> <br/> =============================== <br/> <p> <b> Then Be A "
					+ "Girl~!&#160; </b> is both a slice of life and coming of age story, filled with cute " 
					+ "and fluffy feelings! Rei and Bambi are two kids that have known each other since "
					+ "kindergarten, who hang out and play almost every day! When Rei blurts out that he wants "
					+ "to be a girl to her, Bambi wastes no time putting him into her clothes. But neither of "
					+ "them knew, just how deeply that their lives would begin to change from that one moment. "
					+ "<br/> <br/> This series will be simple fun revolving around two kids, and a loose story "
					+ "overall. <br/> </p> <p> Written By&#160; <a target=\"_self\" href=\"https://www.deviantart"
					+ ".com/fluffytg\"> <img class=\"avatar\" width=\"50\" height=\"50\" src=\"https://a.deviantart"
					+ ".net/avatars/f/l/fluffytg.png?2\" alt=\":iconfluffytg:\" title=\"FluffyTG\"/> </a> <br/> </p>"
					+ " <p> Illustrated By&#160; <a target=\"_self\" href=\"https://www.deviantart.com/toon-fawn\"> "
					+ "<img class=\"avatar\" width=\"50\" height=\"50\" src=\"https://a.deviantart.net/avatars/t/o/"
					+ "toon-fawn.jpg?3\" alt=\":icontoon-fawn:\" title=\"Toon-Fawn\"/> </a> </p> </span> <div> <div> "
					+ "<div align=\"-webkit-center\"> <div> <div align=\"-webkit-center\"> &#160;&#160; <br/> For the "
					+ "early access and special content, support us on Patreon~!&#160; <br/> <a href=\"https://www."
					+ "deviantart.com/users/outgoing?https://www.patreon.com/MagicalTrans\"> </a> <a href=\"https://www."
					+ "deviantart.com/users/outgoing?https://www.patreon.com/FluffyTG\"> </a> <a class=\"external\" "
					+ "href=\"https://www.deviantart.com/users/outgoing?https://www.patreon.com/FluffyTG\"> www.patreon."
					+ "com/FluffyTG </a> &#160;~ <br/> <br/> <br/> </div> <div align=\"-webkit-center\"> &#160; <br/> "
					+ "Wanna chat? Get updates? JOIN OUR DISCORD&#160; <a class=\"external\" href=\"https://www.deviantart"
					+ ".com/users/outgoing?https://discord.gg/fa6J8GWHu9\"> CHANNEL </a> &#160;~ <br/> <br/> &#160; <br/> "
					+ "<br/> And Don't Forget To Follow Us On Twitter! <br/> <a href=\"https://www.deviantart.com/users"
					+ "/outgoing?https://twitter.com/Fluffy_TG\"> </a> <a class=\"external\" href=\"https://www.deviantart"
					+ ".com/users/outgoing?https://twitter.com/Fluffy_TG\"> twitter.com/Fluffy_TG </a> <br/> </div> </div> "
					+ "<div> <br/> </div> </div> </div> <div> <br/> </div> </div>";
			assertEquals(desc, dvk.get_description());
			assertEquals(null, dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Then Be A Girl 30_DVA872563927.dvk", dvk.get_dvk_file().getName());
			assertEquals("Then Be A Girl 30_DVA872563927", dvk.get_media_file().getName());
			assertEquals(null, dvk.get_secondary_file());
			//TEST INVALID DVK
			url = "https://www.deviantart.com/bleh/art/Nonexistant-1729873234598082498510";
			try {
				dvk = this.dev.get_dvk(url, dvk_handler, "Gallery:Main", dvk_dir, null, 0, false, false);
				assertTrue(false);
			}
			catch(DvkException f) {}
			//TEST DVKS ADDED TO DVK HANDLER
			ArrayList<Dvk> dvks = dvk_handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(10, dvks.size());
			assertEquals("Anthro Incineroar TF/TG", dvks.get(0).get_title());
			assertEquals("Calem's Noivern TF", dvks.get(1).get_title());
			assertEquals("Drakovek", dvks.get(2).get_title());
			assertEquals("Finding One's True Self", dvks.get(3).get_title());
			assertEquals("Interactive dragoness transformation", dvks.get(4).get_title());
			assertEquals("Mind Your Manors (Story)", dvks.get(5).get_title());
			assertEquals("Pokeclipse! (TF RP)", dvks.get(6).get_title());
			assertEquals("Test", dvks.get(7).get_title());
			assertEquals("TF: Mask", dvks.get(8).get_title());
			assertEquals("Then Be A Girl~! #30", dvks.get(9).get_title());
		}
		catch(DvkException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/**
	 * Tests the get_journal method.
	 */
	public void test_get_journal_dvk() {
		File jnl_dir = null;
		try {
			jnl_dir = this.temp_dir.newFolder("jounal_dvk");
		}
		catch(IOException e) {
			assertTrue(false);
		}
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(jnl_dir);
		File[] dirs = {jnl_dir};
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
			//FIRST DVK
			String url = "deviantart.com/akuoreo/journal/Looking-to-hire-3D-modeler-817361421";
			Dvk dvk = this.dev.get_journal_dvk(
					url, dvk_handler, jnl_dir, "ArtGuy", 0, true, false);
			assertEquals("DVA817361421-J", dvk.get_dvk_id());
			assertEquals("Looking to hire 3D modeler", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("AkuOreo", dvk.get_artists()[0]);
			assertEquals("2019/10/19|11:53", dvk.get_time());
			assertEquals(6, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Journals", dvk.get_web_tags()[1]);
			assertEquals("Journals", dvk.get_web_tags()[2]);
			assertEquals("Personal", dvk.get_web_tags()[3]);
			assertEquals("DVK:Single", dvk.get_web_tags()[4]);
			assertEquals("Favorite:ArtGuy", dvk.get_web_tags()[5]);
			String desc = "<div> We have wanted to use the money we have gotten to commission more "
					+ "MSF High related stuff";
			assertTrue(dvk.get_description().startsWith(desc));
			desc = "It will be posted for free on my youtube channel once done. </q> </span> </a> </span> "
					+ "<!-- ^TTT --> <!-- TTT$ --> </span> </div> <div> <br/> </div> <br/> <div> <br/> "
					+ "</div> <div> <br/> </div> <div> <br/> </div> <div> <br/> </div> <div> <br/> </div> "
					+ "<div> <br/> </div> <div> <br/> </div>";
			assertTrue(dvk.get_description().endsWith(desc));
			url = "https://www.deviantart.com/akuoreo/journal/Looking-to-hire-3D-modeler-817361421";
			assertEquals(url, dvk.get_page_url());
			assertEquals(null, dvk.get_direct_url());
			url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/4b1ad4c6-d4d5-4075-aef2-66b5e4fbe46b/"
					+ "ddimle3-f2eeb66b-6459-4082-9a9a-0b767df12356.jpg/v1/fill/w_1280,h_720,q_75,strp/vice_principal_"
					+ "kasumi_wallpaper_by_akuoreo_ddimle3-fullview.jpg?";
			assertTrue(dvk.get_secondary_url().startsWith(url));
			assertEquals("Looking to hire 3D modeler_DVA817361421-J.dvk", dvk.get_dvk_file().getName());
			assertEquals("Looking to hire 3D modeler_DVA817361421-J.html", dvk.get_media_file().getName());
			assertEquals("Looking to hire 3D modeler_DVA817361421-J_S.jpg", dvk.get_secondary_file().getName());
			//SECOND DVK
			url = "https://www.deviantart.com/akuoreo/journal/Slime-Girl-TF-TG-Deal-Closed-762225768";
			dvk = this.dev.get_journal_dvk(url, dvk_handler, jnl_dir, null, false);
			assertEquals("DVA762225768-J", dvk.get_dvk_id());
			assertEquals("Slime Girl TF TG Deal (Closed)", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("AkuOreo", dvk.get_artists()[0]);
			assertEquals("2018/09/02|03:09", dvk.get_time());
			assertEquals(4, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Journals", dvk.get_web_tags()[1]);
			assertEquals("Journals", dvk.get_web_tags()[2]);
			assertEquals("Personal", dvk.get_web_tags()[3]);
			desc = "<div> I'm having another deal, but for only two people";
			assertTrue(dvk.get_description().startsWith(desc));
			desc = "Please give info on what you want. </b> <br/> </div>";
			assertTrue(dvk.get_description().endsWith(desc));
			url = "https://www.deviantart.com/akuoreo/journal/Slime-Girl-TF-TG-Deal-Closed-762225768";
			assertEquals(url, dvk.get_page_url());
			assertEquals(null, dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Slime Girl TF TG Deal Closed_DVA762225768-J.dvk", dvk.get_dvk_file().getName());
			assertEquals("Slime Girl TF TG Deal Closed_DVA762225768-J.html", dvk.get_media_file().getName());
			assertEquals(null, dvk.get_secondary_file());
			assertTrue(dvk.get_dvk_file().exists());
			assertTrue(dvk.get_media_file().exists());
			String text = InOut.read_file(dvk.get_media_file());
			desc = "<!DOCTYPE html><html><div> I'm having another deal, but for only two people";
			assertTrue(text.startsWith(desc));
			desc = "Please give info on what you want. </b> <br/> </div> </html>";
			assertTrue(text.endsWith(desc));
			//THIRD DVK
			url = "deviantart.com/fujoshiineko/journal/Important-announcement-Future-of-Commissions-839801217";
			dvk = this.dev.get_journal_dvk(url, dvk_handler, jnl_dir, "thing", 0, true, true);
			assertEquals("DVA839801217-J", dvk.get_dvk_id());
			assertEquals("Important announcement!! (Future of Commissions)", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("FujoshiiNeko", dvk.get_artists()[0]);
			assertEquals("2020/04/28|18:40", dvk.get_time());
			assertEquals(6, dvk.get_web_tags().length);
			assertEquals("Rating:General", dvk.get_web_tags()[0]);
			assertEquals("Gallery:Journals", dvk.get_web_tags()[1]);
			assertEquals("Journals", dvk.get_web_tags()[2]);
			assertEquals("Personal", dvk.get_web_tags()[3]);
			assertEquals("DVK:Single", dvk.get_web_tags()[4]);
			assertEquals("Favorite:thing", dvk.get_web_tags()[5]);
			desc = "<div class=\"kaqlz _1zBoF _1KOBw\"> <p id=\"viewer-foo\" class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- "
					+ "_25MYV public-DraftStyleDefault-block-depth0 public-DraftStyleDefault-text-ltr\"> <span class=\"vkIF2 "
					+ "public-DraftStyleDefault-ltr\"> Commission update: what to expect </span> </p> <div id=\"viewer-criq1\" "
					+ "class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- _25MYV public-DraftStyleDefault-block-depth0 public-DraftStyle"
					+ "Default-text-ltr\"> <span class=\"vkIF2 public-DraftStyleDefault-ltr\"> <br/> </span> </div> <p id=\"viewer-"
					+ "a9u9i\" class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- _25MYV public-DraftStyleDefault-block-depth0 public-"
					+ "DraftStyleDefault-text-ltr\"> <span class=\"vkIF2 public-DraftStyleDefault-ltr\"> School hasn't   for me "
					+ "as the start of my 'internship' will begin shortly around May. The thing is that internship is suspended "
					+ "and we're supposed to undertake Entrepreneurship, Community or Assigned project. </span> </p> <div id=\"viewer"
					+ "-fner8\" class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- _25MYV public-DraftStyleDefault-block-depth0 public-Draft"
					+ "StyleDefault-text-ltr\"> <span class=\"vkIF2 public-DraftStyleDefault-ltr\"> <br/> </span> </div> <p id=\"viewer"
					+ "-pbsu\" class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- _25MYV public-DraftStyleDefault-block-depth0 public-DraftStyle"
					+ "Default-text-ltr\"> <span class=\"vkIF2 public-DraftStyleDefault-ltr\"> School hasn't really ended for me as the "
					+ "start of my 'internship' will begin shortly around May. The thing is that internship is suspended and we're supposed "
					+ "to undertake Entrepreneurship, Community or Assigned project. </span> </p> <p id=\"viewer-f8sog\" class=\"XzvDs _208Ie"
					+ " _1tvZk _1iFR7 _2QAo- _25MYV public-DraftStyleDefault-block-depth0 public-DraftStyleDefault-text-ltr\"> <span class=\"vkIF2"
					+ " public-DraftStyleDefault-ltr\"> The school says they'll provide projects for is to do, which really sucks because you're "
					+ "given no time to really enjoy yourself. But the good thing is that I'll be able to have time to do commissions as part "
					+ "of the entrepreneurship (that which I'll ask my lecture if it's a possible option) </span> </p> <div id=\"viewer-b1558\" "
					+ "class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- _25MYV public-DraftStyleDefault-block-depth0 public-DraftStyleDefault-text-ltr\"> "
					+ "<span class=\"vkIF2 public-DraftStyleDefault-ltr\"> <br/> </span> </div> <p id=\"viewer-86if1\" class=\"XzvDs _208Ie _1tvZk "
					+ "_1iFR7 _2QAo- _25MYV public-DraftStyleDefault-block-depth0 public-DraftStyleDefault-text-ltr\"> <span class=\"vkIF2 "
					+ "public-DraftStyleDefault-ltr\"> However, here's the catch. I can't be drawing beans (TFs) all he time. I'll still take "
					+ "some but will not mention that they're part of the commissions I do unless it's absolutely necessary if there isn't any "
					+ "demand for illustrations. </span> </p> <div id=\"viewer-68t5v\" class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- _25MYV public-"
					+ "DraftStyleDefault-block-depth0 public-DraftStyleDefault-text-ltr\"> <span class=\"vkIF2 public-DraftStyleDefault-ltr\"> "
					+ "<br/> </span> </div> <p id=\"viewer-4v98m\" class=\"XzvDs _208Ie _1tvZk _1iFR7 _2QAo- _25MYV public-DraftStyleDefault-"
					+ "block-depth0 public-DraftStyleDefault-text-ltr\"> <span class=\"vkIF2 public-DraftStyleDefault-ltr\"> Some possible "
					+ "options of commissions are character illustration, character sheet reference, storyboarding ideas, animated icons so "
					+ "on and so forth. I hope some of them would be of interest to you, even thou this account mostly focused on beans. If "
					+ "not, please spread the message as it would really help to be able to reach a larger audience. Thank you. </span> </p> </div>";
			assertEquals(desc, dvk.get_description());
			url = "https://www.deviantart.com/fujoshiineko/journal/Important-announcement-Future-of-Commissions-839801217";
			assertEquals(url, dvk.get_page_url());
			assertEquals(null, dvk.get_direct_url());
			assertEquals(null, dvk.get_secondary_url());
			assertEquals("Important announcement Future of Commissions_DVA839801217-J.dvk", dvk.get_dvk_file().getName());
			assertEquals("Important announcement Future of Commissions_DVA839801217-J.html", dvk.get_media_file().getName());
			assertEquals(null, dvk.get_secondary_file());
			assertTrue(dvk.get_dvk_file().exists());
			assertTrue(dvk.get_media_file().exists());
			text = InOut.read_file(dvk.get_media_file());
			desc = "<!DOCTYPE html><html>" + desc + "</html>";
			assertEquals(desc, text);
			//TEST INVALID JOURNAL
			url = "deviantart.com/blah/journal/Invalid-9204980890823453025204598";
			try {
				dvk = this.dev.get_journal_dvk(url, dvk_handler, jnl_dir, "thing", 0, true, true);
				assertTrue(false);
			}
			catch(DvkException f) {}
			//TEST ADDED TO DVK HANDLER
			ArrayList<Dvk> dvks = dvk_handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(3, dvks.size());
			assertEquals("Important announcement!! (Future of Commissions)", dvks.get(0).get_title());
			assertEquals("Looking to hire 3D modeler", dvks.get(1).get_title());
			assertEquals("Slime Girl TF TG Deal (Closed)", dvks.get(2).get_title());
		}
		catch(DvkException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_status_dvk method.
	 */
	@Test
	public void test_get_status_dvk() {
		//CREATE TEST DIRECTORY
		File sts_dir = null;
		try {
			sts_dir = this.temp_dir.newFolder("status_dvk");
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//CREATE INFO DVK
		Dvk dvk = new Dvk();
		dvk.set_artist("Pokefan-Tf");
		dvk.set_time("2020/04/27|16:35");
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/status/15696838");
		dvk.set_description("This is a test");
		//TEST DVK
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(sts_dir);
		File[] dirs = {sts_dir};
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
			Dvk result = DeviantArt.get_status_dvk(dvk, dvk_handler, sts_dir, true);
			assertEquals("DVA15696838-S", result.get_dvk_id());
			assertEquals("27 April 2020 | Pokefan-Tf Update", result.get_title());
			assertEquals(1, result.get_artists().length);
			assertEquals("Pokefan-Tf", result.get_artists()[0]);
			assertEquals(2, result.get_web_tags().length);
			assertEquals("Gallery:Status-Updates", result.get_web_tags()[0]);
			assertEquals("Rating:General", result.get_web_tags()[1]);
			assertEquals("This is a test", result.get_description());
			assertEquals("27 April 2020 - Pokefan-Tf Update_DVA15696838-S.dvk", result.get_dvk_file().getName());
			assertEquals("27 April 2020 - Pokefan-Tf Update_DVA15696838-S.html", result.get_media_file().getName());
			assertTrue(result.get_dvk_file().exists());
			assertTrue(result.get_media_file().exists());
			String text = InOut.read_file(result.get_media_file());
			assertEquals("<!DOCTYPE html><html>This is a test</html>", text);
			//TEST INVALID DATES
			dvk.set_time("asdlf");
			try {
				result = DeviantArt.get_status_dvk(dvk, dvk_handler, sts_dir, true);
				assertTrue(false);
			}
			catch (DvkException f) {}
			//TEST ADDED TO DVK HANDLER
			ArrayList<Dvk> dvks = dvk_handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(1, dvks.size());
			assertEquals("27 April 2020 | Pokefan-Tf Update", dvks.get(0).get_title());
		}
		catch(DvkException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_poll_dvk method.
	 */
	@Test
	public void test_get_poll_dvk() {
		//CREATE TEST DIRECTORY
		File poll_dir = null;
		try {
			poll_dir = this.temp_dir.newFolder("poll_dvks");
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//CREATE INFO DVK
		Dvk dvk = new Dvk();
		dvk.set_title("What do you think?");
		dvk.set_artist("Person");
		dvk.set_page_url("https://www.deviantart.com/bleh/poll/something-58635424");
		dvk.set_time("2018/07/12|05:36");
		String desc = "25<DVK-POLL-SEP>Yes<DVK-POLL-SEP>47<DVK-POLL-SEP>No"
				+ "<DVK-POLL-SEP>5<DVK-POLL-SEP>Maybe<DVK-POLL-SEP>";
		dvk.set_description(desc);
		//TEST DVK
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(poll_dir);
		File[] dirs = {poll_dir};
		try(DvkHandler dvk_handler = new DvkHandler(prefs, dirs, null)) {
			Dvk result = DeviantArt.get_poll_dvk(dvk, dvk_handler, poll_dir, true);
			assertEquals("DVA58635424-P", result.get_dvk_id());
			assertEquals("What do you think?", result.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Person", dvk.get_artists()[0]);
			assertEquals("2018/07/12|05:36", result.get_time());
			assertEquals(2, result.get_web_tags().length);
			assertEquals("Gallery:Polls", result.get_web_tags()[0]);
			assertEquals("Rating:General", result.get_web_tags()[1]);
			desc = "<body><center><h1>What do you think?</h><p><button><center>Yes</br></br><i>25 Votes</i>"
					+ "</center></button></br></br><button><center>No</br></br><i><b>47 Votes</b></i></center>"
					+ "</button></br></br><button><center>Maybe</br></br><i>5 Votes</i></center></button>"
					+ "</p></center></body>";
			assertEquals(desc, result.get_description());
			assertEquals("What do you think_DVA58635424-P.dvk", result.get_dvk_file().getName());
			assertEquals("What do you think_DVA58635424-P.html", result.get_media_file().getName());
			assertTrue(result.get_dvk_file().exists());
			assertTrue(result.get_media_file().exists());
			String text = InOut.read_file(result.get_media_file());
			desc = "<!DOCTYPE html><html>" + desc + "</html>";
			assertEquals(desc, text);
			//TEST INVALID TAGS - TOO FEW
			desc = "12<DVK-POLL-SEP>yes<DVK-POLL-SEP>53<DVK-POLL-SEP>";
			dvk.set_description(desc);
			try {
				result = DeviantArt.get_poll_dvk(dvk, dvk_handler, poll_dir, false);
				assertTrue(false);
			}
			catch(DvkException f) {}
			//TEST INVALID TAGS - VOTES NOT NUMERICAL
			desc = "12<DVK-POLL-SEP>yes<DVK-POLL-SEP>what?<DVK-POLL-SEP>no<DVK-POLL-SEP>";
			dvk.set_description(desc);
			try {
				result = DeviantArt.get_poll_dvk(dvk, dvk_handler, poll_dir, false);
				assertTrue(false);
			}
			catch(DvkException f) {}
			//TEST ADDED TO DVK
			ArrayList<Dvk> dvks = dvk_handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(1, dvks.size());
			assertEquals("What do you think?", dvks.get(0).get_title());
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_pages method.
	 */
	public void test_get_pages() {
		//CREATE TEST DIRECTORIES
		File page_dir = null;
		try {
			page_dir = this.temp_dir.newFolder("page_dvks");
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("DVA819638523");
		dvk.set_title("Draconic Amulet");
		dvk.set_artist("Pokefan-Tf");
		String[] tags = {"Tags", "stuff", "thing"};
		dvk.set_web_tags(tags);
		String url = "https://www.deviantart.com/pokefan-tf/art/A-Draconic-Amulet-TF-RP-819638523";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(page_dir, "amulet.dvk"));
		dvk.set_media_file("amulet.png");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk = new Dvk();
		dvk.set_dvk_id("DVA837072222");
		dvk.set_title("Latias TG");
		dvk.set_artist("Pokefan-Tf");
		tags[1] = "DVK:Single";
		tags[2] = "Favorite:Whoever";
		dvk.set_web_tags(tags);
		url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Latias-TF-TG-837072222";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(page_dir, "latias.dvk"));
		dvk.set_media_file("latias.jpg");
		dvk.write_dvk();
		//CREATE DVK 3
		dvk = new Dvk();
		dvk.set_dvk_id("DVA345943229");
		dvk.set_title("Fire Within p1");
		dvk.set_artist("AkuOreo");
		dvk.set_web_tags(tags);
		url = "https://www.deviantart.com/akuoreo/art/Fire-Within-p1-345943229";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(page_dir, "fire.dvk"));
		dvk.set_media_file("fire.jpg");
		dvk.write_dvk();
		//READ DVKS
		File[] dirs = {page_dir};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(page_dir);
		try(DvkHandler handler = new DvkHandler(prefs, dirs, null)) {
			File sub = new File(page_dir, "sub");
			if(!sub.isDirectory()) {
				sub.mkdir();
			}
			//TEST SMALL SAMPLE
			ArrayList<String> links = this.dev.get_pages(null, "drakovek", sub, 'm', handler, true);
			assertEquals(1, links.size());
			assertEquals("https://www.deviantart.com/drakovek/art/Drakovek-839354922", links.get(0));
			//GET SAMPLE WITH NO ENTRIES
			links = this.dev.get_pages(null, "drakovek", sub, 's', handler, false);
			assertEquals(0, links.size());
			//TEST GALLERY
			links = this.dev.get_pages(null, "Pokefan-Tf", sub, 'm', handler, false);
			url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Latias-TF-TG-837072222";
			assertFalse(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/A-Draconic-Amulet-TF-RP-819638523";
			assertFalse(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Snake-TF-821107361";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/Snow-Fox-TF-824128177";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Gardevoir-TF-TG-824721814";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/A-Primarina-Gal-Primarina-TF-824780124";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/Protogen-POV-TF-839224833";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/Mallow-TF-TG-735924561";
			assertFalse(links.contains(url));
			url = "https://www.deviantart.com/pokefan-tf/art/Aliens-TF-RP-678082839";
			assertFalse(links.contains(url));
			//TEST SCRAPS
			links = this.dev.get_pages(null, "AkuOreo", sub, 's', handler, false);
			url = "https://www.deviantart.com/akuoreo/art/Fire-Within-p1-345943229";
			assertFalse(links.contains(url));
			assertTrue(links.contains("https://www.deviantart.com/akuoreo/art/Fire-Within-p2-345944945"));
			assertTrue(links.contains("https://www.deviantart.com/akuoreo/art/Fire-Within-p3-345946536"));
			assertTrue(links.contains("https://www.deviantart.com/akuoreo/art/Fire-Within-p4-345947836"));
			assertTrue(links.contains("https://www.deviantart.com/akuoreo/art/Tip-jar-2-346117957"));
			assertTrue(links.contains("https://www.deviantart.com/akuoreo/art/Ruby-Sketch-747801413"));
			//TEST INVALID
			links = this.dev.get_pages(null, "BLEHjaksjdlwehr92oia", sub, 'm', handler, false);
			assertEquals(0, links.size());
			links = this.dev.get_pages(null, "BLEHjaksjdlwehr92oia", sub, 's', handler, false);
			assertEquals(0, links.size());
			links = this.dev.get_pages(null, "BLEHjaksjdlwehr92oia", sub, 'f', handler, false);
			assertEquals(0, links.size());
			//TEST DVKS MOVED
			ArrayList<Dvk> dvks = handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(3, handler.get_size());
			assertEquals(3, dvks.size());
			assertEquals("Draconic Amulet", dvks.get(0).get_title());
			assertEquals("Fire Within p1", dvks.get(1).get_title());
			assertEquals("Latias TG", dvks.get(2).get_title());
			assertEquals(page_dir, dvks.get(0).get_dvk_file().getParentFile());
			assertEquals(sub, dvks.get(1).get_dvk_file().getParentFile());
			assertEquals(sub, dvks.get(2).get_dvk_file().getParentFile());
			assertTrue(dvks.get(0).get_dvk_file().exists());
			assertTrue(dvks.get(1).get_dvk_file().exists());
			assertTrue(dvks.get(2).get_dvk_file().exists());
			tags = dvks.get(0).get_web_tags();
			assertEquals(4, tags.length);
			assertEquals("Gallery:Main", tags[0]);
			assertEquals("Tags", tags[1]);
			assertEquals("stuff", tags[2]);
			assertEquals("thing", tags[3]);
			tags = dvks.get(1).get_web_tags();
			assertEquals(4, tags.length);
			assertEquals("Gallery:Scraps", tags[0]);
			assertEquals("Tags", tags[1]);
			assertEquals("DVK:Single", tags[2]);
			assertEquals("Favorite:Whoever", tags[3]);
			tags = dvks.get(2).get_web_tags();
			assertEquals(4, tags.length);
			assertEquals("Gallery:Main", tags[0]);
			assertEquals("Tags", tags[1]);
			assertEquals("DVK:Single", tags[2]);
			assertEquals("Favorite:Whoever", tags[3]);
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_pages method for getting favorites pages.
	 */
	public void test_get_favorites_pages() {
		//CREATE TEST DIRECTORY
		File fav_dir = null;
		try {
			fav_dir = this.temp_dir.newFolder("favorite_dvks");
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//DVK 1 - STOPS SCANNING DUE TO FAVORITES TAG
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("DVA702065308");
		dvk.set_title("Greatsword");
		dvk.set_artist("Tomek1000");
		String[] tags = {"Favorite:POKEfan-TF", "DVK:Single", "thing"};
		dvk.set_web_tags(tags);
		String url = "https://www.deviantart.com/tomek1000/art/Power-of-Greatsword-commission-702065308";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(fav_dir, "sword.dvk"));
		dvk.set_media_file("sword.png");
		dvk.write_dvk();
		//DVK 2 - KEEPS SCANNING DUE TO LACK OF FAVORITE TAG
		dvk = new Dvk();
		dvk.set_dvk_id("DVA807772446");
		dvk.set_title("Latias Synth");
		dvk.set_artist("Ecliptic-Flare");
		url = "https://www.deviantart.com/ecliptic-flare/art/Latias-Synth-807772446";
		dvk.set_page_url(url);
		tags = new String[2];
		tags[0] = "Favorite:Whoever";
		tags[1] = "blah";
		dvk.set_web_tags(tags);
		dvk.set_dvk_file(new File(fav_dir, "latias.dvk"));
		dvk.set_media_file("latias.png");
		dvk.write_dvk();
		//READ DVKS
		File[] dirs = {fav_dir};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(fav_dir);
		try(DvkHandler handler = new DvkHandler(prefs, dirs, null)) {
			File sub = new File(fav_dir, "sub");
			if(!sub.isDirectory()) {
				sub.mkdir();
			}
			//GET FAVORITES GALLERY
			ArrayList<String> links = this.dev.get_pages(
					null, "Pokefan-Tf", sub, 'f', handler, false);
			assertTrue(links.size() > 39);
			url = "https://www.deviantart.com/tomek1000/art/Power-of-Greatsword-commission-702065308";
			assertFalse(links.contains(url));
			url = "https://www.deviantart.com/ecliptic-flare/art/Latias-Synth-807772446";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/beingobscene/art/Patron-TF-Danger-Cleavage-797049917";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/avatf/art/Firefox-pg-1-797368883";
			assertTrue(links.contains(url));
			url = "https://www.deviantart.com/avatf/art/Firefox-pg-2-797373165";
			assertTrue(links.contains(url));
			//CHECK FAVORITES NOT ADDED ADDED
			ArrayList<Dvk> dvks = handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(2, handler.get_size());
			assertEquals("Greatsword", dvks.get(0).get_title());
			assertEquals("Latias Synth", dvks.get(1).get_title());
			assertEquals(fav_dir, dvks.get(0).get_dvk_file().getParentFile());
			assertEquals(fav_dir, dvks.get(1).get_dvk_file().getParentFile());
			tags = dvks.get(0).get_web_tags();
			assertEquals(3, tags.length);
			assertEquals("Favorite:POKEfan-TF", tags[0]);
			assertEquals("DVK:Single", tags[1]);
			assertEquals("thing", tags[2]);
			tags = dvks.get(1).get_web_tags();
			assertEquals(2, tags.length);
			assertEquals("Favorite:Whoever", tags[0]);
			assertEquals("blah", tags[1]);
		}
		catch(DvkException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Tests the get_module_pages method.
	 */
	public void test_get_module_pages() {
		//CREATE TEST DIRECTORY
		File mod_dir = null;
		try {
			mod_dir = this.temp_dir.newFolder("module_dvks");
		}
		catch(IOException e) {
			assertTrue(false);
		}
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_dvk_id("DVA7924751-P");
		dvk.set_title("Good TF");
		dvk.set_artist("FujoshiiNeko");
		String[] tags = {"Test", "Thing", "Whatever"};
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/fujoshiineko/poll/How-do-you-find-comments-like-FIRST-to-be-7924751");
		dvk.set_dvk_file(new File(mod_dir, "first.dvk"));
		dvk.set_media_file("first.jpg");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk = new Dvk();
		dvk.set_dvk_id("DVA786195189-J");
		dvk.set_title("RPs");
		dvk.set_artist("Pokefan-Tf");
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/journal/RPs-again-786195189");
		dvk.set_dvk_file(new File(mod_dir, "rp.dvk"));
		dvk.set_media_file("rp.jpg");
		dvk.write_dvk();
		//CREATE DVK 3
		dvk = new Dvk();
		dvk.set_dvk_id("DVA15164324-S");
		dvk.set_title("Requests");
		dvk.set_artist("Pokefan-Tf");
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/status-update/15164324");
		dvk.set_dvk_file(new File(mod_dir, "request.dvk"));
		dvk.set_media_file("request.jpg");
		dvk.write_dvk();
		//CREATE DVK 4
		dvk = new Dvk();
		dvk.set_dvk_id("DVA19789401-S");
		dvk.set_title("Bird TF");
		dvk.set_artist("Pokefan-Tf");
		tags[1] = "DVK:Single";
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/status-update/19789401");
		dvk.set_dvk_file(new File(mod_dir, "bird.dvk"));
		dvk.set_media_file("bird.jpg");
		dvk.write_dvk();
		//READ DVKS
		File[] dirs = {mod_dir};
		FilePrefs prefs = new FilePrefs();
		prefs.set_index_dir(mod_dir);
		try(DvkHandler handler = new DvkHandler(prefs, dirs, null)) {
			File sub = new File(mod_dir, "sub");
			if(!sub.isDirectory()) {
				sub.mkdir();
			}
			//GET POLLS
			ArrayList<Dvk> dvks;
			dvks = this.dev.get_module_pages(
					null, "FujoshiiNeko", mod_dir, 'p', handler, false);
			assertTrue(dvks.size() == 20);
			int index = -1;
			for(int i = 0; i < dvks.size(); i++) {
				assertNotEquals("https://www.deviantart.com/fujoshiineko/poll/How-do-you-find-comments-like-FIRST-to-be-7924751",
						dvks.get(i).get_page_url());
				if(dvks.get(i).get_page_url().equals("https://www.deviantart.com/fujoshiineko/poll/"
						+ "Pok-233-mon-Black-and-White-2-is-a-good-game-7625670")) {
					index = i;
				}
			}
			assertNotEquals(-1, index);
			assertEquals("https://www.deviantart.com/fujoshiineko/poll/Luxray-is-a-good-Pok-233-mon-7628901", dvks.get(index - 1).get_page_url());
			assertEquals("https://www.deviantart.com/fujoshiineko/poll/Which-of-my-characters-do-you-like-more-7639342", dvks.get(index - 2).get_page_url());
			dvk = dvks.get(index - 1);
			assertEquals("Luxray is a good Pokémon", dvk.get_title());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("FujoshiiNeko", dvk.get_artists()[0]);
			assertEquals("2019/06/13|19:09", dvk.get_time());
			String desc = "479<DVK-POLL-SEP>Yes<DVK-POLL-SEP>24<DVK-POLL-SEP>No<DVK-POLL-SEP>";
			assertEquals(desc, dvk.get_description());
			//GET STATUS UPDATES
			dvks = this.dev.get_module_pages(null, "Pokefan-tf", sub, 's', handler, false);
			assertTrue(dvks.size() > 21);
			index = -1;
			for(int i = 0; i < dvks.size(); i++) {
				assertFalse(dvks.get(i).get_page_url().endsWith("/15164324"));
				assertFalse(dvks.get(i).get_page_url().endsWith("/19789401"));
				if(dvks.get(i).get_page_url().endsWith("/15165275")) {
					index = i;
				}
			}
			assertNotEquals(-1, index);
			assertTrue(dvks.get(index - 1).get_page_url().endsWith("/15696838"));
			assertTrue(dvks.get(index - 2).get_page_url().endsWith("/15896220"));
			dvk = dvks.get(index - 1);
			assertEquals(null, dvk.get_title());
			assertEquals("2018/12/02|09:23", dvk.get_time());
			assertEquals(1, dvk.get_artists().length);
			assertEquals("Pokefan-Tf", dvk.get_artists()[0]);
			assertTrue(dvk.get_web_tags() == null);
			assertEquals("TFW you have almost 100 RPs to get back to, but you don't "
					+ "feel motivated to respond to any of them...", dvk.get_description());
			//CHECK JOURNALS
			dvks = this.dev.get_module_pages(
					null, "Pokefan-Tf", mod_dir, 'j', handler, false);
			assertTrue(dvks.size() > 22);
			index = -1;
			for(int i = 0; i < dvks.size(); i++) {
				assertNotEquals("https://www.deviantart.com/pokefan-tf/journal/RPs-again-786195189",
						dvks.get(i).get_page_url());
				if(dvks.get(i).get_page_url().equals(
						"https://www.deviantart.com/pokefan-tf/journal/RP-Status-and-the-Future-793770508")) {
					index = i;
				}
			}
			assertNotEquals(-1, index);
			assertEquals("https://www.deviantart.com/pokefan-tf/journal/Discord-799504005", dvks.get(index - 1).get_page_url());
			assertEquals("https://www.deviantart.com/pokefan-tf/journal/Apokelypse-TF-RP-"
					+ "Discord-server-827904463", dvks.get(index - 2).get_page_url());
			//CHECK NO ENTRIES
			dvks = this.dev.get_module_pages(null, "drakovek", sub, 'j', handler, false);
			assertEquals(0, dvks.size());
			dvks = this.dev.get_module_pages(null, "drakovek", sub, 'p', handler, false);
			assertEquals(0, dvks.size());
			dvks = this.dev.get_module_pages(null, "drakovek", sub, 's', handler, false);
			assertEquals(0, dvks.size());
			//CHECK INVALID
			dvks = this.dev.get_module_pages(null, "bleh9q890ri09rqeyrq80", sub, 'j', handler, false);
			assertEquals(0, dvks.size());
			dvks = this.dev.get_module_pages(null, "bleh9q890ri09rqeyrq80", sub, 'p', handler, false);
			assertEquals(0, dvks.size());
			dvks = this.dev.get_module_pages(null, "bleh9q890ri09rqeyrq80", sub, 's', handler, false);
			assertEquals(0, dvks.size());
			//CHECK DVKS MOVED
			dvks = handler.get_dvks(0, -1, 'a', false, false);
			assertEquals(4, handler.get_size());
			assertEquals(4, dvks.size());
			assertEquals("Bird TF", dvks.get(0).get_title());
			assertEquals("Good TF", dvks.get(1).get_title());
			assertEquals("Requests", dvks.get(2).get_title());
			assertEquals("RPs", dvks.get(3).get_title());
			assertEquals(sub, dvks.get(0).get_dvk_file().getParentFile());
			assertEquals(mod_dir, dvks.get(1).get_dvk_file().getParentFile());
			assertEquals(mod_dir, dvks.get(2).get_dvk_file().getParentFile());
			assertEquals(mod_dir, dvks.get(3).get_dvk_file().getParentFile());
			assertTrue(dvks.get(0).get_dvk_file().exists());
			assertTrue(dvks.get(1).get_dvk_file().exists());
			assertTrue(dvks.get(2).get_dvk_file().exists());
			assertTrue(dvks.get(3).get_dvk_file().exists());
		}
		catch(DvkException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
