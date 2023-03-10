package pages.passenger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PassengerHomeOrderRide {
	private WebDriver driver;
	
	@FindBy(how = How.CSS, using = "#passenger-order-departure")
	private WebElement inputDeparture;
	
	@FindBy(how = How.CSS, using = "#passenger-order-destination")
	private WebElement inputDestination;
	
	@FindBy(how = How.CSS, using = "#passenger-order-find-route")
	private WebElement btnFindRoute;
	
	@FindBy(how = How.CSS, using = "#passenger-order-route-spinner")
	private WebElement spinnerLoadingRoute;
	
	@FindBy(how = How.CSS, using = "#passenger-order-distance")
	private WebElement spanDistanceValue;
	
	@FindBy(how = How.CSS, using = "#passenger-order-vehicle-selectbox")
	private WebElement selectVehicleType;
	
//	
	@FindBy(how = How.CSS, using = "#passenger-order-schedule-later")
	private WebElement cbScheduleLater;
	@FindBy(how = How.CSS, using = "#passenger-order-select-hour")
	private WebElement selectHours;
	@FindBy(how = How.CSS, using = "#passenger-order-select-minute")
	private WebElement selectMinutes;
	
	
	@FindBy(how = How.CSS, using = "#passenger-order-babies")
	private WebElement cbBabies;
	
	@FindBy(how = How.CSS, using = "#passenger-order-pets")
	private WebElement cbPets;
	
	@FindBy(how = How.CSS, using = "#passenger-order-price")
	private WebElement spanPrice;
	
	@FindBy(how = How.CSS, using = "#passenger-order-submit")
	private WebElement btnOrder;
	

	@FindBy(how = How.CSS,using = "#mat-input-6")
	private WebElement inputInvite;

	@FindBy(how = How.CSS,using = ".mdc-fab")
	private WebElement btnInvite;

	public PassengerHomeOrderRide(WebDriver webdriver) {
		this.driver = webdriver;
		//driver.get(Util.ShuttleURL + "passenger/home); // don't do this, let angular take care of it. otherwise it'll do it before we log in and move us to /login again.
        PageFactory.initElements(driver, this);
	}
	
	public void enterFields(String dep, String dest, String vehicleType, boolean babies, boolean pets) {
		enterDepartureDestination(dep, dest);
		clickOnFindRoute();
		selectVehicle(vehicleType);
		setBabies(babies);
		setPets(pets);
	}
	
	public void scheduleLater(String hour, String minute) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(selectHours)).click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By.id("passenger-order-select-hour-"+ hour)));
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("passenger-order-select-hour-"+ hour))).click();
		
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(selectMinutes)).click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By.id("passenger-order-select-minute-" + minute)));
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("passenger-order-select-minute-" + minute))).click();
	}
	
	public void setSchedule(boolean checked) {
		boolean curr = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(cbScheduleLater)).findElement(By.cssSelector("input")).isSelected();
		if (curr != checked) {
			cbScheduleLater.click();
		}
	}
	
	public void enterDepartureDestination(String departure, String destination) {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(inputDeparture)).clear();
		inputDeparture.sendKeys(departure);
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(inputDestination)).clear();
		inputDestination.sendKeys(destination);
	}
	
	public void clickOnFindRoute() {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(btnFindRoute)).click();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(spinnerLoadingRoute));
	}
	
	public String getDistanceFromOrderPanel() {
		return (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(spanDistanceValue)).getText();
	}
	
	public void selectVehicle(String type) {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(selectVehicleType)).click();
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOfElementLocated(By.id("passenger-order-vehicle-" + type))).click();
	}
	
	public void setBabies(boolean b) {
		boolean curr = (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(cbBabies)).findElement(By.cssSelector("input")).isSelected();
		if (curr != b) {
			cbBabies.click();
		}
	}
	
	public void setPets(boolean b) {
		boolean curr = (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(cbPets)).findElement(By.cssSelector("input")).isSelected();
		if (curr != b) {
			cbPets.click();
		}
	}
	
	public String getSnackMessage() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[matsnackbarlabel]:first-of-type")));
		return driver.findElement(By.cssSelector("div[matsnackbarlabel]:first-of-type")).getText();
	}
	
	
	public String getPriceFromOrderPanel() {
		return (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(spanPrice)).getText();
	}
	
	public boolean isOrderDisabled() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(btnOrder)).isDisplayed();
	}
	
	public void orderRide() {
		(new WebDriverWait(driver, 20)).until(ExpectedConditions.elementToBeClickable(btnOrder)).click();
	}
	
	public boolean canClickOnFindRoute() {
		return (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(btnFindRoute)).isEnabled();
	}

	public void invitePassenger(String passenger) {
		boolean inviteBtnEnabled = (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(btnInvite)).isEnabled();
		boolean inviteInputEnabled = (new WebDriverWait(driver, 20)).until(ExpectedConditions.visibilityOf(inputInvite)).isEnabled();
		if (inviteInputEnabled&&inviteBtnEnabled){
			inputInvite.sendKeys(passenger);
			btnInvite.click();
		}
	}
}
