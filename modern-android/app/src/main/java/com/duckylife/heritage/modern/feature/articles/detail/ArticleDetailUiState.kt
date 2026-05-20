package com.duckylife.heritage.modern.feature.articles.detail

import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: ArticleDetailDto? = null,
    val errorKind: ErrorKind? = null,
    val isFavorite: Boolean = false,
    val isContentStale: Boolean = false,
)
