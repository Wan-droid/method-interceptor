package intercept.android.content.pm

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import com.example.app1.MainActivity

class PackageManager {

    companion object {
        @JvmStatic
        fun getInstalledApplications(packageManager: android.content.pm.PackageManager, flags: Int): MutableList<ApplicationInfo> {
            val i = ApplicationInfo()
            i.packageName = "a.b.c.d.e"
            i.name = "abcde"
            i.processName = "a.b.c.d.e.process"
            return mutableListOf(i)

        }

        @JvmStatic
        fun getInstalledPackages(packageManager: android.content.pm.PackageManager, flags: Int): MutableList<PackageInfo> {
            val i = PackageInfo()
            i.packageName = "1.2.3.4.5"
            i.activities = arrayOf(ActivityInfo().apply { name = MainActivity::class.java.canonicalName })
            i.versionName = "version name"
            return mutableListOf(i)
        }
    }
}