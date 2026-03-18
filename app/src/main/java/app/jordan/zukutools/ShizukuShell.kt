package app.jordan.zukutools

import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuShell {
    fun exec(command: String): List<String> {
        if (!Shizuku.pingBinder()) return listOf("Shizuku not running")
        val output = mutableListOf<String>()
        try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.forEachLine { output.add(it) }
            process.waitFor()
        } catch (e: Exception) {
            output.add("Error: ${e.message}")
        }
        return output
    }

    fun listFiles(path: String): List<String> = exec("ls -1 \"$path\"")

    fun toggleApp(packageName: String, enable: Boolean) {
        val cmd = if (enable) "pm enable $packageName" else "pm disable-user --user 0 $packageName"
        exec(cmd)
    }

    fun installApk(path: String): String {
        val res = exec("pm install -r -d \"$path\"")
        return if (res.any { it.contains("Success") }) "Success" else "Failed"
    }

    fun reboot(mode: String = "") {
        exec("svc power reboot $mode")
    }
}
