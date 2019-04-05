package com.runtracker.Network;

import java.io.File;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
Utility class to simplify API calls
 */

public class ApiCalls {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType PNG = MediaType.get("image/png");
    private static final MediaType JPEG = MediaType.get("image/jpeg");

    OkHttpClient client = new OkHttpClient();

    public Call post(String jsonBody, String url, Callback callback) {
        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call protectedGet(String url, String authToken, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authToken)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call protectedPost(String jsonBody, String url, String authToken, Callback callback) {
        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authToken)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call protectedPut(String jsonBody, String url, String authToken, Callback callback) {
        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authToken)
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call delete(String url, String authToken, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", authToken)
                .delete()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call postImage(String url,
                   File image,
                   String authToken,
                   Callback callback) {
        RequestBody requestBody;
        if (image.getName().contains(".png")) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", image.getName(),
                            RequestBody.create(PNG, image))
                    .build();
        } else if (image.getName().contains(".jpg") || image.getName().contains(".jpeg")) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", image.getName(),
                            RequestBody.create(JPEG, image))
                    .build();
        } else {
            return null;
        }
        Request request = new Request.Builder()
                .header("Authorization", authToken)
                .url(url)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
