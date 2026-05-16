package com.duckylife.heritage.modern.feature.directory

import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

private data object DirectoryList

private data class DirectoryDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
)

@Composable
fun DirectoryRoute(
    modifier: Modifier = Modifier,
) {
    val backStack = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateListOf<Any>(DirectoryList) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key ->
            when (key) {
                DirectoryList -> NavEntry(key) {
                    DirectoryListRoute(
                        onItemSelected = { item ->
                            backStack.add(
                                DirectoryDetail(
                                    id = item.id,
                                    kind = item.kind,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }

                is DirectoryDetail -> NavEntry(key) {
                    DirectoryDetailRoute(
                        itemId = key.id,
                        sourceId = key.sourceId,
                        kind = key.kind,
                        onBack = { backStack.removeLastOrNull() },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    DirectoryListRoute(
                        onItemSelected = { item ->
                            backStack.add(
                                DirectoryDetail(
                                    id = item.id,
                                    kind = item.kind,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }
            }
        },
    )
}

@Composable
private fun DirectoryListRoute(
    onItemSelected: (DirectoryItemSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DirectoryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val items = viewModel.items.collectAsLazyPagingItems()
    DirectoryScreen(
        uiState = uiState,
        items = items,
        onKindSelected = viewModel::selectKind,
        onItemSelected = onItemSelected,
        modifier = modifier,
    )
}

@Composable
fun DirectoryScreen(
    uiState: DirectoryUiState,
    items: LazyPagingItems<DirectoryItemSummaryDto>,
    onKindSelected: (DirectoryItemKind) -> Unit,
    onItemSelected: (DirectoryItemSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            DirectoryHeader(onRetry = items::refresh)
        }

        item {
            DirectoryKindFilters(
                selectedKind = uiState.selectedKind,
                onKindSelected = onKindSelected,
            )
        }

        when (val refreshState = items.loadState.refresh) {
            is LoadState.Loading -> item {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                )
            }

            is LoadState.Error -> item {
                StatusContent(
                    title = stringResource(R.string.content_load_failed),
                    message = refreshState.error.message ?: stringResource(R.string.directory_load_failed),
                    actionLabel = stringResource(R.string.action_retry),
                    onAction = items::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )
            }

            is LoadState.NotLoading -> {
                if (items.itemCount == 0) {
                    item {
                        StatusContent(
                            title = stringResource(R.string.content_empty_title),
                            message = stringResource(R.string.directory_empty_message),
                            actionLabel = stringResource(R.string.action_refresh),
                            onAction = items::refresh,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                        )
                    }
                }
            }
        }

        items(
            count = items.itemCount,
            key = items.itemKey { it.id ?: it.sourceUrl ?: it.title.orEmpty() },
        ) { index ->
            val item = items[index]
            if (item != null) {
                DirectoryItemRow(
                    item = item,
                    imageLoader = imageLoader,
                    onClick = { onItemSelected(item) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }

        when (val appendState = items.loadState.append) {
            is LoadState.Loading -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> item {
                InlineRetryMessage(
                    message = appendState.error.message ?: stringResource(R.string.directory_append_failed),
                    onRetry = items::retry,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun DirectoryHeader(onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.directory_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.directory_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = stringResource(R.string.action_refresh),
            )
        }
    }
}

@Composable
private fun DirectoryKindFilters(
    selectedKind: DirectoryItemKind,
    onKindSelected: (DirectoryItemKind) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(DirectoryItemKind.entries) { kind ->
            FilterChip(
                selected = kind == selectedKind,
                onClick = { onKindSelected(kind) },
                label = { Text(stringResource(kind.labelRes)) },
            )
        }
    }
}

@Composable
private fun DirectoryItemRow(
    item: DirectoryItemSummaryDto,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unnamedItem = stringResource(R.string.unnamed_directory_item)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ImageOrFallback(
                image = item.coverImage,
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(width = 84.dp, height = 84.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.title.orEmpty().ifBlank { unnamedItem },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                DirectoryMetaRow(item)
                if (!item.summary.isNullOrBlank()) {
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectoryMetaRow(item: DirectoryItemSummaryDto) {
    val projectCode = item.projectCode?.takeIf { it.isNotBlank() }?.let {
        stringResource(R.string.directory_project_code_format, it)
    }
    val publishedYear = item.publishedYear?.let {
        stringResource(R.string.directory_year_format, it)
    }
    val labels = listOfNotNull(
        item.category?.takeIf { it.isNotBlank() },
        item.region?.takeIf { it.isNotBlank() },
        projectCode,
        publishedYear,
        item.listType?.takeIf { it.isNotBlank() },
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(labels) { label ->
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

@Composable
private fun ImageOrFallback(
    image: MediaAssetDto?,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val imageUrl = image?.displayUrl ?: image?.thumbnailUrl ?: image?.originalUrl ?: image?.sourceUrl
    if (imageUrl.isNullOrBlank()) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.brand_fallback),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = image?.altText,
            imageLoader = imageLoader,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun InlineRetryMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f),
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun StatusContent(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = onAction) {
            Text(actionLabel)
        }
    }
}

@get:StringRes
private val DirectoryItemKind.labelRes: Int
    get() = when (this) {
        DirectoryItemKind.NationalProject -> R.string.directory_kind_national_project
        DirectoryItemKind.CulturalEcoZone -> R.string.directory_kind_cultural_eco_zone
        DirectoryItemKind.ProductiveProtectionBase -> R.string.directory_kind_productive_protection_base
        DirectoryItemKind.UnescoEntry -> R.string.directory_kind_unesco_entry
        DirectoryItemKind.ChinaUnescoEntry -> R.string.directory_kind_china_unesco_entry
        DirectoryItemKind.ContractingState -> R.string.directory_kind_contracting_state
    }

@Preview(showBackground = true)
@Composable
private fun DirectoryScreenPreview() {
    HeritageTheme {}
}
