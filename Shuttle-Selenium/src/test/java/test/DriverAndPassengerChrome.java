package test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;

import pages.LoginPage;
import pages.driver.DriverHomeCurrentRide;
import pages.modal.ModalPanic;
import pages.modal.ModalRejectRide;
import pages.modal.ModalReviewRide;
import pages.passenger.PassengerHomeCurrentRide;
import pages.passenger.PassengerHomeOrderRide;
import pages.toolbar.ToolbarCommon;
import util.DriverSetup;

@TestInstance(Lifecycle.PER_CLASS)
public class DriverAndPassengerChrome {
	private static WebDriver webdriver;
	private static WebDriver webdriver2;
	private static WebDriver webdriver3;
	
	@BeforeAll
	public void init() {
		webdriver = DriverSetup.useChromeLinux();
		webdriver.manage().window().maximize();
		
		webdriver2 = DriverSetup.useChromeLinux();
		webdriver2.manage().window().maximize();
		
		webdriver3 = DriverSetup.useChromeLinux();
		webdriver3.manage().window().maximize();
	}
	
	@AfterAll
	public void fini() {
		webdriver.close();
		webdriver2.close();
		webdriver3.close();
	}
	
	
	// This uses assertThat so we can't put it in PassengerHomeCurrentRide because it breaks SRP.
	private void _verifyCurrentRide(PassengerHomeCurrentRide p, String me, List<String> others, String driver, String distance, String vehicle, boolean babies, boolean pets) {
		List<String> otherPassList = p.getOtherPassengers();
		assertThat(otherPassList.size() == others.size());
		assertThat(otherPassList.stream().sorted().toList().equals(others.stream().sorted().toList()));

		String passMe = p.getMe();
		assertThat(passMe.equals(me));
		
		String driverInfo = p.getDriverInfo();
		assertThat(driverInfo).isEqualTo(driver);
		
		String distanceCurrentRide = p.getDistance();
		assertThat(distance).isEqualTo(distanceCurrentRide);
		
		String vehicleTypeCurrentRide = p.getVehicleType();
		assertThat(vehicleTypeCurrentRide).isEqualTo(vehicle);
		
		boolean babiesPCurrentRide = p.getBabies();
		boolean petsPCurrentRide = p.getPets();
		
		assertThat(babiesPCurrentRide == babies);
		assertThat(petsPCurrentRide == pets);
	}
	
	private void _verifyDriverCurrentRide(DriverHomeCurrentRide p, List<String> passengers, String dep, String dest, boolean babies, boolean pets) {
		List<String> plist = p.getPassengers();
		
		assertThat(plist.size()).isEqualTo(passengers.size());
		assertThat(plist.stream().sorted().toList().equals(passengers.stream().sorted().toList()));

		List<String> locations = p.getLocations();
		assertThat(locations.size()).isEqualTo(2);
		assertThat(locations.get(0)).isEqualTo(dep);
		assertThat(locations.get(1)).isEqualTo(dest);
		
		boolean isBabies = p.getBabiesCheck();
		boolean isPets = p.getPetsCheck();
		
		assertThat(isBabies == babies);
		assertThat(isPets == pets);
	}
	
	@Test
	@DisplayName("Happy flow schedule in 4 hours from now")
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
		homePassenger.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		
		homePassenger.setSchedule(true);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime future = now.plusHours(4);
		homePassenger.scheduleLater(Integer.toString(future.getHour()), findFirstBefore(future.getMinute()));
		
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Novi Sad", "Beograd", false, true);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRide.getAcceptedText()).isNotBlank();
		
		driverCurrentRide.startRide();
		driverCurrentRide.finishRide();
		
