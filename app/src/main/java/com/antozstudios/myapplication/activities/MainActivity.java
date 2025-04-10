package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;

import com.antozstudios.myapplication.data.GeoCoding;
import com.antozstudios.myapplication.util.CustomTileFactory;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
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


    Thread thread;

    GetRequestTask getRequestTask;

    SharedPreferences stateData;
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        double minZoom = 10.0;
        mMap.setMinZoomLevel(minZoom);
        mMyLocationOverlay = new MyLocationNewOverlay(mMap);
        mMyLocationOverlay.setPersonIcon(null);


        IMapController controller = mMap.getController();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        controller.setZoom(minZoom);
        mMap.setBackgroundColor(Color.BLACK);
        mMap.getOverlays().add(mMyLocationOverlay);


        stateData = getSharedPreferences("state_data",Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = stateData.edit();
        greenStateButton.setOnClickListener((view)->{
          editor.putInt("currentState",1);
          editor.apply();
        });
        yellowStateButton.setOnClickListener((view)->{
            editor.putInt("currentState",2);
            editor.apply();
        });
        redStateButton.setOnClickListener((view)->{
            editor.putInt("currentState",3);
            editor.apply();
        });

        centerButton.setOnClickListener((view)->{
            mMyLocationOverlay.enableFollowLocation();
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();
    }
}
