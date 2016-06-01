package me.alb_i986.test.reporting;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author ascotto
 */
public class UnitTestWatcher extends TestWatcher {
    protected final Reporter reporter;

    public UnitTestWatcher(Reporter reporter) {
        if (reporter == null) {
            throw new IllegalArgumentException("The Reporter must be null.");
        }
        this.reporter = reporter;
    }

    @Override
    protected void succeeded(Description description) {
        reporter.passed(description);
    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        super.skipped(e, description);
    }

    @Override
    protected void skipped(org.junit.internal.AssumptionViolatedException e, Description description) {
        super.skipped(e, description);
    }

    @Override
    protected void starting(Description description) {
        super.starting(description);
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        reporter.failure(e, description);
    }
}
