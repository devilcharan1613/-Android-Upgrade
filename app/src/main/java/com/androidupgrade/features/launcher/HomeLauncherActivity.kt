package com.androidupgrade.features.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.androidupgrade.MainActivity

/**
 * Optional HOME entry point. Opens the app's installed-app drawer; users can
 * restore their previous launcher from Android's Home app settings at any time.
 */
class HomeLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java).putExtra("open_apps", true))
        finish()
    }
}
