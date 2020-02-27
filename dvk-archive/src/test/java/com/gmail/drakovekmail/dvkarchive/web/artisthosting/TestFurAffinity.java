package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;

/**
 * Unit tests for the FurAffinity object.
 * 
 * @author Drakovek
 */
public class TestFurAffinity {
	
	/**
	 * Directory for holding test files.
	 */
	private File test_dir;
	
	/**
	 * FurAffinity object for testing
	 */
	private FurAffinity fur;
	
	/**
	 * Sets up object and files for testing.
	 */
	@Before
	public void set_up() {
		//CREATE TEST FILES
		String user_dir = System.getProperty("user.dir");
		this.test_dir = new File(user_dir, "faftest");
		if(!this.test_dir.isDirectory()) {
			this.test_dir.mkdir();
		}
		//SET UP FURAFFINITY OBJECT
		FilePrefs prefs = new FilePrefs();
		prefs.set_captcha_dir(this.test_dir);
		this.fur = new FurAffinity(prefs);
	}
	
	/**
	 * Removes test objects and files.
	 */
	@After
	public void tear_down() {
		try {
			FileUtils.deleteDirectory(this.test_dir);
		}
		catch(IOException e) {}
		this.fur.close();
	}
	
	/**
	 * Tests the get_captcha method.
	 */
	@Test
	public void test_get_captcha() {
		File captcha = this.fur.get_captcha();
		assertTrue(captcha.exists());
	}
}
