package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.WebDriverFactory;
import me.alb_i986.selenium.WebDriverProvider;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

/**
 * A {@link TestRule} for Selenium tests.
 * <p>
 * Start from {@link #configure(WebDriverFactory)} to build an instance.
 * <p>
 * Example of usage:
 * <pre>
 * public class MySeleniumTest {
 *     &#064;Rule
 *     public SeleniumRule seleniumRule = SeleniumRule.configure(new ChromeDriverFactory())
 *          .withTestLogger(Logger.getLogger("my.logger"))
 *          .toTakeScreenshotOnFailure(OutputType.BASE64)
 *          .build();
 *
 *     protected WebDriver driver() {
 *         return seleniumRule.getDriver();
 *     }
 *
 *     &#064;Test
 *     public void myTest() {
 *         driver().get("http://www.google.com");
 *          driver().findElement(By.name("q")).sendKeys("selenium-junit" + Keys.ENTER);
 *          new WebDriverWait(driver(), 5).until(ExpectedConditions.titleContains("selenium-junit"));
 *     }
 *
 *     private static class ChromeDriverFactory implements WebDriverFactory {
 *         &#064;Override
 *         public WebDriver create() {
 *             return new ChromeDriver();
 *         }
 *     }
 * }
 * </pre>
 */
public abstract class SeleniumRule implements TestRule, WebDriverProvider {

    private final RuleChain ruleChain;

    /**
     * Gives access to a configuration API (a Builder), which allows to
     * build a {@link SeleniumRule} as per your configuration needs.
     *
     * @param factory the factory to use to create {@link WebDriver} instances before a test starts
     */
    public static Builder configure(WebDriverFactory factory) {
        return new Builder(factory);
    }

    /**
     * Please use {@link #configure(WebDriverFactory)} instead, to instantiate.
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

    public static class Builder {

        private final WebDriverResource driverResource;
        private TakeScreenshotOnFailureRule screenshotOnFailureRule;
        private TestLoggerRule testLogger;

        protected Builder(WebDriverFactory factory) {
            this(new WebDriverResource(factory));
        }

        protected Builder(WebDriverResource driverResource) {
            this.driverResource = driverResource;
        }

        public Builder withTestLogger(Logger logger) {
            return withTestLogger(new TestLoggerRule(logger));
        }

        protected Builder withTestLogger(TestLoggerRule loggerRule) {
            this.testLogger = loggerRule;
            return this;
        }

        public <X> Builder toTakeScreenshotOnFailure(OutputType<X> outputType) {
            return toTakeScreenshotOnFailure(new TakeScreenshotOnFailureRule(driverResource, outputType));
        }

        protected <X> Builder toTakeScreenshotOnFailure(TakeScreenshotOnFailureRule<X> takeScreenshotOnFailureRule) {
            this.screenshotOnFailureRule = takeScreenshotOnFailureRule;
            return this;
        }

        public SeleniumRule build() {
            RuleChainBuilder chainBuilder = new RuleChainBuilder();
            if (testLogger != null) {
                chainBuilder.append(testLogger);
            }
            chainBuilder.append(driverResource);
            if (screenshotOnFailureRule != null) {
                chainBuilder.append(screenshotOnFailureRule);
            }
            return new SeleniumRule(chainBuilder.build()) {
                @Override
                public WebDriver getDriver() {
                    return driverResource.getDriver();
                }
            };
        }
    }

    private static class RuleChainBuilder {
        private RuleChain ruleChain = RuleChain.emptyRuleChain();

        public RuleChainBuilder append(TestRule enclosedRule) {
            ruleChain = ruleChain.around(enclosedRule);
            return this;
        }

        public RuleChain build() {
            return ruleChain;
        }
    }
}
