package com.androidupgrade.core

import android.os.Build

object DeviceCompatibility {
    data class DeviceReport(
        val androidRelease: String,
        val apiLevel: Int,
        val supportedByApp: Boolean,
        val compatibilityMessage: String
    )

    fun current(): DeviceReport {
        val api = Build.VERSION.SDK_INT
        val supported = api >= Build.VERSION_CODES.Q
        val release = Build.VERSION.RELEASE ?: "Unknown"
        val message = when {
            !supported -> "Android 10 or newer is required."
            api >= 36 -> "Android 16 detected. Availability depends on device support and permissions."
            api >= 35 -> "Android 15 detected. Availability depends on device support and permissions."
            api >= 34 -> "Android 14 detected. Availability depends on device support and permissions."
            api >= 33 -> "Android 13 detected. Availability depends on device support and permissions."
            api >= 31 -> "Android 12 detected. Availability depends on device support and permissions."
            api >= 30 -> "Android 11 detected. Availability depends on device support and permissions."
            else -> "Android 10 detected. Some newer platform features are unavailable."
        }
        return DeviceReport(release, api, supported, message)
    }

    fun supportsApi(requiredApi: Int): Boolean = Build.VERSION.SDK_INT >= requiredApi
}
