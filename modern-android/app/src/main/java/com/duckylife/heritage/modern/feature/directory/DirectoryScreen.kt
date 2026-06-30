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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.feature.articles.detail.ArticleDetailRoute
import com.duckylife.heritage.modern.feature.collections.CollectionRoute
import com.duckylife.heritage.modern.feature.detail.DetailContextRouteMapper
import com.duckylife.heritage.modern.feature.detail.DetailContextTarget
import com.duckylife.heritage.modern.feature.directory.detail.DirectoryDetailRoute
import com.duckylife.heritage.modern.feature.explore.ExploreTopicRoute
import com.duckylife.heritage.modern.feature.inheritors.detail.InheritorDetailRoute
import com.duckylife.heritage.modern.feature.my.MyPageDestination
import com.duckylife.heritage.modern.ui.component.HeritageFilterButton
import com.duckylife.heritage.modern.ui.component.HeritageListCard
import com.duckylife.heritage.modern.ui.component.horizontalFadingEdge
import com.duckylife.heritage.modern.ui.text.contentTypeFallbackText
import com.duckylife.heritage.modern.ui.component.mainTabContentPadding
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSearchField
import com.duckylife.heritage.modern.ui.error.toUiError
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

@Composable
fun DirectoryRoute(
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    onKeywordSearch: (String) -> Unit = {},
    onGraphExploreSelected: (contentType: String, contentId: String, initialTabName: String) -> Unit = { _, _, _ -> },
    onLearningRoutesSelected: (seedType: String?, seedId: String?) -> Unit = { _, _ -> },
    pendingNavigation: MyPageDestination.Directory? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var savedStack by rememberSaveable { mutableStateOf("") }
    val backStack = remember {
        mutableStateListOf<Any>().also { it.addAll(deserializeDirectoryRoutes(savedStack)) }
    }
    LaunchedEffect(backStack.toList()) {
        savedStack = serializeDirectoryRoutes(backStack.filterIsInstance<DirectoryRouteKey>())
    }
    val popBackStack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else if (backStack.isEmpty()) {
            backStack.add(DirectoryRouteKey.DirectoryList)
        }
    }
    val isInDetail = backStack.isNotEmpty() && backStack.lastOrNull() !is DirectoryRouteKey.DirectoryList
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
    }
    LaunchedEffect(pendingNavigation) {
        val dest = pendingNavigation ?: return@LaunchedEffect
        backStack.clear()
        backStack.add(DirectoryRouteKey.DirectoryList)
        backStack.add(
            DirectoryRouteKey.DirectoryDetail(
                id = dest.itemId,
                sourceId = dest.sourceId,
                kind = dest.kind,
            ),
        )
        onPendingNavigationConsumed()
    }
    NavDisplay(
        backStack = backStack,
        onBack = popBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { entryKey ->
            val key = entryKey
            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            when (key) {
                is DirectoryRouteKey.DirectoryList -> NavEntry(entryKey) {
                    DirectoryListRoute(
                        onItemSelected = { item ->
                            backStack.add(
                                DirectoryRouteKey.DirectoryDetail(
                                    id = item.id,
                                    kind = item.kind,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }

                is DirectoryRouteKey.DirectoryDetail -> NavEntry(entryKey) {
                    DirectoryDetailRoute(
                        itemId = key.id,
                        sourceId = key.sourceId,
                        kind = key.kind,
                        onBack = popBackStack,
                        onRelatedProjectSelected = { reference, fallbackKind ->
                            when {
                                reference.isInheritorReference -> reference.toInheritorDetail()?.let(backStack::add)
                                else -> reference.toDirectoryDetail(fallbackKind)?.let(backStack::add)
                            }
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toInheritorDetail()?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateDirectoryContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("directoryItem", it, "neighbors") }
                        },
                        onSimilarClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("directoryItem", it, "similar") }
                        },
                        onLearningRoutesClick = {
                            key.idOrSourceId?.let { onLearningRoutesSelected("content", "directoryItem:$it") }
                        },
                        onKeywordSearch = onKeywordSearch,
                        modifier = modifier,
                    )
                }

                is DirectoryRouteKey.DirectoryInheritorDetail -> NavEntry(entryKey) {
                    InheritorDetailRoute(
                        inheritorId = key.id,
                        sourceId = key.sourceId,
                        onBack = popBackStack,
                        onRelatedProjectSelected = { reference ->
                            when {
                                reference.isInheritorReference -> reference.toInheritorDetail()?.let(backStack::add)
                                else -> reference.toDirectoryDetail(DirectoryItemKind.NationalProject)?.let(backStack::add)
                            }
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toInheritorDetail()?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateDirectoryContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("inheritor", it, "neighbors") }
                        },
                        onSimilarClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("inheritor", it, "similar") }
                        },
                        onLearningRoutesClick = {
                            key.idOrSourceId?.let { onLearningRoutesSelected("content", "inheritor:$it") }
                        },
                        onKeywordSearch = onKeywordSearch,
                        modifier = modifier,
                    )
                }

                is DirectoryRouteKey.DirectoryTabArticleDetail -> NavEntry(entryKey) {
                    ArticleDetailRoute(
                        articleId = key.id,
                        sourceId = key.sourceId,
                        sourceUrl = key.sourceUrl,
                        category = key.category,
                        onBack = popBackStack,
                        onRelatedArticleSelected = { reference, category ->
                            reference.toDirectoryTabArticleDetail(category)?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateDirectoryContextTarget(click.target, backStack)
                        },
                        onGraphExploreClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("article", it, "neighbors") }
                        },
                        onSimilarClick = {
                            key.idOrSourceId?.let { onGraphExploreSelected("article", it, "similar") }
                        },
                        onLearningRoutesClick = {
                            key.idOrSourceId?.let { onLearningRoutesSelected("content", "article:$it") }
                        },
                        onKeywordSearch = onKeywordSearch,
                        modifier = modifier,
                    )
                }

                is DirectoryRouteKey.DirectoryTabCollectionDetail -> NavEntry(entryKey) {
                    CollectionRoute(
                        id = key.id,
                        type = null,
                        topicKey = null,
                        onBack = popBackStack,
                        onArticleSelected = { id ->
                            backStack.add(DirectoryRouteKey.DirectoryTabArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DirectoryRouteKey.DirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DirectoryRouteKey.DirectoryInheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                is DirectoryRouteKey.DirectoryTabTopicDetail -> NavEntry(entryKey) {
                    ExploreTopicRoute(
                        type = key.type,
                        key = key.key,
                        onBack = popBackStack,
                        onArticleSelected = { id ->
                            backStack.add(DirectoryRouteKey.DirectoryTabArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(DirectoryRouteKey.DirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(DirectoryRouteKey.DirectoryInheritorDetail(id = id))
                        },
                        onRelatedTopicSelected = { type, topicKey ->
                            backStack.add(DirectoryRouteKey.DirectoryTabTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    DirectoryListRoute(
                        onItemSelected = { item ->
                            backStack.add(
                                DirectoryRouteKey.DirectoryDetail(
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

// Context 目标导航 mapper
private val directoryContextMapper = DetailContextRouteMapper<Any>(
    article = { DirectoryRouteKey.DirectoryTabArticleDetail(id = it) },
    directoryItem = { DirectoryRouteKey.DirectoryDetail(id = it) },
    inheritor = { DirectoryRouteKey.DirectoryInheritorDetail(id = it) },
    collection = { DirectoryRouteKey.DirectoryTabCollectionDetail(id = it) },
    topic = { type, key -> DirectoryRouteKey.DirectoryTabTopicDetail(type = type, key = key) },
)

private fun navigateDirectoryContextTarget(
    target: DetailContextTarget,
    backStack: MutableList<Any>,
) {
    backStack.add(directoryContextMapper.map(target))
}

private val DirectoryRouteKey.DirectoryDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private val DirectoryRouteKey.DirectoryInheritorDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private val DirectoryRouteKey.DirectoryTabArticleDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private fun DirectoryReferenceDto.toDirectoryDetail(fallbackKind: DirectoryItemKind): DirectoryRouteKey.DirectoryDetail? {
    if (sourceId.isNullOrBlank() || isInheritorReference) {
        return null
    }
    return DirectoryRouteKey.DirectoryDetail(
        sourceId = sourceId,
        kind = kind.toDirectoryItemKindOrNull() ?: fallbackKind,
    )
}

private fun DirectoryReferenceDto.toInheritorDetail(): DirectoryRouteKey.DirectoryInheritorDetail? {
    if (sourceId.isNullOrBlank()) {
        return null
    }
    return DirectoryRouteKey.DirectoryInheritorDetail(sourceId = sourceId)
}

private fun com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto.toDirectoryTabArticleDetail(
    category: ArticleCategory,
): DirectoryRouteKey.DirectoryTabArticleDetail? =
    when {
        !sourceId.isNullOrBlank() -> DirectoryRouteKey.DirectoryTabArticleDetail(
            sourceId = sourceId,
            category = category,
        )
        !detailUrl.isNullOrBlank() -> DirectoryRouteKey.DirectoryTabArticleDetail(
            sourceUrl = detailUrl,
            category = category,
        )
        else -> null
    }

private fun String?.toDirectoryItemKindOrNull(): DirectoryItemKind? =
    DirectoryItemKind.entries.firstOrNull { it.wireName == this }

// 后端有些 relatedProjects 实际指向 /ccr_detail/，这是 ihchina 的传承人页面。
// URL 形态比偶尔误导的 kind 字段更可靠。
private val DirectoryReferenceDto.isInheritorReference: Boolean
    get() = kind.equals("inheritor", ignoreCase = true) ||
        detailUrl?.contains("/ccr_detail/", ignoreCase = true) == true

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
        onTabSelected = viewModel::selectTab,
        onSearchKeywordsChanged = viewModel::updateSearchKeywords,
        onFilterClick = { showFilterSheet = true },
        onFilterDismiss = { showFilterSheet = false },
        onApplyFilters = viewModel::applyFilters,
        onClearFilterField = viewModel::clearFilterField,
        onClearFilters = viewModel::clearFilters,
        onClearAdvancedFilters = viewModel::clearAdvancedFilters,
        onItemSelected = onItemSelected,
        onStatisticsRefresh = viewModel::refreshStatistics,
        modifier = modifier,
    )
}

@Composable
fun DirectoryScreen(
    uiState: DirectoryUiState,
    items: LazyPagingItems<DirectoryItemSummaryDto>,
    showFilterSheet: Boolean,
    onKindSelected: (DirectoryItemKind) -> Unit,
    onTabSelected: (DirectoryTab) -> Unit,
    onSearchKeywordsChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    onFilterDismiss: () -> Unit,
    onApplyFilters: (String, String, String, String) -> Unit,
    onClearFilterField: (DirectoryFilterField) -> Unit,
    onClearFilters: () -> Unit,
    onClearAdvancedFilters: () -> Unit,
    onItemSelected: (DirectoryItemSummaryDto) -> Unit,
    onStatisticsRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = mainTabContentPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                DirectoryHeader(
                    activeFilterCount = uiState.activeFilterCount,
                    onFilterClick = onFilterClick,
                    onRetry = if (uiState.selectedTab == DirectoryTab.List) items::refresh else onStatisticsRefresh,
                )
            }

            item {
                DirectoryTabToggle(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            if (uiState.selectedTab == DirectoryTab.List) {
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
            }

            item {
                DirectoryKindFilters(
                    selectedKind = uiState.selectedKind,
                    onKindSelected = onKindSelected,
                )
            }

            if (uiState.selectedTab == DirectoryTab.Statistics) {
                item {
                    DirectoryStatisticsContent(
                        state = uiState.statisticsState,
                        selectedKind = uiState.selectedKind,
                        onRetry = onStatisticsRefresh,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                when (val refreshState = items.loadState.refresh) {
                    is LoadState.Loading -> item {
                        LoadingContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                        )
                    }

                    is LoadState.Error -> item {
                        val refreshErrRes = refreshState.error.toUiError().fallbackResId
                        StatusContent(
                            title = stringResource(R.string.content_load_failed),
                            message = stringResource(refreshErrRes),
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
                        val appendErrRes = appendState.error.toUiError().fallbackResId
                        InlineRetryMessage(
                            message = stringResource(appendErrRes),
                            onRetry = items::retry,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }

                    is LoadState.NotLoading -> Unit
                }
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
                onClearAdvancedFilters()
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
private fun DirectoryTabToggle(
    selectedTab: DirectoryTab,
    onTabSelected: (DirectoryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrimaryTabRow(
        selectedTabIndex = DirectoryTab.entries.indexOf(selectedTab),
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        DirectoryTab.entries.forEachIndexed { index, tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = { Text(stringResource(tab.labelRes)) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DirectoryKindFilters(
    selectedKind: DirectoryItemKind,
    onKindSelected: (DirectoryItemKind) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DirectoryItemKind.entries.forEach { kind ->
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
    val fallbackText = contentTypeFallbackText("directory")
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
    var draftRegion by rememberSaveable { mutableStateOf(initialRegion) }
    var draftCategory by rememberSaveable { mutableStateOf(initialCategory) }
    var draftYear by rememberSaveable { mutableStateOf(initialYear) }
    var draftListType by rememberSaveable { mutableStateOf(initialListType) }
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
                placeholder = { Text(stringResource(R.string.filter_placeholder_region)) },
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
                placeholder = { Text(stringResource(R.string.filter_placeholder_year)) },
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
                placeholder = { Text(stringResource(R.string.filter_placeholder_list_type)) },
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveFilterChipsRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun DirectoryScreenPreview() {
    HeritageTheme {}
}
