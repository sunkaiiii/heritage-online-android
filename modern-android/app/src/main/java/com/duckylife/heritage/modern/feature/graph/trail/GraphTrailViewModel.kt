package com.duckylife.heritage.modern.feature.graph.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.discovery.GraphTrailSource
import com.duckylife.heritage.modern.feature.graph.model.GraphTrailResult
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

@HiltViewModel(assistedFactory = GraphTrailViewModel.Factory::class)
class GraphTrailViewModel @AssistedInject constructor(
    @Assisted("source") private val source: GraphTrailSource,
    private val repository: KnowledgeGraphRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GraphTrailUiState(
            isLoading = true,
            canResample = source is GraphTrailSource.Random,
        ),
    )
    val uiState: StateFlow<GraphTrailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    override fun onCleared() {
        loadJob?.cancel()
    }

    fun retry() = load()

    fun resample() {
        if (!_uiState.value.canResample) return
        load(resampling = true)
    }

    private fun load(resampling: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable { loadTrail() }
                .onSuccess { trail ->
                    _uiState.update {
                        it.copy(isLoading = false, trail = trail, errorKind = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorKind = throwable.toUiError().kind)
                    }
                }
        }
    }

    private suspend fun loadTrail() = when (source) {
        is GraphTrailSource.Random -> loadRandomTrail()
        is GraphTrailSource.FromContent -> {
            if (source.contentId.isBlank()) {
                throw IllegalArgumentException("Invalid content source")
            }
            repository.getGraphTrailFromContent(
                contentType = source.type,
                contentId = source.contentId,
                limit = 6,
            )
        }
        is GraphTrailSource.FromTopic -> {
            if (source.topicType == GraphNodeType.Unknown || source.topicKey.isBlank()) {
                throw IllegalArgumentException("Invalid topic source")
            }
            repository.getGraphTrailFromTopic(
                topicType = source.topicType,
                topicKey = source.topicKey,
                limit = 6,
            )
        }
    }

    /**
     * 随机漫游会尝试多组 strategy/type 组合，避免单一次空响应就给出空态。
     * 只要任意一次返回非空节点或步骤，即使用该结果。
     */
    private suspend fun loadRandomTrail(): GraphTrailResult {
        val strategies = listOf(
            TrailStrategy.Mixed,
            TrailStrategy.Representative,
            TrailStrategy.Diverse,
        )
        val types = listOf(null) + SearchResultType.entries.filter { it != SearchResultType.Unknown }
        var lastResult: GraphTrailResult? = null
        var attempts = 0
        val maxAttempts = 4
        for (strategy in strategies) {
            for (type in types) {
                if (attempts >= maxAttempts) break
                val result = repository.getRandomGraphTrail(
                    strategy = strategy,
                    type = type,
                    limit = 6,
                )
                lastResult = result
                if (result.steps.isNotEmpty() || result.nodes.isNotEmpty()) {
                    return result
                }
                attempts++
            }
        }
        return lastResult ?: repository.getRandomGraphTrail(limit = 6)
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("source") source: GraphTrailSource): GraphTrailViewModel
    }
}
