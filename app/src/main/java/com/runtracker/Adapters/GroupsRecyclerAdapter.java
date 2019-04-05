package com.runtracker.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.runtracker.Fragments.Groups.SingleGroupFragment;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.Models.Group;
import com.runtracker.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for Groups list RecyclerView
 * Uses ViewHolder pattern using the custom_item_group_member layout
 */

public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsRecyclerAdapter.ViewHolder> {

    private Group[] groups;
    private Context context;

    public GroupsRecyclerAdapter(Group[] groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_group_member, parent, false);
        context = parent.getContext();
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
            Bundle bundle = new Bundle();
            bundle.putInt("group_id", groups[position].getGroup_id());
            bundle.putString("groupName", groups[position].getGroupName());
            bundle.putString("admin", groups[position].getAdmin());
            bundle.putStringArray("members", groups[position].getMembers());

            MainNavigatorActivity main = (MainNavigatorActivity) context;

            SingleGroupFragment singleGroup = new SingleGroupFragment();
            singleGroup.setArguments(bundle);

            FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_container, singleGroup);
            ft.addToBackStack("Single group view");
            ft.commit();
        });
    }

    @Override
    public int getItemCount() {
        return groups.length;
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
