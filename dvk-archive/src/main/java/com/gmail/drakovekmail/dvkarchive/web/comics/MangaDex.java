package com.gmail.drakovekmail.dvkarchive.web.comics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;
import com.gmail.drakovekmail.dvkarchive.web.DConnectSelenium;
import com.gmail.drakovekmail.dvkarchive.web.artisthosting.ArtistHosting;

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
		connect.load_page(url, xpath, 2);
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {}
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
							DConnect.clean_element(de.asXml(), true));
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
	 * @param start_gui StartGUI for showing progress
	 * @param language Language of chapters to include
	 * @param page Page of chapter list to start scanning from
	 * @return ArrayList of Dvk objects containing chapter info
	 */
	public static ArrayList<Dvk> get_chapters(
			DConnect connect,
			Dvk base_dvk,
			StartGUI start_gui,
			String language,
			int page) {
		//LOAD TITLE PAGE
		ArrayList<Dvk> dvks = new ArrayList<>();
		String url = base_dvk.get_page_url() 
				+ "/chapters/" + Integer.toString(page);
		String xpath = "//a[@class='text-truncate']";
		connect.load_page(url, xpath, 2);
		//CHECK PAGE LOADED
		if(connect.get_page() == null) {
			if(start_gui != null) {
				start_gui.get_base_gui().set_canceled(true);
				start_gui.append_console("mangadex_failed", true);
			}
			if(page == 1) {
				return new ArrayList<>();
			}
			return null;
		}
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
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
		xpath = "//a[@class='page-link'][contains(@href,'chapters/" + Integer.toString(page + 1) + "/')]";
		ds = connect.get_page().getByXPath(xpath);
		if(ds.size() > 0 && (start_gui == null || !start_gui.get_base_gui().is_canceled())) {
			ArrayList<Dvk> next = get_chapters(connect, base_dvk, start_gui, language, page + 1);
			if(next != null) {
				dvks.addAll(next);
			}
			else if(page > 1) {
				return null;
			}
			else {
				return new ArrayList<>();
			}
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
	
	/**
	 * Returns a list of Dvks for MangaDex media.
	 * 
	 * @param connect Object for connecting to MangaDex
	 * @param dvk_handler Used to check for already downloaded media
	 * @param start_gui StartGui for showing progress
	 * @param directory Directory to save to, if necessary
	 * @param chapters MangaDex chapters to download from
	 * @param check_all Whether to check all chapters
	 * @param save Whether to save Dvk objects to disk
	 * @return List of Dvks for MangaDex media
	 */
	public static ArrayList<Dvk> get_dvks(
			DConnectSelenium connect,
			DvkHandler dvk_handler,
			StartGUI start_gui,
			File directory,
			ArrayList<Dvk> chapters,
			boolean check_all,
			boolean save) {
		//TEST IF PARAMETERS ARE VALID
		if(dvk_handler == null
				|| directory == null
				|| !directory.isDirectory()) {
			return new ArrayList<>();
		}
		DConnect unit_connect = new DConnect(false, false);
		int start = get_start_chapter(dvk_handler, chapters, check_all);
		ArrayList<Dvk> dvks = new ArrayList<>();
		for(int c = start; c > -1; c--) {
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, start - c, start + 1);
				start_gui.append_console(chapters.get(c).get_title(), false);
			}
			int total;
			int page = 1;
			for(total = 100000; page <= total; page++) {
				if(start_gui != null && start_gui.get_base_gui().is_canceled()) {
					break;
				}
				//SET KNOWN INFO
				Dvk dvk = new Dvk();
				dvk.set_id("MDX" + chapters.get(c).get_id()
						+ "-" + Integer.toString(page));
				dvk.set_title(chapters.get(c).get_title()
						+ " | Pg. " + Integer.toString(page));
				dvk.set_artists(chapters.get(c).get_artists());
				dvk.set_time(chapters.get(c).get_time());
				dvk.set_web_tags(chapters.get(c).get_web_tags());
				dvk.set_description(chapters.get(c).get_description());
				dvk.set_page_url(chapters.get(c).get_page_url()
						+ "/" + Integer.toString(page));
				//CHECK IF ALREADY DOWNLOADED
				boolean contains = false;
				int size = dvk_handler.get_size();
				for(int i = 0; i < size; i++) {
					String url = dvk_handler.get_dvk(i).get_page_url();
					if(url.contains("/mangadex.")
							&& url.endsWith(chapters.get(c).get_id()
									+ "/" + Integer.toString(page))) {
						contains = true;
						break;
					}
				}
				//SKIP IF ALREADY DOWNLOADED
				if(!contains) {
					//LOAD PAGE
					String xpath = "//div[@data-page='" + Integer.toString(page)
						+ "']//img[@class='noselect nodrag cursor-pointer']";
					connect.load_page(dvk.get_page_url(), xpath, 1);
					try {
						TimeUnit.MILLISECONDS.sleep(2000);
					} catch (InterruptedException e) {}
					if(connect.get_page() == null) {
						break;
					}
					//CHECK IF IN RIGHT CHAPTER
					xpath = "//span[@class='chapter-title']/@data-chapter-id";
					DomAttr da;
					da = connect.get_page().getFirstByXPath(xpath);
					if(da == null
							|| !da.getNodeValue().equals(chapters.get(c).get_id())) {
						break;
					}
					//GET TOTAL PAGES
					xpath = "//div[@id='content']/@data-total-pages";
					da = connect.get_page().getFirstByXPath(xpath);
					try {
						int v = Integer.parseInt(da.getNodeValue());
						total = v;
					}
					catch(Exception f) {
						break;
					}
					//GET IMAGE URL
					xpath = "//div[@data-page='" + Integer.toString(page)
						+ "']//img[@class='noselect nodrag cursor-pointer']/@src";
					da = connect.get_page().getFirstByXPath(xpath);
					if(da == null) {
						break;
					}
					dvk.set_direct_url(da.getNodeValue());
					//SET FILE
					String filename = dvk.get_filename();
					dvk.set_dvk_file(new File(directory, filename + ".dvk"));
					String extension = StringProcessing.get_extension(
							dvk.get_direct_url());
					dvk.set_media_file(filename + extension);
					//SAVE, IF SPECIFIED
					if(save) {
						dvk.write_media(unit_connect);
					}
					//APPEND DVK
					dvks.add(dvk);
				}
			}
			if(total != 100000 && page <= total) {
				break;
			}
		}
		unit_connect.close_client();
		return dvks;
	}
	
	/**
	 * Returns MangaDex title ID from array of tags.
	 * If there is no MangaDex title tag, returns empty.
	 * 
	 * @param tags String array of tags
	 * @return MangaDex title ID
	 */
	public static String get_id_from_tags(String[] tags) {
		String id = new String();
		for(String tag: tags) {
			if(tag.toLowerCase().startsWith("mangadex:")) {
				return tag.substring(tag.indexOf(':') + 1);
			}
		}
		return id;
	}
	
	/**
	 * Returns a list of Dvks representing downloaded MangaDex titles.
	 * Dvks include title, MangaDex ID, and file location.
	 * 
	 * @param dvk_handler DvkHandler for getting downloaded files.
	 * @return List of MangaDex Dvks
	 */
	public static ArrayList<Dvk> get_downloaded_titles(DvkHandler dvk_handler) {
		int size = dvk_handler.get_size();
		ArrayList<String> ids = new ArrayList<>();
		ArrayList<Dvk> dvks = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			Dvk dvk = dvk_handler.get_dvk(i);
			if(dvk.get_page_url().toLowerCase().contains("mangadex.")) {
				String id = get_id_from_tags(dvk.get_web_tags());
				if(id.length() > 0) {
					int index = ids.indexOf(id);
					if(index == -1) {
						String title = dvk.get_title();
						int end = title.indexOf('|');
						if(end == -1) {
							end = title.length();
						}
						dvk = new Dvk();
						dvk.set_title(title.substring(0, end));
						dvk.set_id(id);
						dvk.set_dvk_file(
								dvk_handler.get_dvk(i).get_dvk_file().getParentFile());
						ids.add(id);
						dvks.add(dvk);
					}
					else {
						dvk = new Dvk();
						dvk.set_title(dvks.get(index).get_title());
						dvk.set_id(id);
						File file = dvk_handler.get_dvk(i)
								.get_dvk_file().getParentFile();
						file = ArtistHosting.get_common_directory(
								dvks.get(index).get_dvk_file(), file);
						dvk.set_dvk_file(file);
						dvks.set(index, dvk);
					}
				}
			}
		}
		return dvks;
	}
}
