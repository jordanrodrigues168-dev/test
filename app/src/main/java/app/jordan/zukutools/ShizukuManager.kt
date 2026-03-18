package app.jordan.zukutools

import android.content.pm.IPackageManager
import android.os.Build
import dev.rikka.shizuku.ShizukuBinderWrapper
import dev.rikka.shizuku.SystemServiceHelper

object ShizukuManager {

    /**
     * Uses Shizuku to disable (debloat) a package.
     * State 2 = COMPONENT_ENABLED_STATE_DISABLED
     */
    fun disableApp(packageName: String): Boolean {
        return try {
            val binder = ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package"))
            val ipm = IPackageManager.Stub.asInterface(binder)
            
            // setApplicationEnabledSetting(packageName, newState, flags, userId, callingPackage)
            ipm.setApplicationEnabledSetting(packageName, 2, 0, 0, "shell")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Example of a "Tweak": Changing a system setting via shell
     * Requires "settings" service or running a raw exec
     */
    fun applySystemTweak(command: String) {
        try {
            Shizuku.newProcess(arrayOf("sh", "-c", command), null, null).waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
