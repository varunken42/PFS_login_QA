package com.ken42;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.opencsv.CSVReader;
import java.util.logging.*;
import java.util.logging.FileHandler;
import java.text.SimpleDateFormat;

public class Pfs_portal extends Thread {
	private String[] csvLineData;
	private int count;
	private static final Exception Exception = null;
	static int time = 1000;
	static Boolean headless;
	public Logger log = Logger.getLogger("Pfs_portal");
	public static boolean faculty_login_set = false;
	public static boolean student_login_set = false;

	@Override
	public void run() {
		System.out.println("Thread- Started" + Thread.currentThread().getName());
		String threadname = Thread.currentThread().getName();
		System.out.println(threadname);
		try {
			Thread.sleep(1000);
			testPFSPortal(this.csvLineData, this.count);
		} catch (InterruptedException e) {
			log.info("Thread- Exception " + Thread.currentThread().getName());
			System.out.println("Thread- Exception " + Thread.currentThread().getName());
			e.printStackTrace();
		} catch (java.lang.Exception e) {
			log.info("Thread- Exception " + Thread.currentThread().getName());
			System.out.println("Thread- Exception " + Thread.currentThread().getName());
			e.printStackTrace();
		}
		log.info("Thread- END " + Thread.currentThread().getName());
		System.out.println("Thread- END " + Thread.currentThread().getName());
	}

	public Pfs_portal(String[] csvCell, int count, Logger log) {
		this.csvLineData = csvCell;
		this.count = count;
		this.log = log;
	}

	public static void main(String[] args) throws Exception {
		Logger log = Logger.getLogger("Pfs_portal");
		String folder = "";
		folder = getFolderPath();
		String CSV_PATH = "";
		CSV_PATH = folder + "\\pfs.csv";
		CSVReader csvReader1;
		int ThreadCount = 0;
		csvReader1 = new CSVReader(new FileReader(CSV_PATH));
		String[] csvCell1;
		while ((csvCell1 = csvReader1.readNext()) != null) {
			ThreadCount++;
		}
		System.out.println("Number of threads to start  " + ThreadCount);
		Thread[] threads = new Thread[ThreadCount];

		CSVReader csvReader;
		int count = 0;
		csvReader = new CSVReader(new FileReader(CSV_PATH));

		String[] csvCell;
		while ((csvCell = csvReader.readNext()) != null) {
			// if (count == 0) {
			// count = count + 1;
			// continue;
			// }
			// testPFSPortal(csvCell);
			Thread t = new Pfs_portal(csvCell, count, log);
			threads[count] = t;
			threads[count].setName("T" + String.valueOf(count + 1));
			t.start();
			Utils.smallSleepBetweenClicks(8);
			count++;
		}
		// SendMail.sendEmail(logFileName);
	}

	// Created a function so this can be threaded
	public static void testPFSPortal(String[] csvCell, int count) throws Exception {
		Logger log = Logger.getLogger("Pfs_portal" + count);
		String folder = "";
		folder = getFolderPath();
		String logFileName = "";
		boolean append = false;
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
		logFileName = String.format(folder + "\\Testresult_%s.HTML", timeStamp);
		FileHandler logFile = new FileHandler(logFileName, append);
		logFile.setFormatter(new MyHtmlFormatter());
		log.addHandler(logFile);
		WebDriver driver = null;

		// boolean faculty_login_set = false;
		// boolean student_login_set = false;
		String PFSurl = csvCell[0];
		String facultyEmail = csvCell[1];
		String studentEmail = csvCell[2];
		String Role = "";
		String Browser = csvCell[3];
		String From = csvCell[4];
		String To = csvCell[5];
		String studentName = csvCell[6];
		// String headless = csvCell[7];
		int from = Integer.parseInt(From);
		int to = Integer.parseInt(To);
		headless = getHeadless(csvCell);
		if ((from < 1 || from > 1 || to < 2 || to > 2) || (to < from)) {
			log.warning("The range specificed is incorrect it has to be between 1 and 68");
			log.warning("Please correct the From and To Columns in CSV file and run again");
			System.exit(1);
		}

		driver = initDriver(Browser, PFSurl);

		if ((from >= 1 && to <= 1)) {
			Role = "student";
			student_login_set = true;
		} else if ((from >= 2 && to <= 2)) {
			Role = "faculty";
			faculty_login_set = true;
		}

		log.info("**********************Testing for  Portal  " + PFSurl);
		for (int i = from; i <= to; i++) {
			switch (i) {
				case 1:
					Utils.login(driver, studentEmail, PFSurl, log, csvCell);
					Utils.logout(driver, PFSurl, Role, log);
					break;
				case 2:
					Utils.login(driver, facultyEmail, PFSurl, log, csvCell);
					Utils.logout(driver, PFSurl, Role, log);
					break;
				default:
					throw Exception;
			}
		}

		quitDriver(driver, PFSurl);
		log.info("***************** COMPLETED TESTTING OF PORTAL" + PFSurl);
		SendMail.sendEmail(logFileName);
	}

