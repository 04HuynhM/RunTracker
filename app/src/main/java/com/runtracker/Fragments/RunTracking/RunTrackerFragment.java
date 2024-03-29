package com.runtracker.Fragments.RunTracking;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.runtracker.Models.Run;
import com.runtracker.Models.SingleLocation;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.AuthUtil;
import com.runtracker.Utilities.Constants;
import com.runtracker.Utilities.HelperMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Controller for Run tracker dialogFragment
 */
public class RunTrackerFragment extends DialogFragment implements OnMapReadyCallback {

    private Context context;

    private MapView mapView;
    private TextView timeValue;
    private GoogleMap googleMap;

    private int seconds;
    private boolean running;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private int REQUEST_LOCATION = 100;

    private ArrayList<SingleLocation> locations;

    public RunTrackerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                fusedLocationClient.removeLocationUpdates(locationCallback);
                dismiss();
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.colorPickerStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        View v = inflater.inflate(R.layout.fragment_run_tracker, container, false);

        timeValue = v.findViewById(R.id.run_time_value);
        Button completeButton = v.findViewById(R.id.complete_run);
        locations = new ArrayList<>();
        Run run = startRun();

        // OnClick for complete run button
        completeButton.setOnClickListener(view -> {
            running = false;
            stopLocationUpdates();
            mapView.onPause();
            run.setLocations(locations);
            run.setTimeInSeconds(seconds);
            uploadRun(run);
        });

        // Location callback implementation for Location service to work properly
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                SingleLocation newLocation = new SingleLocation(location.getLatitude(),
                                                                location.getLongitude());
                locations.add(newLocation);
            }
            }
        };
        running = true;
        runTimer();
        return v;
    }

    // Initiate mapview methods
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.run_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        startLocationUpdates();
    }

    // Check for permissions and initiate mapview to GoogleMap
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            googleMap.setMyLocationEnabled(true);
            setMapToCurrentLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        setFragmentSize();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // onDestroy overrided to stop location service when fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        mapView.onPause();
    }

    // Set size of dialog fragment based on screen size
    private void setFragmentSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthPadding = 16 * (dpi/160);
        int heightPadding = 32 * (dpi/160);
        int width = (size.x - widthPadding);
        int height = size.y - heightPadding;

        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }

    // Set map location (blue dot) to your current/last known location
    private void setMapToCurrentLocation() {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlng, 16);
                    googleMap.moveCamera(cu);
                }
            });
        }
    }

    // Start run and create run object that will eventually be submitted to API
    private Run startRun() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        JWT jwt = new JWT(sharedPreferences.getString("authToken", ""));
        String username = jwt.getClaim("username").asString();
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String timestamp = sfd.format(new Date());

        return new Run(username, timestamp);
    }

    // Start location service that tracks location every 5 seconds
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    // Stop location service
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Start run timer in new thread
    private void runTimer(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int sec = seconds % 60;
                String time = String.format("%d:%02d:%02d", hours, minutes, sec);
                timeValue.setText(time);
                if(running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });

    }

    // Api call to upload filled run object
    private void uploadRun(Run run) {
        JSONArray jsonLocationArray = new JSONArray();
        try {
            for (SingleLocation location : run.getLocations()) {
                JSONObject jsonLocation = new JSONObject();
                jsonLocation.put("latitude", location.getLatitude());
                jsonLocation.put("longitude", location.getLongitude());
                jsonLocation.put("timestamp", location.getTimestamp());
                jsonLocationArray.put(jsonLocation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonLocationArray);

        ApiCalls api = new ApiCalls();

        AuthUtil authUtil = new AuthUtil(getActivity());
        String rawToken = authUtil.getAuthToken();
        String username = authUtil.getCurrentUser();
        String bearerToken = "Bearer " + rawToken;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("locations", jsonLocationArray);
            jsonBody.put("startTime", run.getStartTime());
            jsonBody.put("timeInSeconds", run.getTimeInSeconds());

            String url = Constants.BASE_URL + "run";

            api.protectedPost(jsonBody.toString(), url, bearerToken, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(context, "Something went wrong with the upload", Toast.LENGTH_LONG).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response.code());
                    System.out.println(response.body().string());
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(context, "Run uploaded successfully", Toast.LENGTH_LONG).show();
                            dismiss();
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            try {
                                Toast.makeText(context, response.body().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
