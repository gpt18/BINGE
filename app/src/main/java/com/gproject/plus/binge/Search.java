package com.gproject.plus.binge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Search extends AppCompatActivity {

    ImageView imageView;
    SearchView searchView;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<model> options;
    SearchAdapter SearchAdapter;

    TextView not_found;

    private  List<model> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imageView = findViewById(R.id.imageView4);
        recyclerView = findViewById(R.id.recyclerView);
        not_found = findViewById(R.id.not_found);
        searchView = findViewById(R.id.searchView);
        searchView.requestFocus();

        //init
        itemList = new ArrayList<>();


        Glide.with(this)
                .load(R.drawable.not_found_box)
                .into(imageView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(null);

        databaseReference = FirebaseDatabase.getInstance().getReference("movies");

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


        SearchAdapter = new SearchAdapter(itemList, getApplicationContext());


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


    }

    private void filterList(String text) {
        List<model> filteredList = new ArrayList<>();
        String notFound = "No results for: "+text;
        for (model item : itemList){
            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        if(text.equals("")){
            recyclerView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            not_found.setVisibility(View.VISIBLE);
            not_found.setText("Ready to Search... 🔍");

        }else{
            if (filteredList.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                not_found.setVisibility(View.VISIBLE);
                not_found.setText(notFound);
            }
            else {
                imageView.setVisibility(View.GONE);
                not_found.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(SearchAdapter);
                SearchAdapter.setFilteredList(filteredList);

            }
        }


    }

}