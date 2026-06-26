package com.duckylife.heritage.modern.feature.graph.trail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.discovery.GraphTrailSource
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
        is GraphTrailSource.Random -> repository.getRandomGraphTrail(limit = 6)
        is GraphTrailSource.FromContent -> {
            val contentType = SearchResultType.fromWireName(source.type)
            if (contentType == null || source.contentId.isBlank()) {
                throw IllegalArgumentException("Invalid content source")
            }
            repository.getGraphTrailFromContent(
                contentType = contentType,
                contentId = source.contentId,
                limit = 6,
            )
        }
        is GraphTrailSource.FromTopic -> {
            if (source.topicType.isBlank() || source.topicKey.isBlank()) {
                throw IllegalArgumentException("Invalid topic source")
            }
            repository.getGraphTrailFromTopic(
                topicType = source.topicType,
                topicKey = source.topicKey,
                limit = 6,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("source") source: GraphTrailSource): GraphTrailViewModel
    }
}
