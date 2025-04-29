package com.antozstudios.myapplication.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Diese Klasse führt eine HTTP GET-Anfrage an eine angegebene URL aus und verarbeitet die Antwort.
 * Die Antwort wird als String gespeichert und kann über das Feld {@link #message} abgerufen werden.
 */
public class GetRequestTask {


    SharedPreferences userData;
    /**
     * Enthält die Antwortnachricht der GET-Anfrage.
     */
    public String message;

    /**
     * Konstruktor der {@link GetRequestTask} Klasse.
     * Initialisiert das {@link #message}-Feld mit einem leeren String.
     */
    public GetRequestTask(Context context) {
        this.message = ""; // Initialisierung der message
        userData = context.getSharedPreferences("KEY",MODE_PRIVATE);
    }

    /**
     * Führt eine GET-Anfrage aus und speichert die Antwort im {@link #message}-Feld.
     *
     * @param link  Die Basis-URL der Anfrage.
     * @param table Der zusätzliche Pfadteil, der an die Basis-URL angehängt wird.
     * @throws IllegalArgumentException Wenn die URL ungültig ist oder die Anfrage fehlschlägt.
     */
    public void executeRequest(String link,String table) {
        URL url = null;
        try {
            url = new URL(link+table);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            message = "URL ist ungültig";
            return;
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("apikey",userData.getString("KEY",""));
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

    /**
     * Konvertiert einen InputStream in einen String.
     *
     * @param is Der InputStream, der in einen String umgewandelt werden soll.
     * @return Der konvertierte String.
     */
    String convertStreamToString(InputStream is) {
        StringBuilder stringBuilder = new StringBuilder();
        try (java.util.Scanner scanner = new java.util.Scanner(is)) {
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine()).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
