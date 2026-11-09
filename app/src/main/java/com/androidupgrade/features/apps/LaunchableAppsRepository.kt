package com.androidupgrade.features.apps

import android.content.Context
import android.content.Intent

data class LaunchableApp(val label: String, val packageName: String)

class LaunchableAppsRepository(private val context: Context) {
    fun load(): List<LaunchableApp> {
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return context.packageManager.queryIntentActivities(query, 0)
            .mapNotNull { item ->
                val packageName = item.activityInfo?.packageName ?: return@mapNotNull null
                val label = item.loadLabel(context.packageManager)?.toString()?.trim().orEmpty()
                if (label.isBlank()) null else LaunchableApp(label, packageName)
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}
