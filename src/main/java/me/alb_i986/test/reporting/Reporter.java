package me.alb_i986.test.reporting;

import org.junit.runner.Description;

public interface Reporter {

    void info(String msg);
    void warn(String msg);
    void err(String msg);

    void starting(Description description);
    void passed(Description description);
    void failure(Throwable e, Description description);

    /**
     * Flushes the test report to the configured output.
     */
    void close();
}
