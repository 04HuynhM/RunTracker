package com.runtracker.Fragments.Groups;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.android.jwt.JWT;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.runtracker.Adapters.UserRecyclerAdapter;
import com.runtracker.Fragments.MenuFragments.ProfileFragment;
import com.runtracker.Fragments.UserFragment;
import com.runtracker.Models.User;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.AuthUtil;
import com.runtracker.Utilities.Constants;

import java.io.IOException;
import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SingleGroupFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView groupName;
    private TextView admin;
    private ImageView adminProfilePicture;

    public SingleGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_single_group, container, false);

        recyclerView = v.findViewById(R.id.single_group_members_recyclerview);
        groupName = v.findViewById(R.id.single_group_name);
        admin = v.findViewById(R.id.group_admin_value);
        adminProfilePicture = v.findViewById(R.id.admin_profile_picture);

        Bundle bundle = this.getArguments();
        groupName.setText(bundle.getString("groupName"));
        admin.setText(bundle.getString("admin"));

        ConstraintLayout adminButton = v.findViewById(R.id.admin_row_button);
        adminButton.setOnClickListener(view -> {
            AuthUtil authUtil = new AuthUtil(getActivity());
            String username = authUtil.getCurrentUser();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            if (admin.getText().toString().equals(username)) {
                ProfileFragment fragment = new ProfileFragment();
                ft.addToBackStack("profileFragment");
                ft.replace(R.id.main_container, fragment);
                ft.commit();
            } else {
                Bundle userBundle = new Bundle();
                userBundle.putString("username", admin.getText().toString());
                UserFragment fragment = new UserFragment();
                fragment.setArguments(userBundle);
                ft.addToBackStack("userFragment");
                ft.replace(R.id.main_container, fragment);
                ft.commit();
            }
        });

        callApiForMembers();
        return v;
    }

    private void callApiForMembers() {
        SharedPreferences prefs = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String authToken = prefs.getString("authToken", "");

        Bundle bundle = this.getArguments();
        int groupId = bundle.getInt("group_id");

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
        User[] membersWithAdmin = gson.fromJson(body, User[].class);
        ArrayList<User> membersNoAdmin = new ArrayList<>();
        String admin = getArguments().getString("admin");
        getActivity().runOnUiThread(() -> {
            for (User member : membersWithAdmin) {
                if (member.getUsername().equals(admin)) {
                    Glide.with(getActivity())
                            .load(member.getProfilePicture())
                            .placeholder(R.drawable.man)
                            .into(adminProfilePicture);
                } else {
                    membersNoAdmin.add(member);
                }
            }
            User[] finalMembersArray = membersNoAdmin.toArray(new User[membersNoAdmin.size()]);
            UserRecyclerAdapter adapter = new UserRecyclerAdapter(finalMembersArray, getActivity());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(RecyclerView.VERTICAL);

            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(adapter);
        });
    }

}
