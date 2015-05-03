package com.example.mazzers.voicerecorder.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.mazzers.voicerecorder.R;

/**
 * Fragment with settings
 *
 * @author Vitaliy Vaschehnko
 *         PRJ 5
 */
public class SettingsFragment extends PreferenceFragment {
    public static final String SETTINGS_TAG = "SETTINGS_TAG"; //fragment tag

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general); // set settings resource from xml file

    }
}
