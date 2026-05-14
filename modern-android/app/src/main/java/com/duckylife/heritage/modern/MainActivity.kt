package com.duckylife.heritage.modern

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Scaffold(
        bottomBar = {
            NavigationBar {
                HomeDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = destination == HomeDestination.Articles,
                        onClick = {},
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
        ShellHome(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
        )
    }
}

@Composable
private fun ShellHome(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "E迹",
                style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "现代 Android 客户端壳已就位",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ShellCapability(
                icon = Icons.AutoMirrored.Outlined.Article,
                title = "Articles",
                detail = "新闻、论坛、专题会统一接入新版文章 API。",
            )
            ShellCapability(
                icon = Icons.Outlined.CollectionsBookmark,
                title = "Directory",
                detail = "非遗项目名录将以分页列表和详情页作为第一条业务切片。",
            )
            ShellCapability(
                icon = Icons.Outlined.Groups,
                title = "Inheritors",
                detail = "传承人页面后续独立成 feature 模块。",
            )
        }
    }
}

@Composable
private fun ShellCapability(
    icon: ImageVector,
    title: String,
    detail: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        )
        Column {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = detail,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
