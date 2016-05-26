package me.alb_i986.selenium.junit.rules;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import me.alb_i986.selenium.WebDriverProvider;

/**
 * A {@link org.junit.rules.TestRule} taking a screenshot in case of test failure.
 */
public class TakeScreenshotOnFailureRule<X> extends TestWatcher {

    private final WebDriverProvider driverProvider;
    private final OutputType<X> outputType;

    // TODO rather than OutputType, should probably take a TestReporter or something..
    public TakeScreenshotOnFailureRule(WebDriverProvider driverProvider, OutputType<X> outputType) {
        if (driverProvider == null || outputType == null) {
            throw new IllegalArgumentException("The arguments should not be null");
        }
        this.driverProvider = driverProvider;
        this.outputType = outputType;
    }

    @Override
    protected void failed(Throwable failure, Description description) {
        WebDriver driver = driverProvider.getDriver();
        if (!(driver instanceof TakesScreenshot)) {
            // TODO log
            return;
        }

        try {
            X screenshot = ((TakesScreenshot) driver).getScreenshotAs(outputType);
            // TODO do something with the screenshot!
            System.out.println("<img src=\"data:image/png;base64," + screenshot + "\"" +
                    " alt=\"screenshot on failure for test '" +
                    description.getDisplayName() + "'\" />");
        } catch (WebDriverException e) {
            // TODO report that taking screenshot failed
        }
    }
}
