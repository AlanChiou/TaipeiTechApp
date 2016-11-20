package com.taipeitech.wifi;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.taipeitech.runnable.BaseRunnable;
import com.taipeitech.runnable.LoginNtutccRunnable;
import com.taipeitech.runnable.LoginNtutccWay1Runnable;
import com.taipeitech.runnable.LoginNtutccWay2Runnable;
import com.taipeitech.utility.Utility;
import com.taipeitech.utility.WifiUtility;

public class LoginService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private WifiFragment mFragment;
    private BroadcastReceiver stateReceiver;
    private boolean isLogin = false;
    private String account;
    private String password;

    @Override
    public IBinder onBind(Intent intent) {
        account = intent.getStringExtra("account");
        password = intent.getStringExtra("password");
        stateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (WifiUtility.isWifiOpen(context)) {
                        if (WifiUtility.isConnected(context)) {
                            String currentSSID = WifiUtility
                                    .getCurrentSSID(context);
                            if (currentSSID.contains("ntutcc")) {
                                if (!isLogin) {
                                    LoginNtutcc();
                                }
                            } else {
                                Utility.showNotification(
                                        getApplicationContext(), "Ntutcc自動登入",
                                        currentSSID + "已連線！", true);
                                isLogin = false;
                            }
                        } else {
                            isLogin = false;
                        }
                    } else {
                        isLogin = false;
                    }
                } catch (Exception e) {
                    isLogin = false;
                }
            }
        };

        WifiUtility.regStateReceiver(this, stateReceiver);
        return mBinder;
    }

    public void LoginNtutcc() {
        Thread t = new Thread(new LoginNtutccRunnable(loginHandler));
        t.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(stateReceiver);
        return super.onUnbind(intent);
    }

    private Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof String) {
                        String result = (String) msg.obj;
                        if (result.contains("www.google.com")) {
                            Utility.showNotification(getApplicationContext(),
                                    "Ntutcc自動登入", "認證成功，ntutcc已可以上網！", true);
                            isLogin = true;
                        } else if (result
                                .contains("captiveportal-login.ntut.edu.tw")) {
                            Thread t = new Thread(new LoginNtutccWay1Runnable(
                                    login1Handler, account, password));
                            t.start();
                        } else if (result.contains("externalGuestRedirect.html")) {
                            Thread t = new Thread(new LoginNtutccWay2Runnable(
                                    login1Handler, result, account, password));
                            t.start();
                        } else {
                            Utility.showNotification(getApplicationContext(),
                                    "Ntutcc自動登入", "此為未定義轉址網域！" + " " + result, true);
                            isLogin = false;
                        }
                    }
                    break;
                case BaseRunnable.ERROR:
                    isLogin = false;
                    if (mFragment != null) {
                        mFragment.scanWifi();
                    }
                    break;
            }
        }
    };

    private Handler login1Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    Thread t = new Thread(new LoginNtutccRunnable(checkHandler));
                    t.start();
                case BaseRunnable.ERROR:
                    isLogin = false;
                    if (mFragment != null) {
                        mFragment.scanWifi();
                    }
                    break;
            }
        }
    };

    private Handler checkHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseRunnable.REFRESH:
                    if (msg.obj instanceof String) {
                        String result = (String) msg.obj;
                        if (result.contains("www.google.com")) {
                            Utility.showNotification(getApplicationContext(),
                                    "Ntutcc自動登入", "認證成功，ntutcc已可以上網！", true);
                            isLogin = false;
                        } else {
                            Thread t = new Thread(new LoginNtutccRunnable(
                                    loginHandler));
                            t.start();
                        }
                    }
                    break;
                case BaseRunnable.ERROR:
                    isLogin = false;
                    if (mFragment != null) {
                        mFragment.scanWifi();
                    }
                    break;
            }
        }
    };

    public void setWifiFragment(WifiFragment wifiFragment) {
        this.mFragment = wifiFragment;
    }

    public class LocalBinder extends Binder {
        LoginService getService() {
            return LoginService.this;
        }
    }
}
