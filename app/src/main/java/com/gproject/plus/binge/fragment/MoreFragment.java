package com.gproject.plus.binge.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gproject.plus.binge.R;

public class MoreFragment extends Fragment {


    public MoreFragment() {
        // Required empty public constructor
    }


    TextView deviceId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_more, container, false);

//        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.poison_dart_frog_color));


        ImageView imgTelegram = view.findViewById(R.id.imgTelegram);
        ImageView imgRequest = view.findViewById(R.id.imgRequest);
        TextView version = view.findViewById(R.id.version);
        LinearLayout telegram = view.findViewById(R.id.telegram);
        LinearLayout request = view.findViewById(R.id.request);
        LinearLayout share = view.findViewById(R.id.share);
        deviceId = view.findViewById(R.id.deviceId);



        Glide.with(MoreFragment.this).load("https://i.ibb.co/YPK0gHb/bing-admin.jpg").into(imgRequest);
        Glide.with(MoreFragment.this)
                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Telegram_2019_Logo.svg/182px-Telegram_2019_Logo.svg.png")
                .into(imgTelegram);

        version.setText("Version: " + getCurrentVersionName());


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

        SharedPreferences sp = this.getActivity().getSharedPreferences("key", Context.MODE_PRIVATE);
        String devId = sp.getString("deviceId", "");
        deviceId.setText(devId);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String APP_LINK = sp.getString("app_link", "");
                Log.e("applink",APP_LINK);

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


        boolean adsIn = sp.getBoolean("adsIn", false);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        final SharedPreferences.Editor editor = sp.edit();
        if(sp.contains("adsIn") && adsIn) {
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);

        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("adsIn", checkBox.isChecked());
                editor.apply();
            }
        });

        return view;
    }

    private String getCurrentVersionName() {
        try {
            return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}