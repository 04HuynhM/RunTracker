package com.runtracker.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.runtracker.Login.CreateAccount.CreateAccountPart1Fragment;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.R;

public class LoginActivity extends AppCompatActivity {

    Button login;
    TextView createAccount;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login_button);
        createAccount = findViewById(R.id.login_button_create_account);
        email = findViewById(R.id.login_textfield_email);
        password = findViewById(R.id.login_textfield_password);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccountPart1Fragment fragment = new CreateAccountPart1Fragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.login_container, fragment)
                        .addToBackStack("create_account")
                        .commit();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        //TODO: Perform login with JWT and all that

        Intent intent = new Intent(this, MainNavigatorActivity.class);
        startActivity(intent);
    }
}
