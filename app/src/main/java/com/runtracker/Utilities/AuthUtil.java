package com.runtracker.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.jwt.JWT;

public class AuthUtil {

    private SharedPreferences prefs;
    private JWT jwt;
    private String currentUser;
    private String authToken;

    public AuthUtil(Context context) {
        this.prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        this.authToken = prefs.getString("authToken", "");
        this.jwt = new JWT(authToken);
        this.currentUser = jwt.getClaim("username").asString();
    }

    public JWT getJwt() {
        return jwt;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }
}
