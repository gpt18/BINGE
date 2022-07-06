package com.gproject.plus.binge;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.room.Room;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class download extends YouTubeBaseActivity  {
    YouTubePlayerView youTubePlayerView;

    ImageView back, imgMovie, imgWL;
    TextView tvMovieName, tvDate, tvDes, tvAdmin, tvViews, tvWl;
    CardView c_shareBtn, c_downloadBtn;
    LinearLayout watchList;

    mDatabase db;



    private static final String AD_UNIT_ID = "ca-app-pub-8445679544199474/4683072154";
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

        SharedPreferences sp = getSharedPreferences("key", MODE_PRIVATE);
        String API_KEY = sp.getString("api_key", "");
        String APP_LINK = sp.getString("app_link", "");
        String downloadCount = sp.getString("download", "");



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
        
        //youTubePlayerView.initialize(API_KEY,listener);

        tvMovieName.setText(name);
        tvDate.setText(date);
        tvDes.setText(des);
        tvAdmin.setText(admin);
        Glide.with(this).load(img).into(imgMovie);

        views(id);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        c_downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(id).child("views").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
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
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
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

        watchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase db = Room.databaseBuilder(getApplicationContext(),
                        mDatabase.class, "room_db").allowMainThreadQueries().build();
                mDao moviesDao = db.moviesDao();
                int check = moviesDao.isDataExist(id);


                if (check == 0) {

                    String timeStamp = new SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()).format(new Date());
                    // data not exist.
                    moviesDao.insertRecord(new
                            mEntity(0, id, date, admin, name, img, des, link, vid, tvViews.getText().toString(), timeStamp));


                    tvWl.setText("Watchlist Added");
                    imgWL.setImageResource(R.drawable.ic_baseline_bookmark_added_24);
                    Toast.makeText(download.this, "Adding to watchlist...", Toast.LENGTH_SHORT).show();

                } else {
                    deleteDialog(id, name);
                }

            }


        });



        watchListCheck(id);

    }

    private void deleteDialog(String key, String name) {
        AlertDialog dialog = new AlertDialog.Builder(download.this)
                .setTitle("Remove from watchlist")
                .setMessage("Are you sure you want to remove: "+name+" from watchlist?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        mDatabase db = Room.databaseBuilder(getApplicationContext(),
                                mDatabase.class, "room_db").allowMainThreadQueries().build();
                        mDao moviesDao = db.moviesDao();

                        //delete from room_db
                        moviesDao.deleteByKey(key);

                        tvWl.setText("Add to watchlist");
                        imgWL.setImageResource(R.drawable.ic_baseline_bookmark_add_24);
                        Toast.makeText(download.this, "Removing from watchlist...", Toast.LENGTH_SHORT).show();

                    }


                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void watchListCheck(String id) {
        mDatabase db = Room.databaseBuilder(getApplicationContext(),
                mDatabase.class, "room_db").allowMainThreadQueries().build();
        mDao moviesDao = db.moviesDao();
        int check = moviesDao.isDataExist(id);

        if (check == 0) {
            // data not exist.
            tvWl.setText("Add to Watch list");
            imgWL.setImageResource(R.drawable.ic_baseline_bookmark_add_24);

        } else {
            // data exist.
            tvWl.setText("Remove from watchlist");
            imgWL.setImageResource(R.drawable.ic_baseline_bookmark_added_24);

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
    }

    private void views(String id) {

        databaseReference.child(id).child("views").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
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
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }else {
                        databaseReference.child(id).child("views").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String views = (String) dataSnapshot.getValue();
                                String download =  views + " Downloads";
                                tvViews.setText(download);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        showInterstitial();
    }


}