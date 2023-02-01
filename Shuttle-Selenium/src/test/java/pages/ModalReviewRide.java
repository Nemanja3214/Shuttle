package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ModalReviewRide {
	private WebDriver driver;
	
	@FindBy(how = How.CSS, using = "#modal-review-driver-select")
	private WebElement selectDriverRating;
	
	@FindBy(how = How.CSS, using = "#modal-review-driver-comment")
	private WebElement textboxDriverComment;
	
	@FindBy(how = How.CSS, using = "#modal-review-vehicle-select")
	private WebElement selectVehicleRating;
	
	@FindBy(how = How.CSS, using = "#modal-review-vehicle-comment")
	private WebElement textboxVehicleComment;
	
	@FindBy(how = How.CSS, using = "#modal-review-ok")
	private WebElement btnOk;
	
	@FindBy(how = How.CSS, using = "#modal-review-cancel")
	private WebElement btnCancel;
	
	public ModalReviewRide(WebDriver webdriver) {
		this.driver = webdriver;
        PageFactory.initElements(driver, this);
	}
	
	/**
	 * @param rating Possible ratings are: "", "1", "2", "3", "4", "5"
	 */
	public void rateDriver(String rating) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(selectDriverRating)).click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-review-driver-" + rating))).click();
	}
	
	/**
	 * @param rating Possible ratings are: "", "1", "2", "3", "4", "5"
	 */
	public void rateVehicle(String rating) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(selectVehicleRating)).click();
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-review-vehicle-" + rating))).click();
	}
	
	public void commentDriver(String comment) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(textboxDriverComment)).clear();
		textboxDriverComment.sendKeys(comment);
	}
	
	public void commentVehicle(String comment) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(textboxVehicleComment)).clear();
		textboxVehicleComment.sendKeys(comment);
	}
	
	public void clickOk() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnOk)).click();
	}
	
	public void clickCancel() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnCancel)).click();
	}
}
