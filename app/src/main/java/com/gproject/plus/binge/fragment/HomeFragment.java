package com.gproject.plus.binge.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

    TextView msgTitle;
    ImageView logo;
    LottieAnimationView inDayAnim;

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
        msgTitle = view.findViewById(R.id.myTitle);
        logo = view.findViewById(R.id.logo);
        inDayAnim = view.findViewById(R.id.inDayAnim);

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

        eventCreator();


        return view;
    }

    private void eventCreator() {
        msgTitle.setSelected(true);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if ( date.equals("2022-08-15") ||  date.equals("2022-08-16")){

            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
//                    msgTitle.setText("Thank you for downloading BINGE+ App");
                }
                // When the task is over it will print 00:00:00 there
                public void onFinish() {
                    logo.setVisibility(View.GONE);
                    inDayAnim.setVisibility(View.VISIBLE);
                    msgTitle.setText("Happy Independence Day");
                }
            }.start();


        }else{
            logo.setVisibility(View.VISIBLE);
            inDayAnim.setVisibility(View.GONE);
            msgTitle.setText("BINGE+ 🍿📽️🎬");
        }

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