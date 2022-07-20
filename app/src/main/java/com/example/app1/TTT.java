package com.example.app1;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.peach.privacy.api.DisallowMethodInterceptor;
import com.peach.privacy.api.Interceptor;
import com.peach.privacy.api.Interceptors;
import com.peach.privacy.api.InvokeContext;
import com.peach.privacy.api.Utils;


@SuppressLint({"MissingPermission"})
public class TTT {

    public void privacyMethod(Context context, BluetoothLeScanner scanner, TelephonyManager t, LocationManager locationManager,
                              Criteria criteria, PackageManager packageManager, WifiInfo wifiInfo,
                              PendingIntent intent, ContentResolver contentResolver) {
        AccountManager get = AccountManager.get(context);
        get.getAccounts();
        get.getAccountsByType("");

        scanner.startScan(null);
        t.getDeviceId();

        CharSequence did;
        String a = "a";
        String b = "b";
        int line = 111;
        int op = 211;
        String c = "c";
        String d = "d";
        String f = "f";
        boolean e = false;
        boolean intercept = Utils.intercept(a, b, line, op, c, d, f, e);
        if (intercept) {
            did = TextUtils.stringOrSpannedString("");
        } else {
            did = t.getDeviceId();
        }

        t.getImei();
        t.getCellLocation();
        t.getAllCellInfo();

        packageManager.getInstalledApplications(1);
        packageManager.getInstalledPackages(1);


        locationManager.requestLocationUpdates("", 0L, 1F, null, Looper.myLooper());

        wifiInfo.getMacAddress();

        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);

    }
}
