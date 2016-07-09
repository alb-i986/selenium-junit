package me.alb_i986.selenium.junit.rules;

import org.junit.rules.ExternalResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.alb_i986.selenium.WebDriverFactory;
import me.alb_i986.selenium.WebDriverProvider;

/**
 * A {@link org.junit.rules.TestRule} managing {@link WebDriver} instances during testruns,
 * i.e. opening and closing real browsers.
 * <p>
 * Before a test starts, a new driver is created.
 * The actual creation of the driver is delegated to the configured {@link WebDriverFactory}.
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
public class WebDriverResource extends ExternalResource implements WebDriverProvider {

    private static final Logger LOGGER = Logger.getLogger(WebDriverResource.class.getName());

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
     * <p>
     * Any {@link WebDriverException} thrown is caught and logged.
     *
     * @see WebDriver#quit()
     */
    @Override
    protected void after() {
        try {
            driver.quit();
        } catch (WebDriverException e) {
            LOGGER.log(Level.WARNING, "Quitting driver failed", e);
        }
    }

    /**
     * @throws IllegalStateException if the driver has not been initialized yet,
     * which means that before() hasn't been called.
     */
    @Override
    public WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("Looks like the driver has not been initialized yet.");
        }
        return driver;
    }

    /**
     * <b>Warning</b>: to be used by unit tests only!
     */
    void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
