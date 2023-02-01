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
import pages.ModalRejectRide;
import pages.ModalReviewRide;
import pages.PassengerHomeCurrentRide;
import pages.PassengerHomeOrderRide;
import pages.ToolbarCommon;
import util.DriverSetup;

@TestInstance(Lifecycle.PER_CLASS)
public class DriverAndPassengerTest {
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
		homePassenger.clickOnFindRoute();
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
		
	@Test
	@DisplayName("Orders a ride that the driver can perform. For himself. Not scheduled. Driver rejects, leaving a reason.")
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
		homePassenger.enterDepartureDestination("Novi Sad", "Beograd");
		homePassenger.clickOnFindRoute();
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
		
		driverCurrentRide.openRejectDialog();
		
		ModalRejectRide rejector = new ModalRejectRide(wdDriver);
		assertThat(!rejector.isOkButtonEnabled());
		rejector.stateReason("I don't like this passenger.");
		rejector.clickOk();
		
		homePassenger.enterDepartureDestination("I can order", "a new ride now.");
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
}
