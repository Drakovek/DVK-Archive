package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;

/**
 * Class for downloading media from FurAffinity.net
 * 
 * @author Drakovek
 */
public class FurAffinity extends ArtistHosting {
	
	/**
	 * DConnect object for connecting to FurAffinity
	 */
	private DConnect connect;
	
	/**
	 * FilePrefs for DVK Archive
	 */
	private FilePrefs prefs;
	
	/**
	 * Initializes the FurAffinity object.
	 * 
	 * @param prefs FilePrefs for DVK Archive
	 */
	public FurAffinity(FilePrefs prefs) {
		this.prefs = prefs;
		this.connect = null;
	}
	
	/**
	 * Returns the file for a downloaded CAPTCHA from FurAffinity.
	 * 
	 * @return File path of downloaded CAPTCHA
	 */
	public File get_captcha() {
		close();
		//LOAD LOGIN PAGE
		String xpath = "//img[@id='captcha_img']";
		this.connect = new DConnect(false, true);
		this.connect.load_page(
				"https://www.furaffinity.net/login/?mode=imagecaptcha",
				xpath,
				2);
		if(this.connect.get_page() != null) {
			//GET CAPTCHA URL
			xpath = "//img[@id='captcha_img']/@src";
			DomAttr da = this.connect.get_page().getFirstByXPath(xpath);
			String url = "https://www.furaffinity.net" + da.getNodeValue();
			//GET FILENAME
			File file;
			int num = 0;
			String ext = StringProcessing.get_extension(url);
			do {
				file = new File(
						this.prefs.get_captcha_dir(),
						Integer.toString(num) + ext);
				num++;
			} while(file.exists());
			//DOWNLOAD
			this.connect.download(url, file);
			return file;
		}
		return new File("");
	}
	
	/**
	 * Attempts to login to FurAffinity.
	 * @param username Username
	 * @param password Password
	 * @param captcha Captcha
	 */
	public void login(String username, String password, String captcha) {
		if(this.connect != null && this.connect.get_page() != null) {
			HtmlInput pass;
			try {
				//INPUT USERNAME
				String xpath = "//input[@id='login']";
				HtmlInput user = this.connect.get_page().getFirstByXPath(xpath);
				user.setValueAttribute(username);
				//INPUT PASSWORD
				xpath = "//input[@type='password']";
				pass = this.connect.get_page().getFirstByXPath(xpath);
				pass.setValueAttribute(password);
				//INPUT CAPTCHA
				xpath = "//input[@id='captcha']";
				HtmlInput cap = this.connect.get_page().getFirstByXPath(xpath);
				cap.setValueAttribute(captcha);
				//SUBMIT INFO
				xpath = "//input[@type='submit']";
				HtmlInput submit = this.connect.get_page().getFirstByXPath(xpath);
				this.connect.set_page((HtmlPage)submit.click());
			} catch (IOException e) {
				this.connect.initialize_client();
			}
			pass = null;
		}
	}
	
	/**
	 * Returns whether connect object is logged in to FurAffinity.
	 * 
	 * @return Whether connect is logged in
	 */
	public boolean is_logged_in() {
		if(this.connect == null
				|| this.connect.get_page() == null) {
			return false;
		}
		DomElement de;
		String xpath = "//a[@id='my-username']";
		de = this.connect.get_page().getFirstByXPath(xpath);
		return de != null;
	}
	
	/**
	 * Closes the connect object when operations are finished.
	 */
	public void close() {
		if(this.connect != null) {
			this.connect.close_client();
		}
	}
}
