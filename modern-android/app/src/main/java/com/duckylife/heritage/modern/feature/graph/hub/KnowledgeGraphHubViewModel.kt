package com.duckylife.heritage.modern.feature.graph.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.data.RecentContentProvider
import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class KnowledgeGraphHubViewModel @Inject constructor(
    private val repository: KnowledgeGraphRepository,
    recentContentProvider: RecentContentProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(KnowledgeGraphHubUiState())
    val uiState: StateFlow<KnowledgeGraphHubUiState> = _uiState.asStateFlow()

    init {
        loadCommunities()
        viewModelScope.launch {
            recentContentProvider.observeRecentContent().collect { recent ->
                _uiState.update { it.copy(recentContent = recent) }
            }
        }
    }

    fun retry() {
        loadCommunities()
    }

    fun showInfoSheet() {
        _uiState.update { it.copy(isInfoSheetVisible = true) }
    }

    fun dismissInfoSheet() {
        _uiState.update { it.copy(isInfoSheetVisible = false) }
    }

    private fun loadCommunities() {
        viewModelScope.launch {
            _uiState.update { it.copy(communities = it.communities.copy(isLoading = true, errorKind = null)) }
            runCatchingCancellable { repository.getCommunities(limit = 12, minSize = 3) }
                .onSuccess { communities ->
                    _uiState.update {
                        it.copy(
                            communities = DiscoverySectionState(
                                isLoading = false,
                                data = communities,
                            ),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            communities = DiscoverySectionState(
                                isLoading = false,
                                errorKind = e.toUiError().kind,
                            ),
                        )
                    }
                }
        }
    }
}
