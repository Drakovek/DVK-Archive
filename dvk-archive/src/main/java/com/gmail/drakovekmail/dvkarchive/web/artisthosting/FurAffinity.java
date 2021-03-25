package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.file.InOut;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;
import com.gmail.drakovekmail.dvkarchive.web.DConnectSelenium;

/**
 * Class for downloading media from FurAffinity.net
 * 
 * @author Drakovek
 */
public class FurAffinity extends ArtistHosting {
	
	/**
	 * Milliseconds to wait after connection events for rate limiting.
	 */
	private static final int SLEEP = 4000;
	
	/**
	 * DConnectSelenium object for connecting to FurAffinity
	 */
	private DConnectSelenium connect;
	
	/**
	 * DConnect object for downloading media
	 */
	private DConnect downloader;
	
	/**
	 * Used for canceling and showing progress
	 */
	private StartGUI start_gui;
	
	/**
	 * Initializes the FurAffinity object.
	 * 
	 * @param prefs FilePrefs for DVK Archive
	 * @param start_gui Used to display progress and messages if using GUI
	 */
	public FurAffinity(FilePrefs prefs, StartGUI start_gui) {
		this.connect = null;
		this.start_gui = start_gui;
	}
	
	/**
	 * Initializes the main DConnect object.
	 * 
	 * @param headless Whether Selenium driver should be headless
	 */
	private void initialize_connect(boolean headless) {
		try {
			this.connect = new DConnectSelenium(headless, this.start_gui);
			this.downloader = new DConnect(false, true);
		}
		catch(DvkException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Attempts to login to FurAffinity.
	 */
	public void login() {
		//OPEN SELENIUM WINDOW
		if(this.connect == null) {
			initialize_connect(false);
		}
		//MAXIMIZE WINDOW
		WebDriver driver = this.connect.get_driver();
		try {
			driver.manage().window().maximize();
		}
		catch(Exception e) {}
		if(driver != null) {
			//LOAD FUR AFFINITY HOME PAGE
			String xpath = "//span[@class='top-heading']//a[@href='/login']";
			this.connect.load_page("https://www.furaffinity.net", xpath, 2, 10);
			//CLICK LOGIN PAGE BUTTON
			try {
				WebElement log = driver.findElement(By.xpath(xpath));
				try {
					log.click();
				}
				catch(Exception e) {
					//IF LOGIN PAGE BUTTON DOESN'T EXIST, LOADS LOGIN PAGE
					this.connect.load_page("https://www.furaffinity.net/login", null, 1, 10);
				}
				try {
					//WAIT UNTIL LOGGED IN OR TIMEOUT
					xpath = "//a[@id='my-username']";
					WebDriverWait wait = new WebDriverWait(driver, 180);
					wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));
					//HIDE WINDOW
					driver.manage().window().setPosition(new Point(-4000, 0));
					TimeUnit.MILLISECONDS.sleep(SLEEP);
					this.connect.load_page("https://www.furaffinity.net/tos", xpath, 1, 10);
				}
				catch(Exception e) {}
			}
			catch(Exception f) {} //LOADING PAGE FAILED
		}
	}
	
