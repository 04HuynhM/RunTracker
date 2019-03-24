package com.runtracker.MenuFragments;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.runtracker.MainNavigatorActivity;
import com.runtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackRunMenuFragment extends Fragment {

    private Button startRunButton;

    public TrackRunMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_track_run_menu, container, false);

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
