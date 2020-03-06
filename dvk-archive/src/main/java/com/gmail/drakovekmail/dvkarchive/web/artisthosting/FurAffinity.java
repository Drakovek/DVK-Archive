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
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.file.InOut;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
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
	private void initialize_connect() {
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
			DomAttr da = this.connect.get_page()
					.getFirstByXPath(xpath);
			String url = "https://www.furaffinity.net"
					+ da.getNodeValue();
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
	public void login(
			String username,
			String password,
			String captcha) {
		if(this.connect != null
				&& this.connect.get_page() != null) {
			HtmlInput pass;
			try {
				//INPUT USERNAME
				String xpath = "//input[@id='login']";
				HtmlInput user = this.connect.get_page()
						.getFirstByXPath(xpath);
				user.setValueAttribute(username);
				//INPUT PASSWORD
				xpath = "//input[@type='password']";
				pass = this.connect.get_page()
						.getFirstByXPath(xpath);
				pass.setValueAttribute(password);
				//INPUT CAPTCHA
				xpath = "//input[@id='captcha']";
				HtmlInput cap = this.connect.get_page()
						.getFirstByXPath(xpath);
				cap.setValueAttribute(captcha);
				//SUBMIT INFO
				xpath = "//input[@type='submit']";
				HtmlInput submit = this.connect.get_page()
						.getFirstByXPath(xpath);
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
			String[] months = {"jan", "feb", "mar", "apr",
					"may", "jun", "jul", "aug",
					"sep", "oct", "nov", "dec"};
			for(month = 0; month < 12
					&& !time_mod.contains(months[month]); month++);
			month++;
			//GET DAY
			int end = time_mod.indexOf(',');
			int start = time_mod.lastIndexOf(' ', end) + 1;
			int day = Integer.parseInt(
					time_mod.substring(start, end));
			//GET YEAR
			start = time_mod.indexOf(',');
			for(start++; time_mod.charAt(start) == ' '; start++);
			end = time_mod.indexOf(' ', start);
			int year = Integer.parseInt(
					time_mod.substring(start, end));
			//GET HOUR
			end = time_mod.indexOf(':');
			start = time_mod.lastIndexOf(' ', end) + 1;
			int hour = Integer.parseInt(
					time_mod.substring(start, end));
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
			int minute = Integer.parseInt(
					time_mod.substring(start, end));
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
	 * Returns a list of FurAffinity media page URLs
	 * for a given artist.
	 * 
	 * @param start_gui Used for canceling and showing progress
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
			StartGUI start_gui,
			String artist,
			boolean scraps,
			DvkHandler dvk_handler,
			boolean check_all,
			boolean check_login,
			int page) {
		//GET URL
		StringBuilder url = new StringBuilder();
		url.append("https://www.furaffinity.net/");
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
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//figure//u//a[contains(@href,'/view/')]";
		if(start_gui != null
				&& start_gui.get_base_gui().is_canceled()) {
			this.connect.set_page(null);
		}
		else {
			this.connect.load_page(url.toString(), xpath, 2);
		}
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
			String link = "https://www.furaffinity.net"
					+ ds.get(i).getNodeValue();
			String id = get_page_id(link, true);
			for(int k = 0; k < size; k++) {
				if(get_page_id(
						dvk_handler.get_dvk(k).get_page_url(),
						true).equals(id)) {
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
		xpath = "//button[@class='button standard']"
				+ "[@type='submit']";
		de = this.connect.get_page().getFirstByXPath(xpath);
		if(de != null && !de.asText().equals("Next")) {
			de = null;
		}
		if(de == null) {
			xpath = "//a[@class='button-link right']"
					+ "[contains(@href,'/gallery/')]";
			de = this.connect.get_page()
					.getFirstByXPath(xpath);
		}
		if(de != null && (check_all || check_next)) {
			ArrayList<String> next = get_pages(
					start_gui, artist, scraps,
					dvk_handler, check_all, check_login,
					page + 1);
			if(next == null) {
				if(page == 1) {
					if(start_gui != null) {
						start_gui.append_console(
								"fur_affinity_failed",
								true);
						start_gui.get_base_gui().set_canceled(true);
					}
					return new ArrayList<>();
				}
				return null;
			}
			pages.addAll(next);
		}
		return pages;
	}
	
	/**
	 * Returns a list of FurAffinity journal page URLs
	 * for a given artist.
	 * 
	 * @param start_gui Used for canceling and showing progress
	 * @param artist Fur Affinity artist (in URL form)
	 * @param dvk_handler Used to check for already downloaded files
	 * @param check_all Whether to check all gallery pages
	 * @param check_login Whether to check if still logged in
	 * @param page Gallery page to scan
	 * @return List of FurAffinity media page URLs
	 */
	public ArrayList<String> get_journal_pages(
			StartGUI start_gui,
			String artist,
			DvkHandler dvk_handler,
			boolean check_all,
			boolean check_login,
			int page) {
		//GET URL
		String url = "https://www.furaffinity.net/journals/"
				+ artist
				+ "/"
				+ Integer.toString(page) + "/";
		//LOAD PAGE
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//section[contains(@id,'jid')]"
				+ "//a[contains(@href,'/journal/')]"
				+ "|//table[contains(@id,'jid')]"
				+ "//a[contains(@href,'/journal/')]";
		if(start_gui != null && start_gui.get_base_gui().is_canceled()) {
			this.connect.set_page(null);
		}
		else {
			this.connect.load_page(url.toString(), xpath, 2);
		}
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
		List<DomElement> ds;
		ds = this.connect.get_page().getByXPath(xpath);
		boolean check_next = true;
		int size = dvk_handler.get_size();
		for(int i = 0; i < ds.size(); i++) {
			String link = ds.get(i).asText().toLowerCase();
			if(link.contains("read more")
					|| link.contains("view journal")) {
				boolean contains = false;
				link = "https://www.furaffinity.net"
						+ ds.get(i).getAttribute("href");
				String id = get_page_id(link, true);
				for(int k = 0; k < size; k++) {
					if(get_page_id(
							dvk_handler.get_dvk(k).get_page_url(),
							true).equals(id)) {
						check_next = false;
						contains = true;
						break;
					}
				}
				if(!contains) {
					pages.add(link);
				}
			}
		}
		//GET NEXT PAGES
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
		if(de != null && (check_all || check_next)) {
			ArrayList<String> next = get_journal_pages(
					start_gui, artist, dvk_handler,
					check_all, check_login, page + 1);
			if(next == null) {
				if(page == 1) {
					if(start_gui != null) {
						start_gui.append_console(
								"fur_affinity_failed",
								true);
						start_gui.get_base_gui().set_canceled(true);
					}
					return new ArrayList<>();
				}
				return null;
			}
			pages.addAll(next);
		}
		return pages;
	}
	
	/**
	 * Returns a Dvk for a given FurAffinity media page.
	 * 
	 * @param page_url URL of FurAffinity media page
	 * @param directory Directory in which to save Dvk.
	 * @param save Whether to save Dvk and media
	 * @return Dvk of FurAffinity media page
	 */
	public Dvk get_dvk(
			String page_url,
			File directory,
			boolean save) {
		Dvk dvk = new Dvk();
		dvk.set_id(get_page_id(page_url, true));
		String url = "https://www.furaffinity.net/view/"
				+ get_page_id(page_url, false) + "/";
		dvk.set_page_url(url);
		//LOAD PAGE
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//div[contains(@class,"
				+ "'submission-title')]//h2";
		this.connect.load_page(url, xpath, 2);
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e1) {}
		if(this.connect.get_page() == null) {
			return new Dvk();
		}
		try {
			//GET TITLE
			xpath = "//div[contains"
					+ "(@class,'submission-title')]//h2";
			DomElement de = this.connect.
					get_page().getFirstByXPath(xpath);
			dvk.set_title(de.asText());
			//GET ARTIST
			xpath = "//div[contains(@class,'submission')]"
					+ "[contains(@class,'container')]//a";
			List<DomElement> ds = this.connect
					.get_page().getByXPath(xpath);
			for(DomElement el: ds) {
				if(el.asText().length() > 0) {
					dvk.set_artist(el.asText());
					break;
				}
			}
			//GET TIME
			xpath = "//div[@class='submission-id-sub-container']"
					+ "//span[@class='popup_date']"
					+ "|//td[@class='alt1 stats-container']"
					+ "//span[@class='popup_date']";
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
			xpath = "//div[@class='download fullsize']"
					+ "//a[contains(@href,'facdn.net')]/@href"
					+ "|//table[@class='maintable']"
					+ "//a[contains(@href,'facdn.net')]/@href";
			da = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_direct_url("https:" + da.getNodeValue());
			String m_ext = StringProcessing.get_extension(
					dvk.get_direct_url());
			//GET SECONDARY URL
			xpath = "//img[@id='submissionImg']/@src";
			da = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_secondary_url("https:" + da.getNodeValue());
			String s_ext = StringProcessing.get_extension(
					dvk.get_secondary_url());
			if(m_ext.equals(s_ext)) {
				dvk.set_secondary_url(null);
			}
			//GET DESCRIPTION
			xpath = "//div[contains(@class,'submission-description')]"
					+ "|//table[@class='maintable']//td[@class='alt1']"
					+ "[@style='padding:8px'][@valign='top']"
					+ "[@align='left']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_description(DConnect.remove_header_footer(de.asXml()));
			//GET RATING
			String rating = null;
			ArrayList<String> tags = new ArrayList<>();
			xpath = "//span[contains(@class,'rating-box')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de == null) {
				xpath = "//td[@class='alt1 stats-container']"
						+ "//img[contains(@alt,'rating')]/@alt";
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
				xpath = "//div[strong='Gender']/strong"
						+ "[@class='highlight']/following-sibling::span";
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
				tag = container.substring(start, end)
						.replace("\n", "").replace("\r", "");
				tag = StringProcessing.remove_whitespace(tag);
				tags.add("Category:" + tag);
				//GET TYPE
				start = container.indexOf("Theme:");
				start = container.indexOf('>', start) + 1;
				end = container.indexOf('<', start);
				tag = container.substring(start, end)
						.replace("\n", "").replace("\r", "");
				tag = StringProcessing.remove_whitespace(tag);
				tags.add("Type:" + tag);
				//GET SPECIES
				start = container.indexOf("Species:");
				if(start != -1) {
					start = container.indexOf('>', start) + 1;
					end = container.indexOf('<', start);
					tag = container.substring(start, end)
							.replace("\n", "").replace("\r", "");
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
					tag = container.substring(start, end)
							.replace("\n", "").replace("\r", "");
					tag = StringProcessing.remove_whitespace(tag);
					tags.add("Gender:" + tag);
				}
				else {
					tags.add("Gender:Any");
				}
			}
			//GET GALLERY
			xpath = "//div[@class='submission-content']"
					+ "//a[contains(@href,'/gallery/')]"
					+ "|//div[@class='minigallery-container']"
					+ "//a[contains(@href,'/gallery/')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de != null) {
				tags.add("Gallery:Main");
			}
			else {
				xpath = "//div[@class='submission-content']"
						+ "//a[contains(@href,'/scraps/')]"
						+ "|//div[@class='minigallery-container']"
						+ "//a[contains(@href,'/scraps/')]";
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
			xpath = "//section[@class='tags-row']"
					+ "//a[contains(@href,'/search/')]"
					+ "|//div[@id='keywords']"
					+ "//a[contains(@href,'/search/')]";
			ds = this.connect.get_page().getByXPath(xpath);
			for(int i = 0; i < ds.size(); i++) {
				tags.add(ds.get(i).asText());
			}
			dvk.set_web_tags(ArrayProcessing.list_to_array(tags));
			//SET MEDIA FILES
			String filename = dvk.get_filename();
			dvk.set_dvk_file(
					new File(directory, filename + ".dvk"));
			dvk.set_media_file(filename + m_ext);
			if(dvk.get_secondary_url() != null) {
				dvk.set_secondary_file(filename + s_ext);
			}
			//SAVE DVK
			if(save) {
				dvk.write_media(this.connect);
				if(!dvk.get_dvk_file().exists()) {
					dvk.write_dvk();
				}
			}
			return dvk;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new Dvk();
		}
	}
	
	/**
	 * Returns a Dvk for a given FurAffinity journal page.
	 * 
	 * @param page_url URL of FurAffinity journal page
	 * @param directory Directory in which to save Dvk.
	 * @param save Whether to save Dvk and media
	 * @return Dvk of FurAffinity media page
	 */
	public Dvk get_journal_dvk(
			String page_url,
			File directory,
			boolean save) {
		Dvk dvk = new Dvk();
		dvk.set_id(get_page_id(page_url, true));
		String url = "https://www.furaffinity.net/journal/"
				+ get_page_id(page_url, false) + "/";
		dvk.set_page_url(url);
		//LOAD PAGE
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//h2[@class='journal-title']"
				+ "|//td[@class='journal-title-box']"
				+ "//div[@class='no_overflow']";
		this.connect.load_page(url, xpath, 2);
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e1) {}
		if(this.connect.get_page() == null) {
			return new Dvk();
		}
		try {
			//GET TITLE
			DomElement de = this.connect.get_page()
					.getFirstByXPath(xpath);
			dvk.set_title(
					StringProcessing.remove_whitespace(de.asText()));
			//GET ARTIST
			xpath = "//div[@id='user-profile']"
					+ "//div[@class='userpage-flex-item username']"
					+ "//h2//span|//td[@class='journal-title-box']"
					+ "//a[contains(@href,'/user/')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			String artist = de.asText();
			if(artist.startsWith("~")) {
				artist = artist.substring(1);
			}
			dvk.set_artist(artist);
			//GET TIME
			xpath = "//div[@class='section-header']"
					+ "//span[@class='popup_date']"
					+ "|//td[@class='journal-title-box']"
					+ "//span[@class='popup_date']";
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
			xpath = "//div[@class='journal-content']"
					+ "|//div[@class='journal-body']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_description(
					DConnect.remove_header_footer(de.asXml()));
			//SET FILE
			String filename = dvk.get_filename();
			dvk.set_dvk_file(
					new File(directory, filename + ".dvk"));
			dvk.set_media_file(filename + ".html");
			//SAVE FILE
			if(save) {
				dvk.write_dvk();
				if(!dvk.get_dvk_file().exists()) {
					return new Dvk();
				}
				InOut.write_file(dvk.get_media_file(),
						dvk.get_description());
				if(!dvk.get_media_file().exists()) {
					dvk.get_dvk_file().delete();
					return new Dvk();
				}
			}
			return dvk;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new Dvk();
		}
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
