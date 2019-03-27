package com.runtracker.Fragments.Groups;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.runtracker.Models.Group;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyGroupInvitesFragment extends Fragment {


    public MyGroupInvitesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_group_invites, container, false);

        String auth = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE).getString("authToken", "");
        JWT jwt = new JWT(auth);

        String username = jwt.getClaim("username").asString();

        getGroupsFromInvites(username, auth);


        return v;
    }

    private void getGroupsFromInvites(String username, String auth) {
        ApiCalls api = new ApiCalls();
        String url = Constants.BASE_URL + "group/invites/" + username;
        api.protectedGet(url, "Bearer " + auth, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    populateInvitesList(response.body().string());
                } else if (response.code() == 404) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "No invitations found", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void populateInvitesList(String json) {
        Gson gson = new Gson();
        Group[] groups = gson.fromJson(json, Group[].class);


    }


}
