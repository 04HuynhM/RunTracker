package com.runtracker.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runtracker.Fragments.RunTracking.CompletedRunFragment;
import com.runtracker.MainNavigatorActivity;
import com.runtracker.Models.Run;
import com.runtracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for My Runs List RecyclerView
 * Uses ViewHolder pattern using the custom_item_run_row layout
 */

public class RunRecyclerAdapter extends RecyclerView.Adapter<RunRecyclerAdapter.ViewHolder> {

    private Run[] runs;
    private Context context;

    public RunRecyclerAdapter(Run[] runs, Context context) {
        this.runs = runs;
        this.context = context;
    }

    @NonNull
    @Override
    public RunRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_run_row, parent, false);
        return new RunRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RunRecyclerAdapter.ViewHolder holder, int position) {
        Run run = runs[position];
        holder.runDate.setText(getRunTime(run.getStartTime()));
        holder.runTime.setText(getRunDuration(run.getTimeInSeconds()));
        holder.runNumber.setText(String.valueOf(position+1));
        holder.container.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("run_id", runs[position].getRun_id());
            bundle.putString("user", runs[position].getUser());
            bundle.putString("startTime", runs[position].getStartTime());
            bundle.putSerializable("locations", runs[position].getLocations());
            bundle.putInt("timeInSeconds", runs[position].getTimeInSeconds());

            MainNavigatorActivity main = (MainNavigatorActivity) context;

            CompletedRunFragment completedRunFragment = new CompletedRunFragment();
            completedRunFragment.setArguments(bundle);

            FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("userFromGroup");
            ft.replace(R.id.main_container, completedRunFragment);
            ft.commit();
        });
    }

    private String getRunDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int sec = seconds % 60;
        return String.format("%d:%02d:%02d", hours, minutes, sec);
    }
    private String getRunTime(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/YYYY    HH:mm:ss");
            Date d = sdf.parse(timestamp);
            return output.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return runs.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView runDate;
        TextView runTime;
        TextView runNumber;
        ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            runDate = itemView.findViewById(R.id.run_row_date);
            runNumber = itemView.findViewById(R.id.run_row_number);
            runTime = itemView.findViewById(R.id.run_row_runtime_value);
            container = itemView.findViewById(R.id.run_row_container);
        }
    }
}
