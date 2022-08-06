package com.gproject.plus.binge;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class imgDialogFragment extends Dialog {

    public Activity c;
    public Dialog d;
    public String bImg;
    public String posterTitle;

  public imgDialogFragment(Activity a, String img, String title) {
    super(a);
    this.c = a;
    this.bImg = img;
    this.posterTitle = title;
  }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_img);
        ImageView imageView = findViewById(R.id.bigImg);
        TextView tvTitle = findViewById(R.id.tvPosterTitle);

        Glide.with(getContext()).load(bImg).into(imageView);
        tvTitle.setText(posterTitle);
        tvTitle.setSelected(true);


    }
}
