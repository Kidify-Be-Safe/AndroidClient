package com.antozstudios.myapplication.util;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostHttp {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    final OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String sendCoordinates(double lan, double lat, int b_id,int ampelState) {
        return "{"
                + "\"breitengrad\":" + lat + ","
                + "\"ampel\":" + ampelState + ","
                + "\"laengengrad\":" + lan + ","
                + "\"b_id\":" + b_id
                + "}";
    }




}