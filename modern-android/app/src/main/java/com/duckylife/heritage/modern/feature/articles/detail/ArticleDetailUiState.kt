package com.duckylife.heritage.modern.feature.articles.detail

import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.feature.detail.intelligence.DetailIntelligenceEnhancementState
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: ArticleDetailDto? = null,
    val errorKind: ErrorKind? = null,
    val isFavorite: Boolean = false,
    val isContentStale: Boolean = false,
    override val contextLoading: Boolean = false,
    override val context: DetailContextDto? = null,
    override val contextErrorKind: ErrorKind? = null,
    // Content Digest
    override val digest: ContentDigestDto? = null,
    override val digestLoading: Boolean = false,
    override val digestErrorKind: ErrorKind? = null,
    // Blended Recommendations
    override val blendedRecommendations: BlendedRecommendationResponseDto? = null,
    override val blendedLoading: Boolean = false,
) : DetailIntelligenceEnhancementState<ArticleDetailUiState> {
    override fun copyWithDetailIntelligenceEnhancements(
        context: DetailContextDto?,
        contextLoading: Boolean,
        contextErrorKind: ErrorKind?,
        digest: ContentDigestDto?,
        digestLoading: Boolean,
        digestErrorKind: ErrorKind?,
        blendedRecommendations: BlendedRecommendationResponseDto?,
        blendedLoading: Boolean,
    ): ArticleDetailUiState = copy(
        context = context,
        contextLoading = contextLoading,
        contextErrorKind = contextErrorKind,
        digest = digest,
        digestLoading = digestLoading,
        digestErrorKind = digestErrorKind,
        blendedRecommendations = blendedRecommendations,
        blendedLoading = blendedLoading,
    )
}
