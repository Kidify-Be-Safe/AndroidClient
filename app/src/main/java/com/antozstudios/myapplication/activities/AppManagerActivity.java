package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.assist.AssistStructure;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;

import com.antozstudios.myapplication.util.FileHelper;
import com.antozstudios.myapplication.util.GetApps;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;



public class AppManagerActivity extends AppCompatActivity {

    GetApps appInfo;
    Thread thread;

    Spinner spinner;

    private LinearLayout showAppsLin;



    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch showNotification;






    StringBuilder stringBuilder;
    private Switch readNotification;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appInfo = GetApps.getInstance(getApplicationContext());
        stringBuilder = new StringBuilder();
        setContentView(R.layout.appmanager);
        spinner = findViewById(R.id.selectProfileSpinner);
        showNotification = findViewById(R.id.showNotification);
        readNotification = findViewById(R.id.readNotification);
        Button saveData = findViewById(R.id.saveData);

        List<String> appNames = appInfo.getAppNames();
        List<Drawable> appIcons = appInfo.getAppIcon();
        List<String> appPackageNames = appInfo.getAppPackageNames();


        TextInputEditText profileName = findViewById(R.id.profileName);
        TextInputEditText maxSpeed = findViewById(R.id.maxSpeed);
        ArrayList<String> data = new ArrayList<>();

        Button showApps = findViewById(R.id.showApps);

        showApps.setOnClickListener((View)->{

            ScrollView showAppsScrollView = findViewById(R.id.showAppsScrollView);
            showAppsScrollView.setVisibility(View.VISIBLE);
            saveData.setVisibility(android.view.View.VISIBLE);


            
        });
        showAppsLin = findViewById(R.id.showAppsLin);


        saveData.setOnClickListener((View)->{


                    for(int i = 0;i<showAppsLin.getChildCount();i++){
                        @SuppressLint("UseSwitchCompatOrMaterialCode")
                        Switch s = showAppsLin.getChildAt(i).findViewById(R.id.sw);
                        if(s.isChecked()){
                            data.add(appPackageNames.get(i)+ ";"+s.isChecked());
                        }
                    }
                    String profile_name = Objects.requireNonNull(profileName.getText()).toString().trim();
                    int max_speed = Integer.parseInt(Objects.requireNonNull(maxSpeed.getText()).toString().trim());
                    boolean show_notificaiton =showNotification.isChecked();
                    boolean read_notification = readNotification.isChecked();

                    FileHelper.writeData(getApplicationContext(),"App-Profiles",profile_name,profile_name,max_speed,show_notificaiton,read_notification,data);
                    data.clear();

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            adapter.notifyDataSetChanged();



                    finish();


        });



         thread = new Thread(()->{
             runOnUiThread(()-> {
                 for (int i = 0; i < appNames.size(); i++) {

                     View view = View.inflate(getApplicationContext(), R.layout.mybutton, null);
                     @SuppressLint("UseSwitchCompatOrMaterialCode") Switch s = view.findViewById(R.id.sw);
                     String appName = appNames.get(i);
                     ImageView icon = view.findViewById(R.id.image_icon);
                     icon.setImageDrawable(appIcons.get(i));
                     s.setText(appName);
                     showAppsLin.addView(view);
                 }

             });
        });





    }

    @Override
    protected void onResume() {
        super.onResume();

        thread.start();
    }



}
