package me.alb_i986.selenium.junit.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import me.alb_i986.junit.ExpectedException;
import me.alb_i986.selenium.MockedDriverFactory;
import me.alb_i986.selenium.WebDriverProvider;

import static me.alb_i986.junit.Descriptions.descriptionForFlakyTest;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class SeleniumRuleTest {

    @Test
    public void rulesShouldBeInvokedInCorrectOrder() throws Throwable {
        TestLoggerOnStartRule testLoggerOnStart = spy(new TestLoggerOnStartRule(Logger.getLogger("spied logger")));
        TestLoggerOnFinishRule testLoggerOnFinish = spy(new TestLoggerOnFinishRule(Logger.getLogger("spied logger")));
        TakeScreenshotOnFailureRule screenshotOnFailure = spy(new TakeScreenshotOnFailureRule(mock(WebDriverProvider.class), OutputType.BASE64));
        WebDriverResource driverResource = spy(new WebDriverResource(new MockedDriverFactory()));
        RetryRule retryRule = spy(new RetryRule(1));

        SeleniumRule sut = new SeleniumRule.Builder(driverResource)
                .withTestLoggerOnStart(testLoggerOnStart)
                .withTestLoggerOnFinish(testLoggerOnFinish)
                .toTakeScreenshotOnFailure(screenshotOnFailure)
                .toRetryOnFailure(retryRule)
                .build();

        // given a failing test
        ExpectedException exception = new ExpectedException("simulated test failure (expected)");
        Statement failingTest = mock(Statement.class);
        willThrow(exception).given(failingTest).evaluate();
        Description desc = Description.createTestDescription("test class", "test name");

        // run the compound statement made up of the test and the rules
        try {
            sut.apply(failingTest, descriptionForFlakyTest())
                    .evaluate();
            fail("WTF, the simulated test was supposed to fail");
        } catch (Throwable e) {
            // expected
            assertThat(e.getMessage(), containsString("simulated test failure (expected)"));
        }

        InOrder inOrder = inOrder(testLoggerOnStart, testLoggerOnFinish, screenshotOnFailure, driverResource, failingTest, retryRule);
        inOrder.verify(retryRule).apply(Mockito.any(Statement.class), Mockito.any(Description.class));
        inOrder.verify(testLoggerOnStart).starting(desc); // log "test started"
        inOrder.verify(driverResource).before(); // create driver
        inOrder.verify(failingTest).evaluate(); // run test
        inOrder.verify(testLoggerOnFinish).failed(exception, desc); // log "test failed"
        inOrder.verify(screenshotOnFailure).failed(exception, desc); // take screenshot on failure
        inOrder.verify(driverResource).after(); // close driver

        // the test is retried: verify all the mocks are called once again
        inOrder.verify(testLoggerOnStart).starting(desc); // log "test started"
        inOrder.verify(driverResource).before(); // create driver
        inOrder.verify(failingTest).evaluate(); // run test
        inOrder.verify(testLoggerOnFinish).failed(exception, desc); // log "test failed"
        inOrder.verify(screenshotOnFailure).failed(exception, desc); // take screenshot on failure
        inOrder.verify(driverResource).after(); // close driver
    }

    @Test
    public void integrationTest() {
        Result result = JUnitCore.runClasses(TestClassWithMinimalSeleniumRuleChain.class);

        assertThat(result.getFailures(), empty());
        assertThat(result.getRunCount(), equalTo(2));
        // TODO assert that each test got a different driver
    }

    public static class TestClassWithMinimalSeleniumRuleChain {
        @Rule
        public SeleniumRule seleniumRule = SeleniumRule.configure(new MockedDriverFactory())
                .withTestLogger(Logger.getLogger("asd"))
                .build();

        protected WebDriver driver() {
            return seleniumRule.getDriver();
        }

        @Test
        public void firstTest() {
            assertNotNull(driver());
        }

        @Test
        public void secondTest() {
            assertNotNull(driver());
        }
    }
}
