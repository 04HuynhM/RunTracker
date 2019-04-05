package com.runtracker.Login.CreateAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.runtracker.R;
import com.runtracker.Utilities.HelperMethods;

import androidx.fragment.app.Fragment;

/**
 * Controller for first screen of create account flow
 */
public class CreateAccountPart1Fragment extends Fragment {

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    public CreateAccountPart1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_account_part_1, container, false);

        username = view.findViewById(R.id.create_account_edittext_username);
        name = view.findViewById(R.id.create_account_name);
        email = view.findViewById(R.id.create_account_email);
        password = view.findViewById(R.id.create_account_password);
        confirmPassword = view.findViewById(R.id.create_account_confirm_password);
        Button continueButton = view.findViewById(R.id.create_account_button_continue);

        continueButton.setOnClickListener(view1 -> performContinue());

        return view;
    }

    // Put all data from fields into a bundle to pass to second screen of create account flow
    private void performContinue() {
        if (!validateInputs()) {
            return;
        }
        CreateAccountPart2Fragment fragment = new CreateAccountPart2Fragment();

        Bundle bundle = new Bundle();
        bundle.putString("username", username.getText().toString());
        bundle.putString("name", name.getText().toString());
        bundle.putString("email", email.getText().toString());
        bundle.putString("password", password.getText().toString());

        fragment.setArguments(bundle);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.login_container, fragment)
                .addToBackStack("account_setup")
                .commit();
    }

    // Check that all fields have been filled, inflate alertDialog if empty fields
    private boolean validateInputs() {
        HelperMethods help = new HelperMethods();

        if (username.getText().toString().isEmpty() ||
            name.getText().toString().isEmpty() ||
            email.getText().toString().isEmpty() ||
            password.getText().toString().isEmpty() ||
            confirmPassword.getText().toString().isEmpty()) {

            help.createOkayAlert("Please fill out all fields.", this.getActivity());
            return false;
        }

        if (!(password.getText().toString().equals(confirmPassword.getText().toString()))) {
            help.createOkayAlert("Passwords to not match", this.getActivity());
            return false;
        }

        return true;
    }
}
