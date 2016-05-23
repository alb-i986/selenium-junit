package me.alb_i986.selenium.junit.rules;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;

/**
 * A {@link RuleChain} providing access to a {@link WebDriver}.
 */
public abstract class SeleniumRuleChain implements TestRule {

    private final RuleChain ruleChain;

    public SeleniumRuleChain(RuleChain ruleChain) {
        this.ruleChain = ruleChain;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return ruleChain.apply(base, description);
    }

    public abstract WebDriver getDriver();
}
