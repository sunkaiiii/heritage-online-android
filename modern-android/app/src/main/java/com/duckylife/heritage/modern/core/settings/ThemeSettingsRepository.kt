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

@Singleton
class ThemeSettingsRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val themeMode: Flow<AppThemeMode> =
        context.settingsDataStore.data.map { preferences ->
            AppThemeMode.fromStorageKey(preferences[ThemeModeKey])
        }

    suspend fun setThemeMode(themeMode: AppThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[ThemeModeKey] = themeMode.storageKey
        }
    }

    private companion object {
        val ThemeModeKey = stringPreferencesKey("theme_mode")
    }
}
