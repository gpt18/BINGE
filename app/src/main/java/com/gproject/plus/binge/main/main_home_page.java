package com.gproject.plus.binge.main;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.gproject.plus.binge.MainActivity;
import com.gproject.plus.binge.R;
import com.gproject.plus.binge.fragment.HomeFragment;
import com.gproject.plus.binge.fragment.SearchFragment;
import com.gproject.plus.binge.fragment.WatchListFragment;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class main_home_page extends AppCompatActivity {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_page);
//        this line hide actionbar
//        getSupportActionBar().hide();
//        this line hide status bar
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

                      break;

                  case R.id.nav_search:
                      fragment = new SearchFragment();
                      getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                      break;

                  case R.id.nav_watchlist:
                      fragment = new WatchListFragment();
                      getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                      break;

                  case R.id.nav_more:
                      moreDialog();
                      break;

                  default:
                      Toast.makeText(main_home_page.this, "Action not assign", Toast.LENGTH_SHORT).show();
              }

              return true;
          }
      });
    }

    private void moreDialog() {
        final DialogPlus dialogPlus = DialogPlus.newDialog(main_home_page.this)
                .setContentHolder(new ViewHolder(R.layout.dialog_more))
                .setExpanded(true, 1100)
                .create();

        View myView = dialogPlus.getHolderView();

        ImageView imgTelegram = myView.findViewById(R.id.imgTelegram);
        ImageView imgRequest = myView.findViewById(R.id.imgRequest);
        TextView version = myView.findViewById(R.id.version);
        LinearLayout telegram = myView.findViewById(R.id.telegram);
        LinearLayout request = myView.findViewById(R.id.request);
        LinearLayout share = myView.findViewById(R.id.share);

        Glide.with(main_home_page.this).load("https://i.ibb.co/YPK0gHb/bing-admin.jpg").into(imgRequest);
        Glide.with(main_home_page.this)
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

    private String getCurrentVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}