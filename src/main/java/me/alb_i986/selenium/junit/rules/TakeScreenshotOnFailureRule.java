package me.alb_i986.selenium.junit.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * @author ascotto
 */
public class TakeScreenshotOnFailureRule<X> extends TestWatcher {

    private final TakesScreenshot driver;
    private final OutputType<X> outputType;

    public TakeScreenshotOnFailureRule(TakesScreenshot driver, OutputType<X> outputType) {
        if (driver == null) {
            throw new IllegalArgumentException("the driver should not be null");
        }
        this.driver = driver;
        this.outputType = outputType;
    }

    /**
     * @throws org.openqa.selenium.WebDriverException if taking the screenshot fails
     */
    @Override
    protected void failed(Throwable e, Description description) {
        X screenshot = driver.getScreenshotAs(outputType);
    }
}
