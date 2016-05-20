package me.alb_i986.selenium.junit.rules;

import me.alb_i986.selenium.WebDriverFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class RuleChainBuilderTest {

    public static class TestClass {
        WebDriverFactory chromeFactory = new WebDriverFactory() {
            @Override
            public WebDriver create() {
                return new ChromeDriver();
            }
        };

        @Rule
        public RuleChain ruleChain = new RuleChainBuilder(chromeFactory).build();

        @Test
        public void test() {

        }
    }

    @Test
    public void integrationTest() {
        Result result = JUnitCore.runClasses(TestClass.class);
        assertThat(result.wasSuccessful(), is(true));
    }
}
