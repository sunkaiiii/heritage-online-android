package com.duckylife.heritage.modern.feature.directory.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.DirectoryDetailLookup
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.BlendedRecommendationQuery
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentSnapshot
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import com.duckylife.heritage.modern.core.saved.SavedContentType
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
import kotlinx.serialization.encodeToString

@HiltViewModel(assistedFactory = DirectoryDetailViewModel.Factory::class)
class DirectoryDetailViewModel @AssistedInject constructor(
    @Assisted("itemId") private val itemId: String?,
    @Assisted("sourceId") private val sourceId: String?,
    @Assisted private val kind: DirectoryItemKind,
    private val repository: HeritageRepository,
    private val savedContentRepository: SavedContentRepository,
) : ViewModel() {
    private val lookup = DirectoryDetailLookup(
        itemId = itemId,
        sourceId = sourceId,
        kind = kind,
    )
    private val _uiState = MutableStateFlow(DirectoryDetailUiState())
    val uiState: StateFlow<DirectoryDetailUiState> = _uiState.asStateFlow()

    private var snapshot: SavedContentSnapshot? = null

    init {
        observeFavorite()
        observeCachedItem()
        refresh()
    }

    private fun observeFavorite() {
        val target = SavedContentTarget(
            id = itemId,
            sourceId = sourceId,
            kind = kind.wireName,
        )
        viewModelScope.launch {
            savedContentRepository.observeFavoriteState(target).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    private fun observeCachedItem() {
        viewModelScope.launch {
            repository.cachedDirectoryDetail(lookup).collect { item ->
                if (item != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            item = item,
                            errorKind = null,
                        )
                    }
                    recordViewedIfNew(item)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.item == null,
                    errorKind = null,
                )
            }
            runCatchingCancellable {
                repository.refreshDirectoryDetail(lookup)
            }.onSuccess { item ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        item = item,
                        errorKind = null,
                    )
                }
                recordViewedIfNew(item)
                loadContext(item.id)
                loadDigest(item.id)
                loadBlended(item.id)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorKind = if (it.item == null) throwable.toUiError().kind else null,
                    )
                }
            }
        }
    }

    fun loadContext() {
        val id = _uiState.value.item?.id ?: return
        loadContext(id)
    }

    private fun loadContext(itemId: String?) {
        if (itemId.isNullOrBlank()) return
        _uiState.update { it.copy(contextLoading = true, contextErrorKind = null) }
        viewModelScope.launch {
            runCatchingCancellable { repository.directoryItemContext(itemId) }
                .onSuccess { ctx ->
                    _uiState.update { it.copy(contextLoading = false, context = ctx) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(contextLoading = false, contextErrorKind = e.toUiError().kind) }
                }
        }
    }

    private fun loadDigest(itemId: String?) {
        if (itemId.isNullOrBlank()) return
        _uiState.update { it.copy(digestLoading = true, digestErrorKind = null) }
        viewModelScope.launch {
            runCatchingCancellable { repository.directoryItemDigest(itemId) }
                .onSuccess { digest ->
                    _uiState.update { it.copy(digestLoading = false, digest = digest) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(digestLoading = false, digestErrorKind = e.toUiError().kind) }
                }
        }
    }

    fun retryDigest() {
        val id = _uiState.value.item?.id ?: return
        loadDigest(id)
    }

    private fun loadBlended(itemId: String?) {
        if (itemId.isNullOrBlank()) return
        _uiState.update { it.copy(blendedLoading = true) }
        viewModelScope.launch {
            runCatchingCancellable {
                repository.blendedRecommendations(
                    BlendedRecommendationQuery(
                        type = SearchResultType.DirectoryItem,
                        id = itemId,
                    ),
                )
            }.onSuccess { response ->
                _uiState.update { it.copy(blendedLoading = false, blendedRecommendations = response) }
            }.onFailure {
                _uiState.update { it.copy(blendedLoading = false) }
            }
        }
    }

    fun toggleFavorite() {
        val snap = snapshot ?: return
        viewModelScope.launch {
            savedContentRepository.toggleFavorite(snap)
        }
    }

    private fun recordViewedIfNew(item: com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto) {
        val newSnapshot = SavedContentSnapshot(
            contentType = SavedContentType.DirectoryItem,
            id = item.id,
            title = item.title,
            summary = item.summary,
            coverImageJson = item.coverImage?.let { HeritageJson.encodeToString(it) },
            category = item.category,
            region = item.region,
            year = item.publishedYear,
            sourceUrl = item.sourceUrl,
            target = SavedContentTarget(
                id = item.id,
                sourceId = sourceId,
                kind = kind.wireName,
            ),
        )
        if (newSnapshot != snapshot) {
            snapshot = newSnapshot
            viewModelScope.launch {
                savedContentRepository.recordViewed(newSnapshot)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("itemId") itemId: String?,
            @Assisted("sourceId") sourceId: String?,
            @Assisted kind: DirectoryItemKind,
        ): DirectoryDetailViewModel
    }
}
