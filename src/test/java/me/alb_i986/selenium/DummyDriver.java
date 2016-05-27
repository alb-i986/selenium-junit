package me.alb_i986.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

/**
 * {@link WebDriver} whose methods all throw {@link UnsupportedOperationException}.
 */
public class DummyDriver implements WebDriver {
    @Override
    public void get(String url) {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public String getCurrentUrl() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public List<WebElement> findElements(By by) {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public WebElement findElement(By by) {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public String getPageSource() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public void quit() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public Set<String> getWindowHandles() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public String getWindowHandle() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public TargetLocator switchTo() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public Navigation navigate() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }

    @Override
    public Options manage() {
        throw new UnsupportedOperationException("I'm a Dummy");
    }
}
