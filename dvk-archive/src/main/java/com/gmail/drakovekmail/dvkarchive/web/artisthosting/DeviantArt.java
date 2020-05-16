package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.InOut;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.HtmlProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;

/**
 * Class for downloading media from DeviantArt.com
 * 
 * @author Drakovek
 */
public class DeviantArt extends ArtistHosting {

	/**
	 * Milliseconds to wait after loading page for rate limiting.
	 */
	private static final int SLEEP = 3000;
	
	/**
	 * DConnect object for connecting to DeviantArt
	 */
	private DConnect connect;

	/**
	 * Initializes the DeviantArt class.
	 */
	public DeviantArt() {
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
	 * Closes the connect object when operations are finished.
	 */
	public void close() {
		if(this.connect != null) {
			this.connect.close_client();
		}
	}
	
	/**
	 * Gets the page id for a given DeviantArt page.
	 * Can be either media page or journal page.
	 * 
	 * @param page_url URL of Fur Affinity page
	 * @return ID for page
	 */
	public static String get_page_id(String page_url) {
		if(page_url == null) {
			return new String();
		}
		//CHECKS IF FROM DEVIANTART
		if(!page_url.contains("deviantart.com/")) {
			return new String();
		}
		//GET ENDING OF ID
		int s1 = -1;
		String suffix = null;
		if(page_url.contains("/art/")) {
			s1 = page_url.indexOf("/art/") + 1;
			s1 = page_url.indexOf('/', s1) + 1;
			suffix = "";
		}
		else if(page_url.contains("/journal/")) {
			s1 = page_url.indexOf("/journal/") + 1;
			s1 = page_url.indexOf('/', s1) + 1;
			suffix = "-J";
		}
		else if(page_url.contains("/status-update/")) {
			s1 = page_url.indexOf("/status-update/") + 1;
			s1 = page_url.indexOf('/', s1) + 1;
			suffix = "-S";
		}
		else if(page_url.contains("/status/")) {
			s1 = page_url.indexOf("/status/") + 1;
			s1 = page_url.indexOf('/', s1) + 1;
			suffix = "-S";
		}
		else if(page_url.contains("/poll/")) {
			s1 = page_url.indexOf("/poll/") + 1;
			s1 = page_url.indexOf('/', s1) + 1;
			suffix = "-P";
		}
		else {
			return new String();
		}
		//GET ID SUBSTRING
		int start = page_url.lastIndexOf('-') + 1;
		if(start < s1) {
			start = s1;
		}
		int end = page_url.indexOf('/', start);
		if(end == -1) {
			end = page_url.length();
		}
		if(start == end) {
			return new String();
		}
		return "DVA" + page_url.substring(start, end) + suffix;
	}
	
	/**
	 * Attempts to login to DeviantArt.
	 * 
	 * @param username Username
	 * @param password Password
	 */
	public void login(String username, String password) {
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//input[@id='username']";
		String url = "https://www.deviantart.com/users/login";
		this.connect.load_page(url, xpath, 2);
		try {
			TimeUnit.MILLISECONDS.sleep(SLEEP);
		} catch (InterruptedException e) {}
		HtmlInput pass;
		try {
			//INPUT USERNAME
			HtmlInput user = this.connect.get_page().getFirstByXPath(xpath);
			user.setValueAttribute(username);
			//INPUT PASSWORD
			xpath = "//input[@id='password']";
			pass = this.connect.get_page().getFirstByXPath(xpath);
			pass.setValueAttribute(password);
			//SUBMIT INFO
			xpath = "//button[@id='loginbutton']";
			HtmlButton submit = this.connect.get_page().getFirstByXPath(xpath);
			this.connect.set_page((HtmlPage)submit.click());
		} catch (Exception e) {
			this.connect.initialize_client();
		}
		pass = null;
	}
	
	/**
	 * Returns whether connect object is logged in to DeviantArt.
	 * 
	 * @return Whether connect is logged in
	 */
	public boolean is_logged_in() {
		if(this.connect == null || this.connect.get_page() == null) {
			return false;
		}
		DomElement de;
		String xpath = "//header[@role='banner']//a[contains(@class,'user-link')]";
		de = this.connect.get_page().getFirstByXPath(xpath);
		return de != null;
	}
	
	/**
	 * Returns a Dvk for a given DeviantArt media page.
	 * 
	 * @param page_url URL of DeviantArt media page
	 * @param dvk_handler Used for checking already downloaded favorites pages
	 * @param gallery Gallery tag to add to Dvk
	 * @param directory Directory in which to save Dvk.
	 * @param artist Artist to use when adding favorite tag.
	 * Doesn't create favorite tag if null.
	 * @param single Whether this is a single download
	 * @param save Whether to save Dvk and media
	 * @return Dvk of DeviantArt media page
	 */
	public Dvk get_dvk(
			String page_url,
			DvkHandler dvk_handler,
			String gallery,
			File directory,
			String artist,
			boolean single,
			boolean save) {
		//ADD FAVORITES IF DVK ALREADY EXISTS
		String id = get_page_id(page_url);
		Dvk dvk = null;
		if(dvk_handler != null && artist != null) {
			dvk = ArtistHosting.update_favorite(dvk_handler, artist, id);
			if(dvk != null) {
				if(save) {
					dvk.write_dvk();
				}
				return dvk;
			}
		}
		//GET NEW DVK
		dvk = new Dvk();
		dvk.set_id(id);
		if(dvk.get_id() == null) {
			//CANCEL
			return new Dvk();
		}
		//GET PAGE URL
		int start = page_url.indexOf("deviantart.com/");
		String url = "https://www." + page_url.substring(start);
		dvk.set_page_url(url);
		//LOAD PAGE
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//link[@type='application/json+oembed']";
		this.connect.load_page(url, xpath, 2);
		try {
			TimeUnit.MILLISECONDS.sleep(SLEEP);
		} catch (InterruptedException e) {}
		try {
			//CHECK IF STILL LOGGED IN
			if(!is_logged_in()) {
				throw new Exception();
			}
			//GET DESCRIPTION
			DomElement de;
			xpath = "//a[contains(@class,'user-link')]/parent::div/following-sibling::div[contains(@class,'legacy-journal')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de != null) {
				dvk.set_description(DConnect.clean_element(de.asXml(), true));
			}
			//GET JSON
			DomAttr da;
			xpath = "//link[@type='application/json+oembed']/@href";
			da = this.connect.get_page().getFirstByXPath(xpath);
			String json_url = da.getNodeValue();
			//GET TEXT IF LITERATURE PAGE
			int end;
			String text = null;
			xpath = "//div[contains(@class,'legacy-journal')]//script/parent::div";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de != null) {
				text = DConnect.clean_element(de.asXml(), true);
				start = text.indexOf("<script");
				end = text.indexOf("</script>", start);
				end = text.indexOf('>', end) + 1;
				text = text.substring(0, start) + text.substring(end);
				text = "<!DOCTYPE html><html>" + text + "</html>";
			}
			//GET VIDEO LINK
			List<DomElement> ds;
			String video = null;
			xpath = "//noscript";
			ds = this.connect.get_page().getByXPath(xpath);
			if(ds != null) {
				for(int i = 0; i < ds.size(); i++) {
					String el = ds.get(i).asXml();
					if(el.contains(";video")) {
						start = el.indexOf("src=\"");
						start = el.indexOf("\"", start) + 1;
						end = el.indexOf("\"", start);
						video = el.substring(start, end);
						break;
					}
				}
			}
			//GET DOWNLOAD LINK
			da = null;
			String download = null;
			xpath = "//a[@data-hook='download_button']/@href";
			da = this.connect.get_page().getFirstByXPath(xpath);
			if(da != null) {
				download = da.getNodeValue();
			}
			//GET IMAGE LINK
			String image = null;
			if(download == null) {
				ArrayList<String> imgs = new ArrayList<>();
				xpath = "//div[@data-hook='art_stage']//img";
				while(true) {
					HtmlImage img = this.connect.get_page().getFirstByXPath(xpath);
					if(img == null) {
						break;
					}
					String src = img.getSrcAttribute();
					if(imgs.contains(src)) {
						break;
					}
					imgs.add(src);
					image = src;
					this.connect.set_page((HtmlPage)img.click());
				}
			}
			//GET SWF LINK
			String swf = null;
			if(download == null) {
				xpath = "//iframe[contains(@src,'.swf')][contains(@src,'sandbox.deviantart.com')]/@src";
				da = this.connect.get_page().getFirstByXPath(xpath);
				if(da != null) {
					swf = da.getNodeValue();
					xpath = "//embed[@id='sandboxembed']";
					this.connect.load_page(swf, xpath, 2);
					xpath = "//embed[@id='sandboxembed']/@src";
					da = this.connect.get_page().getFirstByXPath(xpath);
					if(da != null) {
						swf = da.getNodeValue();
					}
					else {
						swf = null;
					}
				}
			}
			JSONObject json = this.connect.load_json(json_url, 2);
			try {
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			} catch (InterruptedException e) {}
			//GET TITLE
			dvk.set_title(json.getString("title"));
			//GET ARTIST
			dvk.set_artist(json.getString("author_name"));
			//GET TIME PUBLISHED
			String time = json.getString("pubdate");
			dvk.set_time(time.substring(0, 16));
			//GET RATING
			ArrayList<String> tags = new ArrayList<>();
			String rating = json.getString("safety");
			if(rating.equals("nonadult")) {
				tags.add("Rating:General");
			}
			else if (rating.equals("adult")) {
				tags.add("Rating:Mature");
			}
			else {
				throw new Exception();
			}
			//GET GALLERY
			tags.add(gallery);
			//GET CATEGORIES
			String category = json.getString("category");
			while(category.contains(" > ")) {
				start = category.indexOf(" > ");
				tags.add(category.substring(0, start));
				category = category.substring(start + 3);
			}
			tags.add(category);
			//GET MAIN TAGS
			try {
				String tag;
				String tag_str = json.getString("tags");
				while(tag_str.contains(", ")) {
					start = tag_str.indexOf(", ");
					tag = tag_str.substring(0, start);
					tag = HtmlProcessing.replace_escapes(tag);
					tags.add(tag);
					tag_str = tag_str.substring(start + 2);
				}
				tags.add(tag_str);
			}
			catch (JSONException f) {}
			//SINGLE AND FAVORITE TAGS
			if(single) {
				tags.add("DVK:Single");
			}
			if(artist != null) {
				tags.add("Favorite:" + artist);
			}
			dvk.set_web_tags(ArrayProcessing.list_to_array(tags));
			//SET DIRECT URL
			String type = json.getString("type");
			if(type.equals("rich")) {
				dvk.set_direct_url(null);
			}
			else if(type.equals("video")) {
				dvk.set_direct_url(video);
			}
			else if(download == null && type.equals("photo")) {
				dvk.set_direct_url(image);
			}
			else if (download == null && swf != null) {
				dvk.set_direct_url(swf);
			}
			else {
				dvk.set_direct_url(download);
			}
			//SET SECONDARY URL
			try {
				String second = json.getString("fullsize_url");
				if(!type.equals("photo")) {
					dvk.set_secondary_url(second);
				}
			}
			catch (JSONException f) {}
			if(dvk.get_secondary_url() == null) {
				try {
					String second = json.getString("thumbnail_url");
					if(!type.equals("photo")) {
						dvk.set_secondary_url(second);
					}
				}
				catch (JSONException f) {}
			}
			//SET FILES
			String ext;
			String filename = dvk.get_filename();
			dvk.set_dvk_file(new File(directory, filename + ".dvk"));
			if(type.equals("rich")) {
				dvk.set_media_file(filename + ".html");
			}
			else {
				ext = StringProcessing.get_extension(dvk.get_direct_url());
				dvk.set_media_file(filename + ext);
			}
			//SET SECONDARY FILE
			if(dvk.get_secondary_url() != null) {
				ext = StringProcessing.get_extension(dvk.get_secondary_url());
				dvk.set_secondary_file(filename + ext);
			}
			//SAVE FILES
			if(save) {
				if(type.equals("rich")) {
					dvk.write_dvk();
					InOut.write_file(dvk.get_media_file(), text);
					if(dvk.get_secondary_url() != null) {
						this.connect.download(dvk.get_secondary_url(), dvk.get_secondary_file());
					}
				}
				else {
					dvk.write_media(this.connect);
				}
				if(!dvk.get_dvk_file().exists()) {
					throw new Exception();
				}
			}
			return dvk;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println(page_url);
		}
		return new Dvk();
	}
	
	/**
	 * Returns a Dvk for a given DeviantArt poll page.
	 * 
	 * @param info Dvk containing poll info
	 * @param directory Directory in which to save Dvk.
	 * @param save Whether to save Dvk and media
	 * @return Dvk of DeviantArt media page
	 */
	public static Dvk get_poll_dvk(Dvk info, File directory, boolean save) {
		if(info.get_description() == null) {
			return new Dvk();
		}
		//PARSE VOTE DATA
		String vote_str = info.get_description();
		ArrayList<String> vote_data = new ArrayList<>();
		int index;
		while(vote_str.length() > 0) {
			index = vote_str.indexOf("<DVK-POLL-SEP>");
			vote_data.add(vote_str.substring(0, index));
			index = vote_str.indexOf('>', index) + 1;
			vote_str = vote_str.substring(index);
		}
		//PARSE OUT VOTING OPTIONS AND VOTE COUNTS
		if((vote_data.size() / 2) * 2 != vote_data.size()) {
			return new Dvk();
		}
		int[] votes = new int[vote_data.size() / 2];
		String[] insides = new String[votes.length];
		int vote = -1;
		int greatest = -1;
		for(int i = 0; i < votes.length; i++) {
			try {
				votes[i] = Integer.parseInt(vote_data.get(i * 2));
				if(votes[i] > vote) {
					vote = votes[i];
					greatest = i;
				}
			}
			catch(NumberFormatException e) {
				return new Dvk();
			}
			insides[i] = vote_data.get((i * 2) + 1);
		}
		//SET COMMON INFO
		Dvk dvk = new Dvk();
		dvk.set_title(info.get_title());
		dvk.set_artists(info.get_artists());
		dvk.set_time(info.get_time());
		String[] gallery = {"Gallery:Polls", "Rating:General"};
		dvk.set_web_tags(gallery);
		dvk.set_page_url(info.get_page_url());
		dvk.set_id(get_page_id(dvk.get_page_url()));
		//SET HTML
		StringBuilder text = new StringBuilder("<body><center><h1>");
		text.append(dvk.get_title());
		text.append("</h><p>");
		for(int i = 0; i < insides.length; i++) {
			if(i > 0) {
				text.append("</br></br>");
			}
			text.append("<button><center>");
			text.append(insides[i]);
			text.append("</br></br><i>");
			String vs = Integer.toString(votes[i]) + " Votes";
			if(i == greatest) {
				vs = "<b>" + vs + "</b>";
			}
			text.append(vs);
			text.append("</i></center></button>");
		}
		text.append("</p></center></body>");
		dvk.set_description(text.toString());
		text.insert(0, "<!DOCTYPE html><html>");
		text.append("</html>");
		//SET FILENAMES
		String filename = dvk.get_filename();
		dvk.set_dvk_file(new File(directory, filename + ".dvk"));
		dvk.set_media_file(filename + ".html");
		if(save) {
			dvk.write_dvk();
			InOut.write_file(dvk.get_media_file(), text.toString());
		}
		return dvk;
	}
	
	/**
	 * Returns a Dvk for a given DeviantArt status update page.
	 * 
	 * @param info Dvk containing status info
	 * @param directory Directory in which to save Dvk.
	 * @param save Whether to save Dvk and media
	 * @return Dvk of DeviantArt media page
	 */
	public static Dvk get_status_dvk(Dvk info, File directory, boolean save) {
		if(info.get_time().equals("0000/00/00|00:00")) {
			return new Dvk();
		}
		Dvk dvk = new Dvk();
		dvk.set_id(get_page_id(info.get_page_url()));
		dvk.set_artists(info.get_artists());
		String[] gallery = {"Gallery:Status-Updates", "Rating:General"};
		dvk.set_web_tags(gallery);
		dvk.set_page_url(info.get_page_url());
		dvk.set_time(info.get_time());
		dvk.set_description(info.get_description());
		String text = "<!DOCTYPE html><html>" + info.get_description() + "</html>";
		//GET TITLE
		String[] months = {"January", "February", "March", "April", "May", "June", 
				"July", "August", "September", "October", "November", "December"};
		int year = Integer.parseInt(info.get_time().substring(0, 4));
		int month = Integer.parseInt(info.get_time().substring(5, 7)) - 1;
		int day = Integer.parseInt(info.get_time().substring(8, 10));
		StringBuilder title = new StringBuilder();
		title.append(day);
		title.append(" ");
		title.append(months[month]);
		title.append(" ");
		title.append(year);
		title.append(" | ");
		title.append(dvk.get_artists()[0]);
		title.append(" Update");
		dvk.set_title(title.toString());
		//SET FILES
		String filename = dvk.get_filename();
		dvk.set_dvk_file(new File(directory, filename + ".dvk"));
		dvk.set_media_file(filename + ".html");
		//SAVE
		if(save) {
			dvk.write_dvk();
			InOut.write_file(dvk.get_media_file(), text);
		}
		return dvk;
	}
	
	/**
	 * Returns a Dvk for a given DeviantArt journal page.
	 * 
	 * @param page_url URL of DeviantArt journal page
	 * @param directory Directory in which to save Dvk.
	 * @param single Whether this is a single download
	 * @param save Whether to save Dvk and media
	 * @return Dvk of DeviantArt media page
	 */
	public Dvk get_journal_dvk(
			String page_url,
			File directory,
			boolean single,
			boolean save) {
		Dvk dvk = new Dvk();
		dvk.set_id(get_page_id(page_url));
		if(dvk.get_id() == null) {
			//CANCEL
			return new Dvk();
		}
		//GET PAGE URL
		int start = page_url.indexOf("deviantart.com/");
		String url = "https://www." + page_url.substring(start);
		dvk.set_page_url(url);
		//LOAD PAGE
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//link[@type='application/json+oembed']";
		this.connect.load_page(url, xpath, 2);
		try {
			TimeUnit.MILLISECONDS.sleep(SLEEP);
		} catch (InterruptedException e) {}
		try {
			//CHECK IF STILL LOGGED IN
			if(!is_logged_in()) {
				throw new Exception();
			}
			//GET DESCRIPTION
			int end;
			String desc = null;
			xpath = "//div[contains(@class,'legacy-journal')]//script/parent::div|"
					+ "//div[@class='da-editor-journal']//div[@dir='ltr']";
			DomElement de = this.connect.get_page().getFirstByXPath(xpath);
			desc = DConnect.clean_element(de.asXml(), true);
			start = desc.indexOf("<script");
			if(start != -1) {
				end = desc.indexOf("</script>", start);
				end = desc.indexOf('>', end) + 1;
				desc = desc.substring(0, start) + desc.substring(end);
			}
			dvk.set_description(desc);
			//GET JSON
			xpath = "//link[@type='application/json+oembed']/@href";
			DomAttr da = this.connect.get_page().getFirstByXPath(xpath);
			JSONObject json = this.connect.load_json(da.getNodeValue(), 2);
			try {
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			} catch (InterruptedException e) {}
			//GET TITLE
			dvk.set_title(json.getString("title"));
			//GET ARTIST
			dvk.set_artist(json.getString("author_name"));
			//GET TIME PUBLISHED
			String time = json.getString("pubdate");
			dvk.set_time(time.substring(0, 16));
			//GET RATING
			ArrayList<String> tags = new ArrayList<>();
			String rating = json.getString("safety");
			if(rating.equals("nonadult")) {
				tags.add("Rating:General");
			}
			else if (rating.equals("adult")) {
				tags.add("Rating:Mature");
			}
			else {
				throw new Exception();
			}
			//GET GALLERY
			tags.add("Gallery:Journals");
			//GET CATEGORIES
			String category = json.getString("category");
			while(category.contains(" > ")) {
				start = category.indexOf(" > ");
				tags.add(category.substring(0, start));
				category = category.substring(start + 3);
			}
			tags.add(category);
			if(single) {
				tags.add("DVK:Single");
			}
			dvk.set_web_tags(ArrayProcessing.list_to_array(tags));
			//SET SECONDARY URL
			try {
				String second = json.getString("fullsize_url");
				dvk.set_secondary_url(second);
			}
			catch (JSONException f) {}
			if(dvk.get_secondary_url() == null) {
				try {
					String second = json.getString("thumbnail_url");
					dvk.set_secondary_url(second);
				}
				catch (JSONException f) {}
			}
			//SET FILES
			String filename = dvk.get_filename();
			dvk.set_dvk_file(new File(directory, filename + ".dvk"));
			dvk.set_media_file(filename + ".html");
			//SET SECONDARY FILE
			if(dvk.get_secondary_url() != null) {
				String ext;
				ext = StringProcessing.get_extension(dvk.get_secondary_url());
				dvk.set_secondary_file(filename + ext);
			}
			//SAVE
			if(save) {
				desc = "<!DOCTYPE html><html>" + desc + "</html>";
				dvk.write_dvk();
				InOut.write_file(dvk.get_media_file(), desc);
				if(dvk.get_secondary_url() != null) {
					this.connect.download(dvk.get_secondary_url(), dvk.get_secondary_file());
				}
				if(!dvk.get_dvk_file().exists()) {
					throw new Exception();
				}
			}
			return dvk;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println(page_url);
		}
		return new Dvk();
	}
	
	/**
	 * Returns a list of DeviantArt media page URLs for a given artist.
	 * 
	 * @param start_gui Used for canceling and showing progress
	 * @param artist DeviantArt artist
	 * @param directory Directory to move DVKs to, if specified
	 * @param type Type of gallery to scan ('m' - Main, 's' - Scraps, 'f' - Favorites)
	 * @param dvk_handler Used to check for already downloaded files
	 * @param check_all Whether to check all gallery pages
	 * @param offset Gallery page offset
	 * @return List of DeviantArt media page URLs
	 */
	public ArrayList<String> get_pages(
			StartGUI start_gui,
			String artist,
			File directory,
			char type,
			DvkHandler dvk_handler,
			boolean check_all,
			int offset) {
		//GET URL
		StringBuilder url = new StringBuilder();
		url.append("https://www.deviantart.com/_napi/da-user-profile/api/");
		if(type == 'm') {
			url.append("gallery/contents?username=");
			url.append(artist);
			url.append("&limit=24&all_folder=true&mode=newest&offset=");
		}
		else if(type == 's') {
			url.append("gallery/contents?username=");
			url.append(artist);
			url.append("&limit=24&scraps_folder=true&mode=newest&offset=");
		}
		else if(type == 'f') {
			url.append("collection/contents?username=");
			url.append(artist);
			url.append("&limit=24&all_folder=true&mode=newest&offset=");
		}
		url.append(offset);
		try {
			//LOAD PAGE
			if(this.connect == null) {
				initialize_connect();
			}
			JSONObject json = null;
			if(start_gui == null || !start_gui.get_base_gui().is_canceled()) {
				json = this.connect.load_json(url.toString(), 2);
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			}
			if(json == null) {
				if(offset == 0) {
					return new ArrayList<>();
				}
				return null;
			}
			//GET PAGES
			JSONArray arr;
			ArrayList<String> pages = new ArrayList<>();
			arr = json.getJSONArray("results");
			boolean check_next = true;
			int size = dvk_handler.get_size();
			for(int i = 0; i < arr.length(); i++) {
				boolean contains = false;
				JSONObject obj = arr.getJSONObject(i).getJSONObject("deviation");
				String link = obj.getString("url");
				String id = get_page_id(link);
				for(int k = 0; k < size; k++) {
					if(get_page_id(dvk_handler.get_dvk(k).get_page_url()).equals(id)) {
						contains = true;
						//ENDS IF DOWNLOADED DVK IS NOT A SINGLE DOWNLOAD
						String[] wts = dvk_handler.get_dvk(k).get_web_tags();
						if (type == 'f') {
							if(!ArrayProcessing.contains(wts, "favorite:" + artist, false)) {
								contains = false;
							}
							else {
								check_next = false;
							}
						}
						else if (!ArrayProcessing.contains(wts, "dvk:single", false)) {
							check_next = false;
						}
						//UPDATE DVK LOCATION AND FAVORITE IF ALREADY DOWNLOADED
						Dvk dvk = dvk_handler.get_dvk(k);
						if(type != 'f') {
							dvk = ArtistHosting.move_dvk(dvk_handler.get_dvk(k), directory);
							dvk_handler.set_dvk(dvk, k);
						}
						//ADD GALLERY TAG
						if(type == 'm' && !ArrayProcessing.contains(dvk.get_web_tags(), "gallery:main", false)) {
							ArrayList<String> tags = ArrayProcessing.array_to_list(dvk.get_web_tags());
							tags.add(0, "Gallery:Main");
							dvk.set_web_tags(ArrayProcessing.list_to_array(tags));
							dvk.write_dvk();
						}
						else if(type == 's' && !ArrayProcessing.contains(dvk.get_web_tags(), "gallery:scraps", false)) {
							ArrayList<String> tags = ArrayProcessing.array_to_list(dvk.get_web_tags());
							tags.add(0, "Gallery:Scraps");
							dvk.set_web_tags(ArrayProcessing.list_to_array(tags));
							dvk.write_dvk();
						}
						break;
					}
				}
				if(!contains) {
					pages.add(link);
				}
			}
			//GET NEXT PAGES
			boolean more = json.getBoolean("hasMore");
			if(more && (check_all || check_next)) {
				ArrayList<String> next = get_pages(start_gui, artist, directory, type, dvk_handler, check_all, offset + 24);
				if(next == null) {
					if(offset == 0) {
						if(start_gui != null) {
							start_gui.append_console("deviantart_failed", true);
							start_gui.get_base_gui().set_canceled(true);
						}
						return new ArrayList<>();
					}
					return null;
				}
				pages.addAll(next);
			}
			return ArrayProcessing.clean_list(pages);
		}
		catch(Exception e) {}
		//RETURN BLANK IF FAILED
		if(offset == 0) {
			return new ArrayList<>();
		}
		return null;
	}
	
	/**
	 * Returns a list of Dvk objects containing media info for a given artist.
	 * Used for getting journals, status updates, and polls.
	 * 
	 * @param start_gui Used for canceling and showing progress
	 * @param artist DeviantArt artist
	 * @param id Module ID for getting pages, leave null to be retrieved automatically
	 * @param directory Directory to move DVKs to, if specified
	 * @param type Type of gallery to scan ('j' - Journals, 's' - Status Updates, 'p' - Polls)
	 * @param dvk_handler Used to check for already downloaded files
	 * @param check_all Whether to check all gallery pages
	 * @param offset Gallery page offset
	 * @return List of Dvks with DeviantArt info
	 */
	public ArrayList<Dvk> get_module_pages(
			StartGUI start_gui,
			String artist,
			final String id,
			File directory,
			char type,
			DvkHandler dvk_handler,
			boolean check_all,
			int offset) {
		StringBuilder url;
		String mod_id = id;
		if(id == null) {
			//GET INITIAL URL
			url = new StringBuilder("https://www.deviantart.com/");
			url.append(artist);
			url.append("/posts/journals");
			//CONNECT
			if(this.connect == null) {
				initialize_connect();
			}
			String xpath = "//div[@id='root']/following-sibling::script";
			this.connect.load_page(url.toString(), xpath, 2);
			try {
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			} catch (InterruptedException e) {}
			if(this.connect.get_page() == null) {
				if(offset == 0) {
					return new ArrayList<>();
				}
				return null;
			}
			//GET ID
			int end;
			int start = 0;
			ArrayList<String> ids = new ArrayList<>();
			String script = ((DomElement)this.connect.get_page().getFirstByXPath(xpath)).asXml();
			start = script.indexOf("moduleId");
			while(start != -1) {
				end = script.indexOf('}', start);
				ids.add(script.substring(start, end));
				start = script.indexOf("moduleId", end);
			}
			String search = "";
			switch(type) {
				case 'j':
					search = "journals";
					break;
				case 's':
					search = "statuses";
					break;
				case 'p':
					search = "polls";
					break;
			}
			for(int i = 0; i < ids.size(); i++) {
				if(ids.get(i).contains(search) && !ids.get(i).contains("featured")) {
					start = ids.get(i).indexOf(':') + 1;
					end = ids.get(i).indexOf(',', start);
					mod_id = ids.get(i).substring(start, end);
					break;
				}
			}
		}
		//GET JSON URL
		url = new StringBuilder("https://www.deviantart.com/_napi/da-user-profile/api/module/");
		switch(type) {
			case 'j':
				url.append("journals?username=");
				break;
			case 's':
				url.append("statuses?username=");
				break;
			case 'p':
				url.append("polls?username=");
				break;
		}
		url.append(artist);
		url.append("&moduleid=");
		url.append(mod_id);
		url.append("&mode=newest&limit=24&offset=");
		url.append(offset);
		try {
			//LOAD PAGE
			if(this.connect == null) {
				initialize_connect();
			}
			JSONObject json = null;
			if(start_gui == null || !start_gui.get_base_gui().is_canceled()) {
				json = this.connect.load_json(url.toString(), 2);
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			}
			if(json == null) {
				if(offset == 0) {
					return new ArrayList<>();
				}
				return null;
			}
			//GET PAGES
			JSONArray arr;
			ArrayList<Dvk> dvks = new ArrayList<>();
			arr = json.getJSONArray("results");
			boolean check_next = true;
			int size = dvk_handler.get_size();
			for(int i = 0; i < arr.length(); i++) {
				boolean contains = false;
				JSONObject obj = arr.getJSONObject(i);
				String link = obj.getString("url");
				String page_id = get_page_id(link);
				for(int k = 0; k < size; k++) {
					if(get_page_id(dvk_handler.get_dvk(k).get_page_url()).equals(page_id)) {
						contains = true;
						//UPDATE DVK LOCATION IF ALREADY DOWNLOADED
						Dvk dvk;
						dvk = ArtistHosting.move_dvk(dvk_handler.get_dvk(k), directory);
						dvk_handler.set_dvk(dvk, k);
						//ENDS IF DOWNLOADED DVK IS NOT A SINGLE DOWNLOAD
						String[] tags = dvk.get_web_tags();
						if(!ArrayProcessing.contains(tags, "dvk:single", false)) {
							check_next = false;
						}
						break;
					}
				}
				if(!contains) {
					Dvk summary = new Dvk();
					summary.set_page_url(link);
					summary.set_artist(obj.getJSONObject("author").getString("username"));
					summary.set_time(obj.getString("publishedTime").substring(0, 16));
					if(type == 'p') {
						summary.set_title(obj.getString("title"));
						//GET POLL RESULTS
						JSONArray poll = obj.getJSONObject("poll").getJSONArray("answers");
						StringBuilder results = new StringBuilder();
						for(int r = 0; r < poll.length(); r++) {
							results.append(Integer.toString(poll.getJSONObject(r).getInt("votes")));
							results.append("<DVK-POLL-SEP>");
							results.append(poll.getJSONObject(r)
									.getJSONObject("textContent").getString("excerpt"));
							results.append("<DVK-POLL-SEP>");
						}
						summary.set_description(results.toString());
					}
					else if(type == 's') {
						summary.set_description(obj.getJSONObject("textContent").getString("excerpt"));
					}
					dvks.add(summary);
				}
			}
			//GET NEXT PAGES
			boolean more = json.getBoolean("hasMore");
			if(more && (check_all || check_next)) {
				ArrayList<Dvk> next = get_module_pages(
						start_gui, artist, mod_id, directory, type, dvk_handler, check_all, offset + 24);
				if(next == null) {
					if(offset == 0) {
						if(start_gui != null) {
							start_gui.append_console("deviantart_failed", true);
							start_gui.get_base_gui().set_canceled(true);
						}
						return new ArrayList<>();
					}
					return null;
				}
				dvks.addAll(next);
			}
			//REMOVE DUPLICATE ENTRIES
			for(int i = 0; i < dvks.size(); i++) {
				for(int k = i + 1; k < dvks.size(); k++) {
					if(dvks.get(i).get_page_url().equals(dvks.get(k).get_page_url())) {
						dvks.remove(k);
						k--;
					}
				}
			}
			return dvks;
		}
		catch(Exception e) {}
		//RETURN BLANK IF FAILED
		if(offset == 0) {
			return new ArrayList<>();
		}
		return null;
	}
}
