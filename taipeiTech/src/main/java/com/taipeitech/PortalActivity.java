package com.taipeitech;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.taipeitech.model.Model;
import com.taipeitech.runnable.BaseRunnable;
import com.taipeitech.runnable.LoginNportalRunnable;
import com.taipeitech.utility.Utility;

import java.lang.ref.WeakReference;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URI;

public class PortalActivity extends AppCompatActivity {
    private static final String PORTAL_URL = "https://nportal.ntut.edu.tw/";
    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        setActionBar();
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.clearCache(true);
        webview.clearHistory();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        String account = Model.getInstance().getAccount();
        String password = Model.getInstance().getPassword();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            mProgressDialog = ProgressDialog.show(this, null,
                    getString(R.string.nportal_loggingin));
            Thread loginThread = new Thread(new LoginNportalRunnable(account, password,
                    new LoginHandler(this)));
            loginThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
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

    private static class LoginHandler extends Handler {
        private WeakReference<PortalActivity> mActivityRef = null;

        public LoginHandler(PortalActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PortalActivity activity = mActivityRef.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            WebView webview = (WebView) activity
                    .findViewById(R.id.webview);
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    java.net.CookieStore rawCookieStore = ((java.net.CookieManager)
                            CookieHandler.getDefault()).getCookieStore();
                    if (rawCookieStore != null) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptCookie(true);
                        try {
                            URI uri = new URI(PORTAL_URL);
                            for (HttpCookie cookie : rawCookieStore.get(uri)) {
                                String cookieString = cookie.getName() + "="
                                        + cookie.getValue() + "; domain="
                                        + cookie.getDomain();
                                cookieManager.setCookie(PORTAL_URL + "myPortal.do",
                                        cookieString);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    webview.loadUrl(PORTAL_URL + "myPortal.do");
                    break;
                case BaseRunnable.ERROR:
                    webview.loadUrl(PORTAL_URL);
                    break;
            }
            activity.dismissProgressDialog();
            Toast.makeText(activity, R.string.web_back_hint, Toast.LENGTH_LONG)
                    .show();
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
            actionBar.setTitle(R.string.portal_title);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.deep_darken)));
        }
        Utility.setStatusBarColor(this, getResources().getColor(R.color.deep_darken));
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
