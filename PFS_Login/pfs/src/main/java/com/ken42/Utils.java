package com.ken42;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.logging.*;
import java.util.regex.*;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import org.testng.annotations.Test;

import com.google.common.base.Function;

import org.openqa.selenium.Alert;

public class Utils {

	static Logger log = Logger.getLogger(Utils.class.getName());
	static int time = 1000;
	// public static Logger log = Logger.getLogger("Pfs_portal");
	static boolean debug = true;

	public static void clickXpath(WebDriver driver, String xpath, int time, String msg, Logger log)
			throws NoSuchElementException, InterruptedException, ElementClickInterceptedException, Exception {
		JavascriptExecutor js3 = (JavascriptExecutor) driver;
		int count = 0;
		int maxTries = 12;
		final String XPATH = xpath;
		while (true) {
			try {
				Thread.sleep(1000);
				if (debug)
					log.info("Click on the:" + msg);
				System.out.print("Click on the:" + msg);
				Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
						.withTimeout(Duration.ofSeconds(60))
						.pollingEvery(Duration.ofSeconds(6))
						.ignoring(NoSuchElementException.class);
				WebElement WE = wait.until(new Function<WebDriver, WebElement>() {
					public WebElement apply(WebDriver driver) {
						return driver.findElement(By.xpath(XPATH));
					}
				});
				WE.click();
				// WebDriverWait wait = new WebDriverWait(driver, 5, 500);
				// WebElement element =
				// wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
				// element.click();
				break;
			} catch (NoSuchElementException e) {
				Thread.sleep(2000);
				if (debug)
					log.warning("Failed to Click on the :" + msg);
				System.out.println("Failed to Click on the :" + msg);
				if (++count == maxTries) {
					log.warning("NoSuchElementException is " + e);
					Utils.printException(e);
					throw e;
				}
			} catch (ElementClickInterceptedException e) {
				Thread.sleep(2000);
				if (debug)
					log.warning("Failed to Click on the :" + msg);
				System.out.println("Failed to Click on the :" + msg);
				if (++count == maxTries) {
					log.warning("ElementClickInterceptedException is " + e);
					Utils.printException(e);
					throw e;
				}
			} catch (Exception e) {
				Thread.sleep(2000);
				if (debug)
					log.warning("Failed to Click on the :" + msg);
				System.out.println("Failed to Click on the :" + msg);
				if (++count == maxTries) {
					log.warning("Exception  is " + e);
					Utils.printException(e);
					throw e;
				}
			}
		}

	}

	public static void callSendkeys(WebDriver driver, String Xpath, String Value, int time1, Logger log)
			throws Exception, NoSuchElementException {
		int count = 0;
		int maxTries = 10;
		final String XPATH = Xpath;
		while (true) {
			try {
				if (debug)
					log.info("***********************Entering value   " + Value);
				System.out.print("***********************Entering value   " + Value);
				Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
						.withTimeout(Duration.ofSeconds(40))
						.pollingEvery(Duration.ofSeconds(4))
						.ignoring(NoSuchElementException.class);
				WebElement WE = wait.until(new Function<WebDriver, WebElement>() {
					public WebElement apply(WebDriver driver) {
						return driver.findElement(By.xpath(XPATH));
					}
				});
				Thread.sleep(500);
				WE.sendKeys(Value);
				Thread.sleep(500);
				break;
			} catch (Exception e) {
				Thread.sleep(1000);
				log.warning("Failed to send value  " + Value);
				if (++count == maxTries) {
					Utils.printException(e);
					throw e;
				}
			}
		}

	}

	public static boolean checkWindowsOs() {
		String OS = "";
		OS = System.getProperty("os.name");
		System.out.println(OS);

		if (OS.contains("Windows")) {
			return true;
		}
		return false;

	}

	@Test
	public static void login(WebDriver driver, String Email, String url, Logger log, String[] csvCell)
			throws Exception {
		try {
			System.out.println("**^#*:" + url);
			if (otplogin(url)) {
				Utils.smallSleepBetweenClicks(1);

				int time = 2000;
				smallSleepBetweenClicks(1);
				String regex = "Null";
				Utils.callSendkeys(driver, ActionXpath.email, Email, time, log);
				Utils.clickXpath(driver, ActionXpath.requestotp, time, "Request OTP", log);

				int count = 0;
				int maxTries = 7;
				String alertMessage = "";
				while (true) {
					try {
						Alert alert = driver.switchTo().alert(); // switch to alert
						alertMessage = driver.switchTo().alert().getText(); // capture alert message
						alert.accept();
						break;
					} catch (Exception e) {
						Utils.smallSleepBetweenClicks(1);
						if (++count > maxTries) {
							log.warning("Max retry of OTP reached");
							throw (e);
						}
					}

				}
				System.out.println(alertMessage); // Print Alert Message
				Pattern pt = Pattern.compile("-?\\d+");
				Matcher m = pt.matcher(alertMessage);
				while (m.find()) {
					regex = m.group();
				}

				Utils.callSendkeys(driver, ActionXpath.otprequest1, regex, time, log);
				Utils.clickXpath(driver, ActionXpath.verifyotp, time, "Verify otp", log);

			} else {
				if (Email.toLowerCase().contains("student")) {
					String studentuname = csvCell[2];
					String studentpassword = csvCell[10];

					Utils.callSendkeys(driver, ActionXpath.username, studentuname, time, log);
					Utils.callSendkeys(driver, ActionXpath.password, studentpassword, time, log);
					Utils.clickXpath(driver, ActionXpath.singnin, time, "Verify", log);
					Utils.smallSleepBetweenClicks(2);
					boolean quitDriver = false;
					quitDriver = driver
							.findElements(By.xpath("//*[text()='Either Username or password is incorrect.']"))
							.size() > 0;
					if (quitDriver) {
						log.warning("The Driver Is Quited Becaues oF Login Credential Is Invalid");
						driver.quit();
					} else {
						System.out.println("Login Working Fine");
					}

				} else if (Email.toLowerCase().contains("faculty")) {
					String facultyuname = csvCell[1];
					String facultypassword = csvCell[9];
					System.out.println("&*#^*^$**      " + facultyuname);
					System.out.println("&*#^*^$**      " + facultypassword);

					Utils.callSendkeys(driver, ActionXpath.username, facultyuname, time, log);
					Utils.callSendkeys(driver, ActionXpath.password, facultypassword, time, log);
					Utils.clickXpath(driver, ActionXpath.singnin, time, "Verify", log);
					Utils.smallSleepBetweenClicks(2);
					boolean quitDriver = false;
					quitDriver = driver
							.findElements(By.xpath("//*[text()='Either Username or password is incorrect.']"))
							.size() > 0;
					if (quitDriver) {
						log.warning("The Driver Is Quited Becaues oF Login Credential Is Invalied");
						driver.quit();
					} else {
						System.out.println("Login Working Fine");
					}

				}

			}
			System.out.println(
					"Sleeping after login for some seconds so that goBacktoHome function does not automatically logout user");
			bigSleepBetweenClicks(1);
			Thread.sleep(6000);

		} catch (

		Exception e) {
			log.warning("Login to portal failed Quitting the driver" + url);
			printException(e);
			// driver.quit();
			// System.exit(01);
		}

	}

