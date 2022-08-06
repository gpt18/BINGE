package com.gproject.plus.binge.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gproject.plus.binge.MainActivity;
import com.gproject.plus.binge.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance(String param1, String param2) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_more, container, false);

        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.poison_dart_frog_color));


        ImageView imgTelegram = view.findViewById(R.id.imgTelegram);
        ImageView imgRequest = view.findViewById(R.id.imgRequest);
        TextView version = view.findViewById(R.id.version);
        LinearLayout telegram = view.findViewById(R.id.telegram);
        LinearLayout request = view.findViewById(R.id.request);
        LinearLayout share = view.findViewById(R.id.share);

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