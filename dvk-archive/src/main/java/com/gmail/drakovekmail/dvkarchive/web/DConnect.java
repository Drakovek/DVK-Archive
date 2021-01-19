package com.gmail.drakovekmail.dvkarchive.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.parser.HTMLParser;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;

/**
 * Class containing methods for dealing with web content.
 * 
 * @author Drakovek
 */
public class DConnect implements AutoCloseable {
	
	/**
	 * WebClient for accessing web pages
	 */
	private WebClient web_client;
	
	/**
	 * Currently loaded web page
	 */
	private HtmlPage page;
	
	/**
	 * Whether to use CSS styling when loading pages
	 */
	private boolean css;
	
	/**
	 * Whether to load Javascript when loading pages
	 */
	private boolean javascript;
	
	/**
	 * Time in seconds to wait before timing out of connection
	 */
	private int timeout;
	
	/**
	 * Initializes the DConnect class by opening a WebClient.
	 * 
	 * @param css Whether to use CSS styling when loading pages
	 * @param javascript Whether to load Javascript when loading pages
	 * @exception DvkException DvkException
	 */
	public DConnect(boolean css, boolean javascript) throws DvkException {
		//TURN OFF HTMLUNIT WARNINGS
		String name = "org.apache.commons.logging.Log";
		String value = "org.apache.commons.logging.impl.NoOpLog";
		LogFactory.getFactory().setAttribute(name, value);
		name = "com.gargoylesoftware.htmlunit";
		java.util.logging.Logger.getLogger(name).setLevel(java.util.logging.Level.OFF);
		name = "org.apache.http";
		java.util.logging.Logger.getLogger(name).setLevel(java.util.logging.Level.OFF);
	    //INITIALIZE THE HTMLUNIT WEBCLIENT
	    close();
		initialize_client(css, javascript);
		this.timeout = 10;
	}
	
	/**
	 * Sets the timeout period to given number of seconds.
	 * 
	 * @param seconds Seconds to wait before timing out
	 */
	public void set_timeout(int seconds) {
		this.timeout = seconds;
	}
	
	/**
	 * Initializes and opens the web_client.
	 * Uses given CSS and Javascript settings.
	 * 
	 * @param use_css Whether to use CSS styling when loading pages
	 * @param use_javascript Whether to load Javascript when loading pages
	 */
	public void initialize_client(boolean use_css, boolean use_javascript) {
		//SET USE_CSS AND USE_JAVASCRIPT VARIABLES
		this.css = use_css;
		this.javascript = use_javascript;
		//INITIALIZE THE HTMLUNIT WEBCLIENT
		initialize_client();
	}
	
	/**
	 * Initializes and opens the web_client.
	 */
	public void initialize_client() {
		//SET THE BROWSER TYPE FOR THE WEBCLIENT TO EMULATE
		this.web_client = new WebClient(BrowserVersion.BEST_SUPPORTED);
		//SET WHETHER TO LOAD CSS AND JAVASCRIPT FROM WEB PAGES
		this.web_client.getOptions().setCssEnabled(this.css);
		this.web_client.getOptions().setJavaScriptEnabled(this.javascript);
		//DISABLE THROWING AN EXCEPTION ON A JAVASCRIPT OR HTML RESPONSE ERROR
		this.web_client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.web_client.getOptions().setThrowExceptionOnScriptError(false);
		//SET THE AMOUNT OF TIME BEFORE LOADING WEB PAGES OR JAVASCRIPT TIMES OUT
		this.web_client.setJavaScriptTimeout(this.timeout * 1000);
		this.web_client.setAjaxController(new NicelyResynchronizingAjaxController());
		this.web_client.getOptions().setTimeout(this.timeout * 1000);
	}
	
	/**
	 * Closes all windows from the WebClient while preserving cookies to reduce memory usage.
	 */
	private void close_windows() {
		//GET THE CURRENTLY STORED COOKIES FROM THE WEBCLIENT
		this.page = null;
		CookieManager cookies = this.web_client.getCookieManager();
		//CLOSE ALL THE WEBCLIENT'S WINDOWS
		List<WebWindow> windows = this.web_client.getWebWindows();
		for(WebWindow window: windows) {
			window.getJobManager().removeAllJobs();
			window.getJobManager().shutdown();
			window = null;
		}
		//CLOSE THE WEBCLIENT TO PREVENT INCREASING MEMORY USE FROM OPENING A NEW WINDOW
		try {
			close();
		}
		catch(DvkException e) {}
		System.gc();
		//RE-INITIALIZE THE WEBCLIENT WITH THE SAME STORED COOKIES
		initialize_client();
		this.web_client.setCookieManager(cookies);
	}

