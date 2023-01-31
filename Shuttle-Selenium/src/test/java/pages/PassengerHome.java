package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PassengerHome {
	private WebDriver driver;
	
	@FindBy(how = How.CSS, using = "#passenger-order-departure")
	private WebElement inputDeparture;
	
	@FindBy(how = How.CSS, using = "#passenger-order-destination")
	private WebElement inputDestination;
	
	@FindBy(how = How.CSS, using = "#passenger-order-find-route")
	private WebElement btnFindRoute;
	
	@FindBy(how = How.CSS, using = "#passenger-order-route-spinner")
	private WebElement spinnerLoadingRoute;
	
	public PassengerHome(WebDriver webdriver) {
		this.driver = webdriver;
		//driver.get(Util.ShuttleURL + "passenger/home); // don't do this, let angular take care of it. otherwise it'll do it before we log in and move us to /login again.
        PageFactory.initElements(driver, this);
	}
	
	public void enterDepartureDestination(String departure, String destination) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputDeparture)).clear();
		inputDeparture.sendKeys(departure);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputDestination)).clear();
		inputDestination.sendKeys(destination);
		
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnFindRoute)).click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(spinnerLoadingRoute));
	}
}
