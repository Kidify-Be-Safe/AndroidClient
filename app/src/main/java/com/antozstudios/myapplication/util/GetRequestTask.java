package com.antozstudios.myapplication.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetRequestTask {

    public String message;

    public GetRequestTask() {
        this.message = ""; // Initialisierung der message
    }

    public void executeRequest(String table) {
        URL url = null;
        try {
            url = new URL("http://app.mluetzkendorf.xyz/api/"+table);
        } catch (MalformedURLException e) {
            e.printStackTrace(); // Fehlerprotokollierung
            message = "URL ist ungültig";
            return;
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            // Hier kannst du die Antwort weiter verarbeiten (z. B. als String lesen)
            message = convertStreamToString(in);  // Hilfsmethode zum Konvertieren des InputStreams in String
        } catch (IOException e) {
            e.printStackTrace(); // Fehlerprotokollierung
            message = "Fehler bei der Anfrage";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    // Hilfsmethode zur Umwandlung des InputStreams in einen String
    private String convertStreamToString(InputStream is) {
        StringBuilder stringBuilder = new StringBuilder();
        try (java.util.Scanner scanner = new java.util.Scanner(is)) {
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine()).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
