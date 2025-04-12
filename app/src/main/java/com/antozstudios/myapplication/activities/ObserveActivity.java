package com.antozstudios.myapplication.activities;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antozstudios.myapplication.R;
import com.antozstudios.myapplication.data.User;
import com.antozstudios.myapplication.util.GetRequestTask;
import com.antozstudios.myapplication.util.PostHttp;
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

    GetRequestTask id_Request = new GetRequestTask();
    PostHttp postHttp = new PostHttp();
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {

                    Thread getID = new Thread(()->{
                        id_Request.executeRequest("http://app.mluetzkendorf.xyz/api/benutzer","?b_id_hash=eq."+"f4a3cffbeb7ad9883d7896522f75760f7d86db93ef1cc639be6be7077e4bff17");

                    });
                    getID.start();

                    Thread post= new Thread(()->{


                        try {
                            getID.join();
                            SharedPreferences sharedPreferences = getSharedPreferences("User_State",0);
                            User[] users = new Gson().fromJson(id_Request.message,User[].class);

                            if(users.length==1){
                                String tempFreundesliste = postHttp.sendFriend(sharedPreferences.getInt("b_id",0),users[0].id);

                                try {
                                    postHttp.post("http://app.mluetzkendorf.xyz/api/freundesliste",tempFreundesliste);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Looper.prepare();
                                Toast.makeText(this,"User gefunden.", LENGTH_LONG).show();
                                Looper.loop();
                            }else{
                                Looper.prepare();
                                Toast.makeText(this,"User nicht gefunden.", LENGTH_LONG).show();
                                Looper.loop();
                            }



                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    });

                    post.start();







                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observe_activity);
    hashTextView = findViewById(R.id.hashTextView);
        SharedPreferences sharedPreferences = getSharedPreferences("User_Data",MODE_PRIVATE);
        tempHASH = sharedPreferences.getString("b_id_hash","");
scanButton = findViewById(R.id.scanButton);
copyButton = findViewById(R.id.copyButton);

    hashTextView.setText(tempHASH);

    copyButton.setOnClickListener((view)->{
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", hashTextView.getText());
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this, "ID kopiert!", Toast.LENGTH_SHORT).show();

    });



        barcodeView = findViewById(R.id.barcodeView);



        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(tempHASH, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcodeView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }





        scanButton.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setBeepEnabled(true).setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            barcodeLauncher.launch(options); // Startet den Scanner mit den definierten Optionen
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
