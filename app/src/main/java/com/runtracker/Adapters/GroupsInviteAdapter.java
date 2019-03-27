package com.runtracker.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.Models.Group;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupsInviteAdapter extends RecyclerView.Adapter<GroupsInviteAdapter.ViewHolder> {

    private Group[] groups;
    private Context context;
    private String invitedUser;
    private DialogFragment dialog;

    public GroupsInviteAdapter(Group[] groups, Context context, String invitedUser, DialogFragment dialog) {
        this.groups = groups;
        this.context = context;
        this.invitedUser = invitedUser;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_group_member, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups[position];

        holder.username.setText(group.getGroupName());
        Glide.with(context)
                .load(R.drawable.man)
                .placeholder(R.drawable.man)
                .into(holder.profilePicture);

        holder.container.setOnClickListener(view -> {
            int groupId = groups[position].getGroup_id();
            inviteUser(groupId, invitedUser);
        });
    }

    @Override
    public int getItemCount() {
        return groups.length;
    }

    private void inviteUser(int groupId, String invitedUser) {
        ApiCalls api = new ApiCalls();

        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String authToken = prefs.getString("authToken", "");
        MainNavigatorActivity main = (MainNavigatorActivity) context;

        try {
            JSONObject body = new JSONObject().put("invitedUser", invitedUser);
            String url = Constants.BASE_URL + "group/" + groupId + "/invite";

            api.protectedPut(body.toString(), url, "Bearer " + authToken, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == 200) {
                        main.runOnUiThread(() -> {
                            Toast.makeText(main, "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    } else {
                        String responseBody = response.body().string();
                        try {
                            JSONObject json = new JSONObject(responseBody);
                            String message = json.getString("message");
                            main.runOnUiThread(() -> {
                                Toast.makeText(main,
                                        "Code: " + response.code() + "\nMessage: " + message,
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView profilePicture;
        ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.group_member_username);
            profilePicture = itemView.findViewById(R.id.group_member_picture);
            container = itemView.findViewById(R.id.layout_group_member);
        }
    }
}

