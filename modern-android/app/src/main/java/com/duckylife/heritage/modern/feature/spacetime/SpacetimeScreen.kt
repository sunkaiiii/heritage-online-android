package com.duckylife.heritage.modern.feature.spacetime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsBreakdownUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCompareItemUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCompareUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCrosstabUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsFacetBucketUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsFacetsUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsOutlierUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.NamedCountUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeFilters
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeHeatmapCellUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeHeatmapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeOverviewUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeRegionMapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeTimelineUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.YearCountUiModel
import com.duckylife.heritage.modern.ui.component.HeritageEmptyState
import com.duckylife.heritage.modern.ui.component.HeritageErrorState
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.text.localizedHeritageFacetLabel
import com.duckylife.heritage.modern.ui.state.AsyncState

private const val MAX_TIMELINE_ROWS = 20
private const val MAX_COMPARE_OPTIONS = 20

@Composable
fun SpacetimeRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpacetimeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SpacetimeScreen(
        uiState = uiState,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onSelectTab = viewModel::selectTab,
        onFiltersChanged = viewModel::updateFilters,
        onHeatmapDimensionsChanged = viewModel::setHeatmapDimensions,
        onRegionClick = { region -> viewModel.loadDrilldown(DrilldownState.RegionTimeline(region)) },
        onCategoryClick = { category -> viewModel.loadDrilldown(DrilldownState.CategoryTimeline(category)) },
        onYearClick = { year -> viewModel.loadDrilldown(DrilldownState.YearMap(year)) },
        onClearDrilldown = viewModel::clearDrilldown,
        onBreakdownDimensionChanged = viewModel::setBreakdownDimension,
        onCompareDimensionChanged = viewModel::setCompareDimension,
        onCompareKeyToggled = viewModel::toggleCompareKey,
        onCompareMetricChanged = viewModel::setCompareMetric,
        onRunCompare = viewModel::runCompare,
        onCrosstabDimensionsChanged = viewModel::setCrosstabDimensions,
        onRunCrosstab = viewModel::runCrosstab,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SpacetimeScreen(
    uiState: SpacetimeUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onFiltersChanged: (SpacetimeFilters) -> Unit,
    onHeatmapDimensionsChanged: (SpacetimeDimension, SpacetimeDimension) -> Unit,
    onRegionClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onYearClick: (Int) -> Unit,
    onClearDrilldown: () -> Unit,
    onBreakdownDimensionChanged: (AnalyticsDimension) -> Unit,
    onCompareDimensionChanged: (AnalyticsDimension) -> Unit,
    onCompareKeyToggled: (String) -> Unit,
    onCompareMetricChanged: (RankingMetric) -> Unit,
    onRunCompare: () -> Unit,
    onCrosstabDimensionsChanged: (AnalyticsDimension, AnalyticsDimension) -> Unit,
    onRunCrosstab: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val tabs = listOf(
        stringResource(R.string.spacetime_tab_overview),
        stringResource(R.string.spacetime_tab_analytics),
    )

    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    var showDrilldownSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.spacetime_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = stringResource(R.string.spacetime_filter_title),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },

    ) { padding ->
        HeritagePageBackground(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { onSelectTab(index) },
                            text = { Text(title) },
                        )
                    }
                }

                when (uiState.selectedTab) {
                    0 -> OverviewTab(
                        uiState = uiState,
                        onRefresh = onRefresh,
                        onHeatmapDimensionsChanged = onHeatmapDimensionsChanged,
                        onRegionClick = {
                            onRegionClick(it)
                            showDrilldownSheet = true
                        },
                        onCategoryClick = {
                            onCategoryClick(it)
                            showDrilldownSheet = true
                        },
                        onYearClick = {
                            onYearClick(it)
                            showDrilldownSheet = true
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                    else -> AnalyticsTab(
                        uiState = uiState,
                        onRefresh = onRefresh,
                        onBreakdownDimensionChanged = onBreakdownDimensionChanged,
                        onCompareDimensionChanged = onCompareDimensionChanged,
                        onCompareKeyToggled = onCompareKeyToggled,
                        onCompareMetricChanged = onCompareMetricChanged,
                        onRunCompare = onRunCompare,
                        onCrosstabDimensionsChanged = onCrosstabDimensionsChanged,
                        onRunCrosstab = onRunCrosstab,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        SpacetimeFilterSheet(
            filters = uiState.filters,
            onFiltersChanged = {
                onFiltersChanged(it)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false },
        )
    }

    if (showDrilldownSheet) {
        DrilldownSheet(
            drilldown = uiState.drilldown,
            regionMap = uiState.regionMap,
            onDismiss = {
                showDrilldownSheet = false
                onClearDrilldown()
            },
        )
    }
}

@Composable
private fun OverviewTab(
    uiState: SpacetimeUiState,
    onRefresh: () -> Unit,
    onHeatmapDimensionsChanged: (SpacetimeDimension, SpacetimeDimension) -> Unit,
    onRegionClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onYearClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val overview = uiState.overview
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when {
            overview.isLoading -> item { LoadingPlaceholder() }
            overview.errorKind != null -> item {
                HeritageErrorState(
                    errorKind = overview.errorKind,
                    onRetry = onRefresh,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            overview.data != null -> {
                item { MetricsRow(metrics = overview.data) }
                item {
                    TopNamedCountsSection(
                        title = stringResource(R.string.spacetime_top_regions_title),
                        items = overview.data.topRegions,
                        onItemClick = { onRegionClick(it.key) },
                    )
                }
                item {
                    TopNamedCountsSection(
                        title = stringResource(R.string.spacetime_top_categories_title),
                        items = overview.data.topCategories,
                        onItemClick = { onCategoryClick(it.key) },
                    )
                }
                if (overview.data.yearTimeline.isNotEmpty()) {
                    item {
                        YearTimelineSection(
                            title = stringResource(R.string.spacetime_year_timeline_title),
                            data = overview.data.yearTimeline,
                            onYearClick = onYearClick,
                            scrollable = false,
                        )
                    }
                }
                item {
                    HeatmapSection(
                        heatmap = uiState.heatmap,
                        x = uiState.heatmapX,
                        y = uiState.heatmapY,
                        onDimensionsChanged = onHeatmapDimensionsChanged,
                        onRefresh = onRefresh,
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricsRow(metrics: SpacetimeOverviewUiModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MetricCard(
            label = stringResource(R.string.spacetime_metrics_total),
            value = metrics.total.toString(),
            modifier = Modifier.weight(2f),
        )
        MetricCard(
            label = stringResource(R.string.content_type_article),
            value = metrics.articleCount.toString(),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = stringResource(R.string.content_type_directory),
            value = metrics.directoryItemCount.toString(),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = stringResource(R.string.content_type_inheritor),
            value = metrics.inheritorCount.toString(),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TopNamedCountsSection(
    title: String,
    items: List<NamedCountUiModel>,
    onItemClick: (NamedCountUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (items.isEmpty()) {
            HeritageEmptyState(message = stringResource(R.string.spacetime_empty_list))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.take(10).forEach { item ->
                    NamedCountBar(
                        item = item,
                        maxCount = items.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1,
                        onClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NamedCountBar(
    item: NamedCountUiModel,
    maxCount: Int,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val fraction = if (maxCount > 0) item.count.toFloat() / maxCount.toFloat() else 0f
    val label = localizedHeritageFacetLabel(item.label ?: item.key) ?: item.key
    Column(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = item.count.toString(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun YearTimelineSection(
    title: String,
    data: List<YearCountUiModel>,
    onYearClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
) {
    val maxCount = data.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Card {
            val contentModifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
            if (scrollable) {
                LazyColumn(
                    modifier = contentModifier.heightIn(max = (data.size.coerceAtMost(20) * 36).dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = data,
                        key = { it.year },
                    ) { yearCount ->
                        YearTimelineRow(
                            yearCount = yearCount,
                            maxCount = maxCount,
                            onYearClick = onYearClick,
                        )
                    }
                }
            } else {
                Column(
                    modifier = contentModifier,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    data.take(MAX_TIMELINE_ROWS).forEach { yearCount ->
                        YearTimelineRow(
                            yearCount = yearCount,
                            maxCount = maxCount,
                            onYearClick = onYearClick,
                        )
                    }
                    if (data.size > MAX_TIMELINE_ROWS) {
                        Text(
                            text = stringResource(
                                R.string.and_more_format,
                                data.size - MAX_TIMELINE_ROWS,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearTimelineRow(
    yearCount: YearCountUiModel,
    maxCount: Int,
    onYearClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = yearCount.count.toFloat() / maxCount.toFloat()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onYearClick(yearCount.year) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = yearCount.year.toString(),
            modifier = Modifier.width(48.dp),
            style = MaterialTheme.typography.bodySmall,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondary),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = yearCount.count.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun HeatmapSection(
    heatmap: AsyncState<SpacetimeHeatmapUiModel>,
    x: SpacetimeDimension,
    y: SpacetimeDimension,
    onDimensionsChanged: (SpacetimeDimension, SpacetimeDimension) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.spacetime_heatmap_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        HeatmapPresetChips(
            selectedX = x,
            selectedY = y,
            onSelected = onDimensionsChanged,
        )
        Spacer(modifier = Modifier.height(12.dp))
        when {
            heatmap.isLoading -> LoadingPlaceholder()
            heatmap.errorKind != null -> HeritageErrorState(
                errorKind = heatmap.errorKind,
                onRetry = onRefresh,
                modifier = Modifier.fillMaxWidth(),
            )

            heatmap.data != null -> HeatmapGrid(heatmap = heatmap.data)
        }
    }
}

@Composable
private fun HeatmapPresetChips(
    selectedX: SpacetimeDimension,
    selectedY: SpacetimeDimension,
    onSelected: (SpacetimeDimension, SpacetimeDimension) -> Unit,
    modifier: Modifier = Modifier,
) {
    val presets = listOf(
        Triple(SpacetimeDimension.Region, SpacetimeDimension.Category, R.string.spacetime_heatmap_preset_region_category),
        Triple(SpacetimeDimension.Region, SpacetimeDimension.Year, R.string.spacetime_heatmap_preset_region_year),
        Triple(SpacetimeDimension.Category, SpacetimeDimension.Year, R.string.spacetime_heatmap_preset_category_year),
        Triple(SpacetimeDimension.Kind, SpacetimeDimension.Region, R.string.spacetime_heatmap_preset_kind_region),
        Triple(SpacetimeDimension.Kind, SpacetimeDimension.Category, R.string.spacetime_heatmap_preset_kind_category),
    )
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        items(presets) { (px, py, labelRes) ->
            val selected = selectedX == px && selectedY == py
            FilterChip(
                selected = selected,
                onClick = { onSelected(px, py) },
                label = { Text(stringResource(labelRes)) },
            )
        }
    }
}

@Composable
private fun HeatmapGrid(heatmap: SpacetimeHeatmapUiModel, modifier: Modifier = Modifier) {
    if (heatmap.cells.isEmpty()) {
        HeritageEmptyState(message = stringResource(R.string.spacetime_heatmap_empty))
        return
    }
    val xKeys = heatmap.cells.map { it.xKey }.distinct()
    val yKeys = heatmap.cells.map { it.yKey }.distinct()
    val maxTotal = heatmap.cells.maxOfOrNull { it.total }?.coerceAtLeast(1) ?: 1
    val cellMap = remember(heatmap.cells) {
        heatmap.cells.associateBy { "${it.xKey}\u0000${it.yKey}" }
    }
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${stringResource(heatmap.x.labelRes())} × ${stringResource(heatmap.y.labelRes())}",
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                yKeys.forEach { yKey ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        xKeys.forEach { xKey ->
                            val cell = cellMap["$xKey\u0000$yKey"]
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (cell != null) {
                                            val intensity = (cell.total.toFloat() / maxTotal.toFloat())
                                                .coerceIn(0f, 1f)
                                            MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.2f + intensity * 0.8f,
                                            )
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = cell?.total?.toString() ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsTab(
    uiState: SpacetimeUiState,
    onRefresh: () -> Unit,
    onBreakdownDimensionChanged: (AnalyticsDimension) -> Unit,
    onCompareDimensionChanged: (AnalyticsDimension) -> Unit,
    onCompareKeyToggled: (String) -> Unit,
    onCompareMetricChanged: (RankingMetric) -> Unit,
    onRunCompare: () -> Unit,
    onCrosstabDimensionsChanged: (AnalyticsDimension, AnalyticsDimension) -> Unit,
    onRunCrosstab: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            BreakdownSection(
                breakdown = uiState.breakdown,
                selectedDimension = uiState.breakdownDimension,
                onDimensionChanged = onBreakdownDimensionChanged,
                onRetry = { onRefresh() },
            )
        }
        item {
            CompareSection(
                facets = uiState.facets.data,
                compare = uiState.compare,
                result = uiState.compareResult,
                onDimensionChanged = onCompareDimensionChanged,
                onKeyToggled = onCompareKeyToggled,
                onMetricChanged = onCompareMetricChanged,
                onRunCompare = onRunCompare,
            )
        }
        item {
            OutliersSection(
                outliers = uiState.outliers,
                onRetry = { onRefresh() },
            )
        }
        item {
            CrosstabSection(
                facets = uiState.facets.data,
                crosstab = uiState.crosstab,
                result = uiState.crosstabResult,
                onDimensionsChanged = onCrosstabDimensionsChanged,
                onRunCrosstab = onRunCrosstab,
            )
        }
    }
}

@Composable
private fun BreakdownSection(
    breakdown: AsyncState<AnalyticsBreakdownUiModel>,
    selectedDimension: AnalyticsDimension,
    onDimensionChanged: (AnalyticsDimension) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.analytics_breakdown_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        DimensionChipRow(
            dimensions = AnalyticsDimension.entries.filter { it != AnalyticsDimension.Unknown },
            selected = selectedDimension,
            onSelected = onDimensionChanged,
            labelResolver = { stringResource(it.labelRes()) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        when {
            breakdown.isLoading -> LoadingPlaceholder()
            breakdown.errorKind != null -> HeritageErrorState(
                errorKind = breakdown.errorKind,
                onRetry = onRetry,
                modifier = Modifier.fillMaxWidth(),
            )

            breakdown.data != null -> {
                if (breakdown.data.buckets.isEmpty()) {
                    HeritageEmptyState(message = stringResource(R.string.analytics_breakdown_empty))
                } else {
                    val max = breakdown.data.buckets.maxOfOrNull { it.total }?.coerceAtLeast(1) ?: 1
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        breakdown.data.buckets.take(15).forEach { bucket ->
                            NamedCountBar(
                                item = NamedCountUiModel(
                                    key = bucket.key,
                                    label = bucket.label ?: bucket.key,
                                    count = bucket.total,
                                ),
                                maxCount = max,
                                onClick = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompareSection(
    facets: AnalyticsFacetsUiModel?,
    compare: CompareSelectionState,
    result: AsyncState<AnalyticsCompareUiModel>,
    onDimensionChanged: (AnalyticsDimension) -> Unit,
    onKeyToggled: (String) -> Unit,
    onMetricChanged: (RankingMetric) -> Unit,
    onRunCompare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.analytics_compare_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        DimensionChipRow(
            dimensions = AnalyticsDimension.entries.filter { it != AnalyticsDimension.Unknown },
            selected = compare.dimension,
            onSelected = onDimensionChanged,
            labelResolver = { stringResource(it.labelRes()) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        MetricDropdown(
            selected = compare.metric,
            onSelected = onMetricChanged,
        )
        Spacer(modifier = Modifier.height(8.dp))
        val options = facets?.bucketsForDimension(compare.dimension) ?: emptyList()
        if (options.isNotEmpty()) {
            Text(
                text = stringResource(R.string.analytics_compare_limit_hint),
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                options.take(MAX_COMPARE_OPTIONS).forEach { bucket ->
                    val selected = bucket.key in compare.selectedKeys
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onKeyToggled(bucket.key) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = selected, onCheckedChange = { onKeyToggled(bucket.key) })
                        Text(
                            text = bucket.label ?: bucket.key,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = bucket.count.toString(),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                if (options.size > MAX_COMPARE_OPTIONS) {
                    Text(
                        text = stringResource(
                            R.string.and_more_format,
                            options.size - MAX_COMPARE_OPTIONS,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Button(
            onClick = onRunCompare,
            enabled = compare.selectedKeys.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.analytics_compare_button))
        }
        Spacer(modifier = Modifier.height(12.dp))
        when {
            result.isLoading -> LoadingPlaceholder()
            result.errorKind != null -> HeritageErrorState(
                errorKind = result.errorKind,
                onRetry = onRunCompare,
                modifier = Modifier.fillMaxWidth(),
            )

            result.data != null -> CompareResultCard(data = result.data)
        }
    }
}

@Composable
private fun CompareResultCard(data: AnalyticsCompareUiModel, modifier: Modifier = Modifier) {
    val maxValue = data.items.maxOfOrNull { it.value }?.coerceAtLeast(1.0) ?: 1.0
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            data.winnerKey?.let { winnerKey ->
                val winnerLabel = data.items.firstOrNull { it.key == winnerKey }?.label ?: winnerKey
                Text(
                    text = stringResource(R.string.analytics_compare_winner, winnerLabel),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.items.forEach { item ->
                    val fraction = (item.value / maxValue).toFloat().coerceIn(0f, 1f)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.label ?: item.key,
                            modifier = Modifier.width(80.dp),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.tertiary),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "%.1f".format(item.value),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(48.dp),
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OutliersSection(
    outliers: AsyncState<List<AnalyticsOutlierUiModel>>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.analytics_outliers_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        when {
            outliers.isLoading -> LoadingPlaceholder()
            outliers.errorKind != null -> HeritageErrorState(
                errorKind = outliers.errorKind,
                onRetry = onRetry,
                modifier = Modifier.fillMaxWidth(),
            )

            outliers.data != null -> {
                if (outliers.data.isEmpty()) {
                    HeritageEmptyState(message = stringResource(R.string.analytics_outliers_empty))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        outliers.data.take(10).forEach { outlier ->
                            OutlierCard(outlier = outlier)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OutlierCard(outlier: AnalyticsOutlierUiModel, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = outlier.label ?: outlier.key,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(outlier.metric.labelRes()),
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.analytics_outlier_ratio,
                    outlier.value,
                    outlier.average,
                    outlier.ratioToAverage,
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            outlier.reason?.let { reason ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CrosstabSection(
    facets: AnalyticsFacetsUiModel?,
    crosstab: CrosstabSelectionState,
    result: AsyncState<AnalyticsCrosstabUiModel>,
    onDimensionsChanged: (AnalyticsDimension, AnalyticsDimension) -> Unit,
    onRunCrosstab: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.analytics_crosstab_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DimensionDropdown(
                label = stringResource(R.string.analytics_crosstab_x),
                selected = crosstab.x,
                onSelected = { onDimensionsChanged(it, crosstab.y) },
                modifier = Modifier.weight(1f),
            )
            DimensionDropdown(
                label = stringResource(R.string.analytics_crosstab_y),
                selected = crosstab.y,
                onSelected = { onDimensionsChanged(crosstab.x, it) },
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRunCrosstab,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.analytics_crosstab_button))
        }
        Spacer(modifier = Modifier.height(12.dp))
        when {
            result.isLoading -> LoadingPlaceholder()
            result.errorKind != null -> HeritageErrorState(
                errorKind = result.errorKind,
                onRetry = onRunCrosstab,
                modifier = Modifier.fillMaxWidth(),
            )

            result.data != null -> CrosstabGrid(data = result.data)
        }
    }
}

@Composable
private fun CrosstabGrid(data: AnalyticsCrosstabUiModel, modifier: Modifier = Modifier) {
    if (data.cells.isEmpty()) {
        HeritageEmptyState(message = stringResource(R.string.analytics_crosstab_empty))
        return
    }
    val maxTotal = data.cells.maxOfOrNull { it.total }?.coerceAtLeast(1) ?: 1
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${stringResource(data.x.labelRes())} × ${stringResource(data.y.labelRes())}",
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            val xKeys = data.xBuckets.ifEmpty { data.cells.map { it.xKey }.distinct() }
            val yKeys = data.yBuckets.ifEmpty { data.cells.map { it.yKey }.distinct() }
            val cellMap = remember(data.cells) {
                data.cells.associateBy { "${it.xKey}\u0000${it.yKey}" }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                yKeys.forEach { yKey ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        xKeys.forEach { xKey ->
                            val cell = cellMap["$xKey\u0000$yKey"]
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (cell != null) {
                                            val alpha = 0.2f + (cell.total.toFloat() / maxTotal.toFloat()) * 0.8f
                                            MaterialTheme.colorScheme.primary.copy(alpha = alpha.coerceIn(0f, 1f))
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = cell?.total?.toString() ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpacetimeFilterSheet(
    filters: SpacetimeFilters,
    onFiltersChanged: (SpacetimeFilters) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var localFilters by remember { mutableStateOf(filters) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(bottom = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.spacetime_filter_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = localFilters.fromYear?.toString() ?: "",
                    onValueChange = {
                        localFilters = localFilters.copy(fromYear = it.toIntOrNull())
                    },
                    label = { Text(stringResource(R.string.spacetime_filter_from_year)) },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = localFilters.toYear?.toString() ?: "",
                    onValueChange = {
                        localFilters = localFilters.copy(toYear = it.toIntOrNull())
                    },
                    label = { Text(stringResource(R.string.spacetime_filter_to_year)) },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = localFilters.region ?: "",
                onValueChange = { localFilters = localFilters.copy(region = it.takeIf { it.isNotBlank() }) },
                label = { Text(stringResource(R.string.spacetime_filter_region)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = localFilters.category ?: "",
                onValueChange = { localFilters = localFilters.copy(category = it.takeIf { it.isNotBlank() }) },
                label = { Text(stringResource(R.string.spacetime_filter_category)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = localFilters.kind ?: "",
                onValueChange = { localFilters = localFilters.copy(kind = it.takeIf { it.isNotBlank() }) },
                label = { Text(stringResource(R.string.spacetime_filter_kind)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            val currentFilters = localFilters
            val valid = currentFilters.fromYear == null || currentFilters.toYear == null ||
                currentFilters.fromYear <= currentFilters.toYear
            if (!valid) {
                Text(
                    text = stringResource(R.string.spacetime_filter_invalid_year_range),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        localFilters = SpacetimeFilters()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.filter_clear))
                }
                Button(
                    onClick = { onFiltersChanged(localFilters) },
                    enabled = valid,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.filter_apply))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrilldownSheet(
    drilldown: AsyncState<SpacetimeTimelineUiModel>,
    regionMap: AsyncState<SpacetimeRegionMapUiModel>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            when {
                drilldown.isLoading || regionMap.isLoading -> LoadingPlaceholder()
                drilldown.errorKind != null -> HeritageErrorState(
                    errorKind = drilldown.errorKind,
                    onRetry = {},
                    modifier = Modifier.fillMaxWidth(),
                )

                regionMap.errorKind != null -> HeritageErrorState(
                    errorKind = regionMap.errorKind,
                    onRetry = {},
                    modifier = Modifier.fillMaxWidth(),
                )

                drilldown.data != null -> {
                    Text(
                        text = stringResource(R.string.spacetime_region_timeline_title, drilldown.data.label ?: drilldown.data.key),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    YearTimelineSection(
                        title = "",
                        data = drilldown.data.buckets,
                        onYearClick = {},
                    )
                }

                regionMap.data != null -> {
                    Text(
                        text = stringResource(R.string.spacetime_year_map_title, regionMap.data.year),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TopNamedCountsSection(
                        title = "",
                        items = regionMap.data.regions,
                        onItemClick = {},
                    )
                }

                else -> {
                    Text(
                        text = stringResource(R.string.spacetime_drilldown_empty),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun <T> DimensionChipRow(
    dimensions: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    labelResolver: @Composable (T) -> String,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(dimensions) { dimension ->
            FilterChip(
                selected = dimension == selected,
                onClick = { onSelected(dimension) },
                label = { Text(labelResolver(dimension)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DimensionDropdown(
    label: String,
    selected: AnalyticsDimension,
    onSelected: (AnalyticsDimension) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val dimensions = AnalyticsDimension.entries.filter { it != AnalyticsDimension.Unknown }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = stringResource(selected.labelRes()),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            dimensions.forEach { dimension ->
                DropdownMenuItem(
                    text = { Text(stringResource(dimension.labelRes())) },
                    onClick = {
                        onSelected(dimension)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricDropdown(
    selected: RankingMetric,
    onSelected: (RankingMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val metrics = RankingMetric.entries.filter { it != RankingMetric.Unknown }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = stringResource(selected.labelRes()),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.analytics_metric_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            metrics.forEach { metric ->
                DropdownMenuItem(
                    text = { Text(stringResource(metric.labelRes())) },
                    onClick = {
                        onSelected(metric)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun AnalyticsFacetsUiModel.bucketsForDimension(dimension: AnalyticsDimension): List<AnalyticsFacetBucketUiModel> =
    when (dimension) {
        AnalyticsDimension.Region -> regions
        AnalyticsDimension.Category -> categories
        AnalyticsDimension.Year -> years
        AnalyticsDimension.Kind -> kinds
        AnalyticsDimension.TargetType -> targetTypes
        AnalyticsDimension.Unknown -> emptyList()
    }

private fun SpacetimeDimension.labelRes(): Int = when (this) {
    SpacetimeDimension.Region -> R.string.dimension_region
    SpacetimeDimension.Category -> R.string.dimension_category
    SpacetimeDimension.Year -> R.string.dimension_year
    SpacetimeDimension.Kind -> R.string.dimension_kind
    SpacetimeDimension.Unknown -> R.string.dimension_unknown
}

private fun AnalyticsDimension.labelRes(): Int = when (this) {
    AnalyticsDimension.Region -> R.string.dimension_region
    AnalyticsDimension.Category -> R.string.dimension_category
    AnalyticsDimension.Year -> R.string.dimension_year
    AnalyticsDimension.Kind -> R.string.dimension_kind
    AnalyticsDimension.TargetType -> R.string.dimension_target_type
    AnalyticsDimension.Unknown -> R.string.dimension_unknown
}

private fun RankingMetric.labelRes(): Int = when (this) {
    RankingMetric.Total -> R.string.ranking_metric_total
    RankingMetric.ArticleCount -> R.string.ranking_metric_article_count
    RankingMetric.DirectoryItemCount -> R.string.ranking_metric_directory_count
    RankingMetric.InheritorCount -> R.string.ranking_metric_inheritor_count
    RankingMetric.Connectivity -> R.string.ranking_metric_connectivity
    RankingMetric.HiddenGem -> R.string.ranking_metric_hidden_gem
    RankingMetric.Completeness -> R.string.ranking_metric_completeness
    RankingMetric.ImageRichness -> R.string.ranking_metric_image_richness
    RankingMetric.AiCoverage -> R.string.ranking_metric_ai_coverage
    RankingMetric.Freshness -> R.string.ranking_metric_freshness
    RankingMetric.Unknown -> R.string.ranking_metric_unknown
}