	public static boolean isAlertPresent(WebDriver driver) {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException ex) {
			return false;
		}
	}

	@Test
	public static void logout(WebDriver driver, String url, String Role, Logger Log) throws Exception {
		try {
			driver.navigate().to(url);
			Utils.clickXpath(driver, ActionXpath.FCCportal, time, "Logout function Click on  initial", log);
			Utils.clickXpath(driver, ActionXpath.facsignOut, time, "Logout function click on signout", log);
			Utils.smallSleepBetweenClicks(2);
			if (Utils.checkIsLogoutWork(url)) {
				boolean signInPresent = false;
				signInPresent = driver.findElements(By.xpath("//*[text()='Sign in']")).size() > 0;
				if (signInPresent) {
					System.out.println("Sign In is present");
				} else {
					driver.navigate().refresh();
				}
			}

		} catch (Exception e) {
			driver.navigate().to(url);
			Utils.printException(e);
			System.out.println("Failure in logout function");
			log.info("Failure in Logout function");
			// Pfs_portal.quitDriver(driver, url);
			// throw (e);
		}
	}

	@Test
	public static Boolean checkIsLogoutWork(String url) {
		String urlToMatch = "portal-demo|sbmppsjal|bimtech|jdinstitutedelhi|nsom";
		Pattern pt = Pattern.compile(urlToMatch);
		Matcher m = pt.matcher(url);
		while (m.find()) {
			return true;
		}
		return false;
	}

	@Test
	public static Boolean otplogin(String url) {
		String urlToMatch = "portal-demo|bimtech|jdinstitutedelhi|nsom|portal-dev1|ltsta|portal-dev|ecampus";
		Pattern pt = Pattern.compile(urlToMatch);
		Matcher m = pt.matcher(url);
		while (m.find()) {
			return true;
		}
		return false;
	}

	@Test
	public static void goBackToHome(WebDriver driver, String url, Logger log) throws Exception {
		try {
			boolean alertPresent = false;
			bigSleepBetweenClicks(1);
			driver.navigate().to(url);
			alertPresent = isAlertPresent(driver);
			if (alertPresent) {
				driver.switchTo().alert().accept();
			}
		} catch (Exception e) {
			Utils.printException(e);
			System.out.println("Failure in go back to");
			log.warning("Failure in go back to home page");
			logout(driver, url, "Role", log);
			// driver.quit();
		}

	}

	@Test
	public static void smallSleepBetweenClicks(int loop) throws InterruptedException {
		int total_time = 2000 * loop;
		System.out.println("Sleeping for " + total_time);
		Thread.sleep(2000 * loop);
	}

	@Test
	public static void vsmallSleepBetweenClicks(int loop) throws InterruptedException {
		int total_time = 1000 * loop;
		System.out.println("Sleeping for " + total_time);
		Thread.sleep(1000 * loop);
	}

	@Test
	public static void bigSleepBetweenClicks(int loop) throws InterruptedException {
		int total_time = 1000 * loop;
		System.out.println("Sleeping for " + total_time);
		Thread.sleep(1000 * loop);
	}

	@Test
	public static void printException(Exception e) {
		log.warning("Exception is  " + e);
	}

	@Test
	public static String getTEXT(WebDriver driver, String xpath, Logger log, String msg) throws Exception {
		int count = 0;
		int maxTries = 7;
		String HtmlText = "";
		while (true) {
			try {
				if (debug)
					log.info("Get text for xpath element " + msg);
				WebElement p = driver.findElement(By.xpath(xpath));
				HtmlText = p.getText();
				return HtmlText;
			} catch (Exception e) {
				Utils.smallSleepBetweenClicks(1);
				if (++count > maxTries) {
					log.warning("Unable to get text for xPath " + msg);
					throw (e);
				}
			}
		}
	}

}