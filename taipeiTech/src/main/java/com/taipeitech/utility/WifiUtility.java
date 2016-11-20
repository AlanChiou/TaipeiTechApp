package com.taipeitech.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WifiUtility {

    public static int MAX_TEST_COUNT = 10;
    public static int testCount = 0;
    public static boolean isNtutccAround = false;
    private static final String NTUTCC_SSID = "ntutcc";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isWifiOpen(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        int state = wifiManager.getWifiState();
        boolean flag = false;
        if (state == WifiManager.WIFI_STATE_ENABLED
                || state == WifiManager.WIFI_STATE_ENABLING) {
            flag = true;
        }
        return flag;
    }

    public static boolean isConnectedOrConnecting(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = activeNetInfo != null
                && activeNetInfo.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected = activeNetInfo != null
                && activeNetInfo.isConnected();
        return isConnected;
    }

    public static boolean openWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (!isWifiOpen(context)) {
            wifiManager.setWifiEnabled(true);
        }
        return isWifiOpen(context);
    }

    public static void regStateReceiver(Context context,
                                        BroadcastReceiver reciever) {
        context.registerReceiver(reciever, new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    public static boolean startScan(Context context, BroadcastReceiver reciever) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(reciever, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        boolean result = wifiManager.startScan();
        return result;
    }

    public static ArrayList<String> getAvailableAPList(Context context) {
        ArrayList<String> apList = new ArrayList<String>();
        try {
            isNtutccAround = false;
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult result : scanResults) {
                apList.add(result.SSID + ":" + result.level + "dBm");
                if (result.SSID.equals("ntutcc")) {
                    isNtutccAround = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apList;
    }

    public static int getWifiLevel(Context context, String BSSID) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
            for (ScanResult result : scanResults) {
                if (result.BSSID.equals(BSSID)) {
                    return result.level;
                }
            }
        } catch (Exception e) {
        }
        return -100;
    }

    public static boolean connectToNtutcc(Context context) {
        testCount++;
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + NTUTCC_SSID + "\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiManager.addNetwork(conf);
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + NTUTCC_SSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static String getCurrentSSID(Context context) throws Exception {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getSSID();
        } catch (Exception e) {
            throw new Exception("目前WIFI連線名稱讀取時發生錯誤");
        }

    }
}
