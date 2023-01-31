package test;

import static org.assertj.core.api.Assertions.assertThat;

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
import pages.LoginPage;
import pages.PassengerHome;
import util.DriverSetup;
import util.Util;

@TestInstance(Lifecycle.PER_CLASS)
public class BasicTest {
	private static WebDriver webdriver;
	private static WebDriver webdriver2;
	
	@BeforeAll
	public void init() {
		webdriver = DriverSetup.useEdge();
		webdriver.manage().window().maximize();
		
		webdriver2 = DriverSetup.useEdge();
		webdriver2.manage().window().maximize();
	}
	
	@AfterAll
	public void fini() {
		webdriver.close();
		webdriver2.close();
	}

	@Test
	public void loginTestBadCredentials() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.login("bob@gmail.com", "b");
		String err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
		
		loginPage.login("b@gmail.com", "bob112");
		err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWithTwoUsersAndPassengerEntersRideDetails() {
		LoginPage loginPage1 = new LoginPage(webdriver);	// webdriver  = PASSENGER
		LoginPage loginPage2 = new LoginPage(webdriver2);	// webdriver2 = DRIVER
		
		loginPage2.btnToolbarLogin_click();
		loginPage2.login("bob@gmail.com", "bob123");
		
		loginPage1.btnToolbarLogin_click();
		loginPage1.login("john@gmail.com", "john123");
		
		PassengerHome passengerHome = new PassengerHome(webdriver);	
		passengerHome.enterDepartureDestination("Novi Sad", "Beograd");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
