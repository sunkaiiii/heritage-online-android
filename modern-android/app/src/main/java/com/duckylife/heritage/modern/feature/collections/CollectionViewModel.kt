package com.duckylife.heritage.modern.feature.collections

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

@HiltViewModel(assistedFactory = CollectionViewModel.Factory::class)
class CollectionViewModel @AssistedInject constructor(
    @Assisted("id") private val id: String?,
    @Assisted("type") private val type: String?,
    @Assisted("topicKey") private val topicKey: String?,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    init {
        loadCollection()
    }

    fun loadCollection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatching {
                if (!id.isNullOrBlank()) {
                    repository.collection(id)
                } else if (!type.isNullOrBlank() && !topicKey.isNullOrBlank()) {
                    repository.topicCollection(type, topicKey)
                } else {
                    error("Missing collection identifier")
                }
            }
                .onSuccess { collection ->
                    _uiState.update { it.copy(isLoading = false, collection = collection) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: String?,
            @Assisted("type") type: String?,
            @Assisted("topicKey") topicKey: String?,
        ): CollectionViewModel
    }
}
