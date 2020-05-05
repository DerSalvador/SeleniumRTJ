package deprecated.com.juliusbaer.selennium.browser.automation_off;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import mx4j.log.Log;

import org.jboss.netty.logging.InternalLogger;
import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class KosmosDownload {
	static WebDriver driverChrome = null;
	static Logger logger = LoggingConfig.getLoggingConfig("KosmosDownload.log", "KosmosDownload");
	public class Params {
		@Parameter(names = "-downloadFolder", description = "Download folder for the kosmos archives", required = true)
		public String downloadFolder;
		@Parameter(names = "-versions", variableArity = true, description = "kosmos version number paths to download (versiongroup\\version), 3.7\\3.7.1.1, 3.6\\3.6.11-SNAPSHOT, ...", required = true)
		public List<String> versions;
		@Parameter(names = "-KosmosLoginPage", variableArity = true, description = "Kosmos Login Page e.g. https://download.kosmos-banking.com", required = true)
		public String kosmosLoginPage;

	}

	public static void main(String[] args) {
		Params params = new KosmosDownload().new Params();
		JCommander jc = new JCommander(params);
		try {
			jc.setProgramName("KosmosDownload");
			jc.parse(args);
			new File(params.downloadFolder).mkdirs();
			List<String> versions = getVersions(params.versions);
			kosmosDownloadChrome(versions, params.downloadFolder, params.kosmosLoginPage);
		} catch (Exception e) {
			e.printStackTrace();
			jc.usage();
		}
	}

	private static List<String> getVersions(List<String> versions) {
		List<String> effectiveVersions = new ArrayList<String>();
		for (String ver : versions) {
			String [] verTokens = ver.split("/");
			effectiveVersions.add(verTokens[0]);
			effectiveVersions.add(verTokens[1]);
		}
		return effectiveVersions;
	}

	public static void kosmosDownloadChrome(List<String> versions,
			String downloadFolder, String kosmosLoginPage) throws Exception {
		startChromeDriver(downloadFolder, kosmosLoginPage);
		loginToKosmosDownloadSite();
		logger.log(Level.INFO, String.format("Downloading from %s to %s following versions %s",kosmosLoginPage, downloadFolder, versions.toString()));
		downloadArchives(versions);
	}

	private static void startChromeDriver(String downloadFolder, String kosmosLoginPage) {
		Thread a = new Thread(() -> {
			driverChrome = startChromeDriverSession(downloadFolder);
		});
		try {
			a.start();
			a.join();
			driverChrome.manage().timeouts()
					.pageLoadTimeout(30, TimeUnit.SECONDS);
			driverChrome.manage().timeouts()
					.implicitlyWait(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		driverChrome.get(kosmosLoginPage);
		driverChrome.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		driverChrome.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	private static void downloadArchives(List<String> versions) {
		for (int i = 0; i < versions.size(); i++) {
			WebElement elem = driverChrome.findElement(By
					.xpath("//*[@id=\"topPane\"]/descendant::span[text()='"
							+ versions.get(i) + "']"));
			elem.click();
			driverChrome.findElement(
					By.xpath("//div[@title='kosmos-" + versions.get(i+1) + ".tar.gz']"))
					.click();
			driverChrome.findElement(By.id("download_button_label")).click();
			i++;
		}
	}

	private static void loginToKosmosDownloadSite() {
		JavascriptExecutor js = (JavascriptExecutor) driverChrome;
		js.executeScript(" var inputs = document.getElementsByTagName('input');"
				+ "for (var p in inputs) {"
				+ "if (inputs[p].type == 'password') { "
				+ "if (inputs[p].name == 'password') {  inputs[p].value = '^My713c@s4JN'; } }"
				+ "if (inputs[p].type == 'text') { "
				+ "if (inputs[p].name == 'userid') {  inputs[p].value = 'bjb'; }"
				+ "console.log('name=' + inputs[p].name);"
				+ "for (var o in p) { console.log(o, p[o]); } " + "}}");
		WebElement element = driverChrome.findElement(By.id("login_form"));
		element.submit();
	}

	public static WebDriver startChromeDriverSession(String downloadFolder) {
// Capabilities [{applicationCacheEnabled=false, rotatable=false, mobileEmulationEnabled=false, chrome={userDataDir=C:\Users\u36342\AppData\Local\Temp\scoped_dir31688_10200}, takesHeapSnapshot=true, databaseEnabled=false, handlesAlerts=true, hasTouchScreen=false, version=43.0.2357.124, platform=XP, browserConnectionEnabled=false, nativeEvents=true, acceptSslCerts=true, locationContextEnabled=true, webStorageEnabled=true, browserName=chrome, takesScreenshot=true, javascriptEnabled=true, cssSelectorsEnabled=true}]
		String driverExe = System.getProperty("webdriver.chrome.driver");
		File file = new File(driverExe);
		String downloadFilepath = downloadFolder;
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		ChromeOptions options = new ChromeOptions();
		HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
		options.setExperimentalOption("prefs", chromePrefs);
		options.addArguments("--test-type");
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setCapability(ChromeOptions.CAPABILITY, options);
		WebDriver driverChrome = new ChromeDriver(cap);
		return driverChrome;
	}
}
