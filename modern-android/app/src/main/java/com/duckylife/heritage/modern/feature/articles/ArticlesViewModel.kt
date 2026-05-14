package com.duckylife.heritage.modern.feature.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.DefaultHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.ArticleQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticlesViewModel(
    private val repository: HeritageRepository = DefaultHeritageRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                coroutineScope {
                    val banners = async { repository.homeBanners() }
                    val articles = async { repository.articles(ArticleQuery(page = 1, pageSize = 20)) }
                    banners.await() to articles.await()
                }
            }.onSuccess { (banners, articles) ->
                _uiState.value = ArticlesUiState(
                    isLoading = false,
                    banners = banners.sortedBy { it.sortOrder },
                    articles = articles.items,
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "内容加载失败",
                    )
                }
            }
        }
    }
}
