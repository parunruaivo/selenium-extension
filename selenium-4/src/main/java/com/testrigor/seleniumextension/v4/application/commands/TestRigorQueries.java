package com.testrigor.seleniumextension.v4.application.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.testrigor.seleniumextension.v4.application.TestrigorDriver;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Query facade for commands that return values to Java.
 * <p>
 * Any method parameter describing an element follows testRigor's referencing syntax:
 * https://testrigor.com/docs/language#referencing
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TestRigorQueries {

	TestrigorDriver driver;

	public static TestRigorQueries queries(TestrigorDriver driver) {
		return new TestRigorQueries(driver);
	}

	public static TestRigorQueries with(TestrigorDriver driver) {
		return queries(driver);
	}

	public String grabValue(String elementDescription) {
		return driver.grabValue(elementDescription);
	}

	/** Grabs value from an explicitly typed element, e.g. input/button/table. */
	public String grabValue(String elementType, String elementDescription) {
		return driver.grabValue("grab value from " + normalizeElementType(elementType) + " " + quotedSegment(elementDescription));
	}

	/** Contextual entry point that does not require a base containing text. */
	public ContextualGrab grabValue() {
		return new ContextualGrab(null, null, null, null, null, null, null, null);
	}

	/** Fluent contextual grab builder. Finalize with {@link ContextualGrab#get()}. */
	public ContextualGrab grabValueFrom(String elementDescription) {
		return new ContextualGrab(elementDescription, null, null, null, null, null, null, null);
	}

	/** Fluent builder entry point for extracting by simple template from an element description. */
	public GrabByTemplate grabValueByTemplate(String template) {
		return new GrabByTemplate(template);
	}

	/** Grabs using template against page-level content. */
	public String grabValueByTemplateFromPage(String template) {
		return driver.grabValue("grab value of template " + quotedSegment(template));
	}

	/**
	 * Grabs value by template from an element description.
	 */
	public String grabValueByTemplate(String template, String elementDescription) {
		return driver.grabValue("grab value of template " + quotedSegment(template) + " from " + quotedSegment(elementDescription));
	}

	/** Grabs value by template from an explicitly typed element. */
	public String grabValueByTemplate(String template, String elementType, String elementDescription) {
		return driver.grabValue("grab value of template " + quotedSegment(template) + " from "
			+ normalizeElementType(elementType) + " " + quotedSegment(elementDescription));
	}

	/** Grabs value by regex against page-level content. */
	public String grabValueOfRegex(String regex) {
		return driver.grabValue("grab value of regex " + quotedSegment(regex));
	}

	/** Grabs value by regex from an element description. */
	public String grabValueOfRegex(String regex, String elementDescription) {
		return driver.grabValue("grab value of regex " + quotedSegment(regex) + " from " + quotedSegment(elementDescription));
	}

	/** Grabs value by regex from an explicitly typed element. */
	public String grabValueOfRegex(String regex, String elementType, String elementDescription) {
		return driver.grabValue("grab value of regex " + quotedSegment(regex) + " from "
			+ normalizeElementType(elementType) + " " + quotedSegment(elementDescription));
	}

	/** Backward-compatible alias for {@link #grabValueOfRegex(String, String)}. */
	public String grabValueByRegex(String regex, String elementDescription) {
		return grabValueOfRegex(regex, elementDescription);
	}

	public String grabValueOfAttribute(String attribute, String elementDescription) {
		return driver.grabValue("grab value of attribute " + quotedSegment(attribute) + " from " + quotedSegment(elementDescription));
	}

	public String grabValueOfCssProperty(String cssProperty, String elementDescription) {
		return driver.grabValue("grab value of css property " + quotedSegment(cssProperty) + " from " + quotedSegment(elementDescription));
	}

	public List<String> grabValuesFromTableAtFirstColumn(String tableDescription) {
		String raw = driver.grabValue("grab values from table " + quotedSegment(tableDescription) + " at first column");
		return parseListResult(raw);
	}

	public List<String> grabValuesFromTableAtFirstRow(String tableDescription) {
		String raw = driver.grabValue("grab values from table " + quotedSegment(tableDescription) + " at first row");
		return parseListResult(raw);
	}

	private static List<String> parseListResult(String raw) {
		if (raw == null || raw.trim().isEmpty()) {
			return Collections.emptyList();
		}
		String trimmed = raw.trim();
		if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
			String content = trimmed.substring(1, trimmed.length() - 1).trim();
			if (content.isEmpty()) {
				return Collections.emptyList();
			}
			return Arrays.stream(content.split("\\s*,\\s*"))
				.map(String::trim)
				.filter(value -> !value.isEmpty())
				.collect(Collectors.toList());
		}
		return Arrays.stream(trimmed.split("\\r?\\n"))
			.map(String::trim)
			.filter(value -> !value.isEmpty())
			.collect(Collectors.toList());
	}

	@RequiredArgsConstructor
	public final class GrabByTemplate {
		final String template;

		public String from(String elementDescription) {
			return grabValueByTemplate(template, elementDescription);
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public final class ContextualGrab {
		final String elementDescription;
		final String elementType;
		final String tableDescription;
		final String rowContaining;
		final String columnDescription;
		final String contextDescription;
		final Anchor belowAnchor;
		final Anchor rightAnchor;

		public ContextualGrab ofType(String elementType) {
			return new ContextualGrab(elementDescription, normalizeElementType(elementType), tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ContextualGrab withinTable(String tableDescription) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ContextualGrab rowContaining(String rowContaining) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ContextualGrab column(String columnDescription) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ContextualGrab inContext(String contextDescription) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ContextualGrab below(String anchorDescription) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.below(anchorDescription), rightAnchor);
		}

		public ContextualGrab roughlyBelow(String anchorDescription) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.roughlyBelow(anchorDescription), rightAnchor);
		}

		public ContextualGrab completelyBelow(String anchorDescription) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.completelyBelow(anchorDescription), rightAnchor);
		}

		public ContextualGrab rightOf(String anchorDescription) {
			return new ContextualGrab(elementDescription, elementType, tableDescription, rowContaining, columnDescription, contextDescription,
				belowAnchor, Anchor.rightOf(anchorDescription));
		}

		/** Readability no-op for fluent chaining. */
		public ContextualGrab and() {
			return this;
		}

		public String get() {
			return grabValue(renderElementDescription());
		}

		public String byTemplate(String template) {
			return grabValue(buildTemplateStep(template));
		}

		public String byRegex(String regex) {
			return grabValue(buildRegexStep(regex));
		}

		private String renderElementDescription() {
			String baseDescriptor = elementType == null ? "element" : elementType;
			StringBuilder line = new StringBuilder(baseDescriptor);
			if (tableDescription != null) {
				line.append(" within the context of table ").append(quotedSegment(tableDescription));
				if (rowContaining != null) {
					line.append(" at row containing ").append(quotedSegment(rowContaining));
				}
				if (columnDescription != null) {
					if (rowContaining != null) {
						line.append(" and");
					} else {
						line.append(" at");
					}
					line.append(" column ").append(quotedSegment(columnDescription));
				}
			} else if (contextDescription != null) {
				line.append(" within the context of ").append(quotedSegment(contextDescription));
			}

			if (belowAnchor != null) {
				line.append(" ").append(belowAnchor.render());
			}
			if (rightAnchor != null) {
				line.append(belowAnchor == null ? " " : " and ").append(rightAnchor.render());
			}

			if (line.toString().equals(baseDescriptor)) {
				if (elementDescription == null) {
					return baseDescriptor;
				}
				if (elementType != null) {
					return elementType + " " + quotedSegment(elementDescription);
				}
				return elementDescription;
			}
			if (elementDescription == null || elementDescription.isBlank()) {
				return line.toString();
			}
			line.append(" containing ").append(quotedSegment(elementDescription));
			return line.toString();
		}

		private String renderContextOnly() {
			StringBuilder line = new StringBuilder();
			if (tableDescription != null) {
				line.append("within the context of table ").append(quotedSegment(tableDescription));
				if (rowContaining != null) {
					line.append(" at row containing ").append(quotedSegment(rowContaining));
				}
				if (columnDescription != null) {
					line.append(rowContaining != null ? " and" : " at")
						.append(" column ").append(quotedSegment(columnDescription));
				}
			} else if (contextDescription != null) {
				line.append("within the context of ").append(quotedSegment(contextDescription));
			}

			if (belowAnchor != null) {
				if (line.length() > 0) {
					line.append(" ");
				}
				line.append(belowAnchor.render());
			}
			if (rightAnchor != null) {
				if (line.length() > 0) {
					line.append(belowAnchor != null ? " and " : " ");
				}
				line.append(rightAnchor.render());
			}
			if (elementType != null && !elementType.isBlank()) {
				if (line.length() > 0) {
					line.insert(0, elementType + " ");
				} else {
					line.append(elementType);
				}
			}
			if (line.length() > 0 && elementDescription != null && !elementDescription.isBlank()) {
				line.append(" containing ").append(quotedSegment(elementDescription));
			}
			return line.toString();
		}

		private String buildRegexStep(String regex) {
			String context = renderContextOnly();
			if (!context.isBlank()) {
				if (elementType != null) {
					return "grab value of regex " + quotedSegment(regex) + " from " + context;
				}
				return "grab value of regex " + quotedSegment(regex) + " from element " + context;
			}
			if (elementDescription == null || elementDescription.isBlank()) {
				return "grab value of regex " + quotedSegment(regex);
			}
			if (elementType != null) {
				return "grab value of regex " + quotedSegment(regex) + " from " + elementType + " " + quotedSegment(elementDescription);
			}
			return "grab value of regex " + quotedSegment(regex) + " from " + quotedSegment(elementDescription);
		}

		private String buildTemplateStep(String template) {
			String context = renderContextOnly();
			if (!context.isBlank()) {
				if (elementType != null) {
					return "grab value of template " + quotedSegment(template) + " from " + context;
				}
				return "grab value of template " + quotedSegment(template) + " from element " + context;
			}
			if (elementDescription == null || elementDescription.isBlank()) {
				return "grab value of template " + quotedSegment(template);
			}
			if (elementType != null) {
				return "grab value of template " + quotedSegment(template) + " from " + elementType + " " + quotedSegment(elementDescription);
			}
			return "grab value of template " + quotedSegment(template) + " from " + quotedSegment(elementDescription);
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class Anchor {
		final String relationPrefix;
		final String anchorDescription;

		static Anchor below(String anchorDescription) {
			return new Anchor("below ", anchorDescription);
		}

		static Anchor roughlyBelow(String anchorDescription) {
			return new Anchor("roughly below ", anchorDescription);
		}

		static Anchor completelyBelow(String anchorDescription) {
			return new Anchor("completely below ", anchorDescription);
		}

		static Anchor rightOf(String anchorDescription) {
			return new Anchor("to the right of ", anchorDescription);
		}

		String render() {
			return relationPrefix + quotedSegment(anchorDescription);
		}
	}

	private static String normalizeElementType(String elementType) {
		if (elementType == null || elementType.trim().isEmpty()) {
			throw new IllegalArgumentException("Element type must not be blank");
		}
		return elementType.trim().toLowerCase();
	}

	private static String quotedSegment(String value) {
		String safe = value == null ? "" : value.replace("\"", "\\\"");
		return "\"" + safe + "\"";
	}
}
