package com.antozstudios.myapplication.util;

import com.antozstudios.myapplication.data.User;
import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Diese Klasse stellt Methoden zum Parsen einer JSON-Antwort und zur Verarbeitung von Benutzerobjekten bereit.
 * Sie verwendet die Gson-Bibliothek, um eine JSON-Antwort in eine Liste von {@link User}-Objekten zu konvertieren.
 */
public class JsonParser {

    /**
     * Die JSON-Antwort, die geparst werden soll.
     */
    public String jsonResponse;

    /**
     * Die Liste von {@link User}-Objekten, die nach dem Parsen der JSON-Antwort erstellt wird.
     */
    public List<User> userList;

    /**
     * Parsen der {@link #jsonResponse} in eine Liste von {@link User}-Objekten.
     * Diese Methode muss aufgerufen werden, nachdem {@link #jsonResponse} gesetzt wurde.
     */
    public void parse() {
        Gson gson = new Gson();
        Type userListType = new TypeToken<List<User>>() {}.getType();
        userList = gson.fromJson(jsonResponse, userListType);
    }

    /**
     * Sucht nach dem Index eines Benutzers anhand der E-Mail-Adresse.
     *
     * @param email Die E-Mail-Adresse des gesuchten Benutzers.
     * @return Der Index des Benutzers in der {@link #userList}, oder -1, wenn der Benutzer nicht gefunden wurde.
     */
    public int getIndex(String email) {
        for (int i = 0; i < userList.size(); i++) {
            if (email.equals(userList.get(i).email)) {
                return i;
            }
        }
        return -1;
    }
}
