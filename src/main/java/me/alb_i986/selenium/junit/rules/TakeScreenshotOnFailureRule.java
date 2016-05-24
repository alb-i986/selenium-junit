package me.alb_i986.selenium.junit.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.internal.WrapsDriver;

/**
 * A {@link org.junit.rules.TestRule} taking a screenshot in case of test failure.
 */
public class TakeScreenshotOnFailureRule<X> extends TestWatcher {

    private final TakesScreenshot driver;
    private final OutputType<X> outputType;

    // TODO rather than OutputType, should probably take a TestReporter or something..
    public TakeScreenshotOnFailureRule(WrapsDriver driver, OutputType<X> outputType) {
        if (driver == null || outputType == null) {
            throw new IllegalArgumentException("The arguments should not be null");
        }
        if (!(driver instanceof TakesScreenshot)) {
            throw new IllegalArgumentException("The driver cannot take screenshots: it's not of type TakesScreenshot");
        }
        this.driver = (TakesScreenshot) driver;
        this.outputType = outputType;
    }

    /**
     * @throws org.openqa.selenium.WebDriverException if taking the screenshot fails
     */
    @Override
    protected void failed(Throwable e, Description description) {
        X screenshot = driver.getScreenshotAs(outputType);
        // TODO do something with screenshot
    }
}
