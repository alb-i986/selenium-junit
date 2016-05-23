package me.alb_i986.selenium;

import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;

public class MockedDriverFactory implements WebDriverFactory {
    @Override
    public WebDriver create() {
        return mock(WebDriver.class);
    }
}
