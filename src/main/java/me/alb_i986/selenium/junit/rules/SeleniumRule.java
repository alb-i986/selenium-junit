package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.WebDriverFactory;
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
 * Start from {@link #builder(WebDriverFactory)} to create an instance.
 * <p>
 * Example of usage:
 * <pre>
 * public class MySeleniumTest {
 *     &#064;Rule
 *     public SeleniumRule seleniumRule = SeleniumRule.builder(new ChromeDriverFactory())
 *          .withTestLogger(Logger.getLogger("my.logger"))
 *          .takeScreenshotOnFailure(OutputType.BASE64)
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
public abstract class SeleniumRule implements TestRule {

    private final RuleChain ruleChain;

    /**
     * @param factory the factory to use to create {@link WebDriver} instances before a test starts
     */
    public static Builder builder(WebDriverFactory factory) {
        return new Builder(factory);
    }

    /**
     * Please use {@link #builder(WebDriverFactory)} instead, to instantiate.
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

        private Builder(WebDriverFactory factory) {
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

        public <X> Builder takeScreenshotOnFailure(OutputType<X> outputType) {
            return takeScreenshotOnFailure(new TakeScreenshotOnFailureRule(driverResource, outputType));
        }

        protected <X> Builder takeScreenshotOnFailure(TakeScreenshotOnFailureRule<X> takeScreenshotOnFailureRule) {
            this.screenshotOnFailureRule = takeScreenshotOnFailureRule;
            return this;
        }

        public SeleniumRule build() {
            RuleChain ruleChain = RuleChain.emptyRuleChain();
            if (testLogger != null) {
                ruleChain = ruleChain.around(testLogger);
            }
            ruleChain = ruleChain.around(driverResource);
            if (screenshotOnFailureRule != null) {
                ruleChain = ruleChain.around(screenshotOnFailureRule);
            }
            return new SeleniumRule(ruleChain) {
                @Override
                public WebDriver getDriver() {
                    return driverResource.getDriver();
                }
            };
        }
    }
}
