package me.alb_i986.selenium.junit.rules;

import org.junit.Test;
import org.junit.runner.Description;
import org.mockito.Mock;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import me.alb_i986.BaseMockitoTestClass;
import me.alb_i986.selenium.DriverProviderReturning;
import me.alb_i986.selenium.DummyDriverProvider;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class TakeScreenshotOnFailureRuleTest extends BaseMockitoTestClass {

    @Mock private WebDriverTakingScreenshot mockedDriverTakingScreenshots;

    @Test
    public void instantiationShouldFailGivenNullDriverProvider() {
        try {
            new TakeScreenshotOnFailureRule(null, OutputType.BASE64);
            fail("null WebDriverProvider in constructor should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The arguments should not be null"));
        }
    }

    @Test
    public void instantiationShouldFailGivenNullOutputType() {
        try {
            new TakeScreenshotOnFailureRule(new DummyDriverProvider(), null);
            fail("null OutputType in constructor should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The arguments should not be null"));
        }
    }

    @Test
    public void failedShouldDoNothingWhenDriverCantTakeScreenshots() {
        WebDriver mockedDriverNotTakingScreenshots = mock(WebDriver.class);
        TakeScreenshotOnFailureRule<String> sut = new TakeScreenshotOnFailureRule<>(
                new DriverProviderReturning(mockedDriverNotTakingScreenshots), OutputType.BASE64);

        sut.failed(new RuntimeException("failure (expected)"), Description.createTestDescription("class", "name"));

        verifyZeroInteractions(mockedDriverNotTakingScreenshots);
    }

    @Test
    public void failedShouldTakeScreenshotSuccessfullyWhenDriverCanTakeScreenshots() {
        OutputType<String> outputType = OutputType.BASE64;
        given(mockedDriverTakingScreenshots.getScreenshotAs(outputType)).willReturn("SCREENSHOT STUB");
        TakeScreenshotOnFailureRule<String> sut = new TakeScreenshotOnFailureRule<>(
                new DriverProviderReturning(mockedDriverTakingScreenshots), outputType);

        sut.failed(new RuntimeException("failure (expected)"), Description.createTestDescription("class", "name"));

        verify(mockedDriverTakingScreenshots).getScreenshotAs(outputType);
        // TODO verify some more when we'll have implemented some more logic in the SUT
    }

    @Test
    public void failedShouldNotThrowWhenGetScreenshotAsFails() {
        given(mockedDriverTakingScreenshots.getScreenshotAs(any(OutputType.class))).willThrow(WebDriverException.class);
        TakeScreenshotOnFailureRule<String> sut = new TakeScreenshotOnFailureRule<>(
                new DriverProviderReturning(mockedDriverTakingScreenshots), OutputType.BASE64);

        sut.failed(new RuntimeException("failure (expected)"), Description.createTestDescription("class", "name"));

        // then failed() should not throw
    }

    private interface WebDriverTakingScreenshot extends WebDriver, TakesScreenshot {}
}