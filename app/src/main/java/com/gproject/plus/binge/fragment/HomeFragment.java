package com.gproject.plus.binge.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.main.AdapterMovies;
import com.gproject.plus.binge.model;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView1;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("movies");


    TextView tvTotal;

    BottomNavigationView bottom_navigation;
    LinearLayout homeHeader;
    Toolbar toolbar;

    ShimmerFrameLayout shimmer;
    TextView tvFailed;

    AdapterMovies adapterMovies;

    private List<model> itemList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //getting item count in the arraylist
        getTotalItems();

        //hooker for all the view
        recyclerView1 = view.findViewById(R.id.rcHomeFragment);
        tvTotal = view.findViewById(R.id.tvTotal);
        bottom_navigation = view.findViewById(R.id.bottom_navigation);
        homeHeader = view.findViewById(R.id.homeHeader);
        toolbar = view.findViewById(R.id.homeToolbar);
        shimmer = view.findViewById(R.id.shimmer);
        tvFailed = view.findViewById(R.id.tvFailed);

        //set loading visibility
        startupComponent();

        //init arraylist
        itemList = new ArrayList<>();

        //setting layout manager to the recyclerview
        GridLayoutManager gridLayout = new GridLayoutManager(getContext(),3);   //for grid layout
        recyclerView1.setLayoutManager(gridLayout);


        //recycler view scroll event
/*
        recyclerView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ) {
                    toolbar.setSubtitle("Scroll Up");

                } else if (dy < 0 && tvTotal.isShown()) {
                    toolbar.setSubtitle("Scroll down");

                }


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
//                toolbar.setSubtitle("state change");
            }
        });*/



        return view;
    }

    private void startupComponent() {
        recyclerView1.setVisibility(View.GONE);
        tvFailed.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
    }

    private void getTotalItems() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                tvFailed.setVisibility(View.GONE);
                shimmer.setVisibility(View.GONE);
                recyclerView1.setVisibility(View.VISIBLE);

                model item = snapshot.getValue(model.class);
                itemList.add(0, item);

                setTotalItem();

                adapterMovies = new AdapterMovies(itemList, getContext());
                recyclerView1.setAdapter(adapterMovies);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                /*itemList.remove(snapshot.getValue(model.class));
                adapterMovies.notifyDataSetChanged();
                recyclerView1.setAdapter(adapterMovies);*/

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        for (DataSnapshot data : snapshot.getChildren()){
                            model item = data.getValue(model.class);
                            itemList.add(0, item);

                        }
                        adapterMovies = new AdapterMovies(itemList, getContext());
                        recyclerView1.setAdapter(adapterMovies);
                        setTotalItem();
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

    }

    private void setTotalItem() {
        int childCount = (int) itemList.size();
        String countText =  "Total: "+ childCount;
        tvTotal.setText(countText);
    }


}