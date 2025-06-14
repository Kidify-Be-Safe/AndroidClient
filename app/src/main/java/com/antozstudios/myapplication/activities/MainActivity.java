package com.antozstudios.myapplication.activities;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.antozstudios.myapplication.R;

import com.antozstudios.myapplication.data.FriendData;

import com.antozstudios.myapplication.util.CustomTileFactory;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.Hash;
import com.antozstudios.myapplication.util.PostHttp;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_UNLOCK =1001 ;
    ImageButton myObserverButton;




    Button sosButton;
    Button greenStateButton;
    Button yellowStateButton;
    Button redStateButton;
    Button centerButton;
    Button observeButton;
    ImageButton settingsButton;
    SharedPreferences userData,locationData,sosData;
    LocationManager locationManager ;
    TextView countryTextView;
    TextView postCodeTextView;
    TextView townTextView;
    TextView roadTextView;
    SharedPreferences.Editor userDataEditor,stateDataEditor,locationDataEditor,sosDataEditor;
    FriendData[]friendData;
    Thread updateMarker;
    AlertDialog gpsProvideraAlert;
    GetRequestTask getRequestTask;
    Intent service;
    SharedPreferences stateData;
    private MyLocationNewOverlay mMyLocationOverlay;
    private MapView mMap;
    Thread countDownThread;
    Intent sosService;
    private Handler handler = new Handler();
    private Runnable checkGPSRunnable = new Runnable() {
        @Override
        public void run() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
              if(!gpsProvideraAlert.isShowing()){
                  gpsProvideraAlert.show();
              }
            }else{
                gpsProvideraAlert.hide();
            }

            // Überprüfung alle 5 Sekunden erneut ausführen
            handler.postDelayed(this, 5000);
        }
    };
    private volatile boolean isRunning = true;



    @Override
    protected void onStart() {
        super.onStart();
        handler.post(checkGPSRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacks(checkGPSRunnable);

    }

    @SuppressLint({"MissingInflatedId", "WrongViewCast", "ClickableViewAccessibility"})
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


        KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
        PostHttp deleteKonto= new PostHttp(MainActivity.this);
         gpsProvideraAlert =   new AlertDialog.Builder(MainActivity.this)
                .setTitle("Dein GPS muss eingeschaltet werden.").setTitle("Wo bist du?")
                 .setCancelable(false)
                .setMessage("Ativiere deinen Standort.").setPositiveButton("Aktivieren", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).create();


        service  = new Intent(this, CheckAppService.class);
        sosService  = new Intent(this,SOS_Service.class);

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
            startAppService();
        }else{
            stopService(service);
        }
        setContentView(R.layout.activity_main);
        getRequestTask = new GetRequestTask(MainActivity.this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        greenStateButton = findViewById(R.id.greenButton);
        yellowStateButton = findViewById(R.id.yellowButton);
        redStateButton = findViewById(R.id.redButton);

        centerButton = findViewById(R.id.centerButton);
        observeButton = findViewById(R.id.observeButton);
        settingsButton = findViewById(R.id.settingsButton);

        postCodeTextView = findViewById(R.id.postCodeTextView);
        townTextView = findViewById(R.id.townTextView);
        countryTextView = findViewById(R.id.countryTextView);
        roadTextView = findViewById(R.id.roadTextView);

        sosButton = findViewById(R.id.sosButton);

/**
 * Setzt einen OnClickListener für den SOS-Button.
 *
 * Beim ersten Klick (wenn das Tutorial noch nicht abgeschlossen wurde) wird ein Hinweisdialog angezeigt,
 * der erklärt, wie der SOS-Knopf funktioniert. Danach wird das Tutorial als abgeschlossen markiert.
 *
 * Wenn das Tutorial bereits abgeschlossen wurde:
 * - Falls das SOS-Signal gerade aktiv ist, fragt ein Dialog, ob es beendet werden soll.
 *   Falls der Benutzer zustimmt, wird zur Sicherheit die Gerätesperre abgefragt.
 * - Falls das SOS-Signal nicht aktiv ist, fragt ein Dialog, ob es gestartet werden soll.
 *   Wird dies bestätigt, startet der SOS-Service und der Status wird gespeichert.
 */

        sosButton.setOnClickListener(view -> {
            boolean tutorial = userData.getBoolean("TutorialPassed", false);

            if (!tutorial) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Tippe den Knopf an. Es wird ein lauter Hilfeton umgehend gestartet!.")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            userDataEditor.putBoolean("TutorialPassed", true);
                            userDataEditor.apply();
                        })
                        .show();

            }else{
                boolean sos_IsRunning = userData.getBoolean("SOS",false);
                if(sos_IsRunning){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("SOS Abbrechen?")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (keyguardManager != null && keyguardManager.isKeyguardSecure()) {
                                        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(
                                                "Bitte entsperren!",
                                                "Damit das SOS-Signal abgebrochen wird."
                                        );
                                        if (intent != null) {
                                            startActivityForResult(intent, REQUEST_CODE_UNLOCK);
                                        }
                                    }

                                }
                            }).setNegativeButton("Abbrechen",null).show();

                }else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("SOS Starten? Signal startet umgehend!")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    startService(sosService);
                                    userDataEditor.putBoolean("SOS",true);
                                    userDataEditor.apply();

                                }
                            }).setNegativeButton("Abbrechen",null).show();
                }






                }


        });


        setupOSM();



        myObserverButton = findViewById(R.id.myObserverButton);

        myObserverButton.setOnClickListener((view) -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet);

            LinearLayout linearLayout = bottomSheetDialog.findViewById(R.id.bottomSheetLayout);


            GetRequestTask friendRequest = new GetRequestTask(MainActivity.this);
            Thread friendRequest_Thread = new Thread(()->{
                friendRequest.executeRequest(userData.getString("URL",""),"freunde?b_id=neq."+userData.getInt("b_id",0));

                FriendData[] friends = new Gson().fromJson(friendRequest.message,FriendData[].class);

                runOnUiThread(()->{

                    if(friends!=null){

                        if(friends.length==0){
                            TextView textView = new TextView(MainActivity.this);
                            textView.setText("Hoopla :(");
                            textView.setTextSize(30);
                            textView.setGravity(Gravity.CENTER);
                            linearLayout.addView(textView);
                            TextView message = new TextView(MainActivity.this);
                            message.setText("Du hast noch keine Beobachter.");
                            message.setGravity(Gravity.CENTER);
                            message.setTextSize(20);
                            linearLayout.addView(message);
                        }

                        Arrays.stream(friends).forEach(friend -> {
                            View field = View.inflate(MainActivity.this, R.layout.my_observer_field, null);
                            TextView userName_TextField = field.findViewById(R.id.userName);
                            ImageButton unfollowButton = field.findViewById(R.id.unfollow);

                            unfollowButton.setOnClickListener(view1 -> {
                                new AlertDialog.Builder(MainActivity.this).setTitle("Bist du dir sicher?").setNegativeButton("Abbrechen",null)
                                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Thread deleteThread = new Thread(()->{

                                            try {
                                                new PostHttp(MainActivity.this).delete("https://app.mluetzkendorf.xyz/api/freundesliste?b_id=eq."+userData.getInt("b_id",0)+"&f_id=eq."+friend.b_id,"");
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }


                                        });
                                        deleteThread.start();
                                        linearLayout.removeView(field);
                                        runOnUiThread(()->{
                                            Toast.makeText(MainActivity.this,"Beobachter entfernt",Toast.LENGTH_SHORT).show();
                                        });
                                        bottomSheetDialog.hide();

                                    }
                                }).show();
                            });

                            userName_TextField.setText(friend.vorname + " " + friend.nachname);
                            linearLayout.addView(field);
                        });



                    }

                });

            });

            friendRequest_Thread.start();

            bottomSheetDialog.show();

        });





