package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
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
	 * DeviantArt class for testing.
	 */
	private DeviantArt dev;
	
	/**
	 * Directory for holding test files.
	 */
	private File test_dir;
	
	/**
	 * Sets up DeviantArt class and test files.
	 */
	@Before
	public void set_up() {
		create_directory();
		this.dev = new DeviantArt();

	}
	
	/**
	 * Closes DeviantArt class and removes test files.
	 */
	@After
	public void tear_down() {
		this.dev.close();
		remove_directory();
	}
	
	/**
	 * Creates test file directory.
	 */
	private void create_directory() {
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "dvatest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
	}
	
	/**
	 * Removes test file directory.
	 */
	private void remove_directory() {
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
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
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA831012876", id);
		url = "www.deviantart.com/artist/art/thing-1579354";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA1579354", id);
		url = "deviantart.com/thing/art/1579354";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA1579354", id);
		//JOURNAL URLS
		url = "https://www.deviantart.com/artist/journal/title-12357";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA12357-J", id);
		url = "deviantart.com/artist/journal/title-54321";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA54321-J", id);
		url = "deviantart.com/bleh/journal/2561024";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA2561024-J", id);
		//STATUS URLS
		url = "https://www.deviantart.com/artist/status-update/12345";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA12345-S", id);
		url = "www.deviantart.com/artist/status-update/5876";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA5876-S", id);
		url = "deviantart.com/artist/status/1800215";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA1800215-S", id);
		//POLL URLS
		url = "https://www.deviantart.com/akuoreo/poll/title-thing-87659";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA87659-P", id);
		url = "www.deviantart.com/akuoreo/poll/thing-2500";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA2500-P", id);
		url = "deviantart.com/akuoreo/poll/86256";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA86256-P", id);
		//INVALID URLS
		url = "www.nope.com/bleh/art/thing-1579354";
		id = DeviantArt.get_page_id(url);
		assertEquals(null, id);
		url = "https://www.deviantart.com/thing/bleh/thing-1579354";
		id = DeviantArt.get_page_id(url);
		assertEquals(null, id);
		url = "https://www.deviantart.com/thing/art/";
		id = DeviantArt.get_page_id(url);
		assertEquals(null, id);
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
		//TEST GETTING MODULE PAGES
		test_get_module_pages();
		remove_directory();
		//TEST GETTING FAVORITES
		create_directory();
		test_get_favorites_pages();
		remove_directory();
		//TEST GETTING GALLERY PAGES
		create_directory();
		test_get_pages();
		remove_directory();
		//TEST GETTING MEDIA DVK
		create_directory();
		test_get_dvk();
		//TEST GETTING JOURNAL DVK
		test_get_journal_dvk();
	}
	
	/**
	 * Tests the get_dvk method.
	 */
	public void test_get_dvk() {
		//FIRST DVK - IMAGE
		String url = "deviantart.com/pokefan-tf/art/Anthro-Incineroar-TF-TG-831012876";
		Dvk dvk = this.dev.get_dvk(url, "Gallery:Main", this.test_dir, "Somebody", true, false);
		assertEquals("DVA831012876", dvk.get_id());
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
		String desc = "<div>Magical belt + girl = buff fire cat.</div><div><br/></div><div>"
				+ "I know Incineroar's kind of anthro already, but shh<br/></div>";
		assertEquals(desc, dvk.get_description());
		url = "https://www.deviantart.com/download/831012876/ddqrhn0-6ba2165d-854d-42a0-b1bd-f1fa0b57ce01.png?";
		assertTrue(dvk.get_direct_url().startsWith(url));
		assertEquals(null, dvk.get_secondary_url());
		assertEquals("Anthro Incineroar TF-TG_DVA831012876.dvk", dvk.get_dvk_file().getName());
		assertEquals("Anthro Incineroar TF-TG_DVA831012876.png", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
		//SECOND DVK - LITERATURE
		url = "https://www.deviantart.com/legomax98/art/Finding-One-s-True-Self-770569130";
		dvk = this.dev.get_dvk(url, "Gallery:Main", this.test_dir, null, false, true);
		assertEquals("DVA770569130", dvk.get_id());
		url = "https://www.deviantart.com/legomax98/art/Finding-One-s-True-Self-770569130";
		assertEquals(url, dvk.get_page_url());
		assertEquals("Finding One's True Self", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Legomax98", dvk.get_artists()[0]);
		assertEquals("2018/10/30|20:33", dvk.get_time());
		assertEquals(13, dvk.get_web_tags().length);
		assertEquals("Rating:General", dvk.get_web_tags()[0]);
		assertEquals("Gallery:Main", dvk.get_web_tags()[1]);
		assertEquals("Literature", dvk.get_web_tags()[2]);
		assertEquals("Prose", dvk.get_web_tags()[3]);
		assertEquals("Fiction", dvk.get_web_tags()[4]);
		assertEquals("Fantasy", dvk.get_web_tags()[5]);
		assertEquals("Short Stories", dvk.get_web_tags()[6]);
		assertEquals("female", dvk.get_web_tags()[7]);
		assertEquals("transformation", dvk.get_web_tags()[8]);
		assertEquals("animaltransformation", dvk.get_web_tags()[9]);
		assertEquals("wolftransformation", dvk.get_web_tags()[10]);
		assertEquals("animaltfstory", dvk.get_web_tags()[11]);
		assertEquals("feraltransformation", dvk.get_web_tags()[12]);
		desc = "My first story of hopefully many to come. A young woman travels to a place she always "
				+ "wanted to visit and ends up staying a bit longer than expected";
		assertEquals(desc, dvk.get_description());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		assertEquals("Finding One-s True Self_DVA770569130.dvk", dvk.get_dvk_file().getName());
		assertEquals("Finding One-s True Self_DVA770569130.txt", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
		assertTrue(dvk.get_media_file().exists());
		String file = InOut.read_file(dvk.get_media_file());
		assertTrue(file.startsWith("<!DOCTYPE html><html><span><p><span>To say that Lisa enjoyed the outdoors"));
		assertTrue(file.endsWith("and together they bounded off into the forest."
				+ "</span></p><div><span><br/></span></div></span></html>"));
		assertTrue(file.contains("A voice in the back of her mind told her this wasn’t normal wolf behavior"));
		//THIRD DVK - LITERATURE
		url = "http://www.deviantart.com/pokefan-tf/art/Pokeclipse-TF-RP-700042244";
		dvk = this.dev.get_dvk(url, "Gallery:Scraps", this.test_dir, null, true, true);
		assertEquals("DVA700042244", dvk.get_id());
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
		assertEquals("Pokeclipse TF RP_DVA700042244.txt", dvk.get_media_file().getName());
		assertTrue(dvk.get_media_file().exists());
		file = InOut.read_file(dvk.get_media_file());
		desc = "<!DOCTYPE html><html>A solar eclipse is something that is amazing to experience... And we "
				+ "happen to be able to see the entire thing!<br/><br/>...however, as you (and / or me) soon "
				+ "find out, this eclipse is different than normal ones.<br/><br/><b>Rules</b><br/><br/>1. "
				+ "This is a Pokemon TF rp... meaning only pokemon are allowed.<br/>2. Allowed side effects "
				+ "are TG, AP, AR, MC, and anthro. Anything else, please ask.<br/>3. Tell me the answer to 5 "
				+ "times 3 in your first comment.<br/>4. Put effort into your replies. No \"...\" or anything "
				+ "like that.<br/>5. Have fun!</html>";
		assertEquals(desc, file);
		//FOURTH DVK - VIDEO
		url = "http://www.deviantart.com/fezmangaka/art/Calem-s-Noivern-TF-786108284";
		dvk = this.dev.get_dvk(url, "Gallery:Scraps", this.test_dir, "Person", false, false);
		assertEquals("DVA786108284", dvk.get_id());
		url = "https://www.deviantart.com/fezmangaka/art/Calem-s-Noivern-TF-786108284";
		assertEquals(url, dvk.get_page_url());
		assertEquals("Calem's Noivern TF", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("FezMangaka", dvk.get_artists()[0]);
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
		desc = "[Insert Story Here]<br/><br/>Did a animatic. Doing the whole thing would be too time "
				+ "consuming so did just the tail instead<br/><br/>For some reason, I can't upload a gif "
				+ "on DA so looping would be impossible. Unless some kind soul could tell me how to";
		assertEquals(desc, dvk.get_description());
		url = "https://wixmp-ed30a86b8c4ca887773594c2.wixmp.com/v/mp4/1d735619-8d85-4a96-a2ee-82852d4551f0/"
				+ "dd01118-e720f3db-1c53-4545-9e41-889295ea4c6e.720p.50714fd3435c49dba3d67d067bcb076c.mp4";
		assertEquals(url, dvk.get_direct_url());
		url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/1d735619-8d85-4a96-a2ee-82852d4551f0/"
				+ "dd01118-49161b67-ecdc-4406-bb89-62212b2d6cda.jpg/v1/fit/w_300,h_900,q_70,strp/"
				+ "calem_s_noivern_tf_by_fezmangaka_dd01118-300w.jpg";
		assertEquals(url, dvk.get_secondary_url());
		assertEquals("Calem-s Noivern TF_DVA786108284.dvk", dvk.get_dvk_file().getName());
		assertEquals("Calem-s Noivern TF_DVA786108284.mp4", dvk.get_media_file().getName());
		assertEquals("Calem-s Noivern TF_DVA786108284.jpg", dvk.get_secondary_file().getName());
		//FIFTH DVK - SWF
		url = "deviantart.com/doom-the-wolf/art/Interactive-dragoness-transformation-298825987";
		dvk = this.dev.get_dvk(url, "Gallery:Scraps", this.test_dir, "Somebody", false, false);
		assertEquals("DVA298825987", dvk.get_id());
		url = "https://www.deviantart.com/doom-the-wolf/art/Interactive-dragoness-transformation-298825987";
		assertEquals(url, dvk.get_page_url());
		assertEquals("Interactive dragoness transformation", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Doom-the-wolf", dvk.get_artists()[0]);
		assertEquals("2012/04/27|23:56", dvk.get_time());
		assertEquals(9, dvk.get_web_tags().length);
		assertEquals("Rating:Mature", dvk.get_web_tags()[0]);
		assertEquals("Gallery:Scraps", dvk.get_web_tags()[1]);
		assertEquals("Flash", dvk.get_web_tags()[2]);
		assertEquals("Interactive", dvk.get_web_tags()[3]);
		assertEquals("dragon", dvk.get_web_tags()[4]);
		assertEquals("dragoness", dvk.get_web_tags()[5]);
		assertEquals("transformation", dvk.get_web_tags()[6]);
		assertEquals("dragondragoness", dvk.get_web_tags()[7]);
		assertEquals("Favorite:Somebody", dvk.get_web_tags()[8]);
		desc = "<b>Update:</b><i>I improved the background. In the previous version is was just a scribble."
				+ "</i><br/><br/><br/>Here's an animation of a kind I haven't really made before. I think I "
				+ "can confidently state that this is my best transformation animation. I've made transformation "
				+ "animations before, but this one is different. And it's not just because she isn't a wolf.<br/>"
				+ "<br/>In this animation, the girl doesn't just go through a single long change. Instead, the "
				+ "changes go happening part by part. You can zoom in and out of the animation, or move the "
				+ "camera around. I gave the world a bit of depth, too.<br/><br/>And finally, notice the buttons "
				+ "don't disappear when the transformation happens (they just become transparent). You can pause "
				+ "the transformation at any time by pressing the button again.<br/><br/>I worked a long time on "
				+ "this animation. I hope you enjoy it.";
		assertEquals(desc, dvk.get_description());
		url = "https://www.deviantart.com/download/298825987/d4xwvlv-f53cf86b-7dfb-4b77-a639-45087a54d165.swf?";
		assertTrue(dvk.get_direct_url().startsWith(url));
		url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/d4558240-52d8-48b1-9de7-26fcb6f24db3/d4xwvlv-dcce8bba-00a8-414f-9bd9-3e8be2326916.jpg";
		assertEquals(url, dvk.get_secondary_url());
		assertEquals("Interactive dragoness transformation_DVA298825987.dvk", dvk.get_dvk_file().getName());
		assertEquals("Interactive dragoness transformation_DVA298825987.swf", dvk.get_media_file().getName());
		assertEquals("Interactive dragoness transformation_DVA298825987.jpg", dvk.get_secondary_file().getName());
		//SIXTH DVK - UNDOWNLOADABLE
		url = "https://www.deviantart.com/drakovek/art/Drakovek-839354922";
		dvk = this.dev.get_dvk(url, "Gallery:Main", this.test_dir, null, false, false);
		assertEquals("DVA839354922", dvk.get_id());
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
		desc = "<div>Hey, I actually posted something! How about that?</div><div><br/></div><div>Well, I'm pretty "
				+ "stubborn, and I held out on making a fursona for years for various reasons, but I finally caved. "
				+ "Well, sort of at least. I'm not really dedicated to having this be a recurring character or "
				+ "anything. I just felt like drawing myself as a cat, I suppose.<br/></div>";
		assertEquals(desc, dvk.get_description());
		url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/a5ad25e4-56ef-4e95-88b4-"
				+ "4c59f7bca389/ddvqaei-cd05fa1d-51f8-4cf1-99b8-27b29ca3e59f.png?";
		assertTrue(dvk.get_direct_url().startsWith(url));
		assertEquals("Drakovek_DVA839354922.dvk", dvk.get_dvk_file().getName());
		assertEquals("Drakovek_DVA839354922.png", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
	}

	/**
	 * Tests the get_journal method.
	 */
	public void test_get_journal_dvk() {
		//FIRST DVK
		String url = "deviantart.com/akuoreo/journal/Looking-to-hire-3D-modeler-817361421";
		Dvk dvk = this.dev.get_journal_dvk(url, this.test_dir, true, false);
		assertEquals("DVA817361421-J", dvk.get_id());
		assertEquals("Looking to hire 3D modeler", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("AkuOreo", dvk.get_artists()[0]);
		assertEquals("2019/10/19|11:53", dvk.get_time());
		assertEquals(5, dvk.get_web_tags().length);
		assertEquals("Rating:General", dvk.get_web_tags()[0]);
		assertEquals("Gallery:Journals", dvk.get_web_tags()[1]);
		assertEquals("Journals", dvk.get_web_tags()[2]);
		assertEquals("Personal", dvk.get_web_tags()[3]);
		assertEquals("DVK:Single", dvk.get_web_tags()[4]);
		String desc = "<div>We have wanted to use the money we have gotten to commission more MSF High related stuff";
		assertTrue(dvk.get_description().startsWith(desc));
		desc = "It will be posted for free on my youtube channel once done.</q></span></a></span><!-- ^TTT --><!-- TTT$"
				+ " --></span></div><div><br/></div><br/><div><br/></div><div><br/></div><div><br/></div><div><br/></div>"
				+ "<div><br/></div><div><br/></div><div><br/></div>";
		assertTrue(dvk.get_description().endsWith(desc));
		url = "https://www.deviantart.com/akuoreo/journal/Looking-to-hire-3D-modeler-817361421";
		assertEquals(url, dvk.get_page_url());
		assertEquals(null, dvk.get_direct_url());
		url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/4b1ad4c6-d4d5-4075-aef2-66b5e4fbe46b/"
				+ "ddimle3-f2eeb66b-6459-4082-9a9a-0b767df12356.jpg/v1/fill/w_1280,h_720,q_75,strp/vice_principal_"
				+ "kasumi_wallpaper_by_akuoreo_ddimle3-fullview.jpg?";
		assertTrue(dvk.get_secondary_url().startsWith(url));
		assertEquals("Looking to hire 3D modeler_DVA817361421-J.dvk", dvk.get_dvk_file().getName());
		assertEquals("Looking to hire 3D modeler_DVA817361421-J.txt", dvk.get_media_file().getName());
		assertEquals("Looking to hire 3D modeler_DVA817361421-J.jpg", dvk.get_secondary_file().getName());
		//SECOND DVK
		url = "https://www.deviantart.com/akuoreo/journal/Slime-Girl-TF-TG-Deal-Closed-762225768";
		dvk = this.dev.get_journal_dvk(url, this.test_dir, false, true);
		assertEquals("DVA762225768-J", dvk.get_id());
		assertEquals("Slime Girl TF TG Deal (Closed)", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("AkuOreo", dvk.get_artists()[0]);
		assertEquals("2018/09/02|03:09", dvk.get_time());
		assertEquals(4, dvk.get_web_tags().length);
		assertEquals("Rating:General", dvk.get_web_tags()[0]);
		assertEquals("Gallery:Journals", dvk.get_web_tags()[1]);
		assertEquals("Journals", dvk.get_web_tags()[2]);
		assertEquals("Personal", dvk.get_web_tags()[3]);
		desc = "<div>I'm having another deal, but for only two people";
		assertTrue(dvk.get_description().startsWith(desc));
		desc = "Please give info on what you want.</b><br/></div>";
		assertTrue(dvk.get_description().endsWith(desc));
		url = "https://www.deviantart.com/akuoreo/journal/Slime-Girl-TF-TG-Deal-Closed-762225768";
		assertEquals(url, dvk.get_page_url());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		assertEquals("Slime Girl TF TG Deal Closed_DVA762225768-J.dvk", dvk.get_dvk_file().getName());
		assertEquals("Slime Girl TF TG Deal Closed_DVA762225768-J.txt", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		String text = InOut.read_file(dvk.get_media_file());
		desc = "<!DOCTYPE html><html><div>I'm having another deal, but for only two people";
		assertTrue(text.startsWith(desc));
		desc = "Please give info on what you want.</b><br/></div></html>";
		assertTrue(text.endsWith(desc));
		//THIRD DVK
		url = "https://www.deviantart.com/pokefan-tf/journal/On-Break-749882887";
		dvk = this.dev.get_journal_dvk(url, this.test_dir, true, true);
		assertEquals("DVA749882887-J", dvk.get_id());
		assertEquals("On Break.", dvk.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Pokefan-Tf", dvk.get_artists()[0]);
		assertEquals("2018/06/15|08:07", dvk.get_time());
		assertEquals(5, dvk.get_web_tags().length);
		assertEquals("Rating:General", dvk.get_web_tags()[0]);
		assertEquals("Gallery:Journals", dvk.get_web_tags()[1]);
		assertEquals("Journals", dvk.get_web_tags()[2]);
		assertEquals("Personal", dvk.get_web_tags()[3]);
		assertEquals("DVK:Single", dvk.get_web_tags()[4]);
		desc = "I'm gonna be away from my computer 'till the beginning of next month, so don't expect "
				+ "many RP responses from me.";
		assertEquals(desc, dvk.get_description());
		url = "https://www.deviantart.com/pokefan-tf/journal/On-Break-749882887";
		assertEquals(url, dvk.get_page_url());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		assertEquals("On Break_DVA749882887-J.dvk", dvk.get_dvk_file().getName());
		assertEquals("On Break_DVA749882887-J.txt", dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
		assertTrue(dvk.get_dvk_file().exists());
		assertTrue(dvk.get_media_file().exists());
		text = InOut.read_file(dvk.get_media_file());
		desc = "<!DOCTYPE html><html>" + desc + "</html>";
		assertEquals(desc, text);
	}
	
	/**
	 * Tests the get_status_dvk method.
	 */
	@Test
	public void test_get_status_dvk() {
		//CREATE INFO DVK
		Dvk dvk = new Dvk();
		dvk.set_artist("Pokefan-Tf");
		dvk.set_time("2020/04/27|16:35");
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/status/15696838");
		dvk.set_description("This is a test");
		//TEST DVK
		Dvk result = DeviantArt.get_status_dvk(dvk, this.test_dir, true);
		assertEquals("DVA15696838-S", result.get_id());
		assertEquals("27 April 2020 | Pokefan-Tf Update", result.get_title());
		assertEquals(1, result.get_artists().length);
		assertEquals("Pokefan-Tf", result.get_artists()[0]);
		assertEquals(2, result.get_web_tags().length);
		assertEquals("Gallery:Status-Updates", result.get_web_tags()[0]);
		assertEquals("Rating:General", result.get_web_tags()[1]);
		assertEquals("This is a test", result.get_description());
		assertEquals("27 April 2020 - Pokefan-Tf Update_DVA15696838-S.dvk", result.get_dvk_file().getName());
		assertEquals("27 April 2020 - Pokefan-Tf Update_DVA15696838-S.txt", result.get_media_file().getName());
		assertTrue(result.get_dvk_file().exists());
		assertTrue(result.get_media_file().exists());
		String text = InOut.read_file(result.get_media_file());
		assertEquals("<!DOCTYPE html><html>This is a test</html>", text);
		//TEST INVALID DATES
		dvk.set_time("asdlf");
		result = DeviantArt.get_status_dvk(dvk, this.test_dir, true);
		assertEquals(null, result.get_title());
	}
	
	/**
	 * Tests the get_poll_dvk method.
	 */
	@Test
	public void test_get_poll_dvk() {
		//CREATE INFO DVK
		Dvk dvk = new Dvk();
		dvk.set_title("What do you think?");
		dvk.set_artist("Person");
		dvk.set_page_url("https://www.deviantart.com/bleh/poll/something-58635424");
		dvk.set_time("2018/07/12|05:36");
		String[] tags = {"25", "Yes", "47", "No", "5", "Maybe"};
		dvk.set_web_tags(tags);
		//TEST DVK
		Dvk result = DeviantArt.get_poll_dvk(dvk, this.test_dir, true);
		assertEquals("DVA58635424-P", result.get_id());
		assertEquals("What do you think?", result.get_title());
		assertEquals(1, dvk.get_artists().length);
		assertEquals("Person", dvk.get_artists()[0]);
		assertEquals("2018/07/12|05:36", result.get_time());
		assertEquals(2, result.get_web_tags().length);
		assertEquals("Gallery:Polls", result.get_web_tags()[0]);
		assertEquals("Rating:General", result.get_web_tags()[1]);
		String desc = "<body><center><h1>What do you think?</h><p><button><center>Yes</br></br><i>25 Votes</i>"
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
		tags = new String[3];
		tags[0] = "12";
		tags[1] = "yes";
		tags[2] = "53";
		dvk.set_web_tags(tags);
		result = DeviantArt.get_poll_dvk(dvk, this.test_dir, false);
		assertEquals(null, result.get_title());
		//TEST INVALID TAGS - VOTES NOT NUMERICAL
		tags = new String[4];
		tags[0] = "12";
		tags[1] = "yes";
		tags[2] = "what?";
		tags[3] = "no";
		dvk.set_web_tags(tags);
		result = DeviantArt.get_poll_dvk(dvk, this.test_dir, false);
		assertEquals(null, result.get_title());
	}
	
	/**
	 * Tests the get_pages method.
	 */
	public void test_get_pages() {
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_id("DVA819638523");
		dvk.set_title("Draconic Amulet");
		dvk.set_artist("Pokefan-Tf");
		String[] tags = {"Tags", "stuff", "thing"};
		dvk.set_web_tags(tags);
		String url = "https://www.deviantart.com/pokefan-tf/art/A-Draconic-Amulet-TF-RP-819638523";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(this.test_dir, "amulet.dvk"));
		dvk.set_media_file("amulet.png");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk = new Dvk();
		dvk.set_id("DVA837072222");
		dvk.set_title("Latias TG");
		dvk.set_artist("Pokefan-Tf");
		tags[1] = "DVK:Single";
		tags[2] = "Favorite:Whoever";
		dvk.set_web_tags(tags);
		url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Latias-TF-TG-837072222";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(this.test_dir, "latias.dvk"));
		dvk.set_media_file("latias.jpg");
		dvk.write_dvk();
		//CREATE DVK 3
		dvk = new Dvk();
		dvk.set_id("DVA345943229");
		dvk.set_title("Fire Within p1");
		dvk.set_artist("AkuOreo");
		dvk.set_web_tags(tags);
		url = "https://www.deviantart.com/akuoreo/art/Fire-Within-p1-345943229";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(this.test_dir, "fire.dvk"));
		dvk.set_media_file("fire.jpg");
		dvk.write_dvk();
		//READ DVKS
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		File sub = new File(this.test_dir, "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		handler.read_dvks(dirs, prefs, null, false, false, false);
		//TEST SMALL SAMPLE
		ArrayList<String> links = this.dev.get_pages(null, "drakovek", sub, 'm', handler, true, 0);
		assertEquals(1, links.size());
		assertEquals("https://www.deviantart.com/drakovek/art/Drakovek-839354922", links.get(0));
		//GET SAMPLE WITH NO ENTRIES
		links = this.dev.get_pages(null, "drakovek", sub, 's', handler, false, 0);
		assertEquals(0, links.size());
		//TEST GALLERY
		links = this.dev.get_pages(null, "Pokefan-Tf", sub, 'm', handler, false, 0);
		assertTrue(links.size() > 41);
		url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Latias-TF-TG-837072222";
		assertFalse(links.contains(url));
		url = "https://www.deviantart.com/pokefan-tf/art/A-Draconic-Amulet-TF-RP-819638523";
		assertFalse(links.contains(url));
		url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Snake-TF-821107361";
		int index = links.indexOf(url);
		assertNotEquals(-1, index);
		url = "https://www.deviantart.com/pokefan-tf/art/Snow-Fox-TF-824128177";
		assertEquals(url, links.get(index - 1));
		url = "https://www.deviantart.com/pokefan-tf/art/Anthro-Gardevoir-TF-TG-824721814";
		assertEquals(url, links.get(index - 2));
		url = "https://www.deviantart.com/pokefan-tf/art/A-Primarina-Gal-Primarina-TF-824780124";
		assertEquals(url, links.get(index - 3));
		//TEST SCRAPS
		links = this.dev.get_pages(null, "AkuOreo", sub, 's', handler, false, 0);
		assertTrue(links.size() > 249);
		url = "https://www.deviantart.com/akuoreo/art/Fire-Within-p1-345943229";
		assertFalse(links.contains(url));
		url = "https://www.deviantart.com/akuoreo/art/Fire-Within-p2-345944945";
		index = links.indexOf(url);
		assertNotEquals(-1, index);
		assertEquals("https://www.deviantart.com/akuoreo/art/Fire-Within-p3-345946536", links.get(index - 1));
		assertEquals("https://www.deviantart.com/akuoreo/art/Fire-Within-p4-345947836", links.get(index - 2));
		assertEquals("https://www.deviantart.com/akuoreo/art/Tip-jar-2-346117957", links.get(index - 3));
		//TEST DVKS MOVED
		handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, true, false);
		handler.sort_dvks_title(false, false);
		assertEquals(3, handler.get_size());
		assertEquals("Draconic Amulet", handler.get_dvk(0).get_title());
		assertEquals("Fire Within p1", handler.get_dvk(1).get_title());
		assertEquals("Latias TG", handler.get_dvk(2).get_title());
		assertEquals(sub, handler.get_dvk(0).get_dvk_file().getParentFile());
		assertEquals(sub, handler.get_dvk(1).get_dvk_file().getParentFile());
		assertEquals(sub, handler.get_dvk(2).get_dvk_file().getParentFile());
		tags = handler.get_dvk(0).get_web_tags();
		assertEquals(3, tags.length);
		assertEquals("Tags", tags[0]);
		assertEquals("stuff", tags[1]);
		assertEquals("thing", tags[2]);
		tags = handler.get_dvk(1).get_web_tags();
		assertEquals(4, tags.length);
		assertEquals("Gallery:Scraps", tags[0]);
		assertEquals("Tags", tags[1]);
		assertEquals("DVK:Single", tags[2]);
		assertEquals("Favorite:Whoever", tags[3]);
		tags = handler.get_dvk(2).get_web_tags();
		assertEquals(4, tags.length);
		assertEquals("Gallery:Main", tags[0]);
		assertEquals("Tags", tags[1]);
		assertEquals("DVK:Single", tags[2]);
		assertEquals("Favorite:Whoever", tags[3]);
	}
	
	/**
	 * Tests the get_pages method for getting favorites pages.
	 */
	public void test_get_favorites_pages() {
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_id("DVA796674205");
		dvk.set_title("Braixen TF");
		dvk.set_artist("Pencilpaper10");
		String[] tags = {"Tags", "stuff", "thing"};
		dvk.set_web_tags(tags);
		String url = "https://www.deviantart.com/pencilpaper10/art/Braixen-TF-796674205";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(this.test_dir, "braixen.dvk"));
		dvk.set_media_file("braixen.png");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk = new Dvk();
		dvk.set_id("DVA807772446");
		dvk.set_title("Latias Synth");
		dvk.set_artist("Ecliptic-Flare");
		tags[1] = "DVK:Single";
		tags[2] = "Favorite:Whoever";
		dvk.set_web_tags(tags);
		url = "https://www.deviantart.com/ecliptic-flare/art/Latias-Synth-807772446";
		dvk.set_page_url(url);
		dvk.set_dvk_file(new File(this.test_dir, "latias.dvk"));
		dvk.set_media_file("latias.jpg");
		dvk.write_dvk();
		//READ DVKS
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		File sub = new File(this.test_dir, "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		//GET FAVORITES GALLERY
		ArrayList<String> links = this.dev.get_pages(null, "Pokefan-Tf", sub, 'f', handler, false, 0);
		assertTrue(links.size() > 20);
		url = "https://www.deviantart.com/pencilpaper10/art/Braixen-TF-796674205";
		assertFalse(links.contains(url));
		url = "https://www.deviantart.com/ecliptic-flare/art/Latias-Synth-807772446";
		assertFalse(links.contains(url));
		url = "https://www.deviantart.com/beingobscene/art/Patron-TF-Danger-Cleavage-797049917";
		int index = links.indexOf(url);
		assertNotEquals(-1, index);
		url = "https://www.deviantart.com/pencilpaper10/art/Anthro-Umbreon-TF-TG-797210157";
		assertEquals(url, links.get(index - 1));
		url = "https://www.deviantart.com/avatf/art/Firefox-pg-1-797368883";
		assertEquals(url, links.get(index - 2));
		url = "https://www.deviantart.com/avatf/art/Firefox-pg-2-797373165";
		assertEquals(url, links.get(index - 3));
		//CHECK FAVORITES ADDED
		handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		handler.sort_dvks_title(false, false);
		assertEquals(2, handler.get_size());
		assertEquals("Braixen TF", handler.get_dvk(0).get_title());
		assertEquals("Latias Synth", handler.get_dvk(1).get_title());
		assertEquals(this.test_dir, handler.get_dvk(0).get_dvk_file().getParentFile());
		assertEquals(this.test_dir, handler.get_dvk(1).get_dvk_file().getParentFile());
		tags = handler.get_dvk(0).get_web_tags();
		assertEquals(4, tags.length);
		assertEquals("Tags", tags[0]);
		assertEquals("stuff", tags[1]);
		assertEquals("thing", tags[2]);
		assertEquals("Favorite:Pokefan-Tf", tags[3]);
		tags = handler.get_dvk(1).get_web_tags();
		assertEquals(4, tags.length);
		assertEquals("Tags", tags[0]);
		assertEquals("DVK:Single", tags[1]);
		assertEquals("Favorite:Whoever", tags[2]);
		assertEquals("Favorite:Pokefan-Tf", tags[3]);
	}
	
	/**
	 * Tests the get_module_pages method.
	 */
	public void test_get_module_pages() {
		//CREATE DVK 1
		Dvk dvk = new Dvk();
		dvk.set_id("DVA7283020-P");
		dvk.set_title("Good TF");
		dvk.set_artist("FezMangaka");
		String[] tags = {"Test", "Thing", "Whatever"};
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/fezmangaka/poll/What-makes-a-good-TF-7283020");
		dvk.set_dvk_file(new File(this.test_dir, "good.dvk"));
		dvk.set_media_file("good.jpg");
		dvk.write_dvk();
		//CREATE DVK 2
		dvk = new Dvk();
		dvk.set_id("DVA786195189-J");
		dvk.set_title("RPs");
		dvk.set_artist("Pokefan-Tf");
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/journal/RPs-again-786195189");
		dvk.set_dvk_file(new File(this.test_dir, "rp.dvk"));
		dvk.set_media_file("rp.jpg");
		dvk.write_dvk();
		//CREATE DVK 3
		dvk = new Dvk();
		dvk.set_id("DVA15164324-S");
		dvk.set_title("Requests");
		dvk.set_artist("Pokefan-Tf");
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/status-update/15164324");
		dvk.set_dvk_file(new File(this.test_dir, "request.dvk"));
		dvk.set_media_file("request.jpg");
		dvk.write_dvk();
		//CREATE DVK 4
		dvk = new Dvk();
		dvk.set_id("DVA19789401-S");
		dvk.set_title("Bird TF");
		dvk.set_artist("Pokefan-Tf");
		tags[1] = "DVK:Single";
		dvk.set_web_tags(tags);
		dvk.set_page_url("https://www.deviantart.com/pokefan-tf/status-update/19789401");
		dvk.set_dvk_file(new File(this.test_dir, "bird.dvk"));
		dvk.set_media_file("bird.jpg");
		dvk.write_dvk();
		//READ DVKS
		File[] dirs = {this.test_dir};
		FilePrefs prefs = new FilePrefs();
		DvkHandler handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		File sub = new File(this.test_dir, "sub");
		if(!sub.isDirectory()) {
			sub.mkdir();
		}
		//GET POLLS
		ArrayList<Dvk> dvks;
		dvks = this.dev.get_module_pages(null, "FezMangaka", null, this.test_dir, 'p', handler, false, 0);
		assertTrue(dvks.size() > 42);
		int index = -1;
		for(int i = 0; i < dvks.size(); i++) {
			assertNotEquals("https://www.deviantart.com/fezmangaka/poll/What-makes-a-good-TF-7283020",
					dvks.get(i).get_page_url());
			if(dvks.get(i).get_page_url().equals(
					"https://www.deviantart.com/fezmangaka/poll/Is-there-any-aftermath-"
					+ "drawing-you-peps-would-want-to-see-as-of-the-recent-posts-7294815")) {
				index = i;
			}
		}
		assertNotEquals(-1, index);
		assertEquals("https://www.deviantart.com/fezmangaka/poll/Which-is-your-favourite-and-what-do-you-think-of-them-7306515", dvks.get(index - 1).get_page_url());
		assertEquals("https://www.deviantart.com/fezmangaka/poll/Transformation-with-mental-changes-7311213", dvks.get(index - 2).get_page_url());
		dvk = dvks.get(index - 2);
		assertEquals("Transformation with mental changes?", dvk.get_title());
		assertEquals("2018/06/21|10:04", dvk.get_time());
		assertEquals(4, dvk.get_web_tags().length);
		assertEquals("274", dvk.get_web_tags()[0]);
		assertEquals("Like", dvk.get_web_tags()[1]);
		assertEquals("127", dvk.get_web_tags()[2]);
		assertEquals("Dislike", dvk.get_web_tags()[3]);
		//GET STATUS UPDATES
		dvks = this.dev.get_module_pages(null, "Pokefan-tf", null, sub, 's', handler, false, 0);
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
		assertTrue(dvk.get_web_tags() == null);
		assertEquals("TFW you have almost 100 RPs to get back to, but you don't "
				+ "feel motivated to respond to any of them...", dvk.get_description());
		//CHECK JOURNALS
		dvks = this.dev.get_module_pages(null, "Pokefan-Tf", null, this.test_dir, 'j', handler, false, 0);
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
		assertEquals("https://www.deviantart.com/pokefan-tf/journal/Apokelypse-TF-RP-Discord-server-827904463", dvks.get(index - 2).get_page_url());
		//CHECK NO ENTRIES
		dvks = this.dev.get_module_pages(null, "drakovek", null, sub, 'j', handler, false, 0);
		assertEquals(0, dvks.size());
		dvks = this.dev.get_module_pages(null, "drakovek", null, sub, 'p', handler, false, 0);
		assertEquals(0, dvks.size());
		dvks = this.dev.get_module_pages(null, "drakovek", null, sub, 's', handler, false, 0);
		assertEquals(0, dvks.size());
		//CHECK DVKS MOVED
		handler = new DvkHandler();
		handler.read_dvks(dirs, prefs, null, false, false, false);
		handler.sort_dvks_title(false, false);
		assertEquals(4, handler.get_size());
		assertEquals("Bird TF", handler.get_dvk(0).get_title());
		assertEquals("Good TF", handler.get_dvk(1).get_title());
		assertEquals("Requests", handler.get_dvk(2).get_title());
		assertEquals("RPs", handler.get_dvk(3).get_title());
		assertEquals(sub, handler.get_dvk(0).get_dvk_file().getParentFile());
		assertEquals(this.test_dir, handler.get_dvk(1).get_dvk_file().getParentFile());
		assertEquals(sub, handler.get_dvk(2).get_dvk_file().getParentFile());
		assertEquals(this.test_dir, handler.get_dvk(3).get_dvk_file().getParentFile());
	}
}
