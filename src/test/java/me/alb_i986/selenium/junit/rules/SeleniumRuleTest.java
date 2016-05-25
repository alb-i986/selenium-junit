package me.alb_i986.selenium.junit.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import me.alb_i986.BaseMockitoTestClass;
import me.alb_i986.selenium.MockedDriverFactory;
import me.alb_i986.selenium.WebDriverFactory;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SeleniumRuleTest extends BaseMockitoTestClass {

    @Mock private WebDriver mockedDriver;

    @Test
    public void rulesShouldBeCalledInOrder() throws Throwable {
        TestLoggerRule testLoggerRule = Mockito.spy(TestLoggerRule.class);
        TakeScreenshotOnFailureRule screenshotOnFailureRule = Mockito.spy(TakeScreenshotOnFailureRule.class);
        WebDriverFactory mockedDriverFactory = Mockito.spy(WebDriverFactory.class);
        SeleniumRule sut = SeleniumRule.builder(mockedDriverFactory)
                .withTestLogger(testLoggerRule)
                .takeScreenshotOnFailure(screenshotOnFailureRule)
                .build();

        Statement s = sut.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw new RuntimeException("test failing");
            }
        }, Description.EMPTY);

        s.evaluate();

        Mockito.verify()
        InOrder inOrder = Mockito.inOrder(testLoggerRule, screenshotOnFailureRule, mockedDriverFactory);
        inOrder.verify(testLoggerRule).starting();
        inOrder.verify(mockedDriverFactory).create();
        inOrder.verify()
        inOrder.verify(screenshotOnFailureRule).failed(any(Throwable.class), any(Description.class));
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
