package me.alb_i986.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Dummy {@link WebDriverFactory}, whose methods all throw {@link UnsupportedOperationException}.
 */
public class DummyDriverFactory implements WebDriverFactory {
    @Override
    public WebDriver create() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }
}
