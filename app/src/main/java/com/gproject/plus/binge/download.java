package com.gproject.plus.binge;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gproject.plus.binge.room.mDao;
import com.gproject.plus.binge.room.mDatabase;
import com.gproject.plus.binge.room.mEntity;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class download extends YouTubeBaseActivity  {
    YouTubePlayerView youTubePlayerView;

    ImageView back, imgMovie, imgWL;
    TextView tvMovieName, tvDate, tvDes, tvAdmin, tvViews, title;
    CheckBox tvWl;
    CardView c_shareBtn, c_downloadBtn, c_openDrive, watchList;

    private static final String TAG = "MyActivity";
    private InterstitialAd mInterstitialAd;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("movies");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        bannerAds();
        loadAd();

        hooker();

        tvViews.setSelected(true);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String img = i.getStringExtra("img");
        String date = i.getStringExtra("date");
        String des = i.getStringExtra("des");
        String link = i.getStringExtra("link");
        String vid = i.getStringExtra("vid");
        String admin = i.getStringExtra("admin");
        String id = i.getStringExtra("id");

        //setup SP
        SharedPreferences sp = getSharedPreferences("key", MODE_PRIVATE);
        String APP_LINK = sp.getString("app_link", "");

        //Setup Room Db
        mDatabase db = Room.databaseBuilder(getApplicationContext(),
                mDatabase.class, "room_db").allowMainThreadQueries().build();
        mDao moviesDao = db.moviesDao();

        //checking watchlist
        watchListCheck(id, moviesDao);

        youTubePlayerView.initialize("SOME KEY", new YouTubePlayer.OnInitializedListener() {
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


        tvMovieName.setText(name);
        tvDate.setText(date);
        description_check(des);
        tvAdmin.setText(admin);
        Glide.with(this).load(img).into(imgMovie);
        views(id);


        sp_wl_checkbox(sp);


        tvWl.setOnClickListener(v -> {
            if (!tvWl.isChecked()){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("watchlist_check_box", "false");
                editor.apply();

            }else{
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("watchlist_check_box", "true");
                editor.apply();

            }
        });

        c_openDrive.setOnClickListener(v -> {

           if (tvWl.isChecked()){
               int check = moviesDao.isDataExist(id);
               if (check == 0) {

                   watchList_add_operation(name, img, date, des, link, vid, admin, id, moviesDao);
                   Toast.makeText(download.this, "Adding to watchlist...", Toast.LENGTH_SHORT).show();

               }
           }

            databaseReference.child(id).child("views").get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String views = String.valueOf(task.getResult().getValue());
                    Integer add = Integer.parseInt(views)+1;
                    String update = String.valueOf(add);
                    Map<String,Object> map = new HashMap<>();
                    map.put("views",update);
                    FirebaseDatabase.getInstance().getReference().child("movies")
                            .child(id).updateChildren(map)
                            .addOnSuccessListener(unused -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                }
            });


            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Link load error", Toast.LENGTH_SHORT).show();
            }

        });

        c_downloadBtn.setOnClickListener(v -> {
            try {

                Intent intentWeb = new Intent(download.this, webPlayer.class);
                intentWeb.putExtra("url", link);
                intentWeb.putExtra("title", name);
                startActivity(intentWeb);
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Link load error", Toast.LENGTH_SHORT).show();
            }
        });

        c_shareBtn.setOnClickListener(v -> {
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
        });

        imgMovie.setOnClickListener(v -> {

            imgDialogFragment cdd = new imgDialogFragment(download.this, img, name);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        });

        watchList.setOnClickListener(v -> {

            int check = moviesDao.isDataExist(id);


            if (check == 0) {

                watchList_add_operation(name, img, date, des, link, vid, admin, id, moviesDao);

            } else {

                watchList_delete_operation(id, moviesDao);
            }

        });

        back.setOnClickListener(v -> onBackPressed());

    }

    private void sp_wl_checkbox(SharedPreferences sp) {

        String wl_check = sp.getString("watchlist_check_box", "");

        if(wl_check.isEmpty()){
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("watchlist_check_box", "true");
            editor.apply();
        }else{
            tvWl.setChecked(!wl_check.equals("false"));
        }
    }

    private void description_check(String des) {

        if (des.isEmpty()){
            tvDes.setText(R.string.description_not_available);

        }
        else {
            tvDes.setText(des);
        }
    }

    private void watchList_add_operation(String name, String img, String date, String des, String link, String vid, String admin, String id, mDao moviesDao) {

        String timeStamp = new SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()).format(new Date());
        // data not exist.
        moviesDao.insertRecord(new
                mEntity(0, id, date, admin, name, img, des, link, vid, tvViews.getText().toString(), timeStamp));

        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                title.setText(R.string.adding_to_watchlist);
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                new CountDownTimer(2000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // Used for formatting digit to be in 2 digits only
                        title.setText(R.string.watchlist_added);
                    }
                    // When the task is over it will print 00:00:00 there
                    public void onFinish() {
                        title.setText(R.string.post_details);
                    }
                }.start();
            }
        }.start();



        imgWL.setImageResource(R.drawable.ic_baseline_favorite_24);
    }


    private void watchList_delete_operation(String id, mDao moviesDao) {

        //delete from room_db
        moviesDao.deleteByKey(id);

        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                title.setText(R.string.removing_from_watchlist);
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                new CountDownTimer(2000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // Used for formatting digit to be in 2 digits only
                        title.setText(R.string.remove_successfully);
                    }
                    // When the task is over it will print 00:00:00 there
                    public void onFinish() {
                        title.setText(R.string.post_details);
                    }
                }.start();
            }
        }.start();

        imgWL.setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }



    private void watchListCheck(String id, mDao moviesDao) {

        int check = moviesDao.isDataExist(id);

        if (check == 0) {
            // data not exist.
            imgWL.setImageResource(R.drawable.ic_baseline_favorite_border_24);

        } else {
            // data exist.

            imgWL.setImageResource(R.drawable.ic_baseline_favorite_24);

        }
    }


    private void hooker() {
        youTubePlayerView = findViewById(R.id.youtubePlayer);
        back = findViewById(R.id.back);
        imgMovie = findViewById(R.id.imgMovie);
        tvMovieName = findViewById(R.id.tvMovieName);
        tvDate = findViewById(R.id.tvDate);
        tvDes = findViewById(R.id.tvDes);
        tvAdmin = findViewById(R.id.tvAdmin);
        c_shareBtn = findViewById(R.id.c_shareBtn);
        c_downloadBtn = findViewById(R.id.c_downloadBtn);
        tvViews = findViewById(R.id.tvViews);
        watchList = findViewById(R.id.watchList);
        tvWl = findViewById(R.id.tvWl);
        imgWL = findViewById(R.id.imgWL);
        c_openDrive = findViewById(R.id.c_openDrive);
        title = findViewById(R.id.title);
    }

    private void views(String id) {

        databaseReference.child(id).child("views").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                String views = String.valueOf(task.getResult().getValue());
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                if (views.equals("null")){
                    Map<String,Object> map = new HashMap<>();
                    map.put("views","0");
                    FirebaseDatabase.getInstance().getReference().child("movies")
                            .child(id).updateChildren(map)
                            .addOnSuccessListener(unused -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                }else {
                    databaseReference.child(id).child("views").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String views = (String) dataSnapshot.getValue();
                            String download =  views + " Views";
                            tvViews.setText(download);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }

    private void bannerAds() {
        MobileAds.initialize(this, initializationStatus -> {});

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("ABIDE012345"))
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        showInterstitial();
    }


}