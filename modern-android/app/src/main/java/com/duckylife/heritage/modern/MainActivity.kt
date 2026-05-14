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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.feature.articles.ArticlesNavHost
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeritageTheme {
                HeritageApp()
            }
        }
    }
}

@Composable
private fun HeritageApp() {
    var selectedDestination by remember { mutableStateOf(HomeDestination.Articles) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                HomeDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = destination == selectedDestination,
                        onClick = { selectedDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { contentPadding ->
        when (selectedDestination) {
            HomeDestination.Articles -> ArticlesNavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            HomeDestination.Directory -> PlaceholderDestination(
                title = "名录",
                description = "下一条切片会接入非遗项目名录。",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            HomeDestination.Inheritors -> PlaceholderDestination(
                title = "传承人",
                description = "传承人列表会在项目名录之后接入。",
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
    val label: String,
    val icon: ImageVector,
) {
    Articles("文章", Icons.AutoMirrored.Outlined.Article),
    Directory("名录", Icons.Outlined.CollectionsBookmark),
    Inheritors("传承人", Icons.Outlined.Groups),
}

@Preview(showBackground = true)
@Composable
private fun HeritageAppPreview() {
    HeritageTheme {
        HeritageApp()
    }
}
