package com.thaid.asylum;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();

        sharedPreferences = getPreferenceManager().getSharedPreferences();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Map<String, ?> preferencesMap = sharedPreferences.getAll();
        for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
            Preference pref = findPreference(preferenceEntry.getKey());
            if (pref instanceof EditTextPreference) {
                updateSummary((EditTextPreference) pref);
            }
        }
    }

    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof EditTextPreference) {
            updateSummary((EditTextPreference) pref);
        }
    }

    private void updateSummary(EditTextPreference preference) {
        preference.setSummary(preference.getText());
    }
}
