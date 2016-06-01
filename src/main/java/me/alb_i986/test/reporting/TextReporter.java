package me.alb_i986.test.reporting;

/**
 * @author ascotto
 */
public class TextReporter implements Reporter {

    private StringBuilder report;

    @Override
    public ReportFormat getFormat() {
        return ReportFormat.TXT;
    }
}
