package com.runtracker.Fragments.MenuFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.runtracker.Fragments.RunTracking.MyRunsFragment;
import com.runtracker.Fragments.RunTracking.RunTrackerFragment;
import com.runtracker.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunsMenuFragment extends Fragment {

    private Button startRunButton;
    private ConstraintLayout myRuns;

    public RunsMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track_run_menu, container, false);

        myRuns = v.findViewById(R.id.my_runs_container);
        myRuns.setOnClickListener(view -> {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("myRuns");
            ft.replace(R.id.main_container, new MyRunsFragment());
            ft.commit();
        });

        startRunButton = v.findViewById(R.id.start_run_button);

        startRunButton.setOnClickListener(view -> {
            showRunTracker();
        });

        return v;
    }

    private void showRunTracker() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("run_tracker");

        DialogFragment dialogFragment = new RunTrackerFragment();
        dialogFragment.show(ft, "run_tracker");
    }

}
