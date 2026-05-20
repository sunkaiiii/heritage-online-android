package com.duckylife.heritage.modern.feature.directory

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.feature.my.MyPageDestination
import com.duckylife.heritage.modern.ui.component.HeritageFilterButton
import com.duckylife.heritage.modern.ui.component.HeritageListCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSearchField
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

private data object DirectoryList

private data class DirectoryDetail(
    val id: String? = null,
    val sourceId: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
)

private data class DirectoryInheritorDetail(
    val id: String? = null,
    val sourceId: String? = null,
)

private fun serializeDirectory(stack: List<Any>): String =
    stack.joinToString("\n") { entry ->
        when (entry) {
            DirectoryList -> "L"
            is DirectoryDetail -> "D|${entry.id.orEmpty()}|${entry.sourceId.orEmpty()}|${entry.kind.wireName}"
            is DirectoryInheritorDetail -> "I|${entry.id.orEmpty()}|${entry.sourceId.orEmpty()}"
            else -> "L"
        }
    }

private fun deserializeDirectory(str: String): List<Any> =
    if (str.isBlank()) listOf(DirectoryList)
    else str.split("\n").mapNotNull { item ->
        val parts = item.split("|")
        when (parts[0]) {
            "D" -> DirectoryDetail(
                id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() },
                sourceId = parts.getOrNull(2)?.takeIf { it.isNotEmpty() },
                kind = DirectoryItemKind.entries.firstOrNull { it.wireName == parts.getOrNull(3) } ?: DirectoryItemKind.NationalProject,
            )
            "I" -> DirectoryInheritorDetail(
                id = parts.getOrNull(1)?.takeIf { it.isNotEmpty() },
                sourceId = parts.getOrNull(2)?.takeIf { it.isNotEmpty() },
            )
            else -> DirectoryList
        }
    }

