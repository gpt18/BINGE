package com.gproject.plus.binge.firebaseDb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.model;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class pagination extends AppCompatActivity {


    RecyclerView recycler_view_user;
    //   LinearLayoutManager manager;    //for linear layout
    NewAdapter adapter;
    String last_key="",last_node="";
    boolean isMaxData=false,isScrolling=false;
    int ITEM_LOAD_COUNT= 13, PAGINATION_ITEM_LOAD_COUNT = 10;
    ProgressBar progressBar;

    int currentitems,tottalitems,scrolledoutitems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagination);
        recycler_view_user= findViewById(R.id.recycler_view_user);
        progressBar= findViewById(R.id.progressBar1);
        getLastKeyFromFirebase(); //43

        int resId = R.anim.grid_layout_animation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recycler_view_user.setLayoutAnimation(animation);

        //   manager=new LinearLayoutManager(getContext());

        GridLayoutManager manager = new GridLayoutManager(this,3);   //for grid layout


        adapter=new NewAdapter(getApplicationContext());

        recycler_view_user.setAdapter(adapter);
        recycler_view_user.setLayoutManager(manager);
        getUsers();

        recycler_view_user.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling=true;

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                currentitems=manager.getChildCount();
                tottalitems=manager.getItemCount();
                scrolledoutitems=manager.findFirstVisibleItemPosition();

                if( isScrolling && currentitems + scrolledoutitems == tottalitems)
                {
                    //  Toast.makeText(getContext(), "fetch data", Toast.LENGTH_SHORT).show();
                    isScrolling=false;
                    //fetch data
                    progressBar.setVisibility(View.VISIBLE);
                    getUsers();

                }

            }
        });

    }

    private void getUsers()
    {
        if(!isMaxData) // 1st fasle
        {
            Query query;

            if (TextUtils.isEmpty(last_node))
                query = FirebaseDatabase.getInstance().getReference()
                        .child("movies")
                        .orderByKey()
                        .limitToFirst(ITEM_LOAD_COUNT);
            else
                query = FirebaseDatabase.getInstance().getReference()
                        .child("movies")
                        .orderByKey()
                        .startAt(last_node)
                        .limitToFirst(PAGINATION_ITEM_LOAD_COUNT);

            query.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChildren())
                    {

                        List<model> itemList = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren())
                        {
                            itemList.add(userSnapshot.getValue(model.class));
                        }

                        last_node =itemList.get(itemList.size()-1).getId();    //10  if it greater than the toatal items set to visible then fetch data from server

                        if(!last_node.equals(last_key))
                            itemList.remove(itemList.size()-1);    // 19,19 so to renove duplicate removeone value
                        else
                            last_node="end";

                         Toast.makeText(getApplicationContext(), "last_node"+last_node, Toast.LENGTH_SHORT).show();

                        adapter.addAll(itemList);
                        adapter.notifyDataSetChanged();


                    }
                    else   //reach to end no further child avaialable to show
                    {
                        isMaxData=true;
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

        }

        else
        {
            progressBar.setVisibility(View.GONE); //if data end
        }
    }

    private void getLastKeyFromFirebase()
    {
        Query getLastKey= FirebaseDatabase.getInstance().getReference()
                .child("movies")
                .orderByKey()
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot lastkey : snapshot.getChildren())
                    last_key=lastkey.getKey();
                //   Toast.makeText(getContext(), "last_key"+last_key, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getApplicationContext(), "can not get last key", Toast.LENGTH_SHORT).show();
            }
        });


    }




}