	/**
	 * Loads a given URL to a HtmlPage object.
	 * 
	 * @param url Given URL
	 * @param element XPath element to wait for
	 * @param tries How many times to attempt loading page
	 */
	public void load_page(String url, String element, int tries) {
		load_page(url, element);
		//IF SPECIFIED AND LOADING PAGE DIDN'T WORK PROPERLY THE FIRST TIME, ATTEMPT LOADING AGAIN
		for(int i = 0; this.get_page() == null && i < (tries - 1); i++) {
			load_page(url, element);
		}
	}
	
	/**
	 * Loads a given URL to a HtmlPage object.
	 * 
	 * @param url Given URL
	 * @param element XPath element to wait for
	 */
	private void load_page(String url, String element) {
		//CLOSE WINDOWS TO FREE UP WEBCLIENT MEMORY
		close_windows();
		//LOAD THE WEBPAGE FOR THE GIVEN URL
		try {
			this.page = this.web_client.getPage(url);
			//IF SPECIFIED, WAIT FOR A GIVEN ELEMENT TO LOAD
			if(!wait_for_element(element)) {
				//SET PAGE TO NULL IF THE ELEMENT FAILS TO LOAD OR TIMES OUT
				this.page = null;
			}
		}
		catch(Exception e) {
			//SET PAGE TO NULL IF LOADING PAGE FAILS IN SOME WAY
			this.page = null;
		}
	}
	
	/**
	 * Returns a JSONObject from a given JSON URL.
	 * 
	 * @param url URL containing JSON data
	 * @param tries How many times to attempt loading page
	 * @return JSONObject from URL
	 */
	public JSONObject load_json(String url, int tries) {
		JSONObject json = null;
		load_json(url);
		//IF SPECIFIED AND LOADING PAGE DIDN'T WORK PROPERLY THE FIRST TIME, ATTEMPT LOADING AGAIN
		for(int i = 0; json == null && i < (tries - 1); i++) {
			json = load_json(url);
		}
		return json;
	}
	
	/**
	 * Returns a JSONObject from a given JSON URL.
	 * 
	 * @param url URL containing JSON data
	 * @return JSONObject from URL
	 */
	private JSONObject load_json(String url) {
		//CLOSE WINDOWS TO FREE UP WEBCLIENT MEMORY
		close_windows();
		try {
			//LOAD GIVEN WEBPAGE AS JSON
			UnexpectedPage u_page;
			u_page = (UnexpectedPage)this.web_client.getPage(url.toString());
			String res = u_page.getWebResponse().getContentAsString();
			JSONObject json = new JSONObject(res);
			TimeUnit.MILLISECONDS.sleep(2000);
			return json;
		}
		catch(Exception e) {
			//RETURN NULL IF LOADING AS JSON FAILS
			return null;
		}
	}
	
