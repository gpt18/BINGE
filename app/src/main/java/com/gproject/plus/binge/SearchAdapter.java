package com.gproject.plus.binge;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.myViewHolder> {

    Context context;

    private List<model> itemList;


    public SearchAdapter(@NonNull List<model> itemList, Context context) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setFilteredList(List<model> filteredList){
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


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

                Intent i = new Intent(v.getContext(),download.class);
                i.putExtra("name", itemList.get(position).getName());
                i.putExtra("img", itemList.get(position).getImg());
                i.putExtra("date", itemList.get(position).getDate());
                i.putExtra("des", itemList.get(position).getMessage());
                i.putExtra("link", itemList.get(position).getLink());
                i.putExtra("vid",itemList.get(position).getVid());
                i.putExtra("admin",itemList.get(position).getAdmin());
                i.putExtra("id", itemList.get(position).getId());
                v.getContext().startActivity(i);

//                FirebaseDatabase.getInstance().getReference("movies").child(getRef(position).getKey())
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                id = snapshot.getKey();
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });


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

//    private DatabaseReference getRef(int position) {
//
//        return key;
//    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_new,parent, false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvAdmin,tvMovieName;
        ImageView  imgMovie;
        CardView c_movie;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            c_movie = itemView.findViewById(R.id.c_movie);
        }
    }



}