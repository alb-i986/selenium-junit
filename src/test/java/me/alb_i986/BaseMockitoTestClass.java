package me.alb_i986;

import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Base class for unit tests which use {@link org.mockito.Mockito}.
 * <p>
 * It provides a {@link MockitoRule}.
 */
public abstract class BaseMockitoTestClass {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
}
