package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.InOut;
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
		//CHECKS IF FROM DEVIANTART
		if(!page_url.contains("deviantart.com/")) {
			return null;
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
		else if(page_url.contains("/poll/")) {
			s1 = page_url.indexOf("/poll/") + 1;
			s1 = page_url.indexOf('/', s1) + 1;
			suffix = "-P";
		}
		else {
			return null;
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
			return null;
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
		if(this.connect != null) {
			String xpath = "//input[@id='username']";
			String url = "https://www.deviantart.com/users/login";
			this.connect.load_page(url, xpath, 1);
			HtmlInput pass;
			try {
				//INPUT USERNAME
				HtmlInput user = this.connect.get_page()
						.getFirstByXPath(xpath);
				user.setValueAttribute(username);
				//INPUT PASSWORD
				xpath = "//input[@id='password']";
				pass = this.connect.get_page()
						.getFirstByXPath(xpath);
				pass.setValueAttribute(password);
				//SUBMIT INFO
				xpath = "//button[@id='loginbutton']";
				HtmlButton submit = this.connect.get_page()
						.getFirstByXPath(xpath);
				this.connect.set_page((HtmlPage)submit.click());
			} catch (Exception e) {
				e.printStackTrace();
				this.connect.initialize_client();
			}
			pass = null;
		}
	}
	
	/**
	 * Returns whether connect object is logged in to DeviantArt.
	 * 
	 * @return Whether connect is logged in
	 */
	public boolean is_logged_in() {
		if(this.connect == null
				|| this.connect.get_page() == null) {
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
	 * @param gallery Gallery tag to add to Dvk
	 * @param directory Directory in which to save Dvk.
	 * @param artist Artist to use when adding favorite tag.
	 * Doesn't create favorite tag if null.
	 * @param single Whether this is a single download
	 * @param save Whether to save Dvk and media
	 * @return Dvk of DeviantArt media page
	 */
	@SuppressWarnings("resource")
	public Dvk get_dvk(
			String page_url,
			String gallery,
			File directory,
			String artist,
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
		this.connect.load_page(url, xpath, 1);
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {}
		try {
			//CHECK IF STILL LOGGED IN
			if(!is_logged_in()) {
				throw new Exception();
			}
			//GET DESCRIPTION
			DomElement de;
			xpath = "//a[contains(@class,'user-link')]/parent::div/"
					+ "following-sibling::div"
					+ "[contains(@class,'legacy-journal')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de != null) {
				dvk.set_description(DConnect.clean_element(
						de.asXml(), true));
			}
			//GET TEXT IF LITERATURE PAGE
			int end;
			String text = null;
			xpath = "//div[contains(@class,'legacy-journal')]//script/parent::div";
			de = this.connect.get_page().getFirstByXPath(xpath);
			if(de != null) {
				text = DConnect.clean_element(
						de.asXml(), true);
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
			String download = null;
			DomAttr da = null;
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
			//GET JSON
			xpath = "//link[@type='application/json+oembed']/@href";
			da = this.connect.get_page().getFirstByXPath(xpath);
			WebClient client = this.connect.get_client();
			UnexpectedPage page;
			page = (UnexpectedPage)client.getPage(da.getNodeValue());
			String res = page.getWebResponse().getContentAsString();
			JSONObject json = new JSONObject(res);
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
			else if (rating.equals("adult")){
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
			else if(download == null
					&& type.equals("photo")) {
				dvk.set_direct_url(image);
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
				dvk.set_media_file(filename + ".txt");
			}
			else {
				ext = StringProcessing.get_extension(
						dvk.get_direct_url());
				dvk.set_media_file(filename + ext);
			}
			//SET SECONDARY FILE
			if(dvk.get_secondary_url() != null) {
				ext = StringProcessing.get_extension(
						dvk.get_secondary_url());
				dvk.set_secondary_file(filename + ext);
			}
			//SAVE FILES
			if(save) {
				if(type.equals("rich")) {
					dvk.write_dvk();
					InOut.write_file(dvk.get_media_file(), text);
					if(dvk.get_secondary_url() != null) {
						this.connect.download(
								dvk.get_secondary_url(),
								dvk.get_secondary_file());
					}
				}
				else {
					dvk.write_media(this.connect);
				}
			}
			return dvk;
		}
		catch(Exception e){}
		return new Dvk();
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
	@SuppressWarnings("resource")
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
		this.connect.load_page(url, xpath, 1);
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {}
		try {
			//CHECK IF STILL LOGGED IN
			if(!is_logged_in()) {
				throw new Exception();
			}
			//GET DESCRIPTION
			int end;
			DomElement de;
			String desc = null;
			xpath = "//div[contains(@class,'legacy-journal')]//script/parent::div";
			de = this.connect.get_page().getFirstByXPath(xpath);
			desc = DConnect.clean_element(de.asXml(), true);
			start = desc.indexOf("<script");
			end = desc.indexOf("</script>", start);
			end = desc.indexOf('>', end) + 1;
			desc = desc.substring(0, start) + desc.substring(end);
			dvk.set_description(desc);
			//GET JSON
			DomAttr da;
			xpath = "//link[@type='application/json+oembed']/@href";
			da = this.connect.get_page().getFirstByXPath(xpath);
			WebClient client = this.connect.get_client();
			UnexpectedPage page;
			page = (UnexpectedPage)client.getPage(da.getNodeValue());
			String res = page.getWebResponse().getContentAsString();
			JSONObject json = new JSONObject(res);
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
			else if (rating.equals("adult")){
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
			dvk.set_media_file(filename + ".txt");
			//SET SECONDARY FILE
			if(dvk.get_secondary_url() != null) {
				String ext;
				ext = StringProcessing.get_extension(
						dvk.get_secondary_url());
				dvk.set_secondary_file(filename + ext);
			}
			//SAVE
			if(save) {
				desc = "<!DOCTYPE html><html>" + 
						desc + "</html>";
				dvk.write_dvk();
				InOut.write_file(dvk.get_media_file(), desc);
				if(dvk.get_secondary_url() != null) {
					this.connect.download(
							dvk.get_secondary_url(),
							dvk.get_secondary_file());
				}
			}
			return dvk;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new Dvk();
	}
}
