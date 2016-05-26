package me.alb_i986.selenium;

import org.openqa.selenium.WebDriver;

/**
 * A {@link WebDriverProvider} returning the instance injected in the constructor.
 */
public class DriverProviderReturning implements WebDriverProvider {
    private final WebDriver driver;

    public DriverProviderReturning(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }
}