	/**
	 * Waits for a given element to appear in the loaded HtmlPage.
	 * 
	 * @param element Element to wait for in XPATH format
	 * @return Whether the element appeared. False if timed out.
	 */
	public boolean wait_for_element(String element) {
		if(element != null) {
			int secs = this.timeout;
			boolean exists = false;
			DomElement de;
			try {
				//CHECK EVERY SECOND UNTIL TIMEOUT OR ELEMENT FOUND
				while(!exists && secs > -1) {
					//ATTEMPT TO FIND ELEMENT
					de = get_page().getFirstByXPath(element);
					if(de == null) {
						//IF ELEMENT NOT FOUND, WAIT ONE SECOND
						secs--;
						try {
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch (InterruptedException e) {}
					}
					else {
						//IF ELEMENT IS FOUND, SET THE EXIST VARIABLE TO TRUE
						exists = true;
					}
				}
				//RETURN WHETHER THE GIVEN ELEMENT WAS FOUND
				return exists;
			}
			catch(Exception e) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Downloads a file from given URL to given file.
	 * Uses HtmlUnit.
	 * 
	 * @param url Given URL
	 * @param file Given file
	 */
	public void download(String url, File file) {
		download(url, file, true);
	}
	
	/**
	 * Downloads a file from given URL to given file.
	 * Uses HtmlUnit.
	 * 
	 * @param url Given URL
	 * @param file Given file
	 * @param fallback Whether to use basic_download if download fails.
	 */
	public void download(String url, File file, boolean fallback) {
		if(file != null) {
			//ATTEMPT TO LOAD MEDIA URL AS AN UNEXPECTED PAGE
			UnexpectedPage u_page = null;
			try {
				u_page = this.web_client.getPage(url);
			}
			catch(Exception e) {
				u_page = null;
			}
			//IF LOADING TO PAGE WORKED, SAVE AS A FILE
			if(u_page != null) {
				try (InputStream is = u_page.getWebResponse().getContentAsStream();
						OutputStream os = new FileOutputStream(file)){
					byte[] buffer = new byte[1024];
					int read;
					while((read = is.read(buffer)) != -1) {
						os.write(buffer, 0, read);
					}
				}
				catch(Exception e) {
					//ATTEMPT A DIFFERENT DOWNLOAD TECHNIQUE IF THE DOWNLOAD FAILS
					if(fallback) {
						basic_download(url, file);
					}
				}
			}
			else if(fallback) {
				//ATTEMPT A DIFFERENT DOWNLOAD TECHNIQUE IF LOADING AS PAGE FAILS
				basic_download(url, file);
			}
		}
	}
	
	/**
	 * Downloads a file from given URL to given file.
	 * Uses standard Java connection methods.
	 * 
	 * @param url Given URL
	 * @param file Given file
	 */
	public static void basic_download(String url, File file) {
		//CHECK PARAMETERS ARE VALID
		if(url != null && file != null) {
			//INITIALIZE VARIABLES
			byte[] b = null;
			byte[] full_data = null;
			int data = 0;
			URLConnection connect = null;
			//ATTEMPT TO OPEN A CONNECTION TO THE GIVEN WEB PAGE
			try {
				connect = new URL(url).openConnection();
				String agent = "Mozilla/5.0 (X11; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0";
				connect.setRequestProperty("User-Agent", agent);
				connect.connect();
			}
			catch(Exception e) {}
			//SAVE CONTENTS OF THE WEB PAGE TO A FILE
			if(connect != null) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
						FileOutputStream fos = new FileOutputStream(file);
						InputStream is = new BufferedInputStream(connect.getInputStream())) {
					b = new byte[1024];
					data = 0;
					while(-1 != (data = is.read(b))) {
						baos.write(b, 0, data);
					}
					full_data = baos.toByteArray();
					fos.write(full_data);
				}
				catch(IOException e) {}
			}
		}
	}
	
	/**
	 * Loads a given HTML String as HtmlPage.
	 * 
	 * @param html Given HTML formatted String
	 */
	public void load_from_string(String html) {
		if(html == null || html.length() == 0) {
			this.page = null;
		}
		else {
			try {
				//CLOSE WINDOWS TO REDUCE MEMORY USAGE
				close_windows();
				//SET WEBCLIENT LOADED PAGE TO THE GIVEN HTML STRING
				HTMLParser parser = this.web_client.getPageCreator().getHtmlParser();
				URL url = new URL("https://www.notreal.com");
				StringWebResponse resp = new StringWebResponse(html, url);
				this.page = parser.parseHtml(resp, this.web_client.getCurrentWindow());
			}
			catch (Exception e) {
				this.page = null;
			}
		}
	}
	
	/**
	 * Sets the currently loaded HtmlPage directly.
	 * 
	 * @param page HtmlPage to use
	 */
	public void set_page(HtmlPage page) {
		this.page = page;
	}
	
	/**
	 * Returns the currently loaded HtmlPage.
	 * 
	 * @return Loaded HtmlPage
	 */
	public HtmlPage get_page() {
		return this.page;
	}
	
	/**
	 * Safely closes the WebClient
	 */
	@Override
	public void close() throws DvkException {
		this.page = null;
		if(this.web_client != null) {
			this.web_client.close();
		}
		this.web_client = null;
	}
}
