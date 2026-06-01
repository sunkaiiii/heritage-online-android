package com.duckylife.heritage.modern.feature.taxonomy

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageReferenceCard
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.component.MetricPill
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun TaxonomyDetailRoute(
    type: String,
    key: String,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedTopicClick: (String, String) -> Unit,
    onViewStory: () -> Unit,
    onCompare: () -> Unit,
    onCollectionSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TaxonomyDetailViewModel = hiltViewModel<TaxonomyDetailViewModel, TaxonomyDetailViewModel.Factory>(
        key = "taxonomy-detail-$type-$key",
        creationCallback = { factory -> factory.create(type = type, key = key) },
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    TaxonomyDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadDetail,
        onArticleSelected = onArticleSelected,
        onDirectoryItemSelected = onDirectoryItemSelected,
        onInheritorSelected = onInheritorSelected,
        onRelatedTopicClick = onRelatedTopicClick,
        onViewStory = onViewStory,
        onCompare = onCompare,
        onCollectionSelected = onCollectionSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyDetailScreen(
    uiState: TaxonomyDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedTopicClick: (String, String) -> Unit,
    onViewStory: () -> Unit,
    onCompare: () -> Unit,
    onCollectionSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    TaxonomyDetailErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.categoryDetail != null -> {
                    CategoryDetailContent(
                        detail = uiState.categoryDetail,
                        onBack = onBack,
                        onArticleSelected = onArticleSelected,
                        onDirectoryItemSelected = onDirectoryItemSelected,
                        onInheritorSelected = onInheritorSelected,
                        onRelatedTopicClick = onRelatedTopicClick,
                        onViewStory = onViewStory,
                        onCompare = onCompare,
                        onCollectionSelected = onCollectionSelected,
                    )
                }
                uiState.regionDetail != null -> {
                    RegionDetailContent(
                        detail = uiState.regionDetail,
                        onBack = onBack,
                        onArticleSelected = onArticleSelected,
                        onDirectoryItemSelected = onDirectoryItemSelected,
                        onInheritorSelected = onInheritorSelected,
                        onRelatedTopicClick = onRelatedTopicClick,
                        onViewStory = onViewStory,
                        onCompare = onCompare,
                        onCollectionSelected = onCollectionSelected,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryDetailContent(
    detail: com.duckylife.heritage.modern.core.network.dto.TaxonomyCategoryDetailDto,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedTopicClick: (String, String) -> Unit,
    onViewStory: () -> Unit,
    onCompare: () -> Unit,
    onCollectionSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
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
                    title = detail.topic.title,
                    subtitle = detail.topic.subtitle,
                )
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_directory_items),
                    value = detail.stats.directoryItemCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_inheritors),
                    value = detail.stats.inheritorCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_articles),
                    value = detail.stats.articleCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_total),
                    value = detail.stats.total.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Top regions
        if (detail.topRegions.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_top_regions),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    detail.topRegions.forEach { region ->
                        HeritageMetaChip(text = "${region.region} (${region.count})")
                    }
                }
            }
        }

        // Articles
        if (detail.articles.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_articles),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.articles) { article ->
                ArticleReferenceRow(
                    article = article,
                    onClick = {
                        val id = article.id ?: return@ArticleReferenceRow
                        onArticleSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Directory items
        if (detail.directoryItems.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_directory_items),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.directoryItems) { item ->
                DirectoryItemReferenceRow(
                    item = item,
                    onClick = {
                        val id = item.id ?: return@DirectoryItemReferenceRow
                        onDirectoryItemSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Inheritors
        if (detail.inheritors.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_inheritors),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.inheritors) { inheritor ->
                InheritorReferenceRow(
                    inheritor = inheritor,
                    onClick = {
                        val id = inheritor.id ?: return@InheritorReferenceRow
                        onInheritorSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Related categories
        if (detail.relatedCategories.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_related_categories),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    detail.relatedCategories.forEach { category ->
                        FilterChip(
                            selected = false,
                            onClick = { onRelatedTopicClick("category", category) },
                            label = { Text(category) },
                        )
                    }
                }
            }
        }

        // Recommended collections
        if (detail.recommendedCollections.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_recommended_collections),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    detail.recommendedCollections.forEach { collection ->
                        HeritageReferenceCard(
                            title = collection.title.orEmpty(),
                            onClick = {
                                if (!collection.id.isNullOrBlank()) {
                                    onCollectionSelected(collection.id)
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // Action buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onViewStory,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.taxonomy_view_story))
                }
                OutlinedButton(
                    onClick = onCompare,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.taxonomy_compare))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegionDetailContent(
    detail: com.duckylife.heritage.modern.core.network.dto.TaxonomyRegionDetailDto,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedTopicClick: (String, String) -> Unit,
    onViewStory: () -> Unit,
    onCompare: () -> Unit,
    onCollectionSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
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
                    title = detail.topic.title,
                    subtitle = detail.topic.subtitle,
                )
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_directory_items),
                    value = detail.stats.directoryItemCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_inheritors),
                    value = detail.stats.inheritorCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_articles),
                    value = detail.stats.articleCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                MetricPill(
                    label = stringResource(R.string.taxonomy_stat_total),
                    value = detail.stats.total.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Top categories
        if (detail.topCategories.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_top_categories),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    detail.topCategories.forEach { category ->
                        HeritageMetaChip(text = "${category.category} (${category.count})")
                    }
                }
            }
        }

        // Articles
        if (detail.articles.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_articles),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.articles) { article ->
                ArticleReferenceRow(
                    article = article,
                    onClick = {
                        val id = article.id ?: return@ArticleReferenceRow
                        onArticleSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Directory items
        if (detail.directoryItems.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_directory_items),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.directoryItems) { item ->
                DirectoryItemReferenceRow(
                    item = item,
                    onClick = {
                        val id = item.id ?: return@DirectoryItemReferenceRow
                        onDirectoryItemSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Inheritors
        if (detail.inheritors.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_inheritors),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(detail.inheritors) { inheritor ->
                InheritorReferenceRow(
                    inheritor = inheritor,
                    onClick = {
                        val id = inheritor.id ?: return@InheritorReferenceRow
                        onInheritorSelected(id)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // Related regions
        if (detail.relatedRegions.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_related_regions),
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
                            onClick = { onRelatedTopicClick("region", region) },
                            label = { Text(region) },
                        )
                    }
                }
            }
        }

        // Recommended collections
        if (detail.recommendedCollections.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.taxonomy_recommended_collections),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    detail.recommendedCollections.forEach { collection ->
                        HeritageReferenceCard(
                            title = collection.title.orEmpty(),
                            onClick = {
                                if (!collection.id.isNullOrBlank()) {
                                    onCollectionSelected(collection.id)
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // Action buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onViewStory,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.taxonomy_view_story))
                }
                OutlinedButton(
                    onClick = onCompare,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.taxonomy_compare))
                }
            }
        }
    }
}

@Composable
private fun ArticleReferenceRow(
    article: ArticleSummaryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageReferenceCard(
        title = article.title.orEmpty().ifBlank { stringResource(R.string.unnamed_article) },
        meta = article.summary,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun DirectoryItemReferenceRow(
    item: DirectoryItemSummaryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageReferenceCard(
        title = item.title.orEmpty().ifBlank { stringResource(R.string.unnamed_directory_item) },
        meta = listOfNotNull(item.kind.wireName, item.region).joinToString(" · "),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun InheritorReferenceRow(
    inheritor: InheritorSummaryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageReferenceCard(
        title = inheritor.name.orEmpty().ifBlank { stringResource(R.string.unnamed_inheritor) },
        meta = listOfNotNull(inheritor.projectName, inheritor.region).joinToString(" · "),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun TaxonomyDetailErrorContent(
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
