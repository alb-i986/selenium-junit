package me.alb_i986.selenium.junit.rules;

import org.junit.runner.Description;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Thrown by {@link RetryRule} when a test is retried and never passes.
 * <p>
 * Its message lists all of the test failures which occurred while retrying,
 * with the stacktrace.
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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Flaky test '%s' failed %d times:\n", description.getDisplayName(), failures.size()));
        int i = 1;
        for (Throwable failure : failures) {
            sb.append(String.format("\n  %d. %s", i++, getStackTrace(failure)));
        }
        return sb.toString();
    }

    // TODO with JUnit 4.13, we'll be able to use Throwables.getStacktrace() instead of this
    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
