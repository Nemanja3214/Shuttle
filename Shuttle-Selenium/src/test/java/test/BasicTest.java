package test;

import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.WebDriver;
import util.DriverSetup;

@TestInstance(Lifecycle.PER_CLASS)
public class BasicTest {
	private static WebDriver webdriver;
	private static WebDriver webdriver2;
	
	@BeforeAll
	public void init() {
		webdriver = DriverSetup.useFirefox();
		webdriver.manage().window().maximize();
		
		webdriver2 = DriverSetup.useFirefox();
		webdriver2.manage().window().maximize();
	}
	
	@AfterAll
	public void fini() {
		webdriver.close();
		webdriver2.close();
	}

//	
//	@Test
//	public void DEMONSTRATIVE_EXAMPLE_TWO_USER_LOGIN() {
//		LoginPage loginPage1 = new LoginPage(webdriver);	// webdriver  = PASSENGER
//		LoginPage loginPage2 = new LoginPage(webdriver2);	// webdriver2 = DRIVER
//		
//		loginPage2.btnToolbarLogin_click();
//		loginPage2.login("bob@gmail.com", "bob123");
//		
//		loginPage1.btnToolbarLogin_click();
//		loginPage1.login("john@gmail.com", "john123");
//		
//		PassengerHome passengerHome = new PassengerHome(webdriver);	
//		passengerHome.enterDepartureDestination("Novi Sad", "Beograd");
//		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}


//	@Test
//	@DisplayName("Orders a ride that the driver can perform. For himself. Not scheduled. Driver accepts and starts the ride. Passenger panics.")
//	public void t4() {
//		
//	}
//	
//	@Test
//	@DisplayName("Orders a ride that the driver can perform. For himself. Not scheduled. Driver accepts and starts the ride. Driver panics.")
//	public void t5() {
//		
//	}
//	
//	@Test
//	@DisplayName("Orders a ride that the driver cannot perform (wrong vehicle type).")
//	public void t6() {
//		
//	}
//	
//	@Test
//	@DisplayName("Orders a ride that the driver cannot perform (more passengers than what the driver can accept).")
//	public void t7() {
//		
//	}
//	
//	@Test
//	@DisplayName("Orders a ride that the driver can perform, but he's busy with a different ride (other ride state = PENDING).")
//	public void t8() {
//		
//	}
//	
//	@Test
//	@DisplayName("Orders a ride that the driver can perform in the future even though he's busy with a different ride (other ride state = PENDING).")
//	public void t9() {
//		
//	}
//	
//	@Test
//	@DisplayName("Multiple drivers, test that the algorithm picks the best one.")
//	public void t10() {
//		
//	}
//	
//	@Test
//	@DisplayName("One driver available but he is on a break so passenger cannot order.")
//	public void t11() {
//		
//	}
//	

}
