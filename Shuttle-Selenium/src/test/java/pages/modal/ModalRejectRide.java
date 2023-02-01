package pages.modal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ModalRejectRide {
	@FindBy(how = How.CSS, using = "#modal-reject-reason")
	private WebElement textboxReason;
	
	@FindBy(how = How.CSS, using = "#modal-reject-ok")
	private WebElement btnOk;
	
	@FindBy(how = How.CSS, using = "#modal-reject-cancel")
	private WebElement btnCancel;
	
	private WebDriver driver;
	public ModalRejectRide(WebDriver webdriver) {
		this.driver = webdriver;
        PageFactory.initElements(driver, this);
	}
	
	public void stateReason(String reason) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(textboxReason)).clear();
		textboxReason.sendKeys(reason);
	}
	
	public void clickOk() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnOk)).click();
	}
	
	public void clickCancel() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnCancel)).click();
	}
	
	public boolean isOkButtonEnabled() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(btnOk)).isSelected();
	}
}
