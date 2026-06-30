package com.duckylife.heritage.modern.feature.learning

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
import com.duckylife.heritage.modern.core.runCatchingCancellable

@HiltViewModel(assistedFactory = LearningPathViewModel.Factory::class)
class LearningPathViewModel @AssistedInject constructor(
    @Assisted("id") private val id: String,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningPathUiState())
    val uiState: StateFlow<LearningPathUiState> = _uiState.asStateFlow()

    init {
        loadPath()
    }

    fun loadPath() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable { repository.learningPathDetail(id) }
                .onSuccess { path ->
                    _uiState.update { it.copy(isLoading = false, path = path) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: String,
        ): LearningPathViewModel
    }
}
