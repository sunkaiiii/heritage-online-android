package com.duckylife.heritage.modern.feature.graph.topicmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.runCatchingCancellable
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

@HiltViewModel(assistedFactory = TopicGraphMapViewModel.Factory::class)
class TopicGraphMapViewModel @AssistedInject constructor(
    @Assisted("topicType") private val topicType: GraphNodeType,
    @Assisted("topicKey") private val topicKey: String,
    private val repository: KnowledgeGraphRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopicGraphMapUiState(isLoading = true))
    val uiState: StateFlow<TopicGraphMapUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun selectViewMode(mode: TopicGraphMapViewMode) {
        _uiState.update { it.copy(selectedViewMode = mode) }
    }

    private fun load() {
        if (topicType == GraphNodeType.Unknown || topicKey.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false, errorKind = com.duckylife.heritage.modern.ui.error.ErrorKind.BadRequest)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable {
                repository.getTopicGraphMap(topicType = topicType, topicKey = topicKey, limit = 50)
            }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(isLoading = false, result = result, errorKind = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorKind = throwable.toUiError().kind)
                    }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("topicType") topicType: GraphNodeType,
            @Assisted("topicKey") topicKey: String,
        ): TopicGraphMapViewModel
    }
}
