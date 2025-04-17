package com.antozstudios.myapplication.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.data.GeoCoding;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.PostHttp;
import com.google.gson.Gson;

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

import java.io.IOException;

public class CheckAppService extends Service {

    private Thread locationThread;
    private PostHttp example;
    private GpsMyLocationProvider gpsProvider;
    private double lat = 0.0, lon = 0.0;
    GeoCoding geoCoding;

    GetRequestTask getRequestTask;
    int ampelState;
    int userID;
    String json;

    SharedPreferences stateData, userData;
    SharedPreferences.Editor stateDataEditor;
    boolean running = true;

    private static final String CHANNEL_ID = "SendLocation";

    @Override
    public void onCreate() {
        super.onCreate();
        example = new PostHttp();
        getRequestTask = new GetRequestTask();

        userData = getSharedPreferences("User_Data", MODE_PRIVATE);
        userID = userData.getInt("b_id", -1);
        stateData = getSharedPreferences("State_Data", MODE_PRIVATE);
        stateDataEditor = stateData.edit();




        gpsProvider = new GpsMyLocationProvider(getApplicationContext());


        gpsProvider.setLocationUpdateMinDistance(1); // Meter
        gpsProvider.setLocationUpdateMinTime(1000);

        gpsProvider.startLocationProvider((location, source) -> {
            lat = location.getLatitude();
            lon = location.getLongitude();
        });


        startSending();
startForegroundService();

    }

    private void startForegroundService() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "SendLocation",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Du bist sicher.")
                .setContentText("Die Position wird im Hintergrund gesendet.")
                .setSmallIcon(R.drawable.app_icon)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        } else {
            startForeground(1, notification);
        }

    }

    private void startSending() {
        Thread checkAmpel = new Thread(() -> {
            try {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    while (true) {
                        ampelState = stateData.getInt("currentState", 0);
                        if (ampelState > 0) {
                            updateCoordinates();
                        }
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                Log.e("CheckAppService", "Error in checkAmpel thread", e);
                stopSelf();
            } catch (Exception e) {
                Log.e("CheckAppService", "Unexpected error", e);
                stopSelf();
            }
        });
        checkAmpel.start();

        locationThread = new Thread(() -> {
            try {
                while (running) {
                    ampelState = stateData.getInt("currentState", 0);
                    if (lat > 0 && lon > 0 && ampelState == 0) {
                        updateCoordinates();
                        Thread.sleep(10000 * 60); // 10 Minuten
                    }
                }
            } catch (InterruptedException e) {
                Log.e("CheckAppService", "Error in locationThread", e);
                stopSelf(); // Stoppt den Service bei einem Fehler im Thread
            } catch (Exception e) {
                Log.e("CheckAppService", "Unexpected error", e);
                stopSelf();
            }
        });
        locationThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gpsProvider != null) gpsProvider.stopLocationProvider();
        if (locationThread != null) locationThread.interrupt();
        Log.d("CheckAppService", "Service destroyed.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void updateCoordinates() {
        SharedPreferences sharedPreferences = getSharedPreferences("Location_Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        getRequestTask.executeRequest("https://nominatim.openstreetmap.org/", "reverse?lat=" + lat + "&lon=+" + lon + "&format=json");

        geoCoding = new Gson().fromJson(getRequestTask.message, GeoCoding.class);

        if (geoCoding != null && geoCoding.address != null) {
            if (example != null) {
                json = example.sendCoordinates(lon, lat, userID, ampelState, geoCoding.address.road,
                        Integer.parseInt(geoCoding.address.postcode),
                        geoCoding.address.town, geoCoding.address.country);

                editor.putString("postCode", geoCoding.address.postcode);
                editor.putString("country", geoCoding.address.country);
                editor.putString("road", geoCoding.address.road);
                editor.putString("town", geoCoding.address.town);
                editor.apply();

                try {
                    example.post("http://app.mluetzkendorf.xyz/api/koordinaten", json);
                    stateDataEditor.putInt("currentState", 0);
                    stateDataEditor.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Handle the case when geoCoding or geoCoding.address is null
            Log.e("UpdateCoordinates", "GeoCoding or Address is null");
        }
    }

}
