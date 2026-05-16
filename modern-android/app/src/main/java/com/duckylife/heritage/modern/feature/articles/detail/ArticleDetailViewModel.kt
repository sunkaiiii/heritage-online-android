package com.duckylife.heritage.modern.feature.articles.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ArticleDetailLookup
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ArticleDetailViewModel.Factory::class)
class ArticleDetailViewModel @AssistedInject constructor(
    @Assisted("articleId") private val articleId: String?,
    @Assisted("sourceId") private val sourceId: String?,
    @Assisted("sourceUrl") private val sourceUrl: String?,
    @Assisted private val category: ArticleCategory,
    private val repository: HeritageRepository,
) : ViewModel() {
    private val lookup = ArticleDetailLookup(
        articleId = articleId,
        sourceId = sourceId,
        sourceUrl = sourceUrl,
        category = category,
    )
    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        observeCachedArticle()
        refresh()
    }

    private fun observeCachedArticle() {
        viewModelScope.launch {
            repository.cachedArticleDetail(lookup).collect { article ->
                if (article != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            article = article,
                            errorMessage = null,
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.article == null,
                    errorMessage = null,
                )
            }
            runCatching {
                repository.refreshArticleDetail(lookup)
            }.onSuccess { article ->
                _uiState.value = ArticleDetailUiState(
                    isLoading = false,
                    article = article,
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = if (it.article == null) throwable.message.orEmpty() else null,
                    )
                }
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
