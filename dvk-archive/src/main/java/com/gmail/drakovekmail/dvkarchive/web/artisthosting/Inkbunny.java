package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.file.InOut;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;
import com.gmail.drakovekmail.dvkarchive.web.DConnect;

/**
 * Class for downloading media from Inkbunny.net
 * 
 * @author Drakovek
 */
public class Inkbunny extends ArtistHosting {
	
	/**
	 * Milliseconds to wait after connection events for rate limiting.
	 */
	private static final int SLEEP = 2000;
	
	/**
	 * DvkHandler for checking already downloaded DVK files
	 */
	private DvkHandler dvk_handler;
	
	/**
	 * DConnect object for downloading media files
	 */
	private DConnect connect;
	
	/**
	 * Session ID for connecting to the Inkbunny API
	 */
	private String sid;
	
	/**
	 * Initializes the Inkbunny class.
	 * 
	 * @param start_gui StartGUI for showing progress
	 * @param dvk_handler DvkHandler for checking already downloaded DVK files
	 */
	//TODO REINSTATE
	/*
	public Inkbunny(StartGUI start_gui, DvkHandler dvk_handler) {
		this.sid = new String();
		this.start_gui = start_gui;
		this.dvk_handler = dvk_handler;
		this.connect = null;
	}
	*/
	
	/**
	 * Initializes the DConnect object.
	 */
	public void initialize_connect() {
		try {
			this.connect = new DConnect(false, false);
		} catch (DvkException e) {
			this.connect = null;
		}
	}
	
	/**
	 * Returns ID for Inkbunny submission or journal URL.
	 * 
	 * @param url Inkbunny submission or journal URL
	 * @param use_ends Whether to include the prefix and suffix for the Dvk ID
	 * @return Page ID
	 */
	public static String get_page_id(String url, boolean use_ends) {
		//CHECK IF URL IS FROM INKBUNNY
		if(!url.toLowerCase().contains("inkbunny.net/")) {
			return new String();
		}
		//GET START INDEX
		int start;
		String suffix = new String();
		if(url.contains("/s/")) {
			start = url.lastIndexOf("/s/") + 3;
		}
		else if(url.contains("/j/")) {
			start = url.lastIndexOf("/j/") + 3;
			suffix = "-J";
		}
		else {
			return new String();
		}
		//GET END INDEX
		int end = url.indexOf('-', start);
		if(end == -1) {
			end = url.indexOf('/', start);
		}
		if(end == -1) {
			end = url.length();
		}
		//RETURN SECTION OF URL AS ID
		String section = url.substring(start, end);
		if(!section.matches("^[0-9]+$")) {
			return new String();
		}
		if(use_ends) {
			return "INK" + section + suffix;
		}
		return url.substring(start, end);
	}
	
	/**
	 * Returns a JSONObject from a POST request to the given URL with given parameters.
	 * 
	 * @param post_url URL to sent HTML POST request
	 * @param params Parameters for the POST request
	 * @return JSONObject response from the POST request
	 */
	private static JSONObject json_post(String post_url, List<NameValuePair> params) {
		HttpPost post = new HttpPost(post_url);
		try(CloseableHttpClient client = HttpClients.createDefault()) {
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			try(CloseableHttpResponse response = client.execute(post)) {
				try(InputStream stream = response.getEntity().getContent()) {
					String response_string = IOUtils.toString(stream, StandardCharsets.UTF_8);
					return new JSONObject(response_string);
				}
			}
		}
		catch(IOException e) {}
		catch(JSONException f) {}
		return null;
	}
	
	/**
	 * Returns if object is logged in to the Inkbunny API.
	 * 
	 * @return Whether object is logged in.
	 */
	public boolean is_logged_in() {
		return this.sid.length() > 0;
	}
	
	/**
	 * Attempts to login to the Inkbunny API, setting the session ID.
	 * 
	 * @param username Inkbunny Username
	 * @param password Inkbunny Password
	 * @return Whether login was successful
	 */
	public boolean login(String username, String password) {
		//GET LOGIN JSON
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = json_post("https://inkbunny.net/api_login.php", params);
		try {
			String id = json.getString("sid");
			this.sid = id;
			return true;
		}
		catch(JSONException e) {}
		catch(NullPointerException f) {}
		this.sid = new String();
		return false;
	}
	
