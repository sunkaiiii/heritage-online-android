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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    val articles: Flow<PagingData<ArticleSummaryDto>> =
        uiState
            .debounce(SEARCH_DEBOUNCE_MS)
            .map { state ->
                ArticleQuery(
                    category = state.selectedCategory,
                    page = 1,
                    pageSize = 20,
                    keywords = state.searchKeywords.trim().takeIf { it.isNotEmpty() },
                    year = state.yearFilter.toIntOrNull(),
                )
            }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                repository.pagedArticles(query)
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

    fun updateSearchKeywords(keywords: String) {
        _uiState.update { it.copy(searchKeywords = keywords) }
    }

    fun updateYearFilter(year: String) {
        _uiState.update { it.copy(yearFilter = year) }
    }

    fun applyFilters(searchKeywords: String, yearFilter: String) {
        _uiState.update { it.copy(searchKeywords = searchKeywords, yearFilter = yearFilter) }
    }

    fun clearFilters() {
        _uiState.update { it.copy(searchKeywords = "", yearFilter = "") }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}
