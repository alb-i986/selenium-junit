package me.alb_i986.selenium;

import org.openqa.selenium.WebDriver;

public interface WebDriverProvider {

    /**
     * @return a non-null driver
     * @throws IllegalStateException if the driver is null
     */
    WebDriver getDriver();
}
