package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.MockedDriverFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SeleniumRuleTest {

    @Test
    public void rulesCanUseTheDriverInitializedByWebDriverResource() throws Throwable {
        SeleniumRule sut = SeleniumRule.builder(new MockedDriverFactory())
                .appendRule(new TestLoggerRule(Logger.getLogger("asd")))
                .build();
        Statement s = sut.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {

                System.out.println("test running");
            }
        }, Description.EMPTY);
        s.evaluate();
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
        public SeleniumRule seleniumRule = SeleniumRule.builder(new MockedDriverFactory()).build();

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
