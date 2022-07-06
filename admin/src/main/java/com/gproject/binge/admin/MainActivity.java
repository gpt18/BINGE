package com.gproject.binge.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ImageView imgLogout;
    TextView tvUsername, tvUser;
    FloatingActionButton fbAdd;
    SearchView searchView;

    adapter adapter;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    LinearLayoutManager mLayoutManager;

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
        tvUser = findViewById(R.id.tvUser);
        fbAdd = findViewById(R.id.fbAdd);
        searchView = (SearchView) findViewById(R.id.searchView);


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
                Intent i = new Intent(MainActivity.this, add.class);
                startActivity(i);
            }
        });

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieCount(username);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                processSearch(query);
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("movies");

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(null);


        FirebaseRecyclerOptions<model> options
                = new FirebaseRecyclerOptions.Builder<model>()
                .setQuery(databaseReference, model.class)
                .build();


        adapter = new adapter(options, getApplicationContext());
        recyclerView.setAdapter(adapter);

        getMovieCount(username);

    }

    private void getMovieCount(String username) {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childCount = (int) dataSnapshot.getChildrenCount();
                String subTitle = username+" | "+"Total Items: "+ childCount;
                tvUsername.setText(subTitle);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childCount = (int) dataSnapshot.getChildrenCount()-1;
                String users = "Total Users: "+ childCount;
                tvUser.setText(users);
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

    private void processSearch(String query) {

        FirebaseRecyclerOptions<model> options
                = new FirebaseRecyclerOptions.Builder<model>()
                .setQuery(databaseReference.orderByChild("name").startAt(query.toUpperCase()).endAt(query.toUpperCase()+"\uf8ff"), model.class)
                .build();

        adapter = new adapter(options);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}