package com.duckylife.heritage.modern.feature.inheritors.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.data.InheritorDetailLookup
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = InheritorDetailViewModel.Factory::class)
class InheritorDetailViewModel @AssistedInject constructor(
    @Assisted("inheritorId") private val inheritorId: String?,
    @Assisted("sourceId") private val sourceId: String?,
    private val repository: HeritageRepository,
) : ViewModel() {
    private val lookup = InheritorDetailLookup(
        inheritorId = inheritorId,
        sourceId = sourceId,
    )
    private val _uiState = MutableStateFlow(InheritorDetailUiState())
    val uiState: StateFlow<InheritorDetailUiState> = _uiState.asStateFlow()

    init {
        observeCachedItem()
        refresh()
    }

    private fun observeCachedItem() {
        viewModelScope.launch {
            repository.cachedInheritorDetail(lookup).collect { item ->
                if (item != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            item = item,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.item == null,
                    errorMessage = null,
                )
            }
            runCatching {
                repository.refreshInheritorDetail(lookup)
            }.onSuccess { item ->
                _uiState.value = InheritorDetailUiState(
                    isLoading = false,
                    item = item,
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = if (it.item == null) throwable.message.orEmpty() else null,
                    )
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("inheritorId") inheritorId: String?,
            @Assisted("sourceId") sourceId: String?,
        ): InheritorDetailViewModel
    }
}
