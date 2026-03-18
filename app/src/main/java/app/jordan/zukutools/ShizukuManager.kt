package app.jordan.zukutools

import android.os.IBinder
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

object ShizukuManager {

    fun isAvailable(): Boolean {
        return Shizuku.pingBinder()
    }

    // Function called by DashboardScreen.kt to "debloat"
    fun disableApp(packageName: String) {
        if (!isAvailable()) return
        try {
            // This is where the magic happens later:
            // pm disable-user --user 0 <package>
            Runtime.getRuntime().exec("pm disable-user --user 0 $packageName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function called by DashboardScreen.kt for tweaks
    fun applySystemTweak(tweakName: String) {
        if (!isAvailable()) return
        // Tweak logic will go here
    }

    // Safely get the Package Manager via Shizuku
    fun getPackageManagerBinder(): IBinder? {
        return if (isAvailable()) {
            val binder = SystemServiceHelper.getSystemService("package")
            ShizukuBinderWrapper(binder)
        } else {
            null
        }
    }
}
