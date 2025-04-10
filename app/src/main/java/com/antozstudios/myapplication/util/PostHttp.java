package com.antozstudios.myapplication.util;

import android.text.Editable;

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
            if(response.code()>200 && response.code()<300){
                return response.body().string();
            }
            return "error";

        }
    }







    public String sendCoordinates(double lan, double lat, int b_id, int ampelState, String strasse, Integer plz, String stadt, String land) {


        return "{"
                + "\"breitengrad\":" + lat + ","
                + "\"laengengrad\":" + lan + ","
                + "\"b_id\":" + b_id + ","
                + "\"stadt\":" + (stadt == null ? "null" : "\"" + stadt + "\"") + ","
                + "\"strasse\":\"" + strasse + "\","
                + "\"plz\":" + plz + ","
                + "\"land\":" + (land == null ? "null" : "\"" + land + "\"") + ","
                + "\"ampel\":" + ampelState
                + "}";
    }

    public String sendUser(String email, String vorname,String passwort,String nachname, String wohnort,String strasse, int plz,String HASH) {


        return "{"
                + "\"email\":\"" + email + "\","
                +"\"name\":\"" + vorname + "\","
                +"\"nachname\":\"" + nachname + "\","
                +"\"passwort\":\"" + passwort + "\","
                +"\"wohnort\":\"" + wohnort + "\","
                + "\"strasse\":\"" + strasse + "\","
                + "\"b_id_hash\":\"" + HASH + "\","
                + "\"plz\":" + plz
                + "}";
    }




}