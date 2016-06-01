package me.alb_i986.selenium.junit.rules;

import org.junit.runner.Description;

import java.util.logging.Logger;

public class TestLoggerOnStartRule extends TestLoggerRule {

    public TestLoggerOnStartRule(Logger logger) {
        super(logger);
    }

    @Override
    protected void starting(Description description) {
        reporter.info("STARTING " + description.getDisplayName());
    }

    // TODO put them in TestLoggerRule?
}
