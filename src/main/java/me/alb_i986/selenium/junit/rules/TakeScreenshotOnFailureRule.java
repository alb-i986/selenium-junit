package me.alb_i986.selenium.junit.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.WrapsDriver;

/**
 * A {@link org.junit.rules.TestRule} taking a screenshot in case of test failure.
 */
public class TakeScreenshotOnFailureRule<X> extends TestWatcher {

    private final WrapsDriver driverWrapper;
    private final OutputType<X> outputType;

    // TODO rather than OutputType, should probably take a TestReporter or something..
    public TakeScreenshotOnFailureRule(WrapsDriver driverWrapper, OutputType<X> outputType) {
        if (driverWrapper == null || outputType == null) {
            throw new IllegalArgumentException("The arguments should not be null");
        }
        this.driverWrapper = driverWrapper;
        this.outputType = outputType;
    }

    /**
     * @throws org.openqa.selenium.WebDriverException if taking the screenshot fails
     */
    @Override
    protected void failed(Throwable e, Description description) {
        WebDriver driver = driverWrapper.getWrappedDriver();
        if (driver instanceof TakesScreenshot) {
            X screenshot = ((TakesScreenshot) driver).getScreenshotAs(outputType);
            System.out.println(screenshot);
            // TODO do something with screenshot
        } // TODO else report that the screenshot was not taken because blahblah
    }
}
