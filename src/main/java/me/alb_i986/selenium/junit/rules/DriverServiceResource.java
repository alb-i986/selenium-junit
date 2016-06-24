package me.alb_i986.selenium.junit.rules;

import org.junit.rules.ExternalResource;
import org.openqa.selenium.remote.service.DriverService;

/**
 * A {@link org.junit.rules.TestRule} managing a {@link DriverService},
 * i.e. starting and stopping it.
 * <p>
 * This rule should be used as a {@link org.junit.ClassRule}.
 * <p>
 * Example of usage:
 * <pre>
 * public class MyTest {
 *
 *     protected static final ChromeDriverService CHROME_DRIVER_SERVICE = ChromeDriverService.createDefaultService();
 *
 *     &#064;ClassRule
 *     public static final DriverServiceResource DRIVER_SERVICE_RESOURCE = new DriverServiceResource(CHROME_DRIVER_SERVICE);
 *
 *     &#064;Test
 *     public void firstTest() {
 *         WebDriver driver = new ChromeDriver(CHROME_DRIVER_SERVICE);
 *         [..]
 *     }
 *
 *     &#064;Test
 *     public void secondTest() {
 *         WebDriver driver = new ChromeDriver(CHROME_DRIVER_SERVICE);
 *         [..]
 *     }
 * }
 * </pre>
 */
public class DriverServiceResource extends ExternalResource {

    private final DriverService service;

    public DriverServiceResource(DriverService service) {
        if (service == null) {
            throw new IllegalArgumentException("The DriverService should not be null");
        }
        this.service = service;
    }

    /**
     * Start the service.
     *
     * @see DriverService#start()
     */
    @Override
    protected void before() throws Throwable {
        service.start();
    }

    /**
     * Stop the service.
     *
     * @see DriverService#stop()
     */
    @Override
    protected void after() {
        service.stop();
    }

    public DriverService getService() {
        return service;
    }
}
