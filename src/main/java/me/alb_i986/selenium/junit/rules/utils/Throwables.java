package me.alb_i986.selenium.junit.rules.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Throwables {

    public static String getStacktraces(List<Throwable> throwables) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Throwable failure : throwables) {
            sb.append(String.format("\n  %d. %s", i++, getStackTrace(failure)));
        }
        return sb.toString();
    }

    // TODO With JUnit 4.13, use JUnit's Throwables.getStacktrace() instead of this
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
