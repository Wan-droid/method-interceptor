package com.example.app1

import android.accounts.Account
import android.accounts.AccountManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app1.ui.theme.App1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                    val accountManager = AccountManager.get(this)
                    val androidId = Settings.Secure.getString(null, Settings.Secure.ANDROID_ID)
                    Greeting(
                        telephonyManager.deviceId,
                        telephonyManager.getDeviceId(2),
                        androidId,
                        accountManager.accounts,
                        accountManager.getAccountsByType("byType"),
                        packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES),
                        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    )
                }
            }
        }
    }

}

@Composable
fun Greeting(
    deviceId: String,
    deviceId2: String,
    androidId: String,
    accounts: Array<Account>,
    accountsByType: Array<Account>,
    installedPackages: MutableList<PackageInfo>,
    installedApplications: MutableList<ApplicationInfo>
) {
    Column(verticalArrangement = Arrangement.Center) {
        Text(text = "deviceId :$deviceId", Modifier.padding(4.dp))
        Text(text = "deviceId2:$deviceId2", Modifier.padding(4.dp))
        Text(text = "androidId:$androidId", Modifier.padding(4.dp))
        accounts.forEach {
            Text(text = "getAccounts:${it.name},${it.type}")
        }
        accountsByType.forEach {
            Text(text = "accountsByType:${it.name},${it.type}", Modifier.padding(4.dp))
        }
        installedPackages.forEach {
            Text(text = "installedPackages:${it.packageName},${it.activities},${it.versionName}", Modifier.padding(4.dp))
        }
        installedApplications.forEach {
            Text(text = "installedApplications:${it.packageName},${it.name},${it.processName}", Modifier.padding(4.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App1Theme {
        Greeting(
            "d",
            "d2",
            "androidId",
            arrayOf(),
            arrayOf(),
            mutableListOf(),
            mutableListOf()
        )
    }
}