package com.androidupgrade.features.assistant

object LocalAssistantEngine {
    enum class Action { WIFI, BLUETOOTH, DISPLAY, SETTINGS, APPS, BATTERY, HELP }

    fun resolve(input: String): Action {
        val text = input.trim().lowercase()
        return when {
            text.contains("wifi") || text.contains("wi-fi") -> Action.WIFI
            text.contains("bluetooth") -> Action.BLUETOOTH
            text.contains("display") || text.contains("brightness") -> Action.DISPLAY
            text.contains("battery") -> Action.BATTERY
            text.contains("apps") || text.contains("app drawer") -> Action.APPS
            text.contains("settings") -> Action.SETTINGS
            else -> Action.HELP
        }
    }
}
