package me.alb_i986.test.reporting;

import org.openqa.selenium.WebDriver;

public interface SeleniumReporter extends Reporter {

    void pageSource(WebDriver driver);
    void screenshot(WebDriver driver);
}
