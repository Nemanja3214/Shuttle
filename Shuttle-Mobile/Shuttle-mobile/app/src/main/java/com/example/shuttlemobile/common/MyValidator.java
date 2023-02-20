package com.example.shuttlemobile.common;

import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.route.RouteDTO;
import com.example.shuttlemobile.user.dto.UserEmailDTO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MyValidator {
    private static final String REGEX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public static LocalDateTime validateDateTime(String dateAsString, String name) throws MyValidatorException {
        if (dateAsString == null || dateAsString.equals("null")) {
            return null;
        }

        LocalDateTime t = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"));
            t = LocalDateTime.parse(dateAsString, formatter);
            return t;
        } catch (DateTimeParseException e) {
            throw new MyValidatorException(msgFormat(name));
        }
    }

    public static void validateLocation(LocationDTO location, String name) throws MyValidatorException {
        validateRequired(location.getLatitude(), name + ".latitude");
        validateRange(location.getLatitude().longValue(), name + ".latitude", -90L, 90L);
        validateRequired(location.getLongitude(), name + ".longitude");
        validateRange(location.getLongitude().longValue(), name + ".longitude", -180L, 180L);
    }

    public static void validateEmail(String value, String name) throws MyValidatorException {
        validatePattern(value, name, REGEX_EMAIL);
    }

    public static void validateRouteDTO(List<RouteDTO> locationList, String name) throws MyValidatorException {
        for (RouteDTO u : locationList) {
            validateRequired(u.getDeparture(), name + ".departure");
            validateRequired(u.getDestination(), name + ".destination");

            validateLocation(u.getDeparture(), name + ".departure");
            validateLocation(u.getDestination(), name + ".destination");
        }
    }

    public static void validateUserRef(List<UserEmailDTO> idEmailList, String name) throws MyValidatorException {
        for (UserEmailDTO u : idEmailList) {
            validateRequired(u.getId(), name + ".id");
            validateRequired(u.getEmail(), name + ".email");

            validateEmail(u.getEmail(), name + ".email");
            validateLength(u.getEmail(), name + ".email", 100);
        }
    }

    public static void validateRequired(Object o, String name) throws MyValidatorException {
        validateObj(o, obj -> obj == null, msgRequired(name));
    }

    public static void validateLength(String t, String name, int length) throws MyValidatorException {
        if (t == null)
            return;
        validateStr(t, s -> s.length() > length, msgLength(name, length));
    }

    public static void validateRange(Long t, String name, Long min, Long max) throws MyValidatorException {
        if (t == null)
            return;
        validateLong(t, l -> l < min || l > max, msgRange(name, min, max));
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
        return "Field (" + field + ") is required!";
    }

    public static String msgRange(String field, Long min, Long max) {
        return "Field (" + field + ") must be between " + min.toString() + " and " + max.toString() + "!";
    }

    public static void validateMatchingPassword(String password,String confirmPassword) throws MyValidatorException {
        if (!confirmPassword.equals(password)) throw new MyValidatorException("Passwords not matching");
    }
}
