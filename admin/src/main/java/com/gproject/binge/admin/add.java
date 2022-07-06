package com.gproject.binge.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class add extends AppCompatActivity {

    ImageView imgBack;
    TextInputEditText itMovieName, itMessage, itMovieLink, itImg, itVid;
    Button btnPublish;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("movies");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //-------------------admob initialization----------------------
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                        .build());

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        //--------------------------------------------

        imgBack = findViewById(R.id.imgBack);
        itMovieName = findViewById(R.id.itMovieName);
        itMessage = findViewById(R.id.itMessage);
        itMovieLink = findViewById(R.id.itMovieLink);
        btnPublish = findViewById(R.id.btnPublish);
        itImg = findViewById(R.id.itImg);
        itVid = findViewById(R.id.itVid);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });
    }

    private void insert() {

        String key = dbRef.push().getKey();

        Map<String,Object> map = new HashMap<>();
        map.put("id", key);
        map.put("name",itMovieName.getText().toString());
        map.put("message",itMessage.getText().toString());
        map.put("link",itMovieLink.getText().toString());
        map.put("img", itImg.getText().toString());
        map.put("vid", itVid.getText().toString());
        map.put("views", "0");

        String timeStamp = new SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()).format(new Date());
        map.put("date", timeStamp);

        SharedPreferences sp = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        map.put("admin",username);

        dbRef.child(key).setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        itMovieName.setText("");
                        itMessage.setText("");
                        itMovieLink.setText("");
                        Toast.makeText(getApplicationContext(),"Published successfully", Toast.LENGTH_SHORT ).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Could not publish", Toast.LENGTH_SHORT ).show();
                    }
                });
    }
}