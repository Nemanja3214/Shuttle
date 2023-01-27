package test;

import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

import pages.HomePage;
import util.DriverSetup;
import util.Util;

@TestInstance(Lifecycle.PER_CLASS)
public class BasicTest {
	private static WebDriver webdriver;
	
	@BeforeAll
	public void init() {
		webdriver = DriverSetup.useEdge();
		webdriver.manage().window().maximize();
	}
	
	@AfterAll
	public void fini() {
		webdriver.close();
	}
	
	@Test
	public void basicTest() {
		HomePage homePage = new HomePage(webdriver);
		homePage.BtnToolbarLogin_click();
		homePage.Login("bob@gmail.com", "bob123");
		
		Util.takeScreenshoot(webdriver, "login_form_bob_entered");
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
