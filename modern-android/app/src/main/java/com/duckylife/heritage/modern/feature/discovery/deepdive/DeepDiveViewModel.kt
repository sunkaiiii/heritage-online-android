package com.duckylife.heritage.modern.feature.discovery.deepdive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.DiscoveryDeepDiveQuery
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
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

private fun searchResultTypeFromWire(wireName: String): SearchResultType =
    SearchResultType.entries.firstOrNull { it.wireName == wireName }
        ?: SearchResultType.Article

@HiltViewModel(assistedFactory = DeepDiveViewModel.Factory::class)
class DeepDiveViewModel @AssistedInject constructor(
    @Assisted("seedType") private val seedType: String,
    @Assisted("seedId") private val seedId: String,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeepDiveUiState())
    val uiState: StateFlow<DeepDiveUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable {
                repository.discoveryDeepDive(
                    DiscoveryDeepDiveQuery(
                        seedType = searchResultTypeFromWire(seedType),
                        seedId = seedId,
                    ),
                )
            }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            seed = result.seed,
                            related = result.related,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorKind = e.toUiError().kind)
                    }
                }
        }
    }

    fun deepDiveAgain(item: DiscoveryItemDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable {
                repository.discoveryDeepDive(
                    DiscoveryDeepDiveQuery(
                        seedType = searchResultTypeFromWire(item.type),
                        seedId = item.id.orEmpty(),
                    ),
                )
            }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            seed = result.seed,
                            related = result.related,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorKind = e.toUiError().kind)
                    }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("seedType") seedType: String,
            @Assisted("seedId") seedId: String,
        ): DeepDiveViewModel
    }
}
