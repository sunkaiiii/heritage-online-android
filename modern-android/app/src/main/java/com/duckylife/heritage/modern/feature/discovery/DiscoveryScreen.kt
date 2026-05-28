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
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.duckylife.heritage.modern.core.network.dto.ExploreIndexDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.core.network.dto.FeaturedCollectionDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
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
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.topics.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else if (uiState.errorKind != null && uiState.topics.isEmpty()) {
                DiscoveryErrorContent(
                    errorKind = uiState.errorKind,
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

        if (uiState.topics.isNotEmpty()) {
            item {
                ExploreTopicsSection(
                    topics = uiState.topics,
                    onTopicClick = onTopicClick,
                )
            }
        }

        if (uiState.learningPaths.isNotEmpty()) {
            item {
                LearningPathsSection(
                    paths = uiState.learningPaths,
                    onPathClick = onLearningPathClick,
                )
            }
        }

        if (uiState.featuredCollections.isNotEmpty()) {
            item {
                FeaturedCollectionsSection(
                    collections = uiState.featuredCollections,
                    onCollectionClick = onCollectionClick,
                )
            }
        }

        if (uiState.regionAtlas != null) {
            item {
                RegionAtlasCard(
                    atlas = uiState.regionAtlas,
                    onClick = onRegionAtlasClick,
                )
            }
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
