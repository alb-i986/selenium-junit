# Selenium-JUnit4

Selenium testing doesn't have to be hard.

The aim of this project is to make it easy by providing a set of easy-to-setup and easy-to-configure [JUnit Rules](https://github.com/junit-team/junit4/wiki/Rules) so that clients can focus on their core business: writing tests.


## Features

- Setup/tear down of WebDriver's
- Test reporting (in HTML or plain text)
- Screenshots on failure


## Example of usage

    public class MySeleniumTest {
       @Rule
       public final SeleniumRule seleniumRule = SeleniumRule.configure(new ChromeDriverFactory())
            .withTestLogger(Logger.getLogger("my.logger"))
            .toTakeScreenshotOnFailure(OutputType.BASE64)
            .build();

       protected final WebDriver driver() {
           return seleniumRule.getDriver();
       }

       @Test
       public void myTest() {
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

Please see the javadoc of `SeleniumRule` for an up-to-date example.