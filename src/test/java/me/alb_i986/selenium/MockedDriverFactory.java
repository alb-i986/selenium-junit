package me.alb_i986.selenium;

import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;

/**
 * A {@link WebDriverFactory} returning mocks of {@link WebDriver}.
 */
public class MockedDriverFactory implements WebDriverFactory {
    @Override
    public WebDriver create() {
        return mock(WebDriver.class);
    }
}
