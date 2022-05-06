package com.gproject.plus.binge;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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

public class adapter extends FirebaseRecyclerAdapter<model, adapter.myViewHolder> {

    Context context;
    private static final String AD_UNIT_ID = "ca-app-pub-8445679544199474/4683072154";
    private static final String TAG = "MyActivity";
    private InterstitialAd mInterstitialAd;

    public adapter(@NonNull FirebaseRecyclerOptions<model> options, Context context) {
        super(options);
        this.context = context;
    }

    public adapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull model model) {


        holder.tvAdmin.setText(model.getAdmin());
        holder.tvMovieName.setText(model.getName());
        holder.tvMessage.setText(model.getMessage());
        holder.tvDate.setText(model.getDate());
        holder.tvLink.setText(model.getButton());

        if (model.getImg()==null){
            holder.imgMovie.setImageResource(R.mipmap.ic_logo_round);

        }else {
            Glide.with(holder.imgMovie.getContext()).load(model.getImg()).into(holder.imgMovie);
        }


        holder.tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd();
                showInterstitial();
                try {

                    String download_link  = model.getLink();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(download_link));
                    v.getContext().startActivity(intent);
                }
                catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), "Link load error", Toast.LENGTH_SHORT).show();
                }


            }
        });


        holder.tvLink.setOnLongClickListener(new View.OnLongClickListener() {

            final ClipData[] myClip = new ClipData[1];
            final ClipboardManager clipboard = (ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);

            @Override
            public boolean onLongClick(View v) {
                myClip[0] = ClipData.newPlainText("text", model.getLink());
                clipboard.setPrimaryClip(myClip[0]);
                Toast.makeText(context, "Link Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        holder.imgShareBtn.setOnClickListener(v -> {
            loadAd();
            showInterstitial();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String body = "Watch Latest Movies and WebSeries on BINGE+ App for Free. Download Now! "
                    + "\n\n➖➖➖➖➖➖➖➖➖➖➖➖\n" +
                    model.getName()
                    + "\n" +
                    model.getMessage()
                    + "\n\n➖➖➖➖➖➖➖➖➖➖➖➖\n" +
                    model.getLink();
            String sub = "Sharing Link! Download BINGE+";
            intent.putExtra(Intent.EXTRA_SUBJECT,sub);
            intent.putExtra(Intent.EXTRA_TEXT,body);
            v.getContext().startActivity(Intent.createChooser(intent, "Share Using"));

        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent, false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvAdmin,tvMovieName, tvMessage, tvDate, tvLink;
        ImageView  imgMovie, imgShareBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgShareBtn = itemView.findViewById(R.id.imgShareBtn);
            tvLink = itemView.findViewById(R.id.tvLink);
        }
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context,
                "ca-app-pub-8445679544199474/4683072154",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);
        } else {
            Log.i(TAG, "Ad did not load");
        }
    }

}
