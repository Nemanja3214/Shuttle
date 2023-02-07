package util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverSetup {
	private static final String DRIVER_EDGE = "webdriver.edge.driver";
	private static final String DRIVER_EDGE_LOC = "src/test/resources/driver/msedgedriver.exe";
	private static final String DRIVER_FIREFOX = "webdriver.gecko.driver";
	private static final String DRIVER_FIREFOX_LOC = "src/test/resources/driver/geckodriver";
	
	private static final String DRIVER_CHROME_LINUX = "webdriver.chrome.driver";
	private static final String DRIVER_CHROME_LOC_LINUX = "src/test/resources/driver/chromedriver";
	
	public static WebDriver useEdge() {
		System.setProperty(DRIVER_EDGE, DRIVER_EDGE_LOC);
		return new EdgeDriver();
	}
	
	public static WebDriver useFirefox() {
		System.setProperty(DRIVER_FIREFOX, DRIVER_FIREFOX_LOC);
		return new FirefoxDriver();
	}
	
	public static WebDriver useChromeLinux() {
		System.setProperty(DRIVER_CHROME_LINUX, DRIVER_CHROME_LOC_LINUX);
		return new ChromeDriver();
	}
	
}
