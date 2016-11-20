package com.taipeitech.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.taipeitech.BaseFragment;
import com.taipeitech.MainActivity;
import com.taipeitech.MainApplication;
import com.taipeitech.R;
import com.taipeitech.model.Model;
import com.taipeitech.utility.PermissionRequestListener;
import com.taipeitech.utility.Utility;
import com.taipeitech.utility.WifiUtility;
import com.taipeitech.wifi.LoginService.LocalBinder;

import java.util.ArrayList;

public class WifiFragment extends BaseFragment implements ServiceConnection,
        OnClickListener, PermissionRequestListener {
    private static View fragmentView;
    private BroadcastReceiver wifiReceiver;
    private Context activityContext = null;
    private boolean isScan = false;
    private LoginService loginService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_wifi, container, false);
        View button = fragmentView.findViewById(R.id.refresh_button);
        button.setOnClickListener(this);
        button = fragmentView.findViewById(R.id.login_button);
        button.setOnClickListener(this);
        initWifi();
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityContext = getActivity();
    }

    public void scanWifi() {
        if (!checkPermission()) {
            if (activityContext instanceof MainActivity) {
                ((MainActivity) activityContext).requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, this);
            }
            return;
        }
        if (!WifiUtility.isWifiOpen(MainApplication.getInstance())) {
            Builder checkDialog = new AlertDialog.Builder(activityContext);
            checkDialog.setTitle("提示");
            checkDialog.setMessage("尚未開啟Wi-Fi，請問是否要開啟？");
            checkDialog.setPositiveButton("開啟",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!isScan) {
                                isScan = true;
                                WifiUtility.openWifi(MainApplication
                                        .getInstance());
                                WifiUtility.startScan(activityContext,
                                        wifiReceiver);
                            }
                        }
                    });
            checkDialog.setNegativeButton(R.string.back, null);
            checkDialog.show();
        } else {
            if (!isScan) {
                isScan = true;
                WifiUtility.startScan(activityContext, wifiReceiver);
            }
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void initWifi() {
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ListView wifi_list = (ListView) fragmentView
                        .findViewById(R.id.wifi_listview);
                ArrayList<String> apList = WifiUtility
                        .getAvailableAPList(getActivity());
                if (apList.size() > 0) {
                    wifi_list.setAdapter(new ArrayAdapter<>(
                            getActivity(), android.R.layout.simple_list_item_1,
                            apList));
                }
                if (WifiUtility.isNtutccAround) {
                    Utility.showNotification(getActivity(), "Ntutcc自動登入",
                            "嘗試連線到ntutcc！", true);
                    WifiUtility.connectToNtutcc(context);
                } else {
                    Utility.showNotification(getActivity(), "Ntutcc自動登入",
                            "未偵測到ntutcc！", true);
                }
                isScan = false;
                try {
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.unregisterReceiver(wifiReceiver);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private boolean startService() {
        String account = Model.getInstance().getAccount();
        String password = Model.getInstance().getPassword();
        if (account != null && account.length() > 0 && password != null
                && password.length() > 0) {
            Intent intent = new Intent(getActivity(), LoginService.class);
            intent.putExtra("account", account);
            intent.putExtra("password", password);
            getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
            return true;
        } else {
            showAlertMessage("請先至帳號設定，設定校園入口網站帳號密碼！");
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String account = Model.getInstance().getAccount();
        String password = Model.getInstance().getPassword();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            startService();
        }
    }

    @Override
    public void onStop() {
        try {
            getActivity().unbindService(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LocalBinder binder = (LocalBinder) service;
        loginService = binder.getService();
        loginService.setWifiFragment(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(getActivity(), "Ntutcc自動連線已關閉！", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClick(View v) {
        if(!Utility.checkAccount(getActivity())) {
            return;
        }
        int id = v.getId();
        boolean result = startService();
        switch (id) {
            case R.id.refresh_button:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_wifi))
                        .setAction(getString(R.string.analytics_action_refresh))
                        .setLabel(getString(R.string.analytics_label_click))
                        .build());
                if (result) {
                    WifiUtility.testCount = 0;
                    scanWifi();
                }
                break;
            case R.id.login_button:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_wifi))
                        .setAction(getString(R.string.analytics_action_login))
                        .setLabel(getString(R.string.analytics_label_click))
                        .build());
                if (result) {
                    try {
                        String ssid = WifiUtility.getCurrentSSID(activityContext);
                        if (ssid != null && ssid.contains("ntutcc")) {
                            loginService.LoginNtutcc();
                        } else {
                            Toast.makeText(getActivity(), "僅提供ntutcc認證登入！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        scanWifi();
                    }
                }
                break;
        }
    }

    @Override
    public int getTitleColorId() {
        return R.color.dark_red;
    }

    @Override
    public int getTitleStringId() {
        return R.string.wifi_text;
    }

    @Override
    public void onRequestPermissionsResult(String permission, int result) {
        if (isDetached()) {
            return;
        }
        if (TextUtils.equals(permission, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                scanWifi();
            } else {
                Toast.makeText(activityContext, "未取得權限，無法提供此功能", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
