package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;

import com.antozstudios.myapplication.data.FriendData;
import com.antozstudios.myapplication.data.GeoCoding;
import com.antozstudios.myapplication.util.CustomTileFactory;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    private MyLocationNewOverlay mMyLocationOverlay;
    private MapView mMap;


    Button greenStateButton;
    Button yellowStateButton;
    Button redStateButton;

    Button centerButton;
    Button observeButton;


    TextView countryTextView;
    TextView postCodeTextView;
    TextView townTextView;
    TextView roadTextView;




    GetRequestTask getRequestTask;

    SharedPreferences stateData;
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        setContentView(R.layout.activity_main);
        getRequestTask = new GetRequestTask();


        greenStateButton = findViewById(R.id.greenButton);
        yellowStateButton = findViewById(R.id.yellowButton);
        redStateButton = findViewById(R.id.redButton);

        centerButton = findViewById(R.id.centerButton);
        observeButton = findViewById(R.id.observeButton);

        postCodeTextView = findViewById(R.id.postCodeTextView);
        townTextView = findViewById(R.id.townTextView);
        countryTextView = findViewById(R.id.countryTextView);
        roadTextView = findViewById(R.id.roadTextView);
        setupOSM();
        Intent service = new Intent(this, CheckAppService.class);
        startService(service);






    }

    void setupOSM() {
        mMap = findViewById(R.id.mapview);
        Configuration.getInstance().load(
                this,
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );

        mMap.setTileSource(CustomTileFactory.Dark);
        mMap.setMultiTouchControls(true);


        double maxZoom = 22.0;
        mMap.setMaxZoomLevel(maxZoom);
        double minZoom = 7;
        mMap.setMinZoomLevel(minZoom);
        mMyLocationOverlay = new MyLocationNewOverlay(mMap);
        mMyLocationOverlay.setPersonIcon(null);


        IMapController controller = mMap.getController();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        controller.setZoom(minZoom);
        mMap.setBackgroundColor(Color.BLACK);
        mMap.getOverlays().add(mMyLocationOverlay);


        stateData = getSharedPreferences("State_Data",Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = stateData.edit();
        greenStateButton.setOnClickListener((view)->{
          editor.putInt("currentState",1);
          editor.apply();
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Grünes Signal wurde verschickt.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", null)
                    .show();
        });
        yellowStateButton.setOnClickListener((view)->{
            editor.putInt("currentState",2);
            editor.apply();
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Gelbes Signal wurde verschickt.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", null)
                    .show();
        });
        redStateButton.setOnClickListener((view)->{
            editor.putInt("currentState",3);
            editor.apply();
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Rotes Signal wurde verschickt.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", null)
                    .show();
        });

        centerButton.setOnClickListener((view)->{
            mMyLocationOverlay.enableFollowLocation();

        });

        observeButton.setOnClickListener((view) -> {
        startActivity(new Intent(MainActivity.this,ObserveActivity.class));
        });


        GetRequestTask getFriendData = new GetRequestTask();
        SharedPreferences userData = getSharedPreferences("User_Data",Context.MODE_PRIVATE);

        Thread requestFriendData = new Thread(()->{



            while(true){

                getFriendData.executeRequest("http://app.mluetzkendorf.xyz/api","/freund_daten?b_id=eq."+userData.getInt("b_id",0));

                FriendData[] friendData = new Gson().fromJson(getFriendData.message, FriendData[].class);

                runOnUiThread(()->{
                    for(int i =0;i<friendData.length;i++){
                        Marker marker = new Marker(mMap);
                        marker.setPosition(new GeoPoint(friendData[i].breitengrad,friendData[i].laengengrad));
                        mMap.getOverlays().add(marker);
                        mMap.invalidate();
                    }

                });

                try {
                    Thread.sleep(5000);


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }





        });
        requestFriendData.start();




    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();

        runOnUiThread(()->{
            SharedPreferences locationData = getSharedPreferences("Location_Data",MODE_PRIVATE);
            postCodeTextView.setText(locationData.getString("postCode",""));
            countryTextView.setText(locationData.getString("country",""));
            roadTextView.setText(locationData.getString("road",""));
            townTextView.setText(locationData.getString("town",""));







        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();
    }
}
