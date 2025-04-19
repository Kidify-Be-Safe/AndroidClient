package com.antozstudios.myapplication.util;

import android.text.Editable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Dienstklasse für HTTP-Requests mit JSON-Body.
 * Unterstützt POST, PUT und DELETE sowie Hilfsmethoden zur JSON-Erstellung.
 */
public class PostHttp {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public OkHttpClient client = new OkHttpClient();


    /**
     * Sendet einen POST-Request an die angegebene URL mit JSON-Inhalt.
     * @param url Ziel-URL
     * @param json JSON-String
     * @return Serverantwort als String oder "error"
     * @throws IOException bei Netzwerkfehlern
     */
    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if(response.code() >= 200 && response.code() < 300){
                return response.body().string();
            }
            return "error";

        }
    }
    /**
     * Sendet einen PUT-Request (hier technisch als POST umgesetzt).
     * @param url Ziel-URL
     * @param json JSON-String
     * @return Serverantwort oder "error"
     * @throws IOException bei Netzwerkfehlern
     */
    public String put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if(response.code() >= 200 && response.code() < 300){
                return response.body().string();
            }
            return "error";


        }
    }

    /**
     * Sendet einen DELETE-Request mit JSON-Body.
     * @param url Ziel-URL
     * @param json JSON-String
     * @return Serverantwort oder "error"
     * @throws IOException bei Netzwerkfehlern
     */
    public String delete(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);

        // Request mit DELETE-Methode und dem Body
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .header("Content-Type", "application/json")  // Content-Type hinzufügen
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Überprüfen, ob der Statuscode im erfolgreichen Bereich liegt
            if (response.code() >= 200 && response.code() < 300) {
                return response.body().string();  // Erfolgreiche Antwort zurückgeben
            } else {
                // Fehlerbehandlung, falls der Statuscode nicht im 2xx-Bereich ist
                System.out.println("Request failed with code: " + response.code());
                System.out.println("Response: " + response.body().string());
                return "error";
            }
        }
    }







    /**
     * Erzeugt einen JSON-String mit Koordinaten und Standortdaten.
     * @param lan Längengrad
     * @param lat Breitengrad
     * @param b_id Benutzer-ID
     * @param ampelState Ampel-Zustand
     * @param strasse Straßenname
     * @param plz Postleitzahl
     * @param stadt Stadtname
     * @param land Ländername
     * @return JSON-String
     */

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
    /**
     * Erzeugt einen JSON-String mit Benutzerdaten.
     * @param email E-Mail-Adresse
     * @param vorname Vorname
     * @param passwort Passwort (Hash)
     * @param nachname Nachname
     * @param wohnort Wohnort
     * @param strasse Straße
     * @param plz Postleitzahl
     * @param HASH Benutzer-ID Hash
     * @return JSON-String
     */
    public String sendUser(String email, String vorname,String passwort,String nachname, String wohnort,String strasse, int plz,String HASH) {


        return "{"
                + "\"email\":\"" + email + "\","
                +"\"vorname\":\"" + vorname + "\","
                +"\"nachname\":\"" + nachname + "\","
                +"\"passwort_hash\":\"" + passwort + "\","
                +"\"wohnort\":\"" + wohnort + "\","
                + "\"strasse\":\"" + strasse + "\","
                + "\"b_id_hash\":\"" + HASH + "\","
                + "\"plz\":" + plz
                + "}";
    }
    /**
     * Erzeugt einen JSON-String für eine Freundschaftsanfrage.
     * @param b_id Benutzer-ID
     * @param f_id Freund-ID
     * @return JSON-String
     */
    public String sendFriend(int b_id, int f_id) {
        return "{"
                + "\"b_id\":" + b_id + ","
                + "\"f_id\":" + f_id + "}";
    }

    /**
     * Erzeugt einen JSON-String zum Löschen eines Benutzers.
     * @param id Benutzer-ID
     * @return JSON-String
     */
    public String deleteUser(int id){
        return "{" + "\"id\":" + id + "}";

    }






}