package pages.passenger;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PassengerHomeCurrentRide {
	private WebDriver driver;
	
	@FindBy(how = How.CSS, using = "#passenger-current")
	private WebElement root;
	
	@FindBy(how = How.CSS, using = ".passenger-current-location-list")
	private List<WebElement> locations;
	
	@FindBy(how = How.CSS, using = "#passenger-current-distance")
	private WebElement distance;
	
	@FindBy(how = How.CSS, using = "#passenger-current-vehicle")
	private WebElement vehicle;
	
	@FindBy(how = How.CSS, using = "#passenger-current-babies")
	private WebElement cbBabies;
	
	@FindBy(how = How.CSS, using = "#passenger-current-pets")
	private WebElement cbPets;
	
	@FindBy(how = How.CSS, using = "#passenger-current-me")
	private WebElement me;
	
	@FindBy(how = How.CSS, using = ".passenger-current-other")
	private List<WebElement> otherPassengers;
	
	@FindBy(how = How.CSS, using = "#passenger-current-driver")
	private WebElement driverInfo;
	
	@FindBy(how = How.CSS, using = "#passenger-current-scheduled-for")
	private WebElement scheduledFor;
	
	@FindBy(how = How.CSS, using = "#passenger-current-cancel")
	private WebElement btnCancel;
	
	@FindBy(how = How.CSS, using = "#passenger-current-accepted")
	private WebElement txtAccepted;
	
	@FindBy(how = How.CSS, using = "#passenger-current-report")
	private WebElement btnReport;
	
	@FindBy(how = How.CSS, using = "#passenger-current-panic")
	private WebElement btnPanic;
	
	public PassengerHomeCurrentRide(WebDriver webdriver) {
		this.driver = webdriver;
        PageFactory.initElements(driver, this);
	}
	
	public List<String> getOtherPassengers() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(root));
		return otherPassengers.stream().map(el -> el.getText()).toList();
	}
	
	public String getMe() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(me)).getText();
	}
	
	public List<String> getLocations() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(root));
		return locations.stream().map(el -> el.getText()).toList();
	}
	
	public String getDistance() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(distance)).getText();
	}
	
	public String getVehicleType() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(vehicle)).getText();
	}
	
	public boolean getBabies() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(cbBabies)).findElement(By.cssSelector("input")).isSelected();
	}
	
	public boolean getPets() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(cbPets)).findElement(By.cssSelector("input")).isSelected();
	}
	
	public String getDriverInfo() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(driverInfo)).getText();
	}
	
	public String getScheduledFor() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(scheduledFor)).getText();
	}
	
	public String getAcceptedText() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(txtAccepted)).getText();
	}
	
	public void cancelRide() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnCancel)).click();
	}
	
	public void openPanicDialog() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnPanic)).click();
	}
}
