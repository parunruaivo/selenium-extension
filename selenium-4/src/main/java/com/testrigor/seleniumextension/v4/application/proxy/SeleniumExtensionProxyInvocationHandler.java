package com.testrigor.seleniumextension.v4.application.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

import com.testrigor.seleniumextension.v4.application.services.SeleniumExtensionService;

public class SeleniumExtensionProxyInvocationHandler extends SeleniumHandlerImpl implements InvocationHandler {

	SeleniumExtensionService manager;

	public SeleniumExtensionProxyInvocationHandler(SeleniumExtensionService aManager) {
		super(aManager);

		this.manager = aManager;
	}

	@Override
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		switch (method.getName()) {
			case "findElement": {
				WebElement element = findElement((By) args[0]);
				if (element != null) {
					return wrapElement(element, loader);
				}
				return method.invoke(driver, args);
			}
			case "findElements": {
				List<WebElement> elements = findElements((By) args[0]);
				return elements.stream().map(it -> wrapElement(it, loader)).collect(Collectors.toList());
			}
			case "getManager":
				return manager;
			case "getDelegate":
				return driver;
			case "setTestCaseName": {
				manager.setTestCaseName((String) args[0]);
				return null;
			}
			case "setTestContext": {
				manager.setTestContext((String) args[0]);
				return null;
			}
			case "clearTestContext": {
				manager.clearTestContext();
				return null;
			}
			case "executePrompt": {
				manager.executePrompt((String) args[0]);
				return null;
			}
			case "executeAction":
				return manager.executeAction((String) args[0], castParameters(args[1]));
			case "click":
				manager.click((String) args[0]);
				return null;
			case "checkPageContains":
				manager.checkPageContains((String) args[0]);
				return null;
			case "grabValue":
				return manager.grabValue((String) args[0]);
			case "get": {
				Object result = method.invoke(driver, args);
				manager.startStreamingIfNeeded();
				return result;
			}
			case "quit":
			case "close": {
				manager.closeConnection();
				return method.invoke(driver, args);
			}
			case "switchTo":
				TargetLocator switched = (TargetLocator) method.invoke(driver, args);
				return wrapTarget(switched, loader);
			default:
				return method.invoke(driver, args);
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> castParameters(Object value) {
		return value == null ? Map.of() : (Map<String, Object>) value;
	}
}
