package com.duckylife.heritage.modern.feature.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    val articles: Flow<PagingData<ArticleSummaryDto>> =
        uiState
            .map { it.selectedCategory }
            .distinctUntilChanged()
            .flatMapLatest { category ->
                repository.pagedArticles(
                    ArticleQuery(
                        category = category,
                        page = 1,
                        pageSize = 20,
                    ),
                )
            }
            .cachedIn(viewModelScope)

    init {
        refreshBanners()
    }

    fun refreshBanners() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBanners = true, bannerErrorMessage = null) }
            runCatching {
                repository.homeBanners()
            }.onSuccess { banners ->
                _uiState.update {
                    it.copy(
                        isLoadingBanners = false,
                        banners = banners.sortedBy { banner -> banner.sortOrder },
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingBanners = false,
                        bannerErrorMessage = throwable.message.orEmpty(),
                    )
                }
            }
        }
    }

    fun selectCategory(category: ArticleCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
