package me.alb_i986.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Dummy {@link WebDriverProvider}, whose methods all throw {@link UnsupportedOperationException}.
 */
public class DummyDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver getDriver() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }
}
