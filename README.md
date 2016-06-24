# Selenium-JUnit4

A light-weight Selenium testing framework providing a number of features in the form of
[JUnit Rules](https://github.com/junit-team/junit4/wiki/Rules).


## Motivation
When starting a test automation project using Selenium, there are a few things that need to be
implemented.
In fact, there's a gap to fill between Selenium and the testing framework of choice.
A classic example is the setup and tear down of a `WebDriver` for each test.
Or to take a screenshot in case of test failure. Selenium provides a method for taking a screenshot,
but it's up to the tester to call it when a test fails.
All of these things are pretty much a must-have, so we all (testers) end up reinventing the wheel.

The aim of this project is to fill the gap for projects using JUnit as the testing framework.
The way it does is by exploiting [JUnit Rules](https://github.com/junit-team/junit4/wiki/Rules),
which by the way allows for a *clean* design.


## Features

- Automatic setup and tear down of `WebDriver`'s
- Retry flaky tests in case of failure

Soon to be added:

- Screenshot on test failure
- HTML test reports (with screenshots on test failure)


## Example of usage

    public class MySeleniumTest {

       @Rule
       public final SeleniumRule seleniumRule = new SeleniumRule(new ChromeDriverFactory())
           .toRetryFlakyTestsOnFailure(2); // retry each test max 2 times (max 3 executions in total)

       protected final WebDriver driver() {
           return seleniumRule.getDriver();
       }

       @Test
       @Flaky // without this, the test will *not* be re-tried in case of failure, even though SeleniumRule was configured to retry
       public void myFlakyTest() {
           throw new RuntimeException("flaky test failure");
       }

       @Test
       public void myStableTest() {
           driver().get("http://www.google.com");
           driver().findElement(By.name("q")).sendKeys("selenium-junit" + Keys.ENTER);
           new WebDriverWait(driver(), 5).until(ExpectedConditions.titleContains("selenium-junit"));
       }

       private static class ChromeDriverFactory implements WebDriverFactory {
           @Override
           public WebDriver create() {
               return new ChromeDriver();
           }
       }
    }

In this example we have a test class with two tests, `myStableTest` and `myFlakyTest`,
and one Rule, a `SeleniumRule`.

In `myStableTest` we are using the driver returned by `seleniumRule.getDriver()`
(which, for convenience, is wrapped in the method `driver()`)
to interact with the web UI of the System Under Test.
Thanks to `SeleniumRule`, the driver has already been initialized
(in this example, it will be a local instance of the browser Chrome),
and it will also be torn down automatically as soon as the test finishes.

`myFlakyTest` is simulating a flaky test by always throwing an exception.
Since the test is annotated with `@Flaky`, and `SeleniumRule` is configured to retry flaky tests
on failure (`.toRetryFlakyTestsOnFailure(2)`), then `myFlakyTest` will be executed 3 times
(1 standard execution + 2 retries, as per configuration).

All of the details regarding the setup/teardown and the retry logic are hidden behind three lines:

    @Rule
    public final SeleniumRule seleniumRule = new SeleniumRule(new ChromeDriverFactory())
       .toRetryFlakyTestsOnFailure(2); // retry each test max 2 times (max 3 executions in total)

`SeleniumRule` is the one and only class being part of the public API of this project.
It can be configured so that users can get only the features they need.
The most minimal configuration includes only the setup and tear down of a WebDriver:

    @Rule
    public final SeleniumRule seleniumRule = new SeleniumRule(new ChromeDriverFactory());

Please see the javadoc of `SeleniumRule` for an up-to-date example of usage.


## Internals

Internally, `SeleniumRule` is not a monolithic Rule implementing all of the features.
Rather, each feature is implemented by a `TestRule` on its own.
`SeleniumRule` simply acts as a collector (a `RuleChain`) of the rules configured:

- makes sure the sub-rules are run in the correct order
- provides clients with a configuration API to activate only the features, aka sub-rules, needed
