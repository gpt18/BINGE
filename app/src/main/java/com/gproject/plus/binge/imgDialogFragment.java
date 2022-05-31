package com.gproject.plus.binge;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class imgDialogFragment extends Dialog {

    public Activity c;
    public Dialog d;
    public String bImg;

  public imgDialogFragment(Activity a, String img) {
    super(a);
    this.c = a;
    this.bImg = img;
  }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog1_img);
        ImageView imageView = findViewById(R.id.bigImg);
        Glide.with(getContext()).load(bImg).into(imageView);
    }
    //    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//         super.onCreateView(inflater, container, savedInstanceState);
//         return inflater.inflate(R.layout.dialog1_img, container,false);
//
//    }
//
//    public void show(FragmentManager fragmentManager, String my_dialog) {
//    }
}
