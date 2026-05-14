package com.duckylife.heritage.modern.feature.articles

import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto

data class ArticlesUiState(
    val isLoading: Boolean = true,
    val banners: List<HomeBannerDto> = emptyList(),
    val articles: List<ArticleSummaryDto> = emptyList(),
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && banners.isEmpty() && articles.isEmpty()
}
