package com.example.tests;

import java.io.File;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class KosmosDownload {
	WebDriver driverIE = null;
	WebDriver driverChrome = null;
	
	private String baseUrl;
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();

	@Before
	public void setUp() throws Exception {
  	}

	private WebDriver setupFireFoxDriver() {
		WebDriver driver;
		FirefoxProfile profile = new FirefoxProfile();
		// profile.addExtension(....);
/*
		profile.setPreference("browser.download.pluginOverrideTypes", false);
		profile.setPreference("prompts.tab_modal.enabled", false);
		profile.setPreference("security.csp.enable", false);
		profile.setPreference("security.fileuri.origin_policy", 3 );
		profile.setPreference("security.fileuri.strict_origin_policy", false);
		profile.setPreference("app.update.auto", false);
		profile.setPreference("app.update.enabled", false);
		profile.setPreference("browser.EULA.3.accepted", true);
		profile.setPreference("browser.EULA.override", true);
		profile.setPreference("browser.download.importedFromSqlite", true);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.download.panel.shown", false);
		profile.setPreference("browser.safebrowsing.enabled", false);
		profile.setPreference("browser.safebrowsing.malware.enabled", false);
		profile.setPreference("browser.tabs.warnOnClose", false);
		profile.setPreference("browser.tabs.warnOnOpen", false);
		profile.setPreference("devtools.errorconsole.enabled", true);
		profile.setPreference("dom.disable_open_during_load", false);
		profile.setPreference("network.cookie.prefsMigrated", true);
		profile.setPreference("network.manage-offline-status", false);
		profile.setPreference("offline-apps.allow_by_default", true);
		profile.setPreference("plugin.importedState", true);
		profile.setPreference("privacy.sanitize.migrateFx3Prefs", true);
		profile.setPreference("prompts.tab_modal.enabled", false);
		profile.setPreference("browser.download.folderList",2); 
		profile.setPreference("browser.download.dir","w:/Downloads");
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/octet-stream");
		profile.updateUserPrefs(new File("C:/Users/u36342/AppData/Local/JBG/Internet Access/Workspace/Firefox30/defaults/pref/prefs.js"));
*/
		profile.setPreference("browser.download.folderList",2);
		profile.setPreference("browser.download.manager.showWhenStarting",false);
		profile.setPreference("browser.download.dir","w:/download");
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk","text/csv");
		
		// profile.setPreference("browser.download.manager.showWhenStarting",
		// true);

		driver = new FirefoxDriver(profile);
		// baseUrl = "https://download.kosmos-banking.com/pydio";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		return driver;

	}

	@Test
	public void testKosmosDownloadFireFox() throws Exception {
		WebDriver driver = setupFireFoxDriver();
		driver.get("https://download.kosmos-banking.com");
		assertEquals("Kosmos Banking - /", driver.getTitle());
		// driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		// waitForLoad(driver);
		driver.findElement(By.name("userid")).clear();
		driver.findElement(By.name("userid")).sendKeys("bjb");
		driver.findElement(By.name("ok")).click();
		driver.findElement(By.name("userid")).clear();
		driver.findElement(By.name("userid")).sendKeys("bjb");
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys("^My713c@s4JN");
		driver.findElement(By.name("ok")).click();
		// driver.findElement(By.id("webfx-tree-object-48-label")).click();
		// driver.findElement(By.xpath("//div[@id='item-37kosmos-373-snapshottargz-cont']/div[3]/span[4]/span")).click();
		// *[@id=\"Navigation\"]/descendant::span[text()='Matrices']
		WebElement elem = null;
		// elem =
		// driver.findElement(By.xpath("//*[@id=\"topPane\"]/descendant::span[text()='3.6.9']"));
		// elem =
		// driver.findElement(By.xpath("//*[@id=\"topPane\"]/descendant::span[text()='3.6.1']"));
		// elem =
		// driver.findElement(By.xpath("//*[@id=\"topPane\"]/descendant::span[text()='3.6.6']"));
		elem = driver.findElement(By
				.xpath("//*[@id=\"topPane\"]/descendant::span[text()='3.7']"));
		elem.click();
		driver.findElement(
				By.xpath("//div[@title='kosmos-3.7.3.1-SNAPSHOT.tar.gz']"))
				.click();
		driver.findElement(By.id("download_button_label")).click();

	}

	@Test
	public void testKosmosDownloadChrome() throws Exception {
		Thread a = new Thread(() -> {
			driverChrome = startChromeDriverSession();
		});
		try {
			a.start();
			a.join();
			driverChrome.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			driverChrome.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			// Thread.currentThread().sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		driverChrome.get("https://download.kosmos-banking.com");
		driverChrome.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		driverChrome.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// waitForLoad(driver);
		// driverChrome.findElement(By.name("userid")).clear();
		// driverChrome.findElement(By.name("userid")).sendKeys("bjb");
		// driverChrome.findElement(By.name("ok")).click();
		// driverChrome.switchTo().
		/*
		driverChrome.findElement(By.name("userid")).clear();
		driverChrome.findElement(By.name("userid")).sendKeys("bjb");
		driverChrome.findElement(By.name("ok")).click();
		driverChrome.findElement(By.name("userid")).click();
		driverChrome.findElement(By.name("userid")).clear();
		*/
		// driverChrome.findElement(By.name("userid")).sendKeys("bjb");
		//driverChrome.switchTo().defaultContent();
/*		((JavascriptExecutor) driverChrome)
				.executeScript("document.getElementsByTagName('userid').setAttribute('value', 'bjb')");*/
		JavascriptExecutor js = (JavascriptExecutor) driverChrome;
		/*
		js.executeScript(
		        "var inputs = document.getElementsByTagName('input');" +
		        "for(var i = 0; i < inputs.length; i++) { " +
		        "    inputs[i].type = 'radio';" +
		        "}" );*/		
		js.executeScript(
		        " var inputs = document.getElementsByTagName('input');" +
				  "for (var p in inputs) {" + 
	        		"if (inputs[p].type == 'password') { " + 
	        		"if (inputs[p].name == 'password') {  inputs[p].value = '^My713c@s4JN'; } }" + 
	        		"if (inputs[p].type == 'text') { " + 
	        		"if (inputs[p].name == 'userid') {  inputs[p].value = 'bjb'; }" + 
	        		"console.log('name=' + inputs[p].name);" + 
				  	"for (var o in p) { console.log(o, p[o]); } " + 
					  "}}"			
/*
		        " var inputs = document.getElementsByTagName('input');" +
				  "for (var p in inputs) {" + 
				    "console.log(p, inputs[p]);" +
				    "console.log('p.name=' + p.id);" +
				  " if (p.name == 'userid') {  p.value = 'bjb'; } else { console.log('input field userid not found'); }" +
					  "}"
					  */			
		        );
		/*
		        "//var inputs = document.getElementsByTagName('input');" +
		        "//for(var i = 0; i < inputs.length; i++) { " +
		        " // console.log(inputs[i].name); }" +
		        " // if (inputs[i].name = 'userid') { inputs[i].value = 'bjb'; }" +
		        "  // if (inputs[i].name = 'password') { inputs[i].value = '^My713c@s4JN'; }" +
		        "//}" 
		 */
		// sendKeysJS(driverChrome, driverChrome.findElement(By.name("userid")),
		// "bjb");
		/*
		driverChrome.findElement(By.name("password")).click();
		driverChrome.findElement(By.name("password")).clear();
		driverChrome.findElement(By.name("password")).sendKeys("^My713c@s4JN");
		*/
		// sendKeysJS(driverChrome,
		// driverChrome.findElement(By.name("password")), "^My713c@s4JN");
		WebElement element = driverChrome.findElement(By.id("login_form"));
		element.submit();
		
		WebElement elem = driverChrome.findElement(By
				.xpath("//*[@id=\"topPane\"]/descendant::span[text()='3.7']"));
		elem.click();
		driverChrome.findElement(
				By.xpath("//div[@title='kosmos-3.7.3.1-SNAPSHOT.tar.gz']"))
				.click();
		driverChrome.findElement(By.id("download_button_label")).click();
		
		// driverChrome.findElement(By.name("ok")).click();
		// driverChrome.findElement(By.name("ok")).submit();
		// driver.findElement(By.id("webfx-tree-object-48-label")).click();
		// driver.findElement(By.xpath("//div[@id='item-37kosmos-373-snapshottargz-cont']/div[3]/span[4]/span")).click();
		/*driverChrome.findElement(
				By.xpath("//div[@title='kosmos-3.7.3.1-SNAPSHOT.tar.gz']"))
				.click();
		driverChrome.findElement(By.id("download_button_label")).click();
		*/
	}

	private void sendKeysJS(WebDriver driver, WebElement elem, String value) {
		WebElement element = elem;
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", element);
		executor.executeScript("document.getElementById('"
				+ elem.getAttribute("name") + "').value='" + value + "'");
	}

	@Test
	public void testKosmosDownloadWithIE() {

		Thread a = new Thread(() -> {
			driverIE = testStartIEDriverSession();
		});
		try {
			a.start();
			a.join();
			driverIE.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			driverIE.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			// Thread.currentThread().sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// waitForLoad(driver);
		// driverIE.findElement(By.name("userid")).clear();
		// driverIE.findElement(By.name("userid")).sendKeys("bjb");
		// driverIE.findElement(By.name("ok")).click();
		// assertEquals("Kosmos Banking - /", driverIE.getTitle());

		driverIE.findElement(By.name("userid")).clear();
		driverIE.findElement(By.name("userid")).sendKeys("bjb");
		driverIE.findElement(By.name("password")).clear();
		driverIE.findElement(By.name("password")).sendKeys("^My713c@s4JN");
		driverIE.findElement(By.name("ok")).click();
		// driver.findElement(By.id("webfx-tree-object-48-label")).click();
		// driver.findElement(By.xpath("//div[@id='item-37kosmos-373-snapshottargz-cont']/div[3]/span[4]/span")).click();
		// driverIE.get("https://download.kosmos-banking.com/pydio/");
		System.out.println(driverIE.getPageSource());
		driverIE.navigate().to("https://download.kosmos-banking.com/pydio/");
		driverIE.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driverIE.findElement(By.xpath("//*[contains(@span,'3.6.11')]")).click();
		// WebDriverWait wait = new WebDriverWait(driver, 30L);
		// WebDriverWait wait = (WebDriverWait) new FluentWait<WebDriver>(
		// driverIE )
		// .withTimeout(30, TimeUnit.SECONDS)
		// .pollingEvery(5, TimeUnit.SECONDS)
		// .ignoring( NoSuchElementException.class );
		// wait.
		System.out.println(driverIE.getPageSource());

		WebElement element = driverIE.findElement(By
				.xpath("//div[@title='kosmos-3.6.11p1.tar.gz']"));
		((JavascriptExecutor) driverIE).executeScript(
				"return arguments[0].click();", element);
		/*
		 * driverIE.findElement(By.xpath(
		 * "//div[@title='kosmos-3.7.3.1-SNAPSHOT.tar.gz']")).click();
		 */
		// driverIE.findElement(By.id("download_button_label")).click();
		element = driverIE.findElement(By.id("download_button_label"));
		((JavascriptExecutor) driverIE).executeScript(
				"return arguments[0].click();", element);
	}

	public WebDriver testStartIEDriverSession() {
		WebDriver driverIE;
		File file = new File(
				"Y:/Workspace/IEDriverServer_Win32_2.42.0/IEDriverServer.exe");
		System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
		// IE CODE
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability(
				InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
				true);
		cap.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,
				"https://download.kosmos-banking.com");
		cap.internetExplorer().setCapability("ignoreProtectedModeSettings",
				true);

		// System.setProperty("webdriver.ie.driver",
		// System.getProperty("user.dir")+"//exe//IEDriverServer1.exe");
		// cap.setCapability("IE.binary",
		// "C:/Program Files (x86)/Internet Explorer/iexplore.exe");
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setJavascriptEnabled(true);
		// cap.setCapability("requireWindowFocus", true);
		// cap.setCapability("enablePersistentHover", false);
		driverIE = new InternetExplorerDriver(cap);
		return driverIE;
		// driverIE.get("https://download.kosmos-banking.com");
		// assertEquals("Kosmos Banking - /", driverIE.getTitle());
	}

	public WebDriver startChromeDriverSession() {
		WebDriver driverChrome;
		String driverExe = System.getProperty("webdriver.chrome.driver");
		File file = new File(driverExe);
		DesiredCapabilities cap = new DesiredCapabilities();
		// IE CODE
		// cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
		// true);
		// cap.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,
		// "https://download.kosmos-banking.com");
		// cap.internetExplorer().setCapability("ignoreProtectedModeSettings",
		// true);
		//
		// // System.setProperty("webdriver.ie.driver",
		// System.getProperty("user.dir")+"//exe//IEDriverServer1.exe");
		// // cap.setCapability("IE.binary",
		// "C:/Program Files (x86)/Internet Explorer/iexplore.exe");
		// cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		// cap.setJavascriptEnabled(true);
		// cap.setCapability("requireWindowFocus", true);
		// cap.setCapability("enablePersistentHover", false);
		driverChrome = new ChromeDriver();
		return driverChrome;
		// driverIE.get("https://download.kosmos-banking.com");
		// assertEquals("Kosmos Banking - /", driverIE.getTitle());
	}

	void waitForLoad(WebDriver driver) {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(pageLoadCondition);
	}

	@After
	public void tearDown() throws Exception {
		// driver.quit();
		// String verificationErrorString = verificationErrors.toString();
		// if (!"".equals(verificationErrorString)) {
		// fail(verificationErrorString);
		// }
	}

	private boolean isElementPresent(By by, WebDriver driver) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isAlertPresent(WebDriver driver) {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	private String closeAlertAndGetItsText(WebDriver driver) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		} finally {
			acceptNextAlert = true;
		}
	}
}
