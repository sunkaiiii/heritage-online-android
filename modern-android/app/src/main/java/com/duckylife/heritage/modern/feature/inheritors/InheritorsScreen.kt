package com.duckylife.heritage.modern.feature.inheritors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.ui.component.HeritageFilterButton
import com.duckylife.heritage.modern.ui.component.HeritageListCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSearchField
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
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateListOf<Any>(InheritorsList) }
    val isInDetail = backStack.lastOrNull() != InheritorsList
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InheritorsListRoute(
    onInheritorSelected: (InheritorSummaryDto) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InheritorsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val inheritors = viewModel.inheritors.collectAsLazyPagingItems()
    var showFilterSheet by remember { mutableStateOf(false) }
    InheritorsScreen(
        uiState = uiState,
        inheritors = inheritors,
        showFilterSheet = showFilterSheet,
        onSearchKeywordsChanged = viewModel::updateSearchKeywords,
        onFilterClick = { showFilterSheet = true },
        onFilterDismiss = { showFilterSheet = false },
        onRegionFilterChanged = viewModel::updateRegionFilter,
        onCategoryFilterChanged = viewModel::updateCategoryFilter,
        onYearFilterChanged = viewModel::updateYearFilter,
        onGenderFilterChanged = viewModel::updateGenderFilter,
        onClearFilters = viewModel::clearFilters,
        onInheritorSelected = onInheritorSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InheritorsScreen(
    uiState: InheritorsUiState,
    inheritors: LazyPagingItems<InheritorSummaryDto>,
    showFilterSheet: Boolean,
    onSearchKeywordsChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    onFilterDismiss: () -> Unit,
    onRegionFilterChanged: (String) -> Unit,
    onCategoryFilterChanged: (String) -> Unit,
    onYearFilterChanged: (String) -> Unit,
    onGenderFilterChanged: (String) -> Unit,
    onClearFilters: () -> Unit,
    onInheritorSelected: (InheritorSummaryDto) -> Unit,
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
                InheritorsHeader(
                    activeFilterCount = uiState.activeFilterCount,
                    onFilterClick = onFilterClick,
                    onRetry = inheritors::refresh,
                )
            }

            item {
                HeritageSearchField(
                    value = uiState.searchKeywords,
                    onValueChange = onSearchKeywordsChanged,
                    label = stringResource(R.string.inheritors_search_label),
                    placeholder = stringResource(R.string.inheritors_search_placeholder),
                    clearContentDescription = stringResource(R.string.action_clear_search),
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
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
                                message = stringResource(
                                    if (uiState.searchKeywords.isBlank()) {
                                        R.string.inheritors_empty_message
                                    } else {
                                        R.string.inheritors_search_empty_message
                                    },
                                ),
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
    if (showFilterSheet) {
        InheritorFilterSheet(
            regionFilter = uiState.regionFilter,
            categoryFilter = uiState.categoryFilter,
            yearFilter = uiState.yearFilter,
            genderFilter = uiState.genderFilter,
            onRegionFilterChanged = onRegionFilterChanged,
            onCategoryFilterChanged = onCategoryFilterChanged,
            onYearFilterChanged = onYearFilterChanged,
            onGenderFilterChanged = onGenderFilterChanged,
            onClear = {
                onClearFilters()
                onFilterDismiss()
            },
            onDismiss = onFilterDismiss,
        )
    }
}

@Composable
private fun InheritorsHeader(
    activeFilterCount: Int,
    onFilterClick: () -> Unit,
    onRetry: () -> Unit,
) {
    HeritagePageHeader(
        title = stringResource(R.string.inheritors_title),
        subtitle = stringResource(R.string.inheritors_subtitle),
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
private fun InheritorRow(
    inheritor: InheritorSummaryDto,
    imageLoader: ImageLoader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unnamedInheritor = stringResource(R.string.unnamed_inheritor)
    val fallbackText = stringResource(R.string.brand_fallback)
    val imageUrl = inheritor.coverImage?.displayUrl
        ?: inheritor.coverImage?.thumbnailUrl
        ?: inheritor.coverImage?.originalUrl
        ?: inheritor.coverImage?.sourceUrl
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
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InheritorMetaRow(inheritor: InheritorSummaryDto) {
    val labels = listOfNotNull(
        inheritor.category?.takeIf { it.isNotBlank() },
        inheritor.region?.takeIf { it.isNotBlank() },
        inheritor.batch?.takeIf { it.isNotBlank() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InheritorFilterSheet(
    regionFilter: String,
    categoryFilter: String,
    yearFilter: String,
    genderFilter: String,
    onRegionFilterChanged: (String) -> Unit,
    onCategoryFilterChanged: (String) -> Unit,
    onYearFilterChanged: (String) -> Unit,
    onGenderFilterChanged: (String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
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
                value = regionFilter,
                onValueChange = onRegionFilterChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.filter_field_region)) },
                placeholder = { Text("四川") },
                singleLine = true,
            )
            OutlinedTextField(
                value = categoryFilter,
                onValueChange = onCategoryFilterChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.filter_field_category)) },
                placeholder = { Text(stringResource(R.string.directory_field_category)) },
                singleLine = true,
            )
            OutlinedTextField(
                value = yearFilter,
                onValueChange = onYearFilterChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.filter_field_year)) },
                placeholder = { Text("2018") },
                singleLine = true,
            )
            Text(
                text = stringResource(R.string.filter_field_gender),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val genderOptions = listOf(
                stringResource(R.string.filter_gender_any),
                stringResource(R.string.filter_gender_male),
                stringResource(R.string.filter_gender_female),
            )
            val selectedIndex = when (genderFilter) {
                "男" -> 1
                "女" -> 2
                else -> 0
            }
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                genderOptions.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = index == selectedIndex,
                        onClick = {
                            val value = when (index) {
                                1 -> "男"
                                2 -> "女"
                                else -> ""
                            }
                            onGenderFilterChanged(value)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = genderOptions.size,
                        ),
                    ) {
                        Text(label)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onClear) {
                    Text(stringResource(R.string.filter_clear))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InheritorsScreenPreview() {
    HeritageTheme {}
}
