package com.gmail.drakovekmail.dvkarchive.web.comics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;

/**
 * Class for downloading media from MangaDex.
 * 
 * @author Drakovek
 */
public class MangaDex {
	
	/**
	 * Returns the title id for a given MangaDex title URL
	 * Returns empty if not a valid title URL.
	 * 
	 * @param url MangaDex title URL
	 * @return MangaDex title ID
	 */
	public static String get_title_id(String url) {
		//CHECK IF URL IS MANGADEX TITLE
		if(!url.contains("mangadex.")
				|| !url.contains("/title/")) {
			return new String();
		}
		//GET ID STRING
		int start = url.indexOf("/title/") + 1;
		start = url.indexOf('/', start) + 1;
		int end = url.indexOf('/', start);
		if(end == -1) {
			end = url.length();
		}
		//CHECK IF ID IS VALID
		try {
			String sub = url.substring(start, end);
			Integer.parseInt(sub);
			return sub;
		}
		catch(NumberFormatException e) {
			return "";
		}
	}
	
	/**
	 * Returns the title id for a given MangaDex chapter URL
	 * Returns empty if not a valid chapter URL.
	 * 
	 * @param url MangaDex chapter URL
	 * @return MangaDex chapter ID
	 */
	public static String get_chapter_id(String url) {
		//CHECK IF URL IS MANGADEX CHAPTER
		if(!url.contains("mangadex.")
				|| !url.contains("/chapter/")) {
			return new String();
		}
		//GET ID STRING
		int start = url.indexOf("/chapter/") + 1;
		start = url.indexOf('/', start) + 1;
		int end = url.indexOf('/', start);
		if(end == -1) {
			end = url.length();
		}
		//CHECK IF ID IS VALID
		try {
			String sub = url.substring(start, end);
			Integer.parseInt(sub);
			return sub;
		}
		catch(NumberFormatException e) {
			return "";
		}
	}
	
	/**
	 * Returns a Dvk object with the base title info for a given ID.
	 * Contains title, artists, tags, description, and page URL.
	 * 
	 * @param connect DConnect object for web connection
	 * @param id MangaDex title ID
	 * @return Dvk object with title info
	 */
	public static Dvk get_title_info(DConnect connect, String id) {
		Dvk dvk = new Dvk();
		String url = "https://mangadex.org/title/" + id + "/";
		String xpath = "//span[@class='mx-1']";
		connect.load_page(url, xpath);
		try {
			//GET TITLE
			DomElement de = connect.get_page().getFirstByXPath(xpath);
			dvk.set_title(de.asText());
			//GET AUTHOR
			String[] artists = new String[2];
			xpath = "//a[contains(@href,'/search?author=')]";
			de = connect.get_page().getFirstByXPath(xpath);
			artists[0] = de.asText();
			//GET ARTIST
			xpath = "//a[contains(@href,'/search?artist=')]";
			de = connect.get_page().getFirstByXPath(xpath);
			artists[1] = de.asText();
			dvk.set_artists(artists);
			//GET TAGS
			List<DomElement> ds;
			xpath = "//a[@class='genre']";
			ds = connect.get_page().getByXPath(xpath);
			xpath = "//a[@class='badge badge-secondary']";
			ds.addAll(connect.get_page().getByXPath(xpath));
			String[] tags = new String[ds.size() + 1];
			tags[0] = "MangaDex:" + id;
			for(int i = 0; i < ds.size(); i++) {
				tags[i + 1] = ds.get(i).asText();
			}
			dvk.set_web_tags(tags);
			//GET DESCRIPTION
			xpath = "//div[@class='col-lg-3 col-xl-2 strong']";
			ds = connect.get_page().getByXPath(xpath);
			for(int i = 0; i < ds.size(); i++) {
				if(ds.get(i).asText().equals("Description:")) {
					de = ds.get(i).getNextElementSibling();
					dvk.set_description(
							DConnect.remove_header_footer(de.asXml()));
					break;
				}
			}
			//GET PAGE URL
			String element;
			xpath = "/title/" + id;
			List<DomAttr> da = connect.get_page().getByXPath("//a/@href");
			for(int i = 0; i < da.size(); i++) {
				element = da.get(i).getNodeValue();
				if(element.startsWith(xpath)) {
					int end = element.indexOf(id);
					end = element.indexOf('/', end) + 1;
					end = element.indexOf('/', end);
					if(end == 0) {
						end = element.length();
					}
					element = element.substring(0, end);
					element = "https://mangadex.org" + element;
					dvk.set_page_url(element);
					break;
				}
			}
			//RETURN DVK
			return dvk;
		}
		catch(Exception e) {
			return new Dvk();
		}
	}
	
