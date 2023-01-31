package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import util.Util;

public class LoginPage {
	private WebDriver driver;
	
	public LoginPage(WebDriver webdriver) {
		this.driver = webdriver;
		driver.get(Util.ShuttleURL);
        PageFactory.initElements(driver, this);
	}
	
	@FindBy(how = How.CSS, using = "#login")
    private WebElement btnToolbarLogin;
	
	@FindBy(how = How.CSS, using = "#login-email")
	private WebElement inputEmail;
	
	@FindBy(how = How.CSS, using = "#login-password")
	private WebElement inputPassword;
	
	@FindBy(how = How.CSS, using = "#login-error")
	private WebElement divLoginError;
	
	@FindBy(how = How.CSS, using = "#submitButton")
	private WebElement btnSubmitLogin;
	
	public void btnToolbarLogin_click() {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnToolbarLogin)).click();
	}
	
	public void login(String email, String password) {
		enterEmailPasswordButDontClickOnLogin(email, password);
		
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(btnSubmitLogin)).click();
	}
	
	public void enterEmailPasswordButDontClickOnLogin(String email, String password) {
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputEmail)).clear();
		inputEmail.sendKeys(email);
		(new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputPassword)).clear();
		inputPassword.sendKeys(password);
	}
	
	public boolean isLoginButtonEnabled() {
		return (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(btnSubmitLogin)).isEnabled();
	}
	
	public String getLoginErrorText() {
		String txt = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(inputEmail)).getText();
		return txt;
	}
}
