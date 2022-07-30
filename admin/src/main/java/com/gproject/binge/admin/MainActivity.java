package com.gproject.binge.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView imgLogout;
    TextView tvUsername, tvUser, tvUserOnline, tvUserOffline;
    FloatingActionButton fbAdd;
    SearchView searchView;

    adapter adapter;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;


    //offline data storage
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private List<model> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgLogout = findViewById(R.id.imgLogout);
        tvUsername = findViewById(R.id.tvUsername);
        tvUser = findViewById(R.id.tvUser);
        tvUserOnline = findViewById(R.id.tvUserOnline);
        tvUserOffline = findViewById(R.id.tvUserOffline);
        fbAdd = findViewById(R.id.fbAdd);
        searchView = (SearchView) findViewById(R.id.searchView);

        //init
        itemList = new ArrayList<>();

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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("movies");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    model item = data.getValue(model.class);
                    itemList.add(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        FirebaseRecyclerOptions<model> options
//                = new FirebaseRecyclerOptions.Builder<model>()
//                .setQuery(databaseReference, model.class)
//                .build();
//
//
//        adapter = new adapter(options, getApplicationContext());
//        recyclerView.setAdapter(adapter);

        adapter = new adapter(itemList, getApplicationContext());
        recyclerView.setAdapter(adapter);

        getMovieCount(username);

    }

    private void filterList(String text) {
        List<model> filteredList = new ArrayList<>();
        String notFound = "No results for: "+text;

        for (model item : itemList){
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getMessage().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }


            if (filteredList.isEmpty()){
                Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
            }
            else {

                recyclerView.setAdapter(adapter);
                adapter.setFilteredList(filteredList);

            }


    }

    private void getMovieCount(String username) {

        final int[] childCount = {0};
        final int[] onlineCount = {0};
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

        DatabaseReference dbRefUser = FirebaseDatabase.getInstance().getReference("users");

        dbRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onlineCount[0] = 0;
                for (DataSnapshot data : snapshot.getChildren()) {

                    if (Objects.equals(data.getValue(), "1")){
                        onlineCount[0]++;
                    }
                }
                String online = "Online: "+ onlineCount[0];
                tvUserOnline.setText(online);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childCount[0] = (int) dataSnapshot.getChildrenCount()-1;

                String users = "Total Users: "+ childCount[0];
                tvUser.setText(users);



                int offlineCount = childCount[0]-onlineCount[0];
                String offline = "Offline: "+ offlineCount;
                tvUserOffline.setText(offline);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });






    }




    private void processSearch(String query) {


    }
}