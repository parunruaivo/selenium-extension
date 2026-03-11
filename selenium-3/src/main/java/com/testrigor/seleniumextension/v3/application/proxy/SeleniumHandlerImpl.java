package com.testrigor.seleniumextension.v3.application.proxy;

import static lombok.AccessLevel.PROTECTED;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.v3.application.services.SeleniumExtensionService;
import com.testrigor.seleniumextension.v3.domain.model.ByUserDescription;

import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PROTECTED, makeFinal = true)
@SuppressWarnings("PMD.ShortVariable")
public class SeleniumHandlerImpl implements SeleniumHandler {

	SeleniumExtensionService seleniumExtensionService;
	RemoteWebDriver driver;

	public SeleniumHandlerImpl(SeleniumExtensionService aSeleniumExtensionService) {
		this.seleniumExtensionService = aSeleniumExtensionService;
		this.driver = aSeleniumExtensionService.getDriver();
	}

	@Override
	public WebElement findElement(By by) {
		if (by instanceof ByUserDescription) {
			return seleniumExtensionService.findByUserDescription(((ByUserDescription) by).getDescription());
		}
		return driver.findElement(by);
	}

	public static String getMethodName(final int depth) {
		return StackWalker
			.getInstance()
			.walk(stream -> stream.skip(depth).findFirst().get())
			.getMethodName();
	}

	@Override
	public List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	@Override
	public WebElement wrapElement(WebElement element, ClassLoader loader) {
		WebElementProxyHandler elementProxyHandler = new WebElementProxyHandler(element, seleniumExtensionService);
		return ProxyFactory.createWebElementProxy(loader, elementProxyHandler);
	}

	@Override
	public WebDriver.TargetLocator wrapTarget(WebDriver.TargetLocator locator, ClassLoader loader) {
		TargetLocatorProxyInvocationHandler handler = new TargetLocatorProxyInvocationHandler(locator, seleniumExtensionService);
		return ProxyFactory.createTargetLocatorProxy(loader, handler);
	}
}
