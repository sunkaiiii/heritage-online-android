package com.duckylife.heritage.modern.feature.discovery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTodayDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTrendingDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryWeeklyDto
import com.duckylife.heritage.modern.core.network.dto.ExploreIndexDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.core.network.dto.FeaturedCollectionDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
import com.duckylife.heritage.modern.ui.component.DiscoveryItemCard
import com.duckylife.heritage.modern.ui.component.DiscoveryItemRow
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSearchField
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun DiscoveryRoute(
    onSearchSubmit: (String) -> Unit,
    onTopicClick: (ExploreTopicInfoDto) -> Unit,
    onLearningPathClick: (LearningPathDto) -> Unit,
    onCollectionClick: (FeaturedCollectionDto) -> Unit,
    onRegionAtlasClick: () -> Unit,
    onTimelineClick: () -> Unit,
    onKnowledgeGraphClick: () -> Unit,
    onTrendingItemClick: (DiscoveryItemDto) -> Unit,
    onWeeklyItemClick: (DiscoveryItemDto) -> Unit,
    onTodayItemClick: (DiscoveryItemDto) -> Unit,
    onDeepDiveClick: (DiscoveryItemDto) -> Unit,
    onTaxonomyClick: () -> Unit,
    onStoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoveryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    DiscoveryScreen(
        uiState = uiState,
        onRefresh = viewModel::loadAll,
        onSearchSubmit = onSearchSubmit,
        onTopicClick = onTopicClick,
        onLearningPathClick = onLearningPathClick,
        onCollectionClick = onCollectionClick,
        onRegionAtlasClick = onRegionAtlasClick,
        onTimelineClick = onTimelineClick,
        onKnowledgeGraphClick = onKnowledgeGraphClick,
        onSerendipityClick = viewModel::serendipity,
        onTrendingItemClick = onTrendingItemClick,
        onWeeklyItemClick = onWeeklyItemClick,
        onTodayItemClick = onTodayItemClick,
        onRetryToday = viewModel::loadToday,
        onRetryTrending = viewModel::loadTrending,
        onRetryWeekly = viewModel::loadWeekly,
        onRetryClassic = viewModel::loadClassic,
        onDeepDiveClick = onDeepDiveClick,
        onTaxonomyClick = onTaxonomyClick,
        onStoriesClick = onStoriesClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(
    uiState: DiscoveryUiState,
    onRefresh: () -> Unit,
    onSearchSubmit: (String) -> Unit,
    onTopicClick: (ExploreTopicInfoDto) -> Unit,
    onLearningPathClick: (LearningPathDto) -> Unit,
    onCollectionClick: (FeaturedCollectionDto) -> Unit,
    onRegionAtlasClick: () -> Unit,
    onTimelineClick: () -> Unit,
    onKnowledgeGraphClick: () -> Unit,
    onSerendipityClick: () -> Unit,
    onTrendingItemClick: (DiscoveryItemDto) -> Unit,
    onWeeklyItemClick: (DiscoveryItemDto) -> Unit,
    onTodayItemClick: (DiscoveryItemDto) -> Unit,
    onDeepDiveClick: (DiscoveryItemDto) -> Unit,
    onTaxonomyClick: () -> Unit,
    onStoriesClick: () -> Unit,
    onRetryToday: () -> Unit = {},
    onRetryTrending: () -> Unit = {},
    onRetryWeekly: () -> Unit = {},
    onRetryClassic: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 全页 loading 仅在所有区块都在加载且没有任何数据时显示
            if (uiState.isAnyLoading && !uiState.today.hasData && !uiState.trending.hasData && !uiState.weekly.hasData && !uiState.classic.hasData) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else if (uiState.isAllFailed) {
                DiscoveryErrorContent(
                    errorKind = uiState.today.errorKind ?: uiState.trending.errorKind ?: uiState.weekly.errorKind ?: ErrorKind.Unknown,
                    onRetry = onRefresh,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                DiscoveryContent(
                    uiState = uiState,
                    onRefresh = onRefresh,
                    onSearchSubmit = onSearchSubmit,
                    onTopicClick = onTopicClick,
                    onLearningPathClick = onLearningPathClick,
                    onCollectionClick = onCollectionClick,
                    onRegionAtlasClick = onRegionAtlasClick,
                    onTimelineClick = onTimelineClick,
                    onKnowledgeGraphClick = onKnowledgeGraphClick,
                    onSerendipityClick = onSerendipityClick,
                    onTrendingItemClick = onTrendingItemClick,
                    onWeeklyItemClick = onWeeklyItemClick,
                    onTodayItemClick = onTodayItemClick,
                    onDeepDiveClick = onDeepDiveClick,
                    onTaxonomyClick = onTaxonomyClick,
                    onStoriesClick = onStoriesClick,
                    onRetryToday = onRetryToday,
                    onRetryTrending = onRetryTrending,
                    onRetryWeekly = onRetryWeekly,
                    onRetryClassic = onRetryClassic,
                )
            }
        }
    }
}

@Composable
private fun DiscoveryContent(
    uiState: DiscoveryUiState,
    onRefresh: () -> Unit,
    onSearchSubmit: (String) -> Unit,
    onTopicClick: (ExploreTopicInfoDto) -> Unit,
    onLearningPathClick: (LearningPathDto) -> Unit,
    onCollectionClick: (FeaturedCollectionDto) -> Unit,
    onRegionAtlasClick: () -> Unit,
    onTimelineClick: () -> Unit,
    onKnowledgeGraphClick: () -> Unit,
    onSerendipityClick: () -> Unit,
    onTrendingItemClick: (DiscoveryItemDto) -> Unit,
    onWeeklyItemClick: (DiscoveryItemDto) -> Unit,
    onTodayItemClick: (DiscoveryItemDto) -> Unit,
    onDeepDiveClick: (DiscoveryItemDto) -> Unit,
    onTaxonomyClick: () -> Unit,
    onStoriesClick: () -> Unit,
    onRetryToday: () -> Unit,
    onRetryTrending: () -> Unit,
    onRetryWeekly: () -> Unit,
    onRetryClassic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            DiscoveryHeader(
                onRefresh = onRefresh,
            )
        }

        item {
            DiscoverySearchBar(
                onSearchSubmit = onSearchSubmit,
            )
        }

        // 随便看看按钮
        item {
            FilledTonalButton(
                onClick = onSerendipityClick,
                modifier = Modifier.padding(horizontal = 16.dp),
                enabled = !uiState.serendipityLoading,
            ) {
                Text(
                    text = if (uiState.serendipityLoading) {
                        stringResource(R.string.discovery_serendipity_loading)
                    } else {
                        stringResource(R.string.discovery_serendipity)
                    },
                )
            }
        }

        // Serendipity 结果
        if (uiState.serendipityItem != null) {
            item {
                SerendipityResultCard(
                    item = uiState.serendipityItem,
                    onItemClick = onTodayItemClick,
                    onDeepDiveClick = onDeepDiveClick,
                )
            }
        }

        // 今日发现
        item {
            SectionContainer(
                section = uiState.today,
                onRetry = onRetryToday,
                loadingContent = { SectionLoadingPlaceholder() },
                errorContent = { errorKind -> SectionErrorRow(errorKind = errorKind, onRetry = onRetryToday) },
                content = { today -> TodaySection(today = today, onItemClick = onTodayItemClick) },
            )
        }

        // 正在被看见 Trending
        item {
            SectionContainer(
                section = uiState.trending,
                onRetry = onRetryTrending,
                loadingContent = { SectionLoadingPlaceholder() },
                errorContent = { errorKind -> SectionErrorRow(errorKind = errorKind, onRetry = onRetryTrending) },
                content = { trending ->
                    if (trending.items.isNotEmpty()) {
                        TrendingSection(trending = trending, onItemClick = onTrendingItemClick)
                    }
                },
            )
        }

        // 本周非遗包 Weekly
        item {
            SectionContainer(
                section = uiState.weekly,
                onRetry = onRetryWeekly,
                loadingContent = { SectionLoadingPlaceholder() },
                errorContent = { errorKind -> SectionErrorRow(errorKind = errorKind, onRetry = onRetryWeekly) },
                content = { weekly ->
                    if (weekly.sections.isNotEmpty()) {
                        WeeklySection(weekly = weekly, onItemClick = onWeeklyItemClick)
                    }
                },
            )
        }

        // 主题库入口
        item {
            DiscoveryEntryCard(
                title = stringResource(R.string.discovery_taxonomy),
                subtitle = stringResource(R.string.discovery_taxonomy_subtitle),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = onTaxonomyClick,
            )
        }

        // 数据故事入口
        item {
            DiscoveryEntryCard(
                title = stringResource(R.string.discovery_stories),
                subtitle = stringResource(R.string.discovery_stories_subtitle),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = onStoriesClick,
            )
        }

        // 知识图谱入口
        item {
            DiscoveryEntryCard(
                title = stringResource(R.string.knowledge_graph_hub_title),
                subtitle = stringResource(R.string.knowledge_graph_hub_subtitle),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                icon = Icons.Outlined.AccountTree,
                onClick = onKnowledgeGraphClick,
            )
        }

        // 经典区块（探索主题、学习路径、精选合集、地区图谱）
        item {
            SectionContainer(
                section = uiState.classic,
                onRetry = onRetryClassic,
                loadingContent = { SectionLoadingPlaceholder() },
                errorContent = { errorKind -> SectionErrorRow(errorKind = errorKind, onRetry = onRetryClassic) },
                content = { classic ->
                    ClassicSections(
                        classic = classic,
                        onTopicClick = onTopicClick,
                        onLearningPathClick = onLearningPathClick,
                        onCollectionClick = onCollectionClick,
                        onRegionAtlasClick = onRegionAtlasClick,
                    )
                },
            )
        }

        item {
            TimelineCard(
                onClick = onTimelineClick,
            )
        }
    }
}

