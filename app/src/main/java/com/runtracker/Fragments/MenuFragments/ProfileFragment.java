package com.runtracker.Fragments.MenuFragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.runtracker.Network.ApiCalls;
import com.runtracker.R;
import com.runtracker.Utilities.AuthUtil;
import com.runtracker.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Controller for profile fragment
 */
public class ProfileFragment extends Fragment {

    private TextView name;
    private CircleImageView profilePicture;
    private ImageView backgroundPicture;
    private TextView usernameTextView;
    private TextView currentWeight;
    private TextView weightGoal;
    private TextView averageDailySteps;
    private TextView dailyStepGoal;

    private ApiCalls api;
    private AuthUtil auth;
    private int REQUEST_STORAGE = 101;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize all variables and views
        api = new ApiCalls();
        auth = new AuthUtil(getActivity());
        name = v.findViewById(R.id.profile_name);
        profilePicture = v.findViewById(R.id.profile_circle_imageview);
        backgroundPicture = v.findViewById(R.id.profile_header_image);
        usernameTextView = v.findViewById(R.id.profile_username_value);
        currentWeight = v.findViewById(R.id.current_weight_value);
        weightGoal = v.findViewById(R.id.weight_goal_value);
        dailyStepGoal = v.findViewById(R.id.step_goal_value);
        averageDailySteps = v.findViewById(R.id.average_steps_value);

        getUserDetails(v);

        // Set onClick for profile picture image view to load file selector
        // Used for uploading a profile picture (doesn't work)
        profilePicture.setOnClickListener(view -> {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            } else {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });
        return v;
    }

    // Called when user has selected an image from their gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                    File file = new File(filePath);
                    uploadImage(auth.getCurrentUser(), file, auth.getAuthToken());
                } else {
                    Toast.makeText(getActivity(),"Image needs to be JPEG or PNG", Toast.LENGTH_LONG).show();
                }
            }
    }

    // Get's a real path from a uri (image selection)
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // Api call to get user details as JSON
    private void getUserDetails(View view) {
        AuthUtil authUtil = new AuthUtil(getActivity());
        String username = authUtil.getCurrentUser();
        String url = Constants.BASE_URL + "user/" + username;
        api.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    populateView(response, view);
                }
            }
        });
    }

    // Populates views with information received from getUserDetails
    private void populateView(Response response, View view) {
        getActivity().runOnUiThread(() -> {
            try {
                JSONObject jsonBody = new JSONObject(response.body().string());
                usernameTextView.setText(jsonBody.getString("username"));
                name.setText(jsonBody.getString("name"));
                currentWeight.setText(jsonBody.getString("currentWeight"));
                weightGoal.setText(jsonBody.getString("weightGoal"));
                dailyStepGoal.setText(jsonBody.getString("dailyStepGoal"));
                String picUrl = jsonBody.getString("profilePicture");
                if (!picUrl.equals("")) {
                    Glide.with(view.getContext())
                            .load(picUrl)
                            .placeholder(R.drawable.man)
                            .into(profilePicture);
                    profilePicture.setVisibility(View.VISIBLE);
                } else {
                    profilePicture.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Api call to upload image
    private void uploadImage(String username, File image, String authToken) {
        String url = Constants.BASE_URL + username + "/image-upload";
        api.postImage(url, image, "Bearer " + authToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    System.out.println(responseString);
                    getActivity().runOnUiThread(() -> Glide.with(getActivity())
                            .load(Uri.fromFile(image))
                            .placeholder(R.drawable.man)
                            .into(profilePicture));
                } else {
                    System.out.println(response.body().string());
                }
            }
        });
    }

}
