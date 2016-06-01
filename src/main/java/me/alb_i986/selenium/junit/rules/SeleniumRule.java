package me.alb_i986.selenium.junit.rules;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import me.alb_i986.selenium.WebDriverFactory;
import me.alb_i986.selenium.WebDriverProvider;

/**
 * A {@link TestRule} for Selenium tests.
 * <p>
 * Start from {@link #configure(WebDriverFactory)} to build an instance.
 * <p>
 * Example of usage:
 * <pre>
 * public class MySeleniumTest {
 *     &#064;Rule
 *     public final SeleniumRule seleniumRule = SeleniumRule.configure(new ChromeDriverFactory())
 *          .withTestLogger(Logger.getLogger("my.logger"))
 *          .toTakeScreenshotOnFailure(OutputType.BASE64)
 *          .build();
 *
 *     protected final WebDriver driver() {
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
     * Gives access to a fluent configuration API (a Builder),
     * which allows to build a {@link SeleniumRule} as per your needs.
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
        private TakeScreenshotOnFailureRule screenshotOnFailure;
        private TestLoggerOnStartRule testLoggerOnStart;
        private TestLoggerOnFinishRule testLoggerOnFinish;

        protected Builder(WebDriverFactory factory) {
            this(new WebDriverResource(factory));
        }

        protected Builder(WebDriverResource driverResource) {
            this.driverResource = driverResource;
        }

        public Builder withTestLogger(Logger logger) {
            withTestLoggerOnStart(new TestLoggerOnStartRule(logger));
            withTestLoggerOnFinish(new TestLoggerOnFinishRule(logger));
            return this;
        }

        protected Builder withTestLoggerOnStart(TestLoggerOnStartRule testLoggerOnStart) {
            this.testLoggerOnStart = testLoggerOnStart;
            return this;
        }

        protected Builder withTestLoggerOnFinish(TestLoggerOnFinishRule testLoggerOnFinish) {
            this.testLoggerOnFinish = testLoggerOnFinish;
            return this;
        }

        public <X> Builder toTakeScreenshotOnFailure(OutputType<X> outputType) {
            return toTakeScreenshotOnFailure(new TakeScreenshotOnFailureRule(driverResource, outputType));
        }

        protected <X> Builder toTakeScreenshotOnFailure(TakeScreenshotOnFailureRule<X> takeScreenshotOnFailureRule) {
            this.screenshotOnFailure = takeScreenshotOnFailureRule;
            return this;
        }

        public SeleniumRule build() {
            RuleChainBuilder chainBuilder = new RuleChainBuilder();
            if (testLoggerOnStart != null) {
                chainBuilder.append(testLoggerOnStart);
            }
            chainBuilder.append(driverResource);
            if (screenshotOnFailure != null) {
                chainBuilder.append(screenshotOnFailure);
            }
            if (testLoggerOnFinish != null) {
                chainBuilder.append(testLoggerOnFinish);
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
