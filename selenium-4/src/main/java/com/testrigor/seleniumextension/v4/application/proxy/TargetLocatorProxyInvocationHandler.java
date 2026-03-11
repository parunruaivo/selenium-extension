package com.testrigor.seleniumextension.v4.application.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;

import com.testrigor.seleniumextension.v4.TestRigor;
import com.testrigor.seleniumextension.v4.application.TestrigorDriver;
import com.testrigor.seleniumextension.v4.application.services.SeleniumExtensionService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TargetLocatorProxyInvocationHandler implements InvocationHandler {

	private final TargetLocator delegate;
	private final SeleniumExtensionService manager;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			Object result = method.invoke(delegate, args);
			boolean isProxy = result instanceof TestrigorDriver;
			boolean isWebDriver = result instanceof WebDriver;
			if (isWebDriver && !isProxy) {
				return TestRigor.create(manager);
			} else {
				return result;
			}
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
