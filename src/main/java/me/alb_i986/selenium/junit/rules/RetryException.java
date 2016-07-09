package me.alb_i986.selenium.junit.rules;

import org.junit.runner.Description;

import java.util.Arrays;
import java.util.List;

import me.alb_i986.selenium.junit.rules.utils.Throwables;

/**
 * Thrown by {@link RetryRule} when a test is retried and never passes.
 * <p>
 * Its message lists all of the test failures occurred while retrying,
 * along with the stacktrace.
 */
public class RetryException extends RuntimeException {

    public RetryException(Description description, Throwable... failures) {
        this(description, Arrays.asList(failures));
    }

    public RetryException(Description description, List<Throwable> failures) {
        super(createMessage(description, failures));
    }

    private static String createMessage(Description description, List<Throwable> failures) {
        if (failures.isEmpty()) {
            throw new IllegalArgumentException("The list of failures must not be empty");
        }
        return String.format("%s > Flaky test failed %d/%d times:\n%s",
                description, failures.size(), failures.size(), Throwables.getStacktraces(failures));
    }
}
