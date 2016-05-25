package me.alb_i986.selenium.junit.rules;

import org.openqa.selenium.WebDriver;

public interface WebDriverProvider {

    /**
     * @return a non-null driver
     * @throws IllegalStateException if the driver has not been initialized yet
     */
    WebDriver getDriver();
}
