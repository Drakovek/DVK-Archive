package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
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
	 * Initializes the main DConnect object.
	 */
	public void initialize_connect() {
		this.connect = new DConnect(false, true);
		this.connect.set_timeout(4);
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
		initialize_connect();
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
	 * Returns String to use in URL for a given artist.
	 * 
	 * @param artist Given artist
	 * @return Artist formatted to use in URL
	 */
	public static String get_url_artist(String artist) {
		String altered = artist.toLowerCase();
		altered = altered.replace("_", "");
		altered = altered.replace(" ", "");
		return altered;
	}
	
	/**
	 * Gets the page id for a given Fur Affinity page.
	 * Can be either media page or journal page.
	 * 
	 * @param url URL of Fur Affinity page
	 * @return ID for page
	 */
	public static String get_page_id(String url) {
		if(!url.contains("furaffinity.net")) {
			return new String();
		}
		//GET START OF SUBSTRING
		int start = 0;
		boolean journal = false;
		if(url.contains("/view/")) {
			//IF MEDIA PAGE
			start = url.indexOf("/view/") + 1;
			start = url.indexOf('/', start) + 1;
		}
		else if(url.contains("/journal/")) {
			//IF JOURNAL
			journal = true;
			start = url.indexOf("/journal/") + 1;
			start = url.indexOf('/', start) + 1;
		}
		else {
			//IF NOT JOURNAL OR MEDIA PAGE
			return new String();
		}
		//GET END OF SUBSTRING
		int end = url.indexOf('/', start);
		if(end == -1) {
			end = url.length();
		}
		//CREATE ID
		StringBuilder id = new StringBuilder("FAF");
		id.append(url.substring(start, end));
		if(journal) {
			id.append("-J");
		}
		return id.toString();
	}
	
	/**
	 * Returns a list of FurAffinity media page URLs
	 * for a given artist.
	 * 
	 * @param artist Fur Affinity artist (in URL form)
	 * @param scraps Whether to check the scraps folder
	 * 				 If false, checks the gallery folder
	 * @param dvk_handler Used to check for already downloaded files
	 * @param check_all Whether to check all gallery pages
	 * @param check_login Whether to check if still logged in
	 * @param page Gallery page to scan
	 * @return List of FurAffinity media page URLs
	 */
	public ArrayList<String> get_pages(
			String artist,
			boolean scraps,
			DvkHandler dvk_handler,
			boolean check_all,
			boolean check_login,
			int page) {
		//GET URL
		StringBuilder url = new StringBuilder("https://www.furaffinity.net/");
		if(scraps) {
			url.append("scraps/");
		}
		else {
			url.append("gallery/");
		}
		url.append(artist);
		url.append('/');
		url.append(page);
		url.append('/');
		//LOAD PAGE
		String xpath = "//figure//u//a[contains(@href,'/view/')]";
		this.connect.load_page(url.toString(), xpath, 2);
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {}
		if(this.connect.get_page() == null
				|| (check_login && !is_logged_in())) {
			if(page == 1) {
				return new ArrayList<>();
			}
			return null;
		}
		//GET PAGES
		ArrayList<String> pages = new ArrayList<>();
		List<DomAttr> ds;
		xpath = "//figure//u//a[contains(@href,'/view/')]/@href";
		ds = this.connect.get_page().getByXPath(xpath);
		boolean check_next = true;
		int size = dvk_handler.get_size();
		for(int i = 0; i < ds.size(); i++) {
			boolean contains = false;
			String link = "https://www.furaffinity.net" + ds.get(i).getNodeValue();
			String id = get_page_id(link);
			for(int k = 0; k < size; k++) {
				if(get_page_id(dvk_handler.get_dvk(k).get_page_url()).equals(id)) {
					check_next = false;
					contains = true;
					break;
				}
			}
			if(!contains) {
				pages.add(link);
			}
		}
		//GET NEXT PAGES
		DomElement de;
		xpath = "//button[@class='button standard'][@type='submit']";
		de = this.connect.get_page().getFirstByXPath(xpath);
		if(de != null && !de.asText().equals("Next")) {
			de = null;
		}
		if(de == null) {
			xpath = "//a[@class='button-link right'][contains(@href,'/gallery/')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
		}
		if(de != null && (check_all || check_next)) {
			ArrayList<String> next = get_pages(artist, scraps, dvk_handler, check_all, check_login, page + 1);
			if(next == null) {
				if(page == 1) {
					return new ArrayList<>();
				}
				return null;
			}
			pages.addAll(next);
		}
		return pages;
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
