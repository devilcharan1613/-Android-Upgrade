package com.androidupgrade

import com.androidupgrade.features.assistant.LocalAssistantEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalAssistantEngineTest {
    @Test fun resolvesSupportedOfflineCommands() {
        assertEquals(LocalAssistantEngine.Action.WIFI, LocalAssistantEngine.resolve("open Wi-Fi"))
        assertEquals(LocalAssistantEngine.Action.BLUETOOTH, LocalAssistantEngine.resolve("Bluetooth settings"))
        assertEquals(LocalAssistantEngine.Action.DISPLAY, LocalAssistantEngine.resolve("change brightness"))
        assertEquals(LocalAssistantEngine.Action.SETTINGS, LocalAssistantEngine.resolve("open settings"))
        assertEquals(LocalAssistantEngine.Action.APPS, LocalAssistantEngine.resolve("show app drawer"))
        assertEquals(LocalAssistantEngine.Action.BATTERY, LocalAssistantEngine.resolve("battery status"))
    }

    @Test fun unknownCommandReturnsHelp() {
        assertEquals(LocalAssistantEngine.Action.HELP, LocalAssistantEngine.resolve("play music"))
    }
}
