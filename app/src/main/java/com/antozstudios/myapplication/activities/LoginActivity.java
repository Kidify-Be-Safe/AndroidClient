package com.antozstudios.myapplication.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.JsonParser;
import com.google.android.material.textfield.TextInputEditText;


import java.io.IOException;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    Intent myIntent;

    TextView outPutText;
    GetRequestTask getRequestTask = new GetRequestTask();
    JsonParser jsonParser;

    TextInputEditText email;
    TextInputEditText passwort;
    Button anmeldenButton;

    Thread request;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Überprüfe, ob der Benutzer bereits angemeldet ist
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);  // Überprüfe den Anmeldestatus

        if (isLoggedIn) {
            // Wenn der Benutzer angemeldet ist, direkt in die MainActivity gehen
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(myIntent);
            finish();  // Beende die LoginActivity
        }

        outPutText = findViewById(R.id.textView2);
        email = findViewById(R.id.inputField_Email);
        passwort = findViewById(R.id.inputField_Passwort);
        anmeldenButton = findViewById(R.id.anmeldenButton);
        jsonParser = new JsonParser();

        request = new Thread(() -> {
            getRequestTask.executeRequest("benutzer");
        });

        request.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        runOnUiThread(() -> {
            try {
                request.join();
                String temp = getRequestTask.message;

                if (jsonParser != null) {
                    jsonParser.jsonResponse = temp;
                    jsonParser.parse();
                }

                anmeldenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int tempEmail = jsonParser.getIndex(email.getText().toString());

                        if (tempEmail != -1 && jsonParser.userList.get(tempEmail).passwort.equals(passwort.getText().toString())) {
                            outPutText.setText("Anmeldung war erfolgreich!");

                            // Speichern des Anmeldestatus in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("is_logged_in", true);  // Setzt den Status auf "angemeldet"
                            editor.putInt("userID",jsonParser.userList.get(tempEmail).id);
                            editor.putString("user_email", email.getText().toString());  // Speichert die E-Mail-Adresse des Benutzers
                            editor.apply();

                            // Starten von MainActivity und die LoginActivity aus dem Stack entfernen
                            myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(myIntent);
                            finish();
                        } else if (tempEmail != 1 && !jsonParser.userList.get(tempEmail).passwort.equals(passwort.getText().toString())) {
                            outPutText.setText("Passwort ist falsch!");
                        } else {
                            outPutText.setText("Das Konto existiert nicht!");
                        }
                    }
                });

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
