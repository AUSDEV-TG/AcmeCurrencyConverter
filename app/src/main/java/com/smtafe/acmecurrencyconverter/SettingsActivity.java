package com.smtafe.acmecurrencyconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * AcmeCurrencyConverter SettingsActivity
 *
 * @author Tom Green
 * @version 1.0
 */

public class SettingsActivity extends AppCompatActivity {
    //Create the activity using the Settings activity and Settings fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }
}
