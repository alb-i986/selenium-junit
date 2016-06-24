package me.alb_i986.selenium.junit.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.mockito.Mock;

import me.alb_i986.BaseMockitoTestClass;
import me.alb_i986.junit.SimulatedTestFailure;

import static java.util.Arrays.asList;
import static me.alb_i986.junit.Descriptions.defaultDescription;
import static me.alb_i986.junit.Descriptions.descriptionForFlakyTest;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RetryRuleTest extends BaseMockitoTestClass {

    @Mock
    private Statement mockedStatement;

    @Test
    public void instantiationShouldFailGivenNegativeInteger() {
        try {
            new RetryRule(-1);
            fail("given negative integer, instantiation should fail");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                    equalTo("The number of retries needs to be an integer >= 0 but was: -1"));
        }
    }

    @Test
    public void instantiationShouldSucceedGivenIntegerGreaterThanOrEqualToZero() {
        new RetryRule(0);
        new RetryRule(1);
    }

    @Test
    public void givenZeroTimesRetryRule_failingFlakyTestShouldNotBeRetried() throws Throwable {
        // given a failing statement
        willThrow(new SimulatedTestFailure())
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(0);

        // when
        try {
            sut.apply(mockedStatement, descriptionForFlakyTest())
                    .evaluate();
            fail("the statement was supposed to fail");
        } catch (SimulatedTestFailure e) {
            // expected
        }

        verify(mockedStatement, times(1)).evaluate();
    }

    @Test
    public void givenZeroTimesRetryRule_failingNonFlakyTestShouldNotBeRetried() throws Throwable {
        // given a failing statement
        willThrow(new SimulatedTestFailure())
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(0);

        // when
        try {
            sut.apply(mockedStatement, defaultDescription())
                    .evaluate();
            fail("the statement was supposed to fail");
        } catch (SimulatedTestFailure e) {
            // expected
        }

        verify(mockedStatement, times(1)).evaluate();
    }

    @Test
    public void givenZeroTimesRetryRule_passingFlakyTestShouldNotBeRetried() throws Throwable {
        RetryRule sut = new RetryRule(0);

        // when
        sut.apply(mockedStatement, descriptionForFlakyTest())
                .evaluate();

        verify(mockedStatement, times(1)).evaluate();
    }

    @Test
    public void nonFlakyTestsShouldNotBeRetried() throws Throwable {
        // given a failing statement
        willThrow(new SimulatedTestFailure())
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(2);

        // when
        try {
            sut.apply(mockedStatement, defaultDescription())
                    .evaluate();
            fail("the statement was supposed to fail");
        } catch (SimulatedTestFailure e) {
            // expected: should propagate the original exception
        }

        verify(mockedStatement, times(1)).evaluate();
    }

    @Test
    public void flakyTestsPassingTheFirstTimeShouldNotBeRetried() throws Throwable {
        RetryRule sut = new RetryRule(2);

        // when
        sut.apply(mockedStatement, descriptionForFlakyTest())
                .evaluate();

        verify(mockedStatement, times(1)).evaluate();
    }

    @Test
    public void flakyTestsFailingOnlyTheFirstTimeShouldBeRetriedOnlyOnce() throws Throwable {
        willThrow(new SimulatedTestFailure())
                .willNothing() // the test passes from the second time on
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(2);

        // when
        sut.apply(mockedStatement, descriptionForFlakyTest())
                .evaluate();

        verify(mockedStatement, times(2)).evaluate();
    }

    @Test
    public void shouldThrowRetryExceptionWhenFlakyTestFailsAllTheTimes() throws Throwable {
        willThrow(new SimulatedTestFailure("failure 1"))
                .willThrow(new SimulatedTestFailure("failure 2"))
                .willThrow(new SimulatedTestFailure("failure 3"))
                .willThrow(new SimulatedTestFailure("failure N"))
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(2);

        // when
        try {
            sut.apply(mockedStatement, descriptionForFlakyTest())
                    .evaluate();
            fail("should throw RetryException");
        } catch (RetryException e) { // expected
            assertThat(e.getMessage(), stringContainsInOrder(asList(
                    "failure 1", "failure 2", "failure 3")));
        }

        verify(mockedStatement, times(3)).evaluate();
    }

    @Test
    public void shouldNotRetryOnOOMError() throws Throwable {
        willThrow(new OutOfMemoryError("simulated OOMError"))
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(2);

        // when
        try {
            sut.apply(mockedStatement, descriptionForFlakyTest())
                    .evaluate();
            fail("should propagate OOMError");
        } catch (OutOfMemoryError e) {
            // expected
        }

        verify(mockedStatement, times(1)).evaluate();
    }

    @Test
    public void shouldRetryOnAssertionError() throws Throwable {
        willThrow(new AssertionError("simulated AssertionError"))
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(2);

        // when
        try {
            sut.apply(mockedStatement, descriptionForFlakyTest())
                    .evaluate();
        } catch (Throwable e) {
            // expected
        }

        verify(mockedStatement, times(3)).evaluate();
    }

    @Test
    public void shouldNotRetryOnAssumptionViolatedException() throws Throwable {
        willThrow(new AssumptionViolatedException("simulated AssumptionViolatedException"))
                .given(mockedStatement).evaluate();

        RetryRule sut = new RetryRule(2);

        // when
        try {
            sut.apply(mockedStatement, descriptionForFlakyTest())
                    .evaluate();
            fail("should propagate AssumptionViolatedException");
        } catch (AssumptionViolatedException e) {
            // expected
        }

        verify(mockedStatement, times(1)).evaluate();
    }

    @Test
    public void integrationTest() {
        Result result = JUnitCore.runClasses(TestClassWithRetryRule.class);

        assertThat(result.getFailures(), hasSize(2));
        assertThat(TestClassWithRetryRule.flakyAlwaysFailingTestRunCount, equalTo(3));
        assertThat(TestClassWithRetryRule.flakyTestFailingTheFirstTimeRunCount, equalTo(2));
        assertThat(TestClassWithRetryRule.nonFlakyAlwaysFailingTestRunCount, equalTo(1));
        assertThat(TestClassWithRetryRule.nonFlakyPassingTestRunCount, equalTo(1));
        assertThat(TestClassWithRetryRule.flakyPassingTestRunCount, equalTo(1));
    }

    public static class TestClassWithRetryRule {

        public static int flakyAlwaysFailingTestRunCount = 0;
        public static int nonFlakyAlwaysFailingTestRunCount = 0;
        public static int flakyTestFailingTheFirstTimeRunCount = 0;
        public static int nonFlakyPassingTestRunCount = 0;
        public static int flakyPassingTestRunCount = 0;

        @Rule
        public RetryRule retryRule = new RetryRule(2);

        @Test
        public void nonFlakyAlwaysFailingTest() {
            nonFlakyAlwaysFailingTestRunCount++;
            throw new SimulatedTestFailure("simulated failure for nonFlakyAlwaysFailingTest");
        }

        @Flaky
        @Test
        public void flakyAlwaysFailingTest() {
            flakyAlwaysFailingTestRunCount++;
            throw new SimulatedTestFailure("simulated failure for flakyAlwaysFailingTest");
        }

        @Flaky
        @Test
        public void flakyTestFailingTheFirstTime() {
            if (flakyTestFailingTheFirstTimeRunCount++ == 0) {
                throw new SimulatedTestFailure("simulated failure for flakyTestFailingTheFirstTime");
            }
        }

        @Test
        public void nonFlakyPassingTest() {
            nonFlakyPassingTestRunCount++;
        }

        @Flaky
        @Test
        public void flakyPassingTest() {
            flakyPassingTestRunCount++;
        }
    }
}