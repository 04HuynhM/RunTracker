package com.runtracker.Fragments.Groups;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.runtracker.Adapters.GroupMemberListAdapter;
import com.runtracker.Models.User;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SingleGroupFragment extends Fragment {

    private RecyclerView recyclerView;

    public SingleGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_group, container, false);

        recyclerView = v.findViewById(R.id.single_group_members_recyclerview);
        callApiForMembers();

        return v;
    }

    private void callApiForMembers() {
        SharedPreferences prefs = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String authToken = prefs.getString("authToken", "");

        Bundle bundle = this.getArguments();
        String groupId = bundle.getString("groupId");

        ApiCalls apiCalls = new ApiCalls();
        String url = Constants.BASE_URL + "group/" + groupId + "/members";
        apiCalls.protectedGet(url, "Bearer " + authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    populateRecyclerView(response.body().string());
                }
            }
        });
    }

    private void populateRecyclerView(String body) {
        Gson gson = new Gson();
        User[] members = gson.fromJson(body, User[].class);
        GroupMemberListAdapter adapter = new GroupMemberListAdapter(members, getActivity());
        recyclerView.setAdapter(adapter);
    }

}
