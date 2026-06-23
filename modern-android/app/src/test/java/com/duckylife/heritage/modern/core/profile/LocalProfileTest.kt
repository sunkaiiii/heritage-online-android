package com.duckylife.heritage.modern.core.profile

import org.junit.Assert.assertTrue
import org.junit.Test

class LocalProfileTest {

    @Test
    fun `generated profile id starts with android prefix and matches whitelist`() {
        val id = generateProfileId()
        assertTrue(id.startsWith("android_"))
        assertTrue(id.isValidProfileId())
    }

    @Test
    fun `valid profile id accepts alphanumeric underscore and hyphen`() {
        assertTrue("android_abc-123".isValidProfileId())
        assertTrue("a".isValidProfileId())
    }
}
