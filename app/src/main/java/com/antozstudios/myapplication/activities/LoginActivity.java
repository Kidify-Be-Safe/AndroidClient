package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.data.User;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.Hash;
import com.antozstudios.myapplication.util.JsonParser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;


public class LoginActivity extends AppCompatActivity {





    TextInputEditText email;
    TextInputEditText passwort;
    Button loginButton;
    Button signUpButton;
    GetRequestTask getRequestTask;


    SharedPreferences userData;
    SharedPreferences.Editor editor;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getRequestTask = new GetRequestTask();


        email = findViewById(R.id.inputField_Email);
        passwort = findViewById(R.id.inputField_Passwort);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        userData = getSharedPreferences("User_Data",MODE_PRIVATE);
        editor = userData.edit();
       int isLoggedIn = userData.getInt("isLoggedIn",0);

       if(isLoggedIn==1){
           startActivity(new Intent(LoginActivity.this,MainActivity.class));
finish();
       }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loginButton.setOnClickListener((view)->{

            if(Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                Thread thread = new Thread(()->{
                    getRequestTask.executeRequest("http://app.mluetzkendorf.xyz/api/","benutzer?email=eq."+email.getText().toString());
                    User[] users = new Gson().fromJson(getRequestTask.message,User[].class);

                    if(users.length>0){
                        String hashPasswort = Hash.sha256(passwort.getText().toString());
                        if(hashPasswort.equals(users[0].passwort)){


                            editor.putInt("b_id",users[0].id);
                            editor.putString("lastEmail",users[0].email);
                            editor.putString("lastPasswort",users[0].passwort);
                            editor.putInt("isLoggedIn",1);
                            editor.apply();


                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
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


}
