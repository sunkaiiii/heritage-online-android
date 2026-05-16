package com.duckylife.heritage.modern.core.settings

enum class AppThemeMode(val storageKey: String) {
    System("system"),
    Light("light"),
    Dark("dark");

    companion object {
        fun fromStorageKey(value: String?): AppThemeMode =
            entries.firstOrNull { it.storageKey == value } ?: System
    }
}