		ModalReviewRide passengerReview = new ModalReviewRide(wdPassenger);
		passengerReview.enterFields("3", "He drove me for less than a second. This is unacceptable.", "5", "It alright. It not subtle or nuanced, but it alright.");
		passengerReview.clickOk();
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
		
	}
	
	private static String findFirstBefore(int minutes) {
		if(minutes > 45)
			return "45";
		if(minutes > 30)
			return "30";
		if(minutes > 15)
			return "15";
		return "0";
	}
	
	
	@Test
	@DisplayName("Should not let me order because order is in more than 5 hours")
	public void t2() {
		WebDriver wdDriver = webdriver;
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassenger = new LoginPage(wdPassenger);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		homePassenger.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		
		homePassenger.setSchedule(true);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime future = now.plusHours(4);
		homePassenger.scheduleLater(Integer.toString(future.getHour()), findFirstBefore(future.getMinute()));
		
		assertTrue(homePassenger.isOrderDisabled());
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
		
	}
	
	
	@Test
	@DisplayName("Should let passenger order even though the driver is in ride currently")
	public void t3() {
//		order ride and start it
		WebDriver wdDriver = webdriver;
		WebDriver wdPassengerRiding = webdriver2;
		WebDriver wdPassengerToBeInRide = webdriver3;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassengerRiding = new LoginPage(wdPassengerRiding);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassengerRiding.btnToolbarLogin_click();
		loginPassengerRiding.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassengerRiding = new PassengerHomeOrderRide(wdPassengerRiding);
		homePassengerRiding.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distance = homePassengerRiding.getDistanceFromOrderPanel();
		
		homePassengerRiding.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRideRiding = new PassengerHomeCurrentRide(wdPassengerRiding);
		_verifyCurrentRide(passengerCurrentRideRiding, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Novi Sad", "Beograd", false, true);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRideRiding.getAcceptedText()).isNotBlank();
		
		driverCurrentRide.startRide();
		
		
//		other passenger requests ride in future
		LoginPage loginPassengerToBeInRide= new LoginPage(wdPassengerToBeInRide);
		loginPassengerToBeInRide.btnToolbarLogin_click();
		loginPassengerToBeInRide.login("troy@gmail.com", "Troytroy123");
		
		PassengerHomeOrderRide homePassengerToBeInRide = new PassengerHomeOrderRide(wdPassengerToBeInRide);
		homePassengerToBeInRide.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distanceToBe = homePassengerToBeInRide.getDistanceFromOrderPanel();
		
		homePassengerToBeInRide.setSchedule(true);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime future = now.plusHours(4);
		homePassengerToBeInRide.scheduleLater(Integer.toString(future.getHour()), findFirstBefore(future.getMinute()));
		
		homePassengerToBeInRide.orderRide();
		
//		first ride finishes
		driverCurrentRide.finishRide();
		
//		first passenger finishes his role
		ModalReviewRide passengerReview = new ModalReviewRide(wdPassengerRiding);
		passengerReview.enterFields("3", "He drove me for less than a second. This is unacceptable.", "5", "It alright. It not subtle or nuanced, but it alright.");
		passengerReview.clickOk();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassengerRiding);
		toolbarCommonPassenger.logOut();
		
//		driver accepts second ride

		wdDriver.navigate().refresh();
		driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("troy@gmail.com"), "Novi Sad", "Beograd", false, true);
		
		PassengerHomeCurrentRide passengerCurrentRideToBeInRide = new PassengerHomeCurrentRide(wdPassengerToBeInRide);
		_verifyCurrentRide(passengerCurrentRideToBeInRide, "troy@gmail.com", new ArrayList<String>(), "bob@gmail.com", distanceToBe, "STANDARD", false, true);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRideToBeInRide.getAcceptedText()).isNotBlank();
		
		driverCurrentRide.startRide();
		driverCurrentRide.finishRide();
		
		passengerReview = new ModalReviewRide(wdPassengerToBeInRide);
		passengerReview.enterFields("3", "He drove me for less than a second. This is unacceptable.", "5", "It alright. It not subtle or nuanced, but it alright.");
		passengerReview.clickOk();
		
