package com.runtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.auth0.android.jwt.JWT;
import com.runtracker.Login.LoginActivity;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This controller has no view. It simply determines if there is a valid JWT stored in sharedpreferences
 * If there is an unexpired JWT, then you are "logged in" and navigated to the main menu
 * Otherwise you are brought to the login page
 */

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("preferences", MODE_PRIVATE);
        String authToken = prefs.getString("authToken", "");

        if (!authToken.equals("")) {
            JWT jwt = new JWT(authToken);
            Date expiresAt = jwt.getExpiresAt();
            if (Calendar.getInstance().getTime().before(expiresAt)) {
                Intent intent = new Intent(this, MainNavigatorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}