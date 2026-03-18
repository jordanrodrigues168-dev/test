package app.jordan.zukutools

import rikka.shizuku.Shizuku
import android.content.Context

object ShizukuManager {

    fun isAvailable(): Boolean = Shizuku.pingBinder()

    // --- THE DEBLOAT MODULES ---
    val debloatModules = mapOf(
        "Google Core" to listOf("com.google.android.videos", "com.google.android.music", "com.google.android.apps.docs"),
        "Samsung/Bloat" to listOf("com.samsung.android.bixby.agent", "com.samsung.android.spay", "com.facebook.services"),
        "Xiaomi/Adware" to listOf("com.miui.msa.global", "com.xiaomi.mipicks", "com.miui.analytics"),
        "General Tracking" to listOf("com.google.android.gms.location.history")
    )

    fun runDebloatModule(moduleName: String) {
        val packages = debloatModules[moduleName] ?: return
        packages.forEach { pkg ->
            executeShell("pm disable-user --user 0 $pkg")
        }
    }

    // --- SYSTEM TWEAKS (The "Settings" Module) ---
    fun applyTweak(action: String) {
        val command = when(action) {
            "HIDDEN_SETTINGS" -> "am start -n com.android.settings/.Settings\\\$UsageAccessSettingsActivity"
            "BATTERY_SAVER_AGGR" -> "settings put global low_power_trigger_level 20"
            "STRICT_DOZE" -> "device_config put device_idle light_after_inactive_to 30000"
            "IGNORE_RECOVERY" -> "settings put global setup_wizard_has_run 1"
            else -> ""
        }
        if (command.isNotEmpty()) executeShell(command)
    }

    private fun executeShell(command: String) {
        if (!isAvailable()) return
        try {
            Shizuku.newProcess(arrayOf("sh", "-c", command), null, null).waitFor()
        } catch (e: Exception) { e.printStackTrace() }
    }
}
