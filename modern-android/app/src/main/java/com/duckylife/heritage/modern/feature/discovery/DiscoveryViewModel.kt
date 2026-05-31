package com.duckylife.heritage.modern.feature.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.DiscoverySerendipityQuery
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.ui.error.ErrorKind
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
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
                    val todayDeferred = async {
                        runCatchingCancellable { repository.discoveryToday() }
                    }
                    val trendingDeferred = async {
                        runCatchingCancellable { repository.discoveryTrending(limit = 10) }
                    }
                    val weeklyDeferred = async {
                        runCatchingCancellable { repository.discoveryWeekly() }
                    }

                    val exploreIndex = exploreIndexDeferred.await().getOrNull()
                    val topics = topicsDeferred.await().getOrNull() ?: emptyList()
                    val learningPaths = learningPathsDeferred.await().getOrNull() ?: emptyList()
                    val featuredCollections = featuredCollectionsDeferred.await().getOrNull() ?: emptyList()
                    val regionAtlas = regionAtlasDeferred.await().getOrNull()
                    val today = todayDeferred.await().getOrNull()
                    val trending = trendingDeferred.await().getOrNull()
                    val weekly = weeklyDeferred.await().getOrNull()

                    val hasAnyData = exploreIndex != null ||
                        topics.isNotEmpty() ||
                        learningPaths.isNotEmpty() ||
                        featuredCollections.isNotEmpty() ||
                        regionAtlas != null ||
                        today != null ||
                        trending != null ||
                        weekly != null

                    if (hasAnyData) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorKind = null,
                                exploreIndex = exploreIndex,
                                topics = topics,
                                learningPaths = learningPaths,
                                featuredCollections = featuredCollections,
                                regionAtlas = regionAtlas,
                                today = today,
                                trending = trending,
                                weekly = weekly,
                            )
                        }
                    } else {
                        val firstError = exploreIndexDeferred.await().exceptionOrNull()
                            ?: topicsDeferred.await().exceptionOrNull()
                            ?: learningPathsDeferred.await().exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorKind = (firstError ?: Exception("Unknown error")).toUiError().kind,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorKind = e.toUiError().kind,
                    )
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
