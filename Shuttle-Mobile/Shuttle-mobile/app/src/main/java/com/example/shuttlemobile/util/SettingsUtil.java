package com.example.shuttlemobile.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.shuttlemobile.common.SettingsFragment;
import com.example.shuttlemobile.user.JWT;
import com.example.shuttlemobile.user.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class SettingsUtil {
    public static final String PREF_FILE = "ShuttleMobile.prefs.preferences";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_SURNAME = "user_surname";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_USER_LOCATION = "user_location";
    public static final String KEY_USER_BLOCKED = "user_blocked";
    public static final String KEY_USER_ACTIVE = "user_active";
    public static final String KEY_USER_PFP = "user_pfp";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String VAL_USER_ROLE_PASSENGER = "passenger";
    public static final String VAL_USER_ROLE_DRIVER = "driver";
    public static final String KEY_ACCESS_TOKEN = "access_token";

    public static final String KEY_CURRENT_RIDE_ID = "current_ride_id";

    private static SharedPreferences prefs = null;

    public static void init(SharedPreferences prefs) {
        SettingsUtil.prefs = prefs;
    }

    public static JWT getUserJWT() {
        Gson gson = new Gson();

        try {
            return gson.fromJson(
                    JWTDecoder.getPayloadJSON(SettingsUtil.get(SettingsUtil.KEY_ACCESS_TOKEN, "no-token")),
                    JWT.class
            );
        } catch (Exception e) {
            return null;
        }
    }
/*
    public static void setUser(User user) {
        SharedPreferences.Editor pe = prefs.edit();

        pe.putLong(KEY_USER_ID, user.getId());
        pe.putString(KEY_USER_EMAIL, user.getEmail());
        pe.putString(KEY_USER_NAME, user.getName());
        pe.putString(KEY_USER_SURNAME, user.getLastName());
        pe.putString(KEY_USER_PHONE, user.getPhone());
        pe.putString(KEY_USER_LOCATION, user.getLocation());
        pe.putBoolean(KEY_USER_BLOCKED, user.isBlocked());
        pe.putBoolean(KEY_USER_ACTIVE, user.isActive());
        pe.putString(KEY_USER_ROLE, user.getRole().toString());
        pe.putString(KEY_USER_PFP, user.getPfp());
        pe.commit();
    }

    public static User getUser() {
        Long id = prefs.getLong(KEY_USER_ID, -1);
        String email = prefs.getString(KEY_USER_EMAIL, "");
        String name = prefs.getString(KEY_USER_NAME, "");
        String lastName = prefs.getString(KEY_USER_SURNAME, "");
        String phone = prefs.getString(KEY_USER_PHONE, "");
        String location = prefs.getString(KEY_USER_LOCATION, "");
        boolean blocked = prefs.getBoolean(KEY_USER_BLOCKED, false);
        boolean active = prefs.getBoolean(KEY_USER_ACTIVE, false);
        User.Role role = User.Role.valueOf(prefs.getString(KEY_USER_ROLE, "Passenger"));
        String pfp = prefs.getString(KEY_USER_PFP, "");

        return new User(id, name, lastName, location, phone, email, "", pfp, blocked, active, role);
    }
*/

    public static void clearUser(){
        prefs.edit().clear().commit();
    }
    public static void put(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public static void put(String key, Long value) {
        prefs.edit().putLong(key, value).commit();
    }

    public static void put(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public static void put(String key, Integer value) {
        prefs.edit().putInt(key, value).commit();
    }

    public static void put(String key, Float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    public static String get(String key, String def) {
        return prefs.getString(key, def);
    }

    public static Long get(String key, Long def) {
        return prefs.getLong(key, def);
    }

    public static boolean get(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }

    public static Integer get(String key, Integer def) {
        return prefs.getInt(key, def);
    }

    public static Float get(String key, Float def) {
        return prefs.getFloat(key, def);
    }
}
