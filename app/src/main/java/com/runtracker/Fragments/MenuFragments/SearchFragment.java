package com.runtracker.Fragments.MenuFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runtracker.Adapters.GroupsRecyclerAdapter;
import com.runtracker.Adapters.UserRecyclerAdapter;
import com.runtracker.Models.Group;
import com.runtracker.Models.User;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private Button searchButton;
    private EditText searchField;
    private RecyclerView userList;
    private RecyclerView groupList;
    private LinearLayout searchResults;
    private ApiCalls api = new ApiCalls();


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        searchButton = v.findViewById(R.id.button_search);
        searchField = v.findViewById(R.id.search_search_text);
        userList = v.findViewById(R.id.search_user_list);
        groupList = v.findViewById(R.id.search_group_list);
        searchResults = v.findViewById(R.id.search_results_container);

        searchButton.setOnClickListener(view -> {

            String authToken = getActivity()
                    .getSharedPreferences("preferences", Context.MODE_PRIVATE)
                    .getString("authToken", "");

            if (!searchField.getText().toString().isEmpty()) {
                String query = searchField.getText().toString();
                searchForUsers(query);

                Thread thread = new Thread(() -> performSynchronousSearch(query, authToken));
                thread.start();

            } else {
                Toast.makeText(getActivity(), "Enter a username or group ID", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void searchForUsers(String query) {
        String userUrl = Constants.BASE_URL + "user/" + query.trim();

        api.get(userUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String userResponseString = response.body().string();
                    populateUserList(userResponseString);
                } else {
                    //Todo: Show no users
                }
            }
        });
    }

    private void performSynchronousSearch(String query, String authToken) {
        String groupByIdUrl = Constants.BASE_URL + "group/" + query.trim();
        String groupByUsernameUrl = Constants.BASE_URL + "group/user/" + query.trim();

        String groupsByIdResponseString = "";
        String groupsByUsernameResponseString = "";

        OkHttpClient client = new OkHttpClient();
        Request groupsByIdRequest = new Request.Builder()
                .url(groupByIdUrl)
                .header("Authorization", "Bearer " + authToken)
                .get()
                .build();

        try {
            Response groupByIdResponse = client.newCall(groupsByIdRequest).execute();

            if (groupByIdResponse.isSuccessful()) {
                groupsByIdResponseString = groupByIdResponse.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request groupsByUsernameRequest = new Request.Builder()
                .url(groupByUsernameUrl)
                .header("Authorization", "Bearer " + authToken)
                .get()
                .build();

        try {
            Response groupByUsernameResponse = client.newCall(groupsByUsernameRequest).execute();
            if (groupByUsernameResponse.isSuccessful()) {
                groupsByUsernameResponseString = groupByUsernameResponse.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        populateGroupList(groupsByIdResponseString, groupsByUsernameResponseString);
    }

    private void populateUserList(String json) {
        Gson gson = new Gson();
        User user = gson.fromJson(json, User.class);
        User[] userArray = new User[]{user};
        getActivity().runOnUiThread(() -> {
            UserRecyclerAdapter adapter = new UserRecyclerAdapter(userArray, getActivity());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(RecyclerView.VERTICAL);
            userList.setLayoutManager(llm);
            userList.setAdapter(adapter);
        });
    }

    private void populateGroupList(String groupsById, String groupsByUsername) {
        ArrayList<Group> groups = new ArrayList<>();
        Gson gson = new Gson();

        if (!groupsById.equals("")) {
            Group groupFromId = gson.fromJson(groupsById, Group.class);
            groups.add(groupFromId);
        }

        if (!groupsByUsername.equals("")) {
            Group[] groupsOfUsername = gson.fromJson(groupsByUsername, Group[].class);
            for (Group group : groupsOfUsername) {
                if (groups.size() > 0) {
                    if (group.getGroup_id() != groups.get(0).getGroup_id()) {
                        groups.add(group);
                    }
                } else {
                    groups.add(group);
                }
            }
        }

        if (groups.size() > 0) {
            Group[] foundGroups = groups.toArray(new Group[groups.size()]);
            GroupsRecyclerAdapter adapter = new GroupsRecyclerAdapter(foundGroups, getActivity());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(RecyclerView.VERTICAL);
            getActivity().runOnUiThread(() -> {
                groupList.setLayoutManager(llm);
                groupList.setAdapter(adapter);
            });
        }
    }

}
