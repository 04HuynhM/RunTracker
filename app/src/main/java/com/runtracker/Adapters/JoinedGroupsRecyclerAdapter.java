package com.runtracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.runtracker.Models.Group;
import com.runtracker.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JoinedGroupsRecyclerAdapter extends RecyclerView.Adapter<JoinedGroupsRecyclerAdapter.ViewHolder> {

    private Group[] groups;
    private Context context;

    public JoinedGroupsRecyclerAdapter(Group[] groups, Context context) {
        this.groups = groups;
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
        Group group = groups[position];

        holder.username.setText(group.getGroupName());
        Glide.with(context)
                .load(R.drawable.man)
                .placeholder(R.drawable.man)
                .into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return groups.length;
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
