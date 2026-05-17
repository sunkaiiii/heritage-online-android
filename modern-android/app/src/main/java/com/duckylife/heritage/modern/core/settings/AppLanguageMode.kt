package com.duckylife.heritage.modern.core.settings

enum class AppLanguageMode(
    val storageKey: String,
    val languageTag: String?,
) {
    System("system", null),
    SimplifiedChinese("zh-Hans", "zh-Hans"),
    English("en", "en");

    companion object {
        fun fromStorageKey(value: String?): AppLanguageMode =
            entries.firstOrNull { it.storageKey == value } ?: System
    }
}
