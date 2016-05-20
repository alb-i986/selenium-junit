package me.alb_i986.selenium.junit.rules;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.logging.Logger;

/**
 * @author ascotto
 */
public class TestLoggerRule extends TestWatcher {

    protected final Logger logger;

    public TestLoggerRule(Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger should not be null");
        }
        this.logger = logger;
    }

    @Override
    protected void starting(Description description) {
        logger.info("STARTING " + description.getDisplayName());
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        logger.info("SKIPPED " + description.getDisplayName());
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
