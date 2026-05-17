package com.duckylife.heritage.modern

import android.os.Bundle
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
            val coroutineScope = rememberCoroutineScope()
            HeritageTheme(themeMode = themeMode) {
                HeritageApp(
                    themeMode = themeMode,
                    onThemeModeSelected = { selectedThemeMode ->
                        coroutineScope.launch {
                            themeSettingsRepository.setThemeMode(selectedThemeMode)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun HeritageApp(
    themeMode: AppThemeMode,
    onThemeModeSelected: (AppThemeMode) -> Unit,
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
                onThemeModeSelected = onThemeModeSelected,
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
            onThemeModeSelected = {},
        )
    }
}
