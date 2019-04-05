package com.runtracker.Fragments.MenuFragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.runtracker.Adapters.GroupsRecyclerAdapter;
import com.runtracker.Fragments.Groups.MyGroupInvitesFragment;
import com.runtracker.Models.Group;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.AuthUtil;
import com.runtracker.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Controller for Groups fragment on the MAIN MENU
 */

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

        FloatingActionButton fab = v.findViewById(R.id.create_group_fab);
        ConstraintLayout groupInvitationsButton = v.findViewById(R.id.my_group_invitations_container);

        // On click for my group invitations button
        groupInvitationsButton.setOnClickListener(view -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            MyGroupInvitesFragment fragment = new MyGroupInvitesFragment();
            ft.replace(R.id.main_container, fragment, "MY_INVITES_FRAGMENT");
            ft.commit();
        });

        // OnClick for FAB to create a group
        fab.setOnClickListener(view -> {
            createGroupDialog();
        });

        callApiForGroups();
        return v;
    }

    // Api call to get group data
    private void callApiForGroups() {
        ApiCalls api = new ApiCalls();
        AuthUtil authUtil = new AuthUtil(getActivity());
        String authToken = authUtil.getAuthToken();
        String username = authUtil.getCurrentUser();

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

    // Populate groups recyclerview with data received from callApiForGroups method
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

    // Creates a dialog for creating a group (dialog with textView) when FAB is clicked
    private void createGroupDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Create a Group");
        alertDialog.setMessage("Enter a group name");

        EditText input = new EditText(getActivity());
        input.setSingleLine();
        alertDialog.setView(input, dpToPx(24), 0, dpToPx(24), 0);
        alertDialog.setIcon(R.drawable.ic_group_black_24dp);

        alertDialog.setPositiveButton("Create",
                (dialog, which) -> {
                    String groupName = input.getText().toString();
                    if (!groupName.isEmpty()) {
                        try {
                            createGroup(groupName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(),
                                    "Your group must have a name",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    // Api call to create group
    private void createGroup(String groupName) throws JSONException{
        ApiCalls api = new ApiCalls();

        GroupsFragment fragment = this;

        String authToken = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE)
                                        .getString("authToken", "");
        JWT jwt = new JWT(authToken);
        String username = jwt.getClaim("username").asString();

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("groupName", groupName);
        jsonBody.put("username", username);

        String url = Constants.BASE_URL + "group";
        api.protectedPost(jsonBody.toString(), url, "Bearer " + authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    if(!responseString.isEmpty()) {
                        getActivity().runOnUiThread(()-> {
                            Toast.makeText(getActivity(), "Group created", Toast.LENGTH_SHORT).show();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(fragment).attach(fragment).commit();
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Error code: " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }

    // Helper method to convert DP to PX for screen size calculation
    private int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
