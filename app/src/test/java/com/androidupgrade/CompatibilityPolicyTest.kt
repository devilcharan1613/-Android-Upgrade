package com.androidupgrade

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CompatibilityPolicyTest {
    @Test fun android10ApiIsMinimumSupported() { assertTrue(29 >= 29) }
    @Test fun android9IsBelowMinimum() { assertFalse(28 >= 29) }
}
