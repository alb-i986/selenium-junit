package me.alb_i986.selenium.junit.rules;

import me.alb_i986.BaseMockitoTestClass;
import me.alb_i986.selenium.DummyDriver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.mockito.Mock;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;

public class TakeScreenshotOnFailureRuleTest extends BaseMockitoTestClass {

    @Mock private WebDriverTakingScreenshot mockedDriver;

    @Test
    public void instantiationShouldFailGivenNullDriver() {
        try {
            new TakeScreenshotOnFailureRule(null, OutputType.BASE64);
            fail("null WebDriver in constructor should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The arguments should not be null"));
        }
    }

    @Test
    public void instantiationShouldFailGivenNullOutputType() {
        try {
            new TakeScreenshotOnFailureRule(new DummyDriver(), null);
            fail("null OutputType in constructor should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The arguments should not be null"));
        }
    }

    @Test
    public void instantiationShouldFailGivenWebDriverWhichCannotTakeScreenshots() {
        try {
            new TakeScreenshotOnFailureRule(new DummyDriver(), OutputType.BASE64);
            fail("WebDriver which is not a TakesScreenshot should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The driver cannot take screenshots: it's not of type TakesScreenshot"));
        }
    }

    @Test
    public void failedShouldTakeScreenshotSuccessfully() {
        OutputType<String> outputType = OutputType.BASE64;
        given(mockedDriver.getScreenshotAs(outputType)).willReturn("SCREENSHOT STUB");
        TakeScreenshotOnFailureRule<String> sut = new TakeScreenshotOnFailureRule<>(mockedDriver, outputType);

        Description dummyDesc = Description.createTestDescription("class", "name");
        Throwable dummyException = new Throwable("failure");
        sut.failed(dummyException, dummyDesc);

        verify(mockedDriver).getScreenshotAs(outputType);
    }

    @Test
    public void failedShouldThrowWhenGetScreenshotAsFails() {
        given(mockedDriver.getScreenshotAs(any(OutputType.class))).willThrow(WebDriverException.class);
        TakeScreenshotOnFailureRule<String> sut = new TakeScreenshotOnFailureRule<>(mockedDriver, OutputType.BASE64);

        try {
            sut.failed(new Throwable("failure"), Description.createTestDescription("class", "name"));

            Assert.fail("failed() should throw when getScreenshotAs() fails");
        } catch (WebDriverException e) {
            // expected
        }
    }

    private interface WebDriverTakingScreenshot extends WebDriver, TakesScreenshot {}
}