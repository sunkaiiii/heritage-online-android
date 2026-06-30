package com.duckylife.heritage.modern.feature.stories

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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import androidx.compose.ui.tooling.preview.Preview
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.component.MetricPill
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun StoriesIndexRoute(
    onBack: () -> Unit,
    onRegionStoryClick: (String) -> Unit,
    onCategoryStoryClick: (String) -> Unit,
    onYearStoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StoriesIndexViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    StoriesIndexScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadAll,
        onRegionStoryClick = onRegionStoryClick,
        onCategoryStoryClick = onCategoryStoryClick,
        onYearStoryClick = onYearStoryClick,
        modifier = modifier,
    )
}

@Composable
fun StoriesIndexScreen(
    uiState: StoriesIndexUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onRegionStoryClick: (String) -> Unit,
    onCategoryStoryClick: (String) -> Unit,
    onYearStoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    StoriesIndexErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.isEmpty -> {
                    StoriesIndexEmptyContent(
                        onBack = onBack,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    StoriesIndexContent(
                        uiState = uiState,
                        onBack = onBack,
                        onRegionStoryClick = onRegionStoryClick,
                        onCategoryStoryClick = onCategoryStoryClick,
                        onYearStoryClick = onYearStoryClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun StoriesIndexContent(
    uiState: StoriesIndexUiState,
    onBack: () -> Unit,
    onRegionStoryClick: (String) -> Unit,
    onCategoryStoryClick: (String) -> Unit,
    onYearStoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                    )
                }
                HeritagePageHeader(
                    title = stringResource(R.string.discovery_stories),
                    subtitle = stringResource(R.string.story_index_subtitle),
                )
            }
        }

        // 按地区阅读
        if (uiState.regions.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.story_index_by_region),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.regions) { region ->
                        StoryEntryCard(
                            title = region.title,
                            subtitle = region.subtitle,
                            total = region.total,
                            onClick = { onRegionStoryClick(region.key) },
                        )
                    }
                }
            }
        }

        // 按分类阅读
        if (uiState.categories.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.story_index_by_category),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.categories) { category ->
                        StoryEntryCard(
                            title = category.title,
                            subtitle = category.subtitle,
                            total = category.total,
                            onClick = { onCategoryStoryClick(category.key) },
                        )
                    }
                }
            }
        }

        // 按年份阅读
        if (uiState.years.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.story_index_by_year),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.years) { year ->
                        StoryYearCard(
                            year = year.year,
                            total = year.total,
                            onClick = { onYearStoryClick(year.year) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryEntryCard(
    title: String,
    subtitle: String?,
    total: Long,
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
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            MetricPill(
                label = stringResource(R.string.taxonomy_stat_total),
                value = total.toString(),
            )
        }
    }
}

@Composable
private fun StoryYearCard(
    year: Int,
    total: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.discovery_items_count, total.toInt()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StoriesIndexErrorContent(
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

@Preview
@Composable
private fun StoriesIndexScreenPreview() {
    HeritageTheme {
        StoriesIndexScreen(
            uiState = StoriesIndexUiState(
                isLoading = false,
                regions = listOf(
                    TaxonomyTopicDto(
                        type = "region",
                        key = "北京",
                        title = "北京",
                        subtitle = "京剧与传统技艺",
                        total = 247,
                    ),
                    TaxonomyTopicDto(
                        type = "region",
                        key = "江苏",
                        title = "江苏",
                        subtitle = "苏绣与昆曲",
                        total = 412,
                    ),
                ),
                categories = listOf(
                    TaxonomyTopicDto(
                        type = "category",
                        key = "traditionalCraft",
                        title = "传统技艺",
                        subtitle = "匠心传承",
                        total = 128,
                    ),
                ),
                years = listOf(
                    TimelineYearBucketDto(year = 2024, total = 12),
                    TimelineYearBucketDto(year = 2023, total = 28),
                    TimelineYearBucketDto(year = 2022, total = 35),
                ),
            ),
            onBack = {},
            onRetry = {},
            onRegionStoryClick = {},
            onCategoryStoryClick = {},
            onYearStoryClick = {},
        )
    }
}

@Preview(name = "Stories Index Empty")
@Composable
private fun StoriesIndexScreenEmptyPreview() {
    HeritageTheme {
        StoriesIndexScreen(
            uiState = StoriesIndexUiState(isLoading = false),
            onBack = {},
            onRetry = {},
            onRegionStoryClick = {},
            onCategoryStoryClick = {},
            onYearStoryClick = {},
        )
    }
}

@Composable
private fun StoriesIndexEmptyContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.story_index_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = stringResource(R.string.action_back))
        }
    }
}