	/**
	 * Returns whether connect object is logged in to FurAffinity.
	 * 
	 * @return Whether connect is logged in
	 */
	public boolean is_logged_in() {
		if(this.connect == null || this.connect.get_page() == null) {
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
	 * @param use_ends Whether to use the end caps for Dvk ID
	 * @return ID for page
	 */
	public static String get_page_id(String url, boolean use_ends) {
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
		StringBuilder id = new StringBuilder();
		if(use_ends) {
			id.append("FAF");
		}
		id.append(url.substring(start, end));
		if(journal && use_ends) {
			id.append("-J");
		}
		return id.toString();
	}
	
	/**
	 * Returns a DVK formatted time from FurAffinity time.
	 * 
	 * @param time FurAffinity formatted publication time
	 * @return DVK formated publication time
	 */
	public static String get_time(String time) {
		try {
			String time_mod = time.toLowerCase();
			//GET MONTH
			int month;
			String[] months = {"jan", "feb", "mar", "apr", "may", "jun",
					"jul","aug", "sep", "oct", "nov", "dec"};
			for(month = 0; month < 12 && !time_mod.contains(months[month]); month++);
			month++;
			//GET DAY
			int end = time_mod.indexOf(',');
			int start = time_mod.lastIndexOf(' ', end) + 1;
			int day = Integer.parseInt(time_mod.substring(start, end));
			//GET YEAR
			start = time_mod.indexOf(',');
			for(start++; time_mod.charAt(start) == ' '; start++);
			end = time_mod.indexOf(' ', start);
			int year = Integer.parseInt(time_mod.substring(start, end));
			//GET HOUR
			end = time_mod.indexOf(':');
			start = time_mod.lastIndexOf(' ', end) + 1;
			int hour = Integer.parseInt(time_mod.substring(start, end));
			if(time_mod.contains("pm")) {
				if(hour != 12) {
					hour += 12;
				}
			}
			else if(time_mod.contains("am")) {
				if(hour == 12) {
					hour = 0;
				}
			}
			else {
				hour = -1;
			}
			//GET MINUTE
			start = end + 1;
			end = time_mod.indexOf(' ', start);
			int minute = Integer.parseInt(time_mod.substring(start, end));
			//GET STRING
			Dvk dvk = new Dvk();
			dvk.set_time_int(year, month, day, hour, minute);
			return dvk.get_time();
		}
		catch(Exception e) {
			return "0000/00/00|00:00";
		}
	}
	
	/**
	 * Returns a list of FurAffinity media page IDs for a given artist.
	 * 
	 * @param artist Fur Affinity artist (in URL form)
	 * @param directory Directory to move DVKs to, if specified
	 * @param type Type of gallery to scan ('m' - Main, 's' - Scraps, 'f' - Favorites)
	 * @param dvk_handler Used to check for already downloaded files
	 * @param check_all Whether to check all gallery pages
	 * @param check_login Whether to check if still logged in
	 * @return List of FurAffinity media page IDs
	 * @throws DvkException Throws DvkException if loading gallery page fails
	 */
	public ArrayList<String> get_gallery_ids(
			String artist,
			File directory,
			char type,
			DvkHandler dvk_handler,
			boolean check_all,
			boolean check_login) throws DvkException {
		//INITIALIZE VARIABLES
		if(this.connect == null) {
			initialize_connect(true);
		}
		String xpath;
		String new_url;
		List<DomAttr> das;
		boolean check_next;
		List<DomElement> des;
		ArrayList<String> cur_ids;
		ArrayList<String> return_ids = new ArrayList<>();
		//GET URL
		StringBuilder c_url = new StringBuilder();
		String url_artist = get_url_artist(artist);
		c_url.append("https://www.furaffinity.net/");
		if(type == 's') {
			c_url.append("scraps/");
		}
		else if(type == 'm') {
			c_url.append("gallery/");
		}
		else if(type == 'f') {
			c_url.append("favorites/");
		}
		c_url.append(url_artist);
		c_url.append("/1/");
		String url = c_url.toString();
		//LOOP THROUGH PAGES
		for(int page = 0; page < 50000; page++) {
			//STOP IF CANCELED
			if(this.start_gui != null && this.start_gui.get_base_gui().is_canceled()) {
				return new ArrayList<>();
			}
			//LOAD PAGE
			xpath = "//div[@id='main-window']//div[@id='header']//div[contains(@class, 'site-banner')]|"
					+ "//div[contains(@class, 'block-banners')]//a[@id='fa_header']";
			this.connect.load_page(url, xpath, 2, 10);
			boolean failed = this.connect.get_page() == null;
			xpath = "//a[contains(@href,'/journals/" + url_artist + "')]";
			this.connect.wait_for_element(xpath, 10);
			if(this.connect.get_page() == null) {
				if(!failed) {
					return new ArrayList<>();
				}
				throw new DvkException();
			}
			if(check_login && !is_logged_in()) {
				throw new DvkException();
			}
			try {
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			} catch (InterruptedException e) {}
			//GET PAGES
			xpath = "//figure//u//a[contains(@href,'/view/')]/@href";
			das = this.connect.get_page().getByXPath(xpath);
			check_next = true;
			//RUN THROUGH MEDIA URLS ON GALLERY PAGE, GETTING IDS
			cur_ids = new ArrayList<>();
			for(int att_num = 0; att_num < das.size(); att_num++) {
				cur_ids.add(get_page_id("furaffinity.net/" + das.get(att_num).getNodeValue(), true));
			}
			ArrayList<Dvk> dvks = get_dvks_from_ids(dvk_handler, cur_ids);
			//REMOVE ALREADY DOWNLOADED IDS
			int size = dvks.size();
			for(int dvk_num = 0; dvk_num < size; dvk_num++) {
				boolean remove = true;
				//RUNS IF ID ALLREADY DOWNLOADED
				Dvk dvk = dvks.get(dvk_num);
				String[] wts = dvk.get_web_tags();
				if (type == 'f') {
					if(!ArrayProcessing.contains(wts, "favorite:" + artist, false)) {
						//ADDS TO LIST IF NOT A FAVORITE OF THE GIVEN ARTIST
						remove = false;
					}
					else {
						//STOPS RUNNING IF ALREADY LISTED AS FAVORITE FOR THE GIVEN ARTIST
						check_next = false;
					}
				}
				else if (!ArrayProcessing.contains(wts, "dvk:single", false)) {
					//STOPS RUNNING IF DOWNLOADED DVK IS NOT A SINGLE DOWNLOAD
					check_next = false;
				}
				//UPDATE DVK LOCATION
				if(type != 'f') {
					dvk = ArtistHosting.move_dvk(dvk, directory);
					dvk_handler.set_dvk(dvk, dvk.get_sql_id());
				}
				//REMOVE ID FROM LIST
				if(remove) {
					cur_ids.remove(cur_ids.indexOf(dvk.get_dvk_id()));
				}
			}
			return_ids.addAll(cur_ids);
			//GET NEXT URL
			new_url = null;
			xpath = "//button[@class='button standard'][@type='submit']/parent::form";
			des = this.connect.get_page().getByXPath(xpath);
			for(int i = 0; i < des.size(); i++) {
				if(des.get(i).asText().equals("Next")) {
					new_url = "https://www.furaffinity.net" + des.get(i).getAttribute("action");
				}
			}
			DomElement de;
			if(new_url == null) {
				xpath = "//a[@class='button-link right'][contains(@href,'/gallery/')]|"
						+ "//a[@class='button-link right'][contains(@href,'/scraps/')]|"
						+ "//a[contains(@class, 'button')][contains(@class, 'right')][contains(@href, '/favorites/')]";
				de = this.connect.get_page().getFirstByXPath(xpath);
				if(de != null) {
					new_url = "https://www.furaffinity.net" + de.getAttribute("href");
				}
			}
			if(new_url == null || (!check_all && !check_next)) {
				return ArrayProcessing.clean_list(return_ids);
			}
			url = new_url;
		}
		throw new DvkException();
	}
	
	/**
	 * Returns a list of FurAffinity journal page IDs
	 * for a given artist.
	 *
	 * @param artist Fur Affinity artist (in URL form)
	 * @param directory Directory to move DVKs to, if specified
	 * @param dvk_handler Used to check for already downloaded files
	 * @param check_all Whether to check all gallery pages
	 * @param check_login Whether to check if still logged in
	 * @return List of FurAffinity media page IDs
	 * @throws DvkException Throws DvkException if loading gallery page fails
	 */
	public ArrayList<String> get_journal_ids(
			String artist,
			File directory,
			DvkHandler dvk_handler,
			boolean check_all,
			boolean check_login) throws DvkException {
		//INITIALIZE VARIABLES
		String url;
		String xpath;
		boolean check_next;
		List<DomElement> ds;
		ArrayList<String> cur_ids;
		ArrayList<String> return_ids = new ArrayList<>();
		String url_artist = get_url_artist(artist);
		if(this.connect == null) {
			initialize_connect(true);
		}
		//LOOP THROUGH PAGES
		for(int page = 1; page < 50000; page++) {
			//STOP IF CANCELLED
			if(this.start_gui != null && this.start_gui.get_base_gui().is_canceled()) {
				return new ArrayList<>();
			}
			//GET URL
			url = "https://www.furaffinity.net/journals/" + url_artist + 
					"/" + Integer.toString(page) + "/";
			xpath = "//div[@id='main-window']//div[@id='header']//div[contains(@class, 'site-banner')]|"
					+ "//div[contains(@class, 'block-banners')]//a[@id='fa_header']";
			this.connect.load_page(url.toString(), xpath, 2, 10);
			boolean failed = this.connect.get_page() == null;
			try {
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			} catch (InterruptedException e) {}
			xpath = "//a[contains(@href,'/gallery/" + url_artist + "')]";
			this.connect.wait_for_element(xpath, 10);
			if(this.connect.get_page() == null) {
				if(!failed) {
					return new ArrayList<>();
				}
				throw new DvkException();
			}
			if(check_login && !is_logged_in()) {
				throw new DvkException();
			}
			//GET PAGES
			xpath = "//section[contains(@id,'jid')]//a[contains(@href,'/journal/')]|"
					+ "//table[contains(@id,'jid')]//a[contains(@href,'/journal/')]";
			ds = this.connect.get_page().getByXPath(xpath);
			check_next = true;
			//RUN THROUGH MEDIA URLS ON GALLERY PAGE, GETTING IDS
			cur_ids = new ArrayList<>();
			for(int el_num = 0; el_num < ds.size(); el_num++) {
				String link = ds.get(el_num).asText().toLowerCase();
				if(link.contains("read more") || link.contains("view journal")) {
					cur_ids.add(get_page_id("furaffinity.net/" + ds.get(el_num).getAttribute("href"), true));
				}	
			}
			ArrayList<Dvk> dvks = get_dvks_from_ids(dvk_handler, cur_ids);
			//REMOVE IDS THAT ARE ALREADY DOWNLOADED
			for(int dvk_num = 0; dvk_num < dvks.size(); dvk_num++) {
				//UPDATE DVK LOCATION AND FAVORITE IF ALREADY DOWNLOADED
				Dvk dvk = ArtistHosting.move_dvk(dvks.get(dvk_num), directory);
				dvk_handler.set_dvk(dvk, dvk.get_sql_id());
				if(!ArrayProcessing.contains(dvk.get_web_tags(), "dvk:single", false)) {
					check_next = false;
				}
				cur_ids.remove(cur_ids.indexOf(dvk.get_dvk_id()));
			}
			return_ids.addAll(cur_ids);
			//CHECK WHETHER TO GET NEW PAGES
			DomElement de;
			xpath = "//button[@class='button standard']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de != null && !de.asText().equals("Older")) {
				de = null;
			}
			if(de == null) {
				xpath = "//a[@class='button older']";
				de = this.connect.get_page().getFirstByXPath(xpath);
			}
			if(de == null || (!check_all && !check_next)) {
				return ArrayProcessing.clean_list(return_ids);
			}
		}
		throw new DvkException();
	}
	
	/**
	 * Returns a Dvk for a given FurAffinity media page.
	 * 
	 * @param id ID of FurAffinity media page
	 * @param dvk_handler Used for checking already downloaded favorites pages
	 * @param directory Directory in which to save Dvk.
	 * @param artist Artist to use when adding favorite tag.
	 * Doesn't create favorite tag if null.
	 * @param save Whether to save Dvk and media
	 * @param single Whether this is a single download
	 * @return Dvk of FurAffinity media page
	 * @throws DvkException Throws DvkException if loading page fails
	 */
	public Dvk get_dvk(
			String id,
			DvkHandler dvk_handler,
			File directory,
			String artist,
			boolean single,
			boolean save) throws DvkException {
		//ADD FAVORITES IF DVK ALREADY EXISTS
		Dvk dvk = null;
		if(dvk_handler != null && artist != null) {
			dvk = ArtistHosting.update_favorite(dvk_handler, artist, id);
			if(dvk != null) {
				return dvk;
			}
		}
		//GET NEW DVK
		dvk = new Dvk();
		dvk.set_dvk_id(id);
		String url = "https://www.furaffinity.net/view/" + id.replace("FAF", "") + "/";
		dvk.set_page_url(url);
		//LOAD PAGE
		if(this.connect == null || this.downloader == null) {
			initialize_connect(true);
		}
		String xpath = "//div[contains(@class,'submission-title')]//h2";
		this.connect.load_page(url, xpath, 2, 10);
		try {
			TimeUnit.MILLISECONDS.sleep(SLEEP);
		} catch (InterruptedException e1) {}
		if(this.connect.get_page() == null) {
			throw new DvkException();
		}
		try {
			//GET TITLE
			xpath = "//div[contains(@class,'submission-title')]//h2";
			DomElement de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_title(de.asText());
			//GET ARTIST
			xpath = "//div[contains(@class,'submission')][contains(@class,'container')]//a";
			List<DomElement> ds = this.connect.get_page().getByXPath(xpath);
			for(DomElement el: ds) {
				if(el.asText().length() > 0) {
					dvk.set_artist(el.asText());
					break;
				}
			}
			//GET TIME
			xpath = "//div[@class='submission-id-sub-container']//span[@class='popup_date']|"
					+ "//td[@class='alt1 stats-container']//span[@class='popup_date']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			String time = get_time(de.asText());
			if(time.equals("0000/00/00|00:00")) {
				time = get_time(de.getAttribute("title"));
			}
			dvk.set_time(time);
			if(dvk.get_time().equals("0000/00/00|00:00")) {
				this.connect.set_page(null);
			}
			//GET DIRECT URL
			DomAttr da;
			xpath = "//div[@class='download fullsize']//a[contains(@href,'/art/')]/@href|"
					+ "//a[contains(@class,'mobile-fix')][contains(@href,'/art/')]/@href|"
					+ "//table[@class='maintable']//a[contains(@href,'/art/')]/@href";
			da = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_direct_url("https:" + da.getNodeValue());
			String m_ext = StringProcessing.get_extension(
					dvk.get_direct_url());
			//GET SECONDARY URL
			xpath = "//img[@id='submissionImg']/@src";
			da = this.connect.get_page().getFirstByXPath(xpath);
			if(da != null) {
			dvk.set_secondary_url("https:" + da.getNodeValue());
				String s_ext = StringProcessing.get_extension(dvk.get_secondary_url());
				if(m_ext.equals(s_ext)) {
					dvk.set_secondary_url(null);
				}
			}
			//GET DESCRIPTION
			xpath = "//div[contains(@class,'submission-description')]"
					+ "|//table[@class='maintable']//td[@class='alt1']"
					+ "[@style='padding:8px'][@valign='top'][@align='left']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_description(DConnect.clean_element(de.asXml(), true));
			//GET RATING
			String rating = null;
			ArrayList<String> tags = new ArrayList<>();
			xpath = "//span[contains(@class,'rating-box')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de == null) {
				xpath = "//td[@class='alt1 stats-container']//img[contains(@alt,'rating')]/@alt";
				da = this.connect.get_page().getFirstByXPath(xpath);
				rating = da.getNodeValue().toLowerCase();
			}
			else {
				rating = de.asText().toLowerCase();
			}
			if(rating.contains("general")) {
				tags.add("Rating:General");
			}
			else if(rating.contains("mature")) {
				tags.add("Rating:Mature");
			}
			else if(rating.contains("adult")) {
				tags.add("Rating:Adult");
			}
			else {
				this.connect.set_page(null);
			}
			//GET CATEGORY TAGS
			String tag;
			try {
				//CURRENT DESIGN
				//GET CATEGORY
				xpath = "//span[@class='category-name']";
				de = this.connect.get_page().getFirstByXPath(xpath);
				tag = de.asText();
				tags.add("Category:" + tag);
				//GET THEME
				xpath = "//span[@class='type-name']";
				de = this.connect.get_page().getFirstByXPath(xpath);
				tag = de.asText();
				tags.add("Type:" + tag);
				//GET SPECIES
				xpath = "//div[strong='Species']/strong"
						+ "[@class='highlight']/following-sibling::span";
				de = this.connect.get_page().getFirstByXPath(xpath);
				tag = de.asText();
				tags.add("Species:" + tag);
				//GET GENDER
				xpath = "//div[strong='Gender']/strong[@class='highlight']/following-sibling::span";
				de = this.connect.get_page().getFirstByXPath(xpath);
				tag = de.asText();
				tags.add("Gender:" + tag);
			}
			catch(Exception f) {
				//CLASSIC DESIGN
				xpath = "//td[@class='alt1 stats-container']";
				de = this.connect.get_page().getFirstByXPath(xpath);
				String container = de.asXml();
				//GET CATEGORY
				int start = container.indexOf("Category:");
				start = container.indexOf('>', start) + 1;
				int end = container.indexOf('<', start);
				tag = container.substring(start, end).replace("\n", "").replace("\r", "");
				tag = StringProcessing.remove_whitespace(tag);
				tags.add("Category:" + tag);
				//GET TYPE
				start = container.indexOf("Theme:");
				start = container.indexOf('>', start) + 1;
				end = container.indexOf('<', start);
				tag = container.substring(start, end).replace("\n", "").replace("\r", "");
				tag = StringProcessing.remove_whitespace(tag);
				tags.add("Type:" + tag);
				//GET SPECIES
				start = container.indexOf("Species:");
				if(start != -1) {
					start = container.indexOf('>', start) + 1;
					end = container.indexOf('<', start);
					tag = container.substring(start, end).replace("\n", "").replace("\r", "");
					tag = StringProcessing.remove_whitespace(tag);
					tags.add("Species:" + tag);
				}
				else {
					tags.add("Species:Unspecified / Any");
				}
				//GET GENDER
				start = container.indexOf("Gender:");
				if(start != -1) {
					start = container.indexOf('>', start) + 1;
					end = container.indexOf('<', start);
					tag = container.substring(start, end).replace("\n", "").replace("\r", "");
					tag = StringProcessing.remove_whitespace(tag);
					tags.add("Gender:" + tag);
				}
				else {
					tags.add("Gender:Any");
				}
			}
			//GET GALLERY
			xpath = "//div[@class='submission-content']//a[contains(@href,'/gallery/')]|"
					+ "//div[@class='minigallery-container']//a[contains(@href,'/gallery/')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de != null) {
				tags.add("Gallery:Main");
			}
			else {
				xpath = "//div[@class='submission-content']//a[contains(@href,'/scraps/')]|"
						+ "//div[@class='minigallery-container']//a[contains(@href,'/scraps/')]";
				de = this.connect.get_page().getFirstByXPath(xpath);
				tags.add("Gallery:Scraps");
				if(de == null) {
					this.connect.set_page(null);
				}
			}
			//GET FOLDERS
			xpath = "//a[@class='dotted'][contains(@href,'/folder/')]";
			ds = this.connect.get_page().getByXPath(xpath);
			for(int i = 0; i < ds.size(); i++) {
				tags.add(ds.get(i).asText());
			}
			//GET TAGS
			xpath = "//section[@class='tags-row']//a[contains(@href,'/search/')]|"
					+ "//div[@id='keywords']//a[contains(@href,'/search/')]";
			ds = this.connect.get_page().getByXPath(xpath);
			for(int i = 0; i < ds.size(); i++) {
				tags.add(ds.get(i).asText());
			}
			if(single) {
				tags.add("DVK:Single");
			}
			if(artist != null) {
				tags.add("Favorite:" + artist);
			}
			dvk.set_web_tags(ArrayProcessing.list_to_array(tags));
			//SET MEDIA FILES
			dvk.set_dvk_file(new File(directory, dvk.get_filename(false) + ".dvk"));
			dvk.set_media_file(dvk.get_filename(false) + m_ext);
			if(dvk.get_secondary_url() != null) {
				String s_ext = StringProcessing.get_extension(dvk.get_secondary_url());
				dvk.set_secondary_file(dvk.get_filename(true) + s_ext);
			}
			//SAVE DVK
			if(save) {
				dvk.write_media(this.downloader);
				if(!dvk.get_dvk_file().exists()) {
					dvk.write_dvk();
				}
			}
			//ADD DVK AND RETURN
			if(dvk_handler != null) {
				dvk_handler.add_dvk(dvk);
			}
			return dvk;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		throw new DvkException();
	}
	
