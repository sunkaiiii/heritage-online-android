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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DeepDiveViewModel.Factory::class)
class DeepDiveViewModel @AssistedInject constructor(
    @Assisted("seedType") private val seedType: SearchResultType,
    @Assisted("seedId") private val seedId: String,
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeepDiveUiState())
    val uiState: StateFlow<DeepDiveUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadWithSeed(seedType, seedId)
    }

    fun deepDiveAgain(item: DiscoveryItemDto) {
        loadWithSeed(
            SearchResultType.fromWireName(item.type) ?: SearchResultType.Article,
            item.id.orEmpty(),
        )
    }

    private fun loadWithSeed(seedType: SearchResultType, seedId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable {
                repository.discoveryDeepDive(
                    DiscoveryDeepDiveQuery(
                        seedType = seedType,
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

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("seedType") seedType: SearchResultType,
            @Assisted("seedId") seedId: String,
        ): DeepDiveViewModel
    }
}
