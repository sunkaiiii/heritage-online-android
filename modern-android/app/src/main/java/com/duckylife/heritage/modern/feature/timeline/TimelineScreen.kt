package com.duckylife.heritage.modern.feature.timeline

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.TimelineItemDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun TimelineRoute(
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    TimelineScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadMore,
        onYearSelected = viewModel::selectYear,
        onToggleType = viewModel::toggleType,
        onLoadMore = viewModel::loadMore,
        onItemClick = { item ->
            val id = item.id ?: return@TimelineScreen
            when (item.type) {
                "article" -> onArticleSelected(id)
                "directoryItem" -> onDirectoryItemSelected(id)
                "inheritor" -> onInheritorSelected(id)
            }
        },
        onClearError = viewModel::clearError,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    uiState: TimelineUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onYearSelected: (Int?) -> Unit,
    onToggleType: (SearchResultType) -> Unit,
    onLoadMore: () -> Unit,
    onItemClick: (TimelineItemDto) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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
                Text(
                    text = stringResource(R.string.discovery_timeline),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            // Year selector
            if (uiState.years.isNotEmpty()) {
                YearSelector(
                    years = uiState.years,
                    selectedYear = uiState.selectedYear,
                    onYearSelected = onYearSelected,
                )
            }

            // Type filter
            if (uiState.facets.isNotEmpty() && uiState.selectedYear != null) {
                TypeFilterRow(
                    facets = uiState.facets,
                    selectedTypes = uiState.selectedTypes,
                    onToggleType = onToggleType,
                )
            }

            // Content
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.errorKind != null && uiState.items.isEmpty() -> {
                        TimelineErrorContent(
                            errorKind = uiState.errorKind,
                            onRetry = onRetry,
                            onClearError = onClearError,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    uiState.selectedYear == null -> {
                        TimelineEmptyContent(
                            message = stringResource(R.string.timeline_select_year),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    uiState.items.isEmpty() && !uiState.isLoading -> {
                        TimelineEmptyContent(
                            message = stringResource(R.string.search_empty_message),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    else -> {
                        TimelineItemsList(
                            items = uiState.items,
                            isLoadingMore = uiState.isLoadingMore,
                            hasMore = uiState.hasMore,
                            onItemClick = onItemClick,
                            onLoadMore = onLoadMore,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearSelector(
    years: List<TimelineYearBucketDto>,
    selectedYear: Int?,
    onYearSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(years) { bucket ->
            val year = bucket.year
            FilterChip(
                selected = year == selectedYear,
                onClick = {
                    onYearSelected(if (year == selectedYear) null else year)
                },
                label = {
                    Text(stringResource(R.string.directory_year_format, year))
                },
            )
        }
    }
}

@Composable
private fun TypeFilterRow(
    facets: List<com.duckylife.heritage.modern.core.network.dto.FacetBucketDto>,
    selectedTypes: Set<SearchResultType>,
    onToggleType: (SearchResultType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(facets) { facet ->
            val key = facet.key ?: return@items
            val type = SearchResultType.entries.firstOrNull { it.wireName == key } ?: return@items
            FilterChip(
                selected = type in selectedTypes,
                onClick = { onToggleType(type) },
                label = {
                    Text("$key (${facet.count})")
                },
            )
        }
    }
}

@Composable
private fun TimelineItemsList(
    items: List<TimelineItemDto>,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onItemClick: (TimelineItemDto) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = { it.id.orEmpty() },
        ) { item ->
            TimelineItemRow(
                item = item,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (hasMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(onClick = onLoadMore) {
                        Text(stringResource(R.string.search_load_more))
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineItemRow(
    item: TimelineItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Timeline dot
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = item.year?.toString().orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val typeLabel = when (item.type) {
                        "article" -> stringResource(R.string.search_type_article)
                        "directoryItem" -> stringResource(R.string.search_type_directory)
                        "inheritor" -> stringResource(R.string.search_type_inheritor)
                        else -> item.type.orEmpty()
                    }
                    HeritageMetaChip(text = typeLabel)
                    if (!item.category.isNullOrBlank()) {
                        HeritageMetaChip(text = item.category)
                    }
                }
                Text(
                    text = item.title.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!item.summary.isNullOrBlank()) {
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!item.region.isNullOrBlank()) {
                        Text(
                            text = item.region,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!item.kind.isNullOrBlank()) {
                        Text(
                            text = item.kind,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineEmptyContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TimelineErrorContent(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    onClearError: () -> Unit,
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onClearError) {
                Text(stringResource(R.string.action_back))
            }
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}
