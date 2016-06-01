package me.alb_i986.selenium.junit.rules;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.logging.Logger;

import me.alb_i986.test.reporting.Reporter;

/**
 * @author ascotto
 */
public abstract class TestLoggerRule extends TestWatcher {

    protected final Reporter reporter;

    public TestLoggerRule(Reporter reporter) {
        if (reporter == null) {
            throw new IllegalArgumentException("reporter should not be null");
        }
        this.reporter = reporter;
    }

    public static TestLoggerRule onStart(Logger logger) {
        return new LogOnStart(logger);
    }

    public static TestLoggerRule onFinish(Logger logger) {
        return new LogOnStart(logger);
    }

    private static class LogOnStart extends TestLoggerRule {

        private LogOnStart(Logger logger) {
            super(logger);
        }

        @Override
        protected void starting(Description description) {
            reporter.info("STARTING " + description.getDisplayName());
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            reporter.info("SKIPPED " + description.getDisplayName());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            reporter.warning("FAILED " + description.getDisplayName());
        }

        @Override
        protected void succeeded(Description description) {
            reporter.info("PASSED " + description.getDisplayName());
        }
    }
}
