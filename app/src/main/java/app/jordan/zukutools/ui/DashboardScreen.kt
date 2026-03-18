package app.jordan.zukutools.ui

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import app.jordan.zukutools.AppPackageInfo
import app.jordan.zukutools.ShizukuShell
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(isAuthorized: Boolean, onAuthorize: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentPath by remember { mutableStateOf("/sdcard") }
    var apps by remember { mutableStateOf<List<AppPackageInfo>>(emptyList()) }
    var showRebootDialog by remember { mutableStateOf(false) }

    fun refreshApps() {
        val pm = context.packageManager
        apps = pm.getInstalledApplications(0).map { info ->
            AppPackageInfo(
                name = info.loadLabel(pm).toString(),
                packageName = info.packageName, 
                isSystem = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0, 
                isEnabled = info.enabled
            )
        }.sortedBy { it.name.lowercase() }
    }

    LaunchedEffect(isAuthorized) {
        if (isAuthorized) refreshApps()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("ZukuTools") },
                actions = {
                    IconButton(onClick = { showRebootDialog = true }) {
                        Icon(Icons.Default.PowerSettingsNew, "Power")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Apps, null) }, label = { Text("Apps") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Folder, null) }, label = { Text("Modules") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (!isAuthorized) {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Button(onClick = onAuthorize) { Text("Authorize Shizuku") }
                }
            } else {
                when (selectedTab) {
                    0 -> AppList(apps) { pkg, enable -> 
                        ShizukuShell.toggleApp(pkg, enable)
                        refreshApps()
                    }
                    1 -> FileExplorer(currentPath, onPathChange = { currentPath = it }, onRun = { path ->
                        scope.launch {
                            val res = if (path.endsWith(".apk")) ShizukuShell.installApk(path) else "Script Executed"
                            snackbarHostState.showSnackbar(res)
                        }
                    })
                }
            }
        }

        if (showRebootDialog) {
            AlertDialog(
                onDismissRequest = { showRebootDialog = false },
                title = { Text("Reboot Menu") },
                confirmButton = { TextButton(onClick = { ShizukuShell.reboot("") }) { Text("System") } },
                dismissButton = { TextButton(onClick = { ShizukuShell.reboot("recovery") }) { Text("Recovery") } }
            )
        }
    }
}

@Composable
fun AppList(apps: List<AppPackageInfo>, onToggle: (String, Boolean) -> Unit) {
    val context = LocalContext.current
    LazyColumn {
        items(apps) { app ->
            ListItem(
                leadingContent = {
                    val icon = remember { context.packageManager.getApplicationIcon(app.packageName) }
                    Image(icon.toBitmap().asImageBitmap(), null, Modifier.size(42.dp))
                },
                headlineContent = { Text(app.name) },
                supportingContent = { Text(app.packageName) },
                trailingContent = {
                    Switch(checked = app.isEnabled, onCheckedChange = { onToggle(app.packageName, it) })
                }
            )
        }
    }
}

@Composable
fun FileExplorer(path: String, onPathChange: (String) -> Unit, onRun: (String) -> Unit) {
    val files = remember(path) { ShizukuShell.listFiles(path) }
    LazyColumn {
        item { Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            Text("Path: $path", Modifier.padding(12.dp).fillMaxWidth(), style = MaterialTheme.typography.labelSmall)
        } }
        items(files) { fileName ->
            val isFile = fileName.contains(".")
            ListItem(
                modifier = Modifier.clickable { 
                    if (!isFile) onPathChange(if (path == "/") "/$fileName" else "$path/$fileName") 
                },
                headlineContent = { Text(fileName) },
                leadingContent = { Icon(if (isFile) Icons.Default.Description else Icons.Default.Folder, null) },
                trailingContent = {
                    if (fileName.endsWith(".apk") || fileName.endsWith(".sh")) {
                        IconButton(onClick = { onRun("$path/$fileName") }) {
                            Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    }
}
