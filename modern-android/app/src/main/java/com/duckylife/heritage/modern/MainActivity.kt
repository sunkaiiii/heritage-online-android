package com.duckylife.heritage.modern

import android.app.LocaleManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.duckylife.heritage.modern.feature.discovery.DiscoveryNavHost
import com.duckylife.heritage.modern.feature.inheritors.InheritorsRoute
import com.duckylife.heritage.modern.feature.my.MyPage
import com.duckylife.heritage.modern.feature.my.MyPageDestination
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
            val systemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                AppThemeMode.System -> systemInDarkTheme
                AppThemeMode.Light -> false
                AppThemeMode.Dark -> true
            }
            // 先等 DataStore 返回真实语言设置；如果先用 System 临时值，
            // Android 会先重启 Activity，等保存值回来后又再重启一次。
            LaunchedEffect(languageMode) {
                languageMode?.let(::applyLanguageMode)
            }
            SideEffect {
                // App 支持独立主题设置，系统栏图标也要跟随 App 当前明暗主题。
                applySystemBarStyle(darkTheme)
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

    private fun applySystemBarStyle(darkTheme: Boolean) {
        val style = if (darkTheme) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.argb(0x80, 0x1B, 0x1B, 0x1B),
            )
        }
        enableEdgeToEdge(
            statusBarStyle = style,
            navigationBarStyle = style,
        )
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
    var selectedDestination by rememberSaveable { mutableStateOf(HomeDestination.Articles) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showMyPage by rememberSaveable { mutableStateOf(false) }
    var articlesInDetail by remember { mutableStateOf(false) }
    var directoryInDetail by remember { mutableStateOf(false) }
    var inheritorsInDetail by remember { mutableStateOf(false) }
    var discoveryInDetail by remember { mutableStateOf(false) }
    var myPageDestination by remember { mutableStateOf<MyPageDestination?>(null) }
    val selectedDestinationInDetail = when (selectedDestination) {
        HomeDestination.Articles -> articlesInDetail
        HomeDestination.Directory -> directoryInDetail
        HomeDestination.Inheritors -> inheritorsInDetail
        HomeDestination.Discovery -> discoveryInDetail
    }
    val shouldShowBottomBar = !showSettings && !showMyPage && !selectedDestinationInDetail

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 0.dp,
                ) {
                    HomeDestination.entries.forEach { destination ->
                        val label = stringResource(destination.labelRes)
                        NavigationBarItem(
                            selected = destination == selectedDestination,
                            onClick = {
                                showSettings = false
                                selectedDestination = destination
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = label,
                                )
                            },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
    ) { contentPadding ->
        // MyPage 分支必须在 Settings 之前：从设置页进入"收藏与最近浏览"后，
        // showSettings 和 showMyPage 同时为 true，MyPage 优先渲染；
        // 从 MyPage 返回时只关 MyPage，Settings 仍在下方。
        if (showMyPage) {
            MyPage(
                onBack = { showMyPage = false },
                onNavigate = { destination ->
                    myPageDestination = destination
                    showMyPage = false
                    showSettings = false
                    selectedDestination = when (destination) {
                        is MyPageDestination.Article -> HomeDestination.Articles
                        is MyPageDestination.Directory -> HomeDestination.Directory
                        is MyPageDestination.Inheritor -> HomeDestination.Inheritors
                        is MyPageDestination.GraphExplore -> HomeDestination.Discovery
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
            return@Scaffold
        }

        if (showSettings) {
            SettingsScreen(
                themeMode = themeMode,
                languageMode = languageMode,
                onThemeModeSelected = onThemeModeSelected,
                onLanguageModeSelected = onLanguageModeSelected,
                onBack = { showSettings = false },
                onMyPageClick = { showMyPage = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
            return@Scaffold
        }

        when (selectedDestination) {
            HomeDestination.Articles -> ArticlesNavHost(
                onSettingsSelected = { showSettings = true },
                onSecondaryDestinationChanged = { articlesInDetail = it },
                pendingNavigation = myPageDestination as? MyPageDestination.Article,
                onPendingNavigationConsumed = { myPageDestination = null },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            HomeDestination.Directory -> DirectoryRoute(
                onSecondaryDestinationChanged = { directoryInDetail = it },
                pendingNavigation = myPageDestination as? MyPageDestination.Directory,
                onPendingNavigationConsumed = { myPageDestination = null },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            HomeDestination.Inheritors -> InheritorsRoute(
                onSecondaryDestinationChanged = { inheritorsInDetail = it },
                pendingNavigation = myPageDestination as? MyPageDestination.Inheritor,
                onPendingNavigationConsumed = { myPageDestination = null },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            HomeDestination.Discovery -> DiscoveryNavHost(
                onSecondaryDestinationChanged = { discoveryInDetail = it },
                pendingNavigation = myPageDestination as? MyPageDestination.GraphExplore,
                onPendingNavigationConsumed = { myPageDestination = null },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

private enum class HomeDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Articles(R.string.nav_articles, Icons.AutoMirrored.Outlined.Article),
    Directory(R.string.nav_directory, Icons.Outlined.CollectionsBookmark),
    Inheritors(R.string.nav_inheritors, Icons.Outlined.Groups),
    Discovery(R.string.nav_discovery, Icons.Outlined.Explore),
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
