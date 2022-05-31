package com.gproject.plus.binge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.Arrays;

public class download extends YouTubeBaseActivity  {
    YouTubePlayerView youTubePlayerView;

    ImageView back, imgMovie;
    TextView tvMovieName, tvDate, tvDes, tvAdmin;
    CardView c_shareBtn, c_downloadBtn;


    private static final String AD_UNIT_ID = "ca-app-pub-8445679544199474/4683072154";
    private static final String TAG = "MyActivity";
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        bannerAds();
        loadAd();


        youTubePlayerView = findViewById(R.id.youtubePlayer);
        back = findViewById(R.id.back);
        imgMovie = findViewById(R.id.imgMovie);
        tvMovieName = findViewById(R.id.tvMovieName);
        tvDate = findViewById(R.id.tvDate);
        tvDes = findViewById(R.id.tvDes);
        tvAdmin = findViewById(R.id.tvAdmin);
        c_shareBtn = findViewById(R.id.c_shareBtn);
        c_downloadBtn = findViewById(R.id.c_downloadBtn);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String img = i.getStringExtra("img");
        String date = i.getStringExtra("date");
        String des = i.getStringExtra("des");
        String link = i.getStringExtra("link");
        String vid = i.getStringExtra("vid");
        String admin = i.getStringExtra("admin");

        SharedPreferences sp = getSharedPreferences("key", MODE_PRIVATE);
        String API_KEY = sp.getString("api_key", "");
        String APP_LINK = sp.getString("app_link", "");

//        YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                youTubePlayer.loadVideo(vid);
//                youTubePlayer.play();
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                Toast.makeText(download.this, "Error: Clear App Data", Toast.LENGTH_SHORT).show();
//            }
//        };

        youTubePlayerView.initialize("SOME KEY",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {

                        youTubePlayer.cueVideo(vid);
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(download.this, "Video load error! Clear App data", Toast.LENGTH_SHORT).show();

                    }
                });
        
//        youTubePlayerView.initialize(API_KEY,listener);

        tvMovieName.setText(name);
        tvDate.setText(date);
        tvDes.setText(des);
        tvAdmin.setText(admin);
        Glide.with(this).load(img).into(imgMovie);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        c_downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    startActivity(intent);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Link load error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        c_downloadBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final ClipData[] myClip = new ClipData[1];
                final ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                myClip[0] = ClipData.newPlainText("text", link);
                clipboard.setPrimaryClip(myClip[0]);
                Toast.makeText(download.this, "Link Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        c_shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd();
                showInterstitial();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String body = "Watch Latest Movies and WebSeries on BINGE+ App for Free. Download Now! "
                    +"\n👉 "+APP_LINK
                    + "\n\n➖➖➖➖➖➖➖➖➖➖➖➖\n" +
                    name
                    + "\n" +
                    des
                    + "\n\n➖➖➖➖➖➖➖➖➖➖➖➖\n" +
                    link;
            String sub = "Sharing Link! Download BINGE+";
            intent.putExtra(Intent.EXTRA_SUBJECT,sub);
            intent.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

        imgMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgDialogFragment cdd = new imgDialogFragment(download.this, img);
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
            });

    }

    private void bannerAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                        .build());

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,
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
            mInterstitialAd.show(this);
        } else {
            Log.i(TAG, "Ad did not load");
        }
    }

    public static class YouTubeBaseActivity extends DialogFragment {

    }
}