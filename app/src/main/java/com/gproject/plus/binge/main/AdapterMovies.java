package com.gproject.plus.binge.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gproject.plus.binge.R;

import com.gproject.plus.binge.download;
import com.gproject.plus.binge.imgDialogFragment;
import com.gproject.plus.binge.model;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMovies extends RecyclerView.Adapter<AdapterMovies.myViewHolder> {
    Context context;
    private final List<model> itemList;

    public AdapterMovies( @NonNull List<model> itemList, Context context) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_new,parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.tvMovieName.setText(itemList.get(position).getName());

        if (itemList.get(position).getImg()==null){
            holder.imgMovie.setImageResource(R.mipmap.ic_logo_round);

        }else {
            Glide.with(holder.imgMovie.getContext()).load(itemList.get(position).getImg()).into(holder.imgMovie);
        }

        /*holder.PbPosterLoading.setVisibility(View.VISIBLE);
        Picasso.with(holder.imgMovie.getContext()).load(itemList.get(position).getImg())
                .into(holder.imgMovie, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.PbPosterLoading.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError() {
                        holder.PbPosterLoading.setVisibility(View.VISIBLE);
                    }
                })
        ;
        */


        holder.imgMovie.setOnClickListener(new View.OnClickListener() {

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
                imgDialogFragment cdd = new imgDialogFragment((Activity) context, itemList.get(position).getImg(), itemList.get(position).getName());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvMovieName;
        ImageView imgMovie;
        ProgressBar PbPosterLoading;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            PbPosterLoading = itemView.findViewById(R.id.PbPosterLoading);
        }
    }
}
