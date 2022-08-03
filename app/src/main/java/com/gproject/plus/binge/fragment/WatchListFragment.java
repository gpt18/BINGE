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

import com.bumptech.glide.Glide;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.room.mAdapter;
import com.gproject.plus.binge.room.mDao;
import com.gproject.plus.binge.room.mDatabase;
import com.gproject.plus.binge.room.mEntity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WatchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WatchListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WatchListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WatchListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WatchListFragment newInstance(String param1, String param2) {
        WatchListFragment fragment = new WatchListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ImageView imgHeader;
    RecyclerView recView;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.royal_maroon_color));

        imgHeader = view.findViewById(R.id.imgHeader);

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

        mAdapter adapter = new mAdapter(moviesTable);
        recView.setAdapter(adapter);



    }
}