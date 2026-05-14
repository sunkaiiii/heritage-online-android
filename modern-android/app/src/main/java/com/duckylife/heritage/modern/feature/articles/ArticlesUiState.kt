package com.duckylife.heritage.modern.feature.articles

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto

data class ArticlesUiState(
    val selectedCategory: ArticleCategory = ArticleCategory.News,
    val isLoadingBanners: Boolean = true,
    val banners: List<HomeBannerDto> = emptyList(),
    val bannerErrorMessage: String? = null,
)
