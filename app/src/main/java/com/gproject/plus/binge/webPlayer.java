package com.gproject.plus.binge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import im.delight.android.webview.AdvancedWebView;

public class webPlayer extends AppCompatActivity {

    AdvancedWebView mWebView;
    SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_player);

        mWebView = findViewById(R.id.webview);
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);

        mWebView.setMixedContentAllowed(false);

        Intent i = getIntent();
        String url = i.getStringExtra("url");
        startWebView(url);

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
                        startWebView(url);
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

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
                Toast.makeText(webPlayer.this, "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });

        mWebView.setWebChromeClient(new MyChrome());
        mWebView.loadUrl(url);
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
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
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