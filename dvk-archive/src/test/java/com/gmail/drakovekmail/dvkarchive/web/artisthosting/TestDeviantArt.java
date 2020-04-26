package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.InOut;

/**
 * Unit tests for the DeviantArt class.
 * 
 * @author Drakovek
 */
public class TestDeviantArt {
	
	//TODO ADD JOURNAL DOWNLOADING
	
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
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "dvatest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		this.dev = new DeviantArt();

	}
	
	/**
	 * Closes DeviantArt class and removes test files.
	 */
	@After
	public void tear_down() {
		this.dev.close();
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
		url = "www.deviantart.com/pokefan-tf/art/Anthro-Incineroar-TF-TG-831012876/";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA831012876", id);
		url = "www.deviantart.com/artist/art/thing-1579354";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA1579354", id);
		url = "https://www.deviantart.com/thing/art/thing-1579354";
		id = DeviantArt.get_page_id(url);
		assertEquals("DVA1579354", id);
		url = "www.nope.com/bleh/art/thing-1579354";
		id = DeviantArt.get_page_id(url);
		assertEquals(null, id);
	}
	
	/**
	 * Tests the get_dvk method.
	 */
	@Test
	public void test_get_dvk() {
		//LOGIN
		this.dev.initialize_connect();
		String[] info = this.dev.get_user_info(
				"DeviantArt", null);
		this.dev.login(info[0], info[1]);
		info = null;
		assertTrue(this.dev.is_logged_in());
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
		String desc = "<div>Magical belt + girl = buff fire cat."
				+ "</div><div><br/></div><div>I know Incineroar's "
				+ "kind of anthro already, but shh<br/></div>";
		assertEquals(desc, dvk.get_description());
		url = "https://www.deviantart.com/download/831012876/"
				+ "ddqrhn0-6ba2165d-854d-42a0-b1bd-f1fa0b57ce01.png?";
		assertTrue(dvk.get_direct_url().startsWith(url));
		assertEquals(null, dvk.get_secondary_url());
		assertEquals("Anthro Incineroar TF-TG_DVA831012876.dvk",
				dvk.get_dvk_file().getName());
		assertEquals("Anthro Incineroar TF-TG_DVA831012876.png",
				dvk.get_media_file().getName());
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
		desc = "My first story of hopefully many to come. "
				+ "A young woman travels to a place she always "
				+ "wanted to visit and ends up staying a bit "
				+ "longer than expected";
		assertEquals(desc, dvk.get_description());
		assertEquals(null, dvk.get_direct_url());
		assertEquals(null, dvk.get_secondary_url());
		assertEquals("Finding One-s True Self_DVA770569130.dvk",
				dvk.get_dvk_file().getName());
		assertEquals("Finding One-s True Self_DVA770569130.txt",
				dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
		assertTrue(dvk.get_media_file().exists());
		String file = InOut.read_file(dvk.get_media_file());
		assertTrue(file.startsWith("<!DOCTYPE html><html><span><p><span>To say that Lisa enjoyed the outdoors"));
		assertTrue(file.endsWith("and together they bounded off into the forest.</span></p><div><span><br/></span></div></span></html>"));
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
		assertEquals("Pokeclipse TF RP_DVA700042244.dvk",
						dvk.get_dvk_file().getName());
		assertEquals("Pokeclipse TF RP_DVA700042244.txt",
				dvk.get_media_file().getName());
		assertTrue(dvk.get_media_file().exists());
		file = InOut.read_file(dvk.get_media_file());
		desc = "<!DOCTYPE html><html>A solar eclipse is something "
				+ "that is amazing to experience... And we happen "
				+ "to be able to see the entire thing!<br/><br/>..."
				+ "however, as you (and / or me) soon find out, "
				+ "this eclipse is different than normal ones."
				+ "<br/><br/><b>Rules</b><br/><br/>1. This is a "
				+ "Pokemon TF rp... meaning only pokemon are "
				+ "allowed.<br/>2. Allowed side effects are TG, "
				+ "AP, AR, MC, and anthro. Anything else, please "
				+ "ask.<br/>3. Tell me the answer to 5 times 3 in "
				+ "your first comment.<br/>4. Put effort into "
				+ "your replies. No \"...\" or anything like "
				+ "that.<br/>5. Have fun!</html>";
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
		desc = "[Insert Story Here]<br/><br/>Did a animatic. "
				+ "Doing the whole thing would be too time "
				+ "consuming so did just the tail instead<br/>"
				+ "<br/>For some reason, I can't upload a gif on "
				+ "DA so looping would be impossible. Unless some "
				+ "kind soul could tell me how to";
		assertEquals(desc, dvk.get_description());
		url = "https://wixmp-ed30a86b8c4ca887773594c2.wixmp.com"
				+ "/v/mp4/1d735619-8d85-4a96-a2ee-82852d4551f0/"
				+ "dd01118-e720f3db-1c53-4545-9e41-889295ea4c6e."
				+ "720p.50714fd3435c49dba3d67d067bcb076c.mp4";
		assertEquals(url, dvk.get_direct_url());
		url = "https://images-wixmp-ed30a86b8c4ca887773594c2."
				+ "wixmp.com/i/1d735619-8d85-4a96-a2ee-"
				+ "82852d4551f0/dd01118-49161b67-ecdc-4406-"
				+ "bb89-62212b2d6cda.jpg/v1/fit/w_300,h_900,"
				+ "q_70,strp/calem_s_noivern_tf_by_"
				+ "fezmangaka_dd01118-300w.jpg";
		assertEquals(url, dvk.get_secondary_url());
		assertEquals("Calem-s Noivern TF_DVA786108284.dvk",
				dvk.get_dvk_file().getName());
		assertEquals("Calem-s Noivern TF_DVA786108284.mp4",
				dvk.get_media_file().getName());
		assertEquals("Calem-s Noivern TF_DVA786108284.jpg",
				dvk.get_secondary_file().getName());
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
		desc = "<b>Update:</b><i>I improved the background. "
				+ "In the previous version is was just a "
				+ "scribble.</i><br/><br/><br/>Here's an "
				+ "animation of a kind I haven't really made "
				+ "before. I think I can confidently state "
				+ "that this is my best transformation "
				+ "animation. I've made transformation "
				+ "animations before, but this one is "
				+ "different. And it's not just because she "
				+ "isn't a wolf.<br/><br/>In this animation, "
				+ "the girl doesn't just go through a single "
				+ "long change. Instead, the changes go "
				+ "happening part by part. You can zoom in "
				+ "and out of the animation, or move the "
				+ "camera around. I gave the world a bit of "
				+ "depth, too.<br/><br/>And finally, notice "
				+ "the buttons don't disappear when the "
				+ "transformation happens (they just become "
				+ "transparent). You can pause the "
				+ "transformation at any time by pressing "
				+ "the button again.<br/><br/>I worked a "
				+ "long time on this animation. I hope "
				+ "you enjoy it.";
		assertEquals(desc, dvk.get_description());
		url = "https://www.deviantart.com/download/298825987/"
				+ "d4xwvlv-f53cf86b-7dfb-4b77-a639-45087a54d165.swf?";
		assertTrue(dvk.get_direct_url().startsWith(url));
		url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/"
				+ "i/d4558240-52d8-48b1-9de7-26fcb6f24db3/d4xwvlv-"
				+ "dcce8bba-00a8-414f-9bd9-3e8be2326916.jpg";
		assertEquals(url, dvk.get_secondary_url());
		assertEquals("Interactive dragoness transformation_DVA298825987.dvk",
				dvk.get_dvk_file().getName());
		assertEquals("Interactive dragoness transformation_DVA298825987.swf",
				dvk.get_media_file().getName());
		assertEquals("Interactive dragoness transformation_DVA298825987.jpg",
				dvk.get_secondary_file().getName());
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
		desc = "<div>Hey, I actually posted something! How about that?"
				+ "</div><div><br/></div><div>Well, I'm pretty "
				+ "stubborn, and I held out on making a fursona "
				+ "for years for various reasons, but I finally caved. "
				+ "Well, sort of at least. I'm not really dedicated "
				+ "to having this be a recurring character or "
				+ "anything. I just felt like drawing myself "
				+ "as a cat, I suppose.<br/></div>";
		assertEquals(desc, dvk.get_description());
		url = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp"
				+ ".com/f/a5ad25e4-56ef-4e95-88b4-4c59f7bca389/"
				+ "ddvqaei-cd05fa1d-51f8-4cf1-99b8-27b29ca3e59f.png?";
		assertTrue(dvk.get_direct_url().startsWith(url));
		assertEquals("Drakovek_DVA839354922.dvk",
				dvk.get_dvk_file().getName());
		assertEquals("Drakovek_DVA839354922.png",
				dvk.get_media_file().getName());
		assertEquals(null, dvk.get_secondary_file());
	}
}
