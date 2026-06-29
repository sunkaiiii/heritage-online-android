package com.duckylife.heritage.modern.feature.regions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicItemDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicLinkDto
import com.duckylife.heritage.modern.core.network.dto.FacetBucketDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDetailDto
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.localizedDirectoryKind
import com.duckylife.heritage.modern.ui.text.localizedHeritageFacetLabel

@Composable
fun RegionDetailRoute(
    region: String,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedRegionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegionDetailViewModel = hiltViewModel<RegionDetailViewModel, RegionDetailViewModel.Factory>(
        key = "region-detail-$region",
        creationCallback = { factory -> factory.create(region = region) },
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    RegionDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadDetail,
        onArticleSelected = onArticleSelected,
        onDirectoryItemSelected = onDirectoryItemSelected,
        onInheritorSelected = onInheritorSelected,
        onRelatedRegionSelected = onRelatedRegionSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDetailScreen(
    uiState: RegionDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedRegionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    RegionDetailErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.detail != null -> {
                    RegionDetailContent(
                        detail = uiState.detail,
                        onBack = onBack,
                        onArticleSelected = onArticleSelected,
                        onDirectoryItemSelected = onDirectoryItemSelected,
                        onInheritorSelected = onInheritorSelected,
                        onRelatedRegionSelected = onRelatedRegionSelected,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegionDetailContent(
    detail: RegionAtlasDetailDto,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedRegionSelected: (String) -> Unit,
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
                    title = detail.displayName ?: detail.region.orEmpty(),
                    subtitle = null,
                )
            }
        }

        // Stats
        if (detail.stats != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatChip(
                        label = stringResource(R.string.region_total_directory),
                        count = detail.stats.directoryItemCount,
                        modifier = Modifier.weight(1f),
                    )
                    StatChip(
                        label = stringResource(R.string.region_total_inheritors),
                        count = detail.stats.inheritorCount,
                        modifier = Modifier.weight(1f),
                    )
                    StatChip(
                        label = stringResource(R.string.region_total),
                        count = detail.stats.total,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Category breakdown
        if (detail.categoryBreakdown.isNotEmpty() && detail.categoryBreakdown.any { it.count > 0 }) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.statistics_category_title),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                BreakdownRow(
                    buckets = detail.categoryBreakdown,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Kind breakdown
        if (detail.kindBreakdown.isNotEmpty() && detail.kindBreakdown.any { it.count > 0 }) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.region_kind_breakdown),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                BreakdownRow(
                    buckets = detail.kindBreakdown,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Featured directory items
        if (detail.featuredDirectoryItems.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.region_featured_items),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.featuredDirectoryItems) { item ->
                DirectoryItemRow(
                    item = item,
                    onClick = {
                        val id = item.id ?: return@DirectoryItemRow
                        onDirectoryItemSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Featured inheritors
        if (detail.featuredInheritors.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.region_featured_inheritors),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.featuredInheritors) { inheritor ->
                InheritorRow(
                    inheritor = inheritor,
                    onClick = {
                        val id = inheritor.id ?: return@InheritorRow
                        onInheritorSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Related articles
        if (detail.relatedArticles.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.related_articles_title),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.relatedArticles) { article ->
                ArticleRow(
                    article = article,
                    onClick = {
                        val id = article.id ?: return@ArticleRow
                        onArticleSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Related regions
        if (detail.relatedRegions.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.region_related_regions),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    detail.relatedRegions.forEach { region ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                if (!region.key.isNullOrBlank()) {
                                    onRelatedRegionSelected(region.key)
                                }
                            },
                            label = { Text(region.title.orEmpty()) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    count: Long,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    buckets: List<FacetBucketDto>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(buckets) { bucket ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = localizedHeritageFacetLabel(bucket.key) ?: bucket.key.orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = bucket.count.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectoryItemRow(
    item: DirectoryItemSummaryDto,
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
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.title.orEmpty().ifBlank { stringResource(R.string.unnamed_directory_item) },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                localizedDirectoryKind(item.kind.wireName)?.let { kindLabel ->
                    HeritageMetaChip(text = kindLabel)
                }
                if (!item.region.isNullOrBlank()) {
                    HeritageMetaChip(text = item.region)
                }
            }
        }
    }
}

@Composable
private fun InheritorRow(
    inheritor: InheritorSummaryDto,
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
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = inheritor.name.orEmpty().ifBlank { stringResource(R.string.unnamed_inheritor) },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!inheritor.projectName.isNullOrBlank()) {
                    HeritageMetaChip(text = inheritor.projectName)
                }
                if (!inheritor.region.isNullOrBlank()) {
                    HeritageMetaChip(text = inheritor.region)
                }
            }
        }
    }
}

@Composable
private fun ArticleRow(
    article: ArticleSummaryDto,
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
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = article.title.orEmpty().ifBlank { stringResource(R.string.unnamed_article) },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!article.summary.isNullOrBlank()) {
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun RegionDetailErrorContent(
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
