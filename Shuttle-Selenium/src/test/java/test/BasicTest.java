package test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.WebDriver;
import pages.DriverHomeCurrentRide;
import pages.LoginPage;
import pages.ModalReviewRide;
import pages.PassengerHomeOrderRide;
import pages.ToolbarCommon;
import pages.PassengerHomeCurrentRide;
import util.DriverSetup;

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
	
	@Test
	@DisplayName("Orders a ride that the driver can perform. For himself. Not scheduled. Driver accepts, starts and finishes. Passenger reviews.")
	public void t1() {
		WebDriver wdDriver = webdriver;
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassenger = new LoginPage(wdPassenger);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		homePassenger.enterDepartureDestination("Novi Sad", "Beograd");
		homePassenger.selectVehicle("STANDARD");
		homePassenger.setBabies(false);
		homePassenger.setPets(true);
		
		final String distance = homePassenger.getDistanceFromOrderPanel();
		final String price = homePassenger.getPriceFromOrderPanel();

		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		
		List<String> otherPassList = passengerCurrentRide.getOtherPassengers();
		assertThat(otherPassList.size() == 0);
		final String passMe = passengerCurrentRide.getMe();
		assertThat(passMe.equals("john@gmail.com"));
		
		String driverInfo = passengerCurrentRide.getDriverInfo();
		assertThat(driverInfo).isEqualTo("bob@gmail.com");
		
		String distanceCurrentRide = passengerCurrentRide.getDistance();
		assertThat(distance).isEqualTo(distanceCurrentRide);
		
		String vehicleTypeCurrentRide = passengerCurrentRide.getVehicleType();
		assertThat(vehicleTypeCurrentRide).isEqualTo("STANDARD");
		
		boolean babiesPCurrentRide = passengerCurrentRide.getBabies();
		boolean petsPCurrentRide = passengerCurrentRide.getPets();
		
		assertThat(babiesPCurrentRide).isFalse();
		assertThat(petsPCurrentRide).isTrue();
		
		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		List<String> passengers = driverCurrentRide.getPassengers();
		
		assertThat(passengers.size()).isEqualTo(1);
		assertThat(passengers.get(0)).isEqualTo("john@gmail.com");
		
		List<String> locations = driverCurrentRide.getLocations();
		System.out.println(locations);
		assertThat(locations.size()).isEqualTo(2);
		assertThat(locations.get(0)).isEqualTo("Novi Sad");
		assertThat(locations.get(1)).isEqualTo("Beograd");
		
		boolean isBabies = driverCurrentRide.getBabiesCheck();
		boolean isPets = driverCurrentRide.getPetsCheck();
		
		assertThat(isBabies).isFalse();
		assertThat(isPets).isTrue();
		
		driverCurrentRide.acceptRide();
		
		String acceptedTextPassengerCurrentRide = passengerCurrentRide.getAcceptedText();
		assertThat(acceptedTextPassengerCurrentRide).isNotBlank();
		
		driverCurrentRide.startRide();
		driverCurrentRide.finishRide();
		
		ModalReviewRide passengerReview = new ModalReviewRide(wdPassenger);
		passengerReview.rateDriver("3");
		passengerReview.commentDriver("He drove me for less than a second. This is unacceptable.");
		passengerReview.rateVehicle("5");
		passengerReview.commentVehicle("It alright. It not subtle or nuanced, but it alright.");
		passengerReview.clickOk();
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
//	
//	@Test
//	@DisplayName("Orders a ride that the driver can perform. For himself. Not scheduled. Driver rejects, leaving a reason.")
//	public void t2() {
//		
//	}
//	
//	@Test
//	@DisplayName("Orders a ride that the driver can perform. For himself. Not scheduled. Driver accepts. Passenger cancels.")
//	public void t3() {
//		
//	}
//	
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
//	@Test
//	@DisplayName("Login test: happy flow.")
//	public void t12() {
//		
//	}
//	
	@Test
	@DisplayName("Login test: wrong email.")
	public void t13() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.login("g@gmail.com", "bob123");
		String err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
	}
	
	@Test
	@DisplayName("Login test: mismatched email and password.")
	public void t14() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.login("bob@gmail.com", "john123");
		String err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
	}
	
	@Test
	@DisplayName("Login test: wrong password.")
	public void t15() {
		LoginPage loginPage = new LoginPage(webdriver);
		loginPage.btnToolbarLogin_click();
		
		loginPage.login("bob@gmail.com", "bob112");
		String err = loginPage.getLoginErrorText();
		assertThat(err.equals("Wrong username or password!"));
	}
	
	@Test
	@DisplayName("Login test: bad field (empty field or email is bad), cannot click on login.")
	public void t16() {
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
