package com.duckylife.heritage.modern.feature.spacetime

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.DataExploreRepository
import com.duckylife.heritage.modern.core.network.AnalyticsFilters
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsBreakdownUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCompareUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCrosstabUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsFacetsUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsOutlierUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeFilters
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeHeatmapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeOverviewUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeRegionMapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeTimelineUiModel
import com.duckylife.heritage.modern.ui.error.toUiError
import com.duckylife.heritage.modern.ui.state.AsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_FILTERS = "spacetime_filters"
private const val KEY_SELECTED_TAB = "spacetime_selected_tab"
private const val KEY_HEATMAP_X = "spacetime_heatmap_x"
private const val KEY_HEATMAP_Y = "spacetime_heatmap_y"
private const val KEY_BREAKDOWN_DIMENSION = "spacetime_breakdown_dimension"
private const val KEY_COMPARE_DIMENSION = "spacetime_compare_dimension"
private const val KEY_COMPARE_KEYS = "spacetime_compare_keys"
private const val KEY_COMPARE_METRIC = "spacetime_compare_metric"
private const val KEY_CROSSTAB_X = "spacetime_crosstab_x"
private const val KEY_CROSSTAB_Y = "spacetime_crosstab_y"

/**
 * 时空探索详情 drilldown 状态。
 */
sealed interface DrilldownState {
    data object None : DrilldownState
    data class RegionTimeline(val region: String) : DrilldownState
    data class CategoryTimeline(val category: String) : DrilldownState
    data class YearMap(val year: Int) : DrilldownState
}

data class CompareSelectionState(
    val dimension: AnalyticsDimension = AnalyticsDimension.Region,
    val selectedKeys: List<String> = emptyList(),
    val metric: RankingMetric = RankingMetric.Total,
) : java.io.Serializable

data class CrosstabSelectionState(
    val x: AnalyticsDimension = AnalyticsDimension.Region,
    val y: AnalyticsDimension = AnalyticsDimension.Category,
) : java.io.Serializable

data class SpacetimeUiState(
    val filters: SpacetimeFilters = SpacetimeFilters(),
    val selectedTab: Int = 0,
    val heatmapX: SpacetimeDimension = SpacetimeDimension.Region,
    val heatmapY: SpacetimeDimension = SpacetimeDimension.Category,
    val overview: AsyncState<SpacetimeOverviewUiModel> = AsyncState(),
    val heatmap: AsyncState<SpacetimeHeatmapUiModel> = AsyncState(),
    val facets: AsyncState<AnalyticsFacetsUiModel> = AsyncState(),
    val drilldown: AsyncState<SpacetimeTimelineUiModel> = AsyncState(),
    val regionMap: AsyncState<SpacetimeRegionMapUiModel> = AsyncState(),
    val breakdownDimension: AnalyticsDimension = AnalyticsDimension.Region,
    val breakdown: AsyncState<AnalyticsBreakdownUiModel> = AsyncState(),
    val compare: CompareSelectionState = CompareSelectionState(),
    val compareResult: AsyncState<AnalyticsCompareUiModel> = AsyncState(),
    val outliers: AsyncState<List<AnalyticsOutlierUiModel>> = AsyncState(),
    val crosstab: CrosstabSelectionState = CrosstabSelectionState(),
    val crosstabResult: AsyncState<AnalyticsCrosstabUiModel> = AsyncState(),
)