//		both log out
		toolbarCommonPassenger = new ToolbarCommon(wdPassengerToBeInRide);
		toolbarCommonPassenger.logOut();
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();

		
	}
	
	@Test
	@DisplayName("Should not assign driver to one passenger order because the driver has been already assigned")
	public void t4() {
//		first passenger orders ride
		WebDriver wdDriver = webdriver;
		WebDriver wdPassengerRiding = webdriver2;
		WebDriver wdPassengerReject = webdriver3;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassengerRiding = new LoginPage(wdPassengerRiding);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassengerRiding.btnToolbarLogin_click();
		loginPassengerRiding.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassengerRiding = new PassengerHomeOrderRide(wdPassengerRiding);
		homePassengerRiding.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distance = homePassengerRiding.getDistanceFromOrderPanel();
		
		homePassengerRiding.setSchedule(true);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime future = now.plusHours(4);
		homePassengerRiding.scheduleLater(Integer.toString(future.getHour()), findFirstBefore(future.getMinute()));
		
		homePassengerRiding.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRideRiding = new PassengerHomeCurrentRide(wdPassengerRiding);
		_verifyCurrentRide(passengerCurrentRideRiding, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Novi Sad", "Beograd", false, true);
		
		
		
//		second passenger tries to order ride but doesn't get assigned driver
		LoginPage loginPassengerReject = new LoginPage(wdPassengerReject);
		loginPassengerReject.btnToolbarLogin_click();
		loginPassengerReject.login("troy@gmail.com", "Troytroy123");
		
		PassengerHomeOrderRide homePassengerReject = new PassengerHomeOrderRide(wdPassengerReject);
		homePassengerReject.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distanceReject = homePassengerReject.getDistanceFromOrderPanel();
		
		homePassengerReject.setSchedule(true);
		homePassengerReject.scheduleLater(Integer.toString(future.getHour()), findFirstBefore(future.getMinute()));
		
		homePassengerReject.orderRide();
		
//		check if no driver is assigned
		PassengerHomeCurrentRide passengerCurrentRideReject = new PassengerHomeCurrentRide(wdPassengerReject);
		_verifyCurrentRide(passengerCurrentRideReject, "troy@gmail.com", new ArrayList<String>(), "No driver assigned yet!", distance, "STANDARD", false, true);

//		check if nothing changed at driver
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Novi Sad", "Beograd", false, true);
		
//		second passenger cancels and logs out
		passengerCurrentRideReject.cancelRide();
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassengerReject);
		toolbarCommonPassenger.logOut();
		
		
//		driver accepts first ride finishes it and first passenger and driver log out
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRideRiding.getAcceptedText()).isNotBlank();
		
		driverCurrentRide.startRide();
		driverCurrentRide.finishRide();
		
		ModalReviewRide passengerReview = new ModalReviewRide(wdPassengerRiding);
		passengerReview.enterFields("3", "He drove me for less than a second. This is unacceptable.", "5", "It alright. It not subtle or nuanced, but it alright.");
		passengerReview.clickOk();
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		toolbarCommonPassenger = new ToolbarCommon(wdPassengerRiding);
		toolbarCommonPassenger.logOut();
		
	}
	
	@Test
	@DisplayName("Should make driver avaliable after passenger canceled future ride")
	public void t5() {
		WebDriver wdDriver = webdriver;
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassenger = new LoginPage(wdPassenger);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		homePassenger.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		
		homePassenger.setSchedule(true);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime future = now.plusHours(4);
		homePassenger.scheduleLater(Integer.toString(future.getHour()), findFirstBefore(future.getMinute()));
		
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Novi Sad", "Beograd", false, true);
		
		passengerCurrentRide.cancelRide();
		
		DriverHomeCurrentRide driverCurrentRideCanceled = new DriverHomeCurrentRide(wdDriver);
//		check if the current ride is still displayed
		assertFalse(driverCurrentRideCanceled.isShown());
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
		
	}
	
	@Test
	@DisplayName("Should not let passenger order ride in past")
	public void t6() {
		WebDriver wdDriver = webdriver;
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassenger = new LoginPage(wdPassenger);
	
		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		homePassenger.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		
		homePassenger.setSchedule(true);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime future = now.minusHours(4);
		homePassenger.scheduleLater(Integer.toString(future.getHour()), findFirstBefore(future.getMinute()));
		
		assertTrue(homePassenger.isOrderDisabled());
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
		
	}
	
	@Test
	@DisplayName("Should not let passenger order ride because no driver is available")
	public void t7() {
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginPassenger = new LoginPage(wdPassenger);
	
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		homePassenger.enterFields("Novi Sad", "Beograd", "STANDARD", false, true);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		
		homePassenger.orderRide();
		
		String snackMessage = homePassenger.getSnackMessage().strip();
		assertEquals("No driver available!", snackMessage);
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
		
	}
}