package com.runtracker.Fragments.RunTracking;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.runtracker.Adapters.RunRecyclerAdapter;
import com.runtracker.Models.Run;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.Constants;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRunsFragment extends Fragment {

    private RecyclerView myRunsRecycler;

    public MyRunsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_runs, container, false);

        myRunsRecycler = v.findViewById(R.id.my_runs_recycler);
        getMyRuns();
        return v;
    }

    private void getMyRuns() {
        String authToken = getActivity()
                .getSharedPreferences("preferences", Context.MODE_PRIVATE)
                .getString("authToken", "");

        JWT jwt = new JWT(authToken);
        String username = jwt.getClaim("username").asString();

        String url = Constants.BASE_URL + "run/" + username;
        ApiCalls api = new ApiCalls();

        api.protectedGet(url, "Bearer " + authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    populateList(responseString);
                }
            }
        });
    }

    private void populateList(String jsonRuns) {
        Gson gson = new Gson();
        Run[] runs = gson.fromJson(jsonRuns, Run[].class);
        RunRecyclerAdapter adapter = new RunRecyclerAdapter(runs, getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        getActivity().runOnUiThread(() -> {
            myRunsRecycler.setLayoutManager(llm);
            myRunsRecycler.setAdapter(adapter);
        });
    }

}
