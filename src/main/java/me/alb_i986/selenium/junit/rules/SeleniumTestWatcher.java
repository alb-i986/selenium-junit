package me.alb_i986.selenium.junit.rules;

import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import me.alb_i986.selenium.WebDriverProvider;
import me.alb_i986.test.reporting.SeleniumReporter;
import me.alb_i986.test.reporting.UnitTestWatcher;

public class SeleniumTestWatcher extends UnitTestWatcher {

    private final WebDriverProvider driverProvider;
    private final SeleniumReporter reporter; // kinda duplicates the one defined in super but this has the more specific type SeleniumReporter

    public SeleniumTestWatcher(WebDriverProvider driverProvider, SeleniumReporter reporter) {
        super(reporter);
        this.driverProvider = driverProvider;
        this.reporter = reporter;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);

        WebDriver driver = driverProvider.getDriver();
        reporter.screenshot(driver);
        reporter.pageSource(driver);
    }

    private static <X> void takeScreenshot(WebDriver driver, OutputType<X> outputType) {
        if (!(driver instanceof TakesScreenshot)) {
            // TODO log
            return;
        }

        try {
            X screenshot = ((TakesScreenshot) driver).getScreenshotAs(outputType);
            // TODO do something with the screenshot!
            System.out.println(getImageHtml(screenshot.toString(), description));
        } catch (WebDriverException e) {
            // TODO report that taking screenshot failed
        }
    }

    private static String getImageHtml(String screenshotAsBase64, Description description) {
        return "<img src=\"data:image/png;base64," + screenshotAsBase64 + "\"" +
                " alt=\"screenshot on failure for test '" +
                description.getDisplayName() + "'\" />";
    }
}
