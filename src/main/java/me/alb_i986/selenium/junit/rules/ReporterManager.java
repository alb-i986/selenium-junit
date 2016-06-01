package me.alb_i986.selenium.junit.rules;

import org.junit.rules.ExternalResource;

import me.alb_i986.commons.Provider;
import me.alb_i986.test.reporting.Reporter;
import me.alb_i986.test.reporting.ReporterFactory;

/**
 * @author ascotto
 */
public class ReporterManager extends ExternalResource implements Provider<Reporter> {

    private final ReporterFactory reporterFactory;
    private ThreadLocal<Reporter> reporter;

    public ReporterManager(ReporterFactory reporterFactory) {
        this.reporterFactory = reporterFactory;
    }

    @Override
    protected void before() throws Throwable {
        reporter = reporterFactory.create();
    }

    @Override
    protected void after() {
        reporter.close();
    }

    @Override
    public Reporter get() {
        if (reporter == null) {
            throw new IllegalStateException("The reporter seems not to have been initialized.");
        }
        return reporter;
    }
}
