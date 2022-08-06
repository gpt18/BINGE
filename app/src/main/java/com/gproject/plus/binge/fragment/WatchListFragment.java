package com.gproject.plus.binge.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.room.mAdapter;
import com.gproject.plus.binge.room.mDao;
import com.gproject.plus.binge.room.mDatabase;
import com.gproject.plus.binge.room.mEntity;

import java.util.List;


public class WatchListFragment extends Fragment {

   

    public WatchListFragment() {
        // Required empty public constructor
    }




    ImageView imgHeader;
    RecyclerView recView;
    TextView tvNoItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.royal_maroon_color));

        imgHeader = view.findViewById(R.id.imgHeader);
        tvNoItem = view.findViewById(R.id.tvNoItem);

        Glide.with(this).load("https://bit.ly/3NMVr8q").into(imgHeader);

        getRoomData(view);

        return view;
    }

    private void getRoomData(View view) {
        mDatabase db = Room.databaseBuilder(getContext(),
                mDatabase.class, "room_db").allowMainThreadQueries().build();
        mDao moviesDao = db.moviesDao();

        recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recView.setLayoutManager(layoutManager);


        List<mEntity> moviesTable =  moviesDao.getAllMovies();

        if(moviesTable.size()==0){
            tvNoItem.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
        }else{
            tvNoItem.setVisibility(View.GONE);
            recView.setVisibility(View.VISIBLE);
        }

        mAdapter adapter = new mAdapter(moviesTable);
        recView.setAdapter(adapter);



    }
}