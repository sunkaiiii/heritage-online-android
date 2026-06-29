package com.duckylife.heritage.modern.feature.inheritors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
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
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
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
import com.duckylife.heritage.modern.ui.component.mainTabContentPadding
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSearchField
import com.duckylife.heritage.modern.ui.error.toUiError
import com.duckylife.heritage.modern.ui.text.InheritorGender
import com.duckylife.heritage.modern.ui.text.contentTypeFallbackText
import com.duckylife.heritage.modern.ui.text.localizedInheritorBatch
import com.duckylife.heritage.modern.ui.text.localizedInheritorGender
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

@Composable
fun InheritorsRoute(
    onSecondaryDestinationChanged: (Boolean) -> Unit,
    onKeywordSearch: (String) -> Unit = {},
    onGraphExploreSelected: (contentType: String, contentId: String, initialTabName: String) -> Unit = { _, _, _ -> },
    onLearningRoutesSelected: (seedType: String?, seedId: String?) -> Unit = { _, _ -> },
    pendingNavigation: MyPageDestination.Inheritor? = null,
    onPendingNavigationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var savedStack by rememberSaveable { mutableStateOf("") }
    val backStack = remember {
        mutableStateListOf<Any>().also { it.addAll(deserializeInheritorsRoutes(savedStack)) }
    }
    LaunchedEffect(backStack.toList()) {
        savedStack = serializeInheritorsRoutes(backStack.filterIsInstance<InheritorsRouteKey>())
    }
    val popBackStack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else if (backStack.isEmpty()) {
            backStack.add(InheritorsRouteKey.InheritorsList)
        }
    }
    val isInDetail = backStack.isNotEmpty() && backStack.lastOrNull() !is InheritorsRouteKey.InheritorsList
    LaunchedEffect(isInDetail) {
        onSecondaryDestinationChanged(isInDetail)
    }
    LaunchedEffect(pendingNavigation) {
        val dest = pendingNavigation ?: return@LaunchedEffect
        backStack.clear()
        backStack.add(InheritorsRouteKey.InheritorsList)
        backStack.add(
            InheritorsRouteKey.InheritorDetail(
                id = dest.inheritorId,
                sourceId = dest.sourceId,
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
                is InheritorsRouteKey.InheritorsList -> NavEntry(entryKey) {
                    InheritorsListRoute(
                        onInheritorSelected = { inheritor ->
                            backStack.add(
                                InheritorsRouteKey.InheritorDetail(
                                    id = inheritor.id,
                                ),
                            )
                        },
                        modifier = modifier,
                    )
                }

                is InheritorsRouteKey.InheritorDetail -> NavEntry(entryKey) {
                    InheritorDetailRoute(
                        inheritorId = key.id,
                        sourceId = key.sourceId,
                        onBack = popBackStack,
                        onRelatedProjectSelected = { reference ->
                            when {
                                reference.isInheritorReference -> reference.toInheritorDetail()?.let(backStack::add)
                                else -> reference.toDirectoryDetail()?.let(backStack::add)
                            }
                        },
                        onRelatedInheritorSelected = { reference ->
                            reference.toInheritorDetail()?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateInheritorContextTarget(click.target, backStack)
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

                is InheritorsRouteKey.InheritorDirectoryDetail -> NavEntry(entryKey) {
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
                            navigateInheritorContextTarget(click.target, backStack)
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

                is InheritorsRouteKey.InheritorTabArticleDetail -> NavEntry(entryKey) {
                    ArticleDetailRoute(
                        articleId = key.id,
                        sourceId = key.sourceId,
                        sourceUrl = key.sourceUrl,
                        category = key.category,
                        onBack = popBackStack,
                        onRelatedArticleSelected = { reference, category ->
                            reference.toInheritorTabArticleDetail(category)?.let(backStack::add)
                        },
                        onExploreTargetClick = { click ->
                            navigateInheritorContextTarget(click.target, backStack)
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

                is InheritorsRouteKey.InheritorTabCollectionDetail -> NavEntry(entryKey) {
                    CollectionRoute(
                        id = key.id,
                        type = null,
                        topicKey = null,
                        onBack = popBackStack,
                        onArticleSelected = { id ->
                            backStack.add(InheritorsRouteKey.InheritorTabArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(InheritorsRouteKey.InheritorDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(InheritorsRouteKey.InheritorDetail(id = id))
                        },
                        modifier = modifier,
                    )
                }

                is InheritorsRouteKey.InheritorTabTopicDetail -> NavEntry(entryKey) {
                    ExploreTopicRoute(
                        type = key.type,
                        key = key.key,
                        onBack = popBackStack,
                        onArticleSelected = { id ->
                            backStack.add(InheritorsRouteKey.InheritorTabArticleDetail(id = id))
                        },
                        onDirectoryItemSelected = { id ->
                            backStack.add(InheritorsRouteKey.InheritorDirectoryDetail(id = id))
                        },
                        onInheritorSelected = { id ->
                            backStack.add(InheritorsRouteKey.InheritorDetail(id = id))
                        },
                        onRelatedTopicSelected = { type, topicKey ->
                            backStack.add(InheritorsRouteKey.InheritorTabTopicDetail(type = type, key = topicKey))
                        },
                        modifier = modifier,
                    )
                }

                else -> NavEntry(Unit) {
                    InheritorsListRoute(
                        onInheritorSelected = { inheritor ->
                            backStack.add(
                                InheritorsRouteKey.InheritorDetail(
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

// Context 目标导航 mapper
private val inheritorContextMapper = DetailContextRouteMapper<Any>(
    article = { InheritorsRouteKey.InheritorTabArticleDetail(id = it) },
    directoryItem = { InheritorsRouteKey.InheritorDirectoryDetail(id = it) },
    inheritor = { InheritorsRouteKey.InheritorDetail(id = it) },
    collection = { InheritorsRouteKey.InheritorTabCollectionDetail(id = it) },
    topic = { type, key -> InheritorsRouteKey.InheritorTabTopicDetail(type = type, key = key) },
)

private fun navigateInheritorContextTarget(
    target: DetailContextTarget,
    backStack: MutableList<Any>,
) {
    backStack.add(inheritorContextMapper.map(target))
}

private val InheritorsRouteKey.InheritorDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private val InheritorsRouteKey.InheritorDirectoryDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private val InheritorsRouteKey.InheritorTabArticleDetail.idOrSourceId: String?
    get() = id?.takeIf { it.isNotBlank() } ?: sourceId?.takeIf { it.isNotBlank() }

private fun DirectoryReferenceDto.toDirectoryDetail(
    fallbackKind: DirectoryItemKind = DirectoryItemKind.NationalProject,
): InheritorsRouteKey.InheritorDirectoryDetail? {
    if (sourceId.isNullOrBlank() || isInheritorReference) {
        return null
    }
    return InheritorsRouteKey.InheritorDirectoryDetail(
        sourceId = sourceId,
        kind = kind.toDirectoryItemKindOrNull() ?: fallbackKind,
    )
}

private fun DirectoryReferenceDto.toInheritorDetail(): InheritorsRouteKey.InheritorDetail? {
    if (sourceId.isNullOrBlank()) {
        return null
    }
    return InheritorsRouteKey.InheritorDetail(sourceId = sourceId)
}

private fun com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto.toInheritorTabArticleDetail(
    category: ArticleCategory,
): InheritorsRouteKey.InheritorTabArticleDetail? =
    when {
        !sourceId.isNullOrBlank() -> InheritorsRouteKey.InheritorTabArticleDetail(
            sourceId = sourceId,
            category = category,
        )
        !detailUrl.isNullOrBlank() -> InheritorsRouteKey.InheritorTabArticleDetail(
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
        onApplyFilters = viewModel::applyFilters,
        onClearFilterField = viewModel::clearFilterField,
        onClearFilters = viewModel::clearFilters,
        onClearAdvancedFilters = viewModel::clearAdvancedFilters,
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
    onApplyFilters: (String, String, String, String) -> Unit,
    onClearFilterField: (InheritorFilterField) -> Unit,
    onClearFilters: () -> Unit,
    onClearAdvancedFilters: () -> Unit,
    onInheritorSelected: (InheritorSummaryDto) -> Unit,
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

            val activeFilters = listOfNotNull(
                uiState.regionFilter.takeIf { it.isNotBlank() }?.let { InheritorFilterField.Region to it },
                uiState.categoryFilter.takeIf { it.isNotBlank() }?.let { InheritorFilterField.Category to it },
                uiState.yearFilter.takeIf { it.isNotBlank() }?.let { InheritorFilterField.Year to it },
                uiState.genderFilter.takeIf { it.isNotBlank() }?.let { InheritorFilterField.Gender to it },
            )
            if (activeFilters.isNotEmpty()) {
                item {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        activeFilters.forEach { (field, value) ->
                            FilterChip(
                                selected = true,
                                onClick = { onClearFilterField(field) },
                                label = {
                                    val displayValue = if (field == InheritorFilterField.Gender) {
                                        localizedInheritorGender(value)
                                    } else {
                                        value
                                    }
                                    Text(stringResource(field.labelRes) + ": " + displayValue)
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

            when (val refreshState = inheritors.loadState.refresh) {
                is LoadState.Loading -> item {
                    LoadingContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                    )
                }

                is LoadState.Error -> item {
                    val inhRefreshErrRes = refreshState.error.toUiError().fallbackResId
                    StatusContent(
                        title = stringResource(R.string.content_load_failed),
                        message = stringResource(inhRefreshErrRes),
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
                    val inhAppendErrRes = appendState.error.toUiError().fallbackResId
                    InlineRetryMessage(
                        message = stringResource(inhAppendErrRes),
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
            initialRegion = uiState.regionFilter,
            initialCategory = uiState.categoryFilter,
            initialYear = uiState.yearFilter,
            initialGender = uiState.genderFilter,
            onApply = { region, category, year, gender ->
                onApplyFilters(region, category, year, gender)
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
    val fallbackText = contentTypeFallbackText("inheritor")
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
        inheritor.batch?.let { localizedInheritorBatch(it) },
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
    initialRegion: String,
    initialCategory: String,
    initialYear: String,
    initialGender: String,
    onApply: (String, String, String, String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var draftRegion by rememberSaveable { mutableStateOf(initialRegion) }
    var draftCategory by rememberSaveable { mutableStateOf(initialCategory) }
    var draftYear by rememberSaveable { mutableStateOf(initialYear) }
    var draftGender by rememberSaveable { mutableStateOf(initialGender) }
    val yearError = draftYear.isNotBlank() && draftYear.length != 4 || draftYear.isNotBlank() && draftYear.toIntOrNull() == null
    val canApply = !yearError

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
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
            Text(
                text = stringResource(R.string.filter_field_gender),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val genderOptions = InheritorGender.entries
            val selectedIndex = genderOptions.indexOfFirst { it.wireValue == draftGender }.takeIf { it >= 0 } ?: 0
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                genderOptions.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = index == selectedIndex,
                        onClick = { draftGender = option.wireValue },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = genderOptions.size,
                        ),
                    ) {
                        Text(stringResource(option.labelRes))
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onClear) {
                    Text(stringResource(R.string.filter_clear))
                }
                Button(onClick = { onApply(draftRegion, draftCategory, draftYear, draftGender) }, enabled = canApply) {
                    Text(stringResource(R.string.filter_apply))
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