	@BeforeSuite
	public static WebDriver initDriver(String Browser, String url) throws Exception {

		try {
			WebDriver driver = null;
			String folder = "";
			folder = getFolderPath();
			String ChromeDriver = folder + "\\chromedriver.exe";
			String EdgeDriver = folder + "\\msedgedriver.exe";
			String FirefoxDriver = folder + "\\geckodriver.exe";
			System.out.println("Browser is ****" + Browser);
			System.out.println("URL is " + url);
			if ("chrome".equals(Browser)) {
				System.setProperty("webdriver.chrome.driver", ChromeDriver);
				ChromeOptions op = new ChromeOptions();
				if (headless) {
					op.addArguments("--headless", "--window-size=1920,1080");
				} else {
					op.addArguments("--disable-notifications");
				}
				// op.addArguments("--headless");
				WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver(op);
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			} else if ("edge".equals(Browser)) {
				System.setProperty("webdriver.edge.driver", EdgeDriver);
				driver = new EdgeDriver();
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			} else if ("firefox".equals(Browser)) {
				System.setProperty("webdriver.edge.driver", FirefoxDriver);
				FirefoxOptions fx = new FirefoxOptions();
				fx.addArguments("--disable-notifications");
				WebDriverManager.firefoxdriver().setup();
				driver = new FirefoxDriver(fx);
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			} else if ("safari".equals(Browser)) {
				driver = new SafariDriver();
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			}
			System.out.println("********************" + url);
			if (driver != null) {
				driver.get(url);
				driver.manage().window().maximize();
				return (driver);
			}
		} catch (Exception e) {
			Utils.printException(e);
			// log.warning("UNABLE TO LAUNCH BROWSER");
			System.exit(01);
		}
		return null;
	}

	@AfterSuite
	public static void quitDriver(WebDriver driver, String Url) throws Exception {
		// log.info("Completed testing of portal" + Url);
		System.out.println("Qutting driver");
		driver.quit();
		Utils.smallSleepBetweenClicks(1);
	}

	public static String getFolderPath() throws Exception {
		try {
			String folder = "";
			InputStream folderPath = Pfs_portal.class.getResourceAsStream("folder.csv");
			CSVReader csvFolderPath = new CSVReader(new InputStreamReader(folderPath, "UTF-8"));
			String[] csvCell_folder;
			while ((csvCell_folder = csvFolderPath.readNext()) != null) {
				folder = csvCell_folder[0];
			}
			System.out.println(folder);
			return folder;
		} catch (Exception e) {
			Utils.printException(e);
		}
		return null;
	}

	public static Boolean getHeadless(String[] csvCell) throws Exception {
		try {
			String headless = csvCell[7];
			if ("TRUE".equals(headless)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Utils.printException(e);
		}
		return false;
	}
}