package me.alb_i986.selenium.junit.rules;

import me.alb_i986.BaseMockitoTestClass;
import me.alb_i986.selenium.DummyDriver;
import me.alb_i986.selenium.DummyDriverFactory;
import me.alb_i986.selenium.WebDriverFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

public class WebDriverResourceTest extends BaseMockitoTestClass {

    @Mock private WebDriverFactory mockedDriverFactory;
    @Mock private WebDriver mockedDriver;

    @Test
    public void nullDriverFactoryInConstructorShouldNotBeAllowed() {
        WebDriverFactory nullFactory = null;
        try {
            new WebDriverResource(nullFactory);
            fail("null WebDriverFactory in constructor should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
            assertThat(e.getMessage(), equalTo("The WebDriverFactory should not be null"));
        }
    }

    @Test
    public void beforeShouldCreateNewDriver() throws Throwable {
        WebDriver dummyDriver = new DummyDriver();
        given(mockedDriverFactory.create()).willReturn(dummyDriver);
        WebDriverResource sut = new WebDriverResource(mockedDriverFactory);

        // when
        sut.before();

        verify(mockedDriverFactory).create();
        assertThat(sut.getDriver(), sameInstance(dummyDriver));
    }

    @Test
    public void beforeShouldThrowWhenFactoryReturnsNullDriver() throws Throwable {
        given(mockedDriverFactory.create()).willReturn(null);
        WebDriverResource sut = new WebDriverResource(mockedDriverFactory);

        try {
            sut.before();
            fail("before() should throw when factory returns null driver");
        } catch (WebDriverException e) {
            // expected
            assertThat(e.getMessage(), startsWith("WebDriverFactory failed creating a new driver"));
        }
    }

    @Test
    public void afterShouldQuitTheDriver() {
        WebDriverResource sut = new WebDriverResource(new DummyDriverFactory());
        sut.setDriver(mockedDriver);

        // when
        sut.after();

        verify(mockedDriver).quit();
    }
}