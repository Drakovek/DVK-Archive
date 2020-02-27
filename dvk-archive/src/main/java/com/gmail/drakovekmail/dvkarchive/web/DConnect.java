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

/**
 * Class containing methods for dealing with web content.
 * 
 * @author Drakovek
 */
public class DConnect {
	
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
	 * Initializes the DConnect class by opening a WebClient.
	 * 
	 * @param css Whether to use CSS styling when loading pages
	 * @param javascript Whether to load Javascript when loading pages
	 */
	public DConnect(boolean css, boolean javascript) {
		//Turn off HtmlUnit warnings
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		initialize_client(css, javascript);
	}
	
	/**
	 * Initializes and opens the web_client.
	 * Uses given CSS and Javascript settings.
	 * 
	 * @param use_css Whether to use CSS styling when loading pages
	 * @param use_javascript Whether to load Javascript when loading pages
	 */
	public void initialize_client(boolean use_css, boolean use_javascript) {
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
		this.web_client.setJavaScriptTimeout(10000);
		this.web_client.setAjaxController(new NicelyResynchronizingAjaxController());
		this.web_client.getOptions().setTimeout(10000);
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
				close_client();
				System.gc();
				initialize_client();
				HTMLParser parser;
				parser = this.web_client.getPageCreator().getHtmlParser();
				URL url = new URL("https://www.notreal.com");
				StringWebResponse r = new StringWebResponse(html, url);
				this.page = parser.parseHtml(
						r, this.web_client.getCurrentWindow());
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
		close_client();
		System.gc();
		initialize_client();
		this.web_client.setCookieManager(cookies);
		try {
			this.page = this.web_client.getPage(url);
			this.web_client.waitForBackgroundJavaScript(10000);
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
			int timeout = 11;
			boolean exists = false;
			DomElement de;
			while(!exists && timeout > -1) {
				de = get_page().getFirstByXPath(element);
				if(de == null) {
					timeout--;
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
	 * Closes the web_client and loaded page.
	 */
	public void close_client() {
		this.page = null;
		this.web_client.close();
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
		UnexpectedPage u_page;
		@SuppressWarnings("resource")
		InputStream is = null;
		@SuppressWarnings("resource")
		OutputStream os = null;
		try {
			u_page = this.web_client.getPage(url);
			is = u_page.getWebResponse().getContentAsStream();
			os = new FileOutputStream(file);
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
		finally {
			try {
				if(is != null) {
					is.close();
				}
			}
			catch(IOException f) {}
			try {
				if(os != null) {
					os.close();
				}
			}
			catch(IOException f) {}
			is = null;
			os = null;
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
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		@SuppressWarnings("resource")
		FileOutputStream fos = null;
		byte[] b = null;
		byte[] full_data = null;
		int data = 0;
		try {
			URLConnection connect = new URL(url).openConnection();
			connect.setRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0");
			connect.connect();
			is = new BufferedInputStream(connect.getInputStream());
			baos = new ByteArrayOutputStream();
			b = new byte[1024];
			data = 0;
			while(-1 != (data = is.read(b))) {
				baos.write(b, 0, data);
			}
			full_data = baos.toByteArray();
			fos = new FileOutputStream(file);
			fos.write(full_data);
		}
		catch(IOException e) {}
		finally {
			try {
				if(is != null) {
					is.close();
				}
			}
			catch(IOException f) {}
			try {
				if(baos != null) {
					baos.close();
				}
			}
			catch(IOException f) {}
			try {
				if(fos != null) {
					fos.close();
				}
			}
			catch(IOException f) {}
			is = null;
			baos = null;
			fos = null;
		}
	}
	
	/**
	 * Removes the HTML header and footer tags from a given element.
	 * 
	 * @param html HTML element
	 * @return Element with header and footer tags removed
	 */
	public static String remove_header_footer(String html) {
		String str = html.replace("\n", "");
		str = str.replace("\r", "");
		//REMOVE HEADER
		int start;
		if(str.startsWith("<")) {
			start = str.indexOf('>');
			str = str.substring(start + 1);
		}
		//REMOVE FOOTER
		int end;
		if(str.endsWith(">")) {
			end = str.lastIndexOf('<');
			if(end != -1) {
				str = str.substring(0, end);
			}
		}
		//REMOVE FRONT SPACE
		while(str.startsWith(" ")) {
			str = str.substring(1);
		}
		//REMOVE END SPACE
		while(str.endsWith(" ")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
}
