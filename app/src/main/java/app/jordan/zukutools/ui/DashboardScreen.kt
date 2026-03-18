package app.jordan.zukutools.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.jordan.zukutools.ShizukuManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    isAuthorized: Boolean,
    onIdentifyClick: () -> Unit
) {
    var packageName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("ZukuTools") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isAuthorized) {
                Text("Shizuku Permission Required")
                Button(onClick = onIdentifyClick) {
                    Text("Grant Permission")
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("App Debloater", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = packageName,
                            onValueChange = { packageName = it },
                            label = { Text("com.example.app") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val success = ShizukuManager.disableApp(packageName)
                                // You could add a snackbar message here
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Disable Package")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { 
                    ShizukuManager.applySystemTweak("settings put global window_animation_scale 0.5")
                }) {
                    Text("Tweak: Faster Animations (0.5x)")
                }
            }
        }
    }
}
