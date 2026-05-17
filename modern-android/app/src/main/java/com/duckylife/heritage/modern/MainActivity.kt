package com.duckylife.heritage.modern

import android.app.LocaleManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.core.settings.AppLanguageMode
import com.duckylife.heritage.modern.core.settings.AppThemeMode
import com.duckylife.heritage.modern.core.settings.ThemeSettingsRepository
import com.duckylife.heritage.modern.feature.articles.ArticlesNavHost
import com.duckylife.heritage.modern.feature.directory.DirectoryRoute
import com.duckylife.heritage.modern.feature.inheritors.InheritorsRoute
import com.duckylife.heritage.modern.feature.settings.SettingsScreen
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeSettingsRepository: ThemeSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by themeSettingsRepository.themeMode.collectAsStateWithLifecycle(
                initialValue = AppThemeMode.System,
            )
            val languageMode: AppLanguageMode? by themeSettingsRepository.languageMode.collectAsStateWithLifecycle(
                initialValue = null,
            )
            val coroutineScope = rememberCoroutineScope()
            // 先等 DataStore 返回真实语言设置；如果先用 System 临时值，
            // Android 会先重启 Activity，等保存值回来后又再重启一次。
            LaunchedEffect(languageMode) {
                languageMode?.let(::applyLanguageMode)
            }
            HeritageTheme(themeMode = themeMode) {
                HeritageApp(
                    themeMode = themeMode,
                    languageMode = languageMode ?: AppLanguageMode.System,
                    onThemeModeSelected = { selectedThemeMode ->
                        coroutineScope.launch {
                            themeSettingsRepository.setThemeMode(selectedThemeMode)
                        }
                    },
                    onLanguageModeSelected = { selectedLanguageMode ->
                        coroutineScope.launch {
                            themeSettingsRepository.setLanguageMode(selectedLanguageMode)
                        }
                    },
                )
            }
        }
    }

    private fun applyLanguageMode(languageMode: AppLanguageMode) {
        val languageTags = languageMode.languageTag.orEmpty()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 不要为了本地化替换 Compose 的 LocalContext：Hilt ViewModel 需要真实 Activity context。
            // Android 13+ 提供了正式的应用级语言 API，优先走这条路。
            val localeManager = getSystemService(LocaleManager::class.java)
            val currentLanguageTags = localeManager.applicationLocales.toLanguageTags()
            if (currentLanguageTags != languageTags) {
                localeManager.applicationLocales = LocaleList.forLanguageTags(languageTags)
            }
            return
        }

        @Suppress("DEPRECATION")
        val configuration = resources.configuration.apply {
            setLocales(LocaleList.forLanguageTags(languageTags))
        }
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}

@Composable
private fun HeritageApp(
    themeMode: AppThemeMode,
    languageMode: AppLanguageMode,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onLanguageModeSelected: (AppLanguageMode) -> Unit,
) {
    var selectedDestination by remember { mutableStateOf(HomeDestination.Articles) }
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                HomeDestination.entries.forEach { destination ->
                    val label = stringResource(destination.labelRes)
                    NavigationBarItem(
                        selected = destination == selectedDestination,
                        onClick = { selectedDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = label,
                            )
                        },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { contentPadding ->
        if (showSettings) {
            SettingsScreen(
                themeMode = themeMode,
                languageMode = languageMode,
                onThemeModeSelected = onThemeModeSelected,
                onLanguageModeSelected = onLanguageModeSelected,
                onBack = { showSettings = false },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
            return@Scaffold
        }

        when (selectedDestination) {
            HomeDestination.Articles -> ArticlesNavHost(
                onSettingsSelected = { showSettings = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            HomeDestination.Directory -> DirectoryRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            HomeDestination.Inheritors -> InheritorsRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun PlaceholderDestination(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private enum class HomeDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Articles(R.string.nav_articles, Icons.AutoMirrored.Outlined.Article),
    Directory(R.string.nav_directory, Icons.Outlined.CollectionsBookmark),
    Inheritors(R.string.nav_inheritors, Icons.Outlined.Groups),
}

@Preview(showBackground = true)
@Composable
private fun HeritageAppPreview() {
    HeritageTheme {
        HeritageApp(
            themeMode = AppThemeMode.System,
            languageMode = AppLanguageMode.System,
            onThemeModeSelected = {},
            onLanguageModeSelected = {},
        )
    }
}
