package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.WebDriverFactory;
import org.junit.rules.RuleChain;
import org.openqa.selenium.OutputType;

import java.util.logging.Logger;

/**
 * @author ascotto
 */
public class RuleChainBuilder {

    private final WebDriverResource driverResource;
    private TakeScreenshotOnFailureRule takeScreenshotOnFailureRule;
    private TestLoggerRule testLogger;

    public RuleChainBuilder(WebDriverFactory factory) {
        this.driverResource = new WebDriverResource(factory);
    }

    public RuleChainBuilder withTestLogger(Logger logger) {
        this.testLogger = new TestLoggerRule(logger);
        return this;
    }

    public <X> RuleChainBuilder takeScreenshotOnFailure(OutputType<X> outputType) {
        this.takeScreenshotOnFailureRule = new TakeScreenshotOnFailureRule(driverResource.getDriver(), outputType);
        return this;
    }

    public RuleChain build() {
        RuleChain ruleChain = RuleChain.outerRule(driverResource);
        if (takeScreenshotOnFailureRule != null) {
            ruleChain.around(takeScreenshotOnFailureRule);
        }
        if (testLogger != null) {
            ruleChain.around(testLogger);
        }
        return ruleChain;
    }
}
