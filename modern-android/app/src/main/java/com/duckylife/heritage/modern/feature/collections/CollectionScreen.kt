package com.duckylife.heritage.modern.feature.collections

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.CollectionItemDto
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.formatIsoDate
import com.duckylife.heritage.modern.ui.text.localizedArticleCategory
import com.duckylife.heritage.modern.ui.text.localizedCollectionType

@Composable
fun CollectionRoute(
    id: String?,
    type: String?,
    topicKey: String?,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CollectionViewModel = hiltViewModel<CollectionViewModel, CollectionViewModel.Factory>(
        key = "collection-${id.orEmpty()}-${type.orEmpty()}-${topicKey.orEmpty()}",
        creationCallback = { factory -> factory.create(id = id, type = type, topicKey = topicKey) },
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    CollectionScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadCollection,
        onItemClick = { item ->
            val itemId = item.id ?: return@CollectionScreen
            when (item.type) {
                "article" -> onArticleSelected(itemId)
                "directoryItem" -> onDirectoryItemSelected(itemId)
                "inheritor" -> onInheritorSelected(itemId)
            }
        },
        modifier = modifier,
    )
}

@Composable
fun CollectionScreen(
    uiState: CollectionUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onItemClick: (CollectionItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    CollectionErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.collection != null -> {
                    CollectionContent(
                        collection = uiState.collection,
                        onBack = onBack,
                        onItemClick = onItemClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CollectionContent(
    collection: CollectionDto,
    onBack: () -> Unit,
    onItemClick: (CollectionItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    title = collection.title.orEmpty(),
                    subtitle = collection.subtitle,
                )
            }
        }

        item {
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                localizedCollectionType(collection.type)?.let { typeLabel ->
                    HeritageMetaChip(text = typeLabel)
                }
                collection.tags.forEach { tag ->
                    HeritageMetaChip(text = tag)
                }
                if (!collection.generatedAt.isNullOrBlank()) {
                    HeritageMetaChip(text = formatIsoDate(collection.generatedAt) ?: collection.generatedAt)
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.search_results_count, collection.items.size),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        items(
            items = collection.items,
            key = { it.id.orEmpty() },
        ) { item ->
            CollectionItemRow(
                item = item,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun CollectionItemRow(
    item: CollectionItemDto,
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
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val typeLabel = when (item.type) {
                    "article" -> stringResource(R.string.search_type_article)
                    "directoryItem" -> stringResource(R.string.search_type_directory)
                    "inheritor" -> stringResource(R.string.search_type_inheritor)
                    else -> item.type.orEmpty()
                }
                HeritageMetaChip(text = typeLabel)
                localizedArticleCategory(item.category)?.let { categoryLabel ->
                    HeritageMetaChip(text = categoryLabel)
                }
            }
            Text(
                text = item.title.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!item.summary.isNullOrBlank()) {
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodyMedium,
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
                if (item.publishedYear != null) {
                    Text(
                        text = stringResource(R.string.directory_year_format, item.publishedYear),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CollectionScreenPreview() {
    HeritageTheme {
        CollectionScreen(
            uiState = CollectionUiState(
                isLoading = false,
                collection = CollectionDto(
                    id = "preview-collection",
                    title = "传统技艺精选",
                    subtitle = "探索各地传统技艺名录",
                    type = "category",
                    tags = listOf("技艺", "推荐"),
                    generatedAt = "2024-01-01T00:00:00Z",
                    items = listOf(
                        CollectionItemDto(
                            id = "1",
                            type = "directoryItem",
                            title = "景泰蓝制作技艺",
                            summary = "北京传统手工艺，以掐丝珐琅闻名。",
                            category = "traditionalCraft",
                            region = "北京",
                            publishedYear = 2006,
                        ),
                        CollectionItemDto(
                            id = "2",
                            type = "article",
                            title = "非遗保护的当代路径",
                            summary = "探讨非遗项目如何在现代社会传承。",
                            category = "specialTopic",
                        ),
                    ),
                ),
            ),
            onBack = {},
            onRetry = {},
            onItemClick = {},
        )
    }
}

@Preview(name = "Collection Loading", showBackground = true)
@Composable
private fun CollectionScreenLoadingPreview() {
    HeritageTheme {
        CollectionScreen(
            uiState = CollectionUiState(isLoading = true),
            onBack = {},
            onRetry = {},
            onItemClick = {},
        )
    }
}

@Composable
private fun CollectionErrorContent(
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
