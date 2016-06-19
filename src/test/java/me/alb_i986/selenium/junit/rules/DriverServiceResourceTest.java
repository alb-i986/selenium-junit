package me.alb_i986.selenium.junit.rules;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.openqa.selenium.remote.service.DriverService;

import me.alb_i986.BaseMockitoTestClass;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.inOrder;

public class DriverServiceResourceTest extends BaseMockitoTestClass {

    @Mock private DriverService mockedService;

    @Test
    public void instantiationShouldFailWhenDriverIsNull() {
        try {
            new DriverServiceResource(null);
            fail("null DriverService should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The DriverService should not be null"));
        }
    }

    @Test
    public void beforeShouldStartTheService() throws Throwable {
        DriverServiceResource sut = new DriverServiceResource(mockedService);

        // when
        sut.before();

        verify(mockedService).start();
    }

    @Test
    public void afterShouldStopTheService() {
        DriverServiceResource sut = new DriverServiceResource(mockedService);

        // when
        sut.after();

        verify(mockedService).stop();
    }

    @Test
    public void integrationTest() throws Exception {
        Result result = JUnitCore.runClasses(TestClassWithDriverServiceResourceRule.class);

        assertThat(result.getFailures(), is(empty()));
        InOrder inOrder = inOrder(TestClassWithDriverServiceResourceRule.MOCKED_DRIVER_SERVICE);
        inOrder.verify(TestClassWithDriverServiceResourceRule.MOCKED_DRIVER_SERVICE).start();
        inOrder.verify(TestClassWithDriverServiceResourceRule.MOCKED_DRIVER_SERVICE).stop();
        assertThat(result.getRunCount(), equalTo(2));
    }

    public static class TestClassWithDriverServiceResourceRule {

        public static final DriverService MOCKED_DRIVER_SERVICE = mock(DriverService.class);

        @ClassRule
        public static final DriverServiceResource DRIVER_SERVICE_RESOURCE = new DriverServiceResource(MOCKED_DRIVER_SERVICE);

        @Test
        public void firstTest() {
            assertNotNull(DRIVER_SERVICE_RESOURCE.getService());
        }

        @Test
        public void secondTest() {
            assertNotNull(DRIVER_SERVICE_RESOURCE.getService());
        }
    }
}