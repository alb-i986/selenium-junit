package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.DummyDriver;
import me.alb_i986.selenium.DummyDriverFactory;
import me.alb_i986.selenium.WebDriverFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WebDriverResourceTest {

    @Mock private WebDriverFactory mockedDriverFactory;
    @Mock private WebDriver mockedDriver;

    @Test
    public void beforeShouldCreateNewDriver() throws Throwable {
        WebDriver dummyDriver = new DummyDriver();
        given(mockedDriverFactory.create()).willReturn(dummyDriver);
        WebDriverResource sut = new WebDriverResource(mockedDriverFactory);

        // when
        sut.before();

        verify(mockedDriverFactory).create();
        assertThat(sut.getDriver(), equalTo(dummyDriver));
    }

    @Test
    public void beforeShouldThrowWhenFactoryReturnsNullDriver() throws Throwable {
        given(mockedDriverFactory.create()).willReturn(null);
        WebDriverResource sut = new WebDriverResource(mockedDriverFactory);

        try {
            sut.before();
            fail("Should Throw When Factory Returns Null Driver");
        } catch (WebDriverException e) {
            // expected
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

    @Test
    public void nullArgsInConstructorShouldNotBeAllowed() {
        try {
            new WebDriverResource(null);
            fail("null arg should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}