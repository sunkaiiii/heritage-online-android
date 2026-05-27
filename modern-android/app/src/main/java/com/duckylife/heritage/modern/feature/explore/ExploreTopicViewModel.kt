package com.duckylife.heritage.modern.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
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

@HiltViewModel(assistedFactory = ExploreTopicViewModel.Factory::class)
class ExploreTopicViewModel @AssistedInject constructor(
    @Assisted("type") private val type: String,
    @Assisted("key") private val key: String,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreTopicUiState())
    val uiState: StateFlow<ExploreTopicUiState> = _uiState.asStateFlow()

    init {
        loadTopic()
    }

    fun loadTopic() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatching { repository.exploreTopic(type, key) }
                .onSuccess { topic ->
                    _uiState.update { it.copy(isLoading = false, topic = topic) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("type") type: String,
            @Assisted("key") key: String,
        ): ExploreTopicViewModel
    }
}
