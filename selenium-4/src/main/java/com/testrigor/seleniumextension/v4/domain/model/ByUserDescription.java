package com.testrigor.seleniumextension.v4.domain.model;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import lombok.Getter;

@Getter
public class ByUserDescription extends By implements By.Remotable {
	private final String description;

	private final Parameters remoteParameters;

	public ByUserDescription(String value) {
		super();
		this.remoteParameters = new Parameters("user_description", value);
		this.description = value;
	}

	@Override public List<WebElement> findElements(SearchContext context) {
		return context.findElements(this);
	}

	@Override public WebElement findElement(SearchContext context) {
		return context.findElement(this);
	}

	@Override
	public String toString() {
		return "By.user_description: " + description;
	}
}
