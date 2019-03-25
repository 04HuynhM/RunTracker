package com.runtracker.MenuFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.runtracker.Adapters.JoinedGroupsRecyclerAdapter;
import com.runtracker.Models.Group;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import java.io.IOException;

public class GroupsFragment extends Fragment {

    private RecyclerView recyclerView;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_groups, container, false);
        recyclerView = v.findViewById(R.id.groups_recyclerview);
        callApiForGroups();
        return v;
    }

    private void callApiForGroups() {
        ApiCalls api = new ApiCalls();

        SharedPreferences prefs = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String authToken = prefs.getString("authToken", "");

        JWT jwt = new JWT(authToken);
        String username = jwt.getClaim("username").asString();

        String url = Constants.BASE_URL + "group/" + username;
        api.protectedGet(url, "Bearer " + authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    populateGroups(response.body().string());
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "Something went wrong when getting groups",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void populateGroups(String jsonBody) {
        Gson gson = new Gson();
        Group[] members = gson.fromJson(jsonBody, Group[].class);
        JoinedGroupsRecyclerAdapter adapter = new JoinedGroupsRecyclerAdapter(members, getActivity());
        recyclerView.setAdapter(adapter);
    }

}