settingsButton.setOnClickListener(view ->{
    BottomSheetDialog settings = new BottomSheetDialog(MainActivity.this);
    View settings_BottomSheetDialog = View.inflate(MainActivity.this,R.layout.settings_bottom_sheet,null);
    settings.setContentView(settings_BottomSheetDialog);

    Button logout = settings_BottomSheetDialog.findViewById(R.id.logutButton);
    logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {


          new AlertDialog.Builder(MainActivity.this).setPositiveButton("Ja", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  userDataEditor.clear();
                  stateDataEditor.clear();
                  locationDataEditor.clear();
                  locationDataEditor.apply();
                  userDataEditor.apply();
                  stateDataEditor.apply();
                    stopService(service);
                  startActivity(new Intent(MainActivity.this,LoginActivity.class));
                  finish();
              }
          })
                  .setMessage("Bist du dir sicher?")
                  .setNegativeButton("Nein",null)
                  .show();

        }
    });
    settings.show();

    Button deleteButton = settings_BottomSheetDialog.findViewById(R.id.deleteAccountButton);

    Switch darkModusButton = settings_BottomSheetDialog.findViewById(R.id.switch_darkModus);
    darkModusButton.setChecked(userData.getBoolean("DarkModus",false));
    darkModusButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                mMap.setTileSource(CustomTileFactory.Dark);

            } else {
                mMap.setTileSource(CustomTileFactory.Light);
            }
            mMap.getTileProvider().clearTileCache(); // Cache löschen
            mMap.invalidate(); // Karte neu zeichnen
            userDataEditor.putBoolean("DarkModus",b);
            userDataEditor.apply();
        }
    });


    deleteButton.setOnClickListener(view1->{
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        EditText email_EditText = new EditText(this);
        EditText passwort_EditText = new EditText(this);
        email_EditText.setHint("E-Mail");
        passwort_EditText.setHint("Passwort");

        Button button = new Button(this);
        button.setText("Löschen");
        button.setBackgroundColor(Color.RED);
        linearLayout.addView(email_EditText);
        linearLayout.addView(passwort_EditText);
        linearLayout.addView(button);

        button.setOnClickListener(view2 -> {
            if(email_EditText.getText().toString().toLowerCase().equals(userData.getString("lastEmail",""))){

                if(Hash.sha256(passwort_EditText.getText().toString()).equals(userData.getString("lastPasswort",""))){


                    new AlertDialog.Builder(MainActivity.this).setTitle("Bist du sicher?")
                            .setMessage("Dein Konto wird unwiderruflich gelöscht.")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Thread thread = new Thread(()->{
                                        try {
                                            if(!deleteKonto.delete("https://app.mluetzkendorf.xyz/api/benutzer?id=eq."+userData.getInt("b_id",0),new PostHttp(MainActivity.this).deleteUser(userData.getInt("b_id",0))).equals("error")){
                                                userDataEditor.clear();
                                                stateDataEditor.clear();
                                                locationDataEditor.clear();
                                                locationDataEditor.apply();
                                                userDataEditor.apply();
                                                stateDataEditor.apply();
                                                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                                stopService(service);
                                                finish();
                                            }
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                    thread.start();
                                }
                            })
                            .setNegativeButton("Abbrechen",null)
                            .show();




                }

            }
        });


        new AlertDialog.Builder(MainActivity.this).setView(linearLayout).show();
    });


});


        AlertDialog alertDialogLocation = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Wo bist du?")
                .setMessage("Erlaube den Zugriff auf deinen Standort.")
                .setPositiveButton("Standort aktivieren", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        }

                    }
                })
                .setCancelable(false).create();


        AlertDialog alertDialogNotification = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Benachrichtigung")
                .setMessage("Sei auch sicher, wenn deine App im Hintergrund läuft.")
                .setPositiveButton("Benachrichtigung aktivieren", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);

                    }
                })
                .setCancelable(false).create();

       Thread thread = new Thread(()->{

           while(true){

               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
               runOnUiThread(()->{
                   if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                           !=PackageManager.PERMISSION_GRANTED){
                       if(!alertDialogLocation.isShowing()){
                           alertDialogLocation.show();
                       }


                   }else{
                alertDialogLocation.hide();
                       if(ContextCompat.checkSelfPermission(MainActivity.this,
                               Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
                           if(!alertDialogLocation.isShowing()){
                               alertDialogNotification.show();
                                stopService(service);
                           }
                       }else{
                            alertDialogNotification.hide();
                       }


                   }

               });

               runOnUiThread(() -> {
                   SharedPreferences locationData = getSharedPreferences("Location_Data", MODE_PRIVATE);
                   postCodeTextView.setText(locationData.getString("postCode", ""));
                   countryTextView.setText(locationData.getString("country", ""));
                   roadTextView.setText(locationData.getString("road", ""));
                   townTextView.setText(locationData.getString("town", ""));
               });

           }

       });
       thread.start();


    }


    /**
     * Initialisiert und konfiguriert die OpenStreetMap (OSM) Karte sowie die zugehörigen Overlay-Elemente.
     *
     * Diese Methode richtet die Karte ein, lädt die gespeicherten Benutzereinstellungen, konfiguriert die Kartenansicht
     * und setzt die Zoom-Stufen. Es wird auch ein Standortüberlagerung für die Benutzerposition hinzugefügt und die
     * GPS-Koordinaten des Benutzers sowie die Daten von Freunden auf der Karte angezeigt.
     *
     * Die Methode verwaltet auch Interaktionen mit verschiedenen Buttons, wie das Verschicken von Signalen (Grün, Gelb, Rot),
     * das Zentrumsen der Karte auf den aktuellen Standort des Benutzers und das Öffnen einer Beobachtungsaktivität.
     * Zusätzlich werden Marker für Freunde auf der Karte platziert, die abhängig vom Status der Freunde eine
     * entsprechende Farbe erhalten.
     *
     * @see MyLocationNewOverlay
     * @see Marker
     * @see FriendData
     * @see AlertDialog
     */
    void setupOSM() {
        mMap = findViewById(R.id.mapview);
        Configuration.getInstance().load(
                this,
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );
        stateData = getSharedPreferences("State_Data",Context.MODE_PRIVATE);
        userData =   getSharedPreferences("User_Data",Context.MODE_PRIVATE);

        locationData = getSharedPreferences("Location_Data", MODE_PRIVATE);


        if(userData.getBoolean("DarkModus",false)){
            mMap.setTileSource(CustomTileFactory.Dark);
        }else{
            mMap.setTileSource(CustomTileFactory.Light);
        }

        mMap.setMultiTouchControls(true);

        double maxZoom = 22.0;
        mMap.setMaxZoomLevel(maxZoom);
        double minZoom = 7;
        mMap.setMinZoomLevel(minZoom);


            mMyLocationOverlay = new MyLocationNewOverlay(mMap);
            Bitmap personIcon = Bitmap.createBitmap(1,1, Bitmap.Config.RGB_565);
            mMyLocationOverlay.setPersonIcon(personIcon);

        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        mMap.setBackgroundColor(Color.BLACK);
        mMap.getOverlays().add(mMyLocationOverlay);

        IMapController controller = mMap.getController();


        controller.setZoom(minZoom);
        mMap.invalidate();






        stateDataEditor = stateData.edit();
        userDataEditor = userData.edit();



        locationDataEditor = locationData.edit();
        greenStateButton.setOnClickListener((view)->{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    stateDataEditor.putInt("currentState", 1);
                    stateDataEditor.apply();
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Grünes Signal wurde verschickt.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", null)
                            .show();
                }

            }
        });
        yellowStateButton.setOnClickListener((view)->{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    stateDataEditor.putInt("currentState", 2);
                    stateDataEditor.apply();
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Gelbes Signal wurde verschickt.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }
        });
        redStateButton.setOnClickListener((view)->{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                  new AlertDialog.Builder(MainActivity.this).setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          stateDataEditor.putInt("currentState", 3);
                          stateDataEditor.apply();
                          new AlertDialog.Builder(MainActivity.this)
                                  .setMessage("Rotes Signal wurde verschickt.")
                                  .setCancelable(false)
                                  .setPositiveButton("Ok", null)
                                  .show();
                      }
                  }).setMessage("Bist du dir sicher?")
                          .setNegativeButton("Abbrechen",null).show();
            }
            }

        });

        centerButton.setOnClickListener((view)->{
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            mMyLocationOverlay.enableFollowLocation();
}
        });

        observeButton.setOnClickListener((view) -> {
        startActivity(new Intent(MainActivity.this,ObserveActivity.class));
        });

        mMap.addOnFirstLayoutListener((v, left, top, right, bottom) -> {

            isRunning = true;

            updateMarker = new Thread(() -> {
                while (isRunning) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        getRequestTask.executeRequest(userData.getString("URL",""), "freundekoordinaten_view?b_id=neq." + userData.getInt("b_id", 0));
                        if (getRequestTask.message != null && !getRequestTask.message.isEmpty()) {
                            try {
                                friendData = new Gson().fromJson(getRequestTask.message, FriendData[].class);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                        } else {
                            continue;
                        }

                        runOnUiThread(() -> {
                            if (friendData != null && mMap != null && mMap.getRepository() != null) {

                                mMap.getOverlays().removeIf(item -> item instanceof Marker);

                                for (FriendData friend : friendData) {
                                    Marker marker = new Marker(mMap);
                                    marker.setPosition(new GeoPoint(friend.breitengrad, friend.laengengrad));
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    marker.setInfoWindow(null);

                                    Drawable tempDrawable;
                                    String name = friend.vorname + " " + friend.nachname;

                                    if (friend.ampel == 1) {
                                        tempDrawable = getTextIcon(name.toUpperCase(), Color.GREEN, 60, Color.WHITE);
                                    } else if (friend.ampel == 2) {
                                        tempDrawable = getTextIcon(name.toUpperCase(), Color.YELLOW, 60, Color.BLACK);
                                    } else if (friend.ampel == 3) {
                                        tempDrawable = getTextIcon(name.toUpperCase(), Color.RED, 60, Color.WHITE);
                                    } else {
                                        tempDrawable = getTextIcon(name.toUpperCase(), Color.WHITE, 60, Color.BLACK);
                                    }

                                    marker.setIcon(tempDrawable);
                                    marker.setTitle(name);

                                    FriendData finalFriend = friend;
                                    marker.setOnMarkerClickListener((marker1, mapView) -> {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("Name: " + finalFriend.vorname + " " + finalFriend.nachname + "\n\n" +
                                                        "Adresse: " + finalFriend.strasse + " " + finalFriend.stadt + "\n" +
                                                        "Zeitpunkt: " + formatDate(finalFriend.zeitpunkt) + "\n\n" +
                                                        "Koordinaten:\n" + "Breitengrad: " + finalFriend.breitengrad + "\n" +
                                                        "Längengrad: " + finalFriend.laengengrad)
                                                .show();
                                        return true;
                                    });

                                    mMap.getOverlays().add(marker);
                                }

                                mMap.invalidate();
                            }
                        });

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            updateMarker.start();
        });



    }





    /**
     * Wird aufgerufen, wenn eine gestartete Aktivität (z. B. zum Entsperren des Geräts)
     * ein Ergebnis zurückgibt. In diesem Fall wird überprüft, ob der Benutzer das Gerät
     * erfolgreich entsperrt hat, um den SOS-Modus zu deaktivieren.
     *
     * @param requestCode Der ursprüngliche Anfragecode, der beim Starten der Aktivität übergeben wurde.
     * @param resultCode  Der Ergebniscode, der von der Aktivität zurückgegeben wurde.
     * @param data        Ein Intent, der evtl. zusätzliche Daten enthält (hier nicht genutzt).
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UNLOCK && resultCode == RESULT_OK) {


            stopService(sosService);
            userDataEditor.putBoolean("SOS",false);
            userDataEditor.apply();



            }

        }





    /**
     * Wird aufgerufen, wenn die Aktivität wieder in den Vordergrund tritt und die Benutzeroberfläche sichtbar wird.
     *
     * In dieser Methode wird überprüft, ob die erforderliche Berechtigung für den Zugriff auf den Standort
     * des Benutzers (`ACCESS_FINE_LOCATION`) gewährt wurde. Wenn die Berechtigung erteilt wurde, wird die Karte
     * (OSM) fortgesetzt, die Standortüberlagerung aktiviert und der Hintergrunddienst gestartet.
     * Andernfalls wird der Hintergrunddienst gestoppt.
     *
     * @see ContextCompat#checkSelfPermission(Context, String)
     * @see PackageManager#PERMISSION_GRANTED
     * @see MapView#onResume()
     * @see MyLocationNewOverlay#enableFollowLocation()
     * @see MainActivity#startAppService()

     */

    @Override
    protected void onResume() {
        super.onResume();
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
            mMap.onResume();
            isRunning = true;
            mMyLocationOverlay.enableFollowLocation();
            startAppService();
        }else{
            stopService(service);
        }






    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();



    }


    /**
     * Erzeugt ein Bitmap-Icon mit Text, der auf einem Hintergrund gezeichnet wird.
     *
     * Diese Methode erstellt ein Bitmap mit einem Text, der auf einem Hintergrund in einer angegebenen Farbe
     * gezeichnet wird. Der Text wird in einer benutzerdefinierten Schriftgröße und -farbe dargestellt.
     * Das resultierende Bitmap wird als `BitmapDrawable` zurückgegeben, um es als Drawable in einer Android-App zu verwenden.
     *
     * @param pText Der Text, der auf dem Bitmap gezeichnet werden soll.
     * @param backgroundColor Die Hintergrundfarbe des Icons.
     * @param textSize Die Schriftgröße des Textes.
     * @param foregroundColor Die Farbe des Textes.
     * @return Ein `BitmapDrawable`, das das erzeugte Bitmap mit dem Text und Hintergrund darstellt.
     */
    public BitmapDrawable getTextIcon(final String pText, int backgroundColor, int textSize, int foregroundColor) {
        // Hintergrundfarbe
        Paint background = new Paint();
        background.setColor(backgroundColor);

        // Text-Stil
        Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(foregroundColor);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.LEFT);


        int width = (int) (textPaint.measureText(pText) + 0.5f);
        float baseline = -textPaint.ascent(); // baseline zum Zeichnen
        int height = (int) (baseline + textPaint.descent() + 0.5f);


        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);


        canvas.drawRect(0, 0, width, height, background);
        canvas.drawText(pText, 0, baseline, textPaint);


        return new BitmapDrawable(getApplicationContext().getResources(), image);
    }



    /**
     * Formatiert ein Datums-String von einem ISO 8601-Format in ein benutzerdefiniertes Datumsformat.
     *
     * Diese Methode nimmt einen Datums-String im Format "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX" (ISO 8601) entgegen,
     * konvertiert ihn in ein Date-Objekt und gibt das Datum im Format "dd.MM.yyyy HH:mm:ss" zurück.
     * Die Umwandlung erfolgt unter der Annahme, dass die Eingabezeit im UTC-Zeitraum vorliegt.
     *
     * @param dateStr Der Eingabe-Datums-String im ISO 8601 Format.
     * @return Der formatierte Datums-String im Format "dd.MM.yyyy HH:mm:ss".
     * @throws RuntimeException Wenn die Eingabe nicht geparst werden kann.
     */
    public String formatDate(String dateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));


        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return outputFormat.format(date);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;

    }


    /**
     * Startet den Dienst als Vordergrunddienst.
     * Dies sorgt dafür, dass der Dienst auch dann weiterläuft, wenn die App im Hintergrund ist.
     * Der Dienst wird mit der `startForegroundService()`-Methode gestartet, um sicherzustellen,
     * dass er im Vordergrund ausgeführt wird und nicht vom System gestoppt wird, wenn die App im Hintergrund ist.
     */
    private void startAppService() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        }
    }

}
