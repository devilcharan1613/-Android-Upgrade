package com.androidupgrade

import com.androidupgrade.core.CompatibilityPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CompatibilityPolicyTest {
    @Test fun android9IsNotSupported() {
        assertFalse(CompatibilityPolicy.isSupported(28))
        assertEquals("Android 10 or newer is required.", CompatibilityPolicy.message(28))
    }

    @Test fun android10IsMinimumSupportedVersion() {
        assertTrue(CompatibilityPolicy.isSupported(29))
        assertEquals(
            "Android 10 detected. Some newer platform features are unavailable.",
            CompatibilityPolicy.message(29)
        )
    }

    @Test fun android11And12AreReportedCorrectly() {
        assertEquals("Android 11 detected. Availability depends on device support and permissions.", CompatibilityPolicy.message(30))
        assertEquals("Android 12 detected. Availability depends on device support and permissions.", CompatibilityPolicy.message(31))
    }

    @Test fun android13Through16AreReportedCorrectly() {
        assertEquals("Android 13 detected. Availability depends on device support and permissions.", CompatibilityPolicy.message(33))
        assertEquals("Android 14 detected. Availability depends on device support and permissions.", CompatibilityPolicy.message(34))
        assertEquals("Android 15 detected. Availability depends on device support and permissions.", CompatibilityPolicy.message(35))
        assertEquals("Android 16 detected. Availability depends on device support and permissions.", CompatibilityPolicy.message(36))
    }

    @Test fun compatibilityBoundaryIsStable() {
        assertFalse(CompatibilityPolicy.isSupported(28))
        assertTrue(CompatibilityPolicy.isSupported(29))
        assertTrue(CompatibilityPolicy.isSupported(36))
    }
}
