package com.runtracker.Fragments.Groups;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runtracker.Adapters.MyInvitesListAdapter;
import com.runtracker.Models.Group;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.AuthUtil;
import com.runtracker.Utilities.Constants;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyGroupInvitesFragment extends Fragment {

    private AuthUtil authUtil;
    private RecyclerView inviteList;

    public MyGroupInvitesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_group_invites, container, false);

        inviteList = v.findViewById(R.id.group_invites_recycler);
        authUtil = new AuthUtil(getActivity());
        String username = authUtil.getCurrentUser();
        String auth = authUtil.getAuthToken();

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
        MyInvitesListAdapter adapter = new MyInvitesListAdapter(groups, getActivity(), authUtil.getCurrentUser(), authUtil.getAuthToken());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        getActivity().runOnUiThread(() -> {
            inviteList.setLayoutManager(llm);
            inviteList.setAdapter(adapter);
        });
    }


}
