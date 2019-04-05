package com.runtracker.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runtracker.Fragments.Groups.MyGroupInvitesFragment;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.Models.Group;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Adapter for My Group Invites List RecyclerView
 * Uses ViewHolder pattern using the custom_item_group_invite layout
 */

public class MyInvitesListAdapter extends RecyclerView.Adapter<MyInvitesListAdapter.ViewHolder> {

    private Group[] groups;
    private String username;
    private Context context;
    private String auth;

    public MyInvitesListAdapter(Group[] groups, Context context, String username, String auth) {
        this.groups = groups;
        this.context = context;
        this.username = username;
        this.auth = auth;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_group_invite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups[position];
        holder.groupName.setText(group.getGroupName());
        holder.membersCount.setText(String.valueOf(group.getMembers().length));
        holder.acceptInvite.setOnClickListener(view -> {
            try {
                respondToInvite("accept", group.getGroup_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        holder.declineInvite.setOnClickListener(view -> {
            try {
                respondToInvite("decline", group.getGroup_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.length;
    }

    private void respondToInvite(String acceptOrDecline, int groupId) throws JSONException {
        ApiCalls api = new ApiCalls();
        String url = Constants.BASE_URL + "group/" + groupId + "/invite/" + acceptOrDecline;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        api.protectedPut(jsonObject.toString(), url, "Bearer " + auth, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    MainNavigatorActivity main = (MainNavigatorActivity) context;
                    main.runOnUiThread(() -> {
                        Toast.makeText(main, acceptOrDecline +"ed invitation", Toast.LENGTH_SHORT).show();
                        MyGroupInvitesFragment fragment = (MyGroupInvitesFragment) main.getSupportFragmentManager().findFragmentByTag("MY_INVITES_FRAGMENT");
                        FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
                        if (Build.VERSION.SDK_INT >= 26) {
                            ft.setReorderingAllowed(false);
                        }
                        ft.detach(fragment).attach(fragment).commit();
                    });
                } else {
                    try {
                        MainNavigatorActivity main = (MainNavigatorActivity) context;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String message = jsonObject.getString("message");
                        main.runOnUiThread(() -> {
                            Toast.makeText(main, message, Toast.LENGTH_SHORT).show();
                            MyGroupInvitesFragment fragment = (MyGroupInvitesFragment) main.getSupportFragmentManager().findFragmentByTag("MY_INVITES_FRAGMENT");
                            FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(fragment).attach(fragment).commit();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;
        TextView membersCount;
        ImageView acceptInvite;
        ImageView declineInvite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_invite_name);
            membersCount = itemView.findViewById(R.id.group_invite_member_count_value);
            acceptInvite = itemView.findViewById(R.id.group_accept_invite_button);
            declineInvite = itemView.findViewById(R.id.group_decline_invite);
        }
    }
}

