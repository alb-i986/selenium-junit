package me.alb_i986.selenium.junit.rules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.remote.service.DriverService;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DriverServiceResourceTest {

    @Mock private DriverService mockedService;

    @Test
    public void beforeShouldStartTheService() throws Throwable {
        DriverServiceResource sut = new DriverServiceResource(mockedService);

        // when
        sut.before();

        verify(mockedService).start();
    }

    @Test
    public void afterShouldStopTheService() throws Exception {
        DriverServiceResource sut = new DriverServiceResource(mockedService);

        // when
        sut.after();

        verify(mockedService).stop();
    }

    @Test
    public void nullArgShouldNotBeAllowed() {
        try {
            new DriverServiceResource(null);
            fail("null arg should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}