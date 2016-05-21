package me.alb_i986.selenium.junit.rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A test marked as {@code Flaky} will be retried in case of failure
 * by {@link RetryRule}, if configured.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Flaky {
}
