package com.androidupgrade.features.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class HomeLauncherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LauncherLanding(
                    onOpenSettings = { startActivity(Intent(Settings.ACTION_SETTINGS)) },
                    onOpenApp = {
                        val launch = packageManager.getLaunchIntentForPackage(packageName)
                        if (launch != null) startActivity(launch) else finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun LauncherLanding(onOpenSettings: () -> Unit, onOpenApp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Android Upgrade Home", style = MaterialTheme.typography.headlineMedium)
        Text("This is an early launcher landing screen, not a complete customizable launcher.")
        Button(onClick = onOpenApp) { Text("Open Android Upgrade") }
        Button(onClick = onOpenSettings) { Text("Device settings") }
    }
}