@Composable
private fun DiscoveryHeader(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageHeader(
        title = stringResource(R.string.discovery_title),
        subtitle = stringResource(R.string.discovery_subtitle),
        modifier = modifier,
        action = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(R.string.action_refresh),
                )
            }
        },
    )
}

@Composable
private fun DiscoverySearchBar(
    onSearchSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchText by remember { mutableStateOf("") }
    HeritageSearchField(
        value = searchText,
        onValueChange = { searchText = it },
        onSearch = { query ->
            if (query.isNotBlank()) {
                onSearchSubmit(query.trim())
            }
        },
        label = stringResource(R.string.discovery_search_placeholder),
        placeholder = stringResource(R.string.discovery_search_placeholder),
        clearContentDescription = stringResource(R.string.action_clear_search),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}

@Composable
private fun ExploreTopicsSection(
    topics: List<ExploreTopicInfoDto>,
    onTopicClick: (ExploreTopicInfoDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HeritageSectionHeader(
            title = stringResource(R.string.discovery_explore_topics),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(topics) { topic ->
                FilterChip(
                    selected = false,
                    onClick = { onTopicClick(topic) },
                    label = {
                        Text(
                            text = topic.title ?: topic.key ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun LearningPathsSection(
    paths: List<LearningPathDto>,
    onPathClick: (LearningPathDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HeritageSectionHeader(
            title = stringResource(R.string.discovery_learning_paths),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(paths) { path ->
                LearningPathCard(
                    path = path,
                    onClick = { onPathClick(path) },
                )
            }
        }
    }
}

@Composable
private fun LearningPathCard(
    path: LearningPathDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = path.title ?: "",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!path.subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = path.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.discovery_items_count, path.stepCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun FeaturedCollectionsSection(
    collections: List<FeaturedCollectionDto>,
    onCollectionClick: (FeaturedCollectionDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HeritageSectionHeader(
            title = stringResource(R.string.discovery_featured_collections),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(collections) { collection ->
                FeaturedCollectionCard(
                    collection = collection,
                    onClick = { onCollectionClick(collection) },
                )
            }
        }
    }
}

@Composable
private fun FeaturedCollectionCard(
    collection: FeaturedCollectionDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = collection.title ?: "",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!collection.subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = collection.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.discovery_items_count, collection.itemCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun RegionAtlasCard(
    atlas: RegionAtlasDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.discovery_region_atlas),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.discovery_items_count, atlas.totals?.regionCount ?: 0),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            TextButton(onClick = onClick) {
                Text(text = stringResource(R.string.discovery_view_all))
            }
        }
    }
}

@Composable
private fun TimelineCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.discovery_timeline),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.discovery_timeline_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            TextButton(onClick = onClick) {
                Text(text = stringResource(R.string.discovery_view_all))
            }
        }
    }
}

// Serendipity 结果卡片
@Composable
private fun SerendipityResultCard(
    item: DiscoveryItemDto,
    onItemClick: (DiscoveryItemDto) -> Unit,
    onDeepDiveClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = { onItemClick(item) },
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = stringResource(R.string.discovery_serendipity),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            if (!item.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!item.category.isNullOrBlank()) {
                    HeritageMetaChip(text = item.category)
                }
                if (!item.region.isNullOrBlank()) {
                    HeritageMetaChip(text = item.region)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onDeepDiveClick(item) }) {
                Text(text = stringResource(R.string.discovery_deep_dive))
            }
        }
    }
}

