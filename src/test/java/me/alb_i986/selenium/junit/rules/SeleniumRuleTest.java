package me.alb_i986.selenium.junit.rules;

import me.alb_i986.BaseMockitoTestClass;
import me.alb_i986.selenium.DummyDriver;
import me.alb_i986.selenium.MockedDriverFactory;
import me.alb_i986.selenium.WebDriverFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.Mock;
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

public class SeleniumRuleTest extends BaseMockitoTestClass {

    @Mock private WebDriver mockedDriver;

    @Test
    public void rulesShouldBeCalledInOrder() throws Throwable {
        TestLoggerRule testLoggerRule = spy(new TestLoggerRule(Logger.getLogger("spied logger")));
        TakeScreenshotOnFailureRule screenshotOnFailureRule = spy(new TakeScreenshotOnFailureRule(new DummyDriver(), OutputType.BASE64));
        WebDriverFactory driverFactory = mock(WebDriverFactory.class);

        SeleniumRule sut = SeleniumRule.builder(driverFactory)
                .withTestLogger(testLoggerRule)
                .takeScreenshotOnFailure(screenshotOnFailureRule)
                .build();

        final RuntimeException exception = new RuntimeException("test failing");
        Statement failingTest = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw exception;
            }
        };
        Description desc = Description.createTestDescription("test class", "test name");
        Statement s = sut.apply(failingTest, desc);

        s.evaluate();

        InOrder inOrder = inOrder(testLoggerRule, screenshotOnFailureRule, driverFactory);
        inOrder.verify(testLoggerRule).starting(desc);
        inOrder.verify(driverFactory).create();
        inOrder.verify(screenshotOnFailureRule).failed(exception, desc);
        inOrder.verify(testLoggerRule).failed(exception, desc);
    }

    private static class BuilderTestSubclass extends SeleniumRule.Builder {
        protected BuilderTestSubclass(WebDriverResource driverResource) {
            super(driverResource);
        }
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
        public SeleniumRule seleniumRule = SeleniumRule.builder(new MockedDriverFactory())
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
