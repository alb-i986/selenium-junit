package me.alb_i986.selenium.junit.rules;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import me.alb_i986.selenium.WebDriverFactory;

/**
 * A {@link RuleChain} providing access to a {@link WebDriver}.
 * <p>
 * Use {@link Builder} to instantiate.
 */
public abstract class SeleniumRuleChain implements TestRule {

    private final RuleChain ruleChain;

    protected SeleniumRuleChain(RuleChain ruleChain) {
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

    public class Builder {

        private final WebDriverResource driverResource;
        private TakeScreenshotOnFailureRule screenshotOnFailureRule;
        private TestLoggerRule testLogger;

        public Builder(WebDriverFactory factory) {
            this.driverResource = new WebDriverResource(factory);
        }

        public Builder withTestLogger(Logger logger) {
            this.testLogger = new TestLoggerRule(logger);
            return this;
        }

        public <X> Builder takeScreenshotOnFailure(OutputType<X> outputType) {
            this.screenshotOnFailureRule = new TakeScreenshotOnFailureRule(
                    driverResource.getDriver(), outputType);
            return this;
        }

        public SeleniumRuleChain build() {
            RuleChain ruleChain = RuleChain.outerRule(driverResource);
            if (screenshotOnFailureRule != null) {
                ruleChain.around(screenshotOnFailureRule);
            }
            if (testLogger != null) {
                ruleChain.around(testLogger);
            }
            return new SeleniumRuleChain(ruleChain) {
                @Override
                public WebDriver getDriver() {
                    return driverResource.getDriver();
                }
            };
        }
    }
}
