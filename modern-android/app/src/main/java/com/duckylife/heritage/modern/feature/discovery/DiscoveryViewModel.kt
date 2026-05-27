package com.duckylife.heritage.modern.feature.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
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
                        runCatching { repository.exploreIndex() }
                    }
                    val topicsDeferred = async {
                        runCatching { repository.exploreTopics(type = "all", limit = 12) }
                    }
                    val learningPathsDeferred = async {
                        runCatching { repository.learningPaths() }
                    }
                    val featuredCollectionsDeferred = async {
                        runCatching { repository.featuredCollections() }
                    }
                    val regionAtlasDeferred = async {
                        runCatching { repository.regionAtlas() }
                    }

                    val exploreIndex = exploreIndexDeferred.await().getOrNull()
                    val topics = topicsDeferred.await().getOrNull() ?: emptyList()
                    val learningPaths = learningPathsDeferred.await().getOrNull() ?: emptyList()
                    val featuredCollections = featuredCollectionsDeferred.await().getOrNull() ?: emptyList()
                    val regionAtlas = regionAtlasDeferred.await().getOrNull()

                    val hasAnyData = exploreIndex != null ||
                        topics.isNotEmpty() ||
                        learningPaths.isNotEmpty() ||
                        featuredCollections.isNotEmpty() ||
                        regionAtlas != null

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
}
