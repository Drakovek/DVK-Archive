package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import com.gargoylesoftware.htmlunit.html.DomAttr;
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
	 * Closes the 
	 */
	public void close() {
		if(this.connect != null) {
			this.connect.close_client();
		}
	}
}
