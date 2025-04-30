
        package com.antozstudios.myapplication.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AppMode {

    private final String PATH="PATH_PLACEHOLDER";
    private final String KEY="KEY_PLACEHOLDER";



    public AppMode(Context context){
        //Basic URL https://app.mluetzkendorf.xyz/api/
        SharedPreferences sharedPreferences = context.getSharedPreferences("User_Data",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(PATH.contains("PATH_PLACEHOLDER")){
            editor.putString("URL","https://app.mluetzkendorf.xyz/api/");
            editor.putString("KEY","KrtKNkLNGcwKQ56la4jcHwxF");
            editor.apply();

        }else{
            editor.putString("URL",PATH);
            editor.putString("KEY",KEY);
            editor.apply();
        }
    }



}