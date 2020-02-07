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
import java.util.concurrent.TimeUnit;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
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
}