package app.jordan.zukutools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.ui.platform.LocalContext
import app.jordan.zukutools.ui.DashboardScreen
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            MaterialTheme(colorScheme = dynamicDarkColorScheme(context)) {
                DashboardScreen(
                    isAuthorized = Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED,
                    onAuthorize = { if (Shizuku.pingBinder()) Shizuku.requestPermission(100) }
                )
            }
        }
    }
}
