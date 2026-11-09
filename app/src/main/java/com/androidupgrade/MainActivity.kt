package com.androidupgrade

import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.content.Context
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.androidupgrade.core.DeviceCompatibility

private val UpgradeGreen = Color(0xFF167D68)
private val SoftGreen = Color(0xFFEAF4F0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UpgradeTheme { UpgradeRoot() } }
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

private data class FeatureItem(
    val title: String,
    val detail: String,
    val status: String,
    val icon: ImageVector
)

private val featureItems = listOf(
    FeatureItem("Personalization", "Theme and appearance settings", "Starter", Icons.Default.Palette),
    FeatureItem("Control Center", "Shortcuts to supported Android settings", "Starter", Icons.Default.Tune),
    FeatureItem("Notifications", "Open notification access settings", "Permission setup only", Icons.Default.Notifications),
    FeatureItem("Privacy & Security", "Privacy settings shortcuts", "Starter", Icons.Default.Security),
    FeatureItem("Battery & Performance", "Battery status and device information", "Basic", Icons.Default.BatteryFull),
    FeatureItem("Multitasking", "Productivity tools", "Planned", Icons.Default.Apps),
    FeatureItem("Accessibility", "Open Android accessibility settings", "Settings shortcut", Icons.Default.Accessibility),
    FeatureItem("Camera & Media", "Camera and media tools", "Planned", Icons.Default.Smartphone),
    FeatureItem("Automation", "Local routines and reminders", "Planned", Icons.Default.Settings),
    FeatureItem("Offline Assistant", "Basic help without cloud AI", "Basic", Icons.Default.Home),
    FeatureItem("Optional Launcher", "Early launcher landing screen", "Early preview", Icons.Default.Home),
    FeatureItem("Connectivity", "Open Wi-Fi and network settings", "Settings shortcut", Icons.Default.Wifi)
)

@Composable
private fun UpgradeRoot() {
    var page by remember { mutableStateOf("Home") }
    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf("Home", "Features", "Settings").forEach { label ->
                    val icon = when (label) {
                        "Home" -> Icons.Default.Home
                        "Features" -> Icons.Default.Apps
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
            "Home" -> HomePage(Modifier.padding(padding), onExplore = { page = "Features" })
            "Features" -> FeaturesPage(Modifier.padding(padding))
            else -> SettingsPage(Modifier.padding(padding))
        }
    }
}

@Composable
private fun HomePage(modifier: Modifier, onExplore: () -> Unit) {
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
            Text("Modern utilities for Android 10 and newer", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Text(battery?.let { "$it% reported battery level" } ?: "Battery level unavailable on this device")
                    Text("This is the Android-reported charge level, not battery health.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Offline by design", fontWeight = FontWeight.SemiBold)
                    Text("No internet permission, account, ads, analytics or cloud service is included.")
                    Text("Android Upgrade does not upgrade or replace your phone's operating system.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { Button(onClick = onExplore, modifier = Modifier.fillMaxWidth()) { Text("Explore feature modules") } }
    }
}

@Composable
private fun FeaturesPage(modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("Feature modules", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { Text("Planned and permission-dependent features are labelled honestly.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(featureItems) { item ->
            Card {
                Row(
                    Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(color = SoftGreen) {
                        Icon(item.icon, contentDescription = null, tint = UpgradeGreen, modifier = Modifier.padding(10.dp))
                    }
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
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Settings & permissions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Open Android settings", fontWeight = FontWeight.SemiBold)
                    Text("Android controls protected settings. These shortcuts do not silently change them.")
                    SettingsButton("General settings", Settings.ACTION_SETTINGS, context)
                    SettingsButton("Accessibility", Settings.ACTION_ACCESSIBILITY_SETTINGS, context)
                    SettingsButton("Notification access", Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS, context)
                    SettingsButton("Wi-Fi", Settings.ACTION_WIFI_SETTINGS, context)
                    SettingsButton("Display", Settings.ACTION_DISPLAY_SETTINGS, context)
                    SettingsButton("Battery", Settings.ACTION_BATTERY_SAVER_SETTINGS, context)
                }
            }
        }
        item { Text("Android Upgrade 0.1.0 · Offline foundation", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun SettingsButton(label: String, action: String, context: Context) {
    Button(
        onClick = {
            runCatching { context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        },
        modifier = Modifier.fillMaxWidth()
    ) { Text(label) }
}

private fun readBatteryPercent(context: Context): Int? {
    val manager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return null
    val level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    return level.takeIf { it in 0..100 }
}
