package com.runtracker.MenuFragments;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.runtracker.R;

public class SettingsFragment extends PreferenceFragmentCompat {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_screen, rootKey);
    }

}
