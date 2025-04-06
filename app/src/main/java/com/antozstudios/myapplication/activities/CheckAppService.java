package com.antozstudios.myapplication.activities;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.antozstudios.myapplication.util.PostHttp;

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.io.IOException;

public class CheckAppService extends Service {

    private Thread locationThread;
    private PostHttp example;
    private GpsMyLocationProvider gpsProvider;
    private double lat = 0.0, lon = 0.0;

    int userID;
    @Override
    public void onCreate() {
        super.onCreate();
        example = new PostHttp();
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
       userID = sharedPreferences.getInt("userID",0);
        // GPS-Provider initialisieren
        gpsProvider = new GpsMyLocationProvider(getApplicationContext());

        // Listener setzen
        gpsProvider.setLocationUpdateMinDistance(1); // Meter
        gpsProvider.setLocationUpdateMinTime(10000*60);  // 10000 ms * 60 = 10 Minuten

        gpsProvider.startLocationProvider(new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    Log.d("SERVICE", "Neue Koordinaten: " + lat + ", " + lon);
                }
            }
        });

        startSending();
    }

    private void startSending() {
        locationThread = new Thread(() -> {

            while (true) {
                if(lat > 0 && lon>0){
                    String json = example.sendCoordinates(lon, lat, userID);
                    try {
                        String response = example.post("http://app.mluetzkendorf.xyz/api/koordinaten", json);
                        Log.d("SERVICE", "POST: " + response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(10000*60); // 10000 ms * 60 = 10 Minuten
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        locationThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gpsProvider != null) gpsProvider.stopLocationProvider();
        if (locationThread != null) locationThread.interrupt();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
