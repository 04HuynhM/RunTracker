package com.runtracker.Login.CreateAccount;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.Models.User;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;
import com.runtracker.Utilities.HelperMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountPart2Fragment extends Fragment {

    private EditText currentWeight;
    private EditText weightGoal;
    private EditText dailyStepGoal;
    private ApiCalls api = new ApiCalls();
    private Context context;

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

        context = getActivity().getApplicationContext();

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
        int currentWeightInt;
        int weightGoalInt;
        int stepGoal;
        if (currentWeight.getText().toString().isEmpty()) {
            currentWeightInt = 0;
        } else {
            currentWeightInt = Integer.parseInt(currentWeight.getText().toString());
        }
        if (weightGoal.getText().toString().isEmpty()) {
            weightGoalInt = 0;
        } else {
            weightGoalInt = Integer.parseInt(weightGoal.getText().toString());
        }
        if (dailyStepGoal.getText().toString().isEmpty()) {
            stepGoal = 0;
        } else {
            stepGoal = Integer.parseInt(dailyStepGoal.getText().toString());
        }
        int[] joinedGroups = {};
        int[] groupInvitations = {};
        String profilePicture = "";

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

        final User user = new User(username,
                             name,
                             email,
                             password,
                             profilePicture,
                             false,
                             currentWeightInt,
                             weightGoalInt,
                             stepGoal,
                             groupInvitations,
                             joinedGroups);

        Gson gson = new Gson();

        final String requestBody = gson.toJson(user);
        System.out.println(requestBody);

        String url = Constants.BASE_URL + "user";
            api.post(requestBody, url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        loginAndGo(user.getUsername(), user.getPassword());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("POST USER FAILED", response.body().string());
                }
            }
        });
    }

    private void loginAndGo(String usernameOrEmail, String password) throws JSONException {
        JSONObject loginDetails = new JSONObject();
        loginDetails.put("usernameOrEmail", usernameOrEmail);
        loginDetails.put("password", password);

        String loginUrl = Constants.BASE_URL + "user/login";
        api.post(loginDetails.toString(), loginUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
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
