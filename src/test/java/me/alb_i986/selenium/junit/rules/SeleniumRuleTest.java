package me.alb_i986.selenium.junit.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;

import me.alb_i986.junit.SimulatedTestFailure;
import me.alb_i986.selenium.MockedDriverFactory;

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
        WebDriverResource driverResource = spy(new WebDriverResource(new MockedDriverFactory()));
        RetryRule retryRule = spy(new RetryRule(1));

        SeleniumRule sut = new SeleniumRule(driverResource)
                .toRetryFlakyTestsOnFailure(retryRule);

        // given a failing test
        SimulatedTestFailure exception = new SimulatedTestFailure("simulated test failure (expected)");
        Statement failingTest = mock(Statement.class);
        willThrow(exception).given(failingTest).evaluate();

        Description desc = descriptionForFlakyTest();

        // run the compound statement made up of the test and the rules
        try {
            sut.apply(failingTest, desc)
                    .evaluate();
            fail("WTF?! the simulated test was supposed to fail");
        } catch (Throwable e) {
            // expected
            assertThat(e.getMessage(), containsString("simulated test failure (expected)"));
        }

        InOrder inOrder = inOrder(driverResource, failingTest, retryRule);
        inOrder.verify(retryRule).apply(Mockito.any(Statement.class), Mockito.any(Description.class));
        inOrder.verify(driverResource).before(); // create driver
        inOrder.verify(failingTest).evaluate(); // run test
        inOrder.verify(driverResource).after(); // close driver

        // the test is retried: verify all the mocks are called once again, in the correct order
        inOrder.verify(driverResource).before(); // create driver
        inOrder.verify(failingTest).evaluate(); // run test
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
        public SeleniumRule seleniumRule = new SeleniumRule(new MockedDriverFactory());

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
