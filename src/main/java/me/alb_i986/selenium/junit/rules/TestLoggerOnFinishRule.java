package me.alb_i986.selenium.junit.rules;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;

import java.util.logging.Logger;

public class TestLoggerOnFinishRule extends TestLoggerRule {

    public TestLoggerOnFinishRule(Logger logger) {
        super(logger);
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        logger.warning("SKIPPED " + description.getDisplayName());
    }

    @Override
    protected void failed(Throwable e, Description description) {
        logger.warning("FAILED " + description.getDisplayName());
    }

    @Override
    protected void succeeded(Description description) {
        logger.info("PASSED " + description.getDisplayName());
    }
}
