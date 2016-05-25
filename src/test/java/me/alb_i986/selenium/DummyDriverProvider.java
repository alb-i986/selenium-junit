package me.alb_i986.selenium;

import me.alb_i986.selenium.junit.rules.WebDriverProvider;
import org.openqa.selenium.WebDriver;

/**
 * {@link WebDriverProvider} whose methods all throw {@link UnsupportedOperationException}.
 */
public class DummyDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver getDriver() {
        throw new UnsupportedOperationException("I'm dummy");
    }
}
