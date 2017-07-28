package com.udacity.julius.bakersapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_recipe);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            setSummary(preference, getContext());
        }
    }

    private void setSummary(Preference preference, Context context) {

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String s = sharedPreferences.getString(context.getResources().getString(R.string.pref_recipe_key), context.getResources().getString(R.string.pref_recipe_nutella_pie_value));
            int index = listPreference.findIndexOfValue(s);
            listPreference.setSummary(listPreference.getEntries()[index]);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            String s = sharedPreferences.getString(key, getContext().getResources().getString(R.string.pref_recipe_nutella_pie_value));
            int index = listPreference.findIndexOfValue(s);
            Log.e("Preference Change", "Preference changed with value of " + s);
            listPreference.setSummary(listPreference.getEntries()[index]);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }
}
