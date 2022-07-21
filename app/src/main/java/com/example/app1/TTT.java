package com.example.app1;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;


@SuppressLint({"MissingPermission"})
public class TTT {

    @SuppressLint({"HardwareIds", "QueryPermissionsNeeded"})
    public void privacyMethod(Context context, BluetoothLeScanner scanner, TelephonyManager t, LocationManager locationManager,
                              Criteria criteria, PackageManager packageManager, WifiInfo wifiInfo,
                              PendingIntent intent, ContentResolver contentResolver) {
        AccountManager get = AccountManager.get(context);
        Account[] accounts = get.getAccounts();
        Account[] accountsByType = get.getAccountsByType("");
        if (scanner != null) {
            scanner.startScan(null);
        }
        String deviceId = t.getDeviceId();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String deviceId1 = t.getDeviceId(2);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String imei = t.getImei();
        }
        CellLocation cellLocation = t.getCellLocation();
        List<CellInfo> allCellInfo = t.getAllCellInfo();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(1);
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(1);
//        locationManager.requestLocationUpdates("", 0L, 1F, null, Looper.myLooper());
//        String macAddress = wifiInfo.getMacAddress();
        String string = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);

        Log.i("qwe", "qwe");

    }
}
