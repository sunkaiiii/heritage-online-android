package com.duckylife.heritage.modern.core.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "heritage_settings")

// 一个很小的应用级设置仓库。这里刻意不绑定 UI，
// 让 Activity 和 SettingsScreen 可以共享同一份主题/语言持久化状态。
@Singleton
class ThemeSettingsRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val themeMode: Flow<AppThemeMode> =
        context.settingsDataStore.data.map { preferences ->
            AppThemeMode.fromStorageKey(preferences[ThemeModeKey])
        }

    val languageMode: Flow<AppLanguageMode> =
        context.settingsDataStore.data.map { preferences ->
            AppLanguageMode.fromStorageKey(preferences[LanguageModeKey])
        }

    suspend fun setThemeMode(themeMode: AppThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[ThemeModeKey] = themeMode.storageKey
        }
    }

    suspend fun setLanguageMode(languageMode: AppLanguageMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[LanguageModeKey] = languageMode.storageKey
        }
    }

    private companion object {
        val ThemeModeKey = stringPreferencesKey("theme_mode")
        val LanguageModeKey = stringPreferencesKey("language_mode")
    }
}
