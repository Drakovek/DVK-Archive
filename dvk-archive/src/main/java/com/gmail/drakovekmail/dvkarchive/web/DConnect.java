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
		//Turn off HtmlUnit warnings
		LogFactory.getFactory().setAttribute(
				"org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger
			.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger
	    	.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
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
	public void initialize_client(
			boolean use_css,
			boolean use_javascript) {
		this.css = use_css;
		this.javascript = use_javascript;
		initialize_client();
	}
	
	/**
	 * Initializes and opens the web_client.
	 */
	public void initialize_client() {
		this.web_client = new WebClient(BrowserVersion.BEST_SUPPORTED);
		this.web_client.getOptions().setCssEnabled(this.css);
		this.web_client.getOptions().setJavaScriptEnabled(this.javascript);
		this.web_client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		this.web_client.getOptions().setThrowExceptionOnScriptError(false);
		this.web_client.setJavaScriptTimeout(this.timeout * 1000);
		this.web_client.setAjaxController(new NicelyResynchronizingAjaxController());
		this.web_client.getOptions().setTimeout(this.timeout * 1000);
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
				List<WebWindow> windows = this.web_client.getWebWindows();
				for(WebWindow window: windows) {
					window.getJobManager().removeAllJobs();
					window.getJobManager().shutdown();
					window = null;
				}
				close();
				System.gc();
				initialize_client();
				HTMLParser parser;
				parser = this.web_client.getPageCreator().getHtmlParser();
				URL url = new URL("https://www.notreal.com");
				StringWebResponse r;
				r = new StringWebResponse(html, url);
				this.page = parser.parseHtml(r, this.web_client.getCurrentWindow());
			}
			catch (Exception e) {
				this.page = null;
			}
		}
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
		for(int i = 0; this.get_page() == null && i < (tries - 1); i++) {
			load_page(url, element);
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
		this.page = null;
		CookieManager cookies = this.web_client.getCookieManager();
		List<WebWindow> windows = this.web_client.getWebWindows();
		for(WebWindow window: windows) {
			window.getJobManager().removeAllJobs();
			window.getJobManager().shutdown();
			window = null;
		}
		try {
			close();
		}
		catch(DvkException e) {}
		System.gc();
		initialize_client();
		this.web_client.setCookieManager(cookies);
		try {
			UnexpectedPage u_page;
			u_page = (UnexpectedPage)this.web_client.getPage(url.toString());
			String res = u_page.getWebResponse().getContentAsString();
			JSONObject json = new JSONObject(res);
			TimeUnit.MILLISECONDS.sleep(2000);
			return json;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Loads a given URL to a HtmlPage object.
	 * 
	 * @param url Given URL
	 * @param element XPath element to wait for
	 */
	private void load_page(String url, String element) {
		CookieManager cookies = this.web_client.getCookieManager();
		List<WebWindow> windows = this.web_client.getWebWindows();
		for(WebWindow window: windows) {
			window.getJobManager().removeAllJobs();
			window.getJobManager().shutdown();
			window = null;
		}
		try {
			close();
		}
		catch(DvkException e) {}
		System.gc();
		initialize_client();
		this.web_client.setCookieManager(cookies);
		try {
			this.page = this.web_client.getPage(url);
			if(!wait_for_element(element)) {
				this.page = null;
			}
		}
		catch(Exception e) {
			this.page = null;
		}
	}
	
	/**
	 * Sets main page of DConnect directly.
	 * 
	 * @param page HtmlPage
	 */
	public void set_page(HtmlPage page) {
		this.page = page;
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
				while(!exists && secs > -1) {
					de = get_page().getFirstByXPath(element);
					if(de == null) {
						secs--;
						try {
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch (InterruptedException e) {}
					}
					else {
						exists = true;
					}
				}
				return exists;
			}
			catch(Exception e) {
				return false;
			}
		}
		return true;
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
	 * Returns the current web_client.
	 * 
	 * @return WebClient
	 */
	public WebClient get_client() {
		return this.web_client;
	}
	
	@Override
	public void close() throws DvkException {
		this.page = null;
		if(this.web_client != null) {
			this.web_client.close();
		}
		this.web_client = null;
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
		UnexpectedPage u_page = null;
		try {
			u_page = this.web_client.getPage(url);
		}
		catch(Exception e) {
			u_page = null;
		}
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
				if(fallback) {
					basic_download(url, file);
				}
			}
		}
		else if(fallback) {
			basic_download(url, file);
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
		byte[] b = null;
		byte[] full_data = null;
		int data = 0;
		URLConnection connect = null;
		try {
			connect = new URL(url).openConnection();
			connect.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0");
			connect.connect();
		}
		catch(Exception e) {}
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

	/**
	 * Cleans up HTML element.
	 * Removes whitespace and removes header and footer tags.
	 * 
	 * @param html HTML element
	 * @param remove_ends Whether to remove header and footer tags
	 * @return Cleaned HTML element
	 */
	public static String clean_element(String html, boolean remove_ends) {
		String str = html.replace("\n", "");
		str = str.replace("\r", "");
		//REMOVE WHITESPACE BETWEEN TAGS
		while(str.contains("  <")) {
			str = str.replace("  <", " <");
		}
		while(str.contains(">  ")) {
			str = str.replace(">  ", "> ");
		}
		//REMOVE HEADER AND FOOTER
		if(remove_ends) {
			int start = str.indexOf('>') + 1;
			int end = str.lastIndexOf('<');
			if(start > 0 && start <= end) {
				str = str.substring(start, end);
			}
		}
		//REMOVE WHITESPACE
		while(str.startsWith(" ")) {
			str = str.substring(1);
		}
		while(str.endsWith(" ")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
}
