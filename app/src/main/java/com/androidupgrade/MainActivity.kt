package com.androidupgrade

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.androidupgrade.core.DeviceCompatibility
import com.androidupgrade.features.apps.LaunchableApp
import com.androidupgrade.features.apps.LaunchableAppsRepository
import com.androidupgrade.features.assistant.LocalAssistantEngine

private val UpgradeGreen = Color(0xFF167D68)
private val SoftGreen = Color(0xFFEAF4F0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startPage = if (intent.getBooleanExtra("open_apps", false)) "Apps" else "Home"
        setContent { UpgradeTheme { UpgradeRoot(startPage) } }
    }
}

@Composable
private fun UpgradeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = UpgradeGreen,
            secondary = Color(0xFF4B665D),
            background = Color(0xFFF6F8F7),
            surface = Color.White
        ),
        content = content
    )
}

private data class FeatureItem(val title: String, val detail: String, val status: String, val icon: ImageVector)

private val featureItems = listOf(
    FeatureItem("Personalization", "App theme and appearance foundation", "Basic", Icons.Default.Palette),
    FeatureItem("Control Center", "In-app quick actions and system settings links", "Implemented", Icons.Default.Tune),
    FeatureItem("Notifications", "Notification access setup shortcut", "Permission required", Icons.Default.Notifications),
    FeatureItem("Privacy & Security", "Android privacy and permission settings", "Settings shortcuts", Icons.Default.Security),
    FeatureItem("Battery & Performance", "Live charge level and battery saver shortcut", "Basic", Icons.Default.BatteryFull),
    FeatureItem("Multitasking", "Productivity tools", "Planned", Icons.Default.Apps),
    FeatureItem("Accessibility", "Android accessibility settings shortcut", "Settings shortcut", Icons.Default.Accessibility),
    FeatureItem("Camera & Media", "Camera/media utilities", "Planned", Icons.Default.Smartphone),
    FeatureItem("Automation", "On-device routines", "Planned", Icons.Default.Settings),
    FeatureItem("Offline Assistant", "Rule-based local commands, no cloud AI", "Basic", Icons.Default.Home),
    FeatureItem("Launcher", "Installed-app drawer using Android launch intents", "Basic", Icons.Default.Apps),
    FeatureItem("Connectivity", "Wi-Fi and Bluetooth settings shortcuts", "Settings shortcut", Icons.Default.Wifi)
)

@Composable
private fun UpgradeRoot(startPage: String) {
    var page by remember { mutableStateOf(startPage) }
    val pages = listOf("Home", "Control", "Apps", "Assistant", "Features", "Settings")
    Scaffold(
        bottomBar = {
            NavigationBar {
                pages.forEach { label ->
                    val icon = when (label) {
                        "Home" -> Icons.Default.Home
                        "Control" -> Icons.Default.Tune
                        "Apps" -> Icons.Default.Apps
                        "Assistant" -> Icons.Default.Bolt
                        "Features" -> Icons.Default.Smartphone
                        else -> Icons.Default.Settings
                    }
                    NavigationBarItem(
                        selected = page == label,
                        onClick = { page = label },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        when (page) {
            "Home" -> HomePage(Modifier.padding(padding), onExplore = { page = "Features" }, onControl = { page = "Control" })
            "Control" -> ControlCenterPage(Modifier.padding(padding))
            "Apps" -> AppsPage(Modifier.padding(padding))
            "Assistant" -> AssistantPage(Modifier.padding(padding))
            "Features" -> FeaturesPage(Modifier.padding(padding))
            else -> SettingsPage(Modifier.padding(padding))
        }
    }
}

@Composable
private fun HomePage(modifier: Modifier, onExplore: () -> Unit, onControl: () -> Unit) {
    val report = remember { DeviceCompatibility.current() }
    val context = LocalContext.current
    val battery = remember { readBatteryPercent(context) }
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 18.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Android Upgrade", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Offline utilities for Android 10 and newer", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SoftGreen)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Smartphone, contentDescription = null, tint = UpgradeGreen)
                    Text("Your device", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("Android ${report.androidRelease} · API ${report.apiLevel}")
                    Text(report.compatibilityMessage, style = MaterialTheme.typography.bodyMedium)
                    AssistChip(onClick = {}, label = { Text(if (report.supportedByApp) "Minimum OS met" else "Unsupported OS") })
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Battery", fontWeight = FontWeight.SemiBold)
                    Text(battery?.let { "$it% reported charge" } ?: "Battery level unavailable")
                    Text("Charge percentage is not battery health.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Private and offline", fontWeight = FontWeight.SemiBold)
                    Text("No internet permission, account, ads, analytics or cloud service.")
                    Text("This app adds utilities. It does not replace or upgrade Android OS.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { Button(onClick = onControl, modifier = Modifier.fillMaxWidth()) { Text("Open Control Center") } }
        item { OutlinedButton(onClick = onExplore, modifier = Modifier.fillMaxWidth()) { Text("Explore all modules") } }
    }
}

@Composable
private fun ControlCenterPage(modifier: Modifier) {
    val context = LocalContext.current
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    var torchOn by remember { mutableStateOf(false) }
    var torchMessage by remember { mutableStateOf("Flashlight is off") }
    val torchPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            setTorch(context, cameraManager, !torchOn) { success, message -> torchOn = success; torchMessage = message }
        } else torchMessage = "Camera permission denied. Flashlight is unavailable."
    }
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(18.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Control Center", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { Text("These are in-app shortcuts. Android protects system Quick Settings and some device controls.") }
        item {
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LightMode, contentDescription = null)
                        Text("Flashlight", fontWeight = FontWeight.SemiBold)
                    }
                    Text(torchMessage, style = MaterialTheme.typography.bodySmall)
                    Button(onClick = {
                        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        if (granted) setTorch(context, cameraManager, !torchOn) { success, message -> torchOn = success; torchMessage = message }
                        else torchPermission.launch(Manifest.permission.CAMERA)
                    }, modifier = Modifier.fillMaxWidth()) { Text(if (torchOn) "Turn flashlight off" else "Turn flashlight on") }
                }
            }
        }
        item { QuickActionCard("Wi-Fi", "Open Wi-Fi settings", Settings.ACTION_WIFI_SETTINGS, Icons.Default.Wifi) }
        item { QuickActionCard("Bluetooth", "Open Bluetooth settings", Settings.ACTION_BLUETOOTH_SETTINGS, Icons.Default.Bolt) }
        item { QuickActionCard("Display", "Brightness, screen timeout and display options", Settings.ACTION_DISPLAY_SETTINGS, Icons.Default.LightMode) }
        item { QuickActionCard("Battery saver", "Open Android battery saver controls", Settings.ACTION_BATTERY_SAVER_SETTINGS, Icons.Default.BatteryFull) }
        item { QuickActionCard("Do Not Disturb", "Open interruption controls", Settings.ACTION_SOUND_SETTINGS, Icons.Default.Notifications) }
    }
}