	/**
	 * Returns a list of Dvk objects for all chapters of a given title.
	 * Dvks contain chapter title, artists, time published,
	 * tags, page URL, description, and chapter ID.
	 * 
	 * @param connect DConnect object for web connection.
	 * @param base_dvk Dvk containing title info, as from get_title_info
	 * @param language Language of chapters to include
	 * @param page Page of chapter list to start scanning from
	 * @return ArrayList of Dvk objects containing chapter info
	 */
	public static ArrayList<Dvk> get_chapters(
			DConnect connect,
			Dvk base_dvk,
			String language,
			int page) {
		//LOAD TITLE PAGE
		ArrayList<Dvk> dvks = new ArrayList<>();
		String url = base_dvk.get_page_url() 
				+ "/chapters/" + Integer.toString(page);
		String xpath = "//span[@class='mx-1']";
		connect.load_page(url, xpath);
		//CHECK PAGE LOADED
		if(connect.get_page() == null) {
			return new ArrayList<>();
		}
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e1) {}
		//GET CHAPTER LINKS
		DomElement de;
		List<DomElement> ds;
		xpath = "//span[@title='" + language + "']/parent::div";
		ds = connect.get_page().getByXPath(xpath);
		for(int i = 0; i < ds.size(); i++) {
			try {
				//SET KNOWN INFO
				Dvk dvk = new Dvk();
				dvk.set_web_tags(base_dvk.get_web_tags());
				dvk.set_description(base_dvk.get_description());
				//GET TITLE AND PAGE URL
				de = ds.get(i).getPreviousElementSibling();
				while(!de.asXml().contains("pr-1")) {
					de = de.getPreviousElementSibling();
				}
				for(DomElement child: de.getChildElements()) {
					String link = child.getAttribute("href");
					if(link.length() > 0) {
						dvk.set_page_url("https://mangadex.org" + link);
						dvk.set_title(base_dvk.get_title()
								+ " | " + child.asText());
						break;
					}
				}
				//GET TIME PUBLISHED
				de = ds.get(i).getPreviousElementSibling();
				while(!de.asXml().contains("order-lg-8")) {
					de = de.getPreviousElementSibling();
				}
				dvk.set_time(de.getAttribute("title").substring(0, 16));
				//GET TRANSLATION GROUP
				String[] artists = new String[base_dvk.get_artists().length + 1];
				for(int k = 0; k < base_dvk.get_artists().length; k++) {
					artists[k + 1] = base_dvk.get_artists()[k];
				}
				de = ds.get(i).getNextElementSibling();
				while(!de.asXml().contains("chapter-list-group")) {
					de = de.getNextElementSibling();
				}
				for(DomElement child: de.getChildElements()) {
					String link = child.getAttribute("href");
					if(link.length() > 0) {
						artists[0] = child.asText();
						break;
					}
				}
				dvk.set_artists(artists);
				//SET ID
				dvk.set_id(get_chapter_id(dvk.get_page_url()));
				//APPEND TO DVK LIST
				dvks.add(dvk);
			}
			catch(Exception e) {}
		}
		//SEE WHETHER TO CHECK FURTHER CHAPTER LISTS
		xpath = "//a[@class='text-truncate']";
		ds = connect.get_page().getByXPath(xpath);
		if(ds.size() > 0) {
			dvks.addAll(get_chapters(
					connect, base_dvk, language, page + 1));
		}
		return dvks;
	}
	
	/**
	 * Returns which MangaDex chapter to start downloading from.
	 * Based on what chapters have already been downloaded.
	 * 
	 * @param dvk_handler DvkHandler for checking downloaded chapters
	 * @param chapters Dvks for MangaDex chapters, as from get_chapters
	 * @param check_all Whether to check all chapters.
	 * 					If true, returns last chapter index.
	 * @return Index of chapter to start downloading from
	 */
	public static int get_start_chapter(
			DvkHandler dvk_handler,
			ArrayList<Dvk> chapters,
			boolean check_all) {
		if(check_all) {
			return chapters.size() - 1;
		}
		int chapter;
		boolean contains = false;
		int size = dvk_handler.get_size();
		for(chapter = 0; !contains && chapter < chapters.size(); chapter++) {
			String cid = chapters.get(chapter).get_id();
			for(int k = 0; k < size; k++) {
				String hid = get_chapter_id(dvk_handler.get_dvk(k).get_page_url());
				if(hid.length() > 0 && hid.equals(cid)) {
					contains = true;
					break;
				}
			}
		}
		return chapter - 1;
	}
}
