package com.runtracker.Fragments.RunTracking;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.runtracker.Models.SingleLocation;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.AuthUtil;
import com.runtracker.Utilities.Constants;
import com.runtracker.Utilities.HelperMethods;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedRunFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private TextView timeValue;
    private TextView distance;
    private TextView runId;
    private GoogleMap map;

    private ArrayList<SingleLocation> locations;
    private ArrayList<LatLng> latLngs;

    public CompletedRunFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_completed_run, container, false);

        timeValue = v.findViewById(R.id.completed_run_time_value);
        distance = v.findViewById(R.id.completed_run_distance_value);
        runId = v.findViewById(R.id.completed_run_id);

        Button deleteRunButton = v.findViewById(R.id.delete_run_button);

        deleteRunButton.setOnClickListener(view -> {
            HelperMethods helperMethods = new HelperMethods();
            helperMethods
                    .createYesNoAlert(
                            "Are you sure you want to delete this run?",
                            getActivity(),
                            (dialogInterface, choice) -> {
                                switch (choice) {
                                    case DialogInterface.BUTTON_POSITIVE :
                                        deleteRun(runId.getText().toString());
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialogInterface.cancel();
                                        break;
                                }
            });
        });

        Bundle bundle = getArguments();
        int runIdText = bundle.getInt("run_id");
        int timeInSeconds = bundle.getInt("timeInSeconds");

        locations = (ArrayList<SingleLocation>) bundle.getSerializable("locations");
        latLngs = new ArrayList<>();

        timeValue.setText(getTime(timeInSeconds));
        runId.setText(String.valueOf(runIdText));

        return v;
    }

    private void deleteRun(String runId) {
        ApiCalls api = new ApiCalls();
        String url = Constants.BASE_URL + "run/" + runId;
        AuthUtil authUtil = new AuthUtil(getActivity());
        String authToken = authUtil.getAuthToken();
        api.delete(url, "Bearer " + authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        if (fm.popBackStackImmediate()) {
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.main_container, new MyRunsFragment());
                            ft.addToBackStack("MyRunsFragmentFromDeletedRun");
                            ft.commit();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(),
                                "Something went wrong when deleting this run",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private String getTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int sec = seconds % 60;
        return String.format("%d:%02d:%02d", hours, minutes, sec);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.completed_run_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    private float getDistance(ArrayList<LatLng> latLngs) {
        float totalDistance = 0;

        for(int i = 1; i < latLngs.size(); i++) {
            Location currLocation = new Location("this");
            currLocation.setLatitude(latLngs.get(i).latitude);
            currLocation.setLongitude(latLngs.get(i).longitude);

            Location lastLocation = new Location("this");
            currLocation.setLatitude(latLngs.get(i-1).latitude);
            currLocation.setLongitude(latLngs.get(i-1).longitude);

            totalDistance += lastLocation.distanceTo(lastLocation);
        }

        return totalDistance;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        for (SingleLocation location : locations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            latLngs.add(latLng);
            System.out.println(latLng.toString());
        }
        PolylineOptions options = new PolylineOptions()
                                        .clickable(false)
                                        .addAll(latLngs)
                                        .width(12)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .startCap(new RoundCap())
                                        .endCap(new RoundCap());

        double distanceDouble = (double) getDistance(latLngs);
        String distanceString = String.format("%.2f" , distanceDouble * 0.001);

        distance.setText(String.valueOf(distanceString));
        googleMap.addPolyline(options);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 17));
    }
}
