package me.alb_i986.selenium.junit.rules;

import org.junit.rules.RuleChain;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import me.alb_i986.selenium.WebDriverFactory;

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

    public SeleniumRuleChain build() {
        RuleChain ruleChain = RuleChain.outerRule(driverResource);
        if (takeScreenshotOnFailureRule != null) {
            ruleChain.around(takeScreenshotOnFailureRule);
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