	/**
	 * Returns the user ID for a given Inkbunny username.
	 * 
	 * @param username Inkbunny username
	 * @return Inkbunny user ID
	 */
	public String get_user_id(String username) {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("sid", this.sid));
		params.add(new BasicNameValuePair("count_limit", "1"));
		params.add(new BasicNameValuePair("username", username));
		JSONObject json = json_post("https://inkbunny.net/api_search.php", params);
		try {
			JSONObject sub = json.getJSONArray("submissions").getJSONObject(0);
			String user_id = sub.getString("user_id");
			return user_id;
		}
		catch(JSONException e) {}
		catch(NullPointerException e) {}
		return "VOID";
	}
	
	/**
	 * Returns a list of submissions in a given Inkbunny gallery.
	 * List uses Dvk objects.
	 * Dvk page_url contains Inkbunny submission id.
	 * Dvk id contains number of pages in the submission.
	 * 
	 * @param user_id Inkbunny user ID
	 * @param directory Directory to move DVKs if necessary
	 * @param type Type of gallery to scan. 'm' - Main gallery, 's' - Scraps gallery, 'f' - Favorites gallery
	 * @param fav_artist Artist to check for in favorites tags when scanning favorites gallery
	 * @param check_all Whether to check all pages in the gallery, not just the newest
	 * @param page_limit Number of submissions per gallery page to show
	 * @return List of Dvks objects for submissions in a given user's Inkbunny gallery
	 * @throws DvkException Throws DvkException if the program can't connect to Inkbunny.net
	 */
	//TODO REINSTATE
	/*
	public ArrayList<String> get_pages(
			String user_id,
			File directory,
			char type,
			String fav_artist,
			boolean check_all,
			int page_limit) throws DvkException {
		//INITIALIZE VARIABLES
		int max_pages;
		int page_count;
		JSONObject base;
		JSONObject json;
		JSONArray array;
		boolean check_next;
		boolean remove;
		String sub_id;
		String rid = null;
		List<NameValuePair> params = new ArrayList<>();
		ArrayList<String> return_ids = new ArrayList<>();
		ArrayList<String> cur_ids;
		ArrayList<Integer> page_counts;
		ArrayList<Dvk> search_dvks;
		params.add(new BasicNameValuePair("get_rid", "yes"));
		if(type == 'm') {
			params.add(new BasicNameValuePair("scraps", "no"));
			params.add(new BasicNameValuePair("user_id", user_id));
			params.add(new BasicNameValuePair("orderby", "last_file_update_datetime"));
		}
		else if(type == 's') {
			params.add(new BasicNameValuePair("scraps", "only"));
			params.add(new BasicNameValuePair("user_id", user_id));
			params.add(new BasicNameValuePair("orderby", "last_file_update_datetime"));
		}
		else {
			params.add(new BasicNameValuePair("favs_user_id", user_id));
			params.add(new BasicNameValuePair("orderby", "fav_datetime"));
		}
		//RUN THROUGH GALLERY PAGES
		for(int page_num = 0; page_num < 50000; page_num++) {
			//LOAD GALLERY JASON OBJECT
			params.add(new BasicNameValuePair("sid", this.sid));
			params.add(new BasicNameValuePair("page", Integer.toString(page_num)));
			params.add(new BasicNameValuePair("submissions_per_page", Integer.toString(page_limit)));
			base = json_post("https://inkbunny.net/api_search.php", params);
			try {
				check_next = true;
				TimeUnit.MILLISECONDS.sleep(SLEEP);
				//BREAK IF CANCELLED
				if(this.start_gui != null && this.start_gui.get_base_gui().is_canceled()) {
					return new ArrayList<>();
				}
				//RETURN DVKS IF LAST PAGE
				max_pages = base.getInt("pages_count");
				array = base.getJSONArray("submissions");
				if(array.length() == 0 || page_num > max_pages) {
					return ArrayProcessing.clean_list(return_ids);
				}
				rid = base.getString("rid");
				//GET SUBMISSION IDS
				cur_ids = new ArrayList<>();
				page_counts = new ArrayList<>();
				for(int arr_num = 0; arr_num < array.length(); arr_num++) {
					json = array.getJSONObject(arr_num);
					sub_id = json.getString("submission_id");
					page_count = Integer.parseInt(json.getString("pagecount"));
					Dvk submission = new Dvk();
					submission.set_page_url(sub_id);
					submission.set_sql_id(page_count);
					for(int num = 0; num < page_count; num++) {
						cur_ids.add("INK" + sub_id + "-" + Integer.toString(num + 1));
						page_counts.add(Integer.valueOf(page_count));
					}
				}
				search_dvks = get_dvks_from_ids(this.dvk_handler, cur_ids);
				//REMOVE IDS THAT ARE ALREADY DOWNLOADED
				for(int dvk_num = 0; dvk_num < search_dvks.size(); dvk_num++) {
					remove = true;
					Dvk dvk = search_dvks.get(dvk_num);
					int index = cur_ids.indexOf(search_dvks.get(dvk_num).get_dvk_id());
					if(type == 'f') {
						//ADDS TO LIST, ONLY IF FAVORITE OF GIVEN ARTIST
						check_next = false;
						if(!ArrayProcessing.contains(dvk.get_web_tags(),
								"favorite:" + fav_artist, false)) {
							check_next = true;
							remove = false;
						}
					}
					else {
						//STOPS RUNNING IF NOT A SINGLE DOWNLOAD
						if(dvk.get_dvk_id().endsWith(
								"-" + Integer.toString(page_counts.get(index).intValue()))
								&& !ArrayProcessing.contains(dvk.get_web_tags(),
								"dvk:single", false)) {
							check_next = false;
						}
						//UPDATE DVK HANDLER		
						dvk = ArtistHosting.move_dvk(dvk, directory);
						this.dvk_handler.set_dvk(dvk, dvk.get_sql_id());
					}
					if(remove) {
						cur_ids.remove(index);
						page_counts.remove(index);
					}
				}
				//REMOVE ENDINGS
				for(int id_num = 0; id_num < cur_ids.size(); id_num++) {
					String id = cur_ids.get(id_num);
					String ext = Integer.toString(page_counts.get(id_num).intValue());
					return_ids.add(id.substring(0, id.indexOf('-')) + "-" + ext);
				}
				//CHECK NEXT
				if(!check_all && !check_next) {
					return ArrayProcessing.clean_list(return_ids);
				}
			}
			catch (Exception e) {
				//TEST IF STILL ONLINE
				if(this.connect == null) {
					initialize_connect();
				}
				String xpath;
				xpath = "//div[@id='banner_clickarea']//h2[@id='banner_panel']//a[@href='https://inkbunny.net/']";
				this.connect.load_page("https://inkbunny.net/stats.php", xpath, 1);
				if(this.connect.get_page() != null) {
					return new ArrayList<>();
				}
				throw new DvkException();
			}
			//SET UP PARAMS FOR NEXT LOOP
			params = new ArrayList<>();
			params.add(new BasicNameValuePair("rid", rid));
		}
		throw new DvkException();
	}
	*/
	
	/**
	 * Returns a list of Inkbunny journal IDs for a given artist.
	 * 
	 * @param artist Inkbunny artist
	 * @param directory Directory to move downloaded DVK files, if necessary
	 * @param check_all Whether to check all gallery pages or just the newest
	 * @return List of Inkbunny journal IDs
	 * @throws DvkException Throws DvkException if loading gallery page fails
	 */
	//TODO REINSTATE
	/*
	public ArrayList<String> get_journal_pages(
			String artist,
			File directory,
			boolean check_all) throws DvkException{
		//GET URL
		StringBuilder url = new StringBuilder("https://inkbunny.net/journals/");
		url.append(artist);
		url.append('/');
		//INITIALIZE VARIABLES
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath;
		boolean check_next;
		List<DomAttr> das;
		ArrayList<Dvk> dvks;
		ArrayList<String> cur_ids;
		ArrayList<String> return_ids = new ArrayList<>();
		//RUN THROUGH PAGES
		for(int page_num = 1; page_num < 50000; page_num++) {
			//BREAK IF CANCELLED
			if(this.start_gui != null && this.start_gui.get_base_gui().is_canceled()) {
				return new ArrayList<>();
			}
			//LOAD PAGE
			xpath = "//div[@id='banner_clickarea']//h2[@id='banner_panel']//a[@href='https://inkbunny.net/']";
			this.connect.load_page(url.toString() + Integer.toString(page_num), xpath, 2);
			try {
				TimeUnit.MILLISECONDS.sleep(SLEEP);
			} catch (InterruptedException e1) {}
			boolean failed = this.connect.get_page() == null;
			xpath = "//div[@class='content']//div[contains(@style,'font-family')]//a[contains(@href,'/j/')]";
			boolean contains = this.connect.wait_for_element(xpath);
			if(!contains) {
				if(!failed) {
					return new ArrayList<>();
				}
				throw new DvkException();
			}
			xpath = xpath + "/@href";
			check_next = true;
			try {
				//RUN THROUGH JOURNAL URLS ON GALLERY PAGE, GETTING IDS
				cur_ids = new ArrayList<>();
				das = this.connect.get_page().getByXPath(xpath);
				for(int att_num = 0; att_num < das.size(); att_num++) {
					cur_ids.add(get_page_id("inkbunny.net" + das.get(att_num).getNodeValue(), true));
				}
				dvks = get_dvks_from_ids(this.dvk_handler, cur_ids);
				//REMOVE ALREADY DOWNLOADED IDS
				for(int dvk_num = 0; dvk_num < dvks.size(); dvk_num++) {
					//UPDATE DVK LOCATION AND FAVORITE IF ALREADY DOWNLOADED
					Dvk dvk = ArtistHosting.move_dvk(dvks.get(dvk_num), directory);
					this.dvk_handler.set_dvk(dvk, dvk.get_sql_id());
					if(!ArrayProcessing.contains(dvk.get_web_tags(), "dvk:single", false)) {
						check_next = false;
					}
					int index = cur_ids.indexOf(dvk.get_dvk_id());
					if(index != -1) {
						cur_ids.remove(index);
					}
				}
				return_ids.addAll(cur_ids);
				//SEARCH FOR NEXT BUTTON
				xpath = "//table[@class='bottomPaginatorBox']//a[@title='next page']";
				DomElement de = this.connect.get_page().getFirstByXPath(xpath);
				if(de == null || (!check_next && !check_all)) {
					return ArrayProcessing.clean_list(return_ids);
				}
			}
			catch(Exception e) {
				throw new DvkException();
			}
		}
		throw new DvkException();
	}
	*/
	
	/**
	 * Returns a list of Dvk objects for a given Inkbunny submission ID.
	 * 
	 * @param sub_id Inkbunny submission ID
	 * @param directory Directory in which to save files
	 * @param single Whether this is a single download
	 * @param favorites Favorites tags to add to Dvks
	 * @param save Whether to save Dvks and associated media to disk
	 * @return List of Dvk objects from given submission ID
	 * @throws DvkException Throws DvkException if getting API info fails
	 */
	//TODO REINSTATE
	/*
	public ArrayList<Dvk> get_dvks(
			String sub_id,
			File directory,
			boolean single,
			ArrayList<String> favorites,
			boolean save) throws DvkException {
		if(this.connect == null) {
			initialize_connect();
		}
		//CHECK FOR EXISTING DVKS
		String id = sub_id.substring(0, sub_id.indexOf('-'));
		id = id.replace("INK", "");
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.DVK_ID);
		sql.append(" LIKE ? AND ");
		sql.append(DvkHandler.DVK_ID);
		sql.append(" NOT LIKE ?;");
		String[] sql_params = {"INK" + id + "-%", "%-J"};
		try(ResultSet rs = this.dvk_handler.sql_select(sql.toString(), sql_params, true)) {
			int page_count = Integer.parseInt(sub_id.substring(sub_id.indexOf('-') + 1));
			ArrayList<Dvk> search_dvks = DvkHandler.get_dvks(rs);
			if(search_dvks.size() > 0) {
				if(search_dvks.size() < page_count) {
					ArrayList<String> new_favs;
					if(favorites != null) {
						new_favs = favorites;
					}
					else {
						new_favs = new ArrayList<>();
					}
					//DELETE DOWNLOADED DVKS IF FULL SET FROM PAGE NOT DOWNLOADED
					for(int i = 0; i < search_dvks.size(); i++) {
						Dvk dvk = search_dvks.get(i);
						dvk.get_dvk_file().delete();
						dvk.get_media_file().delete();
						if(dvk.get_secondary_file() != null) {
							dvk.get_secondary_file().delete();
						}
						this.dvk_handler.delete_dvk(dvk.get_sql_id());
						for(int k = 0; k < dvk.get_web_tags().length; k++) {
							if(dvk.get_web_tags()[k].toLowerCase().startsWith("favorite:")) {
								new_favs.add(dvk.get_web_tags()[k]);
							}
						}
					}
					return this.get_dvks(
							sub_id, directory, single, new_favs, save);
				}
				if(favorites != null) {
					//ADD FAVORITES
					for(int i = 0; i < search_dvks.size(); i++) {
						Dvk dvk = search_dvks.get(i);
						ArrayList<String> tags = ArrayProcessing.array_to_list(dvk.get_web_tags());
						for(int k = 0; k < favorites.size(); k++) {
							tags.add(favorites.get(k));
						}
						dvk.set_web_tags(ArrayProcessing.list_to_array(tags));
						dvk.write_dvk();
						this.dvk_handler.set_dvk(dvk, dvk.get_sql_id());
					}
				}
				return search_dvks;
			}
		}
		catch(SQLException e) {}
		//GET JSON OBJECT
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("sid", this.sid));
		params.add(new BasicNameValuePair("submission_ids", id));
		params.add(new BasicNameValuePair("show_description_bbcode_parsed", "yes"));
		params.add(new BasicNameValuePair("show_writing_bbcode_parsed", "yes"));
		params.add(new BasicNameValuePair("show_pools", "yes"));
		JSONObject json_base = json_post("https://inkbunny.net/api_submissions.php", params);
		try {
			//GET BASIC INFO
			JSONObject json = json_base.getJSONArray("submissions").getJSONObject(0);
			String title = json.getString("title");
			String artist = json.getString("username");
			String time = json.getString("create_datetime").substring(0, 16);
			String description = null;
			try {
				description = json.getString("description_bbcode_parsed");
				if(description.startsWith("<span style=")) {
					description = DConnect.clean_element(description, true);
				}
			}
			catch(JSONException f) {
				description = null;
			}
			//GET WRITING IF AVAILABLE
			String writing = null;
			try {
				writing = json.getString("writing_bbcode_parsed");
				if(writing.startsWith("<span style=")) {
					writing = DConnect.clean_element(writing, true);
				}
				if(writing.length() == 0) {
					writing = null;
				}
				else {
					writing = "<!DOCTYPE html><html>" + writing + "</html>";
				}
			}
			catch(JSONException f) {
				writing = null;
			}
			
			//GET GALLERY
			ArrayList<String> tags = new ArrayList<>();
			if(json.getString("scraps").equals("f")) {
				tags.add("Gallery:Main");
			}
			else {
				tags.add("Gallery:Scraps");
			}
			//GET RATING
			String rating = json.getString("rating_id");
			switch(rating) {
				case "0":
					tags.add("Rating:General");
					break;
				case "1":
					tags.add("Rating:Mature");
					break;
				case "2":
					tags.add("Rating:Adult");
					break;
			}
			//GET CATEGORY
			tags.add(json.getString("type_name"));
			//GET KEYWORDS
			try {
				JSONArray keywords = json.getJSONArray("keywords");
				for(int i = 0; i < keywords.length(); i++) {
					tags.add(keywords.getJSONObject(i).getString("keyword_name"));
				}
			}
			catch(JSONException f) {}
			//GET POOLS
			try {
				JSONArray pools = json.getJSONArray("pools");
				for(int i = 0; i < pools.length(); i++) {
					tags.add(pools.getJSONObject(i).getString("name"));
				}
			}
			catch(JSONException f) {}
			//ADD SINGLE AND FAVORITES TAGS
			if(single) {
				tags.add("DVK:Single");
			}
			if(favorites != null) {
				for(int i = 0; i < favorites.size(); i++) {
					tags.add(favorites.get(i));
				}
			}
			String[] web_tags = ArrayProcessing.list_to_array(tags);
			//SET PAGE URL
			String page_url = "https://inkbunny.net/s/" + id;
			//SET MEDIA URLS
			JSONObject file;
			JSONArray files = json.getJSONArray("files");
			int size = files.length();
			String[] media_urls = new String[size];
			String[] s_urls = new String[size];
			int[] page_nums = new int[size];
			for(int i = 0; i < size; i++) {
				file = files.getJSONObject(i);
				media_urls[i] = file.getString("file_url_full");
				page_nums[i] = Integer.parseInt(file.getString("submission_file_order")) + 1;
				try {
					s_urls[i] = file.getString("thumbnail_url_huge");
				}
				catch(JSONException f) {
					s_urls[i] = null;
				}
				if(writing != null) {
					s_urls[i] = media_urls[i];
					media_urls[i] = null;
				}
			}
			//CREATE DVKS
			String ext;
			String filename;
			ArrayList<Dvk> dvks = new ArrayList<>();
			for(int i = 0; i < size; i++) {
				Dvk dvk = new Dvk();
				dvk.set_dvk_id("INK" + id + "-" + page_nums[i]);
				if(size > 1) {
					dvk.set_title(title + " [" + page_nums[i] + "/" + size + "]");
				}
				else {
					dvk.set_title(title);
				}
				dvk.set_artist(artist);
				dvk.set_time(time);
				dvk.set_description(description);
				dvk.set_web_tags(web_tags);
				dvk.set_page_url(page_url);
				dvk.set_direct_url(media_urls[i]);
				dvk.set_secondary_url(s_urls[i]);
				filename = dvk.get_filename(false);
				dvk.set_dvk_file(new File(directory, filename + ".dvk"));
				ext = StringProcessing.get_extension(dvk.get_direct_url());
				if(ext.length() == 0) {
					ext = ".html";
				}
				dvk.set_media_file(filename + ext);
				if(dvk.get_secondary_url() != null) {
					ext = StringProcessing.get_extension(dvk.get_secondary_url());
					dvk.set_secondary_file(dvk.get_filename(true) + ext);
				}
				if(save) {
					if(writing == null) {
						dvk.write_media(this.connect);
					}
					else {
						dvk.write_dvk();
						InOut.write_file(dvk.get_media_file(), writing);
						if(dvk.get_secondary_url() != null)
						{
							this.connect.download(dvk.get_secondary_url(), dvk.get_secondary_file());
						}
					}
					TimeUnit.MILLISECONDS.sleep(SLEEP);
					if(!dvk.get_dvk_file().exists()) {
						throw new DvkException();
					}
				}
				dvks.add(dvk);
				this.dvk_handler.add_dvk(dvk);
			}
			return dvks;
		}
		catch(Exception e) {}
		throw new DvkException();
	}
	*/
	
	/**
	 * Returns a DVK formatted time string from the date given on an Inkbunny.net page.
	 * 
	 * @param time_string Time string from Inkbunny.net submission
	 * @return Dvk formatted time publication string
	 */
	public static String get_time(String time_string) {
		String str = time_string.toLowerCase();
		//GET MONTH
		String[] months = {"jan", "feb", "mar", "apr", "may", "jun",
				"jul", "aug", "sep", "oct", "nov", "dec"};
		int month;
		for(month = 0; month < months.length && !str.contains(months[month]); month++);
		month++;
		if(month > 12) {
			return null;
		}
		try {
			//GET DAY
			int start;
			for(start = 0; start < str.length() && str.charAt(start) == ' '; start++);
			int end = str.indexOf(' ', start);
			int day = Integer.parseInt(str.substring(start, end));
			//GET MINUTE
			start = str.indexOf(':') + 1;
			end = str.indexOf(' ', start);
			if(end == -1) {
				end = str.length();
			}
			int minute = Integer.parseInt(str.substring(start, end));
			//GET HOUR
			end = start - 1;
			start = str.lastIndexOf(' ', end) + 1;
			int hour = Integer.parseInt(str.substring(start, end));
			//GET YEAR
			for(end = start - 1; end > -1 && str.charAt(end) == ' '; end--);
			start = str.lastIndexOf(' ', end) + 1;
			end++;
			int year = Integer.parseInt(str.substring(start, end));
			//RETURN STRING
			StringBuilder time = new StringBuilder();
			time.append(StringProcessing.pad_int(year, 4));
			time.append('/');
			time.append(StringProcessing.pad_int(month, 2));
			time.append('/');
			time.append(StringProcessing.pad_int(day, 2));
			time.append('|');
			time.append(StringProcessing.pad_int(hour, 2));
			time.append(':');
			time.append(StringProcessing.pad_int(minute, 2));
			return time.toString();
		}
		catch(Exception e) {}
		return null;
	}
	
	/**
	 * Returns a Dvk for a given Inkbunny journal.
	 * 
	 * @param journal_id ID for an Inkbunny.net journal
	 * @param directory Directory in which to save Dvk and media, if specified
	 * @param single Whether this is a single download
	 * @param save Whether to save the DVK file and associated media
	 * @return Dvk object for the Inkbunny journal
	 * @throws DvkException Throws DvkException if loading journal page fails
	 */
	public Dvk get_journal_dvk(
			String journal_id,
			File directory,
			boolean single,
			boolean save) throws DvkException {
		StringBuilder url = new StringBuilder("https://inkbunny.net/j/");
		url.append(journal_id.replace("INK", "").replace("-J", ""));
		if(this.connect == null) {
			initialize_connect();
		}
		String xpath = "//div[@class='content']//table//h1[contains(@style,'font')]";
		try {
			//GET TITLE
			this.connect.load_page(url.toString(), xpath, 2);
			TimeUnit.MILLISECONDS.sleep(SLEEP);
			Dvk dvk = new Dvk();
			DomElement de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_title(de.asText());
			//GET ARTIST
			xpath = "//div[contains(@class,'elephant')][not(contains(@class,'bottom'))]"
					+ "//a[@class='widget_userNameSmall']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_artist(de.asText());
			//GET TIME
			xpath = "//span[@id='submittime_exact']";
			de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_time(get_time(de.asText()));
			//SET DESCRPITON
			xpath = "//div[contains(@class,'elephant_white')]//div[@class='content']//span[contains(@style,'word-wrap')]";
			de = this.connect.get_page().getFirstByXPath(xpath);
			dvk.set_description(DConnect.clean_element(de.asXml(), true));
			//SET ID
			dvk.set_dvk_id(journal_id);
			//SET PAGE URL
			dvk.set_page_url(url.toString());
			//SET TAGS
			String[] tags = new String[2];
			tags[0] = "Gallery:Journals";
			if(single) {
				tags[1] = "DVK:Single";
			}
			dvk.set_web_tags(tags);
			//SET FILES
			String filename = dvk.get_filename(false);
			dvk.set_dvk_file(new File(directory, filename + ".dvk"));
			dvk.set_media_file(filename + ".html");
			if(save) {
				dvk.write_dvk();
				InOut.write_file(dvk.get_media_file(),
						"<!DOCTYPE html><html>" + dvk.get_description() + "</html>");
				if(!dvk.get_dvk_file().exists()) {
					throw new Exception();
				}
			}
			this.dvk_handler.add_dvk(dvk);
			return dvk;
		}
		catch(Exception e) {}
		throw new DvkException();
	}
	
	/**
	 * Closes all connections used when downloading Inkbunny info.
	 */
	public void close() {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("sid", this.sid));
		json_post("https://inkbunny.net/api_logout.php", params);
		if(this.connect != null) {
			try {
				this.connect.close();
				this.connect = null;
			} catch (DvkException e) {}
		}
		if(this.dvk_handler != null) {
			try {
				this.dvk_handler.close();
			} catch (DvkException e) {}
		}
	}
}
