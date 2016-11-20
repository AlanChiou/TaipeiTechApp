package com.taipeitech;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.taipeitech.utility.Utility;

public class DonateActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        setActionBar();
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("file:///android_asset/donate.html");
        CookieManager.getInstance().setAcceptCookie(true);
        Toast.makeText(this, R.string.web_back_hint, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        WebView webview = (WebView) findViewById(R.id.webview);
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            finish();
        }
    }

    public void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            actionBar.setTitle(R.string.donate_text);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.deep_darken)));
        }
        Utility.setStatusBarColor(this, getResources().getColor(R.color.deep_darken));
    }

}
