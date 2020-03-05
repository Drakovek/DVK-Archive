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
				System.out.println("Failed loading Selenium driver.");
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
		try {
			this.driver.get(url);
			if(element != null) {
				WebDriverWait wait = new WebDriverWait(this.driver, 10);
				wait.until(ExpectedConditions
						.presenceOfAllElementsLocatedBy(By.xpath(element)));
			}
			this.connect.load_from_string(this.driver.getPageSource());
		}
		catch(Exception e) {
			this.connect.load_from_string(null);
		}
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
	 * Closes the current WebDriver, if available.
	 */
	public void close_driver() {
		try {
			this.driver.close();
		}
		catch(Exception e) {}
	}
}
