package app.jordan.zukutools

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import dev.rikka.shizuku.Shizuku
import app.jordan.zukutools.ui.DashboardScreen

class MainActivity : ComponentActivity() {

    private val SHIZUKU_CODE = 1001
    var hasPermission by mutableStateOf(false)

    private val binderListener = Shizuku.OnBinderReceivedListener { checkPermission() }
    private val resultListener = Shizuku.OnRequestPermissionResultListener { code, result ->
        if (code == SHIZUKU_CODE) {
            hasPermission = result == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Shizuku.addBinderReceivedListener(binderListener)
        Shizuku.addRequestPermissionResultListener(resultListener)

        checkPermission()

        setContent {
            DashboardScreen(
                isAuthorized = hasPermission,
                onIdentifyClick = { Shizuku.requestPermission(SHIZUKU_CODE) }
            )
        }
    }

    private fun checkPermission() {
        if (Shizuku.pingBinder()) {
            hasPermission = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderListener)
        Shizuku.removeRequestPermissionResultListener(resultListener)
    }
}
