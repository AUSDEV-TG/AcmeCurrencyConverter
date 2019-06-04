package com.smtafe.acmecurrencyconverter;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * AcmeCurrencyConverter SettingsFragment
 *
 * @author Tom Green
 * @version 1.0
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    //Create Preferences View using the xml/preferences.xml resource
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
