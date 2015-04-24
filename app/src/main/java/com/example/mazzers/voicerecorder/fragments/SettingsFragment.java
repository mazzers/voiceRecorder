package com.example.mazzers.voicerecorder.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.mazzers.voicerecorder.R;

/**
 * Created by mazzers on 13. 4. 2015.
 */
public class SettingsFragment extends PreferenceFragment {
    public static final String SETTINGS_TAG = "SETTINGS_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

    }
}
