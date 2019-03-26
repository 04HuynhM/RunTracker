package com.runtracker.Fragments.MenuFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.runtracker.Adapters.GroupsRecyclerAdapter;
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

        String url = Constants.BASE_URL + "group/user/" + username;
        api.protectedGet(url, "Bearer " + authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    populateGroups(responseBody);
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
        if (!jsonBody.equals("")) {
            Gson gson = new Gson();
            Group[] members = gson.fromJson(jsonBody, Group[].class);
            GroupsRecyclerAdapter adapter = new GroupsRecyclerAdapter(members, getActivity());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(RecyclerView.VERTICAL);
            getActivity().runOnUiThread(() -> {
                recyclerView.setLayoutManager(llm);
                recyclerView.setAdapter(adapter);
            });
        }
    }

}
