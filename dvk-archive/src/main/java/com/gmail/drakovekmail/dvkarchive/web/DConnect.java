package com.gmail.drakovekmail.dvkarchive.web;

import java.util.concurrent.TimeUnit;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Class containing methods for dealing with web content.
 * 
 * @author Drakovek
 */
public class DConnect {
	
	/**
	 * WebClient for accessing web pages.
	 */
	private WebClient web_client;
	
	/**
	 * Currently loaded web page;
	 */
	private HtmlPage page;
	
	/**
	 * Initializes the DConnect class by opening a WebClient.
	 */
	public DConnect() {
		initialize_client();
	}
	
	/**
	 * Initializes and opens the web_client
	 */
	public void initialize_client() {
		this.web_client = new WebClient(BrowserVersion.BEST_SUPPORTED);
	}
	
	/**
	 * Loads a given URL to a HtmlPage object.
	 * 
	 * @param url Given URL
	 * @param element XPath element to wait for
	 */
	public void load_page(String url, String element) {
		CookieManager cookies = this.web_client.getCookieManager();
		this.web_client.getCurrentWindow().getJobManager().removeAllJobs();
		close_client();
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
	 * Waits for a given element to appear in the loaded HtmlPage.
	 * 
	 * @param element Element to wait for in XPATH format
	 * @return Whether the element appeared. False if timed out.
	 */
	private boolean wait_for_element(String element) {
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
}
