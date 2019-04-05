package com.runtracker.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.runtracker.Fragments.UserFragment;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.Models.User;
import com.runtracker.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for User List RecyclerView in search page
 * Uses ViewHolder pattern using the custom_item_group_member layout
 */

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    private User[] users;
    private Context context;

    public UserRecyclerAdapter(User[] users, Context context) {
        this.users = users;
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
        User user = users[position];

        holder.username.setText(user.getUsername());
        Glide.with(context)
                .load(user.getProfilePicture())
                .placeholder(R.drawable.man)
                .into(holder.profilePicture);

        holder.container.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("username", users[position].getUsername());

            MainNavigatorActivity main = (MainNavigatorActivity) context;

            UserFragment userFragment = new UserFragment();
            userFragment.setArguments(bundle);

            FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("userFromGroup");
            ft.replace(R.id.main_container, userFragment);
            ft.commit();
        });
    }

    @Override
    public int getItemCount() {
        return users.length;
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
