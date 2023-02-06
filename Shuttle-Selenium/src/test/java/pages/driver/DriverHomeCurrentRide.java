package pages.driver;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverHomeCurrentRide {
	private WebDriver driver;
	
	@FindBy(how = How.CSS, using = ".driver-current-passenger-list")
	private List<WebElement> labelPassengers;
	
	@FindBy(how = How.CSS, using = "#driver-current-passenger-list-container")
	private WebElement spanPassengersContainer;
	
	@FindBy(how = How.CSS, using = ".driver-current-location-list")
	private List<WebElement> locationsList;
	
	@FindBy(how = How.CSS, using = "#driver-current-babies")
	private WebElement cbBabies;
	
	@FindBy(how = How.CSS, using = "#driver-current-pets")
	private WebElement cbPets;
	
	@FindBy(how = How.CSS, using = "#driver-current-future-schedule")
	private WebElement spanScheduledFor;
	
	@FindBy(how = How.CSS, using = "#driver-current-accept")
	private WebElement btnAccept; 
	
	@FindBy(how = How.CSS, using = "#driver-current-reject")
	private WebElement btnReject; 
	
	@FindBy(how = How.CSS, using = "#driver-current-start")
	private WebElement btnStart; 
	
	@FindBy(how = How.CSS, using = "#driver-current-finish")
	private WebElement btnFinish; 
	
	@FindBy(how = How.CSS, using = "#driver-current-panic")
	private WebElement btnPanic; 
	
	
	public DriverHomeCurrentRide(WebDriver webdriver) {
		this.driver = webdriver;
        PageFactory.initElements(driver, this);
	}
	
	public List<String> getPassengers() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(spanPassengersContainer));
		return labelPassengers.stream().map(el -> el.getText()).toList();
	}
	
	public List<String> getLocations() {
		return locationsList.stream().map(el -> el.getText()).toList();
	}
	
	public boolean getBabiesCheck() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(cbBabies)).findElement(By.cssSelector("input")).isSelected();
	}
	
	public boolean getPetsCheck() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(cbPets)).findElement(By.cssSelector("input")).isSelected();
	}
	
	public void acceptRide() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnReject));
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnAccept)).click();
	}
	
	public void openRejectDialog() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnAccept));
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnReject)).click();
	}
	
	public void startRide() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnStart)).click();
	}
	
	public void openPanicDialog() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnFinish));
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnPanic)).click();
	}
	
	public void finishRide() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnPanic));
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnFinish)).click();
	}
	
	public boolean isShown() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(By.id("driver-current-passenger-list-container")));
		return driver.findElements(By.id("driver-current-passenger-list-container")).size() != 0;
	}
}
