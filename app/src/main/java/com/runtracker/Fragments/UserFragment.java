package com.runtracker.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.bumptech.glide.Glide;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private TextView name;
    private CircleImageView profilePicture;
    private ImageView backgroundPicture;
    private TextView usernameTextView;
    private TextView currentWeight;
    private TextView weightGoal;
    private TextView averageDailySteps;
    private TextView dailyStepGoal;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        name = view.findViewById(R.id.user_name);
        profilePicture = view.findViewById(R.id.user_circle_imageview);
        backgroundPicture = view.findViewById(R.id.user_header_image);
        usernameTextView = view.findViewById(R.id.user_username_value);
        currentWeight = view.findViewById(R.id.user_current_weight_value);
        weightGoal = view.findViewById(R.id.user_weight_goal_value);
        dailyStepGoal = view.findViewById(R.id.user_step_goal_value);
        averageDailySteps = view.findViewById(R.id.user_average_steps_value);

        String username;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            username = bundle.getString("username");
        } else {
            username = "";
        }

        ApiCalls api = new ApiCalls();
        String url = Constants.BASE_URL + "user/" + username;
        api.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject jsonBody = new JSONObject(response.body().string());
                            usernameTextView.setText(jsonBody.getString("username"));
                            name.setText(jsonBody.getString("name"));
                            currentWeight.setText(jsonBody.getString("currentWeight"));
                            weightGoal.setText(jsonBody.getString("weightGoal"));
                            dailyStepGoal.setText(jsonBody.getString("dailyStepGoal"));
                            String picUrl = jsonBody.getString("profilePicture");
                            if (!picUrl.equals("")) {
                                Glide.with(view.getContext())
                                        .load(picUrl)
                                        .placeholder(R.drawable.man)
                                        .into(profilePicture);
                                profilePicture.setVisibility(View.VISIBLE);
                            } else {
                                profilePicture.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        return view;
    }
}
