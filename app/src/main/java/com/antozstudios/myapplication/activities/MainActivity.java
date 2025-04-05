package com.antozstudios.myapplication.activities;



import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.antozstudios.myapplication.R;


import com.antozstudios.myapplication.util.CustomTileFactory;
import com.antozstudios.myapplication.util.FileHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;



import org.osmdroid.views.MapView;


import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


public class MainActivity extends AppCompatActivity {


    CheckAppService mService;
    boolean mBound = false;



    private MyLocationNewOverlay mMyLocationOverlay;



    TextView setupSpeed;

    TextView currentProfile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,CheckAppService.class));

        setContentView(R.layout.activity_main);



        setupOSM();
        createThread();










    }


    /** Defines callbacks for service binding, passed to bindService(). */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            CheckAppService.LocalBinder binder = (CheckAppService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, CheckAppService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    void setupOSM() {
        MapView mMap = findViewById(R.id.mapview);
        Configuration.getInstance().load(
                this,
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );

        mMap.setTileSource(CustomTileFactory.Dark);
        mMap.setMultiTouchControls(true);
        double maxZoom = 22.0;
        mMap.setMaxZoomLevel(maxZoom);
        double minZoom = 20.0;
        mMap.setMinZoomLevel(minZoom);
        mMap.getLocalVisibleRect(new Rect());
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mMap);
        IMapController controller = mMap.getController();
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(false);
        controller.setZoom(minZoom);

        mMap.setBackgroundColor(Color.BLACK);
        mMap.getOverlays().add(mMyLocationOverlay);


    }





    void createThread() {


        Handler handler = new Handler();

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                handler.postDelayed(this, 1000);

                if (mMyLocationOverlay.getLastFix() != null) {

                    double newLat = mMyLocationOverlay.getLastFix().getLatitude();
                    double newLon = mMyLocationOverlay.getLastFix().getLongitude();


                }

            }




        };
        runnable.run();







        }












}

