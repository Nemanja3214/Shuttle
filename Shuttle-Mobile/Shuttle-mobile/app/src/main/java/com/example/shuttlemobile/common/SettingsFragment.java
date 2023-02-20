package com.example.shuttlemobile.common;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.shuttlemobile.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}