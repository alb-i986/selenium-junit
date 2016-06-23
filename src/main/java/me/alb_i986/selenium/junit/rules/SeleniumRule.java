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
 * Example of usage:
 * <pre>
 * public class MySeleniumTest {
 *     &#064;Rule
 *     public final SeleniumRule seleniumRule = new SeleniumRule(new ChromeDriverFactory())
 *          .withTestLogger(Logger.getLogger("my.logger"))
 *          .toTakeScreenshotOnFailure(OutputType.BASE64)
 *          .toRetryOnFailure(2); // retry each test max 2 times (max 3 executions in total)
 *
 *     protected final WebDriver driver() {
 *         return seleniumRule.getDriver();
 *     }
 *
 *     &#064;Test
 *     public void myTest() {
 *         driver().get("http://www.google.com");
 *         driver().findElement(By.name("q")).sendKeys("selenium-junit" + Keys.ENTER);
 *         new WebDriverWait(driver(), 5).until(ExpectedConditions.titleContains("selenium-junit"));
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
public class SeleniumRule implements TestRule, WebDriverProvider {

    private final WebDriverResource driverResource;
    private TakeScreenshotOnFailureRule screenshotOnFailure;
    private TestLoggerOnStartRule testLoggerOnStart;
    private TestLoggerOnFinishRule testLoggerOnFinish;
    private RetryRule retryRule;

    /**
     * @param factory the factory to use to create {@link WebDriver} instances before a test starts
     */
    public SeleniumRule(WebDriverFactory factory) {
        this(new WebDriverResource(factory));
    }

    protected SeleniumRule(WebDriverResource driverResource) {
        this.driverResource = driverResource;
    }

    public SeleniumRule withTestLogger(Logger logger) {
        withTestLoggerOnStart(new TestLoggerOnStartRule(logger));
        withTestLoggerOnFinish(new TestLoggerOnFinishRule(logger));
        return this;
    }

    protected SeleniumRule withTestLoggerOnStart(TestLoggerOnStartRule testLoggerOnStart) {
        this.testLoggerOnStart = testLoggerOnStart;
        return this;
    }

    protected SeleniumRule withTestLoggerOnFinish(TestLoggerOnFinishRule testLoggerOnFinish) {
        this.testLoggerOnFinish = testLoggerOnFinish;
        return this;
    }

    public <X> SeleniumRule toTakeScreenshotOnFailure(OutputType<X> outputType) {
        return toTakeScreenshotOnFailure(new TakeScreenshotOnFailureRule(driverResource, outputType));
    }

    protected <X> SeleniumRule toTakeScreenshotOnFailure(TakeScreenshotOnFailureRule<X> takeScreenshotOnFailureRule) {
        this.screenshotOnFailure = takeScreenshotOnFailureRule;
        return this;
    }

    public SeleniumRule toRetryOnFailure(int retryTimes) {
        return toRetryOnFailure(new RetryRule(retryTimes));
    }

    protected SeleniumRule toRetryOnFailure(RetryRule retryRule) {
        this.retryRule = retryRule;
        return this;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        RuleChainBuilder chainBuilder = new RuleChainBuilder();
        if (retryRule != null) {
            chainBuilder.append(retryRule);
        }
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
        return chainBuilder.build().apply(base, description);
    }

    @Override
    public WebDriver getDriver() {
        return driverResource.getDriver();
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