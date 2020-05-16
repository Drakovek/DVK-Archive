package com.gmail.drakovekmail.dvkarchive.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Class for getting web info using Selenium WebDriver.
 * 
 * @author Drakovek
 */
public class DConnectSelenium {
	
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
	 */
	public DConnectSelenium(boolean headless) {
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
		initialize_driver("f", headless);
	}
	
	/**
	 * Initializes WebDriver to the given browser type.
	 * Supports Firefox, Chrome, and Edge browsers.
	 * Falls on other browsers if given browser is unavailable.
	 * 
	 * @param driver_type f - Firefox, c - Chrome, e - Edge
	 * @param headless Whether driver should be headless.
	 */
	public void initialize_driver(String driver_type, boolean headless) {
		if(driver_type.equals("f")) {
			//FIREFOX DRIVER
			try {
				FirefoxOptions op = new FirefoxOptions();
				op.setHeadless(headless);
				this.driver = new FirefoxDriver(op);
			}
			catch(Exception e) {
				initialize_driver("c", headless);
			}
		}
		else if(driver_type.equals("c")) {
			//CHROME DRIVER
			try {
				ChromeOptions op = new ChromeOptions();
				op.setHeadless(headless);
				this.driver = new ChromeDriver(op);
			}
			catch(Exception e) {
				initialize_driver("e", headless);
			}
		}
		else if(driver_type.equals("e")) {
			//CHROME DRIVER
			try {
				this.driver = new EdgeDriver();
			}
			catch(Exception e) {
				//TODO Inform user to get selenium driver
				System.out.println("https://selenium-python.readthedocs.io/installation.html#downloading-python-bindings-for-selenium");
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
	private void load_page(String url, String element, int timeout) {
		try {
			this.driver.get(url);
			if(element != null) {
				WebDriverWait wait = new WebDriverWait(this.driver, timeout);
				wait.until(ExpectedConditions
						.presenceOfAllElementsLocatedBy(By.xpath(element)));
			}
			this.connect.load_from_string(this.driver.getPageSource());
		}
		catch(Exception e) {
			this.connect.set_page(null);
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
	
	/**
	 * Closes the current WebDriver, if available.
	 */
	public void close_driver() {
		try {
			this.driver.close();
			this.connect.close_client();
		}
		catch(Exception e) {}
	}
}
