package com.antozstudios.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.PointerIcon;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.util.CustomTileFactory;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
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

    Bitmap anchor;


    SharedPreferences stateData;
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anchor = Bitmap.createBitmap(50,50, Bitmap.Config.RGB_565);

        greenStateButton = findViewById(R.id.greenButton);
        yellowStateButton = findViewById(R.id.yellowButton);
        redStateButton = findViewById(R.id.redButton);

        centerButton = findViewById(R.id.centerButton);
        observeButton = findViewById(R.id.observeButton);

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
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mMap);

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
