package com.antozstudios.myapplication.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.antozstudios.myapplication.R;


import android.app.AppOpsManager;


public class PermissionActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUESTCODE = 100;


    Button gpsoutButton,usageButton;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );


        setContentView(R.layout.activity_permission);


        gpsoutButton = findViewById(R.id.gpsButton);

        usageButton = findViewById(R.id.usageButton);

        gpsoutButton.setOnClickListener(view -> {
           if(!checkLocation(this)){
               requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUESTCODE);
           }
        });



        usageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasUsageStatsPermission(PermissionActivity.this)){
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

                }
            }
        });






    }




    @Override
    protected void onResume() {
        super.onResume();
        setColorForButtons();

        if(checkLocation(this) && hasUsageStatsPermission(PermissionActivity.this)){
            Intent myIntent = new Intent(PermissionActivity.this,LoginActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);


            finish();
        }

    }
    void setColorForButtons(){
        if (checkLocation(this)) {
            gpsoutButton.setBackgroundColor(Color.GREEN);
        } else {
            gpsoutButton.setBackgroundColor(Color.RED);
        }




        if (hasUsageStatsPermission(PermissionActivity.this)) {
            usageButton.setBackgroundColor(Color.GREEN);
        } else {
            usageButton.setBackgroundColor(Color.RED);
        }



    }


     public static boolean checkLocation(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }






    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }



}
