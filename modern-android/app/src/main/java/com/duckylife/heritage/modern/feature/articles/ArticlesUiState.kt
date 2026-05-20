package com.duckylife.heritage.modern.feature.articles

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto

data class ArticlesUiState(
    val selectedCategory: ArticleCategory = ArticleCategory.News,
    val searchKeywords: String = "",
    val yearFilter: String = "",
    val isLoadingBanners: Boolean = true,
    val banners: List<HomeBannerDto> = emptyList(),
    val bannersFromCache: Boolean = false,
    val bannerErrorMessage: String? = null,
)
