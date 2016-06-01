package me.alb_i986.selenium.junit.rules;

import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

/**
 * @author ascotto
 */
public class SeleniumTestLoggerRule extends TestLoggerRule {

    protected final WebDriver driver;

    public SeleniumTestLoggerRule(Logger logger, WebDriver driver) {
        super(logger);
        this.driver = driver;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        reporter.warn(driver.getPageSource());
    }

    @Override
    protected void finished(Description description) {
        reporter.info(driver.getPageSource());
    }
}
