package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ToolbarCommon {
	@FindBy(how = How.CSS, using = "#sign-out")
	private WebElement btnLogout;
	
	private WebDriver driver;
	
	public ToolbarCommon(WebDriver webdriver) {
		this.driver = webdriver;
        PageFactory.initElements(driver, this);
	}
	
	public void logOut() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnLogout)).click();
	}
}
