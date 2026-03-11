package com.testrigor.seleniumextension.v3.domain.model;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import lombok.Getter;

@Getter
public class ByUserDescription extends By {

	private final String description;

	public ByUserDescription(String value) {
		super();
		this.description = value;
	}

	@Override
	public List<WebElement> findElements(SearchContext context) {
		return context.findElements(this);
	}

	@Override
	public WebElement findElement(SearchContext context) {
		return context.findElement(this);
	}

	@Override
	public String toString() {
		return "By.user_description: " + description;
	}
}
