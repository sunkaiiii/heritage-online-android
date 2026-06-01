package com.duckylife.heritage.modern.feature.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.DiscoverySerendipityQuery
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.duckylife.heritage.modern.core.runCatchingCancellable

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        loadToday()
        loadTrending()
        loadWeekly()
        loadClassic()
    }

    fun loadToday() {
        viewModelScope.launch {
            _uiState.update { it.copy(today = it.today.copy(isLoading = true, errorKind = null)) }
            runCatchingCancellable { repository.discoveryToday() }
                .onSuccess { data ->
                    _uiState.update { it.copy(today = DiscoverySectionState(data = data)) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(today = it.today.copy(isLoading = false, errorKind = e.toUiError().kind))
                    }
                }
        }
    }

    fun loadTrending() {
        viewModelScope.launch {
            _uiState.update { it.copy(trending = it.trending.copy(isLoading = true, errorKind = null)) }
            runCatchingCancellable { repository.discoveryTrending(limit = 10) }
                .onSuccess { data ->
                    _uiState.update { it.copy(trending = DiscoverySectionState(data = data)) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(trending = it.trending.copy(isLoading = false, errorKind = e.toUiError().kind))
                    }
                }
        }
    }

    fun loadWeekly() {
        viewModelScope.launch {
            _uiState.update { it.copy(weekly = it.weekly.copy(isLoading = true, errorKind = null)) }
            runCatchingCancellable { repository.discoveryWeekly() }
                .onSuccess { data ->
                    _uiState.update { it.copy(weekly = DiscoverySectionState(data = data)) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(weekly = it.weekly.copy(isLoading = false, errorKind = e.toUiError().kind))
                    }
                }
        }
    }

    fun loadClassic() {
        viewModelScope.launch {
            _uiState.update { it.copy(classic = it.classic.copy(isLoading = true, errorKind = null)) }
            try {
                coroutineScope {
                    val exploreIndexDeferred = async {
                        runCatchingCancellable { repository.exploreIndex() }
                    }
                    val topicsDeferred = async {
                        runCatchingCancellable { repository.exploreTopics(type = "all", limit = 12) }
                    }
                    val learningPathsDeferred = async {
                        runCatchingCancellable { repository.learningPaths() }
                    }
                    val featuredCollectionsDeferred = async {
                        runCatchingCancellable { repository.featuredCollections() }
                    }
                    val regionAtlasDeferred = async {
                        runCatchingCancellable { repository.regionAtlas() }
                    }

                    val data = DiscoveryClassicData(
                        exploreIndex = exploreIndexDeferred.await().getOrNull(),
                        topics = topicsDeferred.await().getOrNull() ?: emptyList(),
                        learningPaths = learningPathsDeferred.await().getOrNull() ?: emptyList(),
                        featuredCollections = featuredCollectionsDeferred.await().getOrNull() ?: emptyList(),
                        regionAtlas = regionAtlasDeferred.await().getOrNull(),
                    )

                    val hasAnyData = data.exploreIndex != null ||
                        data.topics.isNotEmpty() ||
                        data.learningPaths.isNotEmpty() ||
                        data.featuredCollections.isNotEmpty() ||
                        data.regionAtlas != null

                    if (hasAnyData) {
                        _uiState.update { it.copy(classic = DiscoverySectionState(data = data)) }
                    } else {
                        val firstError = exploreIndexDeferred.await().exceptionOrNull()
                            ?: topicsDeferred.await().exceptionOrNull()
                        _uiState.update {
                            it.copy(classic = DiscoverySectionState(
                                errorKind = (firstError ?: Exception("Unknown error")).toUiError().kind,
                            ))
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(classic = DiscoverySectionState(errorKind = e.toUiError().kind))
                }
            }
        }
    }

    fun serendipity() {
        viewModelScope.launch {
            _uiState.update { it.copy(serendipityLoading = true) }
            runCatchingCancellable {
                repository.discoverySerendipity(DiscoverySerendipityQuery())
            }.onSuccess { item ->
                _uiState.update {
                    it.copy(serendipityItem = item, serendipityLoading = false)
                }
            }.onFailure {
                _uiState.update { it.copy(serendipityLoading = false) }
            }
        }
    }

    fun clearSerendipity() {
        _uiState.update { it.copy(serendipityItem = null) }
    }
}
