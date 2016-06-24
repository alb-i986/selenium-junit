package me.alb_i986;

import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Base class for unit tests which use {@link org.mockito.Mockito}.
 * <p>
 * It provides a {@link MockitoRule} so that mocks defined in concrete test classes are automatically initialized.
 * <p>
 * <b>Note</b>.
 * I prefer {@link MockitoRule} over {@link org.mockito.runners.MockitoJUnitRunner} because, as it turns out,
 * {@link org.mockito.runners.MockitoJUnitRunner} is a pretty heavily customized JUnit runner.
 * In fact, it doesn't extend neither {@link org.junit.runners.ParentRunner} nor {@link org.junit.runners.BlockJUnit4ClassRunner}.
 * Therefore, it is more subject to bugs than a Rule, which is a mechanism purposefully designed for extending JUnit.
 */
public abstract class BaseMockitoTestClass {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
}
