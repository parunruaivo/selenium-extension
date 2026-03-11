# Selenium Extension library

Implementation of the library using **testRigor** service.

####Installation
There are two repositories, one using the selenium library version 3.x
```
<dependency>
	<groupId>com.testrigor</groupId>
	<artifactId>selenium-extension-3</artifactId>
	<version>0.2.0-SNAPSHOT</version>
</dependency>
```
and the other using the version 4.x
```
<dependency>
	<groupId>com.testrigor</groupId>
	<artifactId>selenium-extension-4</artifactId>
	<version>0.2.0-SNAPSHOT</version>
</dependency>
```

For downloading the **SNAPSHOT** version the following maven `<repository>` is needed
```
<repositories>
    <repository>
        <id>ossrh</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

####Usage
Given the following example
```
@Test
public void input_contains_value() {
    RemoteWebDriver driver = new ChromeDriver();
    driver.get("some page");
    WebElement element = driver.findElement(By.id("firstNameInput"));
    assertThat(element.getText()).contains("John");
}
```
we are going to pass the `RemoteWebDriver` to the selenium extension wrapper
```
@Test
public void input_contains_value() {
    RemoteWebDriver driver = new ChromeDriver();
    TestrigorDriver extensionDriver = TestRigor.extendDriver(driver, "API_TOKEN");
    extensionDriver.setTestCaseName("test"); //This needs to be configure for each @Test
    
    
    driver.get("some page");
    WebElement element = extensionDriver.findElement(By.id("firstNameInput"));
    assertThat(element.getText()).contains("John");
}
```
Make sure to call the method
```
setTestCaseName(String testCaseName);
```
on each `@Test` annotated method, this is for creating a relationship between the locators and the test.


For more information go to https://testrigor.com/selenium-extension