// 今日发现
@Composable
private fun TodaySection(
    today: DiscoveryTodayDto,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HeritageSectionHeader(
            title = stringResource(R.string.discovery_today_title),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (today.featuredDirectoryItem != null) {
            DiscoveryItemRow(
                item = today.featuredDirectoryItem,
                onClick = { onItemClick(today.featuredDirectoryItem) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (today.featuredInheritor != null) {
            DiscoveryItemRow(
                item = today.featuredInheritor,
                onClick = { onItemClick(today.featuredInheritor) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (today.articles.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(today.articles) { article ->
                    DiscoveryItemCard(
                        item = article,
                        onClick = { onItemClick(article) },
                    )
                }
            }
        }
    }
}

// 正在被看见 Trending
@Composable
private fun TrendingSection(
    trending: DiscoveryTrendingDto,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HeritageSectionHeader(
            title = stringResource(R.string.discovery_trending_title),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(trending.items) { item ->
                DiscoveryItemCard(
                    item = item,
                    onClick = { onItemClick(item) },
                )
            }
        }
    }
}

// 本周非遗包 Weekly
@Composable
private fun WeeklySection(
    weekly: DiscoveryWeeklyDto,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HeritageSectionHeader(
            title = stringResource(R.string.discovery_weekly_title),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        weekly.sections.take(2).forEach { section ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            if (!section.subtitle.isNullOrBlank()) {
                Text(
                    text = section.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(section.items.take(5)) { item ->
                    DiscoveryItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

// 通用入口卡片（主题库、数据故事）
@Composable
private fun DiscoveryEntryCard(
    title: String,
    subtitle: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            TextButton(onClick = onClick) {
                Text(text = stringResource(R.string.discovery_view_all))
            }
        }
    }
}

// 区块容器：根据 section state 决定显示 loading / error / content
@Composable
private fun <T> SectionContainer(
    section: DiscoverySectionState<T>,
    onRetry: () -> Unit,
    loadingContent: @Composable () -> Unit,
    errorContent: @Composable (ErrorKind) -> Unit,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        when {
            section.isLoading && !section.hasData -> loadingContent()
            section.hasFatalError -> errorContent(section.errorKind!!)
            section.hasData -> {
                section.errorKind?.let { errorKind -> errorContent(errorKind) }
                content(section.data!!)
            }
        }
    }
}

@Composable
private fun SectionLoadingPlaceholder(modifier: Modifier = Modifier) {
    HeritageContentCard(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.context_loading),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionErrorRow(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(errorKind.fallbackResId()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

// 经典区块组合
@Composable
private fun ClassicSections(
    classic: DiscoveryClassicData,
    onTopicClick: (ExploreTopicInfoDto) -> Unit,
    onLearningPathClick: (LearningPathDto) -> Unit,
    onCollectionClick: (FeaturedCollectionDto) -> Unit,
    onRegionAtlasClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (classic.topics.isNotEmpty()) {
            ExploreTopicsSection(
                topics = classic.topics,
                onTopicClick = onTopicClick,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (classic.learningPaths.isNotEmpty()) {
            LearningPathsSection(
                paths = classic.learningPaths,
                onPathClick = onLearningPathClick,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (classic.featuredCollections.isNotEmpty()) {
            FeaturedCollectionsSection(
                collections = classic.featuredCollections,
                onCollectionClick = onCollectionClick,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (classic.regionAtlas != null) {
            RegionAtlasCard(
                atlas = classic.regionAtlas,
                onClick = onRegionAtlasClick,
            )
        }
    }
}

@Composable
private fun DiscoveryErrorContent(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(errorKind.fallbackResId()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.action_retry))
        }
    }
}
