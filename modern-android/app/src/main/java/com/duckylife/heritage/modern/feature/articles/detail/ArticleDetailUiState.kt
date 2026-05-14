package com.duckylife.heritage.modern.feature.articles.detail

import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: ArticleDetailDto? = null,
    val errorMessage: String? = null,
)
