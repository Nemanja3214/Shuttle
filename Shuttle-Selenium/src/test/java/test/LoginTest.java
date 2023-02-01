package test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.WebDriver;

import pages.LoginPage;
import util.DriverSetup;

@TestInstance(Lifecycle.PER_CLASS)
public class LoginTest {
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
	
//	@Test
//	@DisplayName("Login test: happy flow.")
//	public void t1() {
//		
//	}
//	
	
	@Test
	@DisplayName("Login test: wrong email.")
	public void t2() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.login("g@gmail.com", "bob123");
		String err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
	}
	
	@Test
	@DisplayName("Login test: mismatched email and password.")
	public void t3() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.login("bob@gmail.com", "john123");
		String err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
	}
	
	@Test
	@DisplayName("Login test: wrong password.")
	public void t4() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.login("bob@gmail.com", "bob112");
		String err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
	}
	
	@Test
	@DisplayName("Login test: bad field (empty field or email is bad) -> cannot click on login.")
	public void t5() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.enterEmailPasswordButDontClickOnLogin("", "bob112");
		assertThat(loginPage.isLoginButtonEnabled() == false);
		
		loginPage.enterEmailPasswordButDontClickOnLogin("bob@gmail.com", "");
		assertThat(loginPage.isLoginButtonEnabled() == false);
		
		loginPage.enterEmailPasswordButDontClickOnLogin("bob_gmail.com", "bob123");
		assertThat(loginPage.isLoginButtonEnabled() == false);
	}
}
