package com.runtracker.MenuFragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunTrackerFragment extends Fragment {


    public RunTrackerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run_tracker, container, false);
    }

}