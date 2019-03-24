package com.runtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.runtracker.Login.LoginActivity;
import com.runtracker.MenuFragments.ProfileFragment;
import com.runtracker.MenuFragments.SettingsFragment;
import com.runtracker.MenuFragments.RunsFragment;
import com.runtracker.Utilities.HelperMethods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainNavigatorActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.bottom_navigator_profile:
                        loadFragment(new ProfileFragment());
                        return true;
                    case R.id.bottom_navigator_run_tracker:
                        loadFragment(new RunsFragment());
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

        loadFragment(new RunsFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_option :
                DialogInterface.OnClickListener onClickListener = (dialogInterface, choice) -> {
                    switch (choice) {
                        case DialogInterface.BUTTON_POSITIVE :
                            SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE);
                            prefs.edit().remove("authToken").apply();
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogInterface.cancel();
                            break;
                    }
                };
                HelperMethods helperMethods = new HelperMethods();
                helperMethods.createYesNoAlert("Are you sure you want to log out?",
                                            this,
                                            onClickListener);
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
