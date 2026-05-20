package com.duckylife.heritage.modern.feature.articles.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ArticleDetailLookup
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
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
import kotlinx.serialization.encodeToString

@HiltViewModel(assistedFactory = ArticleDetailViewModel.Factory::class)
class ArticleDetailViewModel @AssistedInject constructor(
    @Assisted("articleId") private val articleId: String?,
    @Assisted("sourceId") private val sourceId: String?,
    @Assisted("sourceUrl") private val sourceUrl: String?,
    @Assisted private val category: ArticleCategory,
    private val repository: HeritageRepository,
    private val savedContentRepository: SavedContentRepository,
) : ViewModel() {
    private val lookup = ArticleDetailLookup(
        articleId = articleId,
        sourceId = sourceId,
        sourceUrl = sourceUrl,
        category = category,
    )
    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    private var snapshot: SavedContentSnapshot? = null

    init {
        observeFavorite()
        observeCachedArticle()
        refresh()
    }

    private fun observeFavorite() {
        val target = SavedContentTarget(
            id = articleId,
            sourceId = sourceId,
            sourceUrl = sourceUrl,
            category = category.wireName,
        )
        viewModelScope.launch {
            savedContentRepository.observeFavoriteState(target).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    private fun observeCachedArticle() {
        viewModelScope.launch {
            repository.cachedArticleDetail(lookup).collect { article ->
                if (article != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            article = article,
                            errorKind = null,
                        )
                    }
                    recordViewedIfNew(article)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.article == null,
                    errorKind = null,
                )
            }
            runCatching {
                repository.refreshArticleDetail(lookup)
            }.onSuccess { article ->
                _uiState.value = ArticleDetailUiState(
                    isLoading = false,
                    article = article,
                    isFavorite = _uiState.value.isFavorite,
                )
                recordViewedIfNew(article)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorKind = if (it.article == null) throwable.toUiError().kind else null,
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        val snap = snapshot ?: return
        viewModelScope.launch {
            savedContentRepository.toggleFavorite(snap)
        }
    }

    private fun recordViewedIfNew(article: com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto) {
        val newSnapshot = SavedContentSnapshot(
            contentType = SavedContentType.Article,
            id = article.id,
            title = article.title,
            summary = article.summary,
            coverImageJson = article.coverImage?.let { HeritageJson.encodeToString(it) },
            category = article.category.wireName,
            sourceUrl = article.sourceUrl,
            target = SavedContentTarget(
                id = article.id,
                sourceId = sourceId,
                sourceUrl = sourceUrl,
                category = category.wireName,
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
            @Assisted("articleId") articleId: String?,
            @Assisted("sourceId") sourceId: String?,
            @Assisted("sourceUrl") sourceUrl: String?,
            @Assisted category: ArticleCategory,
        ): ArticleDetailViewModel
    }
}
