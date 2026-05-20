package com.duckylife.heritage.modern.feature.articles

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class ArticlesUiState(
    val selectedCategory: ArticleCategory = ArticleCategory.News,
    val searchKeywords: String = "",
    val yearFilter: String = "",
    val isLoadingBanners: Boolean = true,
    val banners: List<HomeBannerDto> = emptyList(),
    val bannersFromCache: Boolean = false,
    val bannerErrorKind: ErrorKind? = null,
)
