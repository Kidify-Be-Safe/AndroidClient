package com.antozstudios.myapplication.activities;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.data.FriendData;
import com.antozstudios.myapplication.data.User;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.PostHttp;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;


public class ObserveActivity extends AppCompatActivity {

    ImageView barcodeView;
    String tempHASH;

    TextView hashTextView;

    Button scanButton;

    Button copyButton;

    TextInputEditText idInput;
    Button idButton;
    SharedPreferences userData;

    User[] users;
    User[] observer;
    GetRequestTask id_Request = new GetRequestTask();
    GetRequestTask observerRequest = new GetRequestTask();
    PostHttp postHttp = new PostHttp();
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {

                    addUser(result.getContents());

                }
            });

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userData = getSharedPreferences("User_Data", MODE_PRIVATE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        setContentView(R.layout.observe_activity);
        hashTextView = findViewById(R.id.hashTextView);
        SharedPreferences sharedPreferences = getSharedPreferences("User_Data", MODE_PRIVATE);
        tempHASH = sharedPreferences.getString("b_id_hash", "");
        scanButton = findViewById(R.id.scanButton);
        copyButton = findViewById(R.id.copyButton);
        idInput = findViewById(R.id.idInput);
        idButton = findViewById(R.id.idButton);
        hashTextView.setText(tempHASH);

        copyButton.setOnClickListener((view) -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", hashTextView.getText());
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(this, "ID kopiert!", Toast.LENGTH_SHORT).show();

        });


        barcodeView = findViewById(R.id.barcodeView);


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(tempHASH, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcodeView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }


        scanButton.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setBeepEnabled(true).setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            barcodeLauncher.launch(options);
        });

        idButton.setOnClickListener((view) -> {
            addUser(idInput.getText().toString());
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void addUser(String value) {


        if(value.equals(userData.getString("b_id_hash",""))){
            Looper.prepare();
            Toast.makeText(ObserveActivity.this,"Du hast deine eigene ID eingegeben.", LENGTH_LONG).show();
        }else {
            Thread getID = new Thread(() -> {
                id_Request.executeRequest("http://app.mluetzkendorf.xyz/api/benutzer", "?b_id_hash=eq."+value);
            });
            getID.start();

            Thread threadIfUserExist = new Thread(()->{
                try {
                    getID.join();
                    users = new Gson().fromJson(id_Request.message, User[].class);


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
            threadIfUserExist.start();


            Thread checkObserver = new Thread(()->{

                try {
                    threadIfUserExist.join();
                    if(users.length>0){
                        observerRequest.executeRequest("http://app.mluetzkendorf.xyz/api/","freundesliste?b_id=eq."+userData.getInt("b_id",0)+"&f_id=eq."+users[0].id);
                    }else{
                        Looper.prepare();
                        Toast.makeText(ObserveActivity.this,"User nicht gefunden", LENGTH_LONG).show();
                        Looper.loop();
                    }


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }




            });

            checkObserver.start();

            Thread addUser = new Thread(()->{

                try {
                    checkObserver.join();

                        observer= new Gson().fromJson(observerRequest.message,User[].class);
                        try {

                            if(observer.length>=1){
                                Looper.prepare();
                                Toast.makeText(ObserveActivity.this,"User wird bereits beobachtet.", LENGTH_LONG).show();
                            }else{
                                new PostHttp().post("http://app.mluetzkendorf.xyz/api/freundesliste",new PostHttp().sendFriend(userData.getInt("b_id",0),users[0].id));
                                Looper.prepare();
                                Toast.makeText(ObserveActivity.this,"User wird beobachtet.", LENGTH_LONG).show();
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }




                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            addUser.start();





            finish();
        }
        }


}