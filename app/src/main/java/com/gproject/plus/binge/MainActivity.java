package com.gproject.plus.binge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    adapter adapter;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ImageView more;
    TextView tvUsername;


    //offline data storage
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        more = findViewById(R.id.more);
        tvUsername = findViewById(R.id.tvUsername);

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieCount();
            }
        });


        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Developer")
                        .setMessage("BINGE+ App is Created by G. Prajapati.")
                        .setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Your Welcome", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
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

        getMovieCount();

    }

    private void getMovieCount() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childCount = (int) dataSnapshot.getChildrenCount();
                String subTitle =  "Total Movies: "+ childCount;
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