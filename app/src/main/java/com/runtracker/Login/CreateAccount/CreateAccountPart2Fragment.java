package com.runtracker.Login.CreateAccount;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.runtracker.Models.User;
import com.runtracker.R;
import com.runtracker.Utilities.HelperMethods;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountPart2Fragment extends Fragment {

    private EditText currentWeight;
    private EditText weightGoal;
    private EditText dailyStepGoal;

    public CreateAccountPart2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account_part_2, container, false);

        currentWeight = view.findViewById(R.id.onboarding_textfield_current_weight);
        weightGoal = view.findViewById(R.id.onboarding_textfield_weight_goal);
        dailyStepGoal = view.findViewById(R.id.onboarding_textfield_step_goal);
        Button createAccount = view.findViewById(R.id.onboarding_button_create_account);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAccountCreation();
            }
        });

        return view;
    }

    private void performAccountCreation() {
        String username;
        String name;
        String email;
        String password;
        int currentWeightInt = Integer.parseInt(currentWeight.getText().toString());
        int weightGoalInt = Integer.parseInt(weightGoal.getText().toString());
        int stepGoal = Integer.parseInt(dailyStepGoal.getText().toString());

        HelperMethods help = new HelperMethods();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            username = bundle.getString("username");
            name = bundle.getString("name");
            email = bundle.getString("email");
            password = bundle.getString("password");
        } else {
            help.createOkayAlert(
                    "Bundle didn't contain user information. Cannot create an account.",
                    this.getContext());
            return;
        }

        User user = new User(username, name, email, password, currentWeightInt, weightGoalInt, stepGoal);

        //TODO: Add user to database and login user

    }

}
