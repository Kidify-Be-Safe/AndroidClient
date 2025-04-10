package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.data.User;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.Hash;
import com.antozstudios.myapplication.util.PostHttp;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    // Lokale Variablen für die Eingabefelder
    private TextInputEditText email, passwort, vorname, nachname, wohnort, strasse, plz;
    private Button submit;

    private User user;
    private GetRequestTask getRequestTask;
    private PostHttp postHttp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupactivity);
postHttp = new PostHttp();
        // Initialisieren der Felder
        vorname = findViewById(R.id.vornameInput);
        nachname = findViewById(R.id.nachnameInput);
        plz = findViewById(R.id.postleitzahlInput);
        submit = findViewById(R.id.submitButton);
        strasse = findViewById(R.id.strasseInput);
        email = findViewById(R.id.emailInput);
        passwort = findViewById(R.id.passwortInput);
        wohnort = findViewById(R.id.wohnortInput);

        // Initialisieren der Aufgaben
        getRequestTask = new GetRequestTask();
        user = new User();

        submit.setOnClickListener((view) -> {

            Thread thread = new Thread(() -> {
                // Hole Eingabewerte
                String tempEmail = email.getText().toString();
                String tempPasswort = passwort.getText().toString();
                String tempPlz = plz.getText().toString();
                String tempVorname = vorname.getText().toString();
                String tempNachname = nachname.getText().toString();
                String tempWohnort = wohnort.getText().toString();
                String tempStrasse = strasse.getText().toString();

                boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(tempEmail).matches();

                getRequestTask.executeRequest(
                        "http://app.mluetzkendorf.xyz/api/benutzer",
                        "?email=eq." + tempEmail
                );

                User[] users = new Gson().fromJson(getRequestTask.message, User[].class);

                if (isValidEmail) {

                    if (users.length > 0) {
                        runOnUiThread(() -> {
                            Toast.makeText(SignUpActivity.this, "E-Mail gibt es schon.", Toast.LENGTH_LONG).show();
                        });
                    } else {

                        if (!isValidPassword(tempPasswort)) {
                            runOnUiThread(() -> {
                                Toast.makeText(SignUpActivity.this, "Passwort ungültig.", Toast.LENGTH_LONG).show();
                            });
                        } else {

                            boolean isValidVorname = isValidText(tempVorname);
                            boolean isValidNachname = isValidText(tempNachname);
                            boolean isValidWohnort = isValidText(tempWohnort);
                            boolean isValidStrasse = isValidStreet(tempStrasse);

                            if (!isValidVorname) {
                                runOnUiThread(() -> {
                                    Toast.makeText(SignUpActivity.this, "Vorname ungültig.", Toast.LENGTH_LONG).show();
                                });
                            } else {
                                if (!isValidNachname) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(SignUpActivity.this, "Nachname ungültig.", Toast.LENGTH_LONG).show();
                                    });
                                } else {
                                    if (!isValidWohnort) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(SignUpActivity.this, "Wohnort ungültig.", Toast.LENGTH_LONG).show();
                                        });
                                    } else {
                                        if (!isValidStrasse) {
                                            runOnUiThread(() -> {
                                                Toast.makeText(SignUpActivity.this, "Straße ungültig. Beispiel: Beispielstraße 19 oder Beispielstraße. 19", Toast.LENGTH_LONG).show();
                                            });
                                        } else {
                                            if(tempPlz.length()==5){
                                                //

                                                Thread signUp = new Thread(()->{

                                                    String tempHASH_ID = Hash.sha256(tempEmail+tempVorname+tempNachname);
                                                    String tempHASH_Passwort = Hash.sha256(tempPasswort);

                                                    String jsonResponse = postHttp.sendUser(tempEmail,tempVorname,tempHASH_Passwort,tempNachname,tempWohnort,tempStrasse,Integer.parseInt(tempPlz),tempHASH_ID);


                                                    try {
                                                        if(!postHttp.post("http://app.mluetzkendorf.xyz/api/benutzer",jsonResponse).equals("error")){
                                                            runOnUiThread(() -> {
                                                                Toast.makeText(SignUpActivity.this, "Erfolgreich", Toast.LENGTH_LONG).show();
                                                                finish();
                                                            });
                                                        }
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                });
                                                signUp.start();




                                            }else{
                                                runOnUiThread(() -> {
                                                    Toast.makeText(SignUpActivity.this, "PLZ ist ungueltig", Toast.LENGTH_LONG).show();
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
            thread.start();
        });
    }

    // Methode zur Passwortvalidierung
    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    // Methode zur Textvalidierung (Vorname, Nachname)
    boolean isValidText(String text) {
        return text.matches("[a-zA-Z ]+");
    }

    // Methode zur Überprüfung der Straßeneingabe (Wort und Zahl, optional mit Punkt)
    boolean isValidStreet(String street) {
        return street.matches("^[a-zA-Z]+\\.?\\s?\\d+$");
    }


}
