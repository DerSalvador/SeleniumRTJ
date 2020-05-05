package com.juliusbaer.selenium.browser.automation;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import com.sun.jna.platform.win32.WinDef;
import com.sun.org.apache.xerces.internal.util.URI;
import org.apache.commons.codec.Encoder;
import org.apache.commons.lang3.CharSet;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSelenium {
	private static final Path TESTRESULTS = Paths.get(System.getProperty("user.home") + "/TestResults");
	public static final String NICKEXCEP = TESTRESULTS + "/nick.exceptions";
	static WebDriver driver = null;
	static Logger logger = LoggingConfig.getLoggingConfig("KosmosDownload.log", "KosmosDownload");
	public static int alreadySent = 0;
	public static int curProfNr = 0;
	public static int newsent = 0;
	public static ArrayList<String> newList = new ArrayList<String>();
	public static int ignoringme = 0;
	public static ArrayList<String> ignoredList = new ArrayList<String>();
	public class Params {
		@Parameter(names = "-strategy", description = "Strategy", required = true)
		public String strategy;
		@Parameter(names = "-LoginPage", variableArity = true, description = "Login Page", required = true)
		public String LoginPage;
		@Parameter(names = "-subject", variableArity = true, description = "subject", required = true)
		public String subject;
		@Parameter(names = "-body", variableArity = true, description = "body", required = true)
		public String body;
		@Parameter(names = "-zweck", variableArity = true, description = "zweck", required = true)
		public String zweck;
		@Parameter(names = "-region", variableArity = true, description = "region", required = false)
		public String region;
		@Parameter(names = "-country", variableArity = true, description = "country", required = false)
		public String country;
		@Parameter(names = "-username", variableArity = true, description = "username", required = true)
		public String username;
		@Parameter(names = "-password", variableArity = true, description = "password", required = true)
		public String password;
	}

	public static void main(String[] args) {
		Params params = new TestSelenium().new Params();
		JCommander jc = new JCommander(params);
		try {
			jc.setProgramName("Selenium Tests");
			jc.parse(args);
			runTests(params.LoginPage, params.strategy, params.subject, params.body, params.zweck, Optional.ofNullable( params.region), Optional.ofNullable(params.country), params.username, params.password);
			
		} catch (Exception e) {
			e.printStackTrace();
			jc.usage();
		}
		finally {
			logger.info("Finished bot for " + params.country + " and subject " + params.subject);
		}
	}

	public static void runTests(String LoginPage, String strategy, String subject, String body, String zweck, Optional<String> region, Optional<String> country, String username, String password) throws Exception {
		startDriver(LoginPage);
		loginToPageAndStartRobot(driver, LoginPage, strategy, subject, body, zweck, region, country, username, password );
	}

	private static void startDriver(String LoginPage) {
		Thread a = new Thread(() -> {
			String driverExe = System.getProperty("webdriver.chrome.driver");
			if (driverExe != null && driverExe.length() > 0)
				driver = startChromeDriverSession();
			driverExe = System.getProperty("webdriver.ie.driver");
			if (driverExe != null && driverExe.length() > 0)
				driver = startIEDriverSession(LoginPage);
			if (driver == null)
				logger.log(Level.SEVERE,"No driver exe found for " + driverExe);
			
		});
		try {
			a.start();
			a.join();
			setDriverTimeouts(driver, 60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		driver.get(LoginPage);
		setDriverTimeouts(driver, 10);
	}

	private static void loginToPageAndStartRobot(WebDriver driver, String LoginPage,  String strategy, String subject, String body, String zweck, Optional<String> region, Optional<String> country, String username, String password) throws IOException, URISyntaxException {
		WebElement pwdEl = driver.findElement(By.name("pass_lg"));
		pwdEl.sendKeys(password);
		WebElement userEl = driver.findElement(By.name("login_lg"));
		userEl.sendKeys(username);
		WebElement form = driver.findElement(By.name("login_form"));
		form.submit();
		driver.navigate().to(LoginPage + "/de/quick_search.php");
		WebElement elem = driver.findElement(By.name("relation"));
		elem.sendKeys(zweck);
		WebElement countryElem = driver.findElement(By.name("country"));
		countryElem.sendKeys(country.orElse("Alle"));
		WebElement regionELem = driver.findElement(By.name("region"));
		regionELem.sendKeys(region.orElse("Bitte wählen"));

		WebElement searchform = driver.findElement(By.name("search_form"));
		WebElement checkWithFoto = driver.findElement(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody/tr[2]/td/form/div[3]/div/table/tbody/tr[5]/td[2]/input[1]"));
		checkWithFoto.click();
		searchform.submit();
		// /de/viewprofile.php?id=176760&page=1&search_type=q
		// logger.info(driver.getPageSource());
		// List<WebElement> profs = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody/tr[3]/td/div[4]/table/tbody/tr/td[2]/a"));
		List<WebElement> pages = new ArrayList<WebElement>();
		List<String> pagesHrefs = new ArrayList<String>();
		String xpath = "//a[text() = '500']";
		WebElement fivehundred = driver.findElement(By.xpath(xpath));
		fivehundred.click();
		if (driver.getPageSource().indexOf("...") > 0) {
			List<WebElement> pagesGruppe = driver.findElements(By.xpath("//a[@class='page_link' and text() = '...']"));
			while (pagesGruppe.size() > 0) {
				addToProfilePagesList(driver, pages, pagesHrefs);
				pagesGruppe.get(0).click();
				pagesGruppe = driver.findElements(By.xpath("//a[@class='page_link' and text() = '...']"));
				if (pagesGruppe.size() > 0)
					pagesGruppe.remove(0);
			}
		}
		else
		{
			addToProfilePagesListbyImg(driver, pages, pagesHrefs);
			String [] s = new String [1];
			processProfLink(driver, subject, body, (ArrayList)pagesHrefs );
			return;

		}
		// addToProfilePagesList(driver, pages, pagesHrefs);
		if ( !strategy.isEmpty() && strategy.equals("reverse")) {
			WebElement pagesArray[] = new WebElement[1];
			pagesArray = pages.toArray(pagesArray);
			logger.info("Found " + Integer.toString(pagesArray.length) + " pages");
			for (int i = pagesArray.length - 1; i >= 0; i--)
			{
				logger.info("Processing page number " + Integer.toString(i));
				traverseProfiles(driver, LoginPage, pagesHrefs.get(i), subject, body);

			}
		}
		else
			for (WebElement page : pages) {
				traverseProfiles(driver, LoginPage, page.getAttribute("href"), subject, body);
			}
		logger.info("Finished traversing all pages " + Integer.toString(pages.size()));
		logger.info("Count Already sent = " + Integer.toString(alreadySent));
		logger.info("Count Ignoring Me = " + Integer.toString(ignoringme));
		logger.info("Ignoring Me = " + ignoredList.toString());
		logger.info("Count Newly sent = " + Integer.toString(newsent));
		logger.info("New ones = " + newList.toString());
	}

	private static void addToProfilePagesList(WebDriver driver, List<WebElement> pages, List<String> pagesHrefs) {
		for ( String cssClass : new String[] { "page_link", "page_link_active" }) {
			List<WebElement> pageLinks = driver.findElements(By.xpath("//a[@class='" + cssClass + "']"));
			for (WebElement p : pageLinks) {
				if ( !contains(pagesHrefs,p.getAttribute("href")) && !p.getText().equals("...") ) {
					pages.add(p);
					pagesHrefs.add(p.getAttribute("href"));
				}
			}
		}
	}

	private static void addToProfilePagesListbyImg(WebDriver driver, List<WebElement> pages, List<String> pagesHrefs) {
		for ( String cssClass : new String[] { "my_img"}) {
			List<WebElement> pageLinks = driver.findElements(By.xpath("//img[@class='" + cssClass + "']"));
			for (WebElement p : pageLinks) {
					WebElement parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
							"return arguments[0].parentNode;", p);
					pagesHrefs.add(parent.getAttribute("href"));
			}
		}
	}

	private static boolean contains(List<String> list, String elem)
	{
		for(String e : list)
		{
			if (elem.equals(e))
			{
				logger.warning("Already in page list " + e);
				return true;
			}
		}
		return false;
	}

	private static String getValueFromURLQueryString(String key, String url) throws URI.MalformedURIException, URISyntaxException {
		List<NameValuePair> params = URLEncodedUtils.parse(new java.net.URI(url), "UTF-8");

		for (NameValuePair param : params) {
			logger.info(param.getName() + " : " + param.getValue());
			if ( param.getName().equals(key))
				return param.getValue();
		}
		throw new NotFoundException("Key was not found in URL [" + key + "] in " + url);
	}

	public static List<WebElement> FindElement(WebDriver driver, By by, int timeoutInSeconds)
	{
		try {
			setDriverTimeouts(driver, timeoutInSeconds);
			WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
			// wait.ignoring(NoSuchElementException.class);
			wait.until( ExpectedConditions.presenceOfElementLocated(by) ); //throws a timeout exception if element not present after waiting <timeoutInSeconds> seconds
			List<WebElement> elements = driver.findElements(by);
			return elements;
		}
		catch(Exception e)
		{
			logger.warning(e.toString());
		}
		finally {
			setDriverTimeouts(driver, 3);
		}
		return new ArrayList<WebElement>();
	}

	private static boolean checkIfemailAlreadyResponded(String nickname, String profid ) throws UnsupportedEncodingException {
		// /html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/div[15]/table/tbody/tr/td[1]/b
		String checkXPath = "/html/body/table/tbody/tr/td/table[2]/tbody/tr//b[contains(text(),'" + nickname + ":')]";
		//                   /html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/div[7]/table/tbody/tr/td[1]/b
		// http://www.reif-trifft-jung.de/de/mailbox.php?sel=history&id=157556
		String url = "http://www.reif-trifft-jung.de/de/mailbox.php?sel=history&id=" + profid;
		// http://www.reif-trifft-jung.de/de/mailbox.php?sel=viewto&id=16713916
		// String encode = URLEncoder.encode(url, "UTF-8");
		String encode = url;
		driver.navigate().to(encode);
		List<WebElement> we = FindElement(driver,By.xpath(checkXPath), 3);
		if (we.size() > 0)
		{
			return true;
		}
		return false;
	}

	private static void traverseProfiles(WebDriver driver, String LoginPage, String pageUrl, String subjectTxt, String bodyTxt) throws IOException, URISyntaxException {
		//String pageUrl = page.getAttribute("href");
		driver.navigate().to(pageUrl);
		List<WebElement> profs = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/*/tbody/tr/*/table/tbody/*/td/*/table/tbody/tr/*/a"));
		logger.info("Found following profiles " + profs);
		ArrayList<String> profLinks = new ArrayList<String>();
		for (WebElement prof : profs) {
			// String id = HttpUtility.ParseQueryString(prof.getAttribute("href")).Query.Get("id");
			if (!profLinks.contains(prof.getAttribute("href")))
            	profLinks.add(prof.getAttribute("href"));
        }
		processProfLink(driver, subjectTxt, bodyTxt, profLinks);
	}

	private static void processProfLink(WebDriver driver, String subjectTxt, String bodyTxt, ArrayList<String> profLinks) throws URISyntaxException, IOException {
		for (String prof : profLinks) {
			curProfNr++;
			logger.info("Processing profile number " + Integer.toString(curProfNr));
			//String url = prof.getAttribute("href");
			// driver.navigate().to(LoginPage + prof);
			if (prof.indexOf("viewprofile") <=0 ) continue;
			driver.navigate().to(prof);
			String profNickNameXPath = "/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody//div[@class='my_login']";
			WebElement profNickElem = driver.findElement(By.xpath(profNickNameXPath));
			String nicknameProf = profNickElem.getText();
            //prof.click();
			logger.info("Processing nickname: " + nicknameProf);
			String cityInfoXPath = "/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody//div[@class='my_info']";
			List<WebElement> cityInfoElem = driver.findElements(By.xpath(cityInfoXPath));
			String cityInfo = "";
			if (cityInfoElem.get(0).getText().indexOf(",") > 0)
				cityInfo = cityInfoElem.get(0).getText().split(",")[0];
			else
				cityInfo = cityInfoElem.get(0).getText();
            List<WebElement> emails = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody/tr[3]/td/div[4]/div[1]/a[1]"));
			if (emails.size() > 0) {
				emails.get(0).click();
				String pageSource = driver.getPageSource();
				if (pageSource.indexOf("da Sie von diesem Mitglied ignoriert werden") < 0 && pageSource.indexOf("Sie haben das Maximum an Emails für diesen Tag") < 0 )
				{
					WebElement emailNickname = driver.findElement(By.name("to"));
                    String nickname = emailNickname.getAttribute("value").trim();

					String profid = getValueFromURLQueryString("id", prof.toString());
					if (!checkIfemailAlreadyResponded(nickname, profid)) {
						logger.info("Processing profile name " + nickname);
						if (!Files.exists(TESTRESULTS))
							Files.createDirectories(TESTRESULTS);
						String subjectTxtFilename = subjectTxt.replaceAll("[^\\w.-]", "_");
						String nickFilename = TESTRESULTS + "/" + nickname + "_" + subjectTxtFilename + ".txt";
						List<String> exceptionNicks = Arrays.asList("");
						try {
							exceptionNicks = Files.readAllLines(new File(NICKEXCEP).toPath());
						} catch (NoSuchFileException nse)
						{
							logger.warning("File nick.exceptions not found " + NICKEXCEP);
						}
						if (!exceptionNicks.contains(nickname)) {
							if (!Files.exists(Paths.get(nickFilename))) {
								driver.navigate().to(prof);
								List<WebElement> emailelem = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody/tr[3]/td/div[4]/div[1]/a[1]"));
								if (emailelem.size() > 0) {
									emailelem.get(0).click();
									WebElement subject = driver.findElement(By.name("subject"));
									WebElement body = driver.findElement(By.name("body"));
									subject.clear();
									String resolvedBody = bodyTxt;
									resolvedBody = modifyBodyText(resolvedBody, cityInfo, nickname);
									subject.sendKeys(subjectTxt);
									//subject.sendKeys("Konfuzius sagt...");
									// body.sendKeys("... Captain, wir können diesen Planeten verlassen, es gibt hier keinerlei Anzeichen intelligenten Lebens.... Spock!!! Ja Captain! Warten Sie Spock... wir müssen erst den Like-Button finden und drücken, sagt die Brücke... :-)");
									resolvedBody = resolvedBody.replaceAll("\n","");
									resolvedBody = resolvedBody.replaceAll("##","\n");
									body.sendKeys(resolvedBody);
									List<WebElement> sendMail = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/div[11]/div"));
									sendMail.get(0).click();
									newsent++;
									logger.info("Profile number newly sent " + Integer.toString(newsent));
									Files.createFile(Paths.get(nickFilename));
								}
							} else {
								logger.warning("Already send text with subject " + subjectTxt + " to " + nickname);
								alreadySent++;
							}
						} else {
							logger.info("Ignoring " + nickname + " because of entry in " + NICKEXCEP);
							ignoringme++;
						}
					}
					else
						logger.warning("Already answered email for nickname: " + nickname);
				}
				else
				{
					String xp ="/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody//div[@class='my_login']";
					WebElement mylogin = driver.findElement(By.xpath(xp));
					String nick = mylogin.getText().trim();
					logger.info("User " + nick + " is ignoring me or maximum of email sent reached per day (200)");
					ignoredList.add(nick);

				}
			}
        }
	}

	private static String modifyBodyText(String bodyTxt, String cityInfo, String nickname) {
		bodyTxt = bodyTxt.replaceAll("cityinfo",cityInfo);
		String nick = nickname.replaceAll("\\d", "");
		bodyTxt = bodyTxt.replaceAll("nickname",nick);
		return bodyTxt;
	}

	public static WebDriver startChromeDriverSession() {
// Capabilities [{applicationCacheEnabled=false, rotatable=false, mobileEmulationEnabled=false, chrome={userDataDir=C:\Users\u36342\AppData\Local\Temp\scoped_dir31688_10200}, takesHeapSnapshot=true, databaseEnabled=false, handlesAlerts=true, hasTouchScreen=false, version=43.0.2357.124, platform=XP, browserConnectionEnabled=false, nativeEvents=true, acceptSslCerts=true, locationContextEnabled=true, webStorageEnabled=true, browserName=chrome, takesScreenshot=true, javascriptEnabled=true, cssSelectorsEnabled=true}]
		String driverExe = System.getProperty("webdriver.chrome.driver");
		File file = new File(driverExe);
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		// chromePrefs.put("download.default_directory", downloadFilepath);
		ChromeOptions options = new ChromeOptions();
		HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
		options.setExperimentalOption("prefs", chromePrefs);
		options.addArguments("--test-type");
		options.addArguments("--dns-prefetch-disable");
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setCapability(ChromeOptions.CAPABILITY, options);

		WebDriver driverChrome = new ChromeDriver(cap);
		return driverChrome;
	}
	
	public static WebDriver startIEDriverSession(String kosmosLoginPage) {
		// Capabilities [{applicationCacheEnabled=false, rotatable=false, mobileEmulationEnabled=false, chrome={userDataDir=C:\Users\u36342\AppData\Local\Temp\scoped_dir31688_10200}, takesHeapSnapshot=true, databaseEnabled=false, handlesAlerts=true, hasTouchScreen=false, version=43.0.2357.124, platform=XP, browserConnectionEnabled=false, nativeEvents=true, acceptSslCerts=true, locationContextEnabled=true, webStorageEnabled=true, browserName=chrome, takesScreenshot=true, javascriptEnabled=true, cssSelectorsEnabled=true}]
				String driverExe = System.getProperty("webdriver.ie.driver");
				File file = new File(driverExe);
				HashMap<String, Object> prefs = new HashMap<String, Object>();
				DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
/*				cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
				cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				cap.setCapability(ChromeOptions.CAPABILITY, options);
				*/
				//cap.setCapability(InternetExplorerDriver.,"0");
				//cap.setCapability(InternetExplorerDriver.NATIVE_EVENTS,"false");
				cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
				cap.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS,true);
				cap.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,kosmosLoginPage);
				// cap.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,kosmosLoginPage);
				
				/*
				, enablePersistentHover=true, ignoreZoomSetting=false, 
						handlesAlerts=true, version=11, platform=WINDOWS, nativeEvents=true, elementScrollBehavior=0, requireWindowFocus=false, 
						browserName=internet explorer, initialBrowserUrl=, takesScreenshot=true, javascriptEnabled=true, ignoreProtectedModeSettings=false, 
						enableElementCacheCleanup=true, cssSelectorsEnabled=true, unexpectedAlertBehaviour=dismiss
						*/				
				WebDriver driverdriver = new InternetExplorerDriver(cap);
				// setDriverTimeouts(driverdriver, 10);
				
				//driverdriver.navigate().to(kosmosLoginPage);
				// driverdriver.get("http://helloselenium.blogspot.com");
				return driverdriver;
			}

	private static void setDriverTimeouts(WebDriver driverdriver, int sec) {
		driverdriver.manage().timeouts().pageLoadTimeout(sec,  TimeUnit.SECONDS);
		driverdriver.manage().timeouts().implicitlyWait(sec,  TimeUnit.SECONDS);
		driverdriver.manage().timeouts().setScriptTimeout(sec,  TimeUnit.SECONDS);
	}

}