@Composable
private fun QuickActionCard(title: String, detail: String, action: String, icon: ImageVector) {
    val context = LocalContext.current
    Card(Modifier.clickable { openSystemSetting(context, action, title) }) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, contentDescription = null, tint = UpgradeGreen)
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Open", color = UpgradeGreen)
        }
    }
}

private fun setTorch(context: Context, manager: CameraManager, enabled: Boolean, result: (Boolean, String) -> Unit) {
    try {
        val cameraId = manager.cameraIdList.firstOrNull { id ->
            manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                manager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        } ?: manager.cameraIdList.firstOrNull { id ->
            manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        if (cameraId == null) result(false, "No compatible camera flash found.")
        else {
            manager.setTorchMode(cameraId, enabled)
            result(enabled, if (enabled) "Flashlight is on" else "Flashlight is off")
        }
    } catch (_: Exception) {
        result(false, "Unable to control flashlight on this device.")
        Toast.makeText(context, "Flashlight unavailable", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun AppsPage(modifier: Modifier) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val apps = remember { LaunchableAppsRepository(context).load() }
    val filtered = remember(query, apps) { apps.filter { it.label.contains(query, ignoreCase = true) }.sortedBy { it.label.lowercase() } }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text("Apps", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Search installed apps") }, singleLine = true)
        Text("${filtered.size} launchable apps", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filtered, key = { it.packageName }) { app ->
                Card(Modifier.fillMaxWidth().clickable {
                    val launch = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    if (launch != null) runCatching { context.startActivity(launch) }
                        .onFailure { Toast.makeText(context, "Unable to open ${app.label}", Toast.LENGTH_SHORT).show() }
                    else Toast.makeText(context, "App cannot be launched", Toast.LENGTH_SHORT).show()
                }) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(app.label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Text("Open", color = UpgradeGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun AssistantPage(modifier: Modifier) {
    val context = LocalContext.current
    var command by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("I work offline. Try: open Wi-Fi, open Bluetooth, open display, open settings, open apps, or battery status.") }
    Column(modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Offline Assistant", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Local command helper. This is not a cloud or generative AI model.")
        Card {
            Text(response, modifier = Modifier.fillMaxWidth().padding(16.dp))
        }
        OutlinedTextField(
            value = command,
            onValueChange = { command = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Type a command") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                response = runAssistantCommand(context, command)
                command = ""
            })
        )
        Button(onClick = { response = runAssistantCommand(context, command); command = "" }, modifier = Modifier.fillMaxWidth()) { Text("Run command") }
        Text("Available commands", fontWeight = FontWeight.SemiBold)
        listOf("open Wi-Fi", "open Bluetooth", "open display", "open settings", "open apps", "battery status").forEach { example ->
            TextButton(onClick = { response = runAssistantCommand(context, example) }) { Text(example) }
        }
    }
}

private fun runAssistantCommand(context: Context, command: String): String {
    val action = LocalAssistantEngine.resolve(command)
    return when (action) {
        LocalAssistantEngine.Action.WIFI -> { openSystemSetting(context, Settings.ACTION_WIFI_SETTINGS, "Wi-Fi"); "Opening Wi-Fi settings." }
        LocalAssistantEngine.Action.BLUETOOTH -> { openSystemSetting(context, Settings.ACTION_BLUETOOTH_SETTINGS, "Bluetooth"); "Opening Bluetooth settings." }
        LocalAssistantEngine.Action.DISPLAY -> { openSystemSetting(context, Settings.ACTION_DISPLAY_SETTINGS, "Display"); "Opening display settings." }
        LocalAssistantEngine.Action.SETTINGS -> { openSystemSetting(context, Settings.ACTION_SETTINGS, "Settings"); "Opening Android settings." }
        LocalAssistantEngine.Action.APPS -> { context.startActivity(Intent(context, MainActivity::class.java).putExtra("open_apps", true)); "Opening installed apps." }
        LocalAssistantEngine.Action.BATTERY -> readBatteryPercent(context)?.let { "Android reports $it% battery charge. This is not battery health." } ?: "Battery percentage is unavailable."
        LocalAssistantEngine.Action.HELP -> "Try: open Wi-Fi, open Bluetooth, open display, open settings, open apps, or battery status."
    }
}

private fun openSystemSetting(context: Context, action: String, label: String) {
    val intent = Intent(action)
    if (intent.resolveActivity(context.packageManager) != null) {
        runCatching { context.startActivity(intent) }
            .onFailure { Toast.makeText(context, "Unable to open $label on this device.", Toast.LENGTH_SHORT).show() }
    } else Toast.makeText(context, "$label is not available on this device.", Toast.LENGTH_SHORT).show()
}

@Composable
private fun FeaturesPage(modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Feature modules", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { Text("Status labels distinguish working utilities from planned work.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(featureItems) { item ->
            Card {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = SoftGreen) { Icon(item.icon, contentDescription = null, tint = UpgradeGreen, modifier = Modifier.padding(10.dp)) }
                    Column(Modifier.weight(1f)) {
                        Text(item.title, fontWeight = FontWeight.SemiBold)
                        Text(item.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(item.status, style = MaterialTheme.typography.labelSmall, color = UpgradeGreen)
                }
            }
        }
    }
}

@Composable
private fun SettingsPage(modifier: Modifier) {
    val context = LocalContext.current
    var note by remember { mutableStateOf(context.getSharedPreferences("upgrade_local", Context.MODE_PRIVATE).getString("private_note", "") ?: "") }
    LazyColumn(modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Settings & privacy", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Android settings shortcuts", fontWeight = FontWeight.SemiBold)
                    Text("Android controls protected settings. These shortcuts do not silently change them.")
                    SettingsButton("General settings", Settings.ACTION_SETTINGS)
                    SettingsButton("Accessibility", Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    SettingsButton("Notification access", Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    SettingsButton("Wi-Fi", Settings.ACTION_WIFI_SETTINGS)
                    SettingsButton("Display", Settings.ACTION_DISPLAY_SETTINGS)
                    SettingsButton("Battery", Settings.ACTION_BATTERY_SAVER_SETTINGS)
                    SettingsButton("App permissions", Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:${context.packageName}")
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Private local note", fontWeight = FontWeight.SemiBold)
                    Text("Stored only on this device using app-private preferences. It is not encrypted.")
                    OutlinedTextField(value = note, onValueChange = {
                        note = it
                        context.getSharedPreferences("upgrade_local", Context.MODE_PRIVATE).edit().putString("private_note", it).apply()
                    }, modifier = Modifier.fillMaxWidth(), label = { Text("Write a note") }, minLines = 3)
                }
            }
        }
        item { Text("Android Upgrade 0.2.0 · Offline utilities", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun SettingsButton(label: String, action: String, data: String? = null) {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        val intent = Intent(action).apply { if (data != null) this.data = android.net.Uri.parse(data) }
        if (intent.resolveActivity(context.packageManager) != null) runCatching { context.startActivity(intent) }
            .onFailure { Toast.makeText(context, "Unable to open $label", Toast.LENGTH_SHORT).show() }
        else Toast.makeText(context, "$label is unavailable", Toast.LENGTH_SHORT).show()
    }, modifier = Modifier.fillMaxWidth()) { Text(label) }
}

private fun readBatteryPercent(context: Context): Int? {
    val manager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return null
    val level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    return level.takeIf { it in 0..100 }
}
