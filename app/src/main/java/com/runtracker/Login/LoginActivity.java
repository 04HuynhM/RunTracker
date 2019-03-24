package com.runtracker.Login;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.auth0.android.jwt.JWT;
import com.runtracker.Login.CreateAccount.CreateAccountPart1Fragment;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    Button login;
    TextView createAccount;
    EditText email;
    EditText password;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        login = findViewById(R.id.login_button);
        createAccount = findViewById(R.id.login_button_create_account);
        email = findViewById(R.id.login_textfield_email);
        password = findViewById(R.id.login_textfield_password);

        email.setText("martin");
        password.setText("password");

        createAccount.setOnClickListener(view -> {
            CreateAccountPart1Fragment fragment = new CreateAccountPart1Fragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_container, fragment)
                    .addToBackStack("create_account")
                    .commit();
        });

        login.setOnClickListener(view -> {
            try {
                performLogin();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void performLogin() throws JSONException {
        ApiCalls api = new ApiCalls();
        String usernameOrEmail = email.getText().toString();
        String passwordString = password.getText().toString();

        JSONObject loginDetails = new JSONObject();
        loginDetails.put("usernameOrEmail", usernameOrEmail);
        loginDetails.put("password", passwordString);

        String loginUrl = Constants.BASE_URL + "user/login";
        api.post(loginDetails.toString(), loginUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    String responseString = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String authToken = jsonObject.getString("token");
                        prefs.edit().putString("authToken", authToken).apply();

                        Intent intent = new Intent(context, MainNavigatorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
