package me.alb_i986.selenium.junit.rules;

import org.junit.runner.Description;

import java.util.logging.Logger;

public class TestLoggerOnStartRule extends TestLoggerRule {

    public TestLoggerOnStartRule(Logger logger) {
        super(logger);
    }

    @Override
    protected void starting(Description description) {
        logger.info("STARTING " + description.getDisplayName());
    }
}
