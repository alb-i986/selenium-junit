package me.alb_i986.selenium;

import me.alb_i986.selenium.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

/**
 * Dummy factory whose methods all throw {@link UnsupportedOperationException}.
 */
public class DummyDriverFactory implements WebDriverFactory {
    @Override
    public WebDriver create() {
        throw new UnsupportedOperationException("I'm dummy");
    }
}
