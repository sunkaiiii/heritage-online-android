package com.duckylife.heritage.modern.feature.inheritors

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
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

private data object InheritorsList

private data class InheritorDetail(
    val id: String? = null,
    val sourceId: String? = null,
)

private data class InheritorDirectoryDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
)

@Composable
fun InheritorsRoute(
    modifier: Modifier = Modifier,
) {
    val backStack = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateListOf<Any>(InheritorsList) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key ->
            when (key) {
                InheritorsList -> NavEntry(key) {
                    InheritorsListRoute(
                        onInheritorSelected = { inheritor ->
                            backStack.add(
                                InheritorDetail(
                                    id = inheritor.id,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }

                is InheritorDetail -> NavEntry(key) {
                    InheritorDetailRoute(
                        inheritorId = key.id,
                        sourceId = key.sourceId,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedProjectSelected = { reference ->
                            when {
                                reference.isInheritorReference -> reference.toInheritorDetail()?.let(backStack::add)
                                else -> reference.toDirectoryDetail()?.let(backStack::add)
                            }
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toInheritorDetail()?.let(backStack::add)
                        },
                        modifier = modifier,
                    )
                }

                is InheritorDirectoryDetail -> NavEntry(key) {
                    DirectoryDetailRoute(
                        itemId = key.id,
                        sourceId = key.sourceId,
                        kind = key.kind,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedProjectSelected = { reference, fallbackKind ->
                            when {
                                reference.isInheritorReference -> reference.toInheritorDetail()?.let(backStack::add)
                                else -> reference.toDirectoryDetail(fallbackKind)?.let(backStack::add)
                            }
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toInheritorDetail()?.let(backStack::add)
                        },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    InheritorsListRoute(
                        onInheritorSelected = { inheritor ->
                            backStack.add(
                                InheritorDetail(
                                    id = inheritor.id,
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

private fun DirectoryReferenceDto.toDirectoryDetail(
    fallbackKind: DirectoryItemKind = DirectoryItemKind.NationalProject,
): InheritorDirectoryDetail? {
    if (sourceId.isNullOrBlank() || isInheritorReference) {
        return null
    }
    return InheritorDirectoryDetail(
        sourceId = sourceId,
        kind = kind.toDirectoryItemKindOrNull() ?: fallbackKind,
    )
}

private fun DirectoryReferenceDto.toInheritorDetail(): InheritorDetail? {
    if (sourceId.isNullOrBlank()) {
        return null
    }
    return InheritorDetail(sourceId = sourceId)
}

private fun String?.toDirectoryItemKindOrNull(): DirectoryItemKind? =
    DirectoryItemKind.entries.firstOrNull { it.wireName == this }

// 后端有些 relatedProjects 实际指向 /ccr_detail/，这是 ihchina 的传承人页面。
// URL 形态比偶尔误导的 kind 字段更可靠。
private val DirectoryReferenceDto.isInheritorReference: Boolean
    get() = kind.equals("inheritor", ignoreCase = true) ||
        detailUrl?.contains("/ccr_detail/", ignoreCase = true) == true

@Composable
private fun InheritorsListRoute(
    onInheritorSelected: (InheritorSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InheritorsViewModel = hiltViewModel(),
) {
    val inheritors = viewModel.inheritors.collectAsLazyPagingItems()
    InheritorsScreen(
        inheritors = inheritors,
        onInheritorSelected = onInheritorSelected,
        modifier = modifier,
    )
}

@Composable
fun InheritorsScreen(
    inheritors: LazyPagingItems<InheritorSummaryDto>,
    onInheritorSelected: (InheritorSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            InheritorsHeader(onRetry = inheritors::refresh)
        }

        when (val refreshState = inheritors.loadState.refresh) {
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
                    message = refreshState.error.message ?: stringResource(R.string.inheritors_load_failed),
                    actionLabel = stringResource(R.string.action_retry),
                    onAction = inheritors::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )
            }

            is LoadState.NotLoading -> {
                if (inheritors.itemCount == 0) {
                    item {
                        StatusContent(
                            title = stringResource(R.string.content_empty_title),
                            message = stringResource(R.string.inheritors_empty_message),
                            actionLabel = stringResource(R.string.action_refresh),
                            onAction = inheritors::refresh,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                        )
                    }
                }
            }
        }

        items(
            count = inheritors.itemCount,
            key = inheritors.itemKey { it.id ?: it.sourceUrl ?: it.name.orEmpty() },
        ) { index ->
            val inheritor = inheritors[index]
            if (inheritor != null) {
                InheritorRow(
                    inheritor = inheritor,
                    imageLoader = imageLoader,
                    onClick = { onInheritorSelected(inheritor) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }

        when (val appendState = inheritors.loadState.append) {
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
                    message = appendState.error.message ?: stringResource(R.string.inheritors_append_failed),
                    onRetry = inheritors::retry,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun InheritorsHeader(onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.inheritors_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.inheritors_subtitle),
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
private fun InheritorRow(
    inheritor: InheritorSummaryDto,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unnamedInheritor = stringResource(R.string.unnamed_inheritor)
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
                image = inheritor.coverImage,
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
                    text = inheritor.name.orEmpty().ifBlank { unnamedInheritor },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!inheritor.projectName.isNullOrBlank()) {
                    Text(
                        text = inheritor.projectName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                InheritorMetaRow(inheritor)
                if (!inheritor.description.isNullOrBlank()) {
                    Text(
                        text = inheritor.description,
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
private fun InheritorMetaRow(inheritor: InheritorSummaryDto) {
    val projectCode = inheritor.projectCode?.takeIf { it.isNotBlank() }?.let {
        stringResource(R.string.inheritor_project_code_format, it)
    }
    val labels = listOfNotNull(
        inheritor.gender?.takeIf { it.isNotBlank() },
        inheritor.ethnicity?.takeIf { it.isNotBlank() },
        inheritor.category?.takeIf { it.isNotBlank() },
        inheritor.region?.takeIf { it.isNotBlank() },
        projectCode,
        inheritor.batch?.takeIf { it.isNotBlank() },
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

@Preview(showBackground = true)
@Composable
private fun InheritorsScreenPreview() {
    HeritageTheme {}
}
