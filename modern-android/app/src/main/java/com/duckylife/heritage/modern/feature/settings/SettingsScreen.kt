package com.duckylife.heritage.modern.feature.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.settings.AppLanguageMode
import com.duckylife.heritage.modern.core.settings.AppThemeMode
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    languageMode: AppLanguageMode,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onLanguageModeSelected: (AppLanguageMode) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            item {
                SettingsSection(
                    title = stringResource(R.string.settings_appearance_title),
                    groupTitle = stringResource(R.string.settings_theme_mode_title),
                ) {
                    AppThemeMode.entries.forEachIndexed { index, mode ->
                        SettingsOptionRow(
                            label = stringResource(mode.labelRes),
                            selected = mode == themeMode,
                            onClick = { onThemeModeSelected(mode) },
                        )
                        if (index != AppThemeMode.entries.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }

            item {
                SettingsSection(
                    title = stringResource(R.string.settings_language_title),
                    groupTitle = stringResource(R.string.settings_language_mode_title),
                ) {
                    AppLanguageMode.entries.forEachIndexed { index, mode ->
                        SettingsOptionRow(
                            label = stringResource(mode.labelRes),
                            selected = mode == languageMode,
                            onClick = { onLanguageModeSelected(mode) },
                        )
                        if (index != AppLanguageMode.entries.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    groupTitle: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingsSectionTitle(text = title)
        HeritageContentCard {
            Column {
                SettingsGroupTitle(
                    text = groupTitle,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                content()
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
    )
}

@Composable
private fun SettingsGroupTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

@Composable
private fun SettingsOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                } else {
                    Color.Transparent
                },
            )
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
        RadioButton(
            selected = selected,
            onClick = null,
        )
    }
}

@get:StringRes
private val AppThemeMode.labelRes: Int
    get() = when (this) {
        AppThemeMode.System -> R.string.theme_mode_system
        AppThemeMode.Light -> R.string.theme_mode_light
        AppThemeMode.Dark -> R.string.theme_mode_dark
    }

@get:StringRes
private val AppLanguageMode.labelRes: Int
    get() = when (this) {
        AppLanguageMode.System -> R.string.language_mode_system
        AppLanguageMode.SimplifiedChinese -> R.string.language_mode_simplified_chinese
        AppLanguageMode.English -> R.string.language_mode_english
    }

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    HeritageTheme {
        SettingsScreen(
            themeMode = AppThemeMode.System,
            languageMode = AppLanguageMode.System,
            onThemeModeSelected = {},
            onLanguageModeSelected = {},
            onBack = {},
        )
    }
}
