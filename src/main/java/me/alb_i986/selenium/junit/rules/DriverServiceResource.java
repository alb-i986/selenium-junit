package me.alb_i986.selenium.junit.rules;

import org.junit.rules.ExternalResource;
import org.openqa.selenium.remote.service.DriverService;

public class DriverServiceResource extends ExternalResource {

    private final DriverService service;

    public DriverServiceResource(DriverService service) {
        if (service == null) {
            throw new IllegalArgumentException("service should not be null");
        }
        this.service = service;
    }

    /**
     * Start the service
     *
     * @see DriverService#start()
     */
    @Override
    protected void before() throws Throwable {
        service.start();
    }

    /**
     * Stop the service
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
