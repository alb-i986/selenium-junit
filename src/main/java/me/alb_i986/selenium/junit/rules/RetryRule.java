package me.alb_i986.selenium.junit.rules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * A rule which, in case of failure, re-runs a test annotated with {@link Flaky} until it passes,
 * for max {@code n} times (max {@code n+1} executions in total).
 * <p>
 * If a test doesn't pass after {@code n+1} executions, a {@link RetryException} is thrown,
 * containing all of the failures occurred.
 * <p>
 * A test is retried <i>unless</i>:
 * <ul>
 *     <li>The configured retry times is 0 (i.e. {@code new RetryRule(0)}),
 *     in which case the test will be executed only once,
 *     as if there was no {@link RetryRule} in place</li>
 *     <li>The test throws {@link AssumptionViolatedException}</li>
 *     <li>The test throws an {@link Error}, excluding {@link AssertionError},
 *     so that OOM errors are always propagated ASAP</li>
 *     <li>The test is <i>not</i> annotated with {@link Flaky}</li>
 * </ul>
 * <p>
 * For example, given the following rule:
 * <pre>
 *     &#064;Rule TestRule retryRule = new RetryRule(1);
 * </pre>
 *
 * For each test which is run, here's what happens:
 * <ul>
 *     <li>If the first execution is successful, that's it</li>
 *     <li>If instead the test fails, it will be run once again</li>
 *     <li>If the second execution is still not successful,
 *     then a {@link RetryException} will be thrown, with the two test failures occurred attached</li>
 * </ul>
 */
public class RetryRule implements TestRule {

    private final int retryTimes;

    /**
     * @param retries how many times to retry, <i>after the first attempt</i>.
     *                For example, {@code new RetryRule(1)} will run a test max 2 times.
     */
    public RetryRule(int retries) {
        if (retries < 0) {
            throw new IllegalArgumentException("The number of retries needs to be an integer >= 0" +
                    " but was: " + retries);
        }
        this.retryTimes = retries;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // DO NOT retry if the test is not marked as flaky,
                // or this rule has been configured to retry for 0 times
                if (retryTimes == 0 || !isTestFlaky()) {
                    base.evaluate();
                    return;
                }

                List<Throwable> failures = new ArrayList<>();
                int i;
                for (i = 0; i < (retryTimes + 1); i++) {
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

                    // TODO report that it's going to retry
                    // System.err.println(description.getDisplayName() +
                    //             ": Failed, " + i + "retries remain");
                }

                if (!failures.isEmpty()) { // the test was retried
                    if (i == retryTimes + 1) { // the test failed all the times
                        throw new RetryException(description, failures);
                    } else { // the test failed a few times but passed in the end
                        // TODO report the first (retryTimes - i) failures
                    }
                }
            }

            private boolean isTestFlaky() {
                return description.getAnnotation(Flaky.class) != null;
            }
        };
    }
}