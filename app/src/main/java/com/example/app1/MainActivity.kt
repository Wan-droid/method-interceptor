package com.example.app1

import android.accounts.AccountManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)

        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val accountManager = AccountManager.get(this)
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val sb = StringBuilder()
            .append("deviceID:${telephonyManager.deviceId}").append("\n")
            .append("androidId:${androidId}").append("\n")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.append("deviceID1:${telephonyManager.getDeviceId(1)}").append("\n")
        }

        val accounts = accountManager.accounts
        accounts.forEach { sb.append("getAccounts:${it.name},${it.type}\n") }
        val accountsByType = accountManager.getAccountsByType("byType")
        accountsByType.forEach { sb.append("accountsByType:${it.name},${it.type}\n") }
        val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        installedPackages.forEach { sb.append("installedPackages:${it.packageName},${it.activities},${it.versionName}\n") }
        val installedApplications =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        installedApplications.forEach { sb.append("getInstalledApplications:${it.packageName},${it.name},${it.packageName}\n") }

        textView.text = sb.toString()
    }

}