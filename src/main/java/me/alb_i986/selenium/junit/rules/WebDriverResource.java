package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.WebDriverFactory;
import org.junit.rules.ExternalResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/**
 * A {@link org.junit.rules.TestRule} managing {@link WebDriver} instances during testruns,
 * i.e. opening and closing real browsers.
 * <p>
 * Before a test starts, a new {@link WebDriver} is created. The actual creation of the driver is delegated to a {@link WebDriverFactory}.
 * After a test terminates, the driver is quit.
 * <p>
 * Not thread safe. This shouldn't be an issue, as long as every test gets a new instance of the rule.
 * Example of correct usage:
 *
 * <pre>
 * public class MyTest {
 *     &#064;Rule
 *     public TestRule rule = new WebDriverResource(new MyDriverFactory());
 * }
 * </pre>
 */
public class WebDriverResource extends ExternalResource {

    private final WebDriverFactory driverFactory;
    private WebDriver driver;

    public WebDriverResource(WebDriverFactory driverFactory) {
        if (driverFactory == null) {
            throw new IllegalArgumentException("The WebDriverFactory should not be null");
        }
        this.driverFactory = driverFactory;
    }

    /**
     * Creates a new driver by using the given {@link WebDriverFactory}.
     *
     * @throws WebDriverException if the {@link WebDriverFactory} returns a null driver
     *
     * @see WebDriverFactory#create()
     */
    @Override
    protected void before() throws Throwable {
        WebDriver created = driverFactory.create();
        if (created == null) {
            throw new WebDriverException("WebDriverFactory failed creating a new driver. " +
                    "The driver returned was null.");
        }
        this.driver = created;
    }

    /**
     * Quits the driver.
     *
     * @see WebDriver#quit()
     */
    @Override
    public void after() {
        driver.quit();
    }

    public WebDriver getDriver() {
        return driver;
    }

    /**
     * To be used by unit tests only!
     */
    void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
