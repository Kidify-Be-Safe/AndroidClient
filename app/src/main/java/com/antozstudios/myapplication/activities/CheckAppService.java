package com.antozstudios.myapplication.activities;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.antozstudios.myapplication.data.GeoCoding;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.PostHttp;
import com.google.gson.Gson;

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.io.IOException;

public class CheckAppService extends Service {

    private Thread locationThread;
    private PostHttp example;
    private GpsMyLocationProvider gpsProvider;
    private double lat = 0.0, lon = 0.0;
    GeoCoding geoCoding;
    SharedPreferences stateData;
    SharedPreferences.Editor editor;
    GetRequestTask getRequestTask;
    int ampelState;
    int userID;
    String json;

    boolean running = true;
    @Override
    public void onCreate() {
        super.onCreate();
        example = new PostHttp();
        getRequestTask = new GetRequestTask();
        SharedPreferences userData = getSharedPreferences("user_data", Context.MODE_PRIVATE);
      stateData =  getSharedPreferences("state_data", Context.MODE_PRIVATE);
        editor  = stateData.edit();
       userID = userData.getInt("userID",0);

        // GPS-Provider initialisieren
        gpsProvider = new GpsMyLocationProvider(getApplicationContext());

        // Listener setzen
        gpsProvider.setLocationUpdateMinDistance(1); // Meter
        gpsProvider.setLocationUpdateMinTime(1000);

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


        Thread checkAmpel= new Thread(()->{

            while(true){
                ampelState = stateData.getInt("currentState",0);
                if(ampelState>0){
                    updateCoordinates();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        checkAmpel.start();




        locationThread = new Thread(() -> {



            while (running) {
                ampelState = stateData.getInt("currentState",0);
                if(lat > 0 && lon>0 && ampelState==0){

                    updateCoordinates();
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

    void updateCoordinates(){




        getRequestTask.executeRequest("https://nominatim.openstreetmap.org/","reverse?lat="+lat+"&lon=+"+lon+"&format=json");

        geoCoding = new Gson().fromJson(getRequestTask.message,GeoCoding.class);

        json = example.sendCoordinates(lon, lat, userID,ampelState,geoCoding.address.road,Integer.parseInt(geoCoding.address.postcode),geoCoding.address.town,geoCoding.address.country);


        try {
             example.post("http://app.mluetzkendorf.xyz/api/koordinaten", json);
            editor.putInt("currentState",0);
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
