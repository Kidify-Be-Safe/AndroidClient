package com.antozstudios.myapplication.activities;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.data.User;
import com.antozstudios.myapplication.util.AppMode;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.Hash;
import com.antozstudios.myapplication.util.JsonParser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

/**
 * Aktivität für den Login-Bildschirm, in der Benutzer sich mit ihrer E-Mail und ihrem Passwort anmelden können.
 * <p>
 * Diese Aktivität ermöglicht es dem Benutzer, sich anzumelden, indem er seine E-Mail-Adresse und sein Passwort eingibt.
 * Wenn der Benutzer bereits eingeloggt ist, wird er direkt zur Hauptaktivität weitergeleitet.
 * </p>
 */
public class LoginActivity extends AppCompatActivity {





    TextInputEditText email;
    TextInputEditText passwort;
    Button loginButton;
    Button signUpButton;
    GetRequestTask getRequestTask;


    SharedPreferences userData;
    SharedPreferences.Editor editor;


    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die Benutzeroberfläche, stellt sicher, dass der Benutzer nicht erneut die Login-Seite sieht,
     * wenn er bereits eingeloggt ist, und richtet die Schaltflächen für die Anmeldung und Registrierung ein.
     */


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        setContentView(R.layout.activity_login);
        userData = getSharedPreferences("User_Data",MODE_PRIVATE);

        new AppMode(getApplicationContext());


        getRequestTask = new GetRequestTask(getApplicationContext());


        Button supportButton = findViewById(R.id.supportButton);
        supportButton.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.mluetzkendorf.xyz/support")));
        });

        Button datenschutzButton = findViewById(R.id.datenschutzButton);
        datenschutzButton.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.mluetzkendorf.xyz/datenschutz")));
        });

        Button nutzungsbedingungButton = findViewById(R.id.nutzungsbedingungButton);
        nutzungsbedingungButton.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.mluetzkendorf.xyz/nutzungsbedingungen")));
        });


        email = findViewById(R.id.inputField_Email);
        passwort = findViewById(R.id.inputField_Passwort);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);






        editor = userData.edit();
       int isLoggedIn = userData.getInt("isLoggedIn",0);

       if(isLoggedIn==1) {
           startActivity(new Intent(LoginActivity.this, MainActivity.class));
           finish();
       }
        /**
         * Behandelt den Klick auf die Login-Schaltfläche.
         * Überprüft, ob die eingegebene E-Mail-Adresse gültig ist, und vergleicht das eingegebene Passwort mit dem gespeicherten Hash.
         * Wenn die Anmeldedaten korrekt sind und der Benutzer verifiziert wurde, wird der Benutzer eingeloggt und zur Hauptaktivität weitergeleitet.
         * Andernfalls wird eine Fehlermeldung angezeigt.
         */

           loginButton.setOnClickListener((view)->{

               if(Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                   Thread thread = new Thread(()->{
                       getRequestTask.executeRequest(userData.getString("URL",""),"benutzer?email=eq."+email.getText());
                       User[] users = new Gson().fromJson(getRequestTask.message,User[].class);

                       if(users.length==1){
                           String hashPasswort = Hash.sha256(passwort.getText().toString());
                           if(hashPasswort.equals(users[0].passwort_hash)){

                               if(users[0].istverifiziert){
                                   editor.putInt("b_id",users[0].id);
                                   editor.putString("lastEmail",users[0].email);
                                   editor.putString("lastPasswort",users[0].passwort_hash);
                                   editor.putInt("isLoggedIn",1);
                                   editor.putString("b_id_hash",users[0].b_id_hash);
                                   editor.apply();


                                   startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                   finish();
                               }else{
                                   runOnUiThread(()->{
                                       Toast.makeText(this,"Konto nicht verifiziert!",Toast.LENGTH_LONG).show();
                                   });
                               }


                           }else{
                               runOnUiThread(()->{
                                   Toast.makeText(this,"Passwort ist falsch!",Toast.LENGTH_LONG).show();
                               });
                           }
                       }else{
                           runOnUiThread(()->{
                               Toast.makeText(this,"Nutzer nicht gefunden!",Toast.LENGTH_LONG).show();
                           });
                       }



                   });
                   thread.start();
               }





           });



           signUpButton.setOnClickListener((view)->{

               startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
           });



    }








    /**
     * Wird aufgerufen, wenn die Aktivität fortgesetzt wird.
     * Diese Methode überschreibt die onResume-Methode der Elternklasse, jedoch wird sie in dieser Klasse nicht weiter angepasst.
     */
    @Override
    protected void onResume() {
        super.onResume();

    }


}
