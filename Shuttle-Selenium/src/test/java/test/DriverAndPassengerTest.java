package test;

import static org.assertj.core.api.Assertions.assertThat;

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
	@DisplayName("Happy flow - basic test.")
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
		
	@Test
	@DisplayName("Driver can reject a ride.")
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
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Novi Sad", "Beograd", false, true);
		
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
	
	@Test
	@DisplayName("Passenger can cancel a ride.")
	public void t3() {
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
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Novi Sad", "Beograd", false, true);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRide.getAcceptedText()).isNotBlank();
		
		passengerCurrentRide.cancelRide();
		homePassenger.enterDepartureDestination("I can order", "a new ride now.");
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
	
	@Test
	@DisplayName("Passenger cannot search for a route if either location is blank.")
	public void t4() {
		WebDriver wdDriver = webdriver;
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassenger = new LoginPage(wdPassenger);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		
		homePassenger.enterDepartureDestination("Novi Sad", "");
		assertThat(homePassenger.canClickOnFindRoute() == false);

		homePassenger.enterDepartureDestination("", "Beograd");
		assertThat(homePassenger.canClickOnFindRoute() == false);
		
		homePassenger.enterDepartureDestination("", "");
		assertThat(homePassenger.canClickOnFindRoute() == false);
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
	
	@Test
	@DisplayName("Passenger can panic during the ride which ends the ride")
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
		homePassenger.enterFields("Veternik", "Zrenjanin", "STANDARD", true, true);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", true, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Veternik", "Zrenjanin", true, true);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRide.getAcceptedText()).isNotBlank();
		driverCurrentRide.startRide();
		
		passengerCurrentRide.openPanicDialog();
		ModalPanic passengerPanic = new ModalPanic(wdPassenger);
		passengerPanic.stateReason("     ");
		assertThat(passengerPanic.isOkButtonEnabled() == false);
		passengerPanic.clickCancel();
		
		passengerCurrentRide.openPanicDialog();
		passengerPanic.stateReason("The driver is speeding.");
		passengerPanic.clickOk();
		
		homePassenger.enterDepartureDestination("I can order", "a new ride now.");
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
	
	@Test
	@DisplayName("Driver can panic during the ride which ends the ride")
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
		homePassenger.enterFields("Veternik", "Zrenjanin", "STANDARD", true, true);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", true, true);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Veternik", "Zrenjanin", true, true);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRide.getAcceptedText()).isNotBlank();
		driverCurrentRide.startRide();
		
		driverCurrentRide.openPanicDialog();
		ModalPanic driverPanic = new ModalPanic(wdDriver);
		driverPanic.stateReason("     ");
		assertThat(driverPanic.isOkButtonEnabled() == false);
		driverPanic.stateReason("The passenger has a gun.");
		driverPanic.clickOk();
		
		homePassenger.enterDepartureDestination("I can order", "a new ride now.");
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
	
	@Test
	@DisplayName("Multiple users try to panic, subsequent panics don't cause an error.")
	public void t7() {
		WebDriver wdDriver = webdriver;
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassenger = new LoginPage(wdPassenger);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		homePassenger.enterFields("Sombor", "Pancevo", "STANDARD", false, false);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, false);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Sombor", "Pancevo", false, false);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRide.getAcceptedText()).isNotBlank();
		driverCurrentRide.startRide();
		
		driverCurrentRide.openPanicDialog();
		ModalPanic driverPanic = new ModalPanic(wdDriver);
		
		passengerCurrentRide.openPanicDialog();
		ModalPanic passengerPanic = new ModalPanic(wdPassenger);
		
		driverPanic.stateReason("The passenger has a gun.");
		passengerPanic.stateReason("The driver has a gun.");
		driverPanic.clickOk();
		passengerPanic.clickOk();
		
		homePassenger.enterDepartureDestination("I can order", "a new ride now.");
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
	
	@Test
	@DisplayName("Passenger tries to panic after the ride ends, reviews opens up but the panic stays behind.")
	public void t8() {
		WebDriver wdDriver = webdriver;
		WebDriver wdPassenger = webdriver2;
		
		LoginPage loginDriver = new LoginPage(wdDriver);
		LoginPage loginPassenger = new LoginPage(wdPassenger);

		loginDriver.btnToolbarLogin_click();
		loginDriver.login("bob@gmail.com", "bob123");
		
		loginPassenger.btnToolbarLogin_click();
		loginPassenger.login("john@gmail.com", "john123");
		
		PassengerHomeOrderRide homePassenger = new PassengerHomeOrderRide(wdPassenger);
		homePassenger.enterFields("Sombor", "Pancevo", "STANDARD", false, false);	
		String distance = homePassenger.getDistanceFromOrderPanel();
		homePassenger.orderRide();
		
		PassengerHomeCurrentRide passengerCurrentRide = new PassengerHomeCurrentRide(wdPassenger);
		_verifyCurrentRide(passengerCurrentRide, "john@gmail.com", new ArrayList<String>(), "bob@gmail.com", distance, "STANDARD", false, false);

		DriverHomeCurrentRide driverCurrentRide = new DriverHomeCurrentRide(wdDriver);
		_verifyDriverCurrentRide(driverCurrentRide, Arrays.asList("john@gmail.com"), "Sombor", "Pancevo", false, false);
		
		driverCurrentRide.acceptRide();
		assertThat(passengerCurrentRide.getAcceptedText()).isNotBlank();
		driverCurrentRide.startRide();
		
		passengerCurrentRide.openPanicDialog();
		ModalPanic passengerPanic = new ModalPanic(wdPassenger);
		
		driverCurrentRide.finishRide();

		ModalReviewRide passengerReview = new ModalReviewRide(wdPassenger);
		passengerReview.enterFields("5", "It's ok but I want to panic.", "1", "Please let me panic.");
		passengerReview.clickOk();
		
		passengerPanic.stateReason("I hate this driver.");
		passengerPanic.clickOk();
		
		homePassenger.enterDepartureDestination("I can order", "a new ride now.");
		
		ToolbarCommon toolbarCommonDriver = new ToolbarCommon(wdDriver);
		toolbarCommonDriver.logOut();
		
		ToolbarCommon toolbarCommonPassenger = new ToolbarCommon(wdPassenger);
		toolbarCommonPassenger.logOut();
	}
}
