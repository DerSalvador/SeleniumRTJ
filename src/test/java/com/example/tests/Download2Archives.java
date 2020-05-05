//package com.example.tests;
//
//import com.thoughtworks.selenium.*;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import java.util.regex.Pattern;
//
//public class Download2Archives {
//	private Selenium selenium;
//
//	@Before
//	public void setUp() throws Exception {
//		selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://change-this-to-the-site-you-are-testing/");
//		selenium.start();
//	}
//
//	@Test
//	public void testDownload2Archives() throws Exception {
//		selenium.open("https://download.kosmos-banking.com/pydio/#1");
//		assertEquals("Kosmos Banking - 3.7", selenium.getTitle());
//		selenium.click("css=#action_instance_logout > span.menu_label");
//		assertEquals("Kosmos Banking - /", selenium.getTitle());
//		selenium.type("name=userid", "bjb");
//		selenium.click("name=password");
//		selenium.type("name=password", "^My713c@s4JN");
//		selenium.click("name=ok");
//		selenium.click("id=webfx-tree-object-48-label");
//		selenium.click("css=div.thumbLabel.no_select_bg");
//		selenium.click("id=download_button_label");
//		selenium.click("css=#item-37kosmos-37targz-cont > div.thumbLabel.no_select_bg");
//		selenium.click("id=download_button_label");
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		selenium.stop();
//	}
//}