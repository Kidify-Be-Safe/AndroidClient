package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.data.User;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.Hash;
import com.antozstudios.myapplication.util.PostHttp;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;



/**
 * Diese Aktivität ermöglicht es Benutzern, sich zu registrieren, indem sie ihre persönlichen Daten eingeben.
 * Es werden mehrere Validierungen durchgeführt, um sicherzustellen, dass die Eingaben korrekt sind.
 * Bei erfolgreicher Registrierung wird eine Bestätigung angezeigt.
 */
public class SignUpActivity extends AppCompatActivity {


    private TextInputEditText email, passwort, vorname, nachname, wohnort, strasse, plz;
    private Button submit;

    private User user;
    private GetRequestTask getRequestTask;
    private PostHttp postHttp;


    /**
     * Initialisiert die Ansicht und die erforderlichen Komponenten der Aktivität.
     * Wird beim Starten der Aktivität aufgerufen.
     *
     * @param savedInstanceState Gespeicherter Zustand der Aktivität, falls vorhanden.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
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
                String tempEmail = email.getText().toString().toLowerCase();
                String tempPasswort = passwort.getText().toString();
                String tempPlz = plz.getText().toString();
                String tempVorname = vorname.getText().toString().toLowerCase();
                String tempNachname = nachname.getText().toString().toLowerCase();
                String tempWohnort = wohnort.getText().toString().toLowerCase();
                String tempStrasse = strasse.getText().toString().toLowerCase().trim();

                boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(tempEmail).matches();

                getRequestTask.executeRequest(
                        "https://app.mluetzkendorf.xyz/api/benutzer",
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
                                new AlertDialog.Builder(SignUpActivity.this)
                                        .setTitle("Ungültiges Passwort")
                                        .setMessage("Das Passwort muss mindestens 8 Zeichen lang sein und folgende Anforderungen erfüllen:\n\n" +
                                                "- Mindestens ein Großbuchstabe\n" +
                                                "- Mindestens eine Zahl\n" +
                                                "- Mindestens ein Sonderzeichen (@$!%*?&)\n\n" +
                                                "Bitte versuchen Sie es erneut.")
                                        .setPositiveButton("OK", null)
                                        .show();                            });
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
                                                new AlertDialog.Builder(SignUpActivity.this)
                                                        .setTitle("Ungültige Straße")
                                                        .setMessage("Die eingegebene Straße ist ungültig.")
                                                        .setPositiveButton("OK", null)
                                                        .show();
                                            });
                                        } else {
                                            if(tempPlz.length()==5){
                                                //

                                                Thread signUp = new Thread(()->{

                                                    String tempHASH_ID = Hash.sha256(tempEmail+tempVorname+tempNachname);
                                                    String tempHASH_Passwort = Hash.sha256(tempPasswort);

                                                    String jsonResponse = postHttp.sendUser(tempEmail,tempVorname,tempHASH_Passwort,tempNachname,tempWohnort,tempStrasse,Integer.parseInt(tempPlz),tempHASH_ID);


                                                    try {
                                                        if(!postHttp.post("https://app.mluetzkendorf.xyz/api/benutzer",jsonResponse).equals("error")){
                                                            runOnUiThread(() -> {
                                                                new AlertDialog.Builder(SignUpActivity.this)
                                                                        .setMessage("Ihre Registrierung war erfolgreich. \n\n" +
                                                                                "Sie erhalten in Kürze einen Brief. \n\n" +
                                                                                "Bitte folgen Sie den Anweisungen und senden Sie den Brief innerhalb von 30 Tagen an uns zurück. Andernfalls wird Ihr Account automatisch gelöscht.")
                                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                finish();
                                                                            }
                                                                        })
                                                                        .show();

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
                }else{
                    runOnUiThread(()->{
                        Toast.makeText(SignUpActivity.this,"E-Mail ist ungültig.",Toast.LENGTH_LONG).show();
                    });
                }
            });
            thread.start();
        });
    }

    /**
     * Überprüft, ob das Passwort den Sicherheitsanforderungen entspricht.
     *
     * @param password Das Passwort, das überprüft werden soll.
     * @return true, wenn das Passwort gültig ist, andernfalls false.
     */
    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    /**
     * Überprüft, ob der eingegebene Text nur Buchstaben enthält.
     *
     * @param text Der zu überprüfende Text.
     * @return true, wenn der Text nur aus Buchstaben besteht, andernfalls false.
     */
    public boolean isValidText(String text) {
        return text.matches("[a-zA-Z ]+");
    }

    /**
     * Überprüft, ob der eingegebene Text nur Buchstaben enthält.
     *
     * @param street Der zu überprüfende Text.
     * @return true, wenn der Text nur aus Buchstaben besteht, andernfalls false.
     */
    public boolean isValidStreet(String street) {
        return street.matches("^[a-zA-Z]+\\.?\\s?\\d+$");
    }


}