@Composable
fun DirectoryRoute(
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    pendingNavigation: MyPageDestination.Directory? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var savedStack by rememberSaveable { mutableStateOf("L") }
    val backStack = remember { mutableStateListOf<Any>().also { it.addAll(deserializeDirectory(savedStack)) } }
    LaunchedEffect(backStack.size) {
        savedStack = serializeDirectory(backStack.toList())
    }
    val isInDetail = backStack.lastOrNull() != DirectoryList
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
    }
    LaunchedEffect(pendingNavigation) {
        val dest = pendingNavigation ?: return@LaunchedEffect
        backStack.clear()
        backStack.add(DirectoryList)
        backStack.add(
            DirectoryDetail(
                id = dest.itemId,
                sourceId = dest.sourceId,
                kind = dest.kind,
            ),
        )
        onPendingNavigationConsumed()
    }
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

                is DirectoryInheritorDetail -> NavEntry(key) {
                    InheritorDetailRoute(
                        inheritorId = key.id,
                        sourceId = key.sourceId,
                        onBack = { backStack.removeLastOrNull() },
                        onRelatedProjectSelected = { reference ->
                            when {
                                reference.isInheritorReference -> reference.toInheritorDetail()?.let(backStack::add)
                                else -> reference.toDirectoryDetail(DirectoryItemKind.NationalProject)?.let(backStack::add)
                            }
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toInheritorDetail()?.let(backStack::add)
                        },
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

private fun DirectoryReferenceDto.toDirectoryDetail(fallbackKind: DirectoryItemKind): DirectoryDetail? {
    if (sourceId.isNullOrBlank() || isInheritorReference) {
        return null
    }
    return DirectoryDetail(
        sourceId = sourceId,
        kind = kind.toDirectoryItemKindOrNull() ?: fallbackKind,
    )
}

private fun DirectoryReferenceDto.toInheritorDetail(): DirectoryInheritorDetail? {
    if (sourceId.isNullOrBlank()) {
        return null
    }
    return DirectoryInheritorDetail(sourceId = sourceId)
}

private fun String?.toDirectoryItemKindOrNull(): DirectoryItemKind? =
    DirectoryItemKind.entries.firstOrNull { it.wireName == this }

// 后端有些 relatedProjects 实际指向 /ccr_detail/，这是 ihchina 的传承人页面。
// URL 形态比偶尔误导的 kind 字段更可靠。
private val DirectoryReferenceDto.isInheritorReference: Boolean
    get() = kind.equals("inheritor", ignoreCase = true) ||
        detailUrl?.contains("/ccr_detail/", ignoreCase = true) == true

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DirectoryListRoute(
    onItemSelected: (DirectoryItemSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DirectoryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val items = viewModel.items.collectAsLazyPagingItems()
    var showFilterSheet by remember { mutableStateOf(false) }
    DirectoryScreen(
        uiState = uiState,
        items = items,
        showFilterSheet = showFilterSheet,
        onKindSelected = viewModel::selectKind,
        onSearchKeywordsChanged = viewModel::updateSearchKeywords,
        onFilterClick = { showFilterSheet = true },
        onFilterDismiss = { showFilterSheet = false },
        onApplyFilters = viewModel::applyFilters,
        onClearFilterField = viewModel::clearFilterField,
        onClearFilters = viewModel::clearFilters,
        onItemSelected = onItemSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    uiState: DirectoryUiState,
    items: LazyPagingItems<DirectoryItemSummaryDto>,
    showFilterSheet: Boolean,
    onKindSelected: (DirectoryItemKind) -> Unit,
    onSearchKeywordsChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    onFilterDismiss: () -> Unit,
    onApplyFilters: (String, String, String, String) -> Unit,
    onClearFilterField: (DirectoryFilterField) -> Unit,
    onClearFilters: () -> Unit,
    onItemSelected: (DirectoryItemSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                DirectoryHeader(
                    activeFilterCount = uiState.activeFilterCount,
                    onFilterClick = onFilterClick,
                    onRetry = items::refresh,
                )
            }

            item {
                HeritageSearchField(
                    value = uiState.searchKeywords,
                    onValueChange = onSearchKeywordsChanged,
                    label = stringResource(R.string.directory_search_label),
                    placeholder = stringResource(R.string.directory_search_placeholder),
                    clearContentDescription = stringResource(R.string.action_clear_search),
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            val activeFilters = listOfNotNull(
                uiState.regionFilter.takeIf { it.isNotBlank() }?.let { DirectoryFilterField.Region to it },
                uiState.categoryFilter.takeIf { it.isNotBlank() }?.let { DirectoryFilterField.Category to it },
                uiState.yearFilter.takeIf { it.isNotBlank() }?.let { DirectoryFilterField.Year to it },
                uiState.listTypeFilter.takeIf { it.isNotBlank() }?.let { DirectoryFilterField.ListType to it },
            )
            if (activeFilters.isNotEmpty()) {
                item {
                    ActiveFilterChipsRow(modifier = Modifier.padding(horizontal = 20.dp)) {
                        activeFilters.forEach { (field, value) ->
                            FilterChip(
                                selected = true,
                                onClick = { onClearFilterField(field) },
                                label = {
                                    Text(stringResource(field.labelRes) + ": " + value)
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = stringResource(R.string.action_clear_search),
                                        modifier = Modifier.size(16.dp),
                                    )
                                },
                            )
                        }
                    }
                }
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
                                message = stringResource(
                                    if (uiState.searchKeywords.isBlank()) {
                                        R.string.directory_empty_message
                                    } else {
                                        R.string.directory_search_empty_message
                                    },
                                ),
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
    if (showFilterSheet) {
        DirectoryFilterSheet(
            initialRegion = uiState.regionFilter,
            initialCategory = uiState.categoryFilter,
            initialYear = uiState.yearFilter,
            initialListType = uiState.listTypeFilter,
            onApply = { region, category, year, listType ->
                onApplyFilters(region, category, year, listType)
                onFilterDismiss()
            },
            onClear = {
                onClearFilters()
                onFilterDismiss()
            },
            onDismiss = onFilterDismiss,
        )
    }
}

@Composable
private fun DirectoryHeader(
    activeFilterCount: Int,
    onFilterClick: () -> Unit,
    onRetry: () -> Unit,
) {
    HeritagePageHeader(
        title = stringResource(R.string.directory_title),
        subtitle = stringResource(R.string.directory_subtitle),
    ) {
        HeritageFilterButton(
            activeFilterCount = activeFilterCount,
            onClick = onFilterClick,
            contentDescription = stringResource(R.string.filter_button),
        )
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
    val fallbackText = stringResource(R.string.brand_fallback)
    val imageUrl = item.coverImage?.displayUrl
        ?: item.coverImage?.thumbnailUrl
        ?: item.coverImage?.originalUrl
        ?: item.coverImage?.sourceUrl
    HeritageListCard(
        onClick = onClick,
        modifier = modifier,
        image = {
            HeritageListImage(
                imageUrl = imageUrl,
                imageLoader = imageLoader,
                fallbackText = fallbackText,
                modifier = Modifier
                    .size(width = 92.dp, height = 92.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
        },
        text = {
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
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DirectoryMetaRow(item: DirectoryItemSummaryDto) {
    val publishedYear = item.publishedYear?.let {
        stringResource(R.string.directory_year_format, it)
    }
    val labels = listOfNotNull(
        item.category?.takeIf { it.isNotBlank() },
        item.region?.takeIf { it.isNotBlank() },
        publishedYear,
    ).take(3)

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        labels.forEach { label ->
            HeritageMetaChip(text = label)
        }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DirectoryFilterSheet(
    initialRegion: String,
    initialCategory: String,
    initialYear: String,
    initialListType: String,
    onApply: (String, String, String, String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var draftRegion by remember { mutableStateOf(initialRegion) }
    var draftCategory by remember { mutableStateOf(initialCategory) }
    var draftYear by remember { mutableStateOf(initialYear) }
    var draftListType by remember { mutableStateOf(initialListType) }
    val yearError = draftYear.isNotBlank() && !isValidYear(draftYear)
    val canApply = !yearError

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.filter_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = draftRegion,
                onValueChange = { draftRegion = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.filter_field_region)) },
                placeholder = { Text("北京") },
                singleLine = true,
            )
            OutlinedTextField(
                value = draftCategory,
                onValueChange = { draftCategory = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.filter_field_category)) },
                placeholder = { Text(stringResource(R.string.directory_field_category)) },
                singleLine = true,
            )
            OutlinedTextField(
                value = draftYear,
                onValueChange = { draftYear = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.filter_field_year)) },
                placeholder = { Text("2006") },
                singleLine = true,
                isError = yearError,
                supportingText = if (yearError) {
                    { Text(stringResource(R.string.filter_invalid_year)) }
                } else {
                    null
                },
            )
            OutlinedTextField(
                value = draftListType,
                onValueChange = { draftListType = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.filter_field_list_type)) },
                placeholder = { Text("representative") },
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onClear) {
                    Text(stringResource(R.string.filter_clear))
                }
                Button(onClick = { onApply(draftRegion, draftCategory, draftYear, draftListType) }, enabled = canApply) {
                    Text(stringResource(R.string.filter_apply))
                }
            }
        }
    }
}

private fun isValidYear(value: String): Boolean =
    value.length == 4 && value.toIntOrNull() != null

@Composable
private fun ActiveFilterChipsRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun DirectoryScreenPreview() {
    HeritageTheme {}
}
