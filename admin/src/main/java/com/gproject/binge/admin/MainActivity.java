package com.gproject.binge.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ImageView imgLogout;
    TextView tvUsername;
    FloatingActionButton fbAdd;

    adapter adapter;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    //offline data storage
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgLogout = findViewById(R.id.imgLogout);
        tvUsername = findViewById(R.id.tvUsername);
        fbAdd = findViewById(R.id.fbAdd);

        SharedPreferences sp = getSharedPreferences("credentials", MODE_PRIVATE);
        String spText = sp.getString("username", "");
        String username = "👤 "+spText;


        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), add.class);
                startActivity(intent);
            }
        });

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieCount(username);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("movies");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(mLayoutManager);


        FirebaseRecyclerOptions<model> options
                = new FirebaseRecyclerOptions.Builder<model>()
                .setQuery(databaseReference, model.class)
                .build();


        adapter = new adapter(options, getApplicationContext());
        recyclerView.setAdapter(adapter);

        getMovieCount(username);

    }

    private void getMovieCount(String username) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childCount = (int) dataSnapshot.getChildrenCount();
                String subTitle = username+" | "+"Total Movies: "+ childCount;
                tvUsername.setText(subTitle);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}