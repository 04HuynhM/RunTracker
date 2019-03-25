package com.runtracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.runtracker.Models.User;
import com.runtracker.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> {

    private User[] members;
    private Context context;

    public GroupMemberListAdapter(User[] members, Context context) {
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_group_member, parent, false);
        context = parent.getContext();
        v.setOnClickListener(view -> {

        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = members[position];

        holder.username.setText(user.getUsername());
        Glide.with(context)
                .load(user.getProfilePicture())
                .placeholder(R.drawable.man)
                .into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return members.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView profilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.group_member_username);
            profilePicture = itemView.findViewById(R.id.group_member_picture);
        }
    }
}
