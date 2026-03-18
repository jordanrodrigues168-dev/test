package app.jordan.zukutools

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import rikka.shizuku.Shizuku 
import app.jordan.zukutools.ui.DashboardScreen // Ensure this import is correct

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_SHIZUKU = 100

    private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode: Int, grantResult: Int ->
        if (requestCode == REQUEST_CODE_SHIZUKU) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, we recompose the UI
                recreate() 
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Shizuku.addRequestPermissionResultListener(permissionListener)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // FIX: Pass the required parameters to the DashboardScreen
                    DashboardScreen(
                        isAuthorized = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED,
                        onIdentifyClick = { 
                            if (!Shizuku.pingBinder()) {
                                // Shizuku isn't running
                            } else {
                                checkShizukuPermission()
                            }
                        }
                    ) 
                }
            }
        }
        
        checkShizukuPermission()
    }

    private fun checkShizukuPermission() {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(REQUEST_CODE_SHIZUKU)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(permissionListener)
    }
}
