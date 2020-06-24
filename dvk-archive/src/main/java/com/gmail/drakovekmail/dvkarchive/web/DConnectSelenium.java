package com.gmail.drakovekmail.dvkarchive.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gmail.drakovekmail.dvkarchive.file.DvkException;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.compound.DButtonDialog;

/**
 * Class for getting web info using Selenium WebDriver.
 * 
 * @author Drakovek
 */
public class DConnectSelenium implements AutoCloseable {
	
	/**
	 * Main WebDriver for loading online content.
	 */
	private WebDriver driver;
	
	/**
	 * DConnect object for parsing HTML information.
	 */
	private DConnect connect;
	
	/**
	 * Initializes the DConnectSelelium class.
	 * Disables Selenium logs.
	 * 
	 * @param headless Whether drivers should be headless.
	 * @param start_gui GUI to tie message box to if informing user to download Selenium driver
	 * @exception DvkException DvkException
	 */
	public DConnectSelenium(boolean headless, StartGUI start_gui) throws DvkException {
		//DISABLE LOGS
		java.util.logging.Logger.getLogger("org.openqa.selenium")
			.setLevel(java.util.logging.Level.OFF);
		System.setProperty(
				FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,
				"true");
		System.setProperty(
				FirefoxDriver.SystemProperty.BROWSER_LOGFILE,
				"/dev/null");
		//INITIALIZE OBJECT
		this.connect = new DConnect(false, false);
		initialize_driver('f', headless, start_gui);
	}
	
	/**
	 * Initializes WebDriver to the given browser type.
	 * Supports Firefox, Chrome, Safari, and Edge browsers.
	 * Falls on other browsers if given browser is unavailable.
	 * 
	 * @param driver_type f - Firefox, c - Chrome, s - Safari e - Edge
	 * @param headless Whether driver should be headless.
	 * @param start_gui GUI to tie message box to if informing user to download Selenium driver.
	 */
	public void initialize_driver(char driver_type, boolean headless, StartGUI start_gui) {
		if(driver_type == 'f') {
			//FIREFOX DRIVER
			try {
				FirefoxOptions op = new FirefoxOptions();
				op.setHeadless(headless);
				this.driver = new FirefoxDriver(op);
			}
			catch(Exception e) {
				initialize_driver('c', headless, start_gui);
			}
		}
		else if(driver_type == 'c') {
			//CHROME DRIVER
			try {
				ChromeOptions op = new ChromeOptions();
				op.setHeadless(headless);
				this.driver = new ChromeDriver(op);
			}
			catch(Exception e) {
				initialize_driver('s', headless, start_gui);
			}
		}
		else if(driver_type == 's') {
			//SAFARI DRIVER
			try {
				this.driver = new SafariDriver();
			}
			catch(Exception e) {
				initialize_driver('e', headless, start_gui);
			}
		}
		else if(driver_type == 'e') {
			//EDGE DRIVER
			try {
				this.driver = new EdgeDriver();
			}
			catch(Exception e) {
				if(start_gui != null) {
					//INFORM USER TO INSTALL SELENIUM DRIVER
					String[] buttons = {"ok"};
					DButtonDialog dialog = new DButtonDialog();
					dialog.open_html(start_gui.get_base_gui(), start_gui.get_frame(),
							"selenium_title", "selenium_message", buttons);
				}
			}
		}
	}
	
	/**
	 * Loads a given URL to a HtmlPage object.
	 * 
	 * @param url Given URL
	 * @param element XPath element to wait for
	 * @param tries How many times to attempt loading page
	 * @param timeout How long to wait in seconds for element before timing out
	 */
	public void load_page(String url, String element, int tries, int timeout) {
		load_page(url, element, timeout);
		for(int i = 0; this.get_page() == null && i < (tries - 1); i++) {
			load_page(url, element, timeout);
		}
	}
	
	/**
	 * Loads a given URL to a HtmlPage object.
	 * 
	 * @param url Given URL
	 * @param element XPath element to wait for
	 * @param timeout How long to wait in seconds for element before timing out
	 */
	public void load_page(String url, String element, int timeout) {
		try {
			this.driver.get(url);
			boolean loaded = true;
			if(element != null) {
				loaded = wait_for_element(element, timeout);
			}
			if(loaded) {
				this.connect.load_from_string(this.driver.getPageSource());
			}
		}
		catch(Exception e) {
			this.connect.set_page(null);
		}
	}
	
	/**
	 * Waits for a given element to appear on the loaded page
	 * 
	 * @param element XPath element to wait for
	 * @param timeout How long to wait in seconds for element before timing out
	 * @return Whether the function succeeded in finding the given element 
	 */
	public boolean wait_for_element(String element, int timeout) {
		try {
			WebDriverWait wait = new WebDriverWait(this.driver, timeout);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(element)));
			return true;
		}
		catch(Exception e) {
			this.connect.set_page(null);
			return false;
		}
	}
	
	/**
	 * Sets main page of DConnect directly.
	 * 
	 * @param page HtmlPage
	 */
	public void set_page(HtmlPage page) {
		this.connect.set_page(page);
	}
	
	/**
	 * Returns the currently loaded HtmlPage.
	 * 
	 * @return Loaded HtmlPage
	 */
	public HtmlPage get_page() {
		return this.connect.get_page();
	}
	
	/**
	 * Returns the current WebDriver.
	 * 
	 * @return Current WebDriver
	 */
	public WebDriver get_driver() {
		return this.driver;
	}
	
	@Override
	public void close() throws DvkException {
		try {
			this.driver.close();
			this.connect.close();
		}
		catch(Exception e) {
			throw new DvkException();
		}
	}
}
