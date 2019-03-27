package com.runtracker.Fragments.Groups;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.runtracker.Adapters.GroupsInviteAdapter;
import com.runtracker.Models.Group;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupInviteSelectionFragment extends DialogFragment {

    private RecyclerView groupList;
    private TextView heading;
    private Button cancelButton;

    public GroupInviteSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_invite_selection, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        groupList = v.findViewById(R.id.group_invite_recyclerview);
        heading = v.findViewById(R.id.invite_group_list_heading);
        cancelButton = v.findViewById(R.id.cancel_invite_button);

        cancelButton.setOnClickListener(view -> {
            dismiss();
        });

        String invitedUser = getArguments().getString("invitedUser");
        String title = "Invite " + invitedUser + " to:";
        heading.setText(title);
        callApiForGroups(invitedUser);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setFragmentSize();
    }

    private void setFragmentSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthPadding = 16 * (dpi/160);
        int heightPadding = 32 * (dpi/160);
        int width = (size.x - widthPadding);
        int height = size.y - heightPadding;

        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }

    private void callApiForGroups(String invitedUser) {
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
                    populateGroups(responseBody, invitedUser);
                } else {
                    getActivity().runOnUiThread(() -> {
                        try {
                            System.out.println(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(),
                                "Something went wrong when getting groups",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void populateGroups(String jsonBody, String invitedUser) {
        if (!jsonBody.equals("")) {
            Gson gson = new Gson();
            Group[] members = gson.fromJson(jsonBody, Group[].class);
            GroupsInviteAdapter adapter = new GroupsInviteAdapter(members, getActivity(), invitedUser, this);
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(RecyclerView.VERTICAL);
            getActivity().runOnUiThread(() -> {
                groupList.setLayoutManager(llm);
                groupList.setAdapter(adapter);
            });
        }
    }


}