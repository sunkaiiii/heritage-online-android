package com.duckylife.heritage.modern.feature.regions

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
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegionAtlasUiState())
    val uiState: StateFlow<RegionAtlasUiState> = _uiState.asStateFlow()

    init {
        loadAtlas()
    }

    fun loadAtlas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatching { repository.regionAtlas() }
                .onSuccess { atlas ->
                    _uiState.update { it.copy(isLoading = false, atlas = atlas) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
                }
        }
    }
}

@HiltViewModel(assistedFactory = RegionDetailViewModel.Factory::class)
class RegionDetailViewModel @AssistedInject constructor(
    @Assisted("region") private val region: String,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegionDetailUiState())
    val uiState: StateFlow<RegionDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatching { repository.regionAtlasDetail(region) }
                .onSuccess { detail ->
                    _uiState.update { it.copy(isLoading = false, detail = detail) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("region") region: String,
        ): RegionDetailViewModel
    }
}
