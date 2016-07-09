package me.alb_i986.selenium.junit.rules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.alb_i986.selenium.junit.rules.utils.Throwables;

/**
 * A rule which, in case of failure, re-runs a test annotated with {@link Flaky} until it passes,
 * for max {@code n} times (max {@code n+1} executions in total).
 * <p>
 * If a test doesn't pass after {@code n+1} executions, a {@link RetryException} is thrown,
 * whose message contains all of the failures occurred.
 * <p>
 * A test is retried <i>unless</i>:
 * <ul>
 *     <li>The test is <i>not</i> annotated with {@link Flaky}</li>
 *     <li>The test throws {@link AssumptionViolatedException}</li>
 *     <li>The test throws an {@link Error}, excluding {@link AssertionError},
 *     so that OOM errors are always propagated ASAP</li>
 *     <li>The configured retry times is 0 (i.e. {@code new RetryRule(0)}),
 *     in which case any test will be executed only once,
 *     as if there was no {@link RetryRule} in place</li>
 * </ul>
 * <h3>Example</h3>
 * <pre>
 * &#064;Rule TestRule retryRule = new RetryRule(1);
 * </pre>
 *
 * Given the rule above, this is what happens for each test run:
 * <ul>
 *     <li>If the first execution is successful, that's it</li>
 *     <li>Otherwise, the test will be run once again</li>
 *     <li>If the second execution is still not successful,
 *     then a {@link RetryException} will be thrown,
 *     with the two test failures occurred attached to the exception message</li>
 * </ul>
 */
public class RetryRule implements TestRule {

    private static final Logger LOGGER = Logger.getLogger(RetryRule.class.getName());

    private final int maxExecutions;

    /**
     * @param retryTimes how many times to retry, <i>after the first attempt</i>.
     *                For example, {@code new RetryRule(1)} will run a test max 2 times.
     */
    public RetryRule(int retryTimes) {
        if (retryTimes < 0) {
            throw new IllegalArgumentException("The number of retries needs to be an integer >= 0" +
                    " but was: " + retryTimes);
        }
        this.maxExecutions = retryTimes + 1;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // DO NOT retry if the test is not marked as flaky,
                // or this rule has been configured to retry for 0 times
                if (maxExecutions == 1 || !isTestFlaky()) {
                    base.evaluate();
                    return;
                }

                List<Throwable> failures = new ArrayList<>();
                int i;
                for (i = 0; i < maxExecutions; i++) {
                    try {
                        base.evaluate();
                        break;
                    } catch (AssertionError e) {
                        failures.add(e);
                    } catch (Error error) {
                        throw error;
                    } catch (AssumptionViolatedException e) {
                        throw e;
                    } catch (Throwable t) {
                        failures.add(t);
                    }

                    LOGGER.info(String.format("%s > Flaky test failed" +
                                    (i < maxExecutions - 1 ? ". Retrying.." : "") +
                                    " (%d/%d executions so far)",
                            description, i + 1, maxExecutions));
                }

                if (!failures.isEmpty()) { // the test was retried
                    if (i == maxExecutions) { // the test failed all the times
                        throw new RetryException(description, failures);
                    } else { // the test failed a few times but eventually passed
                        LOGGER.warning(String.format("%s > Flaky test eventually passed after %d/%d executions. " +
                                "Test failures occurred:\n%s",
                                description, i + 1, maxExecutions, Throwables.getStacktraces(failures)));
                    }
                }
            }

            private boolean isTestFlaky() {
                return description.getAnnotation(Flaky.class) != null;
            }
        };
    }
}