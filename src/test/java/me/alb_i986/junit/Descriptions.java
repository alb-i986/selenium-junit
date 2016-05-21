package me.alb_i986.junit;

import org.junit.runner.Description;

import java.lang.annotation.Annotation;

import me.alb_i986.selenium.junit.rules.Flaky;

public class Descriptions {

    public static Description descriptionForFlakyTest() {
        return Description.createTestDescription("test class", "test name", new Flaky() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Flaky.class;
            }
        });
    }

    public static Description defaultDescription() {
        return Description.createTestDescription("test class", "test name");
    }
}
