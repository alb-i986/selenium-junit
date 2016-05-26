package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.MockedDriverFactory;
import me.alb_i986.selenium.WebDriverProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class SeleniumRuleTest {

    @Test
    public void rulesShouldBeInvokedInCorrectOrder() throws Throwable {
        TestLoggerRule testLoggerRule = spy(new TestLoggerRule(Logger.getLogger("spied logger")));
        TakeScreenshotOnFailureRule screenshotOnFailureRule = spy(new TakeScreenshotOnFailureRule(mock(WebDriverProvider.class), OutputType.BASE64));
        WebDriverResource driverResource = spy(new WebDriverResource(new MockedDriverFactory()));

        SeleniumRule sut = new SeleniumRule.Builder(driverResource)
                .withTestLogger(testLoggerRule)
                .toTakeScreenshotOnFailure(screenshotOnFailureRule)
                .build();

        final RuntimeException exception = new ExpectedException("test failing (expected)");
        Statement failingTest = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw exception;
            }
        };
        Description desc = Description.createTestDescription("test class", "test name");
        Statement s = sut.apply(failingTest, desc);

        try {
            s.evaluate();
        } catch (ExpectedException e) {
            // expected
        }

        InOrder inOrder = inOrder(testLoggerRule, screenshotOnFailureRule, driverResource);
        inOrder.verify(testLoggerRule).starting(desc); // log "test started"
        inOrder.verify(driverResource).before(); // create driver
        inOrder.verify(testLoggerRule).failed(exception, desc); // log "test failed"
        inOrder.verify(screenshotOnFailureRule).failed(exception, desc); // take screenshot on failure
    }

    @Test
    public void integrationTest() {
        Result result = JUnitCore.runClasses(TestClassWithMinimalSeleniumRuleChain.class);

        assertTrue(result.wasSuccessful());
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

    private static class ExpectedException extends RuntimeException {
        public ExpectedException(String message) {
            super(message);
        }
    }
}
