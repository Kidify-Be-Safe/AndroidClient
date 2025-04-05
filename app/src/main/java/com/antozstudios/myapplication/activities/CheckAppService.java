package com.antozstudios.myapplication.activities;

import static android.content.Intent.ACTION_INSERT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.antozstudios.myapplication.util.FileHelper;
import com.antozstudios.myapplication.util.GetApps;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class CheckAppService extends Service {

    GetApps getApps;

    private final IBinder binder = (IBinder) new LocalBinder();
    // Random number generator.


    @Override
    public void onCreate() {
        super.onCreate();
        getApps = GetApps.getInstance(getApplicationContext());

    }

    public class LocalBinder extends Binder {
        CheckAppService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return CheckAppService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 500);
            }
        };
        runnable.run();
        return super.onStartCommand(intent, flags, startId);
    }





}