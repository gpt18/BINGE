package com.gproject.plus.binge.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.SearchAdapter;
import com.gproject.plus.binge.main.AdapterMovies;
import com.gproject.plus.binge.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }


    LottieAnimationView imageView;
    SearchView searchView;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<model> options;
    com.gproject.plus.binge.SearchAdapter SearchAdapter;

    TextView not_found, tvInfo;

    private List<model> itemList;

    AdView adView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

//       getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.teal_blue_color));


        imageView = view.findViewById(R.id.imageView4);
        recyclerView = view.findViewById(R.id.recyclerView);
        not_found = view.findViewById(R.id.not_found);
        tvInfo = view.findViewById(R.id.tvInfo);
        searchView = view.findViewById(R.id.searchView);
        adView = view.findViewById(R.id.adView);


        //init
        itemList = new ArrayList<>();

        //banner Ads
        bannerAds();



        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(null);

        databaseReference = FirebaseDatabase.getInstance().getReference("movies");


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {



                model item = snapshot.getValue(model.class);
                itemList.add(0, item);

                SearchAdapter = new SearchAdapter(itemList, getContext());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        for (DataSnapshot data : snapshot.getChildren()){
                            model item = data.getValue(model.class);
                            itemList.add(0, item);
                        }
                        SearchAdapter = new SearchAdapter(itemList, getContext());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        SearchAdapter = new SearchAdapter(itemList, getContext());

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


        return view;


    }

    private void filterList(String text) {
        List<model> filteredList = new ArrayList<>();
        String notFound = "No results for: "+text;

        for (model item : itemList){
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getMessage().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        if(text.equals("")){
            recyclerView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            not_found.setVisibility(View.VISIBLE);
            adView.setVisibility(View.VISIBLE);
            not_found.setText("Ready to Search... 🔍");

        }else{
            if (filteredList.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                not_found.setVisibility(View.VISIBLE);
                adView.setVisibility(View.VISIBLE);
                not_found.setText(notFound);
            }
            else {
                imageView.setVisibility(View.GONE);
                not_found.setVisibility(View.GONE);
                tvInfo.setVisibility(View.GONE);
                adView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(SearchAdapter);
                SearchAdapter.setFilteredList(filteredList);

            }
        }


    }

    private void bannerAds() {
        MobileAds.initialize(getContext(), initializationStatus -> {});

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("ABIDE012345"))
                        .build());

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

}