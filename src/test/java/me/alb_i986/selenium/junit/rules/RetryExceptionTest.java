package me.alb_i986.selenium.junit.rules;

import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static me.alb_i986.junit.Descriptions.defaultDescription;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class RetryExceptionTest {

    @Test
    public void givenEmptyListOfFailures_instantiationShouldFail() {
        try {
            new RetryException(defaultDescription(), Collections.<Throwable>emptyList());
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The list of failures must not be empty"));
        }
    }

    @Test
    public void givenNonEmptyListOfFailures_instantiationShouldSucceed() {
        new RetryException(defaultDescription(), new Throwable("failure"));
    }

    @Test
    public void messageShouldListAllOfTheFailuresWithTheStacktrace() {
        RetryException sut = new RetryException(defaultDescription(),
                new Throwable("failure1"), new Throwable("failure2"));

        assertThat(sut.getMessage(), stringContainsInOrder(asList(
                String.format("Flaky test '%s' failed %d time(s):", defaultDescription(), 2),
                "java.lang.Throwable: failure1\n" +
                        "\tat me.alb_i986.selenium.junit.rules.RetryExceptionTest.messageShouldListAllOfTheFailures",
                "java.lang.Throwable: failure2\n" +
                        "\tat me.alb_i986.selenium.junit.rules.RetryExceptionTest.messageShouldListAllOfTheFailures"))
        );
    }
}