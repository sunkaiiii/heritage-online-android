package com.duckylife.heritage.modern.feature.search

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.FacetBucketDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultItemDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.SearchSuggestionDto
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritageSearchField
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

// ---------------------------------------------------------------------------
// Route
// ---------------------------------------------------------------------------

@Composable
fun SearchRoute(
    initialQuery: String,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(initialQuery) {
        viewModel.updateQuery(initialQuery)
        viewModel.search()
    }

    SearchScreen(
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::updateQuery,
        onSearch = viewModel::search,
        onSuggestionSelected = viewModel::selectSuggestion,
        onResultClick = { item ->
            val id = item.id ?: return@SearchScreen
            when (item.type) {
                "article" -> onArticleSelected(id)
                "directoryItem" -> onDirectoryItemSelected(id)
                "inheritor" -> onInheritorSelected(id)
            }
        },
        onLoadMore = viewModel::loadMore,
        onToggleType = viewModel::toggleType,
        onUpdateRegionFilter = viewModel::updateRegionFilter,
        onUpdateCategoryFilter = viewModel::updateCategoryFilter,
        onUpdateYearFilter = viewModel::updateYearFilter,
        onUpdateKindFilter = viewModel::updateKindFilter,
        onUpdateHasImageFilter = viewModel::updateHasImageFilter,
        onClearFilters = viewModel::clearFilters,
        onClearError = viewModel::clearError,
        modifier = modifier,
    )
}

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSuggestionSelected: (String) -> Unit,
    onResultClick: (SearchResultItemDto) -> Unit,
    onLoadMore: () -> Unit,
    onToggleType: (SearchResultType) -> Unit,
    onUpdateRegionFilter: (String) -> Unit,
    onUpdateCategoryFilter: (String) -> Unit,
    onUpdateYearFilter: (Int?) -> Unit,
    onUpdateKindFilter: (DirectoryItemKind?) -> Unit,
    onUpdateHasImageFilter: (Boolean?) -> Unit,
    onClearFilters: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar: back + search field
            SearchTopBar(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onBack = onBack,
                onFilterClick = { showFilterSheet = true },
                activeFilterCount = uiState.activeFilterCount,
            )

            // Suggestions dropdown
            AnimatedVisibility(visible = uiState.suggestions.isNotEmpty() && uiState.query.isNotBlank()) {
                SuggestionsList(
                    suggestions = uiState.suggestions,
                    onSuggestionSelected = onSuggestionSelected,
                )
            }

            // Active filter chips
            if (uiState.hasActiveFilters) {
                ActiveFiltersRow(
                    uiState = uiState,
                    onToggleType = onToggleType,
                    onClearRegion = { onUpdateRegionFilter("") },
                    onClearCategory = { onUpdateCategoryFilter("") },
                    onClearYear = { onUpdateYearFilter(null) },
                    onClearKind = { onUpdateKindFilter(null) },
                    onClearHasImage = { onUpdateHasImageFilter(null) },
                    onClearAll = onClearFilters,
                )
            }

            // Results area
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isSearching -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.errorKind != null && uiState.results.isEmpty() -> {
                        SearchErrorContent(
                            errorKind = uiState.errorKind,
                            onRetry = onSearch,
                            onClearError = onClearError,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.results.isEmpty() && uiState.query.isNotBlank() && !uiState.isSearching -> {
                        SearchEmptyContent(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.results.isNotEmpty() -> {
                        SearchResultsList(
                            results = uiState.results,
                            total = uiState.total,
                            isLoadingMore = uiState.isLoadingMore,
                            hasMore = uiState.hasMore,
                            onResultClick = onResultClick,
                            onLoadMore = onLoadMore,
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        SearchFilterSheet(
            uiState = uiState,
            onToggleType = onToggleType,
            onUpdateRegion = onUpdateRegionFilter,
            onUpdateCategory = onUpdateCategoryFilter,
            onUpdateYear = onUpdateYearFilter,
            onUpdateKind = onUpdateKindFilter,
            onUpdateHasImage = onUpdateHasImageFilter,
            onClearAll = onClearFilters,
            onDismiss = { showFilterSheet = false },
        )
    }
}

// ---------------------------------------------------------------------------
// Top bar
// ---------------------------------------------------------------------------

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    onFilterClick: () -> Unit,
    activeFilterCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
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
        HeritageSearchField(
            value = query,
            onValueChange = onQueryChange,
            onSearch = { onSearch() },
            label = stringResource(R.string.discovery_search_placeholder),
            placeholder = stringResource(R.string.discovery_search_placeholder),
            clearContentDescription = stringResource(R.string.action_clear_search),
            modifier = Modifier.weight(1f),
        )
        if (activeFilterCount > 0) {
            Badge(activeFilterCount)
        }
        IconButton(onClick = onFilterClick) {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = stringResource(R.string.filter_button),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Suggestions
// ---------------------------------------------------------------------------

@Composable
private fun SuggestionsList(
    suggestions: List<SearchSuggestionDto>,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column {
            suggestions.forEach { suggestion ->
                val text = suggestion.text.orEmpty()
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSuggestionSelected(text) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Active filters
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveFiltersRow(
    uiState: SearchUiState,
    onToggleType: (SearchResultType) -> Unit,
    onClearRegion: () -> Unit,
    onClearCategory: () -> Unit,
    onClearYear: () -> Unit,
    onClearKind: () -> Unit,
    onClearHasImage: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        uiState.selectedTypes.forEach { type ->
            FilterChip(
                selected = true,
                onClick = { onToggleType(type) },
                label = { Text(type.wireName) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        if (uiState.regionFilter.isNotBlank()) {
            FilterChip(
                selected = true,
                onClick = onClearRegion,
                label = { Text(uiState.regionFilter) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        if (uiState.categoryFilter.isNotBlank()) {
            FilterChip(
                selected = true,
                onClick = onClearCategory,
                label = { Text(uiState.categoryFilter) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        if (uiState.yearFilter != null) {
            FilterChip(
                selected = true,
                onClick = onClearYear,
                label = { Text(uiState.yearFilter.toString()) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        if (uiState.kindFilter != null) {
            FilterChip(
                selected = true,
                onClick = onClearKind,
                label = { Text(uiState.kindFilter.wireName) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        if (uiState.hasImageFilter != null) {
            FilterChip(
                selected = true,
                onClick = onClearHasImage,
                label = { Text(stringResource(if (uiState.hasImageFilter) R.string.filter_has_image else R.string.filter_no_image)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        TextButton(onClick = onClearAll) {
            Text(stringResource(R.string.filter_clear))
        }
    }
}

// ---------------------------------------------------------------------------
// Results list
// ---------------------------------------------------------------------------

@Composable
private fun SearchResultsList(
    results: List<SearchResultItemDto>,
    total: Long,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onResultClick: (SearchResultItemDto) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.search_results_count, total),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        items(
            items = results,
            key = { it.id ?: it.title.orEmpty() },
        ) { item ->
            SearchResultRow(
                item = item,
                onClick = { onResultClick(item) },
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

// ---------------------------------------------------------------------------
// Result row
// ---------------------------------------------------------------------------

@Composable
private fun SearchResultRow(
    item: SearchResultItemDto,
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val typeLabel = when (item.type) {
                    "article" -> stringResource(R.string.search_type_article)
                    "directoryItem" -> stringResource(R.string.search_type_directory)
                    "inheritor" -> stringResource(R.string.search_type_inheritor)
                    else -> item.type.orEmpty()
                }
                SearchTypeBadge(text = typeLabel)
                if (!item.category.isNullOrBlank()) {
                    SearchTypeBadge(text = item.category)
                }
                if (!item.kind.isNullOrBlank()) {
                    SearchTypeBadge(text = item.kind)
                }
            }
            Text(
                text = item.title.orEmpty().ifBlank { stringResource(R.string.unnamed_article) },
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
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (item.highlights.isNotEmpty()) {
                Text(
                    text = item.highlights.first(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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

@Composable
private fun SearchTypeBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ---------------------------------------------------------------------------
// Empty / Error states
// ---------------------------------------------------------------------------

@Composable
private fun SearchEmptyContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.search_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.search_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SearchErrorContent(
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

// ---------------------------------------------------------------------------
// Filter sheet
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SearchFilterSheet(
    uiState: SearchUiState,
    onToggleType: (SearchResultType) -> Unit,
    onUpdateRegion: (String) -> Unit,
    onUpdateCategory: (String) -> Unit,
    onUpdateYear: (Int?) -> Unit,
    onUpdateKind: (DirectoryItemKind?) -> Unit,
    onUpdateHasImage: (Boolean?) -> Unit,
    onClearAll: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = stringResource(R.string.filter_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            // Type filters
            if (uiState.facets?.types?.isNotEmpty() == true) {
                FilterSection(
                    title = stringResource(R.string.search_filter_type),
                    buckets = uiState.facets.types,
                    selectedKeys = uiState.selectedTypes.map { it.wireName }.toSet(),
                    onToggle = { key ->
                        val type = SearchResultType.entries.firstOrNull { it.wireName == key }
                        if (type != null) onToggleType(type)
                    },
                )
            }

            // Category filters
            if (uiState.facets?.categories?.isNotEmpty() == true) {
                FilterSection(
                    title = stringResource(R.string.filter_field_category),
                    buckets = uiState.facets.categories,
                    selectedKeys = setOfNotNull(uiState.categoryFilter.takeIf { it.isNotBlank() }),
                    onToggle = { key ->
                        onUpdateCategory(if (uiState.categoryFilter == key) "" else key)
                    },
                )
            }

            // Region filters
            if (uiState.facets?.regions?.isNotEmpty() == true) {
                FilterSection(
                    title = stringResource(R.string.filter_field_region),
                    buckets = uiState.facets.regions,
                    selectedKeys = setOfNotNull(uiState.regionFilter.takeIf { it.isNotBlank() }),
                    onToggle = { key ->
                        onUpdateRegion(if (uiState.regionFilter == key) "" else key)
                    },
                )
            }

            // Kind filters
            if (uiState.facets?.kinds?.isNotEmpty() == true) {
                FilterSection(
                    title = stringResource(R.string.search_filter_kind),
                    buckets = uiState.facets.kinds,
                    selectedKeys = setOfNotNull(uiState.kindFilter?.wireName),
                    onToggle = { key ->
                        val kind = DirectoryItemKind.entries.firstOrNull { it.wireName == key }
                        onUpdateKind(if (uiState.kindFilter == kind) null else kind)
                    },
                )
            }

            // Year filters
            if (uiState.facets?.years?.isNotEmpty() == true) {
                FilterSection(
                    title = stringResource(R.string.filter_field_year),
                    buckets = uiState.facets.years,
                    selectedKeys = setOfNotNull(uiState.yearFilter?.toString()),
                    onToggle = { key ->
                        val year = key.toIntOrNull()
                        onUpdateYear(if (uiState.yearFilter == year) null else year)
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onClearAll) {
                    Text(stringResource(R.string.filter_clear))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.filter_apply))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(
    title: String,
    buckets: List<FacetBucketDto>,
    selectedKeys: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            buckets.forEach { bucket ->
                val key = bucket.key ?: return@forEach
                FilterChip(
                    selected = key in selectedKeys,
                    onClick = { onToggle(key) },
                    label = {
                        Text("$key (${bucket.count})")
                    },
                )
            }
        }
    }
}

@Composable
private fun Badge(count: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
    ) {
        Text(
            text = count.toString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
        )
    }
}
