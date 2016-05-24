package me.alb_i986.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;

import java.util.List;
import java.util.Set;

/**
 * I'm dummy whose methods all throw {@link UnsupportedOperationException}.
 */
public class DummyDriver implements WebDriver, WrapsDriver {
    @Override
    public void get(String url) {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public String getCurrentUrl() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public List<WebElement> findElements(By by) {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public WebElement findElement(By by) {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public String getPageSource() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public void quit() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public Set<String> getWindowHandles() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public String getWindowHandle() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public TargetLocator switchTo() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public Navigation navigate() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public Options manage() {
        throw new UnsupportedOperationException("I'm dummy");
    }

    @Override
    public WebDriver getWrappedDriver() {
        throw new UnsupportedOperationException("I'm dummy");
    }
}
