package com.testrigor.seleniumextension.commons.application.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.SeleniumExtensionException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonHelpers {

	private static final ObjectMapper MAPPER = new ObjectMapper()
		.registerModule(new JavaTimeModule())
		.registerModule(new JodaModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public static String serializeJson(Object value) {
		if (value == null) {
			return "";
		}
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new SeleniumExtensionException("Failed to serialize value to JSON", e);
		}
	}

	public static <T> T deserializeJson(String json, TypeReference<T> typeReference) {
		String effectiveJson = (json == null || json.isBlank()) ? "{}" : json;
		try {
			return MAPPER.readValue(effectiveJson, typeReference);
		} catch (JsonProcessingException e) {
			throw new SeleniumExtensionException("Failed to deserialize JSON", e);
		}
	}
}
