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
	
	@FindBy(how = How.CSS, using = "#passenger-order-babies")
	private WebElement cbBabies;
	
	@FindBy(how = How.CSS, using = "#passenger-order-pets")
	private WebElement cbPets;
	
	@FindBy(how = How.CSS, using = "#passenger-order-price")
	private WebElement spanPrice;
	
	@FindBy(how = How.CSS, using = "#passenger-order-submit")
	private WebElement btnOrder;

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
	
	public void enterDepartureDestination(String departure, String destination) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputDeparture)).clear();
		inputDeparture.sendKeys(departure);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputDestination)).clear();
		inputDestination.sendKeys(destination);
	}
	
	public void clickOnFindRoute() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnFindRoute)).click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(spinnerLoadingRoute));
	}
	
	public String getDistanceFromOrderPanel() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(spanDistanceValue)).getText();
	}
	
	public void selectVehicle(String type) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(selectVehicleType)).click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("passenger-order-vehicle-" + type))).click();
	}
	
	public void setBabies(boolean b) {
		boolean curr = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(cbBabies)).findElement(By.cssSelector("input")).isSelected();
		if (curr != b) {
			cbBabies.click();
		}
	}
	
	public void setPets(boolean b) {
		boolean curr = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(cbPets)).findElement(By.cssSelector("input")).isSelected();
		if (curr != b) {
			cbPets.click();
		}
	}
	
	public String getPriceFromOrderPanel() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(spanPrice)).getText();
	}
	
	public void orderRide() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnOrder)).click();
	}
	
	public boolean canClickOnFindRoute() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(btnFindRoute)).isEnabled();
	}
}
