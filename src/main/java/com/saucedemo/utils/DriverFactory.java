package com.saucedemo.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates one WebDriver and caches the browser driver inside this project,
 * so the download follows whichever drive the project is stored on.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final String DRIVER_CACHE = System.getProperty("user.dir")
            + File.separator + "webdriver-cache";

    private DriverFactory() {
    }

    private static void setup(io.github.bonigarcia.wdm.WebDriverManager manager) {
        new File(DRIVER_CACHE).mkdirs();
        System.setProperty("wdm.cachePath", DRIVER_CACHE);
        manager.cachePath(DRIVER_CACHE).setup();
    }

    public static void initDriver(String browser) {
        if (driver.get() != null) {
            return;
        }

        String selectedBrowser = (browser == null || browser.isBlank())
                ? ConfigReader.getBrowser()
                : browser;

        WebDriver webDriver;

        switch (selectedBrowser.toLowerCase()) {
            case "firefox":
                setup(WebDriverManager.firefoxdriver());
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                webDriver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                setup(WebDriverManager.edgedriver());
                EdgeOptions edgeOptions = new EdgeOptions();
                webDriver = new EdgeDriver(edgeOptions);
                break;
            case "chrome":
            default:
                setup(WebDriverManager.chromedriver());
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-features=PasswordLeakDetection");
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                chromeOptions.setExperimentalOption("prefs", prefs);
                String chromeProfile = System.getProperty("user.dir") + File.separator + "chrome-profile";
                new File(chromeProfile).mkdirs();
                chromeOptions.addArguments("--user-data-dir=" + chromeProfile);
                webDriver = new ChromeDriver(chromeOptions);
                break;
        }

        webDriver.manage().deleteAllCookies();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        if (!selectedBrowser.equalsIgnoreCase("chrome")) {
            webDriver.manage().window().maximize();
        }

        driver.set(webDriver);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
