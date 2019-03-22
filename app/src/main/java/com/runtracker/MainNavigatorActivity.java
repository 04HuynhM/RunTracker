package com.runtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.runtracker.MenuFragments.ProfileFragment;
import com.runtracker.MenuFragments.RunTrackerFragment;
import com.runtracker.MenuFragments.SettingsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.widget.Toast;

public class MainNavigatorActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.bottom_navigator_profile:
                        loadFragment(new ProfileFragment());
                        return true;
                    case R.id.bottom_navigator_run_tracker:
                        loadFragment(new RunTrackerFragment());
                        return true;
                    case R.id.bottom_navigator_settings:
                        loadFragment(new SettingsFragment());
                        return true;
                }
                return false;
            };

    private void loadFragment(Fragment fragment) {
        if (fragment!=null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigator);

        BottomNavigationView navigation = findViewById(R.id.main_navigator);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String authToken = prefs.getString("authToken", "");

        Toast.makeText(this, "Auth token is: " + authToken, Toast.LENGTH_SHORT).show();
    }
}
