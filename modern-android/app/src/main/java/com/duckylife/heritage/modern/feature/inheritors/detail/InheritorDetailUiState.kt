package com.duckylife.heritage.modern.feature.inheritors.detail

import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.feature.detail.intelligence.DetailIntelligenceEnhancementState
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class InheritorDetailUiState(
    val isLoading: Boolean = true,
    val item: InheritorDetailDto? = null,
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
) : DetailIntelligenceEnhancementState<InheritorDetailUiState> {
    override fun copyWithDetailIntelligenceEnhancements(
        context: DetailContextDto?,
        contextLoading: Boolean,
        contextErrorKind: ErrorKind?,
        digest: ContentDigestDto?,
        digestLoading: Boolean,
        digestErrorKind: ErrorKind?,
        blendedRecommendations: BlendedRecommendationResponseDto?,
        blendedLoading: Boolean,
    ): InheritorDetailUiState = copy(
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
