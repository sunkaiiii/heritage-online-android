package com.duckylife.heritage.modern.feature.discovery.graphexplore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.feature.discovery.GraphTab

/**
 * 内容关系图谱探索页的最小占位实现。
 *
 * 步骤 17 只需要从“旅程”卡片跳转到“相似”tab；完整的数据加载、邻居/相似/探索/证据
 * 内容将在步骤 29–36 中替换本占位。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphExploreRoute(
    contentType: String,
    contentId: String,
    initialTab: GraphTab,
    onBack: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = GraphTab.entries
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(tabs.indexOf(initialTab).coerceAtLeast(0))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.graph_explore_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(stringResource(tab.labelResId())) },
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.graph_explore_coming_soon),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun GraphTab.labelResId(): Int = when (this) {
    GraphTab.Neighbors -> R.string.graph_tab_neighbors
    GraphTab.Similar -> R.string.graph_tab_similar
    GraphTab.Explore -> R.string.graph_tab_explore
    GraphTab.Evidence -> R.string.graph_tab_evidence
}
