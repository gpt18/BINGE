package com.gproject.plus.binge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class watchList extends AppCompatActivity {

    ImageView imgHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);

        imgHeader = findViewById(R.id.imgHeader);

        Glide.with(this).load("https://bit.ly/3NMVr8q").into(imgHeader);
    }
}