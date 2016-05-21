package me.alb_i986.selenium.junit.rules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Re-runs a test which fails and is explicitly marked as {@link Flaky} until it passes, for max n times.
 * <p>
 * {@link Error}s, excluding {@link AssertionError}s, are <i>not</i> caught.
 * Therefore, OOM errors will always be propagated ASAP.
 * <p>
 * If a test doesn't pass after the configured number of retry times,
 * a {@link RetryException} is thrown, collecting all of the failures occurred.
 * <p>
 * A test is <i>not</i> retried if:
 * <ul>
 *     <li>the configured retry times is 0 (in which case, the test will be executed only once,
 *     as if there was no {@link RetryRule})</li>
 *     <li>The test throws {@link AssumptionViolatedException}</li>
 *     <li>The test throws an {@link Error}, excluding {@link AssertionError}
 *     (therefore, OOM errors will always be propagated ASAP)</li>
 *     <li>The test is not annotated with {@link Flaky}</li>
 * </ul>
 * <p>
 * Examples:
 * <pre>
 *     &#064;Rule RetryRule retryRule = new RetryRule(2);
 * </pre>
 *
 * <ul>
 *     <li>Each test will be run once</li>
 *     <li>If it passes, that's it</li>
 *     <li>If it fails, it will be run once again</li>
 *     <li>If it fails again, a {@link RetryException} will be thrown</li>
 * </ul>
 * A RetryRule with 0 retry times will run tests only once, as if no {@link RetryRule} was in place.
 *
 * TODO finish javadoc
 */
public class RetryRule implements TestRule {

    private final int retryTimes;

    /**
     * @param retries how many times to retry, <i>after the first attempt</i>.
     *                Example: {@code new RetryRule(1)} will run a test twice    if it fails.
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
                    } catch (AssertionError e) { // retry
                        failures.add(e);
                    } catch (Error error) {
                        throw error;
                    } catch (AssumptionViolatedException e) {
                        throw e;
                    } catch (Throwable t) { // retry
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