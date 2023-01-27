package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import util.Util;

public class HomePage {
	private WebDriver driver;
	
	public HomePage(WebDriver webdriver) {
		this.driver = webdriver;
		driver.get(Util.ShuttleURL);
        PageFactory.initElements(driver, this);
	}
	
	@FindBy(how = How.CSS, using = "#login")
    private WebElement btnToolbarLogin;
	
	@FindBy(how = How.CSS, using = "[name~='email']")
	private WebElement inputEmail;
	
	@FindBy(how = How.CSS, using = "[name~='password']")
	private WebElement inputPassword;
	
	@FindBy(how = How.CSS, using = "#submitButton")
	private WebElement btnSubmitLogin;
	
	public void BtnToolbarLogin_click() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnToolbarLogin)).click();
	}
	
	public void Login(String email, String password) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputEmail)).sendKeys(email);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputPassword)).sendKeys(password);
		
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnSubmitLogin)).click();
	}
}
