package com.saucedemo.base;

import com.saucedemo.listeners.ExtentTestListener;
import com.saucedemo.utils.ConfigReader;
import com.saucedemo.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * One browser for every test in the class.
 * It opens before the first test and closes after the last test.
 */
@Listeners({ExtentTestListener.class})
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        String browserToUse = (browser != null && !browser.isBlank())
                ? browser
                : ConfigReader.getBrowser();

        DriverFactory.initDriver(browserToUse);
        driver = DriverFactory.getDriver();
        driver.get(ConfigReader.getBaseUrl());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
