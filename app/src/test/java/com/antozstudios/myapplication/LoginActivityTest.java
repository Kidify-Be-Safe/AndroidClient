package com.antozstudios.myapplication;

import android.content.SharedPreferences;

import com.antozstudios.myapplication.data.User;
import com.antozstudios.myapplication.util.Hash;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LoginActivityTest {

    // Die LoginManager-Klasse enthält die Logik für den Login-Vorgang
    public static class LoginManager {

        // Die login-Methode überprüft die Eingabe (JSON, Email, Passwort) und speichert die Anmeldedaten
        public static String login(String jsonResponse, String email, String passwort, SharedPreferences.Editor editor) {
            // Parst die JSON-Antwort in ein User-Array
            User[] users = new Gson().fromJson(jsonResponse, User[].class);

            // Wenn kein oder mehr als ein Nutzer gefunden wird, gibt es einen Fehler
            if (users.length != 1) return "Nutzer nicht gefunden";

            User user = users[0]; // Holt sich den ersten Nutzer aus dem Array
            String hashPass = Hash.sha256(passwort); // Hash des eingegebenen Passworts

            // Überprüft, ob das Passwort korrekt ist
            if (!hashPass.equals(user.passwort_hash)) return "Passwort ist falsch";

            // Überprüft, ob das Konto verifiziert wurde
            if (!user.istverifiziert) return "Konto nicht verifiziert";

            // Speichert die Login-Daten im SharedPreferences-Editor
            editor.putInt("b_id", user.id);
            editor.putString("lastEmail", user.email);
            editor.putString("lastPasswort", user.passwort_hash);
            editor.putInt("isLoggedIn", 1); // Markiert, dass der Nutzer eingeloggt ist
            editor.putString("b_id_hash", user.b_id_hash);
            editor.apply(); // Wendet die Änderungen an

            return "OK"; // Erfolgreiches Login
        }
    }

    SharedPreferences.Editor editor; // Mock für den SharedPreferences-Editor

    @Before
    public void setup() {
        // Setzt den Editor als Mock-Objekt
        editor = mock(SharedPreferences.Editor.class);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor); // Ermöglicht das Mocken der putInt-Methode
        when(editor.putString(anyString(), anyString())).thenReturn(editor); // Ermöglicht das Mocken der putString-Methode
    }

    // Testet den Fall, dass der Nutzer nicht gefunden wurde
    @Test
    public void test_userNotFound() {
        String result = LoginManager.login("[]", "x@test.com", "1234", editor);
        assertEquals("Nutzer nicht gefunden", result); // Erwartet die Rückgabe: "Nutzer nicht gefunden"
    }

    // Testet den Fall, dass das Passwort falsch ist
    @Test
    public void test_wrongPassword() {
        User user = new User();
        user.email = "x@test.com";
        user.passwort_hash = Hash.sha256("geheim"); // Der gespeicherte Hash des Passworts
        user.istverifiziert = true;

        String json = new Gson().toJson(new User[]{user}); // Erzeugt ein JSON für den Test
        String result = LoginManager.login(json, "x@test.com", "falsch", editor); // Versucht sich mit dem falschen Passwort einzuloggen
        assertEquals("Passwort ist falsch", result); // Erwartet: "Passwort ist falsch"
    }

    // Testet den Fall, dass der Nutzer noch nicht verifiziert ist
    @Test
    public void test_notVerified() {
        User user = new User();
        user.email = "x@test.com";
        user.passwort_hash = Hash.sha256("1234");
        user.istverifiziert = false; // Der Nutzer ist nicht verifiziert

        String json = new Gson().toJson(new User[]{user});
        String result = LoginManager.login(json, "x@test.com", "1234", editor); // Versucht sich mit einem verifizierten Passwort einzuloggen
        assertEquals("Konto nicht verifiziert", result); // Erwartet: "Konto nicht verifiziert"
    }

    // Testet einen erfolgreichen Login
    @Test
    public void test_successfulLogin() {
        User user = new User();
        user.id = 1;
        user.email = "x@test.com";
        user.passwort_hash = Hash.sha256("1234");
        user.istverifiziert = true;
        user.b_id_hash = "abc"; // Der b_id_hash für den Nutzer

        String json = new Gson().toJson(new User[]{user});
        String result = LoginManager.login(json, "x@test.com", "1234", editor); // Erfolgreicher Login-Versuch
        assertEquals("OK", result); // Erwartet: "OK"

        // Überprüft, ob die korrekten Werte im Editor gespeichert wurden
        verify(editor).putInt("b_id", 1); // Überprüft, ob die b_id korrekt gesetzt wurde
        verify(editor).putString("lastEmail", "x@test.com"); // Überprüft, ob die E-Mail korrekt gesetzt wurde
        verify(editor).putString("b_id_hash", "abc"); // Überprüft, ob der b_id_hash korrekt gesetzt wurde
        verify(editor).apply(); // Überprüft, ob apply aufgerufen wurde, um die Änderungen anzuwenden
    }
}
