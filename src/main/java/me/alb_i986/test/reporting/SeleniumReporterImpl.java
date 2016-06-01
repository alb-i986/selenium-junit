package me.alb_i986.test.reporting;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static javafx.scene.input.KeyCode.X;

/**
 * @author ascotto
 */
public class SeleniumReporterImpl implements SeleniumReporter {
    @Override
    public void pageSource(WebDriver driver) {
        try {
            String pageSource = driver.getPageSource();
            writer.write(formatter.format(pageSource));
        } catch (WebDriverException e) {
            // TODO report error
        }
    }

    @Override
    public void screenshot(WebDriver driver) {
        if (!(driver instanceof TakesScreenshot)) {
            // TODO report
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
}
