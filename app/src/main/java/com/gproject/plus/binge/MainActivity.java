package com.gproject.plus.binge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    adapter adapter, adapter1;
    DatabaseReference databaseReference;
    RecyclerView recyclerView, recyclerView1;
    TextView tvUsername, tvShortName, tvShortDate, tvShortViews;
    MaterialToolbar toolbar;
    RelativeLayout shortName, shortDate, shortViews;
    LinearLayout svSort;
    ImageView imgSort;
    String sortChild = "Name";

    FirebaseRemoteConfig remoteConfig;

    ShimmerFrameLayout shimmer1, shimmer2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hooks();
        checkForUpdate();
        admobInit();
        loading();


        toolbar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.more:
                        moreDialog();
                        break;

                    case R.id.watchList:
                        Intent i = new Intent(MainActivity.this, watchList.class);
                        startActivity(i);
                        break;

                    case R.id.search:
                        startActivity(new Intent(MainActivity.this, Search.class));
                        break;

                }

                return true;
            }

        });

        databaseReference = FirebaseDatabase.getInstance().getReference("movies");
        databaseReference.keepSynced(true);

        //--------------Header------------------//

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView1.setLayoutManager(layoutManager);
        recyclerView1.setItemAnimator(null);

        FirebaseRecyclerOptions<model> options1
                = new FirebaseRecyclerOptions.Builder<model>()
                .setQuery(databaseReference.limitToLast(10), model.class)
                .build();

        //----------------Header-----------------//


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(null);


        FirebaseRecyclerOptions<model> options
                = new FirebaseRecyclerOptions.Builder<model>()
                .setQuery(databaseReference.orderByChild("name").limitToFirst(51), model.class)
                .build();


        adapter = new adapter(options, getApplicationContext());
        adapter1 = new adapter(options1, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView1.setAdapter(adapter1);


        shortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortChild = "name";
                sortColor();
                shortName.setBackgroundColor(getResources().getColor(R.color.teal_700));
                tvShortName.setTextColor(getResources().getColor(R.color.white));
                FirebaseRecyclerOptions<model> options
                        = new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(databaseReference.orderByChild("name").limitToFirst(51), model.class)
                        .build();

                adapter = new adapter(options, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.startListening();

            }
        });

        shortDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortChild = "date";
                sortColor();
                shortDate.setBackgroundColor(getResources().getColor(R.color.teal_700));
                tvShortDate.setTextColor(getResources().getColor(R.color.white));
                FirebaseRecyclerOptions<model> options
                        = new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(databaseReference.orderByChild("date").limitToFirst(51), model.class)
                        .build();

                adapter = new adapter(options, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.startListening();

            }
        });

        shortViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortChild = "views";
                sortColor();
                shortViews.setBackgroundColor(getResources().getColor(R.color.teal_700));
                tvShortViews.setTextColor(getResources().getColor(R.color.white));
                FirebaseRecyclerOptions<model> options
                        = new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(databaseReference.orderByChild("views").limitToFirst(51), model.class)
                        .build();

                adapter = new adapter(options, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.startListening();

            }
        });

        imgSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseRecyclerOptions<model> options
                        = new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(databaseReference.orderByChild(sortChild).limitToLast(51), model.class)
                        .build();

                adapter = new adapter(options, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.startListening();

            }
        });



    }

    public void sortColor(){
        tvShortName.setTextColor(getResources().getColor(R.color.teal_200));
        tvShortDate.setTextColor(getResources().getColor(R.color.teal_200));
        tvShortViews.setTextColor(getResources().getColor(R.color.teal_200));
        shortName.setBackgroundColor(getResources().getColor(R.color.black));
        shortDate.setBackgroundColor(getResources().getColor(R.color.black));
        shortViews.setBackgroundColor(getResources().getColor(R.color.black));

    }

    private void userCurrentStatus(String state){

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseDatabase.getInstance().getReference().child("users").child(android_id).setValue(state);

    }

    private void loading() {

        new CountDownTimer(4000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                shimmer1.startShimmer();
                shimmer2.startShimmer();
                String subTitle = "⏳ Fetching all items for you...";
                tvUsername.setText(subTitle);
            }

            @Override
            public void onFinish() {
                getMovieCount();
                shimmer1.stopShimmer();
                shimmer2.stopShimmer();
                shimmer1.setVisibility(View.GONE);
                shimmer2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView1.setVisibility(View.VISIBLE);
                svSort.setVisibility(View.VISIBLE);
            }
        }.start();

    }

    private void admobInit() {

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

    private void hooks() {

        tvUsername = findViewById(R.id.tvUsername);
        toolbar = findViewById(R.id.topAppBar);
        recyclerView1 = findViewById(R.id.recyclerView1);
        recyclerView = findViewById(R.id.recyclerView);
        shimmer1 = findViewById(R.id.shimmer1);
        shimmer2 = findViewById(R.id.shimmer2);
        shortName = findViewById(R.id.shortName);
        shortDate = findViewById(R.id.shortDate);
        shortViews = findViewById(R.id.shortViews);
        tvShortName = findViewById(R.id.tvShortName);
        tvShortDate = findViewById(R.id.tvShortDate);
        tvShortViews = findViewById(R.id.tvShortViews);
        svSort = findViewById(R.id.svSort);
        imgSort = findViewById(R.id.imgSort);

    }

    private void moreDialog() {
        final DialogPlus dialogPlus = DialogPlus.newDialog(MainActivity.this)
                .setContentHolder(new ViewHolder(R.layout.dialog_more))
                .setExpanded(true, 900)
                .create();

        View myView = dialogPlus.getHolderView();

        ImageView imgTelegram = myView.findViewById(R.id.imgTelegram);
        ImageView imgRequest = myView.findViewById(R.id.imgRequest);
        TextView version = myView.findViewById(R.id.version);
        LinearLayout telegram = myView.findViewById(R.id.telegram);
        LinearLayout request = myView.findViewById(R.id.request);
        LinearLayout share = myView.findViewById(R.id.share);

        Glide.with(MainActivity.this).load("https://i.ibb.co/YPK0gHb/bing-admin.jpg").into(imgRequest);
        Glide.with(MainActivity.this)
                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Telegram_2019_Logo.svg/182px-Telegram_2019_Logo.svg.png")
                .into(imgTelegram);

        version.setText("Version: " + getCurrentVersionName());

        dialogPlus.show();

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://t.me/+B8DH6ow_6OE4NWNl")));
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://t.me/bingerequest")));
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sp = getSharedPreferences("key", MODE_PRIVATE);
                String APP_LINK = sp.getString("app_link", "");

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String body = "Watch Latest Movies and WebSeries on BINGE+ App for Free. Download Now! "
                        +"\n👉 "+APP_LINK
                        +"\n\n"+"Telegram channel"+"\n"+"Find latest films and TV shows right here. https://t.me/+B8DH6ow_6OE4NWNl"
                        +"\n\n"+"Telegram group"+"\n"+"Request films and web series here. https://t.me/bingerequest";
                String sub = "Sharing Link! Download BINGE+";
                intent.putExtra(Intent.EXTRA_SUBJECT,sub);
                intent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

    }

    private void getMovieCount() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childCount = (int) dataSnapshot.getChildrenCount();
                String subTitle =  "Total Items: "+ childCount;
                tvUsername.setText(subTitle);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter1.startListening();

        userCurrentStatus("online");

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter1.stopListening();
        userCurrentStatus("offline");
    }

    @Override
    protected void onDestroy() {
        userCurrentStatus("offline");
        super.onDestroy();

    }

    //****************************Checking In-AppUpdate using Firebase************************//

    private void checkForUpdate(){

        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);


        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {

                    final double app_version = getCurrentVersionCode();
                    Log.d("current value: ", String.valueOf(app_version));

                    String api_key = remoteConfig.getString("api_key");
                    String appLink = remoteConfig.getString("appLink");
                    SharedPreferences sp = getSharedPreferences("key", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("api_key", api_key);
                    editor.putString("app_link", appLink);
                    editor.apply();

                    final double new_version_code = remoteConfig.getDouble("versionCode");
                    Log.d("final value: ", String.valueOf(new_version_code));

                    if (new_version_code > app_version) {

                        showUpdateDialog();
                    }
                }
            }
        });
    }

    private void showUpdateDialog() {
        String app_version = getCurrentVersionName();
        Log.d("version name: ", getCurrentVersionName());
        String latest_version_name = remoteConfig.getString("versionName");
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Please Update the App")
                .setMessage("A new version of this app is available. Please update it.\n"
                        + "Current Version: "+ app_version
                        + "\nNew Version: "+ latest_version_name)
                .setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            String download_link  = remoteConfig.getString("appLink");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(download_link));
                            startActivity(intent);
                            finish();

                        }
                        catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Something Went wrong Try again  ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setCancelable(false).show();
    }

    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getCurrentVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //****************************Checking In-AppUpdate using Firebase************************//


}