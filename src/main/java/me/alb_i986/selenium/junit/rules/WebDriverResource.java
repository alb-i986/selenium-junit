package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.WebDriverFactory;
import org.junit.rules.ExternalResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

public class WebDriverResource extends ExternalResource {

    private final WebDriverFactory driverFactory;
    private WebDriver driver;

    public WebDriverResource(final WebDriver driver) {
        this(new WebDriverFactory() {
                 @Override
                 public WebDriver create() {
                     return driver;
                 }
             }
        );
    }

    public WebDriverResource(WebDriverFactory driverFactory) {
        if (driverFactory == null) {
            throw new IllegalArgumentException("the WebDriverFactory should not be null");
        }
        this.driverFactory = driverFactory;
    }

    /**
     * Opens a browser.
     * <p>
     * Creates a new driver with the given {@link WebDriverFactory}.
     *
     * @throws WebDriverException if the {@link WebDriverFactory} returns a null driver
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
     * Closes the browser
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
