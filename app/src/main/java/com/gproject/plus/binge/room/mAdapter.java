package com.gproject.plus.binge.room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.download;

import java.util.List;

public class mAdapter extends RecyclerView.Adapter<mAdapter.myViewHolder> {

    Context context;
    List<mEntity> moviesTable;

    public mAdapter(List<mEntity> moviesTable) {
        this.moviesTable = moviesTable;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watch_list,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Glide.with(holder.imgMovie.getContext()).load(moviesTable.get(position).getImg()).into(holder.imgMovie);
        holder.tvMovieName.setText(moviesTable.get(position).getName());
        holder.tvAdmin.setText(moviesTable.get(position).getAdmin());
        holder.tvDate.setText(moviesTable.get(position).getAddTime());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase db = Room.databaseBuilder(holder.tvMovieName.getContext(),
                        mDatabase.class, "room_db").allowMainThreadQueries().build();
                mDao moviesDao = db.moviesDao();

                //delete from room_db
                moviesDao.deleteByKey(moviesTable.get(position).getKey());

                //snack bar
                Snackbar snack = Snackbar.make(v,
                        "Item removed from watchlist...", Snackbar.LENGTH_LONG);
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                        snack.getView().getLayoutParams();
                params.setMargins(20, 20, 20, 250);
                snack.getView().setLayoutParams(params);
                snack.show();


//                Toast.makeText(v.getContext(), "Removing from watchlist...", Toast.LENGTH_SHORT).show();

                //delete form arraylist
                moviesTable.remove(position);

                //update arraylist
                notifyDataSetChanged();
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), download.class);
                i.putExtra("name", moviesTable.get(position).getName());
                i.putExtra("img", moviesTable.get(position).getImg());
                i.putExtra("date", moviesTable.get(position).getDate());
                i.putExtra("des", moviesTable.get(position).getMessage());
                i.putExtra("link", moviesTable.get(position).getLink());
                i.putExtra("vid",moviesTable.get(position).getVid());
                i.putExtra("admin",moviesTable.get(position).getAdmin());
                i.putExtra("id", moviesTable.get(position).getKey());
                v.getContext().startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesTable.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView tvAdmin, tvMovieName, tvDate;
        ImageView  imgMovie, remove;
        CardView c_movie;
        RelativeLayout item;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvDate = itemView.findViewById(R.id.tvDate);
            c_movie = itemView.findViewById(R.id.c_movie);
            remove = itemView.findViewById(R.id.remove);
            item = itemView.findViewById(R.id.item);

        }
    }


}
