package com.juliusbaer.selenium.browser.automation;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.sun.org.apache.xerces.internal.util.URI;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StievesSeleniumRTJ {
    private static final Path TESTRESULTS = Paths.get(System.getProperty("user.home") + "/TestResults");
    public static final String NICKEXCEP = TESTRESULTS + "/nick.exceptions";
    static WebDriver driver = null;
    static Logger logger = LoggingConfig.getLoggingConfig("/tmp/rtj.log", "StievesSeleniumRTJ");
    public static int alreadySent = 0;
    public static int curProfNr = 0;
    public static int newsent = 0;
    public static ArrayList<String> newList = new ArrayList<String>();
    public static int ignoringme = 0;
    public static ArrayList<String> ignoredList = new ArrayList<String>();
    private static int elementTimeout;
    private static int maxcount;
    private static boolean onlineList;
    private static boolean headless;
    private static boolean newones;
    private static Object o;
    private static boolean femaleLookingForFemale;
    private static boolean femaleLookingForMale;
    private static boolean maleLookingForFemale;
    private static int pagesCount;

    private static class MaximumEmailReachedException extends Throwable {
    }

    public class Params {
        @Parameter(names = "-elementTimeout", description = "elementTimeout", required = true)
        public Integer elementTimeout;
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
        @Parameter(names = "-online-list", variableArity = true, description = "online-list", required = false)
        public String onlineList;
        @Parameter(names = "-headless", variableArity = true, description = "headless", required = false)
        public String headless;
        @Parameter(names = "-newones", variableArity = true, description = "newones", required = false)
        public String newones;
        @Parameter(names = "-female-looking-for-female", variableArity = true, description = "female-looking-for-female", required = false)
        public String femaleLookingForFemale;
        @Parameter(names = "-female-looking-for-male", variableArity = true, description = "female-looking-for-male", required = false)
        public String femaleLookingForMale;
        @Parameter(names = "-male-looking-for-female", variableArity = true, description = "male-looking-for-female", required = false)
        public String maleLookingForFemale;
        @Parameter(names = "-maxcount", variableArity = true, description = "maxcount", required = false)
        public Integer maxcount;
    }

    static <T> void inspect(Object o) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
        for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
            String propertyName = propertyDesc.getName();
            Object value = propertyDesc.getReadMethod().invoke(o);
            logger.info(propertyName + "=" + value);
        }
    }

    public static void main(String[] args) {
        Params params = new StievesSeleniumRTJ().new Params();
        JCommander jc = new JCommander(params);
        try {
            jc.setProgramName("Selenium Tests");
            for (String arg : args) {
                logger.info("arg=" + arg);
            }
            jc.parse(args);
            logger.info("Params passed ");
            for (ParameterDescription dsc : jc.getParameters()) {
                logger.info(dsc.getNames() + "=");
            }
            for (Object o : jc.getObjects()) {
                Params p = (Params) o;
                logger.info("body=" + ((Params) o).body);
                logger.info("elementTimeout=" + String.valueOf(((Params) o).elementTimeout));
                inspect(Params.class);
            }
            elementTimeout = params.elementTimeout;
            onlineList = Boolean.parseBoolean(params.onlineList);
            headless = Boolean.parseBoolean(params.headless);
            newones = Boolean.parseBoolean(params.newones);
            maleLookingForFemale = Boolean.parseBoolean(params.maleLookingForFemale);
            femaleLookingForFemale = Boolean.parseBoolean(params.femaleLookingForFemale);
            femaleLookingForMale = Boolean.parseBoolean(params.femaleLookingForMale);
            maxcount = params.maxcount;
            runTests(params.LoginPage, params.strategy, params.subject, params.body, params.zweck, Optional.ofNullable(params.region), Optional.ofNullable(params.country), params.username, params.password, onlineList, newones, headless);

        } catch (Exception e) {
            e.printStackTrace();
            jc.usage();
        }
    }

    public static void runTests(String LoginPage, String strategy, String subject, String body, String zweck, Optional<String> region, Optional<String> country, String username, String password, boolean onlineList, boolean newones, boolean headless) throws Exception {
        startDriver(LoginPage, headless);
        loginToPageAndStartRobot(driver, LoginPage, strategy, subject, body, zweck, region, country, username, password, onlineList, newones, headless);
        logger.info("Finished bot for " + country + " and subject " + subject);
    }

    private static void startDriver(String LoginPage, boolean headless) {
        runWebBrowser(LoginPage, headless);
        setDriverTimeouts(driver, 60);
        /*Thread driverThread = new Thread(() -> {
            runWebBrowser(LoginPage);

        });
        try {
            driverThread.start();
            driverThread.join();
            setDriverTimeouts(driver, 30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        driver.get(LoginPage);
        ((JavascriptExecutor) driver).executeScript(
                "document.title = 'SeleniumTest Running'");
        setDriverTimeouts(driver, 30);
    }

    private static void runWebBrowser(String LoginPage, boolean headless) {
        String driverExe = System.getProperty("webdriver.chrome.driver");
        logger.info("Driver Exe = " + driverExe);
        if (driverExe != null && driverExe.length() > 0)
            driver = startChromeDriverSession(headless);
        driverExe = System.getProperty("webdriver.ie.driver");
        if (driverExe != null && driverExe.length() > 0)
            driver = startIEDriverSession(LoginPage);
        if (driver == null)
            logger.log(Level.SEVERE, "No driver exe found for " + driverExe);
    }

    private static void loginToPageAndStartRobot(WebDriver driver, String LoginPage, String strategy, String subject, String body, String zweck, Optional<String> region, Optional<String> country, String username, String password, boolean onlineList, boolean newones, boolean headless) throws IOException, URISyntaxException {
        List<WebElement> pages = new ArrayList<WebElement>();
        try {
            WebElement pwdEl = driver.findElement(By.name("pass_lg"));
            pwdEl.sendKeys(password);
            WebElement userEl = driver.findElement(By.name("login_lg"));
            userEl.sendKeys(username);
            WebElement form = driver.findElement(By.name("login_form"));
            form.submit();
            if (newones) {
                traverseNewOnes(driver, LoginPage, strategy, subject, body, zweck, region, country);
            } else if (onlineList)
                traverseOnlineList(driver, subject, body, country);
            else
                traversePages(driver, LoginPage, strategy, subject, body, zweck, region, country);
        } catch (MaximumEmailReachedException e) {
            logger.warning("Maxium sent email reached (" + Integer.toString(maxcount) + ")");
        } catch (StoppingAtMaxCountEmailSentException stoppingAtMaxCountEmailSentException) {
            stoppingAtMaxCountEmailSentException.printStackTrace();
        } catch (Exception exc) {
            logger.warning("Caught exception " + exc.getMessage());
            exc.printStackTrace();
        }
        finally {
            String jss = "";
            String.format(jss, "document.title = '%s'", country);
            ((JavascriptExecutor) driver).executeScript(jss);

            logger.info("Finished traversing all pages " + Integer.toString(pagesCount));
            logger.info("Count Already sent = " + Integer.toString(alreadySent));
            logger.info("Count Ignoring Me = " + Integer.toString(ignoringme));
            logger.info("Ignoring Me = " + ignoredList.toString());
            logger.info("Count Newly sent = " + Integer.toString(newsent));
            logger.info("New ones = " + newList.toString());
            try {
                driver.close();
                driver.quit();
            } catch(Exception e)
            {
                logger.info("could not close driver");
            }
        }
    }

    private static int traverseNewOnes(WebDriver driver, String LoginPage, String strategy, String subject, String body, String zweck, Optional<String> region, Optional<String> country) throws URISyntaxException, IOException, MaximumEmailReachedException, StoppingAtMaxCountEmailSentException {
        driver.navigate().to(LoginPage + "/de/quick_search.php?sel=search_new");
        // /de/viewprofile.php?id=176760&page=1&search_type=q
        // logger.info(driver.getPageSource());
        // List<WebElement> profs = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody/tr[3]/td/div[4]/table/tbody/tr/td[2]/a"));
        List<WebElement> pages = new ArrayList<WebElement>();
        List<String> pagesHrefs = new ArrayList<String>();
        boolean onlyOnePage = false;
        // Get 500 profiles per page
        clickNumberProfsOnPage(driver);
        /*logger.info("-------------------------------------------------------------------------");
        logger.info("Page Source New Ones");
        logger.info("-------------------------------------------------------------------------");
        logger.info(driver.getPageSource());
        logger.info("-------------------------------------------------------------------------");
        */
        if (driver.getPageSource().indexOf("...") > 0 && 
           driver.getPageSource().indexOf("...,") < 0) {
            List<WebElement> pagesGruppe = driver.findElements(By.xpath("//a[@class='page_link' and text() = '...']"));
            int pagesCountBefore = -1;
            while (pagesCountBefore != pages.size()) {
                pagesCount += pagesCountBefore;
                pagesCountBefore = pages.size();
                addToProfilePagesList(driver, pages, pagesHrefs);
                pagesGruppe.get(0).click();
                pagesGruppe = driver.findElements(By.xpath("//a[@class='page_link' and text() = '...']"));
                //if (pagesGruppe.size() > 0)
                //    pagesGruppe.remove(0);
            }
            // addToProfilePagesList(driver, pages, pagesHrefs);
            processMoreThanOnePage(driver, strategy, subject, body, country, pages, pagesHrefs);
        } else {
            addToProfilePagesList(driver, pages, pagesHrefs);
            addToProfilePagesListbyImg(driver, pages, pagesHrefs);
            String[] s = new String[1];
            processProfLink(driver, subject, body, (ArrayList) pagesHrefs, country.get());

        }
        return pagesCount;
    }

    private static void traverseOnlineList(WebDriver driver, String subject, String body, Optional<String> country) throws MaximumEmailReachedException, StoppingAtMaxCountEmailSentException, IOException, URISyntaxException {
        List<WebElement> onlineContainer = null;
        if (maleLookingForFemale || femaleLookingForFemale)
            onlineContainer = driver.findElements(By.xpath("//*[@class=\"im_list_element chat_userlist_div_female \"]"));
        else
            onlineContainer = driver.findElements(By.xpath("//*[@class=\"im_list_element chat_userlist_div_male \"]"));
        List<String> ids = new ArrayList<String>();
        Pattern p = Pattern.compile("(user=[0-9]+)");
        for (WebElement onlineSau : onlineContainer) {
            String divTextOnClick = new String(onlineSau.findElement(By.tagName("div")).getAttribute("onClick").getBytes());
            Matcher m = p.matcher(divTextOnClick);
            if (m.find()) {
                String group = m.toMatchResult().group(1);
                String id = group.split("=")[1];
                ids.add(id);
            }


        }
        for (String id : ids) {
            String profile = String.format("http://www.reif-trifft-jung.de/viewprofile.php?id=%s", id);
            // driver.navigate().to(profile);
            processProfile(driver, subject, body, country.orElse("Empty"), profile);

            // traverseProfiles(driver, String.format("http://www.reif-trifft-jung.de/viewprofile.php?id=%s", id), subject, body, country.get());
        }

    }

    private static int traversePages(WebDriver driver, String LoginPage, String strategy, String subject, String body, String zweck, Optional<String> region, Optional<String> country) throws URISyntaxException, IOException, MaximumEmailReachedException, StoppingAtMaxCountEmailSentException {
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
        //
        List<WebElement> inputs = driver.findElements(By.tagName("select"));

        for (WebElement input : inputs) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].removeAttribute('readonly','readonly')", input);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].removeAttribute('disabled','disabled')", input);
        }
        //String script = "document.getElementById('gender_1').removeAttribute('disabled')";
        //JavascriptExecutor js = (JavascriptExecutor)driver;
        //js.executeScript(script);
        //WebElement inp= driver.findElement(By.name("gender_1"));
        //inp.sendKeys("Whatever");
        int searcher = 1;
        int searchee = 2;
        if (maleLookingForFemale) {
            searcher = 1;
            searchee = 2;
        } else if (femaleLookingForFemale) {
            searcher = 2;
            searchee = 2;
        } else if (femaleLookingForMale) {
            searcher = 2;
            searchee = 1;
        }
        driver.findElement(By.xpath(String.format(".//*[@name='gender_1']/option[%d]",searcher))).click();
        driver.findElement(By.xpath(String.format(".//*[@name='gender_2']/option[%d]",searchee))).click();
        //*[@id="gen_ages_from"]/select
        WebElement genagesfrom = driver.findElement(By.xpath("//*[@id='gen_ages_from']/select"));
        genagesfrom.sendKeys("18");
        //gender_1.sendKeys("W");
        // ((JavascriptExecutor) driver).executeScript("document.getElementById('gender_1').value='Weiblich'");
        //
        searchform.submit();
        // /de/viewprofile.php?id=176760&page=1&search_type=q
        // logger.info(driver.getPageSource());
        // List<WebElement> profs = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody/tr[3]/td/div[4]/table/tbody/tr/td[2]/a"));
        List<WebElement> pages = new ArrayList<WebElement>();
        List<String> pagesHrefs = new ArrayList<String>();
        boolean onlyOnePage = false;
        // Get 500 profiles per page
        clickNumberProfsOnPage(driver);
        if (driver.getPageSource().indexOf("...") > 0 && 
           driver.getPageSource().indexOf("...,") < 0) {
            List<WebElement> pagesGruppe = driver.findElements(By.xpath("//a[@class='page_link' and text() = '...']"));
            int pagesCountBefore = -1;
            while (pagesCountBefore != pages.size()) {
                pagesCount += pagesCountBefore;
                pagesCountBefore = pages.size();
                addToProfilePagesList(driver, pages, pagesHrefs);
                pagesGruppe.get(0).click();
                pagesGruppe = driver.findElements(By.xpath("//a[@class='page_link' and text() = '...']"));
                //if (pagesGruppe.size() > 0)
                //    pagesGruppe.remove(0);
            }
            // addToProfilePagesList(driver, pages, pagesHrefs);
            processMoreThanOnePage(driver, strategy, subject, body, country, pages, pagesHrefs);
        } else {
            addToProfilePagesList(driver, pages, pagesHrefs);
            addToProfilePagesListbyImg(driver, pages, pagesHrefs);
            String[] s = new String[1];
            processProfLink(driver, subject, body, (ArrayList) pagesHrefs, country.get());

        }
        return pagesCount;
    }

    private static void processMoreThanOnePage(WebDriver driver, String strategy, String subject, String body, Optional<String> country, List<WebElement> pages, List<String> pagesHrefs) throws IOException, URISyntaxException, MaximumEmailReachedException, StoppingAtMaxCountEmailSentException {
        addToProfilePagesList(driver, pages, pagesHrefs);
        if (!strategy.isEmpty() && strategy.equals("reverse")) {
            WebElement pagesArray[] = new WebElement[1];
            pagesArray = pages.toArray(pagesArray);
            logger.info("Found " + Integer.toString(pagesArray.length) + " pages");
            for (int i = pagesArray.length - 1; i >= 0; i--) {
                if (pagesHrefs != null && pagesHrefs.get(i) != null) {
                    logger.info("Processing page number " + Integer.toString(i));
                    traverseProfiles(driver, pagesHrefs.get(i), subject, body, country.orElse("Alle"));
                }

            }
        } else
            for (WebElement page : pages) {
                traverseProfiles(driver, page.getAttribute("href"), subject, body, country.get());
            }
    }

    private static void clickNumberProfsOnPage(WebDriver driver) {
        String xpathProfsOnPage = "";
        for (int n : new int[]{500, 100, 50}) {
            xpathProfsOnPage = String.format("//a[text() = '%d']", n);
            try {
                WebElement profsOnPage = driver.findElement(By.xpath(xpathProfsOnPage));
                if (profsOnPage != null) {
                    profsOnPage.click();
                    return;

                }
            } catch (Throwable ex) {
                logger.warning(ex.getMessage());
            }
        }
    }

    private static void addToProfilePagesList(WebDriver driver, List<WebElement> pages, List<String> pagesHrefs) {
        for (String cssClass : new String[]{"page_link", "page_link_active"}) {
            List<WebElement> pageLinks = driver.findElements(By.xpath("//a[@class='" + cssClass + "']"));
            for (WebElement p : pageLinks) {
                if (!contains(pagesHrefs, p.getAttribute("href")) && !p.getText().equals("...")) {
                    pages.add(p);
                    pagesHrefs.add(p.getAttribute("href"));
                }
            }
        }
    }

    private static void addToProfilePagesListbyImg(WebDriver driver, List<WebElement> pages, List<String> pagesHrefs) {
        for (String cssClass : new String[]{"my_img"}) {
            List<WebElement> pageLinks = driver.findElements(By.xpath("//img[@class='" + cssClass + "']"));
            for (WebElement p : pageLinks) {
                WebElement parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return arguments[0].parentNode;", p);
                pagesHrefs.add(parent.getAttribute("href"));
            }
        }
    }

    private static boolean contains(List<String> list, String elem) {
        for (String e : list) {
            if (elem.equals(e)) {
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
            if (param.getName().equals(key))
                return param.getValue();
        }
        throw new NotFoundException("Key was not found in URL [" + key + "] in " + url);
    }

    /*public static List<WebElement> FindElement(WebDriver driver, By by, int timeoutInSeconds) {
        try {
            setDriverTimeouts(driver, timeoutInSeconds);
            WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
            // wait.ignoring(NoSuchElementException.class);
            wait.until(ExpectedConditions.presenceOfElementLocated(by)); //throws a timeout exception if element not present after waiting <timeoutInSeconds> seconds
            List<WebElement> elements = driver.findElements(by);
            return elements;
        } catch (Exception e) {
            logger.warning(e.toString());
        } finally {
            setDriverTimeouts(driver, 60);
        }
        return new ArrayList<WebElement>();
    }
*/
    private static boolean checkIfemailAlreadyResponded(String nickname, String profid) throws UnsupportedEncodingException {
//        // /html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/div[15]/table/tbody/tr/td[1]/b
//        //String checkXPath = "/html/body/table/tbody/tr/td/table[2]/tbody/tr//b[contains(text(),'" + nickname.trim() + ":')]";
//        String checkXPath = "/html/body/table/tbody/tr/td/table[2]/tbody/tr//b[contains(text(),'" + nickname.trim() + "')]";
//        //                   /html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/div[7]/table/tbody/tr/td[1]/b
//        // http://www.reif-trifft-jung.de/de/mailbox.php?sel=history&id=157556
//        String url = "http://www.reif-trifft-jung.de/de/mailbox.php?sel=history&id=" + profid;
//        // http://www.reif-trifft-jung.de/de/mailbox.php?sel=viewto&id=16713916
//        // String encode = URLEncoder.encode(url, "UTF-8");
//        String encode = url;
//        driver.navigate().to(encode);
//        List<WebElement> we = FindElement(driver, By.xpath(checkXPath), elementTimeout);
//        if (we.size() > 0) {
//            return true;
//        }
        return false;
    }

    private static void traverseProfiles(WebDriver driver, String pageUrl, String subjectTxt, String bodyTxt, String country) throws IOException, URISyntaxException, MaximumEmailReachedException, StoppingAtMaxCountEmailSentException {
        if (pageUrl == null) {
            logger.warning("Pargeurl is null for " + country);
            return;
        }
        String js = "";
        String.format(js, "document.title = '%s'", country);
        ((JavascriptExecutor) driver).executeScript(js);

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
        processProfLink(driver, subjectTxt, bodyTxt, profLinks, country);
    }

    private static void processProfLink(WebDriver driver, String subjectTxt, String bodyTxt, ArrayList<String> profLinks, String country) throws URISyntaxException, IOException, MaximumEmailReachedException, StoppingAtMaxCountEmailSentException {
        for (String prof : profLinks) {
            processProfile(driver, subjectTxt, bodyTxt, country, prof);
        }
    }

    private static void processProfile(WebDriver driver, String subjectTxt, String bodyTxt, String country, String prof) throws URISyntaxException, IOException, StoppingAtMaxCountEmailSentException, MaximumEmailReachedException {
        String js = "";
        String.format(js, "document.title = '%s'", country);
        ((JavascriptExecutor) driver).executeScript(js);

        curProfNr++;
        logger.info("Processing profile number " + Integer.toString(curProfNr));
        logger.info("Count Newly sent = " + Integer.toString(newsent));
        logger.info("Pages to process " + Integer.toString(pagesCount));

        //String url = prof.getAttribute("href");
        // driver.navigate().to(LoginPage + prof);
        if (prof.indexOf("viewprofile") <= 0) return;
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
            if (pageSource.indexOf("da Sie von diesem Mitglied ignoriert werden") < 0) {
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
                    } catch (NoSuchFileException nse) {
                        logger.warning("File nick.exceptions not found " + NICKEXCEP);
                    }
                    if (!exceptionNicks.contains(nickname)) if (!Files.exists(Paths.get(nickFilename))) {
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
                            resolvedBody = resolvedBody.replaceAll("\n", "");
                            resolvedBody = resolvedBody.replaceAll("##", "\n");
                            body.sendKeys(resolvedBody);
                            List<WebElement> sendMail = driver.findElements(By.xpath("/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/div[11]/div"));
                            sendMail.get(0).click();
                            if (driver.getPageSource().indexOf("Sie haben das Maximum an Emails für diesen Tag erreicht") < 0) {
                                Files.createFile(Paths.get(nickFilename));
                                newsent++;
                                if (newsent >= maxcount) {
                                    throw new StoppingAtMaxCountEmailSentException(maxcount, logger);
                                }
                                newList.add(nickname);
                                logger.info("Profile number newly sent " + Integer.toString(newList.size()));
                            } else {
                                logger.warning("Maxium of sent email reached, leaving program...");
                                throw new MaximumEmailReachedException();
                            }
                        }
                    } else {
                        logger.warning("Already send text with subject " + subjectTxt + " to " + nickname);
                        alreadySent++;
                    }
                    else {
                        logger.info("Ignoring " + nickname + " because of entry in " + NICKEXCEP);
                        ignoringme++;
                    }
                } else
                    logger.warning("Already answered email for nickname: " + nickname);
            } else {
                String xp = "/html/body/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table/tbody//div[@class='my_login']";
                WebElement mylogin = driver.findElement(By.xpath(xp));
                String nick = mylogin.getText().trim();
                logger.info("User " + nick + " is ignoring me");
                ignoredList.add(nick);

            }
        }
    }

    private static String modifyBodyText(String bodyTxt, String cityInfo, String nickname) {
        bodyTxt = bodyTxt.replaceAll("cityinfo", cityInfo);
        String nick = nickname.replaceAll("\\d", "");
        bodyTxt = bodyTxt.replaceAll("nickname", nick);
        return bodyTxt;
    }

    public static WebDriver startChromeDriverSession(boolean headless) {
// Capabilities [{applicationCacheEnabled=false, rotatable=false, mobileEmulationEnabled=false, chrome={userDataDir=C:\Users\u36342\AppData\Local\Temp\scoped_dir31688_10200}, takesHeapSnapshot=true, databaseEnabled=false, handlesAlerts=true, hasTouchScreen=false, version=43.0.2357.124, platform=XP, browserConnectionEnabled=false, nativeEvents=true, acceptSslCerts=true, locationContextEnabled=true, webStorageEnabled=true, browserName=chrome, takesScreenshot=true, javascriptEnabled=true, cssSelectorsEnabled=true}]
        String driverExe = System.getProperty("webdriver.chrome.driver");
        logger.info("Starting Chrome Driver " + driverExe);
        File file = new File(driverExe);
        /*
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        // chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();

        //options.addArguments("start-maximized"); // https://stackoverflow.com/a/26283818/1689770
        //options.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
        options.addArguments("--headless"); // only if you are ACTUALLY running headless
        //options.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-infobars"); //https://stackoverflow.com/a/43840128/1689770
        options.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
        options.addArguments("--disable-gpu"); //https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc
        // driver = new ChromeDriver(options);

        HashMap<String, Object> chromeOptionsMap = new HashMap<String, Object>();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--test-type");
        options.addArguments("--dns-prefetch-disable");
        // options.addArguments("--headless");
	options.addArguments("enable-automation");
	// options.addArguments("--headless");
	options.addArguments("--window-size=1920,1080");
	options.addArguments("--no-sandbox");
	options.addArguments("--disable-extensions");
	options.addArguments("--dns-prefetch-disable");
	options.addArguments("--disable-gpu");
	//options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, chromeOptionsMap);
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        WebDriver driverChrome = new ChromeDriver(cap);
*/
        ChromeOptions optionsHeadlessOnly = new ChromeOptions();
        if (headless) {
            optionsHeadlessOnly.addArguments("--headless");
        }
        optionsHeadlessOnly.addArguments("start-minimized"); // https://stackoverflow.com/a/26283818/1689770
        WebDriver driverChrome = new ChromeDriver(optionsHeadlessOnly);
        setDriverTimeouts(driverChrome, 30);
        //driverChrome.manage().window().setSize(new Dimension(100, 100));
        //driverChrome.manage().window().setPosition(new Point(2000,100));

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
        cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        cap.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
        cap.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, kosmosLoginPage);
        // cap.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,kosmosLoginPage);
				
				/*
				, enablePersistentHover=true, ignoreZoomSetting=false, 
						handlesAlerts=true, version=11, platform=WINDOWS, nativeEvents=true, elementScrollBehavior=0, requireWindowFocus=false, 
						browserName=internet explorer, initialBrowserUrl=, takesScreenshot=true, javascriptEnabled=true, ignoreProtectedModeSettings=false, 
						enableElementCacheCleanup=true, cssSelectorsEnabled=true, unexpectedAlertBehaviour=dismiss
						*/
        WebDriver driverdriver = new InternetExplorerDriver(cap);
        driverdriver.manage().window().setSize(new Dimension(100, 100));
        driverdriver.manage().window().setPosition(new Point(2000, 100));
        setDriverTimeouts(driverdriver, 120);

        //driverdriver.navigate().to(kosmosLoginPage);
        // driverdriver.get("http://helloselenium.blogspot.com");
        return driverdriver;
    }

    private static void setDriverTimeouts(WebDriver driverdriver, int sec) {
        logger.info("driverdriver " + driverdriver);
        driverdriver.manage().timeouts().pageLoadTimeout(sec, TimeUnit.SECONDS);
        driverdriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driverdriver.manage().timeouts().setScriptTimeout(sec, TimeUnit.SECONDS);
    }

}
