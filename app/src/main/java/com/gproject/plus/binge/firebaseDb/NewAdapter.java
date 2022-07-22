package com.gproject.plus.binge.firebaseDb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.SearchAdapter;
import com.gproject.plus.binge.download;
import com.gproject.plus.binge.imgDialogFragment;
import com.gproject.plus.binge.model;

import java.util.ArrayList;
import java.util.List;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.myViewHolder>
{
    List<model> itemList;
    Context context;

    public NewAdapter( Context context)
    {
        this.itemList = new ArrayList<>();
        this.context = context;
    }

    public void addAll(List<model> newmodel)
    {
        int initsize=itemList.size();
        itemList.addAll(newmodel);
        notifyItemRangeChanged(initsize,newmodel.size());
    }




    public static class myViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvAdmin,tvMovieName;
        ImageView imgMovie;
        CardView c_movie;

        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            c_movie = itemView.findViewById(R.id.c_movie);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_new,parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position)
    {
        holder.tvMovieName.setText(itemList.get(position).getName());

        if (itemList.get(position).getImg()==null){
            holder.imgMovie.setImageResource(R.mipmap.ic_logo_round);

        }else {
            Glide.with(holder.imgMovie.getContext()).load(itemList.get(position).getImg()).into(holder.imgMovie);
        }


        holder.imgMovie.setOnClickListener(new View.OnClickListener() {
            String id;

            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), download.class);
                i.putExtra("name", itemList.get(position).getName());
                i.putExtra("img", itemList.get(position).getImg());
                i.putExtra("date", itemList.get(position).getDate());
                i.putExtra("des", itemList.get(position).getMessage());
                i.putExtra("link", itemList.get(position).getLink());
                i.putExtra("vid",itemList.get(position).getVid());
                i.putExtra("admin",itemList.get(position).getAdmin());
                i.putExtra("id", itemList.get(position).getId());
                v.getContext().startActivity(i);
            }
        });

        holder.imgMovie.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imgDialogFragment cdd = new imgDialogFragment((Activity) context, itemList.get(position).getImg());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return itemList.size();
    }


}
