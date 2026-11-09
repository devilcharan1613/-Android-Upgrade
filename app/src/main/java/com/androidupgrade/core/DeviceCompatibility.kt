package com.androidupgrade.core

import android.os.Build

/**
 * Pure API-level policy kept separate from Android device access so it can be unit tested.
 */
object CompatibilityPolicy {
    const val MIN_SUPPORTED_API = 29

    fun isSupported(apiLevel: Int): Boolean = apiLevel >= MIN_SUPPORTED_API

    fun message(apiLevel: Int): String = when {
        apiLevel < MIN_SUPPORTED_API -> "Android 10 or newer is required."
        apiLevel >= 36 -> "Android 16 detected. Availability depends on device support and permissions."
        apiLevel >= 35 -> "Android 15 detected. Availability depends on device support and permissions."
        apiLevel >= 34 -> "Android 14 detected. Availability depends on device support and permissions."
        apiLevel >= 33 -> "Android 13 detected. Availability depends on device support and permissions."
        apiLevel >= 31 -> "Android 12 detected. Availability depends on device support and permissions."
        apiLevel >= 30 -> "Android 11 detected. Availability depends on device support and permissions."
        else -> "Android 10 detected. Some newer platform features are unavailable."
    }
}

object DeviceCompatibility {
    data class DeviceReport(
        val androidRelease: String,
        val apiLevel: Int,
        val supportedByApp: Boolean,
        val compatibilityMessage: String
    )

    fun current(): DeviceReport {
        val api = Build.VERSION.SDK_INT
        return DeviceReport(
            androidRelease = Build.VERSION.RELEASE ?: "Unknown",
            apiLevel = api,
            supportedByApp = CompatibilityPolicy.isSupported(api),
            compatibilityMessage = CompatibilityPolicy.message(api)
        )
    }

    fun supportsApi(requiredApi: Int): Boolean = Build.VERSION.SDK_INT >= requiredApi
}
