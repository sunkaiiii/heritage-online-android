package com.duckylife.heritage.modern.feature.articles

import androidx.lifecycle.SavedStateHandle
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

private const val KEY_ARTICLE_CATEGORY = "article_category"
private const val KEY_ARTICLE_SEARCH = "article_search"
private const val KEY_ARTICLE_YEAR = "article_year"

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val repository: HeritageRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ArticlesUiState(
            selectedCategory = savedStateHandle.get<String>(KEY_ARTICLE_CATEGORY)
                ?.let { ArticleCategory.entries.firstOrNull { c -> c.wireName == it } }
                ?: ArticleCategory.News,
            searchKeywords = savedStateHandle.get<String>(KEY_ARTICLE_SEARCH) ?: "",
            yearFilter = savedStateHandle.get<String>(KEY_ARTICLE_YEAR) ?: "",
        )
    )
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
        observeCachedBanners()
        refreshBanners()
    }

    private fun observeCachedBanners() {
        viewModelScope.launch {
            repository.cachedHomeBanners().collect { cached ->
                if (cached.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoadingBanners = false,
                            banners = cached,
                            bannersFromCache = true,
                            bannerErrorMessage = null,
                        )
                    }
                }
            }
        }
    }

    fun refreshBanners() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBanners = it.banners.isEmpty(), bannerErrorMessage = null) }
            runCatching {
                repository.homeBanners()
            }.onSuccess { banners ->
                _uiState.update {
                    it.copy(
                        isLoadingBanners = false,
                        banners = banners.sortedBy { it.sortOrder },
                        bannersFromCache = false,
                        bannerErrorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingBanners = false,
                        // 已有缓存时不显示错误，只保留缓存 banner
                        bannerErrorMessage = if (it.banners.isEmpty()) {
                            throwable.message?.takeIf { m -> m.isNotBlank() }
                                ?: "Banner load failed"
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }

    fun selectCategory(category: ArticleCategory) {
        savedStateHandle[KEY_ARTICLE_CATEGORY] = category.wireName
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchKeywords(keywords: String) {
        savedStateHandle[KEY_ARTICLE_SEARCH] = keywords
        _uiState.update { it.copy(searchKeywords = keywords) }
    }

    fun updateYearFilter(year: String) {
        savedStateHandle[KEY_ARTICLE_YEAR] = year
        _uiState.update { it.copy(yearFilter = year) }
    }

    fun applyFilters(searchKeywords: String, yearFilter: String) {
        savedStateHandle[KEY_ARTICLE_SEARCH] = searchKeywords
        savedStateHandle[KEY_ARTICLE_YEAR] = yearFilter
        _uiState.update { it.copy(searchKeywords = searchKeywords, yearFilter = yearFilter) }
    }

    fun clearFilters() {
        savedStateHandle[KEY_ARTICLE_SEARCH] = ""
        savedStateHandle[KEY_ARTICLE_YEAR] = ""
        _uiState.update { it.copy(searchKeywords = "", yearFilter = "") }
    }

    fun clearAdvancedFilters() {
        savedStateHandle[KEY_ARTICLE_YEAR] = ""
        _uiState.update { it.copy(yearFilter = "") }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}
