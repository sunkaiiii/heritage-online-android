package com.duckylife.heritage.modern.core.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class AppThemeModeTest {
    @Test
    fun fromStorageKeyParsesKnownModes() {
        assertEquals(AppThemeMode.System, AppThemeMode.fromStorageKey("system"))
        assertEquals(AppThemeMode.Light, AppThemeMode.fromStorageKey("light"))
        assertEquals(AppThemeMode.Dark, AppThemeMode.fromStorageKey("dark"))
    }

    @Test
    fun fromStorageKeyFallsBackToSystem() {
        assertEquals(AppThemeMode.System, AppThemeMode.fromStorageKey(null))
        assertEquals(AppThemeMode.System, AppThemeMode.fromStorageKey("unexpected"))
    }
}
