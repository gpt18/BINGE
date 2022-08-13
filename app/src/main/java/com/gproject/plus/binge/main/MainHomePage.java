package com.gproject.plus.binge.main;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.fragment.HomeFragment;
import com.gproject.plus.binge.fragment.MoreFragment;
import com.gproject.plus.binge.fragment.SearchFragment;
import com.gproject.plus.binge.fragment.WatchListFragment;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


public class MainHomePage extends AppCompatActivity {

    BottomNavigationView navigationView;

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new SearchFragment();
    final Fragment fragment3 = new WatchListFragment();
    final Fragment fragment4 = new MoreFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_page);
        checkForUpdate();
/*        this line hide actionbar
        getSupportActionBar().hide();
        this line hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        fm.beginTransaction().add(R.id.body_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.body_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.body_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.body_container, fragment1, "1").commit();


        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.nav_home);



      navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch (item.getItemId()){
                  case R.id.nav_home:
                     fm.beginTransaction().hide(active).show(fragment1).commit();
                     active = fragment1;
                     break;

                  case R.id.nav_search:
                      fm.beginTransaction().hide(active).show(fragment2).commit();
                      active = fragment2;
                      break;

                  case R.id.nav_watchlist:
                      fm.beginTransaction().hide(active).show(fragment3).commit();
                      active = fragment3;
                      break;

                  case R.id.nav_more:
                      fm.beginTransaction().hide(active).show(fragment4).commit();
                      active = fragment4;
                      break;

                  default:
                      Toast.makeText(MainHomePage.this, "Action not assign", Toast.LENGTH_SHORT).show();
              }

              return true;
          }
      });
    }

    private void userCurrentStatus(String state){

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences sp = getSharedPreferences("key", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("deviceId", android_id);
        editor.apply();

        FirebaseDatabase.getInstance().getReference().child("users").child(android_id).setValue(state);

    }

    @Override
    protected void onStart() {
        super.onStart();
        userCurrentStatus("1");

    }

    @Override
    protected void onStop() {
        super.onStop();
        userCurrentStatus("0");

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //****************************Checking In-AppUpdate using Firebase************************//

    FirebaseRemoteConfig remoteConfig;

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
                    Log.d("app link value: ", String.valueOf(appLink));

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