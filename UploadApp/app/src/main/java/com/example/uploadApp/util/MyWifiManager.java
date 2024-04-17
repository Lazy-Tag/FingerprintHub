package com.example.uploadApp.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyWifiManager {
    private final WifiManager wifiManager;
    public MyWifiManager(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public List<JSONObject> getResult() {
        List<JSONObject> result = new ArrayList<>();
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        for (ScanResult scanResult : scanResults) {
            String bssid = scanResult.BSSID;
            int rssi = scanResult.level;
            try {
                JSONObject wifi = new JSONObject();
                wifi.put("Mac address", bssid);
                wifi.put("RSSI", rssi);
                result.add(wifi);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
