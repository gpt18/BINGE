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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainHomePage extends AppCompatActivity {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_page);
//        this line hide actionbar
//        getSupportActionBar().hide();
//        this line hide status bar
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkForUpdate();

        navigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        navigationView.setSelectedItemId(R.id.nav_home);


      navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              Fragment fragment = null;
              switch (item.getItemId()){
                  case R.id.nav_home:
                      fragment = new HomeFragment();
                      getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                      navigationView.setBackgroundResource(R.drawable.bg_round_nav_1);
                      break;

                  case R.id.nav_search:
                      fragment = new SearchFragment();
                      getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                      navigationView.setBackgroundResource(R.drawable.bg_round_nav_2);
                      break;

                  case R.id.nav_watchlist:
                      fragment = new WatchListFragment();
                      getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                      navigationView.setBackgroundResource(R.drawable.bg_round_nav_3);
                      break;

                  case R.id.nav_more:
                      fragment = new MoreFragment();
                      getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                      navigationView.setBackgroundResource(R.drawable.bg_round_nav_4);
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