	/**
	 * Returns a Dvk for a given FurAffinity journal page.
	 * 
	 * @param id ID of FurAffinity journal page
	 * @param dvk_handler DvkHandler to add to when getting Dvk
	 * @param directory Directory in which to save Dvk.
	 * @param save Whether to save Dvk and media
	 * @param single Whether this is a single download
	 * @return Dvk of FurAffinity media page
	 * @exception DvkException Throws DvkException if loading page fails
	 */
	public Dvk get_journal_dvk(
			String id,
			DvkHandler dvk_handler,
			File directory,
			boolean single,
			boolean save) throws DvkException {
		Dvk dvk = new Dvk();
		dvk.set_dvk_id(id);
		String url = "https://www.furaffinity.net/journal/"
				+ id.replace("FAF", "").replace("-J", "") + "/";
		dvk.set_page_url(url);
		//LOAD PAGE
		if(this.connect == null) {
			initialize_connect(true);
		}
		String xpath = "//h2[@class='journal-title']"
				+ "|//td[@class='journal-title-box']//div[@class='no_overflow']";
		this.connect.load_page(url, xpath, 2, 10);
		try {
			TimeUnit.MILLISECONDS.sleep(SLEEP);
		} catch (InterruptedException e1) {}
		if(this.connect.get_page() == null) {
			throw new DvkException();
		}
		try {
			//GET TITLE
			DomElement de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_title(StringProcessing.remove_whitespace(de.asText()));
			//GET ARTIST
			xpath = "//div[@id='user-profile']//div[@class='userpage-flex-item username']"
					+ "//h2//span|//td[@class='journal-title-box']//a[contains(@href,'/user/')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			String artist = de.asText();
			if(artist.startsWith("~")) {
				artist = artist.substring(1);
			}
			dvk.set_artist(artist);
			//GET TIME
			xpath = "//div[@class='section-header']//span[@class='popup_date']|"
					+ "//td[@class='journal-title-box']//span[@class='popup_date']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			String time = get_time(de.asText());
			if(time.equals("0000/00/00|00:00")) {
				time = get_time(de.getAttribute("title"));
			}
			dvk.set_time(time);
			if(dvk.get_time().equals("0000/00/00|00:00")) {
				this.connect.set_page(null);
			}
			//GET JOURNAL BODY
			xpath = "//div[@class='journal-content']|"
					+ "//div[@class='journal-body']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			String description = "";
			if(de != null) {
				description = DConnect.clean_element(de.asXml(), true);
				dvk.set_description(description);
			}
			//SET TAGS
			String[] tags = new String[3];
			tags[0] = "Rating:General";
			tags[1] = "Gallery:Journals";
			if(single) {
				tags[2] = "DVK:Single";
			}
			dvk.set_web_tags(tags);
			//SET FILE
			dvk.set_dvk_file(new File(directory, dvk.get_filename(false) + ".dvk"));
			dvk.set_media_file(dvk.get_filename(false) + ".html");
			//SAVE FILE
			if(save) {
				dvk.write_dvk();
				if(!dvk.get_dvk_file().exists()) {
					throw new DvkException();
				}
				InOut.write_file(dvk.get_media_file(),
						"<!DOCTYPE html><html>" + description + "</html>");
				if(!dvk.get_media_file().exists()) {
					dvk.get_dvk_file().delete();
					throw new DvkException();
				}
			}
			//ADD DVK TO DVK_HANDLER
			if(dvk_handler != null) {
				dvk_handler.add_dvk(dvk);
			}
			return dvk;
		}
		catch(Exception e) {}
		throw new DvkException();
	}
	
	/**
	 * Closes the connect object when operations are finished.
	 */
	public void close() {
		if(this.connect != null) {
			try {
				this.connect.close();
			}
			catch(DvkException e) {}
		}
		if(this.downloader != null) {
			try {
				this.downloader.close();
			}
			catch(DvkException e) {}
		}
	}
}
