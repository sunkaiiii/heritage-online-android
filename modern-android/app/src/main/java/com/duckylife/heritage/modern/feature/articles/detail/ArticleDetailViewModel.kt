package com.duckylife.heritage.modern.feature.articles.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ArticleDetailLookup
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRef
import com.duckylife.heritage.modern.core.network.BlendedRecommendationQuery
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceUiState
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceViewModelDelegateFactory
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentSnapshot
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import com.duckylife.heritage.modern.core.saved.SavedContentType
import com.duckylife.heritage.modern.core.network.dto.extractCoverImageUrl
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
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

@HiltViewModel(assistedFactory = ArticleDetailViewModel.Factory::class)
class ArticleDetailViewModel @AssistedInject constructor(
    @Assisted("articleId") private val articleId: String?,
    @Assisted("sourceId") private val sourceId: String?,
    @Assisted("sourceUrl") private val sourceUrl: String?,
    @Assisted private val category: ArticleCategory,
    private val repository: HeritageRepository,
    private val savedContentRepository: SavedContentRepository,
    private val syncRepository: LocalUserSyncRepository,
    intelligenceDelegateFactory: ContentIntelligenceViewModelDelegateFactory,
) : ViewModel() {

    private val intelligenceDelegate = intelligenceDelegateFactory.create(viewModelScope)
    val intelligenceUiState: kotlinx.coroutines.flow.StateFlow<ContentIntelligenceUiState> =
        intelligenceDelegate.uiState
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
            runCatchingCancellable {
                repository.refreshArticleDetail(lookup)
            }.onSuccess { article ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        article = article,
                        errorKind = null,
                    )
                }
                recordViewedIfNew(article)
                loadContext(article.id)
                loadDigest(article.id)
                loadBlended(article.id)
                loadIntelligence(article.id)
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

    fun loadContext() {
        val id = _uiState.value.article?.id ?: return
        loadContext(id)
    }

    private fun loadContext(articleId: String?) {
        if (articleId.isNullOrBlank()) return
        _uiState.update { it.copy(contextLoading = true, contextErrorKind = null) }
        viewModelScope.launch {
            runCatchingCancellable { repository.articleContext(articleId) }
                .onSuccess { ctx ->
                    _uiState.update { it.copy(contextLoading = false, context = ctx) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(contextLoading = false, contextErrorKind = e.toUiError().kind) }
                }
        }
    }

    private fun loadDigest(articleId: String?) {
        if (articleId.isNullOrBlank()) return
        _uiState.update { it.copy(digestLoading = true, digestErrorKind = null) }
        viewModelScope.launch {
            runCatchingCancellable { repository.articleDigest(articleId) }
                .onSuccess { digest ->
                    _uiState.update { it.copy(digestLoading = false, digest = digest) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(digestLoading = false, digestErrorKind = e.toUiError().kind) }
                }
        }
    }

    fun retryDigest() {
        val id = _uiState.value.article?.id ?: return
        loadDigest(id)
    }

    private fun loadIntelligence(articleId: String?) {
        if (articleId.isNullOrBlank()) return
        intelligenceDelegate.load(ContentIntelligenceRef(SearchResultType.Article, articleId))
    }

    private fun loadBlended(articleId: String?) {
        if (articleId.isNullOrBlank()) return
        _uiState.update { it.copy(blendedLoading = true) }
        viewModelScope.launch {
            runCatchingCancellable {
                repository.blendedRecommendations(
                    BlendedRecommendationQuery(
                        type = SearchResultType.Article,
                        id = articleId,
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
            val id = snap.id ?: return@launch
            syncRepository.toggleFavorite(
                type = "article",
                id = id,
                titleSnapshot = snap.title,
                coverImageUrlSnapshot = extractCoverImageUrl(snap.coverImageJson),
            )
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
                val historyId = article.id ?: return@launch
                syncRepository.recordHistory(
                    type = "article",
                    id = historyId,
                    titleSnapshot = article.title,
                    lastPosition = null,
                )
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

    /**
     * 公开给 UI 的重试入口：当 V3 增强层失败时调用。
     */
    fun retryIntelligence() = intelligenceDelegate.retry()
}