@HiltViewModel
class SpacetimeViewModel @Inject constructor(
    private val repository: DataExploreRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreUiState())
    val uiState: StateFlow<SpacetimeUiState> = _uiState.asStateFlow()

    private var overviewJob: Job? = null
    private var heatmapJob: Job? = null
    private var facetsJob: Job? = null
    private var drilldownJob: Job? = null
    private var regionMapJob: Job? = null
    private var breakdownJob: Job? = null
    private var compareJob: Job? = null
    private var outliersJob: Job? = null
    private var crosstabJob: Job? = null

    init {
        loadOverview()
        loadHeatmap()
        loadFacets()
        if (_uiState.value.selectedTab == 1) {
            loadBreakdown()
            loadOutliers()
        }
    }

    fun refresh() {
        loadOverview()
        loadHeatmap()
        loadFacets()
        if (_uiState.value.selectedTab == 1) {
            loadBreakdown()
            loadOutliers()
        }
    }

    fun updateFilters(filters: SpacetimeFilters) {
        val shouldReloadAnalytics = _uiState.value.selectedTab == 1
        _uiState.update {
            it.copy(
                filters = filters,
                breakdown = AsyncState(),
                outliers = AsyncState(),
                compareResult = if (shouldReloadAnalytics) AsyncState() else it.compareResult,
                crosstabResult = if (shouldReloadAnalytics) AsyncState() else it.crosstabResult,
            )
        }
        persistUiState()
        loadOverview()
        loadHeatmap()
        loadFacets()
        if (shouldReloadAnalytics) {
            loadBreakdown()
            loadOutliers()
        }
    }

    fun selectTab(index: Int) {
        if (_uiState.value.selectedTab == index) return
        _uiState.update { it.copy(selectedTab = index) }
        persistUiState()
        if (index == 1) {
            if (_uiState.value.breakdown.data == null) loadBreakdown()
            if (_uiState.value.outliers.data == null) loadOutliers()
        }
    }

    fun setHeatmapDimensions(x: SpacetimeDimension, y: SpacetimeDimension) {
        _uiState.update { it.copy(heatmapX = x, heatmapY = y) }
        persistUiState()
        loadHeatmap()
    }

    fun setBreakdownDimension(dimension: AnalyticsDimension) {
        _uiState.update {
            it.copy(
                breakdownDimension = dimension,
                breakdown = AsyncState(),
                outliers = AsyncState(),
            )
        }
        persistUiState()
        loadBreakdown()
        loadOutliers()
    }

    fun setCompareDimension(dimension: AnalyticsDimension) {
        _uiState.update {
            it.copy(
                compare = it.compare.copy(dimension = dimension, selectedKeys = emptyList()),
                compareResult = AsyncState(),
            )
        }
        persistUiState()
    }

    fun toggleCompareKey(key: String) {
        _uiState.update { state ->
            val current = state.compare.selectedKeys
            val updated = if (key in current) current - key else {
                if (current.size >= 10) current else current + key
            }
            state.copy(compare = state.compare.copy(selectedKeys = updated))
        }
        persistUiState()
    }

    fun setCompareMetric(metric: RankingMetric) {
        _uiState.update { it.copy(compare = it.compare.copy(metric = metric)) }
        persistUiState()
    }

    fun runCompare() {
        val selection = _uiState.value.compare
        if (selection.selectedKeys.isEmpty()) return
        compareJob?.cancel()
        compareJob = viewModelScope.launch {
            val requestedFilters = _uiState.value.filters.toAnalyticsFilters()
            _uiState.update { it.copy(compareResult = AsyncState(isLoading = true)) }
            runCatchingCancellable {
                repository.getAnalyticsCompare(
                    dimension = selection.dimension,
                    keys = selection.selectedKeys,
                    metric = selection.metric,
                    filters = requestedFilters,
                )
            }.onSuccess { result ->
                if (_uiState.value.compare == selection && _uiState.value.filters.toAnalyticsFilters() == requestedFilters) {
                    _uiState.update { it.copy(compareResult = AsyncState(data = result)) }
                }
            }.onFailure { throwable ->
                if (_uiState.value.compare == selection && _uiState.value.filters.toAnalyticsFilters() == requestedFilters) {
                    _uiState.update { it.copy(compareResult = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
            }
        }
    }

    fun setCrosstabDimensions(x: AnalyticsDimension, y: AnalyticsDimension) {
        if (x == y) return
        _uiState.update { it.copy(crosstab = CrosstabSelectionState(x = x, y = y), crosstabResult = AsyncState()) }
        persistUiState()
    }

    fun runCrosstab() {
        val selection = _uiState.value.crosstab
        crosstabJob?.cancel()
        crosstabJob = viewModelScope.launch {
            val requestedFilters = _uiState.value.filters.toAnalyticsFilters()
            _uiState.update { it.copy(crosstabResult = AsyncState(isLoading = true)) }
            runCatchingCancellable {
                repository.getAnalyticsCrosstab(
                    x = selection.x,
                    y = selection.y,
                    filters = requestedFilters,
                )
            }.onSuccess { result ->
                if (_uiState.value.crosstab == selection && _uiState.value.filters.toAnalyticsFilters() == requestedFilters) {
                    _uiState.update { it.copy(crosstabResult = AsyncState(data = result)) }
                }
            }.onFailure { throwable ->
                if (_uiState.value.crosstab == selection && _uiState.value.filters.toAnalyticsFilters() == requestedFilters) {
                    _uiState.update { it.copy(crosstabResult = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
            }
        }
    }

    fun loadDrilldown(state: DrilldownState) {
        drilldownJob?.cancel()
        regionMapJob?.cancel()
        _uiState.update { it.copy(drilldown = AsyncState(), regionMap = AsyncState()) }
        when (state) {
            is DrilldownState.RegionTimeline -> {
                drilldownJob = viewModelScope.launch {
                    _uiState.update { it.copy(drilldown = AsyncState(isLoading = true)) }
                    runCatchingCancellable { repository.getRegionTimeline(state.region) }
                        .onSuccess { result ->
                            _uiState.update { it.copy(drilldown = AsyncState(data = result)) }
                        }
                        .onFailure { throwable ->
                            _uiState.update { it.copy(drilldown = AsyncState(errorKind = throwable.toUiError().kind)) }
                        }
                }
            }

            is DrilldownState.CategoryTimeline -> {
                drilldownJob = viewModelScope.launch {
                    _uiState.update { it.copy(drilldown = AsyncState(isLoading = true)) }
                    runCatchingCancellable { repository.getCategoryTimeline(state.category) }
                        .onSuccess { result ->
                            _uiState.update { it.copy(drilldown = AsyncState(data = result)) }
                        }
                        .onFailure { throwable ->
                            _uiState.update { it.copy(drilldown = AsyncState(errorKind = throwable.toUiError().kind)) }
                        }
                }
            }

            is DrilldownState.YearMap -> {
                regionMapJob = viewModelScope.launch {
                    _uiState.update { it.copy(regionMap = AsyncState(isLoading = true)) }
                    runCatchingCancellable { repository.getYearMap(state.year) }
                        .onSuccess { result ->
                            _uiState.update { it.copy(regionMap = AsyncState(data = result)) }
                        }
                        .onFailure { throwable ->
                            _uiState.update { it.copy(regionMap = AsyncState(errorKind = throwable.toUiError().kind)) }
                        }
                }
            }

            DrilldownState.None -> Unit
        }
    }

    fun clearDrilldown() {
        drilldownJob?.cancel()
        regionMapJob?.cancel()
        _uiState.update { it.copy(drilldown = AsyncState(), regionMap = AsyncState()) }
    }

    fun loadOverview() {
        overviewJob?.cancel()
        overviewJob = viewModelScope.launch {
            val currentFilters = _uiState.value.filters
            _uiState.update { it.copy(overview = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getSpacetimeOverview(currentFilters) }
                .onSuccess { result ->
                    if (_uiState.value.filters == currentFilters) {
                        _uiState.update { it.copy(overview = AsyncState(data = result)) }
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.filters == currentFilters) {
                        _uiState.update { it.copy(overview = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun loadHeatmap() {
        heatmapJob?.cancel()
        heatmapJob = viewModelScope.launch {
            val x = _uiState.value.heatmapX
            val y = _uiState.value.heatmapY
            val currentFilters = _uiState.value.filters
            _uiState.update { it.copy(heatmap = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getSpacetimeHeatmap(x = x, y = y, filters = currentFilters) }
                .onSuccess { result ->
                    if (_uiState.value.heatmapX == x && _uiState.value.heatmapY == y && _uiState.value.filters == currentFilters) {
                        _uiState.update { it.copy(heatmap = AsyncState(data = result)) }
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.heatmapX == x && _uiState.value.heatmapY == y && _uiState.value.filters == currentFilters) {
                        _uiState.update { it.copy(heatmap = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun loadFacets() {
        facetsJob?.cancel()
        facetsJob = viewModelScope.launch {
            val currentFilters = _uiState.value.filters.toAnalyticsFilters()
            _uiState.update { it.copy(facets = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getAnalyticsFacets(currentFilters) }
                .onSuccess { result ->
                    if (_uiState.value.filters.toAnalyticsFilters() == currentFilters) {
                        _uiState.update { it.copy(facets = AsyncState(data = result)) }
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.filters.toAnalyticsFilters() == currentFilters) {
                        _uiState.update { it.copy(facets = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun loadBreakdown() {
        breakdownJob?.cancel()
        breakdownJob = viewModelScope.launch {
            val dimension = _uiState.value.breakdownDimension
            val currentFilters = _uiState.value.filters.toAnalyticsFilters()
            _uiState.update { it.copy(breakdown = AsyncState(isLoading = true)) }
            runCatchingCancellable {
                repository.getAnalyticsBreakdown(groupBy = dimension, filters = currentFilters)
            }.onSuccess { result ->
                if (_uiState.value.breakdownDimension == dimension && _uiState.value.filters.toAnalyticsFilters() == currentFilters) {
                    _uiState.update { it.copy(breakdown = AsyncState(data = result)) }
                }
            }.onFailure { throwable ->
                if (_uiState.value.breakdownDimension == dimension && _uiState.value.filters.toAnalyticsFilters() == currentFilters) {
                    _uiState.update { it.copy(breakdown = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
            }
        }
    }

    fun loadOutliers() {
        outliersJob?.cancel()
        outliersJob = viewModelScope.launch {
            val currentFilters = _uiState.value.filters.toAnalyticsFilters()
            val dimension = _uiState.value.breakdownDimension
            _uiState.update { it.copy(outliers = AsyncState(isLoading = true)) }
            runCatchingCancellable {
                repository.getAnalyticsOutliers(
                    dimension = dimension,
                    metric = RankingMetric.Total,
                    filters = currentFilters,
                )
            }.onSuccess { result ->
                if (_uiState.value.filters.toAnalyticsFilters() == currentFilters && _uiState.value.breakdownDimension == dimension) {
                    _uiState.update { it.copy(outliers = AsyncState(data = result)) }
                }
            }.onFailure { throwable ->
                if (_uiState.value.filters.toAnalyticsFilters() == currentFilters && _uiState.value.breakdownDimension == dimension) {
                    _uiState.update { it.copy(outliers = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
            }
        }
    }

    private fun persistUiState() {
        val state = _uiState.value
        savedStateHandle[KEY_FILTERS] = state.filters
        savedStateHandle[KEY_SELECTED_TAB] = state.selectedTab
        savedStateHandle[KEY_HEATMAP_X] = state.heatmapX
        savedStateHandle[KEY_HEATMAP_Y] = state.heatmapY
        savedStateHandle[KEY_BREAKDOWN_DIMENSION] = state.breakdownDimension
        savedStateHandle[KEY_COMPARE_DIMENSION] = state.compare.dimension
        savedStateHandle[KEY_COMPARE_KEYS] = ArrayList(state.compare.selectedKeys)
        savedStateHandle[KEY_COMPARE_METRIC] = state.compare.metric
        savedStateHandle[KEY_CROSSTAB_X] = state.crosstab.x
        savedStateHandle[KEY_CROSSTAB_Y] = state.crosstab.y
    }

    @Suppress("UNCHECKED_CAST")
    private fun restoreUiState(): SpacetimeUiState {
        val filters = savedStateHandle.get<SpacetimeFilters>(KEY_FILTERS) ?: SpacetimeFilters()
        val selectedTab = savedStateHandle.get<Int>(KEY_SELECTED_TAB) ?: 0
        val heatmapX = savedStateHandle.get<SpacetimeDimension>(KEY_HEATMAP_X) ?: SpacetimeDimension.Region
        val heatmapY = savedStateHandle.get<SpacetimeDimension>(KEY_HEATMAP_Y) ?: SpacetimeDimension.Category
        val breakdownDimension = savedStateHandle.get<AnalyticsDimension>(KEY_BREAKDOWN_DIMENSION) ?: AnalyticsDimension.Region
        val compareDimension = savedStateHandle.get<AnalyticsDimension>(KEY_COMPARE_DIMENSION) ?: AnalyticsDimension.Region
        val compareKeys = savedStateHandle.get<ArrayList<String>>(KEY_COMPARE_KEYS)?.toList() ?: emptyList()
        val compareMetric = savedStateHandle.get<RankingMetric>(KEY_COMPARE_METRIC) ?: RankingMetric.Total
        val crosstabX = savedStateHandle.get<AnalyticsDimension>(KEY_CROSSTAB_X) ?: AnalyticsDimension.Region
        val crosstabY = savedStateHandle.get<AnalyticsDimension>(KEY_CROSSTAB_Y) ?: AnalyticsDimension.Category
        return SpacetimeUiState(
            filters = filters,
            selectedTab = selectedTab,
            heatmapX = heatmapX,
            heatmapY = heatmapY,
            breakdownDimension = breakdownDimension,
            compare = CompareSelectionState(
                dimension = compareDimension,
                selectedKeys = compareKeys,
                metric = compareMetric,
            ),
            crosstab = CrosstabSelectionState(x = crosstabX, y = crosstabY),
        )
    }

    private fun SpacetimeFilters.toAnalyticsFilters(): AnalyticsFilters =
        AnalyticsFilters(
            targetType = targetType,
            region = region,
            category = category,
            year = fromYear ?: toYear,
            kind = kind,
            hasImage = null,
            hasAiResult = null,
        )
}
