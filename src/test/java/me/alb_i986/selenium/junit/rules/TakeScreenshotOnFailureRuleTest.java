package me.alb_i986.selenium.junit.rules;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TakeScreenshotOnFailureRuleTest {

    @Mock private TakesScreenshot mockedDriver;

    @Test
    public void failedShouldTakeScreenshot() {
        OutputType<String> outputType = OutputType.BASE64;
        given(mockedDriver.getScreenshotAs(outputType)).willReturn("SCREENSHOT STUB");
        TakeScreenshotOnFailureRule<String> sut = new TakeScreenshotOnFailureRule<>(mockedDriver, outputType);

        Description dummyDesc = Description.createTestDescription("class", "name");
        Throwable dummyException = new Throwable("failure");
        sut.failed(dummyException, dummyDesc);

        verify(mockedDriver).getScreenshotAs(outputType);
    }

    @Test
    public void failedShouldFailWhenTakingScreenshotFails() {
        OutputType<String> outputType = OutputType.BASE64;
        given(mockedDriver.getScreenshotAs(outputType)).willThrow(WebDriverException.class);
        TakeScreenshotOnFailureRule<String> sut = new TakeScreenshotOnFailureRule<>(mockedDriver, outputType);

        Description dummyDesc = Description.createTestDescription("class", "name");
        Throwable dummyException = new Throwable("failure");
        try {
            sut.failed(dummyException, dummyDesc);
            Assert.fail("should throw");
        } catch (WebDriverException e) {
            // expected
        }
    }
}