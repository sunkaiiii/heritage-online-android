package com.duckylife.heritage.modern.feature.discovery.graphexplore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
import com.duckylife.heritage.modern.feature.discovery.GraphTab
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.feature.graph.model.centerNode
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = GraphExploreViewModel.Factory::class)
class GraphExploreViewModel @AssistedInject constructor(
    @Assisted("contentType") private val contentType: String,
    @Assisted("contentId") private val contentId: String,
    @Assisted("initialTab") initialTab: GraphTab,
    private val repository: KnowledgeGraphRepository,
) : ViewModel() {

    private val contentRef: SearchResultType? = SearchResultType.fromWireName(contentType)

    private val _uiState = MutableStateFlow(
        GraphExploreUiState(
            isInvalidRoute = contentRef == null || contentId.isBlank(),
            selectedTab = initialTab,
        ),
    )
    val uiState: StateFlow<GraphExploreUiState> = _uiState.asStateFlow()

    private val tabJobs = mutableMapOf<GraphTab, Job>()
    private var aiInferredJob: Job? = null

    init {
        if (contentRef != null && contentId.isNotBlank()) {
            loadTab(GraphTab.Neighbors)
            if (initialTab != GraphTab.Neighbors) {
                loadTab(initialTab)
            }
        }
    }

    fun selectTab(tab: GraphTab) {
        if (tab == _uiState.value.selectedTab) return
        _uiState.update { it.copy(selectedTab = tab) }
        loadTab(tab)
        if (tab == GraphTab.Evidence && _uiState.value.includeAiInferred) {
            loadAiInferred()
        }
    }

    fun retry() {
        val selectedTab = _uiState.value.selectedTab
        loadTab(selectedTab, force = true)
        if (selectedTab == GraphTab.Evidence && _uiState.value.includeAiInferred) {
            loadAiInferred(force = true)
        }
    }

    /**
     * 刷新当前 tab 的数据。
     *
     * 与 [retry] 语义相同，都从菜单触发当前选中 tab 的重新加载。
     */
    fun refresh() = retry()

    fun toggleAiInferred() {
        val shouldInclude = !_uiState.value.includeAiInferred
        _uiState.update { it.copy(includeAiInferred = shouldInclude) }
        if (shouldInclude) {
            loadAiInferred()
        }
    }

    fun selectExploreDepth(depth: Int) {
        val clampedDepth = depth.coerceIn(1, 2)
        if (clampedDepth == _uiState.value.exploreDepth) return
        _uiState.update { it.copy(exploreDepth = clampedDepth) }
        loadTab(GraphTab.Explore, force = true)
    }

    private fun loadTab(tab: GraphTab, force: Boolean = false) {
        val ref = contentRef ?: return
        if (!force && tab.isLoadedOrLoading()) return

        tabJobs[tab]?.cancel()
        tabJobs[tab] = viewModelScope.launch {
            setTabLoading(tab)
            when (tab) {
                GraphTab.Neighbors -> loadNeighbors(ref)
                GraphTab.Similar -> loadSimilar(ref)
                GraphTab.Explore -> loadExplore(ref)
                GraphTab.Evidence -> loadEvidence(ref)
            }
        }
    }

    private suspend fun loadNeighbors(ref: SearchResultType) {
        runCatchingCancellable { repository.loadNeighbors(ref, contentId) }
            .onSuccess { neighbors ->
                _uiState.update {
                    it.copy(
                        neighbors = DiscoverySectionState(data = neighbors),
                        centerNode = neighbors.centerNode(),
                    )
                }
            }
            .onFailure { setTabError(GraphTab.Neighbors, it.toUiError().kind) }
    }

    private suspend fun loadSimilar(ref: SearchResultType) {
        runCatchingCancellable { repository.loadSimilar(ref, contentId) }
            .onSuccess { similar ->
                _uiState.update { it.copy(similar = DiscoverySectionState(data = similar)) }
            }
            .onFailure { setTabError(GraphTab.Similar, it.toUiError().kind) }
    }

    private suspend fun loadExplore(ref: SearchResultType) {
        val depth = _uiState.value.exploreDepth
        runCatchingCancellable { repository.loadExplore(ref, contentId, depth = depth) }
            .onSuccess { explore ->
                _uiState.update { it.copy(explore = DiscoverySectionState(data = explore)) }
            }
            .onFailure { setTabError(GraphTab.Explore, it.toUiError().kind) }
    }

    private suspend fun loadEvidence(ref: SearchResultType) {
        runCatchingCancellable {
            repository.loadEvidence(
                ref,
                contentId,
                includeAiInferred = false,
            )
        }
            .onSuccess { evidence ->
                _uiState.update { it.copy(evidence = DiscoverySectionState(data = evidence)) }
            }
            .onFailure { setTabError(GraphTab.Evidence, it.toUiError().kind) }
    }

    private fun loadAiInferred(force: Boolean = false) {
        val ref = contentRef ?: return
        val section = _uiState.value.aiInferredEdges
        if (!force && (section.isLoading || section.hasData)) return

        aiInferredJob?.cancel()
        aiInferredJob = viewModelScope.launch {
            _uiState.update {
                it.copy(aiInferredEdges = it.aiInferredEdges.copy(isLoading = true, errorKind = null))
            }
            runCatchingCancellable { repository.loadAiInferredEdges(ref, contentId) }
                .onSuccess { edges ->
                    _uiState.update {
                        it.copy(aiInferredEdges = DiscoverySectionState(data = edges))
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            aiInferredEdges = it.aiInferredEdges.copy(
                                isLoading = false,
                                errorKind = throwable.toUiError().kind,
                            ),
                        )
                    }
                }
        }
    }

    private fun GraphTab.isLoadedOrLoading(): Boolean {
        val section = when (this) {
            GraphTab.Neighbors -> _uiState.value.neighbors
            GraphTab.Similar -> _uiState.value.similar
            GraphTab.Explore -> _uiState.value.explore
            GraphTab.Evidence -> _uiState.value.evidence
        }
        return section.isLoading || section.hasData
    }

    private fun setTabLoading(tab: GraphTab) {
        _uiState.update { state ->
            when (tab) {
                GraphTab.Neighbors -> state.copy(neighbors = state.neighbors.copy(isLoading = true, errorKind = null))
                GraphTab.Similar -> state.copy(similar = state.similar.copy(isLoading = true, errorKind = null))
                GraphTab.Explore -> state.copy(explore = state.explore.copy(isLoading = true, errorKind = null))
                GraphTab.Evidence -> state.copy(evidence = state.evidence.copy(isLoading = true, errorKind = null))
            }
        }
    }

    private fun setTabError(tab: GraphTab, errorKind: ErrorKind) {
        _uiState.update { state ->
            when (tab) {
                GraphTab.Neighbors -> state.copy(neighbors = state.neighbors.copy(isLoading = false, errorKind = errorKind))
                GraphTab.Similar -> state.copy(similar = state.similar.copy(isLoading = false, errorKind = errorKind))
                GraphTab.Explore -> state.copy(explore = state.explore.copy(isLoading = false, errorKind = errorKind))
                GraphTab.Evidence -> state.copy(evidence = state.evidence.copy(isLoading = false, errorKind = errorKind))
            }
        }
    }

    override fun onCleared() {
        tabJobs.values.forEach { it.cancel() }
        tabJobs.clear()
        aiInferredJob?.cancel()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("contentType") contentType: String,
            @Assisted("contentId") contentId: String,
            @Assisted("initialTab") initialTab: GraphTab,
        ): GraphExploreViewModel
    }
}
