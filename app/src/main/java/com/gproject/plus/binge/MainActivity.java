package com.gproject.plus.binge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;
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
import android.widget.ProgressBar;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.gproject.plus.binge.firebaseDb.NewAdapter;
import com.gproject.plus.binge.firebaseDb.pagination;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    adapter adapter1;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("movies");
    RecyclerView recyclerView, recyclerView1;
    TextView tvUsername, tvShortName, tvShortDate, tvShortViews;
    MaterialToolbar toolbar;
    RelativeLayout shortName, shortDate, shortViews;
    LinearLayout svSort;
    ImageView imgSort;
    String sortChild = "Name";

    FirebaseRemoteConfig remoteConfig;

    ShimmerFrameLayout shimmer1, shimmer2;

    //for pagination
    NestedScrollView nestedScrollView;
    //   LinearLayoutManager manager;    //for linear layout
    NewAdapter adapter;
    String last_key="",last_node="";
    boolean isMaxData=false,isScrolling=false;
    int ITEM_LOAD_COUNT= 13, PAGINATION_ITEM_LOAD_COUNT = 10;
    ProgressBar progressBar;
    int currentitems,tottalitems,scrolledoutitems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hooks();
        checkForUpdate();
        admobInit();
        getMovieCount();


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

        adapter1 = new adapter(options1, getApplicationContext());
        recyclerView1.setAdapter(adapter1);

        //----------------Header-----------------//

        recyclerView.setNestedScrollingEnabled(false);


        progressBar= findViewById(R.id.progressBar1);
        getLastKeyFromFirebase(); //43


        GridLayoutManager manager = new GridLayoutManager(this,3);   //for grid layout


        adapter =new NewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        getUsers();


        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                View view = (View)nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    // your pagination code

                    if (!isScrolling) {


                        isScrolling = true;

                        //code to fetch more data for endless scrolling
                        currentitems=manager.getChildCount();
                        tottalitems=manager.getItemCount();
                        scrolledoutitems=manager.findFirstVisibleItemPosition();

                        if( currentitems + scrolledoutitems == tottalitems)
                        {
                            //  Toast.makeText(getContext(), "fetch data", Toast.LENGTH_SHORT).show();

                            //fetch data
                            progressBar.setVisibility(View.VISIBLE);
                            getUsers();

                        }
                    }
                }
            }
        });

//        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (v.getChildAt(v.getChildCount() - 1) != null) {
//                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                        scrollY > oldScrollY) {
//                    //code to fetch more data for endless scrolling
//
//
//                    if (!isScrolling) {
//
//
//                        isScrolling = true;
//
//                        //code to fetch more data for endless scrolling
//                        currentitems=manager.getChildCount();
//                        tottalitems=manager.getItemCount();
//                        scrolledoutitems=manager.findFirstVisibleItemPosition();
//
//                        if( currentitems + scrolledoutitems == tottalitems)
//                        {
//                            //  Toast.makeText(getContext(), "fetch data", Toast.LENGTH_SHORT).show();
//
//                            //fetch data
//                            progressBar.setVisibility(View.VISIBLE);
//                            getUsers();
//
//                        }
//                    }
//
//                }
//
//            }
//        });


    }

    private void getUsers()
    {
        if(!isMaxData) // 1st fasle
        {
            Query query;

            if (TextUtils.isEmpty(last_node))
                query = FirebaseDatabase.getInstance().getReference()
                        .child("movies")
                        .orderByKey()
                        .limitToFirst(ITEM_LOAD_COUNT);
            else
                query = FirebaseDatabase.getInstance().getReference()
                        .child("movies")
                        .orderByKey()
                        .startAt(last_node)
                        .limitToFirst(PAGINATION_ITEM_LOAD_COUNT);

            query.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChildren())
                    {

                        List<model> itemList = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren())
                        {
                            itemList.add(userSnapshot.getValue(model.class));
                        }

                        last_node =itemList.get(itemList.size()-1).getId();    //10  if it greater than the toatal items set to visible then fetch data from server

                        if(!last_node.equals(last_key))
                            itemList.remove(itemList.size()-1);    // 19,19 so to renove duplicate removeone value
                        else
                            last_node="end";

                        // Toast.makeText(getContext(), "last_node"+last_node, Toast.LENGTH_SHORT).show();

                        adapter.addAll(itemList);
                        adapter.notifyDataSetChanged();

                        isScrolling = false;


                    }
                    else   //reach to end no further child avaialable to show
                    {
                        isMaxData=true;
                        Toast.makeText(MainActivity.this, "You are at the last", Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

        }

        else
        {
            progressBar.setVisibility(View.GONE); //if data end
        }
    }

    private void getLastKeyFromFirebase()
    {
        Query getLastKey= FirebaseDatabase.getInstance().getReference()
                .child("movies")
                .orderByKey()
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot lastkey : snapshot.getChildren())
                    last_key=lastkey.getKey();
                //   Toast.makeText(getContext(), "last_key"+last_key, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getApplicationContext(), "can not get last key", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void userCurrentStatus(String state){

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseDatabase.getInstance().getReference().child("users").child(android_id).setValue(state);

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
        nestedScrollView = findViewById(R.id.nestedScrollView);

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

        shimmer1.startShimmer();
        shimmer2.startShimmer();
        String subTitle = "⏳ Fetching all items for you...";
        tvUsername.setText(subTitle);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childCount = (int) dataSnapshot.getChildrenCount();
                String subTitle =  "Total Items: "+ childCount;
                tvUsername.setText(subTitle);
                shimmer1.stopShimmer();
                shimmer2.stopShimmer();
                shimmer1.setVisibility(View.GONE);
                shimmer2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView1.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter1.startListening();

        userCurrentStatus("online");

    }

    @Override
    public void onStop() {
        super.onStop();
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