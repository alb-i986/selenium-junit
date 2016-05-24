package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.WebDriverFactory;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A {@link RuleChain} providing access to a {@link WebDriver}.
 * <p>
 * Use {@link #builder(WebDriverFactory)} to instantiate.
 */
public abstract class SeleniumRule implements TestRule {

    private final RuleChain ruleChain;

    /**
     * @param factory the factory to use to create {@link WebDriver} instances before a test starts
     */
    public static Builder builder(WebDriverFactory factory) {
        return new Builder(factory);
    }

    /**
     * Please use {@link #builder(WebDriverFactory)} instead,
     * to instantiate.
     */
    protected SeleniumRule(RuleChain ruleChain) {
        if (ruleChain == null) {
            throw new IllegalArgumentException("RuleChain should not be null");
        }
        this.ruleChain = ruleChain;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return ruleChain.apply(base, description);
    }

    public abstract WebDriver getDriver();

    public static class Builder {

        private final WebDriverResource driverResource;
        private TakeScreenshotOnFailureRule screenshotOnFailureRule;
        private TestLoggerRule testLogger;
        private Set<TestRule> otherRules = new HashSet<>();

        private Builder(WebDriverFactory factory) {
            this.driverResource = new WebDriverResource(factory);
        }

        public Builder withTestLogger(Logger logger) {
            this.testLogger = new TestLoggerRule(logger);
            return this;
        }

        public <X> Builder takeScreenshotOnFailure(OutputType<X> outputType) {
            this.screenshotOnFailureRule = new TakeScreenshotOnFailureRule(
                    driverResource, outputType);
            return this;
        }

        /**
         * To be used by unit tests only!
         */
        Builder appendRule(TestRule otherRule) {
            otherRules.add(otherRule);
            return this;
        }

        public SeleniumRule build() {
            RuleChain ruleChain;
            if (testLogger != null) {
                ruleChain = RuleChain.outerRule(testLogger);
            } else {
                ruleChain = RuleChain.outerRule(driverResource);
            }
            if (screenshotOnFailureRule != null) {
                ruleChain = ruleChain.around(screenshotOnFailureRule);
            }
            for (TestRule otherRule : otherRules) {
                ruleChain = ruleChain.around(otherRule);
            }
            return new SeleniumRule(ruleChain) {
                @Override
                public WebDriver getDriver() {
                    return driverResource.getWrappedDriver();
                }
            };
        }
    }
}
