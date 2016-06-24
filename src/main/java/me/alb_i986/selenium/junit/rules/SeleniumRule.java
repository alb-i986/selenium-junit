package me.alb_i986.selenium.junit.rules;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;

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
 *          .toRetryFlakyTestsOnFailure(2); // retry each test max 2 times (max 3 executions in total)
 *
 *     protected final WebDriver driver() {
 *         return seleniumRule.getDriver();
 *     }
 *
 *     &#064;Test
 *     &#064;Flaky // without this, the test will <i>not</i> be re-tried in case of failure
 *     public void myFlakyTest() {
 *         throw new RuntimeException("flaky test failure");
 *     }
 *
 *     &#064;Test
 *     public void myStableTest() {
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

    public SeleniumRule toRetryFlakyTestsOnFailure(int retryTimes) {
        return toRetryFlakyTestsOnFailure(new RetryRule(retryTimes));
    }

    protected SeleniumRule toRetryFlakyTestsOnFailure(RetryRule retryRule) {
        this.retryRule = retryRule;
        return this;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        RuleChainBuilder chainBuilder = new RuleChainBuilder();
        if (retryRule != null) {
            chainBuilder.append(retryRule);
        }
        chainBuilder.append(driverResource);
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