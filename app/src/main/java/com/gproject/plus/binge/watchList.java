package com.gproject.plus.binge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gproject.plus.binge.room.mAdapter;
import com.gproject.plus.binge.room.mDao;
import com.gproject.plus.binge.room.mDatabase;
import com.gproject.plus.binge.room.mEntity;

import java.util.List;

public class watchList extends AppCompatActivity {

    ImageView imgHeader;
    RecyclerView recView;
    Toolbar toolbar;
    mDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgHeader = findViewById(R.id.imgHeader);

        Glide.with(this).load("https://bit.ly/3NMVr8q").into(imgHeader);

        getRoomData();



    }

    private void getRoomData() {
        mDatabase db = Room.databaseBuilder(getApplicationContext(),
                mDatabase.class, "room_db").allowMainThreadQueries().build();
        mDao moviesDao = db.moviesDao();

        recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
//        recView.setLayoutManager(layoutManager);
//        recView.setItemAnimator(null);

        List<mEntity> moviesTable =  moviesDao.getAllMovies();

        mAdapter adapter = new mAdapter(moviesTable);
        recView.setAdapter(adapter);



    }

}