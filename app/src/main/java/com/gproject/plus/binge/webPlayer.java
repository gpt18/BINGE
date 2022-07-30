package com.gproject.plus.binge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;

import im.delight.android.webview.AdvancedWebView;

public class webPlayer extends AppCompatActivity {

    AdvancedWebView mWebView;
    SwipeRefreshLayout mySwipeRefreshLayout;
    TextView tvUrl, tvError;
    ImageView errorImg, img_help, img_back;
    LinearLayout llError, llWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_player);

        mWebView = findViewById(R.id.webView);
        tvUrl = findViewById(R.id.tvUrl);
        errorImg = findViewById(R.id.errorImg);
        tvError = findViewById(R.id.tvError);
        llError = findViewById(R.id.llError);
        llWebview = findViewById(R.id.llWebView);
        img_help = findViewById(R.id.img_help);
        img_back = findViewById(R.id.img_back);
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);

        tvUrl.setSelected(true);

        Glide.with(this)
                .load(R.drawable.error)
                .into(errorImg);

        mWebView.setMixedContentAllowed(false);



        Intent i = getIntent();
        String url = i.getStringExtra("url");
        String title = i.getStringExtra("title");

        startWebView(url);
        tvUrl.setText(title);

        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mWebView.clearHistory();
                        mWebView.clearCache(true);
                        startWebView(url);
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        img_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog_help = new Dialog(webPlayer.this);
                dialog_help.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_help.setCancelable(false);
                dialog_help.setContentView(R.layout.custom_dialog_help);
                dialog_help.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView ok = dialog_help.findViewById(R.id.ok);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_help.dismiss();
                    }
                });

                dialog_help.show();

            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void startWebView(String url) {

        WebSettings settings = mWebView.getSettings();


        //Make sure No cookies are created
        CookieManager.getInstance().setAcceptCookie(false);

        //Make sure no caching is done
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);

        //Make sure no autofill for Forms/ user-name password happens for the app
        settings.setSaveFormData(false);

        ProgressDialog progressDialog = new ProgressDialog(webPlayer.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                url = mWebView.getUrl();
                trimurl(url);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {


                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                llWebview.setVisibility(View.GONE);
                llError.setVisibility(View.VISIBLE);
                tvError.setText("Error: "+description);
                Toast.makeText(webPlayer.this, "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });

        mWebView.setWebChromeClient(new MyChrome());
        mWebView.loadUrl(url);
    }

    private void trimurl(String url) {

        if(url.contains("drive.google.com/uc?id=")){

            llWebview.setVisibility(View.GONE);
            llError.setVisibility(View.VISIBLE);
            ViewDialog alertDialoge = new ViewDialog();
            alertDialoge.showDialog(this, "PUT DIALOG TITLE");


        }else{
            llError.setVisibility(View.GONE);
            llWebview.setVisibility(View.VISIBLE);
        }
    }

    public class ViewDialog {

        public void showDialog(Activity activity, String msg) {

            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            CardView download = (CardView) dialog.findViewById(R.id.btnDownload);
            CardView play = (CardView) dialog.findViewById(R.id.btnPlay);
            ImageView close = dialog.findViewById(R.id.close);


            play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Perfome Action

                    String url1 = mWebView.getUrl();
                    String s1 = url1.substring(url1.indexOf("=") + 1).trim();
                    String[] s2 = s1.split("&");
                    String playerUrl = "https://gdriveplayer.us/embed2.php?link=https://drive.google.com/file/d/"+s2[0];
                    mWebView.getSettings().setSupportMultipleWindows(true);
                    mWebView.loadUrl(playerUrl);
                    dialog.dismiss();

                }
            });
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    mWebView.getSettings().setSupportMultipleWindows(false);
                    llError.setVisibility(View.GONE);
                    llWebview.setVisibility(View.VISIBLE);
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWebView.getSettings().setSupportMultipleWindows(false);
                    dialog.dismiss();
                    llError.setVisibility(View.GONE);
                    llWebview.setVisibility(View.VISIBLE);

                }
            });

            dialog.show();

        }
    }


    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}


        public void onHideCustomView()
        {

            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }

            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        super.onDestroy();
    }
}