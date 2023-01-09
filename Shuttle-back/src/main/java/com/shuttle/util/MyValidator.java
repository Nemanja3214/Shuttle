package com.shuttle.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;


public class MyValidator {
	public static void validateRequired(Object o, String name) throws MyValidatorException {
		validateObj(o, obj -> obj == null, msgRequired(name));
	}

	public static void validateLength(String t, String name, int length) throws MyValidatorException {
		if (t == null)
			return;
		validateStr(t, s -> s.length() > length, msgLength(name, length));
	}

	public static void validatePattern(String t, String name, String pattern) throws MyValidatorException {
		if (t == null)
			return;
		validateStr(t, s -> !Pattern.matches(pattern, t), msgFormat(name));
	}

	public static void validateStr(String t, Predicate<String> ruleForBreak, String message)
			throws MyValidatorException {
		if (ruleForBreak.test(t)) {
			throw new MyValidatorException(message);
		}
	}

	public static void validateLong(Long t, Predicate<Long> ruleForBreak, String message) throws MyValidatorException {
		if (ruleForBreak.test(t)) {
			throw new MyValidatorException(message);
		}
	}

	public static void validateObj(Object t, Predicate<Object> ruleForBreak, String message)
			throws MyValidatorException {
		if (ruleForBreak.test(t)) {
			throw new MyValidatorException(message);
		}
	}

	public static String msgLength(String field, int length) {
		return "Field (" + field + ") cannot be longer than " + String.valueOf(length) + " characters!";
	}

	public static String msgFormat(String field) {
		return "Field (" + field + ") format is not valid!";
	}

	public static String msgRequired(String field) {
		return "Field (" + field + ") format is required!";
	}
}
